package com.riskybusiness.genetic;

public class UniqueID
{
	private int genomeID = 0;
	private int linkID   = 0;
	private int neuronID = 0;

	//Increment the genome ID and then return it
	public int getNextGenomeID()
	{
		genomeID++;
		return genomeID;
	}

	//Increment the link ID and then return it
	public int getNextLinkID()
	{
		linkID++;
		return linkID;
	}

	//Increment the neuron ID and then return it
	public int getNextNeuronID()
	{
		neuronID++;
		return neuronID;
	}

	//Return the genome ID
	public int getCurGenomeID()
	{
		return genomeID;
	}

	//Return the link ID
	public int getCurLinkID()
	{
		return linkID;
	}

	//Return the neuron ID
	public int getCurNeuronID()
	{
		return neuronID;
	}
}