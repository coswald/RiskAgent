package com.riskybusiness.genetic;

import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.StepNeuron;
import com.riskybusiness.neural.SigmoidNeuron;

import java.io.Serializable;
import java.util.ArrayList;

public class GenomeHelper implements Serializable
{
	//Dummy Variable
	private int myID;

	public GenomeHelper()
	{
		//Just to put something here
		myID = 1;
	}

	//This function takes in an array of neuron genes and seperates them by layer
	public ArrayList<ArrayList<NeuronGene>> seperate(ArrayList<NeuronGene> genes, int numLayers)
	{
		//Represents the fill neural network seperated by layers
		ArrayList<ArrayList<NeuronGene>> seperatedNeuronArray = new ArrayList<ArrayList<NeuronGene>>();

		//Seperate the neurons genes into their corresponding layers
		//Loop through the layers
		for (int i = 0; i < numLayers; i++)
		{
			//Represents the individual layers of the neural network
			ArrayList<NeuronGene> layer = new ArrayList<NeuronGene>();

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

	public void sortNeuronArray(ArrayList<NeuronGene> genes, int numLayers)
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
			for (int j = 0; j < geneSet.get(i).size(); j++)
			{
				sortedNeuronArray.add(geneSet.get(i).get(j));
			}
		}

		genes.clear();

		for (int i = 0; i < sortedNeuronArray.size(); i++)
		{
			genes.add(sortedNeuronArray.get(i));
		}
	}

	//Should this be void since everything we pass in is the actual copy of it? 

	//Assumes the neuron passed in has the correct layer
	//This fucntion takes in a recently added neuron and then determines if the neurons that
	//proceed that added neuron need to be pushed back to a new layer
	public int pushNeurons(ArrayList<NeuronGene> neuronGenes, ArrayList<LinkGene> linkGenes, NeuronGene addedNeuron, int numLayers) 
	{

		//Represents the fill neural network seperated by layers
		ArrayList<ArrayList<NeuronGene>> geneSet = new ArrayList<ArrayList<NeuronGene>>();

		//Represents the neuron that a link points to
		NeuronGene toNeuron = new NeuronGene();

		//Represents whether the output layer was affect as special things need to happen if this happens
		boolean outputLayerPushed = false;

		//Recursivly find the next neuron to push back
		//Loop through each link on a neuron and push back that neurons neurons from that neurons links.
		for (int i= 0; i < linkGenes.size(); i++)
		{
			//If we have found a link that is enabled and not looped and comes from our neuron then check that links
			//To neuron and see if it needs to be pushed back. Then recursivly call push neurons on that
			//Neuron
			if ((linkGenes.get(i).getFromNeuron() == addedNeuron.getID()) && (linkGenes.get(i).getEnabled()) && !(linkGenes.get(i).getRecurrency()))
			{
				//Find the neuron gene that the link points to
				for (int j = 0; j < neuronGenes.size(); j++)
                {
                    if (neuronGenes.get(j).getID() == linkGenes.get(i).getToNeuron())
                    {
                       toNeuron = neuronGenes.get(j);
                    }
                }

				//Check the neuron and see if it needs to be pushed back
				if(toNeuron.getNeuronLayer() == addedNeuron.getNeuronLayer())
				{
					if (toNeuron.getNeuronLayer() == numLayers)
					{
						outputLayerPushed = true;
					}

					toNeuron.pushLayer();
					this.pushNeurons(neuronGenes, linkGenes, toNeuron, numLayers);
				}
				//else is one of the base cases(the neuron doesn't need to be pushed back)
			}
		}
		
		return (numLayers + 1);
	}

	//This function requires the incoming neuron genes to be sorted in order to properly sort the links
	//The function also requires that the push back function was called if a neuron was recently added
	//This function sorts the array of links by the layer they first appear.
	public void sortLinkArray(ArrayList<NeuronGene> neuronGenes, ArrayList<LinkGene> linkGenes)
	{
		//Represents the sorted links
		ArrayList<LinkGene> sortedLinkArray = new ArrayList<LinkGene>();

		//Represents the unsorted links
		ArrayList<LinkGene> unsortedLinkArray = linkGenes; //Create a real copy?

		//Loops through the neuron gene set and finds the corresponding links to each neuron
		for (int i = 0; i < neuronGenes.size(); i++)
		{
			//Loops through all the links
			for (int j = 0; j < unsortedLinkArray.size(); j++)
			{
				//If the links from neuron is equal to the current neuron then add it to the sorted array and remove it from the unsorted list
				if (neuronGenes.get(i).getID() == unsortedLinkArray.get(j).getFromNeuron())
				{
					//Add the link to the sorted array
					sortedLinkArray.add(unsortedLinkArray.get(j));

					//Remove the link from the unsorted array(variable trash doesn't do anything)
					//LinkGene trash = unsortedLinkArray.remove(j);
				}
			}
		}

		//Replace the current link array with the sorted one
		linkGenes.clear();
		for (int i = 0; i < sortedLinkArray.size(); i++)
		{
			linkGenes.add(sortedLinkArray.get(i));
		}
	}

	public ArrayList<LinkGene> removeDisabledLinks(ArrayList<LinkGene> linkGenes)
	{

		//Represents the link array of only active links
		ArrayList<LinkGene> activeLinkGenes = linkGenes;

		//Loops through all the links
		for (int i = 0; i < linkGenes.size(); i++)
		{
			if (!(linkGenes.get(i).getEnabled()))
			{
				//If the link is disable remove it
				LinkGene trash = activeLinkGenes.remove(i);
			}
		}

		//Return the array of active links
		return activeLinkGenes;
	}
}

