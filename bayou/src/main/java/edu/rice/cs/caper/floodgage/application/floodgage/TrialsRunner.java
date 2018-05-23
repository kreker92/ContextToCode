/*
Copyright 2017 Rice University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package edu.rice.cs.caper.floodgage.application.floodgage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.rice.cs.caper.bayou.core.dsl.*;
import edu.rice.cs.caper.bayou.core.sketch_metric.EqualityASTMetric;
import edu.rice.cs.caper.bayou.core.synthesizer.RuntimeTypeAdapterFactory;
import edu.rice.cs.caper.floodgage.application.floodgage.model.plan.Trial;
import edu.rice.cs.caper.floodgage.application.floodgage.synthesizer.SynthesizeException;
import edu.rice.cs.caper.floodgage.application.floodgage.synthesizer.Synthesizer;
import edu.rice.cs.caper.floodgage.application.floodgage.view.View;
import edu.rice.cs.caper.bayou.core.dom_driver.Visitor;
import edu.rice.cs.caper.bayou.core.dom_driver.Options;
import net.openhft.compiler.CompilerUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

class TrialsRunner
{
    /**
     * A pair of a class's name and source code.
     */
    private static class SourceClass
    {
        final String className;

        final String classSource;

        SourceClass(String className, String classSource)
        {
            if(className == null)
                throw new NullPointerException("className");

            if(classSource == null)
                throw new NullPointerException("classSource");

            this.className = className;
            this.classSource = classSource;
        }
    }

    /**
     * Execute the given trials synthesizing result programs from the given Synthesizer and report progress
     * to the given view.
     *
     * @param trials the trials to run
     * @param synthesizer the synthesizer to use for mapping draft programs onto results
     * @param view the view used to track execution progress
     */
    static void runTrails(List<Trial> trials, Synthesizer synthesizer, View view)
    {
        if(trials == null)
            throw new NullPointerException("trials");

        if(synthesizer == null)
            throw new NullPointerException("synthesizer");

        if(view == null)
            throw new NullPointerException("view");

        int currentTrialId = 0; // unique number for the current trial.
        int possibleCompilePointsAccum = 0;
        int obtainedCompilePointsAccum = 0;
        int possibleTestCasePointsAccum = 0;
        int obtainedTestCasePointsAccum = 0;
        int possibleSketchMatchPointsAccum = 0;
        int obtainedSketchMatchPointsAccum = 0;

        boolean anySynthesizeFailed = false; // track any thrown SynthesizeException from synthesizer.synthesize(...)

        for(Trial trial : trials)
        {
            currentTrialId++;

            String draftProgram = trial.getDraftProgramSource();
            view.declareStartOfTrial(trial.getDescription(), draftProgram);

            /*
             * Provide the draft program to the synthesizer and collect the synthesis results.
             */
            List<String> synthResults;
            try
            {
                synthResults = synthesizer.synthesize(draftProgram);
            }
            catch (SynthesizeException e)
            {
                view.declareSynthesisFailed();
                view.declareTrialResultSynthesisFailed();
                anySynthesizeFailed = true;
               continue;
            }
            view.declareNumberOfResults(synthResults.size());

            /*
             * Rewrite each result class source to have a unique class name.
             *
             * We will compile and class-load each result so each class needs to have a unique class name.
             */
            List<SourceClass> synthResultsWithUniqueClassNames = new LinkedList<>();
            {
                // floodgage assumes that the name of the synthesized class remains unchanged from the input class name.
                String assumedResultClassName = trial.getDraftProgramClassName();

                int resultId = 0;
                for(String result : synthResults)
                {
                    resultId++;
                    String uniqueResultClassName = assumedResultClassName + "_" + currentTrialId + "_" + resultId;
                    String classSource = result.replaceFirst("public class " + assumedResultClassName,
                                                             "public class " + uniqueResultClassName);
                    synthResultsWithUniqueClassNames.add(new SourceClass(uniqueResultClassName, classSource));
                }
            }

            /*
             * If an expected sketch source was added to this trial, construct the sketch from the source.
             * Also increment possiblePointsAccum if there is an expected sketch.
             *
             * If no expected sketch is defined, set expectedSketch to null.
             */
            Boolean anySketchMatch; // true: sketch expected and a match found in some result.
                                    // false: sketch expected but no match found so far among any result.
                                    // null: no expected sketch.
            DSubTree expectedSketch;
            {
                if(trial.containsSketchProgramSource())
                {
                    String expectedSketchSource = trial.tryGetSketchProgramSource(null);
                    expectedSketch = makeSketchFromSource(expectedSketchSource,
                                                          trial.getDraftProgramClassName() + ".java");
                    anySketchMatch = false; // we will test to sketch matches, so init to none seen yet

                }
                else
                {
                    expectedSketch = null;
                    anySketchMatch = null; // we wont do sketch testing to singal no expected sketch
                }
            }

            if(trial.containsSketchProgramSource())
                possibleSketchMatchPointsAccum++; // possible point for some result matching the sketch.

            /*
             * For each result:
             *
             *     1.) Check that the result compiles
             *     2.) If compiles, check that the result passes the test suite.
             *     3.) If compiles and the trial contains an expected sketch, check for sketch equivalency.
             */
            int resultId = 0;
            for(SourceClass result : synthResultsWithUniqueClassNames)
            {
                resultId++;
                possibleCompilePointsAccum++; // possible point for result compiling.


                view.declareSynthesizeResult(resultId, result.classSource);

                /*
                 * If a sketch expectation is declared for this trial, perform sketch comparison.
                 */
                boolean sketchMatch = false;
                if(anySketchMatch != null) // if there is an expected sketch
                {
                    DSubTree resultSketch = makeSketchFromSource(result.classSource, result.className + ".java");

                    if(expectedSketch == null && resultSketch == null) // both null
                    {
                        view.warnSketchesNull();
                        sketchMatch = true;
                    }
                    else if(expectedSketch != null && resultSketch != null) // both non-null
                    {
                        EqualityASTMetric m = new EqualityASTMetric();
                        float equalityResult = m.compute(expectedSketch, Collections.singletonList(resultSketch), "");
                        view.declareSketchMetricResult(equalityResult);

                        if(equalityResult == 1)
                            sketchMatch = true;

                    }
                    else // one null
                    {
                        view.warnSketchesNullMismatch();
                    }
                    anySketchMatch = anySketchMatch || sketchMatch;
                }


                /*
                 * Check that the result compiles.
                 */
                boolean resultCompiled;
                try
                {
                    System.out.println(""); // ensure any compiler errors start on new line
                    CompilerUtils.CACHED_COMPILER.loadFromJava(result.className, result.classSource);
                    resultCompiled = true;
                    obtainedCompilePointsAccum++; // for compiling
                }
                catch (ClassNotFoundException e)
                {
                    resultCompiled = false;
                }

                /*
                 * If result compiles, run test cases.
                 *
                 * (Construct a test suite for result and run the result against the suite.)
                 */
                boolean testCasesPass;
                {
                    if(resultCompiled)
                    {
                        view.declareStartOfTestCases();
                        Class resultSpecificTestSuite = makeResultSpecificTestSuite(result.className);
                        TestSuiteRunner.RunResult runResult =
                                TestSuiteRunner.runTestSuiteAgainst(resultSpecificTestSuite, view);
                        possibleTestCasePointsAccum+=runResult.TestCaseCount;
                        obtainedTestCasePointsAccum+=runResult.TestCaseCount-runResult.FailCount;
                        testCasesPass=runResult.FailCount==0;
                    }
                    else
                    {
                        testCasesPass = false; // even if no test cases we say uncompilable code fails the null t.c.

                        /*
                         * Count the number of test cases in TestSuite (since we didnt run JUnit) and add each
                         * to the possiblePointsAccum count.
                         */
                        Class testSuiteClass;
                        try
                        {
                            testSuiteClass = Class.forName("TestSuite");
                        }
                        catch (ClassNotFoundException e)
                        {
                            throw new RuntimeException(e); // TestSuite should be in the trialpack
                        }

                        for(Method m : testSuiteClass.getMethods())
                        {
                            if(m.getAnnotation(org.junit.Test.class) != null)
                                possibleTestCasePointsAccum++;
                        }
                    }
                }

                /*
                 * Report results for the result.
                 */

                Boolean reportSketchMatch = trial.containsSketchProgramSource() ? sketchMatch : null;
                view.declareSynthResultResult(resultCompiled, testCasesPass, reportSketchMatch);

            }

            /*
             * If any sketch matched among the results, award a point. 
             */
            if(anySketchMatch != null && anySketchMatch)
                obtainedSketchMatchPointsAccum++;


        }

        /*
         * Report total tallies.
         */
        view.declarePointScore(possibleCompilePointsAccum, obtainedCompilePointsAccum, possibleTestCasePointsAccum,
                               obtainedTestCasePointsAccum, possibleSketchMatchPointsAccum,
                               obtainedSketchMatchPointsAccum);

        if(anySynthesizeFailed)
            view.declareSynthesisFailed(); // this can really influence the score by skipping points, so remind.
    }

    /*
     * Build a sketch representation of the given source and unit name.
     *
     * Patterned after edu.rice.cs.caper.bayou.application.dom_driver.Driver
     */
    private static DSubTree makeSketchFromSource(String source, String unitName)
    {
        if(source == null)
            throw new IllegalArgumentException("source");

        CompilationUnit cu;
        {
            ASTParser parser = ASTParser.newParser(AST.JLS8);
            parser.setSource(source.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setUnitName(unitName); // removing this always make the sketch null
            parser.setEnvironment(new String[] { "" }, new String[] { "" }, new String[] { "UTF-8" }, true);
            parser.setResolveBindings(true);

            cu = (CompilationUnit) parser.createAST(null);
        }

        Options options = new Options();
        String sketch;
        try
        {
            Visitor visitor = new Visitor(cu, options);
            cu.accept(visitor);
            sketch = visitor.buildJson();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        if(sketch == null)
            return null;

        JSONObject sketchObject = new JSONObject(sketch);
        JSONObject program = sketchObject.getJSONArray("programs").getJSONObject(0);
        JSONObject astNode = program.getJSONObject("ast");

        RuntimeTypeAdapterFactory<DASTNode> nodeAdapter = RuntimeTypeAdapterFactory.of(DASTNode.class, "node")
                .registerSubtype(DAPICall.class)
                .registerSubtype(DBranch.class)
                .registerSubtype(DExcept.class)
                .registerSubtype(DLoop.class)
                .registerSubtype(DSubTree.class);
        Gson gson = new GsonBuilder().serializeNulls()
                .registerTypeAdapterFactory(nodeAdapter)
                .create();

        return gson.fromJson(astNode.toString(), DSubTree.class);

    }

    /*
     * Compiles and loads a class named [resultName]Test extends the class TestSuite.
     */
    private static Class makeResultSpecificTestSuite(String resultName)
    {
        String className =  resultName + "Test";
        String source = "public class " + className + " extends TestSuite"  + "\n" +
                "{\n" +
                "\t@Override\n" +
                "\tprotected " + resultName  + " makeTestable()" + "\n" +
                "\t{\n" +
                "\t\treturn new " + resultName + "();\n" +
                "\t}\n" +
                "}";

        try
        {
            return CompilerUtils.CACHED_COMPILER.loadFromJava(className, source);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
}
