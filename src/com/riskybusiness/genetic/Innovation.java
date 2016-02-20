package com.riskybusiness.genetic;

public class Innovation extends Object
{
	
	private InnovationType type;
	private int in;
	private int out;
	private int neuronID;

	public Innovation(InnovationType type, int in, int out, int id)
	{
		this.type = type;
		this.in = in;
		this.out = out;
		this.neuronID = id;
	}

	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Innovation))
			return false;
		Innovation i = (Innovation)o;
		return (this.type == o.type && this.in == o.in && this.out == o.out && this.neuronID == o.id);
	}

	//Get whether the innovation is a neuron or a link
	public InnovationType getType() 
	{
		return this.type;
	}

	//Get synapse input neuronID, return -1 for neurons
	public int getIn() 
	{
		return this.in;
	}

	//Get synapse output neuronID, return -1 for neurons
	public int getOut()
	{
		return this.out;
	}

	//Get id of the neuron, will return -1 for links
	public int getID()
	{
		return this.neuronID;
	}
}