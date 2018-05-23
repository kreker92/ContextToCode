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
package edu.rice.cs.caper.bayou.application.ast_quality_perf_test;

import edu.rice.cs.caper.bayou.core.dsl.DSubTree;
import org.apache.commons.math3.stat.StatUtils;

import java.util.List;

public interface Metric {
    float compute(DSubTree originalAST, List<DSubTree> predictedASTs);

    static float min(List<? extends Number> values) {
        double[] dValues = values.stream().mapToDouble(v -> v.floatValue()).toArray();
        return (float) StatUtils.min(dValues);
    }

    static float mean(List<? extends Number> values) {
        double[] dValues = values.stream().mapToDouble(v -> v.floatValue()).toArray();
        return (float) StatUtils.mean(dValues);
    }

    static float standardDeviation(List<? extends Number> values) {
        double[] dValues = values.stream().mapToDouble(v -> v.floatValue()).toArray();
        return (float) Math.sqrt(StatUtils.variance(dValues));
    }
}
