import java.lang.Integer;
import java.lang.Object;

public class NeuralNet extends Object implements Serializable
{
	private static final long serialVersionUID = 7382374626520742474L;
	
	protected Synapse[] synapses;
	protected Neuron[] neurons;
	
	public NeuralNet(Neuron[] neurons, Synapse[] synapses)
	{
		this.neurons = neurons;
		this.synapses = synapses;
	}
	
	public NeuralNet(int inputLayerRows, int outputLayerRows, int... hiddenLayerRows)
	{
		int sum = inputLayerRows + outputLayerRows;
		int[] connections = new int[1 + hiddenLayerRows.length];
		connections[0] = inputLayerRows * hiddenLayerRows[0];
		int synapsesSum = connections[0];
		
		for(int i = 0; i < hiddenLayerRows.length - 1; i++)
		{
			sum += hiddenLayerRows[i];
			connections[i + 1] = hiddenLayerRows[i] * hiddenLayerRows[i + 1];
			synapsesSum += connections[i + 1];
		}
		
		sum += hiddenLayerRows[hiddenLayerRows.length - 1];
		connections[connections.length - 1] = hiddenLayerRows[hiddenLayerRows.length - 1] * outputLayerRows;
		synapsesSum += connections[connections.length - 1];
		
		this.neurons = new Neuron[sum];
		this.synapses = new Synapse[synapsesSum];
		sum = Integer.MAX_VALUE;
		
		for(int i = 0, j = -1; i < neurons.length; i++)
		{
			neurons[i] = new SigmoidNeuron((j == -1) ? 1 : connections[j]);
			if(j == -1 && i >= inputLayerRows - 1)
				sum = hiddenLayerRows[++j] + inputLayerRows - 1;
			else if(i >= sum)
				sum += hiddenLayerRows[++j];
		}
		
		for(int i = 0, j = 0; i < synapses.length; i++, j++)
		{
			synapses[i] = new Synapse(, neurons[j], neurons[k])
		}
	}
}