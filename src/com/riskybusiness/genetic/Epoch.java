package com.riskybusiness.genetic.test;

import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.Synapse;
import com.riskybusiness.neural.NeuralNet;
import com.riskybusiness.neural.Trainer;
import com.riskybusiness.genetic.Genome;
import com.riskybusiness.genetic.Innovation;
import com.riskybusiness.genetic.InnovationDB;
import com.riskybusiness.genetic.InnovationType;
import com.riskybusiness.genetic.LinkGene;
import com.riskybusiness.genetic.NeuronGene;
import com.riskybusiness.genetic.UniqueID;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class Epoch
{
	public static void main(String... arg) throws Exception
	{
		/* Parameters */

		//Represents the size of the population
		int 	populationSize 			= 1;
		//Represents the number of input neurons
		int 	numInputNeurons			= 2;
		//Represents the number of output neurons
		int 	numOutputNeurons		= 1;
		//Represents the number of initial hidden layers
		int 	numHiddenLayers			= 1;
		//Represents the number of initial neurons in each hidden layer
		int[]	hiddenLayers 			= new int[numHiddenLayers](1);
		//Represents the number of neurons in the genome up to the given index
		int[] 	summationNeuronsInLayer	= new int[numHiddenLayers + 3](0, numInputNeurons, 2, numOutputNeurons);


		/* Helper Items */

		//Represents the item used to create pseudo-random numbers
		Random random = new Random();


		/* Population Items */

		//Represents the array of created neurons
		ArrayList<NeuronGene> 	neuronGenes	= new ArrayList<NeuronGene>();
		//Represents the array of created links
   		ArrayList<LinkGene>   	linkGenes  	= new ArrayList<LinkGene>();
   		//Represents the genomes of the population
   		Genome[]				population 	= new Genome[populationSize];
   		//Represents the neural networks
   		NeuralNet[]				myNetworks	= new NeuralNet[populationSize];


   		/* Historical Data */	

   		//Represents the historical changes of all the previous populations
   		InnovationDB 			innovations	= new InnovationDB();		



		//Create or load initial genomes
		for (int lcv = 0; lcv < populationSize; lcv++)
		{
			//Reset the genes for the new population
			neuronGenes.clear();
			linkGenes.clear();

			//The next three loops will create the neurons
			//Currently, there is no need to seperate into three loops, but I am working on implementing different neuron types
			for (int i = 0; i < numInputNeurons; i++)
			{
				fweight = random.nextFloat();
				neuronGenes.add(new NeuronGene(unique.getNextNeuronID(), "Sigmoid", false, fweight, "Input")); 
			}
			for (int i = 0; i < summationNeuronsInLayer[(numHiddenLayers + 1)] - summationNeuronsInLayer[1]; i++)
			{
				fweight = random.nextFloat();
				neuronGenes.add(new NeuronGene(unique.getNextNeuronID(), "Sigmoid", false, fweight, "Hidden"));
			}
			for (int i = 0; i < summationNeuronsInLayer[(numHiddenLayers + 2)] - summationNeuronsInLayer[(numHiddenLayers + 1)]; i++)
			{
				fweight = random.nextFloat();
				neuronGenes.add(new NeuronGene(unique.getNextNeuronID(), "Step", false, fweight, "Output"));
			}

			for (int i = 0; i < numHiddenLayers + 1; i++)
			{
				//This loop goes through each neuron in the active layer
				for (int j = summationNeuronsInLayer[i] + 1; j <= summationNeuronsInLayer[(i + 1)]; j++)
				{
					//This loop goes through each neuron in the layer that comes after the active layer
					for (int k = summationNeuronsInLayer[(i + 1)] + 1; k <= summationNeuronsInLayer[(i + 2)]; k++)
					{
						//Create random weight
						dweight = random.nextDouble();
						//Add the link to the link gene array
			 			linkGenes.add(new LinkGene(j, k, unique.getNextLinkID(), dweight, false));
					}
				}
			}
		}
	}
}