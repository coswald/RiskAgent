import java.io.Serializable;
import java.lang.Comparable;
import java.lang.Math;
import java.lang.Object;

//http://natureofcode.com/book/chapter-10-neural-networks/
//*TO-DO Implement momentum: http://stats.stackexchange.com/questions/70101/neural-networks-weight-change-momentum-and-weight-decay

public abstract class Neuron extends Object implements Serializable, Comparable<Neuron>
{
	private static final long serialVersionUID = 37641900214726419L;
	
	protected float[] inputs;
	protected float[] weights;
	protected float learningRate;
	
	public Neuron(float learningRate, float... weights)
	{
		this.weights = weights;
		this.inputs = new float[weights.length - 1];
		this.clearInputs();
		this.learningRate = learningRate;
	}
	
	public Neuron(float... weights)
	{
		this(0.1F, weights);
	}
	
	public Neuron(float learningRate, int inputNum)
	{
		this.learningRate = learningRate;
		this.weights = new float[inputNum + 1];
		this.inputs = new float[inputNum];
		this.clearInputs();
		Neuron.fillList(true, this.weights);
	}
	
	public Neuron(int inputNum)
	{
		this(0.1F, inputNum);
	}
	
	private static void fillList(boolean random, float[] list)
	{
		for(int i = 0; i < list.length; i++)
		{
			if(random)
				list[i] = (float)Math.random();
			else
				list[i] = 1;
		}
	}
	
	public void clearInputs()
	{
		for(int i = 0; i < this.inputs.length; i++)
			this.inputs[i] = -1;
	}
	
	public boolean canFire()
	{
		for(float x : this.inputs)
			if(x < 0)
				return false;
		return true;
	}
	
	public void addToInputs(int index, float output) throws ExceededNeuronInputException, InvalidNeuronInputException
	{
		if(this.canFire())
			throw new ExceededNeuronInputException("The amount of given inputs is already at its maximum!");
		
		if(this.inputs[index] >= 0)
			throw new InvalidNeuronInputException("The input " + index + " on neuron " + this.toString() + "cannot be overriden!")
		
		this.inputs[index] = output;
	}
	
	public int compareTo(Neuron other)
	{
		float[] sample = new float[this.weights.length];
		float[] sampleTwo = new float[other.weights.length];
		Neuron.fillList(false, sample);
		Neuron.fillList(false, sampleTwo);
		
		return (int)Math.round(this.fire(sample) - other.fire(sampleTwo));
	}
	
	protected float sum(float... inputs) throws InvalidNeuronInputException
	{
	    if(inputs.length + 1 != weights.length)
			throw new InvalidNeuronInputException("The amount of inputs is not the same as weights.");
			
		float sum = 0F;
		for(int i = 0; i < inputs.length; i++)
			sum += inputs[i] * weights[i];
		sum += 1F * weights[weights.length - 1]; //bias
		return sum;
	}
	
	public abstract float activate(float summation);
	
	public float fire(float... inputs)
	{
		return this.activate(this.sum(inputs));
	}
	
	public float fire()
	{
		return this.fire(this.inputs);
	}
	
	public float[] getWeights()
	{
		return this.weights;
	}
	
	public void adjustWeights(float... adjustments) throws InvalidNeuronInputException
	{
		if(adjustments.length != weights.length)
			throw new InvalidNeuronInputException("The amount of inputs is not the same as weights.");
		
		for(int i = 0; i < adjustments.length; i++)
			this.weights[i] += (adjustments[i] * this.learningRate);
	}
	
	public void setLearningRate(float learningRate)
	{
		this.learningRate = learningRate;
	}
	
	public void train(float desired, float... inputs) throws InvalidNeuronInputException
	{
		float error = desired - this.fire(inputs);
		
		float[] adjustments = new float[inputs.length + 1];
		for(int i = 0; i < inputs.length; i++)
			adjustments[i] = inputs[i] * error;
		adjustments[adjustments.length - 1] = 1F * error;
		
		this.adjustWeights(adjustments);
	}
	
	public void train(float desired)
	{
		this.train(desired, this.inputs);
	}
	
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + " with a learning rate of " + this.learningRate + " and " + this.weights.length + " weights";
	}
}