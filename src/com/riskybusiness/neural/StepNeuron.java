public class StepNeuron extends Neuron
{
	private static final long serialVersionUID = 607098269654808303L;
	
	protected float threshold;
	
	public StepNeuron(float threshold, int inputNum)
	{
		super(inputNum);
		this.setThreshold(threshold);
	}
	
	public void setThreshold(float threshold)
	{
		this.threshold = threshold;
	}
	
	@Override
	public float activate(float summation)
	{
		return (summation < threshold) ? 0F : 1F;
	}
}