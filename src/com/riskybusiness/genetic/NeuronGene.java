public class NeuronGene implements Serializable
{
	private static final long serialVersionUID = 1L;

	int 			iId;
	Neuron_Type		neuronType;
	boolean 		bRecurrent;
	double			dActivationResponse;
	double			dSplitX;
	double			dSplitY;

	public NeuronGene(int id, Neuron_Type type, boolean recur, double activate, double splX, double splY) {

		//Is their a way to make this unique and not have to provide it? Like an autoincrement
		iId 				= id;
		neuronType 			= type; //input, hidden, bias, output, none
		//Do we want to add something to signify whether it is a step or sigmoid
		bRecurrent			= recur;
		dActivationResponse	= activate;
		dSplitX				= splX;
		dSplitY				= splY;
	}

	//Get the id of the neuron
	public int getID() {
		return iId;
	}

	//Get the neuron type
	public Neuron_Type getNeuronType() {
		return neuronType;
	}

	//Set the neuron type
	public void setNeuronType(Neuron_Type type) {
		this.neuronType = type;
	}

	//Return whether the link is recurrent
	public boolean isRecurrent() {
		return bRecurrent;
	}

	//Disable the recurrency of the neuron
	public void disableRecurrency() {
		this.bRecurrent = FALSE;
	}
	
	//Enables the recurrency of the neuron
	public void enableRecurrency() {
		this.bRecurrent = TRUE;
	}

	//Get the activation response of the neuron
	public double getActivationResponse() {
		return dActivationResponse;
	}

	//Set the activation response of the neuron
	public void setActivationResponse(double activate) {
		this.setActivationResponse = activate;
	}

	//Get the x-coord of the neuron
	public double getXCoord() {
		return dSplitX;
	}

	//Set the x-coord of the neuron
	public double setXCoord(double splX) {
		this.dSplitX = splX;
	}

	//Get the y-ccord of the neuron
	public double getYCoord() {
		return dSplitY;
	}

	//Set the y-ccord of the neuron
	public double setYCoord(double splY) {
		this.dSplitY = splY;
	}

	public void createNeuron() {
		//I'm not sure if this is correct
		com.riskybusiness.neural.SigmoidNeuron(/*Possible issues with double vs float*/
					  					  	   this.dActivationResponse,
					  		 				   /*We don't currently have anything to express num inputs in the gene*/
					  		 				   5);

	}












