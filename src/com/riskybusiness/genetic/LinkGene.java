public class LinkGene implements Serializable
{
	private static final long serialVersionUID = 1L;


	int			fromNeuron
	int			toNeuron
	double		dWeight
	boolean		bEnabled
	boolean		bRecurrent
	int			innovationID

	//Create a LinkGene
	public LinkGene (int fNeuron, int tNeuron, double weight, boolean recur) {
		fromNeuron	= fNeuron;
		toNeuron	= tNeuron;
		dWeight 	= weight;
		bEnabled	= TRUE;
		bRecurrent	= recur;
		//I have no clue what to do with the innovationID.
	}

	//Return whether the link is enabled
	public void getEnabled() {
		return bEnabled;
	}

	//Disables link
	public void disableLink() {
		this.bEnabled = FALSE;
	} 

	//Re-enables a link
	//Is this possible??
	public void enableLink() {
		this.bEnabled = TRUE;
	}

	//Disable the recurrency of the link
	public void disableRecurrency() {
		this.bRecurrent = FALSE;
	}
	
	//Enables the recurrency of the link
	public void disableRecurrency() {
		this.bRecurrent = TRUE;
	}

	//Set the weight of the link
	public void setWeight(double weight) {
		this.dWeight = weight;
	}


	//Get the weight of the link
	public double getWeight() {
		return dWeight;
	}

	public void setToNeuron(int neuron) {
		this.toNeuron = neuron;
	}


	public int getToNeuron() {
		return toNeuron;
	}


	public void setFromNeuron(int neuron) {
		this.fromNeuron = neuron;
	}

	public int getFromNeuron() {
		return fromNeuron;
	}

	public void createSynapse() {
		com.riskybusiness.neural.Synapse(//Not sure if this is correct but I think it is unique so should work for an id
										 this.innovationID, this.fromNeuron, this.toNeuron);
	}

}
