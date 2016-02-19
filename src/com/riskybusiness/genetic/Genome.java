import java.util.*;

public class Genome implements Serializable
{
	private static final long serialVersionUID = 1L;

	int 				m_GenomeID;
	Vector				m_vecNeurons;
	Vector				m_vecLinks;
	//Phenotype?
	double				m_dFitness;
	double				m_dAdjustedFitness;
	double				m_dAmountToSpawn;
	int 				m_iNumInputs;
	int 				m_iNumOutputs;
	int 				m_iSpecies;

	//Returns true if the specified link is already part of the genome
	public boolean duplicateLink(int neuronIn, int neuronOut){
		//Idk what to do yet;
		null;
	}

	//Given a neuron id this function just finds its position in m_vecNeurons
	public int getElementPos(int neuronId){
		//Nope
		null;
	}

	//Tests if the passed ID is the same as any existing neuron IDs. Used in AddNeuron
 	public boolean AlreadyHaveThisNeuronID(const int ID){
 		//Meh?
 		null;
 	}

 	//This constructor creates a genome from a vector of SLinkGenes a vector of SNeuronGenes and an ID number
 	public CGenome(int id, Vector neurons, Vector genes, int inputs, int outputs);

 	//Create a neural network from the genome
 	//public CNeuralNet createPhenotype(int depth){?}

 	//Delete the neural network
 	//public void deletePhenotype(){?}

 	//Add a link to the genome dependent upon the mutation rate
 	//public void addLink(double mutationRate, double chanceOfRecurrent, CInnovation &innovation?, int numeTrysToFindLoop, int numTrysToAddLink){?}

 	//Add a neuron to the genome dependent upon the mutation rate
 	//public void addNeuron(double mutationRate, CInnovation &innovation?, int numTrysToFindOldLink){?}

 	//Function to mutate the connection weights
 	//public void mutateWeights(double mutationRate, double probNewMutation, double dMaxPertubation){?}

 	//Perturbs the activation responses of the neurons
 	//public void mutateActivationResponse(double mutationRate, double MaxPertubation){?}

 	//Calculates the compatibility score between this genome and another genome
 	//public double getCompatibilityScore(const CGenome &genome?);
 	
 	//Not sure
 	//public void sortGenes();







}