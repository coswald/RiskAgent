package com.riskybusiness.genetic.test;

import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.Synapse;
import com.riskybusiness.neural.NeuralNet;
import com.riskybusiness.genetic.Genome;
import com.riskybusiness.genetic.Innovation;
import com.riskybusiness.genetic.InnovationDB;
import com.riskybusiness.genetic.InnovationType;
import com.riskybusiness.genetic.LinkGene;
import com.riskybusiness.genetic.NeuronGene;

import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class GeneticTest
{
	public static void main(String... arg) throws Exception
	{
		//Represents the scanner to get input
		Scanner input = new Scanner(System.in);
		//Represents the array of created neurons
		ArrayList<NeuronGene> neuronGenes = new ArrayList<NeuronGene>();
		//Represents the array of created links
   		ArrayList<LinkGene>   linkGenes   = new ArrayList<LinkGene>();
   		//Represents the number of input neurons
		int numInputs  = 2;
   		//Represents the number of hidden neurons
		int numHidden  = 5;
		//Represents the number of output neurons
		int numOutputs = 2;
		//Represents the number of total neurons
		int numNeurons = numInputs + numHidden + numOutputs;
		//Loop control variables
		int i;
		int j;
		int k;
		//Represents the current id of the neuron to be added
		int neuronID = 1; //Also, this is a really piss poor way to address the issue that exists
		//Represents the current id of the link to be added
		int linkID   = 1; //Also, this is a really piss poor way to address the issue that exists
		//Represents the variable used to create psuedorandom numbers
		Random random = new Random();
		//Represents the weight to be assigned to the neuron
		float fweight;
		//Represents the weight to be assigned to the link
		double dweight;
		//Represents the genome we created
		Genome myGenome;
		//Represents the neural network
		NeuralNet myNetwork;

		//This will allow us to specify the number of hidden layers and then we will get user input to determine
		//how many neurons in each hidden layer
		//int numHiddenLayers = 2;
		//ArrayList<int> hiddenLayers = new ArrayList<int>(numHiddenLayers);


		System.out.println("Ready to test?");
		k = input.nextInt();

		//The next three loops will create the neurons
		//Currently, there is no need to seperate into three loops, but I am working on implementing different neuron types
		for (i = 0; i < numInputs; i++)
		{
			fweight = random.nextFloat();
			neuronGenes.add(new NeuronGene(neuronID, "Sigmoid", false, fweight/**, INPUT**/));
			neuronID++; 
		}
		for (i = 0; i < numHidden; i++)
		{
			fweight = random.nextFloat();
			neuronGenes.add(new NeuronGene(neuronID, "Sigmoid", false, fweight/**, HIDDEN**/));
			neuronID++; 
		}
		for (i = 0; i < numOutputs; i++)
		{
			fweight = random.nextFloat();
			neuronGenes.add(new NeuronGene(neuronID, "Sigmoid", false, fweight/**, OUTPUT**/));
			neuronID++; 
		}

		System.out.println("Yay neurons were created");
		System.out.println("Ready to test links?");
		k = input.nextInt();

		//Create links
		//This presents a wierd scenario. Will all neurons be connected in the beginning? If no how will we determine to connect them?
		//I am going to go with the approach that all are connected to begin with

		//First simple scenario with 1 hidden layer
		//for (i = 0; i < numInputs; i++)
		//{
			//Create input links
		//}

		//Adds links from all input neurons to hidden neurons
		for (i = 0; i < numInputs; i++)
		{
			for (j = numInputs + 1; j < numInputs + numHidden + 1; j++) //numInputs + 1 was added so j is equal
			{															//to the starting hidden node id
				dweight = random.nextDouble();
				linkGenes.add(new LinkGene(i, j, linkID, dweight, false));
				linkID++;
			}
		}

		//Adds links from all hidden neurons to output neurons
		for (i = numInputs + 1; i < numInputs + numHidden + 1; i++)
		{
			for (j = numNeurons - numOutputs + 1; j < numNeurons + 1; j++) //Similarily to above the math is to ensure
			{															   //that j starts at the first output node id
				dweight = random.nextDouble();
				linkGenes.add(new LinkGene(i, j, linkID, dweight, false));
				linkID++;
			}
		}

		System.out.println("Yay links were created");
		System.out.println("Ready to test genome?");
		k = input.nextInt();

		//Create a genome!!
		myGenome = new Genome(1, neuronGenes, linkGenes, numInputs, numOutputs);

		/**
		 * Space to create testers for manipulating genome
		 *
		 *
		 *
		 *
		 *
		**/

		System.out.println("Yay the genome was created");
		System.out.println("Ready to test neural nets?");
		k = input.nextInt();

		//Create a neural network!!
		System.out.println(myGenome.getSizeNeuron());
		System.out.println(myGenome.getSizeLink());
		myNetwork = myGenome.createPhenotype();

	}
}