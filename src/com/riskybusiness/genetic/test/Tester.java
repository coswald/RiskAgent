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
import com.riskybusiness.genetic.Species;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class Tester
{
	public static void main(String... arg) throws Exception
	{
		/* Tester Params */
		
		//Represents the debugging option
		boolean debug 		= false;
		//Tester variable
		boolean testFire 	= false;
		//Tester variable
		boolean printGenome = false;
		//Tester variable
		boolean testAddNeuron = true;

		/*User Params */
		
		//Represents whether a user is going to load a genome
		boolean	loadFile 	= false;
		//Represents the users input
		String 	userInput;


		/* Link Initialization Params */

		//Represents the chance of a link spawning
		double 	chanceOfLink	= 0.5;
		//Represents whether the links will be full or partial
		boolean fullLink		= true;
		//Represents the number of links created for the active neuron
		int 	linksCreated	= 0;

		/* Parameters */

		//Represents the size of the population
		int 	populationSize 			= 1;
		//Represents the number of input neurons
		int 	numInputNeurons			= 3;
		//Represents the number of output neurons
		int 	numOutputNeurons		= 1;
		//Represents the number of initial hidden layers
		int 	numHiddenLayers			= 5;
		//Represents the number of initial neurons in each hidden layer
		int[]	hiddenLayers 			= {4, 3, 4, 3, 2};
		//Represents the number of neurons in the genome up to the given index
		int[] 	summationNeuronsInLayer	= new int[numHiddenLayers + 3];
		//Represents the current neuron ID 
		int 	curNeuronID 			= 0;
		//Represents the current link ID
		int 	curLinkID 				= 0;
		//Represents the current genome ID
		int 	curGenomeID 			= 0;



		/* Helper Items */

		//Represents the item used to create pseudo-random numbers
		Random 	random 	= new Random();
		//Represents the scanner to get user input
		Scanner input 	= new Scanner(System.in);
		//Represents the package used to create unique ID's
		UniqueID unique = new UniqueID();


		/* Population Items */

		//Represents the array of created neurons
		ArrayList<NeuronGene> 	neuronGenes	= new ArrayList<NeuronGene>();
		//Represents the array of created links
   		ArrayList<LinkGene>   	linkGenes  	= new ArrayList<LinkGene>();
   		//Represents the genomes of the population
   		ArrayList<Genome>		population 	= new ArrayList<Genome>();
   		//Represents the neural networks
   		ArrayList<NeuralNet>	myNetworks	= new ArrayList<NeuralNet>();


   		/* Historical Data */	

   		//Represents the historical changes of all the previous populations
   		InnovationDB 			innovations	= new InnovationDB();
   		//Represents the current generation
   		int 					generation 	= 1;		

   		System.out.println("Would you like to load[L] a file or start fresh[S]?");
   		userInput = input.nextLine();
   		/*
   		while (!(userInput == "S" || userInput == "L"))
   		{
   			System.out.println("You need to enter either 'L' or 'S' to begin!");
   			System.out.println("Would you like to load[L] a file or start fresh[S]?");
   			userInput = input.nextLine();
   		}
   		
   		if (userInput == "L") loadFile = true;
		*/
   		if (loadFile)
   		{
   			int temp = 0;
   		} 
   		else 
   		{
   			//Initialize number of neurons
   			summationNeuronsInLayer[0] = 0;
   			summationNeuronsInLayer[1] = numInputNeurons;
   			for (int i = 0; i < numHiddenLayers; i++)
   			{
   				summationNeuronsInLayer[i + 2] = summationNeuronsInLayer[i + 1] + hiddenLayers[i];
   			}
   			summationNeuronsInLayer[numHiddenLayers + 2] = numOutputNeurons + summationNeuronsInLayer[numHiddenLayers + 1];


			//Create the initial genomes
			for (int lcv = 0; lcv < populationSize; lcv++)
			{
				//Reset the genes for the new population
				neuronGenes.clear();
				linkGenes.clear();

				if (debug)
				{
					System.out.println("Creating Neurons...");
				}

				//The next three loops will create the neurons
				//Currently, there is no need to seperate into three loops, but I am working on implementing different neuron types
				for (int i = 0; i < numInputNeurons; i++)
				{
					float fweight = random.nextFloat();
					neuronGenes.add(new NeuronGene(++curNeuronID, "Sigmoid", false, fweight, 1)); 
				}
				for (int i = 0; i < numHiddenLayers; i++)
				{
					for (int j = summationNeuronsInLayer[i + 1]; j < summationNeuronsInLayer[i + 2]; j++)
					{
						float fweight = random.nextFloat();
						neuronGenes.add(new NeuronGene(++curNeuronID, "Sigmoid", false, fweight, (i + 2)));
					}
				}
				for (int i = 0; i < numOutputNeurons; i++)
				{
					float fweight = random.nextFloat();
					neuronGenes.add(new NeuronGene(++curNeuronID, "Sigmoid", false, fweight, (numHiddenLayers + 2)));
				}

				if (debug)
				{
					System.out.println("Creating Links...");
				}

				//Create the link genes
				//This behemoth of a triple nested loop inside a loop simply goes creates the links between all the neurons
				//The first loop increments the active layer
				for (int i = 0; i < numHiddenLayers + 1; i++)
				{
					//This loop goes through each neuron in the active layer
					for (int j = summationNeuronsInLayer[i]; j < summationNeuronsInLayer[(i + 1)]; j++)
					{
						linksCreated = 0;
						//This loop goes through each neuron in the layer that comes after the active layer
						for (int k = summationNeuronsInLayer[(i + 1)]; k < summationNeuronsInLayer[(i + 2)]; k++)
						{
							if (fullLink || random.nextDouble() <= chanceOfLink)
							{
								linksCreated++;
								//Create random weight
								double dweight = random.nextDouble();
								//Add the link to the link gene array
				 				linkGenes.add(new LinkGene(neuronGenes.get(j).getID(), neuronGenes.get(k).getID(), ++curLinkID, dweight, false));
							}
						}
						//Still have to deal with the fact that this way results in some neurons not getting links
						if (linksCreated == 0)
						{
							int toNeuron = random.nextInt(summationNeuronsInLayer[(i + 2)] - (summationNeuronsInLayer[(i + 1)] + 1) + summationNeuronsInLayer[(i + 1)] + 1); 
						}
						/**
						Need to ensure all neurons connect to output layer too and have a connectionto the input layer
						**/
					}
				}

				if (debug)
				{
					System.out.println("Adding genome to population");
				}

				population.add(new Genome(++curGenomeID, neuronGenes, linkGenes, numInputNeurons, numOutputNeurons, innovations));

			}
		}

		if (printGenome)
		{
			for (int i = 0; i <  population.size(); i++)
			{
				population.get(i).print();
			}
		}

		for (int i = 0; i <  population.size(); i++)
		{
			myNetworks.add(population.get(i).createPhenotype());
		}

		if (testFire)
		{

			// float[] outputs = new float[numOutputNeurons];

			// float[][] inputs = new float[numInputNeurons][1];

			for (int i = 0; i <  population.size(); i++)
			{
				for(float x = 0; x < 2; x++)
				{
					for(float y = 0; y < 2; y++)
					{
						System.out.println(myNetworks.get(i).fire(new float[][] {new float[] {x}, new float[] {y}})[0]);
					}
				}
			}
		}

		if (testAddNeuron)
		{
			for (int i = 0; i <  population.size(); i++)
			{
				population.get(i).addNeuron(1, innovations, 20);
			}
		}

		// if (testPush)
		// {

		// 	//Represents the package that rpovide the genome with helper functions
  //   		private GenomeHelper genomeHelper = new GenomeHelper();

		// 	genomeHelper.pushNeurons()
		// }

		if (printGenome)
		{
			for (int i = 0; i <  population.size(); i++)
			{
				population.get(i).print();
			}
		}

		System.out.println("Population size: " + population.size());
	}
}