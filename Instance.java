
import java.util.List;
import java.util.ArrayList;

/**
 * @author Nishka
 * 
 * Holds details for one instance of data
 *
 */
public class Instance {
	
	public int label;
	public String labelString;
	public int foldNumber;
	public int instanceID;
	public List<Double> attributes = null;
	public int predictedClass;
	public String predictedString;
	public Double sigmoidOutput;

	/**
	 * Add attribute values in the order of
	 * attributes as specified by the dataset
	 */
	public void addAttribute(Double attr) {
		if (attributes == null) {
			attributes = new ArrayList<Double>();
		}
		attributes.add(attr);
	}
	
	/**
	 * Add label value to the instance
	 */
	public void setLabel(int _label) {
		label = _label;
	}
	
	public void setLabelString(String _labelString) {
		labelString = _labelString;
	}

	public void setFoldNumber(int _foldNum) {
		foldNumber = _foldNum;
	}
	
	public void setInstanceID(int _instanceID) {
		instanceID = _instanceID;
	}
	
	public void setPredictedClass(int _predictedClass) {
		predictedClass = _predictedClass;
	}
	
	public void setPredictedString(String _predictedString) {
		predictedString = _predictedString;
	}
	
	public void setSigmoidOutput(Double _sigmoidOutput) {
		sigmoidOutput = _sigmoidOutput;
	}

}
