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

@SuppressWarnings("unchecked")
public class Epoch implements Runnable
{

	public void run()
	{
		Scanner z = new Scanner(System.in);

		while(true)
		{
			String s = z.nextLine();
			break;
		}
		//save stuff too... but maybe not.
		System.out.println("Stopped!");
		System.exit(0);
	}

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
		int[]	hiddenLayers 			= {numInputNeurons, 1};
		//Represents the number of neurons in the genome up to the given index
		int[] 	summationNeuronsInLayer	= new int[numHiddenLayers + 3];
		//Represents the current neuron ID 
		int 	curNeuronID 			= 0;
		//Represents the current link ID
		int 	curLinkID 				= 0;
		//Represents the maximum number of species allowed
		int 	maxNumSpecies 			= 6;
		//Represents the species threshold which determines whether a genome will be accepted into a species
		double 	speciesThreshold 		= 0.15;
		//Represents how much we should increase the compatibility threshhold should we exceed the number of species
		double 	threshholdPerturbation 	= 0.02;
		//Represents the number of generations to run
		int 	numGenerations 			= 0;
		//Represents whether the user has chosen to do an advanced network creation
		boolean advancedNetCreation 	= false;
		//Represents the rate at which a genomes breed
		double 				crossoverRate 		= 0.3;
		//Represents the total adjusted fitness of the entire population
		double 				totalAdjustedFitness = 0.0;
		//Represents the reward for being younger
		double				youthReward 		= 1.2;
		//Represents the penalty for being old
		double				oldAgePenalty		= 0.8;
		//Represents the age at which a species is old
		int 				oldAge 				= 35;
		//Rpeprsents the age at which a species is still young
		int 				youngAge			= 16;

		//Represents how often the genome is saved to file
		int backupGen = 0;

		//These are the mutate parameters that determine which mutator operator is used
		boolean addNeuron			= true;
		boolean	addLink				= true;
		boolean	addLoopedLink		= false;
		boolean changeNeuronType 	= false;
		boolean mutateBiasWeight	= true;
		boolean mutateInputLink 	= true;
		boolean mutateInputNeuron 	= false;
		boolean mutateNeuron 		= true;
		boolean mutateLink 			= true;

		//These are all the mutator params
		double 	addNeuronRate 		= 0.05;
		int    	maxCheckForNeuron 	= 20;
		double  addLinkRate 		= 0.05;
		int 	maxCheckforLink		= 20;
		double 	addLoopedLinkRate 	= 0.05;
		int 	maxCheckForLooped 	= 20;
		double  chanceOfTypeChange  = 0.10;
		double  chanceOfSigmoid		= 0.5;
		double  biasLinkMutateRate 	= 0.2;
		double 	inLinkMutateRate	= 0.5;
		double  inNeuronMutateRate 	= 0.1;

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
   		InnovationDB 		innovations	= new InnovationDB();	


   		//Load param file
   		//Throws File Exception: 
   		FileInputStream			fileObject = null;
   		try
   		{
   			fileObject 	= new FileInputStream("params.gapf");
   		}
   		catch(Exception e)
   		{
   			System.err.println("Error: " + e.getMessage());
   			System.exit(1);
   		}
		//Represents the scanner for the params file
   		Scanner 			z 			= new Scanner(fileObject);
   		//Represents the index of the variable in the params file
   		int 				varIndex 	= 1;
		
		/*Epoch Variables*/

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
		//Represents the genome used to make deep copies
		Genome 				toCopy 				= new Genome();
		//Represents the child of a crossover
		Genome 				child 				= new Genome();




		while(z.hasNext())
		{
			String line = z.nextLine();
			String text = line.replace(" ", "");
			if(!line.contains("#") && !line.equals(""))
			{
				text = text.split("=")[1];
				switch (varIndex) {
					case 1: populationSize = Integer.parseInt(text);
						break;
					case 2: numGenerations = Integer.parseInt(text);
						break;
					case 3: backupGen = Integer.parseInt(text);
						break;
					/**
					case 4: 
					*/
					case 5: 
						if (text.toLowerCase().equals("false"))
						{
							advancedNetCreation = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							advancedNetCreation = true;
						}
						else
						{
							throw new RuntimeException("Error 101: Advance Network Creation parameter not set properly");
						}
						break;
					case 6: 
						if (!advancedNetCreation)
						{
							numInputNeurons = Integer.parseInt(text);
						}
						break;
					case 7: 
						if (!advancedNetCreation)
						{
							numOutputNeurons = Integer.parseInt(text);
						}
						break;
					case 8:
						if (!advancedNetCreation)
						{
							numHiddenLayers = Integer.parseInt(text) + 1;
						}
						break;
					case 9: 
						if (!advancedNetCreation)
						{
							hiddenLayers[0] = numInputNeurons;
							for (int i = 1; i < numHiddenLayers; i++)
							{
								hiddenLayers[i] = Integer.parseInt(text);
							}
						}
						break;
					case 10:
						if (advancedNetCreation)
						{
							numHiddenLayers = Integer.parseInt(text);
						}
						break;
					case 11:
						if (advancedNetCreation)
						{
							String[] t = text.split(",");
							for(int i = 0; i < t.length; i++)
							{
								if (i == 0)
								{
									numInputNeurons = Integer.parseInt(t[i]);
								}
								else if (i == t.length)
								{
									numOutputNeurons = Integer.parseInt(t[i]);
								}
								else
								{
									hiddenLayers[i - 1] = Integer.parseInt(t[i]);
								}
							}
						}
						break;
					case 12: maxNumSpecies = Integer.parseInt(text);
						break;
					case 13: speciesThreshold = Double.parseDouble(text);
						break;
					case 14: threshholdPerturbation = Double.parseDouble(text);
						break;
					case 15: extinctionLimit = Integer.parseInt(text);
						break;
					case 16: youthReward = Double.parseDouble(text);
						break;
					case 17: oldAgePenalty = Double.parseDouble(text);
						break;
					case 18: oldAge = Integer.parseInt(text);
						break;
					case 19: youngAge = Integer.parseInt(text);
						break;
					case 20: 
						if (text.toLowerCase().equals("false"))
						{
							addNeuron = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							addNeuron = true;
						}
						else
						{
							throw new RuntimeException("Error 102: Add Neuron parameter not set properly");
						}
						break;
					case 21: addNeuronRate = Double.parseDouble(text);
						break;
					case 22: maxCheckForNeuron = Integer.parseInt(text);
						break;
					case 23: 
						if (text.toLowerCase().equals("false"))
						{
							addLink = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							addLink = true;
						}
						else
						{
							throw new RuntimeException("Error 103: Add Link parameter not set properly");
						}
						break;
					case 24: addLinkRate = Double.parseDouble(text);
						break;
					case 25: maxCheckforLink = Integer.parseInt(text);
						break;
					case 26:
						if (text.toLowerCase().equals("false"))
						{
							addLoopedLink = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							addLoopedLink = true;
						}
						else
						{
							throw new RuntimeException("Error 104: Add Looped Link parameter not set properly");
						}
						break;
					case 27: 
						if(addLoopedLink)
						{
							addLoopedLinkRate = Double.parseDouble(text);
						}
						else
						{
							addLoopedLinkRate = 0.00;
						}
						break;
					case 28: 
						if(addLoopedLink)
						{
							maxCheckForLooped =Integer.parseInt(text);
						}
						else
						{
							maxCheckForLooped = 0;
						}
						break;
					case 29:
						if (text.toLowerCase().equals("false"))
						{
							changeNeuronType = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							changeNeuronType = true;
						}
						else
						{
							throw new RuntimeException("Error 105: Change Neuron Type parameter not set properly");
						}
						break;
					case 30: chanceOfTypeChange = Double.parseDouble(text);
						break;
					case 31: chanceOfSigmoid = Double.parseDouble(text);
						break;
					case 32:
						if (text.toLowerCase().equals("false"))
						{
							mutateBiasWeight = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							mutateBiasWeight = true;
						}
						else
						{
							throw new RuntimeException("Error 106: Mutate Bias Weight parameter not set properly");
						}
						break;
					case 33: biasLinkMutateRate = Double.parseDouble(text);
						break;						
					case 34:
						if (text.toLowerCase().equals("false"))
						{
							mutateInputLink = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							mutateInputLink = true;
						}
						else
						{
							throw new RuntimeException("Error 107: Mutate Input Link parameter not set properly");
						}
						break;
					case 35: inLinkMutateRate = Double.parseDouble(text);
						break;
					case 36:
						if (text.toLowerCase().equals("false"))
						{
							mutateInputNeuron = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							mutateInputNeuron = true;
						}
						else
						{
							throw new RuntimeException("Error 108: Mutate Input Neuron parameter not set properly");
						}
						break;
					case 37: inNeuronMutateRate = Double.parseDouble(text);
						break;
					case 38:
						if (text.toLowerCase().equals("false"))
						{
							mutateNeuron = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							mutateNeuron = true;
						}
						else
						{
							throw new RuntimeException("Error 109: Mutate Neuron parameter not set properly");
						}
						break;
					case 39:
						if (text.toLowerCase().equals("false"))
						{
							mutateLink = false;
						}
						else if (text.toLowerCase().equals("true"))
						{
							mutateLink = true;
						}
						else
						{
							throw new RuntimeException("Error 110: Mutate Link parameter not set properly");
						}
						break;
				}
				varIndex++;
			}
		}
		if (varIndex == 1)
		{
			throw new RuntimeException("Error 111: File does not exist");
		}
		if (varIndex != 40)
		{
			throw new RuntimeException("Error 112: Error with file");
		}

   		//Validate user input and determine if they would like to load a file or start new
   		while (!(userInput.equals("L") || userInput.equals("S")))
   		{
   			System.out.println("Would you like to load[L] a file or start fresh[S]?");
   			userInput = input.nextLine();
   		}


   		if (userInput.equals("L"))
   		{
   			loadFile = true;
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
		}

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

   		Thread t = new Thread(new Epoch());
   		t.start();
		for (int generation = 1; generation <= numGenerations; generation++)
		{
			System.out.println("Generation: " + generation);

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

			//Represents whether a member has been added
			boolean addedMember = false;

			//Speciate the new population
			//Loop through all the current genomes in the population and determine the species they belong to 
			for (int genomeIndex = 0; genomeIndex < population.size(); genomeIndex++)
			{
				//Loop through each species and compare the genome to be speciated to the best genome in the species
				for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
				{
					//Determine the compatibility score between the current genome and the best member of the current species
					double compatibilityScore = population.get(genomeIndex).getCompatibilityScore(species.get(speciesIndex).getBestMember());

					//If the compatibility score is lower then the species threshold then add the current genome to the current species
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

			//Set species spawn levels
			for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
			{
				species.get(speciesIndex).determineSpawnLevels();
			}

			System.out.println("Overall best fitness " + bestFitness + "\n");

			//Loop through each species and spawn genomes from each species
			for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
			{
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
						//System.out.println("Best Member's Fitness " + toCopy.determineFitness() + "\n");
						//System.out.println("SpeciesID: " + species.get(speciesIndex).getSpeciesID() + " numSpawns: " + species.get(speciesIndex).getNumSpawns());
						//System.out.println(species.getBestMember(speciesID));

						//Use elitism and always take the best member from the species
						child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
					}
					else if (i==1.0)
					{
						toCopy = species.get(speciesIndex).getBestMember();

						//Use elitism and always take the best member from the species and mutate it
						child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
						/**
						Here
						*/
						if(addNeuron)
						{
							child.addNeuron(addNeuronRate, innovations, maxCheckForNeuron);
						}
						if(addLink)
						{
							child.addLink(addLinkRate, addLoopedLinkRate, innovations, maxCheckForLooped, maxCheckforLink);
						}
						if(changeNeuronType)
						{
							child.changeNeuronType(chanceOfTypeChange, chanceOfSigmoid);
						}
						if(mutateBiasWeight)
						{
							child.changeBiasWeight(biasLinkMutateRate);
						}
						if(mutateInputLink)
						{
							child.mutateInputLink(inLinkMutateRate);
						}
						if(mutateInputNeuron)
						{
							//child.mutateInputNeuron(inNeuronMutateRate);
						}
						if(mutateNeuron)
						{
							child.mutateNeuronWeights();
						}
						if(mutateLink)
						{
							child.mutateLinkWeights();
						}
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

							/**
							PARAMS!!!!!
							*/

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
						}

						//During a crossover the child can't calculate it's input neurons and thus needs to be set manually
						child.setNumInputs(numInputNeurons);
						child.setNumOutputs(numOutputNeurons);

						//Mutate the genome dependent on pre-set parameters
						if(addNeuron)
						{
							child.addNeuron(addNeuronRate, innovations, maxCheckForNeuron);
						}
						if(addLink)
						{
							child.addLink(addLinkRate, addLoopedLinkRate, innovations, maxCheckForLooped, maxCheckforLink);
						}
						if(changeNeuronType)
						{
							child.changeNeuronType(chanceOfTypeChange, chanceOfSigmoid);
						}
						if(mutateBiasWeight)
						{
							child.changeBiasWeight(biasLinkMutateRate);
						}
						if(mutateInputLink)
						{
							child.mutateInputLink(inLinkMutateRate);
						}
						if(mutateInputNeuron)
						{
							//child.mutateInputNeuron(inNeuronMutateRate);
						}
						if(mutateNeuron)
						{
							child.mutateNeuronWeights();
						}
						if(mutateLink)
						{
							child.mutateLinkWeights();
						}
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

			}

			//Transfer the next generation
			population.clear();

			//Add the new generation to the old population
			for (int i = 0; i < newPopulation.size(); i++)
			{
				population.add(new Genome(newPopulation.get(i).getID(), newPopulation.get(i).getNeurons(), newPopulation.get(i).getLinks(), newPopulation.get(i).getNumInputs(), newPopulation.get(i).getNumOutputs()));
			}

			//Clear the new population 
			newPopulation.clear();
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
			// File f = new File("population.txt");
			// f.delete();

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
		System.exit(0);
	}
}