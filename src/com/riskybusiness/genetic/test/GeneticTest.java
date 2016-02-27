package com.riskybusiness.genetic.test;

import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.Synapse;
import com.riskybusiness.neural.NeuralNet;
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

public class GeneticTest
{
	public static void main(String... arg) throws Exception
	{
		//Represents the scanner to get input
		Scanner input = new Scanner(System.in);
		//Represents the array of created neurons
		ArrayList<NeuronGene> neuronGenes = new ArrayList<NeuronGene>();
		//Represents the array of created links
   		ArrayList<LinkGene>   linkGenes   = new ArrayList<LinkGene>();
		//Represents the population of genomes created
		ArrayList<Genome>     myGenomes   = new ArrayList<Genome>();
   		//Represents the number of hidden layers
		int numHiddenLayers;
		//Represents the size of the population
		int populationSize;
		//Loop control variables
		int h;
		int i;
		int j;
		int k;
		//Represents the current id of the neuron to be added
		//int neuronID = 1; //Also, this is a really piss poor way to address the issue that exists
		//Represents the current id of the link to be added
		//int linkID   = 1; //Also, this is a really piss poor way to address the issue that exists
		//Represents the current id of the genome to be added
		//int genomeID = 1; //Also, this is a really piss poor way to address the issue that exists
		//Represent the class used to create the unique ID's for the neurons, links, and genomes
		UniqueID unique = new UniqueID();
		//Represents the variable used to create psuedorandom numbers
		Random random = new Random();
		//Represents the weight to be assigned to the neuron
		float fweight;
		//Represents the weight to be assigned to the link
		double dweight;
		//Represents the neural network
		NeuralNet myNetwork;

		System.out.println("How many hidden layers are there?");
		numHiddenLayers = input.nextInt();

		int[] hiddenLayers = new int[(numHiddenLayers)];
		int[] summationNeuronsInLayer = new int[(numHiddenLayers + 3)];

		//Note: numInputNeurons = summationNeuronsInLayer[1]
		//Note: numOutputNeurons = summationNeuronsInLayer[(numHiddenLayers + 2)] - summationNeuronsInLayer[(numHiddenLayers + 1)]

		summationNeuronsInLayer[0] = 0;

		System.out.println("How many input neurons are there?");
		summationNeuronsInLayer[1] = input.nextInt();

		for (i = 0; i < numHiddenLayers; i++)
		{
			System.out.println("How many hidden neurons are there in hidden layer " + (i + 1) + "?");
			hiddenLayers[i] = input.nextInt();
			summationNeuronsInLayer[(i+2)] = summationNeuronsInLayer[(i+1)] + hiddenLayers[i];
		}

		System.out.println("How many output neurons are there?");
		summationNeuronsInLayer[numHiddenLayers + 2] = summationNeuronsInLayer[numHiddenLayers + 1] + input.nextInt();
		
		System.out.println("How large is your population?");
		populationSize = input.nextInt();

		//Have fun trying to figure out what the hell is going on here
		for (h = 1; h <= populationSize; h++)
		{

			//The next three loops will create the neurons
			//Currently, there is no need to seperate into three loops, but I am working on implementing different neuron types
			for (i = 0; i < summationNeuronsInLayer[1]; i++)
			{
				fweight = random.nextFloat();
				neuronGenes.add(new NeuronGene(unique.getNextNeuronID(), "Sigmoid", false, fweight/**, INPUT**/)); 
			}
			for (i = 0; i < summationNeuronsInLayer[(numHiddenLayers + 1)] - summationNeuronsInLayer[1]; i++)
			{
				fweight = random.nextFloat();
				neuronGenes.add(new NeuronGene(unique.getNextNeuronID(), "Sigmoid", false, fweight/**, HIDDEN**/));
			}
			for (i = 0; i < summationNeuronsInLayer[(numHiddenLayers + 2)] - summationNeuronsInLayer[(numHiddenLayers + 1)]; i++)
			{
				fweight = random.nextFloat();
				neuronGenes.add(new NeuronGene(unique.getNextNeuronID(), "Step", false, fweight/**, OUTPUT**/));
			}

			//Create links
			//This presents a wierd scenario. Will all neurons be connected in the beginning? If no how will we determine to connect them?
			//I am going to go with the approach that all are connected to begin with

			//This behemoth of a triple nested loop inside a loop simply goes creates the links between all the neurons
			//The first loop increments the active layer
			for (i = 0; i < numHiddenLayers + 1; i++)
			{
				//This loop goes through each neuron in the active layer
				for (j = summationNeuronsInLayer[i] + 1; j <= summationNeuronsInLayer[(i + 1)]; j++)
				{
					//This loop goes through each neuron in the layer that comes after the active layer
					for (k = summationNeuronsInLayer[(i + 1)] + 1; k <= summationNeuronsInLayer[(i + 2)]; k++)
					{
						//Create random weight
						dweight = random.nextDouble();
						//Add the link to the link gene array
			 			linkGenes.add(new LinkGene(j, k, unique.getNextLinkID(), dweight, false));
					}
				}
			}

			//Create a genome!!
			myGenomes.add(new Genome(unique.getNextGenomeID(), neuronGenes, linkGenes, summationNeuronsInLayer[1], summationNeuronsInLayer[(numHiddenLayers + 2)] - summationNeuronsInLayer[(numHiddenLayers + 1)]));
		
			System.out.println("Creaeted genome: " + unique.getCurGenomeID() + "!");//  Neurons: " + myGenomes.get(unique.getCurGenomeID()).getSizeNeuron() + " Links: " + myGenomes.get(unique.getCurGenomeID()).getSizeLink());
		}


		/**
		 * Space to create testers for manipulating genome
		 *
		 *
		 *
		 *
		 *
		**/

		//Create a neural network!!
		//System.out.println(myGenome.getSizeNeuron());
		//System.out.println(myGenome.getSizeLink());
		//myNetwork = myGenome.createPhenotype();

	}
}

