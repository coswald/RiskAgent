package com.riskybusiness.genetic.test;

import com.riskybusiness.genetic.*;
import java.io.*;
import java.util.*;

public class ConvertToEpoch
{
	public static void main(String... args)
	{
		Epoch e = new Epoch(50, 30, 6, .3D, .15D, .02D);
		//Initialize the reader
		ObjectInputStream parametersReader = null;
		ObjectInputStream populationReader = null;
		ObjectInputStream speciesReader = null;
		//Try reading the data from the files
		try
		{
			//Initialize the readers and their files as well
			System.out.println("\tAttempting to load the chosen one");
			parametersReader = new ObjectInputStream(new FileInputStream("parameters.txt"));
			populationReader = new ObjectInputStream(new FileInputStream("population.txt"));
			speciesReader = new ObjectInputStream(new FileInputStream("species.txt"));
			
			e.setGenomeID((int) parametersReader.readObject());
			e.setTheChosenOne((Genome) parametersReader.readObject());
			e.setInnovations((InnovationDB) parametersReader.readObject());
			e.setSpeciesID((int) parametersReader.readObject());
			e.setBestFitness((double) parametersReader.readObject());
			
			ArrayList<Genome> population = (ArrayList<Genome>)populationReader.readObject();
			Genome[] g = new Genome[population.size()];
			for(int i = 0; i < population.size(); i++)
				g[i] = population.get(i);
			e.setPopulation(g);
			
			e.setSpecies((ArrayList<Species>)speciesReader.readObject());
			
			e.saveToFile("golden.gaif");
			//Close the reader
			if(parametersReader != null)
				parametersReader.close();
		}
		catch(Exception a)
		{
			System.out.println("\tError!");
			a.printStackTrace();
			System.err.println(a.toString());
			System.exit(1);
		}
		finally
		{
			System.out.println("\tInput Successful!");
		}
	}
}