/* 
 * Copyright (C) 2016  Coved Oswald, Kaleb Luse, and Weston Miller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.riskybusiness.genetic;

import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.StepNeuron;
import com.riskybusiness.neural.SigmoidNeuron;

import java.util.ArrayList;

public final class GenomeHelper extends Object
{
	private GenomeHelper(){}

	//This function takes in an array of neuron genes and seperates them by layer
	public static ArrayList<ArrayList<NeuronGene>> seperate(ArrayList<NeuronGene> genes, int numLayers)
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
				}
			}

			//Once all the neuron have been checked add the layer to the final genome
			seperatedNeuronArray.add(layer);
		}

		return seperatedNeuronArray;
	}

	public static void sortNeuronArray(ArrayList<NeuronGene> genes, int numLayers)
	{

		//Represents the array to return to the calling function
		ArrayList<NeuronGene> sortedNeuronArray = new ArrayList<NeuronGene>();

		//Represents the fill neural network seperated by layers
		ArrayList<ArrayList<NeuronGene>> geneSet = new ArrayList<ArrayList<NeuronGene>>();

		//Seperate the given neuron gene array into an array of layers
		geneSet = seperate(genes, numLayers);

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

	//Assumes the neuron passed in has the correct layer
	//This fucntion takes in a recently added neuron and then determines if the neurons that
	//proceed that added neuron need to be pushed back to a new layer
	public static void pushNeurons(ArrayList<NeuronGene> neuronGenes, ArrayList<LinkGene> linkGenes, NeuronGene addedNeuron) 
	{
		//Represents the neuron that a link points to
		NeuronGene 	toNeuron 	= new NeuronGene();

		//Recursivly find the next neuron to push back
		//Loop through each link on a neuron and push back that neurons neurons from that neurons links.
		for (int i = 0; i < linkGenes.size(); i++)
		{
			//If we have found a link that is enabled and not looped and comes from our neuron then check that links
			//To neuron and see if it needs to be pushed back. Then recursivly call push neurons on that
			//Neuron
			if ((linkGenes.get(i).getFromNeuron() == addedNeuron.getID()) && !(linkGenes.get(i).getFromNeuron() == addedNeuron.getID()))
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
				if (toNeuron.getNeuronLayer() == addedNeuron.getNeuronLayer())
				{
					if (toNeuron.getLayerType() == "Output")
					{
						for (int k = 0; k < neuronGenes.size(); k++)
						{
							if (neuronGenes.get(k).getLayerType() == "Output")
							{
								neuronGenes.get(k).pushLayer();
							}
						}
						pushNeurons(neuronGenes, linkGenes, toNeuron);
					}
					else
					{
						toNeuron.pushLayer();
						pushNeurons(neuronGenes, linkGenes, toNeuron);
					}
				}
				//else is one of the base cases(the neuron doesn't need to be pushed back)
			}
		}
	}

	//This function requires the incoming neuron genes to be sorted in order to properly sort the links
	//The function also requires that the push back function was called if a neuron was recently added
	//This function sorts the array of links by the layer they first appear.
	public static void sortLinkArray(ArrayList<NeuronGene> neuronGenes, ArrayList<LinkGene> linkGenes)
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

	public static ArrayList<LinkGene> removeDisabledLinks(ArrayList<LinkGene> linkGenes)
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