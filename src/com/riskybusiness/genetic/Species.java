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

public class Species implements Serializable
{

	//Represents the item used to create pseudo-random numbers
	private Random 							random 				= new Random();

	private	ArrayList<Genome> 				speciesPop 			= new ArrayList<Genome>();

	private ArrayList<ArrayList<Genome>>	myPopulation		= new ArrayList<ArrayList<Genome>>();

	private ArrayList<Double>				compatibilityRow	= new ArrayList<Double>();

	private ArrayList<ArrayList<Double>>	compatibilityTable	= new ArrayList<ArrayList<Double>>();

	private double 							speciesThreshold	= 0.13;

	private static final long serialVersionUID = -4268206798591932773L;

	// public Species(ArrayList<Genome> population)
	// {

	// 	//Clear the old species and add the new population
	// 	myPopulation.clear();
	// 	myPopulation.add(population);

	// 	//Determine the adjusted fitness
	// 	//For now adjusted fitness is simply fitness
	// 	for (int i = 0; i < this.myPopulation.size(); i++)
	// 	{
	// 		for (int j = 0; j < this.myPopulation.get(i).size(); j++)
	// 		{
	// 			myPopulation.get(i).get(j).setAdjustedFitness(myPopulation.get(i).get(j).getFitness());
	// 		}
	// 	}
	// }

	public Species(ArrayList<Genome> toSpeciate)
	{
		myPopulation.clear();

		//Represents how many genomes are left to be speciated
		int numLeft = toSpeciate.size();

		//Represents the index of the first genome to be added to the next species
		int nextIndex = 0;

		//Represents whether the next index has been set as it should only be set once per species
		boolean nextIndexSet = true;

		//
		boolean[] speciated;

		speciated = new boolean[toSpeciate.size()];

		for (int i = 0; i < toSpeciate.size(); i++)
		{
			speciated[i] = false;
		}

		for (int speciesID = 0; numLeft > 0; speciesID++)
		{
			//System.out.println(numLeft);
			//System.out.println("Making a new species!");
			myPopulation.add(new ArrayList<Genome>());
			myPopulation.get(speciesID).add(toSpeciate.get(nextIndex));
			speciated[nextIndex] = true;
			numLeft--;
			nextIndexSet = false;

			for(int genomeIndex = 0; genomeIndex < toSpeciate.size() - 1; genomeIndex++)
			{
				double compatibility = toSpeciate.get(genomeIndex).getCompatibilityScore(getBestMember(speciesID));

				if (compatibility <= speciesThreshold && !speciated[genomeIndex])
				{
					myPopulation.get(speciesID).add(toSpeciate.get(genomeIndex));
					numLeft--;
					speciated[genomeIndex] = true;
				}
				else if(!nextIndexSet && !speciated[genomeIndex])
				{
					nextIndexSet = true;
					nextIndex = genomeIndex;
				}
			}
		}
	}

	//Returns the number of species
	public int getNumSpecies()
	{
		return this.myPopulation.size();
	}

	//Returns the number of children the given species is supposed to spawn
	public double getNumSpawns(int speciesID)
	{
		//Represents the number of children to spawn from the given speciesID
		double numToSpawn = 0;

		//Loop through the genomes in the given species and find the amount each genome
		//is supposed to spawn and add it to the total for the species
		for (int genomeID = 0; genomeID < myPopulation.get(speciesID).size(); genomeID++)
		{
			numToSpawn += myPopulation.get(speciesID).get(genomeID).getNumSpawns();
		}

		//Return
		return numToSpawn;
	}

	//Returns the size of the given species
	public int getSize(int speciesID)
	{
		//Return
		return this.myPopulation.get(speciesID).size();
	}

	//Returns the fittest/best member if the given species
	public Genome getBestMember(int speciesID)
	{
		//Represents the ID of the fittest genome
		int fittestGenomeIndex = 0;

		//Loop through the genomes and compare the current genome to the fittest and 
		//determine the fittest individual
		for (int genomeIndex = 1; genomeIndex < myPopulation.get(speciesID).size(); genomeIndex++)
		{

			myPopulation.get(speciesID).get(genomeIndex).createPhenotype();

			if (myPopulation.get(speciesID).get(fittestGenomeIndex).determineFitness() < 
				   myPopulation.get(speciesID).get(genomeIndex).determineFitness())
			{
				fittestGenomeIndex = genomeIndex;
			}
		}

		//Return
		return myPopulation.get(speciesID).get(fittestGenomeIndex);
	}

	//Figure out a way to return better people
	//Returns a random member from the population
	public Genome getMember(int speciesID)
	{
		ArrayList<Integer> 	speciesToCompete 	= new ArrayList<Integer>();

		int 				bestFitnessID;

		//This uses a tournament selection which accomplishes our goal of choosing genomes
		//with higher fitness at a higher rate and choosing those with lower fitness at a 
		//lower rate
  
		//May need to lower value to 5 or some computer value such as 10% of the species.

		for (int i = 0; i < 3; i++)
		{
			speciesToCompete.add(new Integer(random.nextInt((myPopulation.get(speciesID).size() - 1))));
		}

		bestFitnessID = speciesToCompete.get(0);

		for (int i = 1; i < 3; i++)
		{
			if (myPopulation.get(speciesID).get(speciesToCompete.get(i)).getFitness() > myPopulation.get(speciesID).get(bestFitnessID).getFitness())
			{
				bestFitnessID = speciesToCompete.get(i);
			}
		}

		return myPopulation.get(speciesID).get(bestFitnessID);
		//return myPopulation.get(speciesID).get(random.nextInt((myPopulation.get(speciesID).size() - 1)));
	}

	public int getNumMembers(int speciesID)
	{
		return myPopulation.get(speciesID).size();
	}

	@Override
	public String toString()
	{
		//The string to return
        String toReturn = "";

        for (int i = 0; i < myPopulation.size(); i++)
        {
        	toReturn += "Species ID: " + (i + 1) + "\n";
        	for (int j = 0; j < myPopulation.get(i).size(); j++)
        	{
        		toReturn += myPopulation.get(i).get(j).toString();
        	}
        	toReturn += "\n";
        }

        return toReturn;
	}
}
