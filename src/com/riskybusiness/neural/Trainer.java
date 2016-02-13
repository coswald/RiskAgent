import java.lang.Object;

public class Trainer extends Object
{
	private float[] inputs;
	private float desiredAnswer;
	
	public Trainer(float desiredAnswer, float... inputs)
	{
		this.inputs = new float[inputs.length];
		for(int i = 0; i < inputs.length; i++)
			this.inputs[i] = inputs[i];
		//this.inputs[inputs.length] = 1; //bias input //*Removed after bias support changed*//
		this.desiredAnswer = desiredAnswer;
	}
	
	public float[] getInputs()
	{
		return this.inputs;
	}
	
	public float getAnswer()
	{
		return this.desiredAnswer;
	}
}
