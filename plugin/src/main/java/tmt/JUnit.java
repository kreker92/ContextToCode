package tmt;

// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.codeInsight.TestFrameworks;
import com.intellij.codeInsight.intention.impl.AddOnDemandStaticImportAction;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.actions.CleanupInspectionUtil;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.JavaVersionService;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringManager;
import com.intellij.refactoring.migration.MigrationManager;
import com.intellij.refactoring.migration.MigrationMap;
import com.intellij.refactoring.migration.MigrationProcessor;
import com.intellij.refactoring.util.RefactoringUIUtil;
import com.intellij.testIntegration.TestFramework;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.junit.JUnit5AssertionsConverterInspection;
import com.siyeh.ig.junit.JUnitCommonClassNames;
import com.siyeh.ig.psiutils.TestUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.intellij.codeInsight.AnnotationUtil.CHECK_HIERARCHY;

public class JUnit extends BaseInspection {
    private static final List<String> ruleAnnotations = Arrays.asList(JUnitCommonClassNames.ORG_JUNIT_RULE, JUnitCommonClassNames.ORG_JUNIT_CLASS_RULE);

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return InspectionGadgetsBundle.message("junit5.converter.display.name");
    }

    @NotNull
    @Override
    protected String buildErrorString(Object... infos) {
        return "#ref can be JUnit 5 test";
    }

    @Override
    public boolean shouldInspect(PsiFile file) {
        if (!JavaVersionService.getInstance().isAtLeast(file, JavaSdkVersion.JDK_1_8)) return false;
        if (JavaPsiFacade.getInstance(file.getProject()).findClass(JUnitCommonClassNames.ORG_JUNIT_JUPITER_API_ASSERTIONS, file.getResolveScope()) == null) {
            return false;
        }
        return super.shouldInspect(file);
    }

//    @Nullable
//    @Override
////    protected InspectionGadgetsFix buildFix(Object... infos) {
////        return new null;
////    }

    @Override
    public BaseInspectionVisitor buildVisitor() {
        System.err.println("$$$$");
        return new BaseInspectionVisitor() {

            @Override
            public void visitClass(PsiClass aClass) {
                TestFramework framework = TestFrameworks.detectFramework(aClass);
                if (framework == null || !"JUnit4".equals(framework.getName())) {
                    return;
                }

                if (!canBeConvertedToJUnit5(aClass)) return;

                registerClassError(aClass);
            }
        };
    }

    protected static boolean canBeConvertedToJUnit5(PsiClass aClass) {
        if (AnnotationUtil.isAnnotated(aClass, TestUtils.RUN_WITH, CHECK_HIERARCHY)) {
            return false;
        }

        for (PsiField field : aClass.getAllFields()) {
            if (AnnotationUtil.isAnnotated(field, ruleAnnotations, 0)) {
                return false;
            }
        }

        for (PsiMethod method : aClass.getMethods()) {
            if (AnnotationUtil.isAnnotated(method, ruleAnnotations, 0)) {
                return false;
            }

            PsiAnnotation testAnnotation = AnnotationUtil.findAnnotation(method, true, JUnitCommonClassNames.ORG_JUNIT_TEST);
            if (testAnnotation != null && testAnnotation.getParameterList().getAttributes().length > 0) {
                return false;
            }
        }
        return true;
    }
}

