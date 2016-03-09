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
			System.out.println("!");
			compatibilityTable.add(compatibilityRow);
		}

		for (int i = 0; i < population.size(); i++)
		{
			System.out.print("Row " + i + ":");
			for (int j = 0; j < population.size(); j++)
			{
				if (i != j)
				{
					System.out.print(compatibilityTable.get(i).get(j) + " ");
				}
			}
			System.out.println();
		}


		//Using the compatibilty table determine the species
		// for (int i = 0; population.size(); i++)
		// {
		// 	for (int j = 0; j < population.size(); j++)
		// 	{
				
		// 	}
		// }

		speciesPop.add(population.get(0));
		myPopulation.add(speciesPop);


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
		int fittestGenomeID = 0;

		//Loop through the genomes and compare the current genome to the fittest and 
		//determine the fittest individual
		for (int genomeID = 1; genomeID < myPopulation.get(speciesID).size(); genomeID++)
		{
			if (myPopulation.get(speciesID).get(fittestGenomeID).getFitness() < 
				   myPopulation.get(speciesID).get(genomeID).getFitness())
			{
				fittestGenomeID = genomeID;
			}
		}

		//Return
		return myPopulation.get(speciesID).get(genomeID);
	}

	//Figure out a way to return better people
	//Returns a random member from the population
	public Genome getMember(int speciesID)
	{
		return myPopulation.get(speciesID).get(random.nextInt(myPopulation.get(speciesID).size() - 1));
	}
}
