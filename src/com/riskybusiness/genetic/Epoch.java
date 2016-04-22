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

//Used for saving genomes
import java.lang.InterruptedException;
import java.lang.Object;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
		String 	userInput 	= "";
		//Represents the debugging option
		boolean debug 		= false;


		/* Link Initialization Params */

		//Represents the chance of a link spawning
		double 	chanceOfLink	= 0.5;
		//Represents whether the links will be full or partial
		boolean fullLink		= true;
		//Represents the number of links created for the active neuron
		int 	linksCreated	= 0;

		/* Parameters */

		//Represents the size of the population
		int 	populationSize 			= 50;
		//Represents the max number of generations without improvement before a species is killed
		int 	extinctionLimit			= 20;
		//Represents the number of input neurons
		int 	numInputNeurons			= 13;
		//Represents the number of output neurons
		int 	numOutputNeurons		= 1;
		//Represents the number of initial hidden layers
		int 	numHiddenLayers			= 2;
		//Represents the number of initial neurons in each hidden layer
		int[]	hiddenLayers 			= {13,1};
		//Represents the number of neurons in the genome up to the given index
		int[] 	summationNeuronsInLayer	= new int[numHiddenLayers + 3];
		//Represents the current neuron ID 
		int 	curNeuronID 			= 0;
		//Represents the current link ID
		int 	curLinkID 				= 0;
		//Represents the maximum number of species allowed
		int 	maxNumSpecies 			= 6;
		//Represents the species threshold which determines whether a genome will be accepted into a species
		double speciesThreshold 		= 0.15;
		//Represents how much we should increase the compatibility threshhold should we exceed the number of species
		double 	threshholdPerturbation 	= 0.02;



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
   		//Represents the ID of the genome
   		int 					genomeID 	= 0;


   		/* Historical Data */	

   		//Represents the historical changes of all the previous populations
   		InnovationDB 			innovations	= new InnovationDB(5);	

   		//Validate user input and determine if they would like to load a file or start new
   		while (!(userInput.equals("L") || userInput.equals("S")))
   		{
   			System.out.println("Would you like to load[L] a file or start fresh[S]?");
   			userInput = input.nextLine();
   		}


   		if (!loadFile) 
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
				curNeuronID = 0;
				curLinkID = 0;

				if (debug)
				{
					System.out.println("Creating Neurons...");
				}

				//The next three loops will create the neurons
				//Currently, there is no need to seperate into three loops, but I am working on implementing different neuron types
				for (int i = 0; i < numInputNeurons; i++)
				{
					double dweight = random.nextDouble();
					innovations.addInnovation(InnovationType.NEW_NEURON, -1, -1, ++curNeuronID);
					neuronGenes.add(new NeuronGene(curNeuronID, "Sigmoid", "Input", dweight, 1)); 
				}
				for (int i = 0; i < numHiddenLayers; i++)
				{
					for (int j = summationNeuronsInLayer[i + 1]; j < summationNeuronsInLayer[i + 2]; j++)
					{
						double dweight = random.nextDouble();
						innovations.addInnovation(InnovationType.NEW_NEURON, -1, -1, ++curNeuronID);
						neuronGenes.add(new NeuronGene(curNeuronID, "Sigmoid", "Hidden", dweight, (i + 2)));
					}
				}
				for (int i = 0; i < numOutputNeurons; i++)
				{
					double dweight = random.nextDouble();
					innovations.addInnovation(InnovationType.NEW_NEURON, -1, -1, ++curNeuronID);
					neuronGenes.add(new NeuronGene(curNeuronID, "Sigmoid", "Output", dweight, (numHiddenLayers + 2)));
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
								//Add the innovation
								if (neuronGenes.get(j).getID() <= numInputNeurons)
								{
									if (neuronGenes.get(j).getID() == (neuronGenes.get(k).getID() - numInputNeurons))
									{
										linkGenes.add(new LinkGene(linkGenes.size() + 1, neuronGenes.get(j).getID(), neuronGenes.get(k).getID(), innovations.curID(), 1, true));
									}
								}

								else 
								{
									int innovationCheck = innovations.addInnovation(InnovationType.NEW_LINK, neuronGenes.get(j).getID(), neuronGenes.get(k).getID(), -1);
									if (innovationCheck == 0)
								    {
								        linkGenes.add(new LinkGene(linkGenes.size() + 1, neuronGenes.get(j).getID(), neuronGenes.get(k).getID(), innovations.curID(), random.nextDouble(), true));
								    }
								    else
								    {
								        linkGenes.add(new LinkGene(linkGenes.size() + 1, neuronGenes.get(j).getID(), neuronGenes.get(k).getID(), innovationCheck, random.nextDouble(), true));
								    }
								}
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

				population.add(new Genome(++genomeID, neuronGenes, linkGenes, numInputNeurons, numOutputNeurons));
			}
			System.out.println("Population size: " + population.size());
		}


		//Represents the newPopulation created from the old one
		ArrayList<Genome> 	newPopulation 		= new ArrayList<Genome>();
		//Represents the speciated population
		ArrayList<Species> 	species 			= new ArrayList<Species>();
		//Represents the ID of the current species
		int 				speciesID 			= 0;
		//Represents the best genome seen so far
		Genome 				theChosenOne		= new Genome();
		//Represents the best fitness seen so far
		double 				bestFitness			= 0.0;
		//Represents the number of children spawned so far
		int 				numChildrenSpawned 	= 0;
		//Represents the number of children to spawn
		int 				numberToSpawn 		= 100;
		//Represents the genome used to make deep copies
		Genome 				toCopy 				= new Genome();
		//Represents the child of a crossover
		Genome 				child 				= new Genome();
		//Represents the rate at which a genomes breed
		double 				crossoverRate 		= 0.3;
		//Represents the total adjusted fitness of the entire population
		double 				totalAdjustedFitness = 0.0;

		//If load file then load all the variables from the file
		if (loadFile)
		{
			//Initialize the readers
			ObjectInputStream populationReader = null;
			ObjectInputStream speciesReader = null;
			ObjectInputStream parametersReader = null;
			//Try reading the data from the files
			try
			{
				//Initialize the readers and their files as well
				System.out.println("\tAttempting to load the genome file");
				populationReader = new ObjectInputStream(new FileInputStream("population.txt"));
				speciesReader 	 = new ObjectInputStream(new FileInputStream("species.txt"));
				parametersReader = new ObjectInputStream(new FileInputStream("parameters.txt"));

				//Read in all the data
				population 		= (ArrayList<Genome>) populationReader.readObject();
				species 		= (ArrayList<Species>) speciesReader.readObject();
				genomeID 		= (int) parametersReader.readObject();
				theChosenOne 	= (Genome) parametersReader.readObject();
				innovations 	= (InnovationDB) parametersReader.readObject();
				speciesID 		= (int) parametersReader.readObject();
				bestFitness 	= (double) parametersReader.readObject();

				//Close the readers
				if(populationReader != null)
					populationReader.close();
				if(speciesReader != null)
					speciesReader.close();
				if(parametersReader != null)
					parametersReader.close();
			}
			catch(Exception e)
			{
				System.out.println("\tError!");
				e.printStackTrace();
				System.err.println(e.toString());
				System.exit(1);
			}
			finally
			{
				System.out.println("\tInput Successful!");
			}
		}

		//Starts the program
		System.out.println("Hit [Enter] to start!");
   		userInput = input.nextLine();


		for (int generation = 1; generation < 20000; generation++)
		{
			System.out.println("Generation: " + generation);
			System.out.println("Innovation Size: " + innovations.getSize());

			//Loop through the species and kill off species that aren't improving and 
			//create a new generation for each species
			for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
			{
				//Create the new generation
				species.get(speciesIndex).newGeneration();

				//Check for extinciont
				if (species.get(speciesIndex).gensWithNoImprovement() > extinctionLimit)
				{
					//Kill off the species
					species.remove(speciesIndex);
					//Decrement the species index to account for the loss of a species
					speciesIndex--;
				}
			}

			//Represents the fitenss of the competitor
			double competitorFitness;

			//Loop through each species and see if any of the alphas has surpassed the best
			//fitness seen so far
			for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
			{
				//Find the fitness of the competitor
				competitorFitness = species.get(speciesIndex).getBestFitness();

				//If the competitor fitness is better than the best so far then do stuff
				if (competitorFitness > bestFitness)
				{
					//Copy the best member into the toCopy genome
					toCopy = species.get(speciesIndex).getBestMember();
					//Set the best fitness to the competitor fitness
					bestFitness = competitorFitness;
					//Create the chosen one
					theChosenOne = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
				}
			}

			//Check if we have exceeded the number of species
			if (species.size() > maxNumSpecies)
			{
				speciesThreshold += threshholdPerturbation;
			}
			else if (species.size() <= 2)
			{
				speciesThreshold -= threshholdPerturbation;
			}

			boolean addedMember = false;

			//Speciate the new population
			for (int genomeIndex = 0; genomeIndex < population.size(); genomeIndex++)
			{
				for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
				{
					double compatibilityScore = population.get(genomeIndex).getCompatibilityScore(species.get(speciesIndex).getBestMember());

					if (compatibilityScore < speciesThreshold)
					{
						//Add member to the species
						species.get(speciesIndex).addMember(population.get(genomeIndex));
						//Set the variable to let the function know a member was added
						addedMember = true;
						//Break from the loop once a member has been added
						break;
					}
				}

				//If the member wasnt added to any species then we need to create a new species
				if (!addedMember)
				{
					//Create the new species
					Species speciesToAdd = new Species(++speciesID, population.get(genomeIndex));

					//Add the new species
					species.add(speciesToAdd);
				}

				//Set the added member to false for the next loop
				addedMember = false;
			}

			//Set the adjusted fitnesses of each species
			for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
			{
				species.get(speciesIndex).setAdjustedFitness();
			}

			//Reset the total adjusted fitness
			totalAdjustedFitness = 0;

			//Find the total adjusted fitness of the population
			for (int genomeIndex = 0; genomeIndex < populationSize; genomeIndex++)
			{
				totalAdjustedFitness += population.get(genomeIndex).getAdjustedFitness();
			}

			//Calculate the average adjusted fitness
			double avgAdjustedFitness = totalAdjustedFitness / populationSize;

			double total = 0;

			//Calculate spawn levels
			for (int genomeIndex = 0; genomeIndex < populationSize; genomeIndex++)
			{
				double amountToSpawn = population.get(genomeIndex).getAdjustedFitness() / avgAdjustedFitness;

				population.get(genomeIndex).setNumSpawns(amountToSpawn);

				total += amountToSpawn;
			}

			System.out.println("Total: " + total);

			//Set species spawn levels
			for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
			{
				species.get(speciesIndex).determineSpawnLevels();
			}

			System.out.println("Overall best fitness " + bestFitness + "\n");

			//Loop through each species and spawn genomes from each species
			for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
			{
				//Check to see if we still need to spawn children
				//if (numChildrenSpawned < numberToSpawn)
				//{
					//Debug function
					if (debug)
					{
						System.out.println("Number of spawns: " + species.get(speciesIndex).getNumSpawns());
					}

					//Creates the number of children necassary for the species
					for (double i = 0.0; i < species.get(speciesIndex).getNumSpawns(); i++)
					{	
						//Debug function
						if (debug)
						{
							System.out.println ("Creating genome: " + (genomeID + 1));
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

							toCopy = species.get(speciesIndex).getBestMember();

							//toCopy.printFitness();
							System.out.println("Best Member's Fitness " + toCopy.determineFitness() + "\n");
							System.out.println("SpeciesID: " + species.get(speciesIndex).getSpeciesID() + " numSpawns: " + species.get(speciesIndex).getNumSpawns());
							//System.out.println(species.getBestMember(speciesID));

							//Use elitism and always take the best member from the species
							child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
						}
						else if (i==1.0)
						{
							toCopy = species.get(speciesIndex).getBestMember();

							//Use elitism and always take the best member from the species and mutate it
							child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
							
							child.addNeuron(0.05, innovations, 20);
							child.addLink(0.05, 0.0, innovations, 10, 20);
							child.changeBiasWeight(0.2);

							child.mutateNeuronWeights();
							child.mutateLinkWeights();
						}
						else
						{
							//If the size of the species is only 1 then we can't breed
							if (species.get(speciesIndex).getNumMembers() == 1)
							{
								//Grab a member from the species
								toCopy = species.get(speciesIndex).getMember();

								child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());

								//Debug function
								if (debug)
								{
									System.out.println("Grabbing the sole survivor!");
								}
							} 
							else
							{
								//Grab a random member to breed with or mutate
								Genome mom = species.get(speciesIndex).getMember();

								//Debug function
								if (debug)
								{
									System.out.println("Grabbing mom, ID: " + mom.getID());
								}

								//Decide whether to do crossover or not based on the crossover rate
								if (random.nextDouble() < crossoverRate)
								{
									//Grab a random genome to breed with
									Genome dad = species.get(speciesIndex).getMember();
									
									//Debug function
									if (debug)
									{
										System.out.println("Grabbing dad, ID: " + dad.getID());
									}
									
									//Represents th loop control variable
									int 	lcv = 10;

									//If the dad is the same as the mom then loop and try to find a
									//new dad. Only do this as many times the lcv is initialized to.
									while (dad.getID() == mom.getID() && lcv > 0)
									{
										//Debug function
										if (debug)
										{
											System.out.println("Dad is the same as mom");
										}

										//Grab a random genome to breed with
										dad = species.get(speciesIndex).getMember();
										
										//Decrement the lcv
										lcv--;
									}

									//If the dad genome does not equal the mom genome the breed
									if (dad.getID() != mom.getID())
									{
										//Debug function
										if (debug)
										{
											System.out.println("Breeding...");
										}
										/**
										FIX
										*/

										//Breed the mom with the dad
										child = mom.crossover(dad, innovations);
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
										toCopy = mom;
										child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());

									}
								}
								toCopy = mom;
								child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
								//Do we want to set a limit on the number of nuerons?
							}

							child.addNeuron(0.05, innovations, 20);
							child.addLink(0.05, 0.0, innovations, 10, 20);
							//child.changeNeuronType(0.1, 0.7);
							child.changeBiasWeight(0.2);
							child.mutateInputLink(0.8);
							//child.mutateinputNeuron(0.8, numInputNeurons);

							child.mutateNeuronWeights();
							child.mutateLinkWeights();
						}	
						
						//Set the id of the child to the next genome ID
						child.setID(++genomeID);

						//Debug function
						if (debug)
						{
							System.out.println("Adding member to new population");
						}
						
						//Add the child to the new population
						newPopulation.add(new Genome(child.getID(), child.getNeurons(), child.getLinks(), child.getNumInputs(), child.getNumOutputs()));
					}
				//}
			}
			//Fancy ass math to determine if we met the required amount of children
			//??

			//Transfer the next generation
			population.clear();

			for (int i = 0; i < newPopulation.size(); i++)
			{
				population.add(new Genome(newPopulation.get(i).getID(), newPopulation.get(i).getNeurons(), newPopulation.get(i).getLinks(), newPopulation.get(i).getNumInputs(), newPopulation.get(i).getNumOutputs()));
			}

			newPopulation.clear();

			for (int i = 0; i < population.size(); i++)
			{
				//System.out.println(population.get(i));
			}
		}

		//Initialize the object writers
		ObjectOutputStream populationWriter = null;
		ObjectOutputStream speciesWriter = null;
		ObjectOutputStream parametersWriter = null;

		//Try to write to the files
		try
		{
			//Initialize the writers and their files as well
			System.out.println("\tCreating a file and saving the parameters");
			populationWriter = new ObjectOutputStream(new FileOutputStream("population.txt"));
			speciesWriter = new ObjectOutputStream(new FileOutputStream("species.txt"));
			parametersWriter = new ObjectOutputStream(new FileOutputStream("parameters.txt"));
			
			//Write out all the data
			populationWriter.writeObject(population);
			parametersWriter.writeObject(genomeID);
			parametersWriter.writeObject(theChosenOne);
			parametersWriter.writeObject(innovations);
			speciesWriter.writeObject(species);
			parametersWriter.writeObject(speciesID);
			parametersWriter.writeObject(bestFitness);

			//Close the writers files
			if(populationWriter != null)
				populationWriter.close();
			if(speciesWriter != null)
				speciesWriter.close();
			if(parametersWriter != null)
				parametersWriter.close();
		}
		catch(IOException io)
		{
			System.out.println("\tError!");
			io.printStackTrace();
			System.err.println(io.toString());
			System.exit(1);
		}
		finally
		{
			System.out.println("\tSuccessfully wrote to the file");
		}
	}
}