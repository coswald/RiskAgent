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
import com.riskybusiness.genetic.Genome;

import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;

/**
 * <p>&nbsp&nbsp&nbsp&nbsp&nbspDetermines a cluster of similar
 * {@code Genome}s, effectivly creating new {@code Species} within
 * a given population. This class will be used by the main method
 * of this application to assert new species in order to save the
 * newest outliers within a population. This is because new 
 * {@code Genome}s may have good topologies, but contain bad
 * weights initially.</p>
 * @author Kaleb Luse
 * @author Coved W Oswald
 * @author Wetson Miller
 * @version 1.0
 * @since 1.6
 */
public class Species implements Serializable
{
	/**********
	 * Params *
	 **********/
	
	//Represents the reward for being younger
	private double				youthReward 		= 1.2;

	//Represents the penalty for being old
	private double				oldAgePenalty		= 0.8;

	//Represents the age at which a species is old
	private int 				oldAge 				= 35;

	//Rpeprsents the age at which a species is still young
	private int 				youngAge			= 16;

	/*********************
	 * Species variables *
	 *********************/

	//Represents the species ID
	private int 				speciesID 			= 0;

	//Represents the generation 
	private int 				generation 			= 0;

	//Represents the members of the species
	private ArrayList<Genome> 	species 			= new ArrayList<Genome>();

	//Represents the best member of the species
	private Genome 				alphaGene 			= new Genome();

	//Represents the fitness of the best member
	private double 				alphaFitness		= 0;
	
	//Represents the number of generations without improvement
	private int 				gensNoImprovement 	= 0;

	//Represents the number of children to spawn from species
	private double 				numSpawns 			= 0.0;

	//Represents the package used to supply psuedo-random numbers
	private Random 				random 				= new Random();


	//Represents the ID to make species saveable to a text file
	private static final long serialVersionUID = -4268206798591932773L;

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspCreates a new species with the given
	 * ID and the initial {@code Genome} that started the species.</p>
	 * @param specID The species ID.
	 * @param genome The initial individual to start the species.
	 */
	public Species (int specID, Genome genome)
	{
		//Initialize the speciesID
		speciesID = specID;

		//Initialize the generation number
		generation = 1;

		//Add the newest member to the species
		species.add(genome);

		//Make the newest member the alpha gene
		alphaGene = genome;

		//Determine the fitness of the newest gene and make it the alpha fitness
		alphaFitness = genome.determineFitness();

		//Initilize generations without imporvement
		gensNoImprovement = 0;
	}
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspAdds a new member to the
	 * {@code Species}.</p>
	 * @param genome The new {@code Genome} to add.
	 */
	public void addMember(Genome genome)
	{
		//Represents the fitness of the new member
		double competitorFitness;

		//Add the new member to the species
		species.add(genome);

		//Determine the fitness of the new member
		competitorFitness = genome.determineFitness();

		//If the new member has a higher fitness than the alpha gene
		//then we should do some things
		if(competitorFitness > alphaFitness)
		{
			//Change the alpha fitness to the new members fitness
			alphaFitness = competitorFitness;

			//Change the alpha gene to the new member
			alphaGene    = genome;

			//Update generations with new improvement
			gensNoImprovement = 0;
		}
	}

	//Creates a new generation of species
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspGenerates a new generation
	 * of a species.</p>
	 */
	public void newGeneration()
	{
		//Clear the old species
		species.clear();

		//Update the generation count
		generation++;

		//Update the generations without improvement
		gensNoImprovement++;

		//Reset the number of spawns
		numSpawns = 0.0;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the training parameters
	 * of the species.</p>
	 * @param reward The new reward.
	 * @param penalty The new penalty.
	 * @param young The new age at which the young reward is
	 * 			received.
	 * @param old The new age at chich the old penalty is
	 * 			recieved.
	 */
	public void setParams(double reward, double penalty, int young, int old)
	{
		youthReward = reward;
		oldAgePenalty = penalty;
		youngAge = young;
		oldAge = old;
	}

	//Returns the number of generations without improvement
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the number
	 * of generations without improvement.</p>
	 * @return The number of generation without improvement.
	 */
	public int gensWithNoImprovement()
	{
		return this.gensNoImprovement;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the species ID.</p>
	 * @return The species ID.
	 */
	public int getSpeciesID()
	{
		return this.speciesID;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the number of
	 * members in the species.</p>
	 * @return The number of members in the species.
	 */
	public int getNumMembers()
	{
		return this.species.size();
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the number of
	 * children the given species is supposed to spawn.</p>
	 * @return The number of given spawns.
	 */
	public double getNumSpawns()
	{
		return numSpawns;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the "fittest"
	 * of best member of this species.</p>
	 * @return The best member of the species.
	 */
	public Genome getBestMember()
	{
		return this.alphaGene;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the fitness
	 * of the best member of the species.</p>
	 * @return The best fitness within the species.
	 */
	public double getBestFitness()
	{
		return this.alphaFitness;
	}

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspDetermines the number
	 * of spawns this species receives.</p>
	 */
	public void determineSpawnLevels()
	{
		//Loop through the genomes in the given species and find the amount each genome
		//is supposed to spawn and add it to the total for the species
		for (int genomeID = 0; genomeID < species.size(); genomeID++)
		{
			numSpawns += species.get(genomeID).getNumSpawns();
		}
	}

	//Figure out a way to return better people
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns a random 
	 * member from the population.</p>
	 * @return A random member from the population.
	 */
	public Genome getMember()
	{
		//If there is only one member in the species return the member
		if (species.size() == 1)
		{
			return species.get(0);
		}

		//Represents the list of ID's of the competitors
		ArrayList<Integer> 	speciesToCompete 	= new ArrayList<Integer>();

		//Represents the ID of the best member found so far
		int 				bestFitnessID 		= 0;

		//Represents the best fitness found so far
		double 				bestFitness 		= 0.0;

		//Represents the fitness of the competitor
		double 				competitorFitness	= 0.0;

		//This uses a tournament selection which accomplishes our goal of choosing genomes
		//with higher fitness at a higher rate and choosing those with lower fitness at a 
		//lower rate

		//Grab the random competitors
		for (int i = 0; i < 3; i++)
		{
			//Grab three random members to compete
			speciesToCompete.add(new Integer(random.nextInt((species.size() - 1))));
		}

		//Assign the first species as the first competitor
		bestFitnessID = speciesToCompete.get(0);

		//Start the rounds of the tourney
		for (int i = 1; i < 3; i++)
		{
			//Calcualte the fitenss of the next competitor
			competitorFitness = species.get(speciesToCompete.get(i)).determineFitness();

			//Compare the best member to the competitor
			if (competitorFitness > bestFitness)
			{
				//If the competitor was better, set his ID to the best 
				bestFitnessID = speciesToCompete.get(i);

				//Also set the bestFitness to the winner
				bestFitness = competitorFitness;
			}
		}

		//Return the winner of the tourney
		return species.get(bestFitnessID);
	}
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspSets the adjusted
	 * fitness for this species. The adjusted fitness
	 * describes how many spawns this species will
	 * receive based on the other species adjusted
	 * fitness. This is applied to every {@code Genome}
	 * in the species.</p>
	 */
	public void setAdjustedFitness()
	{
		//Loop through the species and calculate the species adjusted fitness
		for (int speciesIndex = 0; speciesIndex < species.size(); speciesIndex++)
		{
			//Represents the fitness of the genome taken from the species
			double fitness = species.get(speciesIndex).determineFitness();

			//If the species generation is greater than what we consider old age then penalize the genome
			if (generation > oldAge)
			{
				fitness *= oldAgePenalty;
			}
			//Else if its still considered young reward it
			else if (generation < youngAge)
			{
				fitness *= youthReward;
			}

			//Determine the final adjusted fitenss and then set the genomes adjusted fitness
			double adjustedFitness = fitness / species.size();
			species.get(speciesIndex).setAdjustedFitness(adjustedFitness);
		}
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public String toString()
	{
		//The string to return
        	String toReturn = "";
		
        	//Add the proper elements to the string
        	for (int i = 0; i < species.size(); i++)
        	{
        		toReturn += "Species ID: " + speciesID + "\n";
        		toReturn += species.get(i).toString();
        		toReturn += "\n";
        	}
	
	        //Return the string
	        return toReturn;
	}
}
