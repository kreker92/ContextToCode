package tmt.dsl.tensorflow;

import java.util.ArrayList;
import java.util.HashMap;

import tmt.dsl.executor.info.Step;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//import org.tensorflow.SavedModelBundle;
//import org.tensorflow.Session;
//import org.tensorflow.Tensor;

public class TF {

 /* private SavedModelBundle model;

  public TF(ArrayList<HashMap<Integer, Step>> output, String model_path) {
    model = SavedModelBundle.load(model_path, "serve");
  }

  public int eval() {
    int size = items.get(0).feature_values.length;
    ArrayList<Float> scores = new ArrayList<>();
    // create the session from the Bundle
    Session s = model.session();
    
    float[][] matrix = new float[items.size()][size];
    for (int j=0;j<items.size();j++) {
      for(int i=0;i<size;i++)
        matrix[j][i]=(float)items.get(j).feature_values[i];
      
      items.get(j).feature_values = null;
    }
    
    Tensor inputTensor = Tensor.create(matrix);

    Tensor result = s.runner()
        .feed("input_layer", inputTensor)
        .fetch("cutoff_layer")
        .run().get(0);

    int[] m = new int[items.size()];
    int[] vector = result.copyTo(m);

    for(int i=0;i<vector.length;i++)  
      items.get(i).cutoff = (int)vector[i];
    
    result = s.runner()
        .feed("input_layer", inputTensor)
        .fetch("output_layer")
        .run().get(0);

    float[][] m1 = new float[items.size()][1];
    float[][] vector1 = result.copyTo(m1);
    
    for(int i=0;i<vector.length;i++)  
      items.get(i).score = vector1[i][0];

    return 0;
  }
  
//private static void generateDataSet() {
//with open(DSL_DATA_PATH, 'r') as handle:
//parsed = json.load(handle)
//
//train_data = []
//test_data = []
//count = 0
//
//for row_r in parsed:
//row = collections.OrderedDict(sorted(row_r.items()))
//trace = []
//count += 1
//for key, values in row.items():
//    step = {}
//
//    for k, v in values.items():
//        if k == 'supervised_env':
//            environment = {}
//            for e_k, e_v in v.items():
//                environment[e_k] = int(e_v.get('value'))
//            environment['terminate'] = "false"
//            step['environment'] = environment
//        elif k == 'argument':
//            args = {}
//            #for e_k, e_v in v.items():
//            #   if e_k == 'id':
//            args['id'] = '1'
//            step['args'] = args
//        elif k == 'program':
//            program = {}
//            for e_k, e_v in v.items():
//                if e_k == 'program':
//                    program['program'] = e_v.get('value')
//                if e_k == 'id':
//                    program['id'] = e_v.get('value')
//
//            step['program'] = program
//    trace.append(step)
//print(trace)
//}
*/
}
