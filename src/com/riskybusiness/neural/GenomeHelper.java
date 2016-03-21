package com.riskybusiness.genetic;

import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.StepNeuron;
import com.riskybusiness.neural.SigmoidNeuron;

import java.io.Serializable;

public class GenomeHelper implements Serializable
{

	private int myID;

	public GenomeHelper()
	{
		myID = 1;
	}

	//This function takes in an array of neuron genes and seperates them by layer
	public ArrayList<ArrayList<NeuronGene>> seperate(ArrayLsit<NeuronGene> genes, int numLayers)
	{
		//Represents the individual layers of the neural network
		ArrayList<NeuronGene> layer = new ArrayList<NeuronGene>();
		
		//Represents the fill neural network seperated by layers
		ArrayList<ArrayList<NeuronGene>> seperatedNeuronArray = new ArrayList<ArrayList<NeuronGene>>();

		//Seperate the neurons genes into their corresponding layers
		//Loop through the layers
		for (int i = 0; i < numLayers; i++)
		{
			//Clear the layer first
			layer.clear();

			//Loop through each neuron in the neuron gene set
			for (int j = 0; j < genes.size(); j++)
			{
				//If the neuron belongs to the current layer then add it to the layer array
				if (genes.get(j).getNeuronLayer() == (i + 1))
				{
					//Add the neuron to the layer array
					layer.add(genes.get(j));
					/**
					Should I do something about deactivated neurons??
					**/
				}
			}
			//Once all the neuron have been checked add the layer to the final genome
			seperatedNeuronArray.add(layer);
		}

		return seperatedNeuronArray;
	}

	public ArrayList<NeuronGene> sortNeuronArray(ArrayList<NeuronGene> genes, int numLayers)
	{

		//Represents the array to return to the calling function
		ArrayList<NeuronGene> sortedNeuronArray = new ArrayList<NeuronGene>();

		//Represents the fill neural network seperated by layers
		ArrayList<ArrayList<NeuronGene>> geneSet = new ArrayList<ArrayList<NeuronGene>>();

		//Seperate the given neuron gene array into an array of layers
		geneSet = this.seperate(genes, numLayers);

		//Now that the genome has been seperated by layer and sorted, 
		//This loop brings the array back together.
		for (int i = 0; i < numLayers; i++)
		{
			for (int j = 0; j < geneSet.get(i).size; j++)
			{
				sortedNeuronArray.add(geneSet.get(i).get(j));
			}
		}

		return sortedNeuronArray;
	}

	//Should this be void since everything we pass in is the actual copy of it? 

	//Assumes the neuron passed in has the correct layer
	//This fucntion takes in a recently added neuron and then determines if the neurons that
	//proceed that added neuron need to be pushed back to a new layer
	public ArrayList<NeuronGene> pushNeurons(ArrayList<NeuronGene> neuronGenes, ArrayList<LinkGene> linkGenes, NeuronGene addedNeuron, int numLayers) 
	{

		//Represents the new array of neurons with the neurons pushed back
		ArrayList<NeuronGene> pushedBackArray = new ArrayList<NeuronGene>();

		//Represents the fill neural network seperated by layers
		ArrayList<ArrayList<NeuronGene>> geneSet = new ArrayList<ArrayList<NeuronGene>>();

		//Recursivly find the next neuron to push back
		//Loop through each link on a neuron and push back that neurons neurons from that neurons links.
		for (int i= 0; i < linkGenes.size(); i++)
		{
			//If we have found a link that is enabled and not looped and comes from our neuron then check that links
			//To neuron and see if it needs to be pushed back. Then recursivly call push neurons on that
			//Neuron
			if (linkGenes.get(i).getFromNeuron() == addedNeuron.getID() && linkGenes.get(i).getEnabled() && linkGenes.get(i).getRecurrency())
			{
				//Find the neuron gene that the link points to
				for (int j = 0; j < neuronGenes.size(); j++)
                {
                     if (neuronGenes.get(i).getID() == linkGenes.get(i).getToNeuron())
                     {
                        NeuronGene toNeuron = neuronGenes.get(i);
                     }
                }

				//Check the neuron and see if it needs to be pushed back
				if(toNeuron.getNeuronLayer() == addedNeuron.getNeuronLayer())
				{
					toNeuron.pushLayer();
					pushNeurons(neuronGenes, linkGenes, toNeuron, numLayers);
				}
				//else is one of the base cases(the neuron doesn't need to be pushed back)
			}
		}
	}

	//This function requires the incoming neuron genes to be sorted in order to properly sort the links
	//The function also requires that the push back function was called if a neuron was recently added
	public ArrayList<LinkGene> sortLinkArray(ArrayList<NeuronGene> neuronGenes, ArrayList<LinkGene> linkGenes)
	{
		/*
		for (int i = 0; i < neuronGenes.size())
		{

		}
		*/
	}
}
