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


	public Species(ArrayList<Genome> population)
	{
		//Get compatitbility of the population
		for (int i = 0; i < population.size(); i++)
		{
			for (int j = 0; j < population.size(); j++)
			{
				if (i != j)
				{
					compatibilityRow.add(j, new Double(population.get(i).getCompatibilityScore(population.get(j))));
				}
				else
				{
					compatibilityRow.add(j, new Double(1.0));
				}
			}
			compatibilityTable.add(compatibilityRow);
		}

		// for (int i = 0; i < population.size(); i++)
		// {
		// 	System.out.print("Row " + i + ":");
		// 	for (int j = 0; j < population.size(); j++)
		// 	{
		// 		if (i != j)
		// 		{
		// 			System.out.print(compatibilityTable.get(i).get(j) + " ");
		// 		}
		// 	}
		// 	System.out.println();
		// }


		//Using the compatibilty table determine the species
		// for (int i = 0; population.size(); i++)
		// {
		// 	for (int j = 0; j < population.size(); j++)
		// 	{
				
		// 	}
		// }

		//Determine the adjusted fitness
		//For now adjusted fitness is simply fitness

		//speciesPop.add(population);
		//Until I can find a way to actually speciate the people the species is
		//essentially going to be the population
		myPopulation.add(population);

		//Determine the adjusted fitness
		//For now adjusted fitness is simply fitness
		for (int i = 0; i < this.myPopulation.size(); i++)
		{
			for (int j = 0; j < this.myPopulation.get(j).size(); i++)
			{
				myPopulation.get(i).get(j).setAdjustedFitness(myPopulation.get(i).get(j).getFitness());
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
		//return numToSpawn;
		return 5.0;
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
		for (int genomeIndex = 1; genomeIndex < myPopulation.get(speciesID).size() + 1; genomeIndex++)
		{
			if (myPopulation.get(speciesID).get(fittestGenomeIndex).getFitness() < 
				   myPopulation.get(speciesID).get(genomeIndex).getFitness())
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
		// ArrayList<Double> 	fitnessArray 	= new ArrayList<Double>();

		// double 				myRandom 		= 0.0;

		// int  				genomeIndex 	= 1;

		// for (genomeIndex = 1; genomeIndex < myPopulation.get(speciesID).size() + 1; genomeIndex++)
		// {
		// 	fitnessArray += myPopulation.get(speciesID).get(genomeIndex).getFitness();
		// }

		// myRandom = random.nextDouble(/*Get the last value from the fitnessarray*/);

		// for (int genomeIndex = 0; genomeIndex < myPopulation.get(speciesID).size(); genomeIndex++)
		// {
		// 	if (myRandom < myPopulation.get(speciesID).get(genomeIndex))
		// 	{
		// 		break;
		// 	}
		// }


		// return myPopulation.get(speciesID).get(genomeIndex);
		return myPopulation.get(speciesID).get(random.nextInt((myPopulation.get(speciesID).size() - 1)));
	}

	public int getNumMembers(int speciesID)
	{
		return myPopulation.get(speciesID).size();
	}
}
