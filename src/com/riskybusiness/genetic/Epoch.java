////////////////////////////////////////////////////////////////////////////////////////////////////
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
		Random 	random 	= new Random();
		//Represents the scanner to get user input
		Scanner input 	= new Scanner(System.in);


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

   		System.out.println("Would you like to load[L] a file or start fresh[S]?")
   		userInput = input.nextLine();
   		while (userInput.toUpperCase() != "S" || userInput.toUpperCase() != "L")
   		{
   			System.out.println("You need to enter either 'L' or 'S' to begin!")
   			System.out.println("Would you like to load[L] a file or start fresh[S]?")
   			userInput = input.nextLine();
   		}
   		
   		if (userInput == "L") loadFile = true;



   		if (loadFile)
   		{
   			int temp = 0;
   		} 
   		else 
   		{
			//Create the initial genomes
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


				//Create the link genes
				//This behemoth of a triple nested loop inside a loop simply goes creates the links between all the neurons
				//The first loop increments the active layer
				for (int i = 0; i < numHiddenLayers + 1; i++)
				{
					//This loop goes through each neuron in the active layer
					for (int j = summationNeuronsInLayer[i] + 1; j <= summationNeuronsInLayer[(i + 1)]; j++)
					{
						linksCreated = 0;
						//This loop goes through each neuron in the layer that comes after the active layer
						for (int k = summationNeuronsInLayer[(i + 1)] + 1; k <= summationNeuronsInLayer[(i + 2)]; k++)
						{
							if (fullLink || random.nextDouble() <= chanceOfLink)
							{
								linksCreated++;
								//Create random weight
								dweight = random.nextDouble();
								//Add the link to the link gene array
				 				linkGenes.add(new LinkGene(j, k, unique.getNextLinkID(), dweight, false));
							}
						}
						//Still have to deal with the fact that this way results in some neurons not getting links
						if (linksCreated == 0)
						{
							int toNeuron = random.nextInt(summationNeuronsInLayer[(i + 2)] - (summationNeuronsInLayer[(i + 1)] + 1) + summationNeuronsInLayer[(i + 1)] + 1); 
						}
					}
				}

				population.add(new Genome(lcv + 1, neuronGenes, linkGenes, numInputNeurons, numOutputNeurons, innovations);
			}
		}

		//Represents the newPopulation created from the old one
		ArrayList<Genome> 	newPopulation 		= new ArrayList<Genome>();
		//Represents the population, but classified into their corresponding species
		Species 			species 			= new Species(population);
		//Represents the number of children spawned so far
		int 				numChildrenSpawned 	= 0;
		//Represents the number of children to spawn
		int 				numberToSpawn 		= 10;
		//Represents the child of a crossover
		Genome 				child;
		//Represents the survivor
		Genome 				survivor;




		//Start epoch
		/* Note:
		 *   Need to add more when I implement speciation
		 */

		for (int speciesID = 0; speciesID < species.getNumSpecies(); i++)
		{
			if (numChildrenSpawned < numberToSpawn)
			{
				for (int i = 0; i < species.getNumSpawns(speciesID); i++)
				{
					//If this is our first pass then the first thing we want to do
					//is grab the best member of the species and pass them along
					if (i == 0)
					{
						survivor = species.getBestMember(speciesID);
					}
					else
					{
						//If the size of the species is only 1 then we can't breed
						if (species.getNumMembers(speciesID) == 1)
						{
							survivor = species.getMember(speciesID);
						}
						else
						{
							Genome mom = species.getMember(speciesID);

							if (random.nextDouble() < crossoverRate)
							{
								
							}
						}
					}
				}
			}
		}


	}
}