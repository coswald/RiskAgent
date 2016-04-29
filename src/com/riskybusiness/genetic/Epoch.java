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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.lang.InterruptedException;
import java.lang.Object;
import java.lang.Runnable;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class Epoch extends Object implements Runnable, Serializable
{
	private static Random random = new Random(); //doesn't need to be declared transient apparently...
	
	private ArrayList<NeuronGene> neuronGenes;
	private ArrayList<LinkGene> linkGenes;
	private ArrayList<Species> species;
	private Genome[] population;
	private int speciesID; //can't be declared static due to the transient property of static...
	private int genomeID; //can't be declared static due to transient property of static...
	private InnovationDB innovations;
	
	private double bestFitness;
	private double crossoverRate;
	private int extinctionLimit;
	private int maxNumSpecies;
	private double speciesThreshold;
	private double thresholdPerturbation;
	private Genome theChosenOne;
	private int generation;
	
	private transient int numGenerations = 500; //Represents the number of generations to run
	private transient boolean advancedNetCreation = false; //Represents whether the user has chosen to do an advanced network creation
	private transient double totalAdjustedFitness = 0.0; //Represents the total adjusted fitness of the entire population
	private transient double youthReward = 1.2; //Represents the reward for being younger
	private transient double oldAgePenalty = 0.8; //Represents the penalty for being old
	private transient int oldAge = 35; //Represents the age at which a species is old
	private transient int youngAge = 16; //Represents the age at which a species is still young
	private transient int backupGen = 12; //Represents how often the genome is saved to file

	//These are the mutate parameters that determine which mutator operator is used
	private transient boolean addNeuron = true;
	private transient boolean addLink = true;
	private transient boolean addLoopedLink = false;
	private transient boolean changeNeuronType = false;
	private transient boolean mutateBiasWeight = true;
	private transient boolean mutateInputLink = true;
	private transient boolean mutateInputNeuron = false;
	private transient boolean mutateNeuron = true;
	private transient boolean mutateLink = true;

	//These are all the mutator params
	private transient double addNeuronRate = 0.05;
	private transient int maxCheckForNeuron = 20;
	private transient double addLinkRate = 0.05;
	private transient int maxCheckforLink = 20;
	private transient double addLoopedLinkRate = 0.00;
	private transient int maxCheckForLooped = 20;
	private transient double chanceOfTypeChange = 0.10;
	private transient double chanceOfSigmoid = 0.5;
	private transient double biasLinkMutateRate = 0.2;
	private transient double inLinkMutateRate = 0.5;
	private transient double inNeuronMutateRate = 0.1;
	
	private transient int numInputNeurons = 13;
	private transient int numOutputNeurons = 1;
	private transient int numHiddenLayers = 1;
	private transient int[] hiddenLayers = new int[] {13,1};
	
	private transient boolean running = false;
	private transient boolean paused = false;
	
	public Epoch(int populationSize, int extinctionLimit, int maxNumSpecies, double crossoverRate, double speciesThreshold, double thresholdPerturbation)
	{
		this.extinctionLimit = extinctionLimit;
		this.maxNumSpecies = maxNumSpecies;
		this.crossoverRate = crossoverRate;
		this.speciesThreshold = speciesThreshold;
		this.thresholdPerturbation = thresholdPerturbation;
		this.bestFitness = 0;
		this.theChosenOne = new Genome();
		
		this.neuronGenes = new ArrayList<NeuronGene>();
		this.linkGenes = new ArrayList<LinkGene>();
		this.species = new ArrayList<Species>();
		this.population = new Genome[populationSize];
		this.genomeID = 0;
		this.speciesID = 0;
		this.generation = 0;
		this.innovations = new InnovationDB();
	}
	
	public void mutateFromOther(Epoch other)
	{
		this.neuronGenes = other.neuronGenes;
		this.linkGenes = other.linkGenes;
		this.species = other.species;
		this.population = other.population;
		this.speciesID = other.speciesID;
		this.genomeID = other.genomeID;
		this.innovations = other.innovations;
		this.generation = other.generation;
		
		this.bestFitness = other.bestFitness;
		this.crossoverRate = other.crossoverRate;
		this.extinctionLimit = other.extinctionLimit;
		this.maxNumSpecies = other.maxNumSpecies;
		this.speciesThreshold = other.speciesThreshold;
		this.thresholdPerturbation = other.thresholdPerturbation;
		this.theChosenOne = other.theChosenOne;
	}
	
	public Epoch dontChangeParams(Epoch e)
	{
		e.numGenerations = this.numGenerations;
		e.advancedNetCreation = this.advancedNetCreation;
		e.totalAdjustedFitness = this.totalAdjustedFitness;
		e.youthReward = this.youthReward;
		e.oldAgePenalty = this.oldAgePenalty;
		e.oldAge = this.oldAge;
		e.youngAge = this.youngAge;
		e.backupGen = this.backupGen;

		e.addNeuron = this.addNeuron;
		e.addLink = this.addLink;
		e.addLoopedLink = this.addLoopedLink;
		e.changeNeuronType = this.changeNeuronType;
		e.mutateBiasWeight = this.mutateBiasWeight;
		e.mutateInputLink = this.mutateInputLink;
		e.mutateInputNeuron = this.mutateInputNeuron;
		e.mutateNeuron = this.mutateNeuron;
		e.mutateLink = this.mutateLink;

		e.addNeuronRate = this.addNeuronRate;
		e.maxCheckForNeuron = this.maxCheckForNeuron;
		e.addLinkRate = this.addLinkRate;
		e.maxCheckforLink = this.maxCheckforLink;
		e.addLoopedLinkRate = this.addLoopedLinkRate;
		e.maxCheckForLooped = this.maxCheckForLooped;
		e.chanceOfTypeChange = this.chanceOfTypeChange;
		e.chanceOfSigmoid = this.chanceOfSigmoid;
		e.biasLinkMutateRate = this.biasLinkMutateRate;
		e.inLinkMutateRate = this.inLinkMutateRate;
		e.inNeuronMutateRate = this.inNeuronMutateRate;
		
		e.numInputNeurons = this.numInputNeurons;
		e.numOutputNeurons = this.numOutputNeurons;
		e.numHiddenLayers = this.numHiddenLayers;
		e.hiddenLayers = this.hiddenLayers;
			
		return e;
	}
	
	
	public void createPopulation(double chanceOfLink, boolean fullLink, int[] layers)
	{
		System.out.println("Creating a new population...");
		int[] summationNeuronsInLayer = new int[layers.length + 2];
		
		//Initialize number of neurons
		summationNeuronsInLayer[0] = 0;
		summationNeuronsInLayer[1] = layers[0];

		for (int i = 0; i < layers.length - 1; i++)
		{
			summationNeuronsInLayer[i + 2] = summationNeuronsInLayer[i + 1] + layers[i];
		}
		summationNeuronsInLayer[layers.length + 1] = layers[layers.length - 1] + summationNeuronsInLayer[layers.length];

		//Create the initial genomes
		for (int lcv = 0; lcv < population.length; lcv++)
		{
			//Reset the genes for the new population
			neuronGenes.clear();
			linkGenes.clear();
			int curNeuronID = 0;
			int curLinkID = 0;

			//The next three loops will create the neurons
			//Currently, there is no need to seperate into three loops, but I am working on implementing different neuron types
			double dweight = 0.0D;
			for (int i = 0; i < layers[0]; i++)
			{
				innovations.addInnovation(InnovationType.NEW_NEURON, -1, -1, ++curNeuronID);
				neuronGenes.add(new NeuronGene(curNeuronID, "Sigmoid", "Input", random.nextDouble(), 1)); 
			}
			for (int i = 0; i < layers.length - 1; i++)
			{
				for (int j = summationNeuronsInLayer[i + 1]; j < summationNeuronsInLayer[i + 2]; j++)
				{
					innovations.addInnovation(InnovationType.NEW_NEURON, -1, -1, ++curNeuronID);
					neuronGenes.add(new NeuronGene(curNeuronID, "Sigmoid", "Hidden", random.nextDouble(), (i + 2)));
				}
			}
			for (int i = 0; i < layers[layers.length - 1]; i++)
			{
				innovations.addInnovation(InnovationType.NEW_NEURON, -1, -1, ++curNeuronID);
				neuronGenes.add(new NeuronGene(curNeuronID, "Sigmoid", "Output", random.nextDouble(), (layers.length + 1)));
			}
			
			//Create the link genes
			//This behemoth of a triple nested loop inside a loop simply goes creates the links between all the neurons
			//The first loop increments the active layer
			int linksCreated = 0;
			for (int i = 0; i < layers.length; i++)
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
							dweight = random.nextDouble();
							//Add the innovation
							if (neuronGenes.get(j).getID() <= layers[0])
							{
								if (neuronGenes.get(j).getID() == (neuronGenes.get(k).getID() - layers[0]))
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
				}
			}

			population[lcv] = new Genome(++genomeID, neuronGenes, linkGenes, layers[0], layers[layers.length - 1]);
		}
		System.out.println("The new population has been created.");
	}
	
	public void createPopulation(double chanceOfLink, boolean fullLink)
	{
		int[] layers = new int[2 + numHiddenLayers];
		layers[0] = numInputNeurons;
		layers[layers.length - 1] = numOutputNeurons;
		for(int i = 1; i < layers.length - 1; i++)
			layers[i] = hiddenLayers[i - 1];
		this.createPopulation(chanceOfLink, fullLink, layers);
	}
	
	public boolean isPaused()
	{
		return this.paused;
	}
	
	public boolean isRunning()
	{
		return this.running;
	}
	
	public void stop()
	{
		this.running = false;
	}
	
	public void switchPausedState()
	{
		this.paused = !this.paused;
	}
	
	public void setParams(String paramFile) throws RuntimeException
	{
		FileInputStream	fileObject = null;
   		try
   		{
   			fileObject 	= new FileInputStream(paramFile);
   		}
   		catch(Exception e)
   		{
   			System.err.println("Error: " + e.getMessage());
   			System.exit(1);
   		}
		
		//Represents the scanner for the params file
   		Scanner z = new Scanner(fileObject);
   		//Represents the index of the variable in the params file
   		int varIndex = 1;
		
		while(z.hasNext())
		{
			String line = z.nextLine();
			String text = line.replace(" ", "");
			if(!line.contains("#") && !line.equals(""))
			{
				text = text.split("=")[1];
				switch (varIndex)
				{
					case 1: 
						int size = Integer.parseInt(text);
						if(size <= 0)
							throw new RuntimeException("Error 100: Invalid Population size!");
						population = new Genome[size];
						break;
					case 2:
						numGenerations = Integer.parseInt(text);
						if(numGenerations <= 0)
							throw new RuntimeException("Error 99: Invalid Generation number!");
						break;
					case 3:
						backupGen = Integer.parseInt(text);
						if(backupGen <= 0)
							throw new RuntimeException("Error 98: Invalid backup number!");
						break;
					case 4: 
						break;
					case 5: 
						if (text.toLowerCase().equals("false"))
							advancedNetCreation = false;
						else if (text.toLowerCase().equals("true"))
							advancedNetCreation = true;
						else
							throw new RuntimeException("Error 101: Advance Network Creation parameter not set properly");
						break;
					case 6: 
						if (!advancedNetCreation)
						{
							numInputNeurons = Integer.parseInt(text);
							if(numInputNeurons <= 0)
								throw new RuntimeException("Error 97: Input Neuron number Invalid!");
						}
						break;
					case 7: 
						if (!advancedNetCreation)
						{
							numOutputNeurons = Integer.parseInt(text);
							if(numOutputNeurons <= 0)
								throw new RuntimeException("Error 96: Output Neuron number Invalid!");
						}
						break;
					case 8:
						if (!advancedNetCreation)
						{
							numHiddenLayers = Integer.parseInt(text);
							if(numHiddenLayers <= 0)
								throw new RuntimeException("Error 95: Hidden Neuron number Invalid!");
						}
						hiddenLayers = new int[numHiddenLayers + 1];
						break;
					case 9: 
						if (!advancedNetCreation)
						{
							hiddenLayers[0] = numInputNeurons;
							for (int i = 1; i < numHiddenLayers; i++)
								hiddenLayers[i] = Integer.parseInt(text);
						}
						break;
					case 10:
						if (advancedNetCreation)
						{
							numHiddenLayers = Integer.parseInt(text) + 1;
							hiddenLayers = new int[numHiddenLayers];
						}
						break;
					case 11:
						if (advancedNetCreation)
						{
							String[] t = text.split(",");
							for(int i = 0; i < t.length; i++)
							{
								if (i == 0)
									numInputNeurons = Integer.parseInt(t[i]);
								else if (i == 1)
									hiddenLayers[i - 1] = numInputNeurons;
								else if (i == t.length - 1)
									numOutputNeurons = Integer.parseInt(t[i]);
								else
									hiddenLayers[i - 1] = Integer.parseInt(t[i]);
							}
						}
						break;
					case 12: maxNumSpecies = Integer.parseInt(text);
						break;
					case 13: speciesThreshold = Double.parseDouble(text);
						break;
					case 14: thresholdPerturbation = Double.parseDouble(text);
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
							addNeuron = false;
						else if (text.toLowerCase().equals("true"))
							addNeuron = true;
						else
							throw new RuntimeException("Error 102: Add Neuron parameter not set properly");
						break;
					case 21: addNeuronRate = Double.parseDouble(text);
						break;
					case 22: maxCheckForNeuron = Integer.parseInt(text);
						break;
					case 23: 
						if (text.toLowerCase().equals("false"))
							addLink = false;
						else if (text.toLowerCase().equals("true"))
							addLink = true;
						else
							throw new RuntimeException("Error 103: Add Link parameter not set properly");
						break;
					case 24: addLinkRate = Double.parseDouble(text);
						break;
					case 25: maxCheckforLink = Integer.parseInt(text);
						break;
					case 26:
						if (text.toLowerCase().equals("false"))
							addLoopedLink = false;
						else if (text.toLowerCase().equals("true"))
							addLoopedLink = true;
						else
							throw new RuntimeException("Error 104: Add Looped Link parameter not set properly");
						break;
					case 27: 
						if(addLoopedLink)
							addLoopedLinkRate = Double.parseDouble(text);
						else
							addLoopedLinkRate = 0.00;
						break;
					case 28: 
						if(addLoopedLink)
							maxCheckForLooped =Integer.parseInt(text);
						else
							maxCheckForLooped = 0;
						break;
					case 29:
						if (text.toLowerCase().equals("false"))
							changeNeuronType = false;
						else if (text.toLowerCase().equals("true"))
							changeNeuronType = true;
						else
							throw new RuntimeException("Error 105: Change Neuron Type parameter not set properly");
						break;
					case 30: chanceOfTypeChange = Double.parseDouble(text);
						break;
					case 31: chanceOfSigmoid = Double.parseDouble(text);
						break;
					case 32:
						if (text.toLowerCase().equals("false"))
							mutateBiasWeight = false;
						else if (text.toLowerCase().equals("true"))
							mutateBiasWeight = true;
						else
							throw new RuntimeException("Error 106: Mutate Bias Weight parameter not set properly");
						break;
					case 33: biasLinkMutateRate = Double.parseDouble(text);
						break;						
					case 34:
						if (text.toLowerCase().equals("false"))
							mutateInputLink = false;
						else if (text.toLowerCase().equals("true"))
							mutateInputLink = true;
						else
							throw new RuntimeException("Error 107: Mutate Input Link parameter not set properly");
						break;
					case 35: inLinkMutateRate = Double.parseDouble(text);
						break;
					case 36:
						if (text.toLowerCase().equals("false"))
							mutateInputNeuron = false;
						else if (text.toLowerCase().equals("true"))
							mutateInputNeuron = true;
						else
							throw new RuntimeException("Error 108: Mutate Input Neuron parameter not set properly");
						break;
					case 37: inNeuronMutateRate = Double.parseDouble(text);
						break;
					case 38:
						if (text.toLowerCase().equals("false"))
							mutateNeuron = false;
						else if (text.toLowerCase().equals("true"))
							mutateNeuron = true;
						else
							throw new RuntimeException("Error 109: Mutate Neuron parameter not set properly");
						break;
					case 39:
						if (text.toLowerCase().equals("false"))
							mutateLink = false;
						else if (text.toLowerCase().equals("true"))
							mutateLink = true;
						else
							throw new RuntimeException("Error 110: Mutate Link parameter not set properly");
						break;
				}
				varIndex++;
			}
		}
		if (varIndex == 1)
			throw new RuntimeException("Error 111: File does not exist");
		if (varIndex != 40)
			throw new RuntimeException("Error 112: Error with file");
	}
	
	public void saveToFile(String fileName) throws IOException
	{
		//Initialize the object writer
		ObjectOutputStream epochWriter = null;

		//Try to write to the file
		File f = new File(fileName);
		if(f.exists())
			f.delete();
		//Initialize the writer with the given file
		epochWriter = new ObjectOutputStream(new FileOutputStream(f));
		
		//Write out all the data
		epochWriter.writeObject(this);

		//Close the writers files
		if(epochWriter != null)
			epochWriter.close();
	}
	
	@Override
	public void run()
	{
		this.running = true;
		boolean changedState = true;
		
		Genome toCopy = new Genome();
		Genome child = new Genome();
		double totalAdjustedFitness = 0;
		
		double competitorFitness; //represents the fitenss of the competitor.
		boolean addedMember = false; //represents whether or not a member has been added.
		double avgAdjustedFitness = 0.0D; //demonstrates the average fitness for the population.
		double compatibilityScore = 0.0D; //used to calculate compatibility score.
		double amountToSpawn = 0.0D;
		Genome dad; 
		Genome mom; //used for crossover.
		
		while(running && generation < numGenerations)
		{
			if(paused)
			{
				if(changedState)
				{
					System.err.println("Paused!");
					changedState = !changedState;
				}
				else
				{
					try
					{
						Thread.sleep(100); //needed so thread won't stop.
					}
					catch(InterruptedException ie)
					{
					}
				}
			}
			else
			{
				changedState = true;
				generation++;
				System.out.println("Generation " + generation);
				if(generation % backupGen == 0)
				{
					System.out.println("\tBacking up file...");
					try
					{
						this.saveToFile("epoch.gaif");
						System.out.println("\tFile backed up to \'epoch.gaif\'!");
					}
					catch(IOException io)
					{
						System.err.println("Error: " + io.getMessage());
					}
				}
				
				//Loop through the species and kill off species that aren't improving and 
				//create a new generation for each species
				System.out.println("\tLooping through to create and destroy species!");
				for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
				{
					//Create the new generation
					species.get(speciesIndex).newGeneration();

					//Check for extinction
					if (species.get(speciesIndex).gensWithNoImprovement() > this.extinctionLimit)
					{
						//Kill off the species
						species.remove(speciesIndex);
						//Decrement the species index to account for the loss of a species
						speciesIndex--;
					}
				}

				//Loop through each species and see if any of the alphas has surpassed the best
				//fitness seen so far
				System.out.println("\tLooping through to find the Chosen One...");
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
						this.theChosenOne = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
					}
				}
				System.out.println("\t\tChosen one's Fitness: " + bestFitness);
				//Check if we have exceeded the number of species
				System.out.println("\tChanging Species Threshold (with one \"h\")");
				if (species.size() > maxNumSpecies)
				{
					this.speciesThreshold += this.thresholdPerturbation;
				}
				else if (species.size() <= 2)
				{
					this.speciesThreshold -= this.thresholdPerturbation;
				}

				//Speciate the new population
				//Loop through all the current genomes in the population and determine the species they belong to
				System.out.println("\tSpeciating the new population...");
				for (int genomeIndex = 0; genomeIndex < population.length; genomeIndex++)
				{
					//Loop through each species and compare the genome to be speciated to the best genome in the species
					for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
					{
						//Determine the compatibility score between the current genome and the best member of the current species
						compatibilityScore = population[genomeIndex].getCompatibilityScore(species.get(speciesIndex).getBestMember());

						//If the compatibility score is lower then the species threshold then add the current genome to the current species
						if (compatibilityScore < speciesThreshold)
						{
							//Add member to the species
							species.get(speciesIndex).addMember(population[genomeIndex]);
							//Set the variable to let the function know a member was added
							addedMember = true;
							//Break from the loop once a member has been added
							break;
						}
					}

					//If the member wasn't added to any species then we need to create a new species
					if (!addedMember)
					{
						//Create the new species
						Species speciesToAdd = new Species(++this.speciesID, population[genomeIndex]);
						//Add the new species
						species.add(speciesToAdd);
					}
					//Set the added member to false for the next loop
					addedMember = false;
				}

				//Set the adjusted fitnesses of each species
				System.out.println("\tAdjusting Fitness...");
				for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
					species.get(speciesIndex).setAdjustedFitness();

				//Reset the total adjusted fitness
				totalAdjustedFitness = 0;

				//Find the total adjusted fitness of the population
				System.out.println("\tFinding Total Fitness...");
				for (int genomeIndex = 0; genomeIndex < population.length; genomeIndex++)
					totalAdjustedFitness += population[genomeIndex].getAdjustedFitness();

				//Calculate the average adjusted fitness
				avgAdjustedFitness = totalAdjustedFitness / population.length;

				//Calculate spawn levels
				System.out.println("\tCalculating Spawn Levels for Population...");
				for (int genomeIndex = 0; genomeIndex < population.length; genomeIndex++)
				{
					amountToSpawn = population[genomeIndex].getAdjustedFitness() / avgAdjustedFitness;
					population[genomeIndex].setNumSpawns(amountToSpawn);
				}

				//Set species spawn levels
				System.out.println("\tCalculating Spawn Levels for Species...");
				for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
					species.get(speciesIndex).determineSpawnLevels();
				
				//Loop through each species and spawn genomes from each species
				System.out.println("\tSpawning..." + species.size());
				int populationIndex = 0;
				for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
				{
					//Creates the number of children necassary for the species
					//System.out.println("\t\tCreating Children..." + speciesIndex);
					for (double i = 0.0; i < species.get(speciesIndex).getNumSpawns(); i++)
					{
						//If this is our first pass then the first thing we want to do
						//is grab the best member of the species and pass them along
						if (i == 0.0D)
						{
							toCopy = species.get(speciesIndex).getBestMember();
							//Use elitism and always take the best member from the species
							child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
						}
						else if (i == 1.0D)
						{
							toCopy = species.get(speciesIndex).getBestMember();

							//Use elitism and always take the best member from the species and mutate it
							child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
							
							//Mutate the genome dependent on pre-set parameters
							if(addNeuron)
								child.addNeuron(addNeuronRate, innovations, maxCheckForNeuron);
							if(addLink)
								child.addLink(addLinkRate, addLoopedLinkRate, innovations, maxCheckForLooped, maxCheckforLink);
							if(changeNeuronType)
								child.changeNeuronType(chanceOfTypeChange, chanceOfSigmoid);
							if(mutateBiasWeight)
								child.changeBiasWeight(biasLinkMutateRate);
							if(mutateInputLink)
								child.mutateInputLink(inLinkMutateRate);
							if(mutateInputNeuron)
							{
								//child.mutateInputNeuron(inNeuronMutateRate);
							}
							if(mutateNeuron)
								child.mutateNeuronWeights();
							if(mutateLink)
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
							} 
							else
							{
								//Grab a random member to breed with or mutate
								mom = species.get(speciesIndex).getMember();
								//Decide whether to do crossover or not based on the crossover rate
								if (random.nextDouble() < crossoverRate)
								{
									//Grab a random genome to breed with
									dad = species.get(speciesIndex).getMember();
									
									int lcv = 10;

									//If the dad is the same as the mom then loop and try to find a
									//new dad.
									while (dad.getID() == mom.getID() && lcv > 0)
									{
										dad = species.get(speciesIndex).getMember(); //Grab a random genome to breed with
										lcv--;
									}

									//Breed the mom with the dad
									child = mom.crossover(dad, innovations);
								}
								toCopy = mom;
								child = new Genome(toCopy.getID(), toCopy.getNeurons(), toCopy.getLinks(), toCopy.getNumInputs(), toCopy.getNumOutputs());
							}
							
							if(addNeuron)
								child.addNeuron(addNeuronRate, innovations, maxCheckForNeuron);
							if(addLink)
								child.addLink(addLinkRate, addLoopedLinkRate, innovations, maxCheckForLooped, maxCheckforLink);
							if(changeNeuronType)
								child.changeNeuronType(chanceOfTypeChange, chanceOfSigmoid);
							if(mutateBiasWeight)
								child.changeBiasWeight(biasLinkMutateRate);
							if(mutateInputLink)
								child.mutateInputLink(inLinkMutateRate);
							if(mutateInputNeuron)
							{
								//child.mutateInputNeuron(inNeuronMutateRate);
							}
							if(mutateNeuron)
								child.mutateNeuronWeights();
							if(mutateLink)
								child.mutateLinkWeights();
							
							//Set the id of the child to the next genome ID
							child.setID(++this.genomeID);
							//Add the child to the new population
							population[populationIndex++] = new Genome(child.getID(), child.getNeurons(), child.getLinks(), child.getNumInputs(), child.getNumOutputs());
						}
					}
				}
				System.out.println("\tDone!");
			}
		}
		this.running = false;
	}
}