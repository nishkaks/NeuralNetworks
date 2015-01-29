
import java.util.ArrayList;
import java.util.List;

/**
 * This class organizes the information of a data set into simple structures.
 * 
 */

public class DataSet {
	
	public List<Instance> instances = null; // ordered list of instances
	public int numNegative = 0;
	public int numPositive = 0;
	
	public DataSet() {
		// blank
	}
	
	public DataSet(List<Instance> instances) {
		this.instances = instances;
	}
	
	/**
	 * Add instance to collection.
	 */
	public void addInstance(Object[] datarow,int instanceID) {
		if (instances == null) {
			instances = new ArrayList<Instance>();
		}
		
		Instance instance = new Instance();
		
		for (int i =0; i < datarow.length - 1 ; i++) {
			instance.addAttribute(Double.parseDouble(datarow[i].toString()));
		}
		
		instance.setLabelString(datarow[datarow.length -1].toString());
		
		if (datarow[datarow.length -1].toString().equals(NeuralNet.positiveClass)) {
		    instance.setLabel(1);
		    numPositive++;
		}
		else {
			instance.setLabel(0);
			numNegative++;
		}
		
		instance.setInstanceID(instanceID);
		
		instances.add(instance);
		
	}

}
