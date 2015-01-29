
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NeuralNet {
	
	public static String negativeClass;
	public static String positiveClass;
	static ArffFile arffDataSet;
	static int numFolds;
	static double learningRate;
	static int numEpochs;

	public static void main(String[] args) {

		if (args.length != 4) {
			System.out.println("usage: java -jar neuralnet.jar <data-set-file> n l e");
			System.exit(-1);
		}

		try {
			// Load Arff file
			arffDataSet = ArffFile.load(args[0]);
			numFolds = Integer.parseInt(args[1]);
			learningRate = Double.parseDouble(args[2]);
			numEpochs = Integer.parseInt(args[3]);

			if (numFolds < 2) {
				System.out.println("Error: Number of folds must be atleast 2");
				System.exit(-1);
			}
			
			if (numEpochs < 1) {
				System.out.println("Error: Number of epochs must be atleast 1");
				System.exit(-1);
			}
			
			if (learningRate == 0) {
				System.out.println("Error: Learning rate cannot be 0");
				System.exit(-1);
			}
			
			negativeClass = arffDataSet.getAttributeData("Class")[0];
			positiveClass = arffDataSet.getAttributeData("Class")[1];
			
     		// Create training data in DataSet format
			DataSet instDataSet = createDataSet(arffDataSet.getData());

			List<Integer> positiveInst = stratifyClass(instDataSet,1);
			List<Integer> negativeInst = stratifyClass(instDataSet,0);
			
			Collections.shuffle(positiveInst);
			Collections.shuffle(negativeInst);
			
			
			int assignedFoldNumber;
			for (int i=0; i< positiveInst.size();i++) {
				assignedFoldNumber = i % numFolds;
				instDataSet.instances.get(positiveInst.get(i)).setFoldNumber(assignedFoldNumber);
			}
			
			for (int i=0; i< negativeInst.size();i++) {
				assignedFoldNumber = i % numFolds;
				instDataSet.instances.get(negativeInst.get(i)).setFoldNumber(assignedFoldNumber);
			}

			Double[] weights = new Double[arffDataSet.getNumberOfAttributes()];
			
			double outputK;
			double deltaK;
			double x;
			
			// Train Neural Net
			
			for (int fold = 0; fold < numFolds; fold++) {
				
				Arrays.fill(weights, 0.1);				
				for (int epoch = 0; epoch < numEpochs; epoch++) {
					for(int i=0; i < instDataSet.instances.size(); i++) {
						if (instDataSet.instances.get(i).foldNumber != fold) { // leave one out
							x = weights[0];
							for(int j=1; j<weights.length; j++) {
								x+= weights[j] * instDataSet.instances.get(i).attributes.get(j-1);
							}
							outputK = sigmoid(x);
							deltaK = (outputK - instDataSet.instances.get(i).label) * outputK * (1-outputK);
							weights[0]-= learningRate * deltaK; 
							for(int j=1; j<weights.length; j++) {
								weights[j]-= learningRate * deltaK * instDataSet.instances.get(i).attributes.get(j-1);
							}	
						}
					}
				}
				
				Double sigmoidOutput;
				for(int i=0; i < instDataSet.instances.size(); i++) {
					// Prediction for Test Set - held out set 
					if (instDataSet.instances.get(i).foldNumber == fold) {
						x = weights[0];
						for(int j=1; j<weights.length; j++) {
							x+= weights[j] * instDataSet.instances.get(i).attributes.get(j-1);
						}
						
						sigmoidOutput = sigmoid(x);
						instDataSet.instances.get(i).setSigmoidOutput(sigmoidOutput);
						if (sigmoidOutput > 0.5) {
							instDataSet.instances.get(i).setPredictedClass(1);
							instDataSet.instances.get(i).setPredictedString(positiveClass);
						}
						else {
							instDataSet.instances.get(i).setPredictedClass(0);
							instDataSet.instances.get(i).setPredictedString(negativeClass);
						}
						
					}
				}
				
			}
			

			System.out.println("Fold    Predicted     Actual    Confidence");
			System.out.println("=====   ==========    =======   ===========");
			for(int i=0; i < instDataSet.instances.size(); i++) {
				System.out.println(String.format("%5s %10s %10s %10s",instDataSet.instances.get(i).foldNumber + 1
						                                              ,instDataSet.instances.get(i).predictedString
						                                              ,instDataSet.instances.get(i).labelString
						                                              ,String.format("%.3f",instDataSet.instances.get(i).sigmoidOutput)));
				

			}
			
//			// PLOT ROC
//			Map<Double,Integer> confidenceLabel = new HashMap<Double,Integer>();
//			for(int i=0; i<instDataSet.instances.size(); i++) {
//				confidenceLabel.put(instDataSet.instances.get(i).sigmoidOutput, instDataSet.instances.get(i).instanceID);
//			}
//			
//			// Sort descending and find ROC plot 
//			
//			System.out.println("ROC coordinates");
//			int TP = 0;
//			int FP = 0;
//			int last_TP = 0;
//			Double cIMinusOne = -99.0;
//			Double FPR;
//			Double TPR;
//			Map<Double,Integer> sortedconfidenceLabel = new TreeMap<Double,Integer>(confidenceLabel).descendingMap();
//			for (Map.Entry<Double, Integer> entry: sortedconfidenceLabel.entrySet()) {
//				if (entry.getKey() != cIMinusOne && instDataSet.instances.get(entry.getValue()).label == 0 && TP > last_TP) {
//					FPR = FP * 1.0/instDataSet.numNegative;
//					TPR = TP * 1.0/instDataSet.numPositive;
//					//System.out.println("FPR="+FPR+" TPR="+TPR);
//					System.out.println(FPR+"       ,"+TPR);
//					last_TP = TP;
//				}
//				cIMinusOne = entry.getKey();
//				if (instDataSet.instances.get(entry.getValue()).label == 1) 
//					++TP;
//				else 
//					++FP;
//				//System.out.println("sigmoid "+entry.getKey()+" ID "+entry.getValue());
//			}
//			FPR = FP * 1.0/instDataSet.numNegative;
//			TPR = TP * 1.0/instDataSet.numPositive;
//			System.out.println("FPR="+FPR+" TPR="+TPR);
//			//
			

			
		} catch (ArffFileParseError e) {
			System.out.println("Couldn't parse ARFF file.");
		} catch (IOException e) {
			System.out.println("File IO Exception.");
		}		


	}
	

	
	private static double sigmoid(double x)
	{
	    return 1.0 / (1 + Math.exp(-x));
	}
	
	private static List<Integer> stratifyClass(DataSet instDataSet, int classLabel) {
		List<Integer> returnInstance = new ArrayList<Integer>();
		for (int i=0; i < instDataSet.instances.size(); i++) {
			if (instDataSet.instances.get(i).label == classLabel) {
				returnInstance.add(i);
			}
		}
		
		return returnInstance;
	}

	/**
	 * Converts from data format of ArffFile to DataSet format.
	 * 
	 */
	private static DataSet createDataSet(List<Object[]> data) {
		
		DataSet set = new DataSet();
		
		for (int i = 0; i < data.size(); i++) {
			Object[] datarow = data.get(i);
			set.addInstance(datarow,i);
		}
		
		return set;
	}
	




}
