import java.lang.Math;

public class SigmoidNeuron extends Neuron
{
	private static final long serialVersionUID = 6203772663984140533L;
	
	private float p;
	
	public SigmoidNeuron(float p, int inputNum)
	{
		super(inputNum);
		this.setP(p);
	}
	
	public SigmoidNeuron(int inputNum)
	{
		this(1.0F, inputNum);
	}
	
	public void setP(float p)
	{
		this.p = p;
	}
	
	@Override
	public float activate(float summation)
	{
		return 1.0F / (1F + (float)Math.exp(summation / p));
	}
}