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

public class Epoch
{
	public static void main(String... arg) throws Exception
	{
		/*User Params */
		
		//Represents whether a user is going to load a genome
		boolean	loadFile 	= false;
		//Represents the users input
		String 	userInput;
		//Represents the debugging option
		boolean debug 		= true;


		/* Link Initialization Params */

		//Represents the chance of a link spawning
		double 	chanceOfLink	= 0.5;
		//Represents whether the links will be full or partial
		boolean fullLink		= true;
		//Represents the number of links created for the active neuron
		int 	linksCreated	= 0;

		/* Parameters */

		//Represents the size of the population
		int 	populationSize 			= 10;
		//Represents the number of input neurons
		int 	numInputNeurons			= 2;
		//Represents the number of output neurons
		int 	numOutputNeurons		= 1;
		//Represents the number of initial hidden layers
		int 	numHiddenLayers			= 1;
		//Represents the number of initial neurons in each hidden layer
		int[]	hiddenLayers 			= new int[numHiddenLayers];
		//Represents the number of neurons in the genome up to the given index
		int[] 	summationNeuronsInLayer	= new int[numHiddenLayers + 3];
		//Represents the current neuron ID 
		int 	curNeuronID 			= 0;
		//Represents the current link ID
		int 	curLinkID 				= 0;



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
   		//Represents the ID of the genome
   		int 					genomeID 	= 0;


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
   			//Initialize number of hidden layers
   			hiddenLayers[0] = 1;

   			//Initialize number of neurons
   			summationNeuronsInLayer[0] = 0;
   			summationNeuronsInLayer[1] = numInputNeurons;
   			summationNeuronsInLayer[2] = numInputNeurons + 2;
   			summationNeuronsInLayer[3] = numOutputNeurons + 2 + numInputNeurons;


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
					neuronGenes.add(new NeuronGene(unique.getNextNeuronID(), "Sigmoid", false, fweight, 1)); 
				}
				for (int i = 0; i < summationNeuronsInLayer[(numHiddenLayers + 1)] - summationNeuronsInLayer[1]; i++)
				{
					float fweight = random.nextFloat();
					neuronGenes.add(new NeuronGene(unique.getNextNeuronID(), "Sigmoid", false, fweight, 2));
				}
				for (int i = 0; i < summationNeuronsInLayer[(numHiddenLayers + 2)] - summationNeuronsInLayer[(numHiddenLayers + 1)]; i++)
				{
					float fweight = random.nextFloat();
					neuronGenes.add(new NeuronGene(unique.getNextNeuronID(), "Step", false, fweight, 3));
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
					}
				}

				if (debug)
				{
					System.out.println("Adding genome to population");
				}

				population.add(new Genome(genomeID++, neuronGenes, linkGenes, numInputNeurons, numOutputNeurons, innovations));
			}
		}

		System.out.println("Population size: " + population.size());

		//Represents the newPopulation created from the old one
		ArrayList<Genome> 	newPopulation 		= new ArrayList<Genome>();
		//Represents the population, but classified into their corresponding species
		Species 			species 			= new Species(population);
		//Represents the number of children spawned so far
		int 				numChildrenSpawned 	= 0;
		//Represents the number of children to spawn
		int 				numberToSpawn 		= 10;
		//Represents the child of a crossover
		Genome 				child 				= population.get(0);
		//Represents the rate at which a genomes breed
		double 				crossoverRate 		= 0.5;


		//Start epoch
		/* Note:
		 *   Need to add more when I implement speciation
		 */

		//Debug function
		if (debug)
		{
			System.out.println("Number of species: " + species.getNumSpecies());
		}

		//Loop through each species and spawn genomes from each species
		for (int speciesID = 0; speciesID < species.getNumSpecies(); speciesID++)
		{
			//Check to see if we still need to spawn children
			if (numChildrenSpawned < numberToSpawn)
			{
				//Debug function
				if (debug)
				{
					System.out.println("Number of spawns: " + species.getNumSpawns(speciesID));
				}
				//Creates the number of children necassary for the species
				for (double i = 0.0; i < species.getNumSpawns(speciesID); i++)
				{
					//Debug function
					if (debug)
					{
						System.out.println ("Creating genome: " + (int)i);
					}
					//If this is our first pass then the first thing we want to do
					//is grab the best member of the species and pass them along
					if (i == 0.0)
					{
						//Debug function
						if (debug)
						{
							System.out.println("Elitism Loop");
						}
						//Use elitism and always take the best member from the population
						child = species.getBestMember(speciesID);
					}
					else
					{
						//If the size of the species is only 1 then we can't breed
						if (species.getNumMembers(speciesID) == 1)
						{
							//Grab a member from the species
							child = species.getMember(speciesID);
							//Debug function
							if (debug)
							{
								System.out.println("Grabbing the sole survivor!");
							}
						}
						else
						{
							//Grab a random member to breed with or mutate
							Genome mom = species.getMember(speciesID);

							//Debug function
							if (debug)
							{
								System.out.println("Grabbing mom, ID: " + mom.getID());
							}

							//Decide whether to do crossover or not based on the crossover rate
							if (random.nextDouble() < crossoverRate)
							{
								//Grab a random genome to breed with
								Genome 	dad = species.getMember(speciesID);
								
								//Debug function
								if (debug)
								{
									System.out.println("Grabbing dad, ID: " + dad.getID());
								}
								
								//Represents th loop control variable
								int 	lcv = 10;

								//If the dad is the same as the mom then loop and try to find a
								//new dad. Only do this as many times the lcv is initialized to.
								while (dad.getID() == mom.getID())
								{
									//Debug function
									if (debug)
									{
										System.out.println("Dad is the same as mom");
									}

									//Grab a random genome to breed with
									dad = species.getMember(speciesID);
									
									//Decrement the lcv
									lcv--;

									//If the lcv is 0 then break out of the loop
									if (lcv == 0)
									{
										break;
									}
								}

								//If the dad genome does not equal the mom genome the breed
								if (dad.getID() != mom.getID())
								{
									//Debug function
									if (debug)
									{
										System.out.println("Breeding...");
									}
									//Breed the mom with the dad
									child = mom.makeBabies(dad);
								}
								else
								{
									//Debug function
									if (debug)
									{
										System.out.println("Couldn't breed");
									}
									//If the dad does equal the mom then simply set the child
									//equal to mom.
									child = mom;
								}
							}

							//Set the id of the child to the next genome ID
							child.setID(genomeID++);


							//Do we want to set a limit on the number of nuerons?
							//if ()

							//child.addNeuron(0.8, innovations, 10);
							//child.addLink(0.8, 0.15, innovations, 10, 20);
							//child.mutateLinks(0.8);
							//child.mutateNeurons(0.8);
						}
					}	
					
					//Debug function
					if (debug)
					{
						System.out.println("Adding member to population");
					}
					
					//Add the child to the new population
					newPopulation.add(child);
				}
			}
		}
		//Fancy ass math to determine if we met the required amount of children
		//??

		//Transfer the next generation
		population = newPopulation;

		//Increment the generation counter
		generation++;
	}
}