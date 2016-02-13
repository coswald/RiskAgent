import java.lang.Object;

public class Synapse extends Object implements Serializable
{
	private static final long serialVersionUID = 3646014859697821258L;
	
	private int neuronIndex;
	private Neuron sender;
	private Neuron receiver;
	
	public Synapse(int neuronIndex, Neuron sender, Neuron receiver)
	{
		this.neuronIndex = neuronIndex;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public void feedForward(float... inputs) throws ExceededNeuronInputException, InvalidNeuronInputException
	{
		receiver.addToInput(neuronIndex, sender.fire(inputs));
	}
	
	public void feedForward() throws ExceededNeuronInputException, InvalidNeuronInputException, NeuronCannotFireException
	{
		if(!sender.canFire())
			throw new NeuronCannotFireException(sender.toString() + " cannot fire!");
		receiver.addToInput(neuronIndex, sender.fire());
	}
	
	@Override
	public String toString()
	{
		return "A Synapse between a " + sender.toString() + " and a " + receiver.toString();
	}
}