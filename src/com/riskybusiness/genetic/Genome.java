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

import com.riskybusiness.genetic.Epoch;

import com.riskybusiness.neural.Neuron;
import com.riskybusiness.neural.Synapse;
import com.riskybusiness.neural.NeuralNet;

import static java.lang.Math.abs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;

/**
 * <p>&nbsp&nbsp&nbsp&nbsp&nbspThis class describes a collection of
 * {@code LinkGene}s and {@code NeuronGene}s to form a {@code NeuralNet}.
 * This class will form the phenotype in order for an individual to
 * fire, and trians a neural network based on the N. E. A. T. algorithm.
 * Of course, this class relies on methods defined in {@code GenomeHelper}
 * to do this.</p>
 * @author Kaleb Luse
 * @author Coved W Oswald
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.genetic.GenomeHelper
 * @see com.riskybusiness.neural.NeuralNet
 */
public class Genome implements Serializable
{
    
    private static final long serialVersionUID = 2649985816998697033L;

    //Represents the ID of the genome
    private int                     genomeID;
    //Represents the list of links
    private ArrayList<NeuronGene>   neuronGeneSet = new ArrayList<NeuronGene>();
    //Represents the list of links
    private ArrayList<LinkGene>     linkGeneSet   = new ArrayList<LinkGene>();
    //Represents the nueral net of the genome
    private NeuralNet               myNetwork;
    //Represents the fitness of the genome
    private double                  genomeFitness;
    //Represents the fitness of the genome adjusted for the species of the genome
    private double                  genomeAdjFitness;
    //Represents the amount of children to spawn
    private double                  amountToSpawn;
    //Represents the number of input Neurons
    private int                     numInputNeurons;
    //Represents the number of output Neurons
    private int                     numOutputNeurons;
    //Represents the current number of layers of the genome
    private int                     numLayers;
    //Represents the speciesID of the genome
    private int                     species;
    //Represents the number of link genes in the genome
    private int                     numLinkGenes;
    //Represents the score given to speciate the genome
    private int                     compatibilityScore;
    //Represents if the output layer is pushed
    private boolean                 outputLayerPushed;
    //Represents the package to create psuedorandom numbers
    private Random                  random = new Random();

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspThis constructor creates
	 * a genome from an {@code ArrayList} of {@code NeuronGene}s,
	 * an {@code ArrayList} of {@code NeuronGene}s, and ID number
	 * for this {@code Genome}, and the number of inputs/outputs
	 * for the {@code NeuralNet}.
	 * @param id The ID for this genome.
	 * @param neurons The list of metadata neurons.
	 * @param links The list of metadata links.
	 * @param inputs The amount of inputs to the {@code NeuralNet}.
	 * @param outputs The amount of outputs from the {@code NeuralNet}.
	 */
    public Genome(int id, ArrayList<NeuronGene> neurons, ArrayList<LinkGene> links, int inputs, int outputs)
    {
        //Represents the ID of the genome
        genomeID = id;

        //Create a deep copy of the passed in ArrayList of neurons
        for (int i = 0; i < neurons.size(); i++)
        {
            neuronGeneSet.add(new NeuronGene(neurons.get(i).getID(), neurons.get(i).getNeuronType(), neurons.get(i).getLayerType(), neurons.get(i).getActivationResponse(), neurons.get(i).getNeuronLayer()));
        }

        //Create a deep copy of the passed in ArrayList of links
        for (int i = 0; i < links.size(); i++)
        {
            linkGeneSet.add(new LinkGene(links.get(i).getID(), links.get(i).getFromNeuron(), links.get(i).getToNeuron(), links.get(i).getInnovationID(), links.get(i).getWeight(), links.get(i).getEnabled()));
        }

        //Set the genome parameters with information passed in
        numInputNeurons  = inputs;
        numOutputNeurons = outputs;

        //Set some parameters using some of the metadata provided by the inputs
        numLinkGenes     = links.size();
        numLayers        = neurons.get((neurons.size() - 1)).getNeuronLayer();
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspCreates a blank {@code Genome}.</p>
	 */
    public Genome()
    {
        genomeID = -1;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the {@code NeuronGene}s
	 * that make up the {@code Genome}.</p>
	 * @return The list of neurons.
	 */
    public ArrayList<NeuronGene> getNeurons()
    {
        return this.neuronGeneSet;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the {@code LinkGene}s
	 * that make up the {@code Genome}.</p>
	 * @return The list of links.
	 */
    public ArrayList<LinkGene> getLinks()
    {
        return this.linkGeneSet;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the number of inputs
	 * that is sent into {@code NeuralNet}.</p>
	 * @return The number of inputs sent to the neural network.
	 */
    public int getNumInputs()
    {
        return this.numInputNeurons;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the number of outputs
	 * that is sent form the {@code NeuralNet}.</p>
	 * @return The number of outputs sent from the neural network.
	 */
    public int getNumOutputs()
    {
        return this.numOutputNeurons;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the number of inputs
	 * that are sent to the {@code NeuralNet}.</p>
	 * @param numInputs The new input number.
	 */
    public void setNumInputs(int numInputs)
    {
        this.numInputNeurons = numInputs;
    }

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the number of outputs
	 * that are sent form the {@code NeuralNet}.</p>
	 * @param numOutputs The new output number.
	 */
    public void setNumOutputs(int numOutputs)
    {
        this.numOutputNeurons = numOutputs;
    }

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspGets the number of
	 * neurons in this {@code Genome}.</p>
	 * @return The number of neurons in this {@code Genome}.
	 */
    public int getSizeNeuron()
    {
        return neuronGeneSet.size();
    }

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspGets the number of
	 * links in this {@code Genome}.</p>
	 * @return The number of links in this {@code Genome}.
	 */
	public int getSizeLink()
    {
        return linkGeneSet.size();
    }
    
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the ID of
	 * this {@code Genome}.</p>
	 * @return The ID of this {@code Genome}.
	 */
    public int getID()
    {
       return this.genomeID;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the adjusted
	 * fitness of this {@code Genome}. This is determined
	 * by the species this class belongs to.</p>
	 * @return The adjusted fitness of this {@code Genome}.
	 */
    public double getAdjustedFitness()
    {
        return this.genomeAdjFitness;
    }

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the actual
	 * fitness of this {@code Genome}.
	 * @return The actual fitness of this {@code Genome}.
	 */
    public double getFitness()
    {
        return this.genomeFitness;
    }

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the innovation
	 * number of the given link provided.
	 * @param link The link to find the innovation ID of.
	 * @return The innovation ID of link.
	 */
    public int getInnovationNum(LinkGene link)
    {
        return link.getInnovationID();
    }

    public int getNumLinkGenes()
    {
        return this.numLinkGenes;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the number of
	 * spawns this {@code Genome} can have.</p>
	 * @return The number of children we can have.
	 */
    public double getNumSpawns()
    {
       return this.amountToSpawn;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the number of
	 * spawns this {@code Genome} can have.</p>
	 * @param spawns The new spawn number.
	 */
    public void setNumSpawns(double spawns)
    {
        this.amountToSpawn = spawns;
    }

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspCalculates the
	 * compatibility number between this {@code Genome}
	 * and the given one.</p>
	 * @param toCompare The {@code Genome} to compare to
	 * 			this one.
	 * @return The compatibility score between the two {@code Genome}s.
	 */
	public double getCompatibilityScore(Genome toCompare)
    {
        //Represents the number of disjoint, excess, and common genes the two
        //genomes share
        double  numDisjoint = 0;
        double  numExcess   = 0;
        double  numCommon   = 0;

        //Represents the summed difference in weights between link genes in common
        double  diffWeights = 0;

        //Represents the indexes for each genome being searched
        int     index1      = 0;
        int     index2      = 0;

        //Represents the innovation ID's of the current links
        int     innovID1    = 0;
        int     innovID2    = 0;

        //As long as we have not reached the end of both linkGeneSets then continue to loop
        while((index1 < this.linkGeneSet.size() - 1) || (index2 < toCompare.getLinks().size() - 1))
        {
            //Represents whether or not we should skip some conditional statements
            boolean skip = false;

            //If we have reached the end of the first genome then increment the index
            //of the second genome and excess genes.
            if (index1 == this.linkGeneSet.size() - 1)
            {
                index2++;
                numExcess++;
                skip = true;
            }
            else if (index2 == toCompare.getLinks().size() - 1)
            {
                index1++;
                numExcess++;
                skip = true;
            }

            //If we don't need to skip then continue with the if statement
            if (!skip)
            {
                //Grab the innovation ID's of the current links
                innovID1 = this.linkGeneSet.get(index1).getInnovationID();
                innovID2 = toCompare.getLinks().get(index2).getInnovationID();

                //If the innovation ID's are the same then increment both indices
                //and number in common and add the value to the difference in weights
                if (innovID1 == innovID1)
                {
                    index1++;
                    index2++;
                    numCommon++;

                    diffWeights += abs(this.linkGeneSet.get(index1).getWeight() - toCompare.getLinks().get(index2).getWeight());
                }
                //Else if the first link is younger than the second link then increment
                //the second link and number of disjoint links
                else if (innovID1 < innovID2)
                {
                    numDisjoint++;
                    index1++;
                }
                //Else if the second link is younger than the first link then increment
                //the first link and number of disjoint links
                else if (innovID1 > innovID2)
                {
                    numDisjoint++;
                    index2++;
                }
            }
        }

        //Determine which genome is longer
        int numGenes = toCompare.getNumLinkGenes();

        if (this.numLinkGenes > numGenes)
        {
            numGenes = this.numLinkGenes;
        }

        //Initialize multipliers
        double disjointMultiplier = 1;
        double excessMultiplier = 1;
        double commonMultiplier = 0.4;

        //Figure out the compatibility score
        double score = (excessMultiplier * numExcess/(double)numGenes) + 
                       (disjointMultiplier * numDisjoint/(double)numGenes) + 
                       (commonMultiplier * diffWeights / numCommon);

        //Return the compatibility score
        return score;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspSets the ID for
	 * this {@code Genome}.
	 * @param id The new ID number.
	 */
    public void setID(int id)
    {
       this.genomeID = id;
    }

    //Returns true if the specified link is already part of the genome
    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns true if the
	 * specified link is already part of the {@code Genome}.
	 * @param neuronIn The link's input neuron.
	 * @param neuronOut The link's output neuron.
	 * @return true if the specified link is present, false otherwise.
	 */
	public boolean duplicateLink(int neuronIn, int neuronOut)
    {
        //Should I account for the possibility that the link is disabled??
        for (int i = 0; i < linkGeneSet.size() - 1; i++)
        {
            if ((neuronIn == linkGeneSet.get(i).getFromNeuron() && neuronOut == linkGeneSet.get(i).getToNeuron()) ||
                (neuronIn == linkGeneSet.get(i).getToNeuron() && neuronOut == linkGeneSet.get(i).getFromNeuron()))
            {
                return true;
            }
        }
        return false;
    }

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspThis method creates
	 * a {@code Neuron} from the given {@code NeuronGene}.
	 * @param neuron The gene to create from.
	 * @return A {@code Neuron} representative of the
	 * 			metadata present within the given gene.
	 * @see com.riskybusiness.neural.Neuron
	 * @see com.riskybusiness.genetic.NeuronGene
	 */
	public Neuron createNeuron(NeuronGene neuron)
    {
        //Represents the number of input links to a neuron
        int numInputs = 0;

        //Set the input neurons number of inputs to 1
        if (neuron.getID() <= numInputNeurons)
        {
            numInputs = 1;
        }
        else
        {
            //Loop through the link gene set and see how many links point to the neuron ID
            for (int i = 0; i < linkGeneSet.size(); i++)
            {
                //If link points to the neuron and is enabled increment the number of inputs
                if (linkGeneSet.get(i).getToNeuron() == neuron.getID() && linkGeneSet.get(i).getEnabled())
                {
                    numInputs++;
                }
            }    
        }

        float weight = (float)neuron.getActivationResponse();

        if (neuron.getID() <= numInputNeurons)
        {
            float[] weights = new float[2];
            for (int i = 0; i < 2; i++)
            {
                weights[i] = 1.0f;
            }

            return new com.riskybusiness.neural.SigmoidNeuron(weight, weights);
        }
        else
        {
            //Create a neuron dependent on its type
            if (neuron.getNeuronType().equals("Sigmoid"))
            {
                float[] weights = new float[numInputs + 1];

                for (int i = 0; i < numInputs; i++)
                {
                    weights[i] = 1.0f;
                }

                weights[numInputs] = (float)neuron.getBiasWeight();
                return new com.riskybusiness.neural.SigmoidNeuron(weight, weights);
            }
            else if (neuron.getNeuronType().equals("Step")) 
            {
                float[] weights = new float[numInputs + 1];

                for (int i = 0; i < numInputs; i++)
                {
                    weights[i] = 1.0f;
                }

                weights[numInputs] = (float)neuron.getBiasWeight();
                return new com.riskybusiness.neural.StepNeuron(weight, weights);
            }
            else
            {
                //throw some error
                return new com.riskybusiness.neural.StepNeuron(weight, numInputs);
            }
        }
    }

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspThis method creates
	 * a {@code Synapse} from the given {@code LinkGene}.
	 * @param link The gene to create from.
	 * @param neuronSet The set of {@code Neuron}s needed
	 * 			for creation of the link.
	 * @return A {@code Neuron} representative of the
	 * 			metadata present within the given gene.
	 * @throws RuntimeException If the link sent in is disabled,
	 * 			or contains corrupt/invalid metadata.
	 * @see com.riskybusiness.neural.Synapse
	 * @see com.riskybusiness.genetic.LinkGene
	 */
    public Synapse createSynapse(LinkGene link, Neuron[] neuronSet)
    {
        //Represents the neuron the synapse is going to and coming from
        int         toNeuronID      = 0;
        //Represents the neuron the synapse is coming from
        int         fromNeuronID    = 0;
        //Represents the ID of the current neuron to be checked
        NeuronGene  currentNeuron;
        //Represents whether the toNeuron has been found and set yet
        boolean     toNeuronIsSet   = false;
        //Represents whether the fromNeuron has been found and set yet
        boolean     fromNeuronIsSet = false;
        //Loop Control Variable
        int         i;
        //Turns to false after the first link has been found
        boolean     firstLink       = true;
        //Represetns the position of the link in the neuron
        int         linkPos         = 0;

        if (!link.getEnabled())
		{
			//original message: "You dumb idiot. You sent in a disabled link"
			throw new RuntimeException("Cannot make a link that is disabled!");
        }

        for (i = 0;i < neuronGeneSet.size(); i++)
        {
            //Grab the neuron from the gene set and see if it matches the neurons on 
            //any of the links
            currentNeuron = neuronGeneSet.get(i);

            if (currentNeuron.getID() == link.getToNeuron() && currentNeuron.getID() == link.getFromNeuron())
            {
                toNeuronID = i;
                fromNeuronID = i;
                toNeuronIsSet = true;
                fromNeuronIsSet = true;
            } 
            else if (currentNeuron.getID() == link.getToNeuron())
            {
                toNeuronID = i;
                toNeuronIsSet = true;
            }
            else if (currentNeuron.getID() == link.getFromNeuron())
            {
                fromNeuronID = i;
                fromNeuronIsSet = true;
            }
            
            //If both neurons have been set, stop looking through the neuron set
            if (toNeuronIsSet && fromNeuronIsSet)
            {
                break;
            }
        }
        //If both neurons have been set then find the position of the link on the neuron
        if (toNeuronIsSet && fromNeuronIsSet)
        {
            //Loop through the linkGeneSet and determine the position of the link on the neuron
            for (i = 0; i < linkGeneSet.size(); i++)
            {
                //If the links neuron = the passed in neuron then determine if we have a link match
                if (linkGeneSet.get(i).getToNeuron() == link.getToNeuron())
                {
                    //If the link matches what we are looking for then break else increment the linkpos
                    if (linkGeneSet.get(i).getID() == link.getID())
                    {
                        break;
                    }
                    else
                    {
                        //Do I need to check for a disabled link here? Yes!
                        if (linkGeneSet.get(i).getEnabled())
                        {
                            linkPos++;
                        }
                    }
                }
            }
            neuronSet[toNeuronID].setWeight(linkPos, (float)link.getWeight());
            return new com.riskybusiness.neural.Synapse (linkPos, neuronSet[fromNeuronID], neuronSet[toNeuronID]);
        }
        else
        {
            //throw some error
            throw new RuntimeException("Link genes were not set properly");
        }
        
    }
     
    /**
     * <p>This function converts the respective genome into a 
     * neural network.</p>
     **/
    public void createPhenotype()
    {
        int activeLinks = 0;
        for (int i = 0;i < linkGeneSet.size(); i++)
        {
            //Only create links that are enabled
            if (linkGeneSet.get(i).getEnabled())
            {
                activeLinks++;
            }
        }
        //These arrays hold the actual neurons and synapses of the neural network
        Synapse[] linkSet = new Synapse[activeLinks];
        Neuron[] neuronSet = new Neuron[neuronGeneSet.size()];

        //This loop loops through the global neuronGeneSet and creates neurons from these genes
        //and then adds them to the neuronSet
        for (int i = 0;i < neuronGeneSet.size(); i++)
        {
            neuronSet[i] = createNeuron(neuronGeneSet.get(i));
        }
        
        int j = 0;
        //This loop does the same as above but converts the link genes into synapses
        for (int i = 0;i < linkGeneSet.size(); i++)
        {
            //Only create links that are enabled
            if (linkGeneSet.get(i).getEnabled())
            {
                linkSet[j] = createSynapse(linkGeneSet.get(i), neuronSet);
                j++;
            }
        }

        this.myNetwork = new com.riskybusiness.neural.NeuralNet(neuronSet, linkSet);
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChecks to see
	 * whether the neuron exists within the {@code Genome}.</p>
	 * @param id The ID of the neuron in question.
	 * @return true if the neuron in question is here,
	 * 			false if not.
	 */
    public boolean neuronIDExists(int id)
    {
        for (int i = 0; i < neuronGeneSet.size(); i++)
        {
            if (neuronGeneSet.get(i).getID() == id)
            {
                return true;
            }
        }

        return false;
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns the
	 * {@code NeurlNet} associated with this
	 * {@code Genome}. This method will return
	 * null if {@link com.riskybusiness.genetic.Genome#createPhenotype()}
	 * has not been called, or the last valid
	 * phenotype created since the last call of that
	 * method.</p>
	 * @return The neural network formed from
	 * 			this class.
	 */
    public NeuralNet getNetwork()
    {
        return this.myNetwork;
    }

    /**
     * This functions adds a link to the genome dependent upon the mutation rate and other parameters.
     * @param mutationRate The rate at which links can be added
     * @param chanceOfLooped The chance a link has of being a looped link
     * @param innovation This is simply the database that contains all the innovations
     * @param numTrysToFindLoop This determines how many times the function will look to 
     *                          create a looped link
     * @param numTrysToAddLink This prevents the possibility of an infinite loop in the case
     *                         where the all neurons are connected to each other and thus limits
     *                         the amount of times the function will look for the opportunity to add a link
     *
     */ 
    public void addLink(double mutationRate, double chanceOfLooped, InnovationDB innovation, int numTrysToFindLoop, int numTrysToAddLink)
    {
        //This variable describes a function to create random numbers
        Random random = new Random();
        //These two variables respresent the id's of the neurons the link will connect. If the id's are -1
        //then these is no link connecting two neurons
        int toNeuronID   = -1;
        int fromNeuronID = -1;


        //If the random value doesn't exceed the probability threshold then exit by returning
        if (random.nextDouble() > mutationRate)
        {
            return;
        }

        //If we made it here then we are going to attempt to mutate the genome
        //If the random value exceeds the chance of a looped link then attempt to create a looped link
        if (random.nextDouble() < chanceOfLooped)
        {
            //This loop will loop thorugh as many times as specified looking for a proper neuron to create a looped link with
            while(numTrysToFindLoop > 0)
            {
                //This needs to be a number between the number of inputs + 1 and the size of the neuron arraylist - 1
                int neuronIndex = random.nextInt(neuronGeneSet.size() - numInputNeurons - 1) + numInputNeurons;

                //This if statement is supposed to ensure that the gene we are adding a looped link to isn't an input gene or output gene
                if (neuronGeneSet.get(neuronIndex).getNeuronLayer() != 1 && neuronGeneSet.get(neuronIndex).getNeuronLayer() != numLayers)
                {
                    toNeuronID   = neuronGeneSet.get(neuronIndex).getID();
                    fromNeuronID = neuronGeneSet.get(neuronIndex).getID();
                    //If we find a good neuron that satisfies our conditions then we don't need to loop anymore
                    numTrysToFindLoop = 0;
                }
                numTrysToFindLoop--;
            }
        }
        //If the random value didn't exceed the chance to create a looped link then we still need to mutate the genome
        else
        {
            //This loop will loop through and try to create a link until it has created a link or has exceed its number of tries
            while(numTrysToAddLink > 0)
            {
                //Find two random neurons and determine if a link can be made between them
                //Needs to be inbetween 0 and the size of the neuron array - 1
                int neuronIndexFrom  = random.nextInt(neuronGeneSet.size() - 1); 
                fromNeuronID         = neuronGeneSet.get(neuronIndexFrom).getID();
                //Needs to be inbetween the number of input neurons + 1 and the size of the neuron array - 1
                int neuronIndexTo    = random.nextInt(neuronGeneSet.size() - numInputNeurons - 1) + numInputNeurons;
                toNeuronID           = neuronGeneSet.get(neuronIndexTo).getID();

                //Prevent recurrent links
                //If the neuron layer of the from neuron is greater than the neuron layer of the to neuron then 
                //swap the ID's of the neurons.
                if (neuronGeneSet.get(neuronIndexFrom).getNeuronLayer() > neuronGeneSet.get(neuronIndexTo).getNeuronLayer())
                {
                    fromNeuronID = neuronGeneSet.get(neuronIndexTo).getID();
                    toNeuronID   = neuronGeneSet.get(neuronIndexFrom).getID();
                }

                //Don't add links to the input layer
                if(fromNeuronID <= numInputNeurons)
                {
                    toNeuronID   = -1;
                    fromNeuronID = -1;
                }
                else
                {
                    //Check to see if a link exists and if it doesn't then break from the loop else continue looping
                    if((!(duplicateLink(fromNeuronID,toNeuronID)) || (fromNeuronID == toNeuronID)) && 
                        !(neuronGeneSet.get(neuronIndexFrom).getNeuronLayer() == numLayers || neuronGeneSet.get(neuronIndexTo).getNeuronLayer() == numLayers) &&
                        !(neuronGeneSet.get(neuronIndexFrom).getNeuronLayer() == neuronGeneSet.get(neuronIndexTo).getNeuronLayer()))
                    {
                        numTrysToAddLink = 0;
                    }
                    else
                    {
                        toNeuronID   = -1;
                        fromNeuronID = -1;
                    }
                }
                numTrysToAddLink--;
            }
        }
        //If either neuronID is less than 0 then we can't create a link so exit by returning
        if (toNeuronID < 0 || fromNeuronID < 0)
        {
            return;
        }

        //Check the database for this innovation, if it exists it returns the ID of the link else returns 0 if new
        int innovationCheck = innovation.addInnovation(InnovationType.NEW_LINK, fromNeuronID, toNeuronID, -1);
        
        //If new innovation add it to the db else grab the info from the db
        if (innovationCheck == 0)
        {
            //Push the new gene into the array
            linkGeneSet.add(new LinkGene(linkGeneSet.size() + 1, fromNeuronID, toNeuronID, innovation.curID(), random.nextDouble(), true));
            GenomeHelper.sortLinkArray(neuronGeneSet, linkGeneSet);
            numLinkGenes++;
        }
        else
        {
            linkGeneSet.add(new LinkGene(linkGeneSet.size() + 1, fromNeuronID, toNeuronID, innovationCheck, random.nextDouble(), true));
            GenomeHelper.sortLinkArray(neuronGeneSet, linkGeneSet);
            numLinkGenes++;
        }
    }

    /**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspAdds a neuron into the {@code Genome}.
	 * @param mutationRate The rate at which neurons are added.
	 * @param innovation The database to check against.
	 * @param numTrysToFindOldLink A parameter that states how many times
	 * 			a {@code Genome} is allowed to randomly find a link to 
	 * 			add this neuron to.
	 */
    public void addNeuron(double mutationRate, InnovationDB innovation, int numTrysToFindOldLink) 
    {
        //If a valid link is found to add a neuron to then this will be set to true
        boolean linkFound = false;

        boolean skip = true;

        //This is the ID of the chosen link to test
        int chosenLinkID = -1;
        
        //Represent the index of the chosen link 
        LinkGene chosenLink = new LinkGene();
        
        //Represents the weight of the original link
        double originalWeight;
        
        //These two variables respresent the id's of the neurons the original link connects
        int toNeuronID;
        int fromNeuronID;
        
        //This represents the maximum amount of neurons allowed in the genome
        int sizeThreshold = 60;

        //If the random value doesn't exceed the probability threshold then exit by returning
        if (random.nextDouble() > mutationRate)
        {
            return;
        }

        //Not quite sure what the size threshold is yet but it prevents the chaining effect so I will implement it
        if (linkGeneSet.size() < sizeThreshold)
        {
            //Loop through and try to find an old link to add a neuron to
            for (int i = numTrysToFindOldLink; i > 0; i--)
            {
                //Prevents the chaining problem by choosing older genes to replace
                chosenLinkID = random.nextInt(numLinkGenes - numInputNeurons - 1 - ((int)Math.sqrt(numLinkGenes))) + numInputNeurons;

                //Loop through the linkGeneSet to find the chosen link
                for (int j = 0; j < linkGeneSet.size(); j++)
                {
                    if (linkGeneSet.get(j).getID() == chosenLinkID)
                    {
                        chosenLink = linkGeneSet.get(j);
                        break;
                    }
                }

                //If the link is enabled then we have found the link we are going to disable
                if ((chosenLink.getEnabled()))
                {
                    linkFound = true;
                    skip = false;
                    numTrysToFindOldLink = 0;
                }

                //If we didn't find a link then exit by returning
                if (!linkFound)
                {
                    return;
                }
                
                //Disable the original link gene
                chosenLink.setLink(false);

                //Grab the weight of the original link
                originalWeight = chosenLink.getWeight();

                //Get the id's of the neurons the original link connected
                fromNeuronID = chosenLink.getFromNeuron();
                toNeuronID   = chosenLink.getToNeuron();

                //Check to see if the innovation exists already
                int innovationCheck = innovation.addInnovation(InnovationType.NEW_NEURON, fromNeuronID, toNeuronID, (neuronGeneSet.size() + 1));

                if (innovationCheck != -1)
                {
                    //Determine Nueron layer
                    NeuronGene fromNeuron = new NeuronGene();

                    //Find the from neuron
                    for (int j = 0; j < neuronGeneSet.size(); j++)
                    {
                        if (neuronGeneSet.get(j).getID() == fromNeuronID)
                        {
                           fromNeuron = neuronGeneSet.get(j);
                           break;
                        }
                    }

                    //Determine the layer of the fromNeuron and add 1 to get the neuron to be added layer
                    int newNeuronLayer = fromNeuron.getNeuronLayer() + 1;

                    //Add the new neuron to the gene set
                    neuronGeneSet.add(new NeuronGene((neuronGeneSet.size() + 1), "Sigmoid", "Hidden", random.nextDouble(), newNeuronLayer));
                    innovationCheck = innovation.addInnovation(InnovationType.NEW_LINK, fromNeuronID, neuronGeneSet.size(), -1);
                    if (innovationCheck == 0)
                    {
                        linkGeneSet.add(new LinkGene(linkGeneSet.size() + 1, fromNeuronID, neuronGeneSet.size(), innovation.curID(), 1.0, true));
                    }
                    else
                    {
                        linkGeneSet.add(new LinkGene(linkGeneSet.size() + 1, fromNeuronID, neuronGeneSet.size(), innovationCheck, 1.0, true));
                    }
                    numLinkGenes++;
                    innovationCheck = innovation.addInnovation(InnovationType.NEW_LINK, neuronGeneSet.size(), toNeuronID, -1);
                    if (innovationCheck == 0)
                    {
                        linkGeneSet.add(new LinkGene(linkGeneSet.size() + 1, neuronGeneSet.size(), toNeuronID, innovation.curID(), originalWeight, true));
                    }
                    else
                    {
                        linkGeneSet.add(new LinkGene(linkGeneSet.size() + 1, neuronGeneSet.size(), toNeuronID, innovationCheck, originalWeight, true));
                    }
                    numLinkGenes++;

                    //Push back any neurons that were affected by the addition
                    GenomeHelper.pushNeurons(neuronGeneSet, linkGeneSet, neuronGeneSet.get((neuronGeneSet.size() - 1)));

                    for (int j = 0; j < neuronGeneSet.size(); j++)
                    {
                        if(neuronGeneSet.get(j).getNeuronLayer() > numLayers)
                        {
                            numLayers++;
                        }
                    }

                    //Sort the neuron array
                    GenomeHelper.sortNeuronArray(neuronGeneSet, numLayers);

                    //Sort the link genes
                    GenomeHelper.sortLinkArray(neuronGeneSet, linkGeneSet);
                    break;
                }
            }
        }
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspChanges the adjusted
	 * fitness for this {@code Genome}.</p>
	 * @param fitness The new adjusted fitness.
	 */
    public void setAdjustedFitness(double fitness)
    {
        //Set the adjusted fitness
        this.genomeAdjFitness = fitness;
    }

	public double determineFitness()
	{
	   return this.determineFitness(Epoch.dataFile);
	}
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspReturns a value
	 * that shows the fitness of this {@code Genome}.
	 * @return The fitness of this {@code Genome}.
	 */
    public double determineFitness(String fileName)
    {
        //Create the phenotype
        this.createPhenotype();

        // This will reference one line at a time
        String line = null;

        //Represents the inputs on the line of the file
        String[] fileLine = new String[numInputNeurons + 1];

        //Represents the input to the network
        float[][] inputs = new float[numInputNeurons][1];

        //Represents the expected output from the network
        float expectedOutput = 0.0f;

        //Initialize the genome fitness to 0
        genomeFitness = 0;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Loop through each line of the file, get the inputs, fire them and then evaluate the results
            while((line = bufferedReader.readLine()) != null) {
                fileLine = line.split("\\t");
                //Loop through each input on the line and add it to the input array or expected output
                for (int x = 0; x < fileLine.length; x++)
                    //If we are at the last input then store it into the expected results
                    if (x == numInputNeurons)
                    {
                        expectedOutput = (float)Integer.parseInt(fileLine[x]);
                    }
                    else
                    {
                        inputs[x] = new float[]{(float)Integer.parseInt(fileLine[x])};
                    }
                //Calculate the variance and add it to the fitness
                genomeFitness += Math.pow((this.myNetwork.fire(inputs)[0] * 365) - expectedOutput, 2);
            }   

            //Find the genome fitness by dividing the max variance by the total variance
            genomeFitness = 133225000 / genomeFitness;

            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.err.println(
                "Error reading file '" 
                + fileName + "'");                  
        }
        return genomeFitness;
    }

	public void printFitness()
	{
		this.printFitness(Epoch.dataFile);
	}
    
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspPrints the information
	 * about the fitness to the standard output.</p>
	 * @param fileName The name of the file to look into
	 * 				to gather fitness data from.
	 * @see java.lang.System#out
	 */
    public void printFitness(String fileName)
    {
        //Create the phenotype
        this.createPhenotype();

        // This will reference one line at a time
        String line = null;

        //Represents the inputs on the line of the file
        String[] fileLine = new String[numInputNeurons + 1];

        //Represents the input to the network
        float[][] inputs = new float[numInputNeurons][1];

        //Represents the expected output from the network
        float expectedOutput = 0.0f;

        //Initialize the genome fitness to 0
        genomeFitness = 0;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Loop through each line of the file, get the inputs, fire them and then evaluate the results
            while((line = bufferedReader.readLine()) != null) {
                fileLine = line.split("\\t");
                //Loop through each input on the line and add it to the input array or expected output
                for (int x = 0; x < fileLine.length; x++)
                    //If we are at the last input then store it into the expected results
                    if (x == numInputNeurons)
                    {
                        expectedOutput = (float)Integer.parseInt(fileLine[x]);
                    }
                    else
                    {
                        inputs[x] = new float[]{(float)Integer.parseInt(fileLine[x])};
                    }
                //Print the forecasted result
                System.out.println(this.myNetwork.fire(inputs)[0] * 365);
            }   

            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.err.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.err.println(
                "Error reading file '" 
                + fileName + "'");                  
        }
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspMakes a new
	 * {@code Genome} that has random features present
	 * within this one and the other {@code Genome}.</p>
	 * @param dad The other {@code Genome} to mate with.
	 * @param innovation The database used for changes.
	 */
    public Genome crossover(Genome dad, InnovationDB innovation)
    {
        //Represents which genome has the best fitness
        int best; //0 = Mom, 1 = Dad;
        
        //Represents the current index of mom and dads gene counter
        int dadIndex = 0;
        int momIndex = 0;

        //Represents if a gene has been selected from either mom or dad
        boolean selected = false;

        //Represents the array of neuronGenes for the baby
        ArrayList<NeuronGene> babyNeuronGenes = new ArrayList<NeuronGene>();
        
        //Represents the array of linkGenes for the baby
        ArrayList<LinkGene> babyLinkGenes = new ArrayList<LinkGene>();

        //Represents the neuronID's to be added 
        ArrayList<Integer> neuronIDS = new ArrayList<Integer>();

        LinkGene selectedLink = new LinkGene();

        this.determineFitness();
        dad.determineFitness();

        if (this.getFitness() == dad.getFitness())
        {
            if (this.getNumLinkGenes() == dad.getNumLinkGenes())
            {
                best = random.nextInt(2);
            }
            else
            {
                if (this.getNumLinkGenes() < dad.getNumLinkGenes())
                {
                    best = 0; //Mom
                }
                else
                {
                    best = 1; //Dad
                }
            }
        }
        else
        {
            if (this.getFitness() > dad.getFitness())
            {
                best = 0; //Mom
            }
            else
            {
                best = 1; //Dad
            }
        }

        while(!((momIndex == this.linkGeneSet.size()) && dadIndex == dad.linkGeneSet.size()))
        {
            if ((momIndex == this.linkGeneSet.size() && dadIndex != dad.linkGeneSet.size()))
            {
                if (best == 1)
                {
                    selectedLink = dad.linkGeneSet.get(dadIndex);
                    selected = true;
                }

                dadIndex++;
            }
            else if ((dadIndex == dad.linkGeneSet.size()) && momIndex != this.linkGeneSet.size())
            {
                if (best == 0)
                {
                    selectedLink = this.linkGeneSet.get(momIndex);
                    selected = true;
                }

                momIndex++;
            }
            else if (this.getInnovationNum(this.linkGeneSet.get(momIndex)) < dad.getInnovationNum(dad.linkGeneSet.get(dadIndex)))
            {
                if (best == 0)
                {
                    selectedLink = this.linkGeneSet.get(momIndex);
                    selected = true;
                }

                momIndex++;
            }
            else if (this.getInnovationNum(this.linkGeneSet.get(momIndex)) > dad.getInnovationNum(dad.linkGeneSet.get(dadIndex)))
            {
                if (best == 1)
                {
                    selectedLink = dad.linkGeneSet.get(dadIndex);
                    selected = true;
                }

                dadIndex++;
            }
            else if (this.getInnovationNum(this.linkGeneSet.get(momIndex)) == dad.getInnovationNum(dad.linkGeneSet.get(dadIndex)))
            {
                if (random.nextDouble() < 0.5)
                {
                    selectedLink = this.linkGeneSet.get(momIndex);
                    selected = true;
                }
                else
                {
                    selectedLink = dad.linkGeneSet.get(dadIndex);
                    selected = true;
                }

                momIndex++;
                dadIndex++;
            }

            if (selected)
            {
                selected = false;
                if (babyLinkGenes.size() == 0)
                {
                    babyLinkGenes.add(selectedLink);
                }
                else
                {
                    if (babyLinkGenes.get(babyLinkGenes.size() - 1).getInnovationID() != selectedLink.getInnovationID())
                    {
                        babyLinkGenes.add(selectedLink);
                    }
                }

                //Represents whether the neuron ID exists within the neuronID array
                boolean found = false;

                for (int i = 0; i < neuronIDS.size(); i++)
                {
                    if (selectedLink.getToNeuron() == neuronIDS.get(i))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    neuronIDS.add(selectedLink.getToNeuron());
                }

                found = false;

                for (int i = 0; i < neuronIDS.size(); i++)
                {
                    if (selectedLink.getFromNeuron() == neuronIDS.get(i))
                    {
                        found = true;
                        break;
                    }
                }

                if (!found)
                {
                    neuronIDS.add(selectedLink.getFromNeuron());
                }
            }
        }

        if (best == 1)
        {
            for (int i = 0; i < neuronIDS.size(); i++)
            {
                for (int j = 0; j < dad.getNeurons().size(); j++)
                {
                    if (neuronIDS.get(i) == dad.getNeurons().get(j).getID())
                    {
                        babyNeuronGenes.add(dad.getNeurons().get(j));
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < neuronIDS.size(); i++)
            {
                for (int j = 0; j < this.neuronGeneSet.size(); j++)
                {
                    if (neuronIDS.get(i) == this.neuronGeneSet.get(j).getID())
                    {
                        babyNeuronGenes.add(this.neuronGeneSet.get(j));
                    }
                }
            }
        }

        int babyNumLayers = 0;

        for (int i = 0; i < babyNeuronGenes.size(); i++)
        {
            if (babyNeuronGenes.get(i).getNeuronLayer() > babyNumLayers)
            {
                babyNumLayers = babyNeuronGenes.get(i).getNeuronLayer();
            }
        }

        //sort neurons
        GenomeHelper.sortNeuronArray(babyNeuronGenes, babyNumLayers);

        GenomeHelper.sortLinkArray(babyNeuronGenes, babyLinkGenes);

        //create the genome.
        return new Genome(0, babyNeuronGenes, babyLinkGenes, 0, 0);

    }
    
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspA method that
	 * mutates the neuron weights within the
	 * {@code Genome}.</p>
	 * @param mutationRate The mutation rate to mutate to.
	 * @param newMutationRate The rate at which new neurons
	 * 			are created.
	 * @param changeWeight The value to modify existing weights by
	 */
    public void mutateNeuronWeights(double mutationRate, double newMutationRate, double changeWeight)
    {
        for (int i = 0; i < neuronGeneSet.size(); i++)
        {
            if (random.nextDouble() < mutationRate)
            {
                if (random.nextDouble() < newMutationRate)
                {
                    neuronGeneSet.get(i).setActivationResponse(random.nextFloat());
                }
                else
                {
                    double curWeight = neuronGeneSet.get(i).getActivationResponse();
                    double newWeight = curWeight + (random.nextDouble() * 2.0 - 1.0) * changeWeight;
					neuronGeneSet.get(i).setActivationResponse(newWeight);
                }
            }
        }
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspA method that
	 * mutates the neuron weights. This will work
	 * off of the similar method with values of .33,
	 * .1, and .1 respectively.</p>
	 * @see com.riskybusiness.genetic.Genome#mutateNeuronWeights(double, double, double)
	 */
    public void mutateNeuronWeights()
    {
        this.mutateNeuronWeights(.33D, .1D, .1D);
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspA method that
	 * mutates the link weights within the
	 * {@code Genome}.</p>
	 * @param mutationRate The mutation rate to mutate to.
	 * @param newMutationRate The rate at which new links
	 * 			are created.
	 * @param changeWeight The value to modify existing weights by.
	 */
    public void mutateLinkWeights(double mutationRate, double newMutationRate, double changeWeight)
    {
        for (int i = 0; i < linkGeneSet.size(); i++)
        {
            if (random.nextDouble() < mutationRate)
            {
                if (random.nextDouble() < newMutationRate)
                {
                    linkGeneSet.get(i).setWeight(random.nextFloat());
                }
                else
                {
                    double curWeight = linkGeneSet.get(i).getWeight();
                    double newWeight = curWeight + (random.nextDouble() * 2.0 - 1.0) * changeWeight;
                    linkGeneSet.get(i).setWeight(newWeight);
                }
            }
        }
    }
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspA method that
	 * mutates the lnk weights. This will work
	 * off of the similar method with values of .33,
	 * .1, and .1 respectively.</p>
	 * @see com.riskybusiness.genetic.Genome#mutateLinkWeights(double, double, double)
	 */
    public void mutateLinkWeights()
    {
        this.mutateLinkWeights(.33D, .1D, .1D);
    }

    public void changeNeuronType(double mutationRate, double sigmoidRate)
    {
        for (int i = 0; i < neuronGeneSet.size(); i++)
        {
            if (random.nextDouble() < mutationRate)
            {
                if (random.nextDouble() < sigmoidRate)
                {
                    neuronGeneSet.get(i).setNeuronType("Sigmoid");
                }
                else
                {
                    neuronGeneSet.get(i).setNeuronType("Step");
                }
            }
        }
    }

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspA method that
	 * changes the bias weight for every neuron
	 * based on the mutation rate.</p>
	 * @param mutationRate The rate to change the
	 * 			bias weights.
	 */
    public void changeBiasWeight(double mutationRate)
    {
        for (int i = 0; i < neuronGeneSet.size(); i++)
        {
            int links = 0;
            if (random.nextDouble() < mutationRate)
            {
                neuronGeneSet.get(i).setBiasWeight(random.nextDouble());
            }
        }
    }

	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspMutates the weights
	 * on the input layer based on the mutation rate.</p>
	 * @param mutationRate the rate to change the input
	 * 			layer weights.
	 */
    public void mutateInputLink(double mutationRate)
    {
        int linkToMutate = random.nextInt(numInputNeurons);
        if(random.nextDouble() < mutationRate)
        {
            if (random.nextDouble() < 0.1)
            {
                linkGeneSet.get(linkToMutate).setWeight(random.nextFloat());
            }
            else
            {
                double curWeight = linkGeneSet.get(linkToMutate).getWeight();
                double newWeight = curWeight + (random.nextDouble() * 2.0 - 1.0) * 0.1;
                linkGeneSet.get(linkToMutate).setWeight(newWeight);
            }
        }
    }

    /**
     * <p>&nbsp&nbsp&nbsp&nbsp&nbspMutates the weights
     * on the input layer based on the mutation rate.</p>
     * @param mutationRate the rate to change the input
     *          layer weights.
     */
    public void mutateInputNeuron(double mutationRate)
    {
        int neuronToMutate = random.nextInt(numInputNeurons);
        if(random.nextDouble() < mutationRate)
        {
            if (random.nextDouble() < 0.1)
            {
                neuronGeneSet.get(neuronToMutate).setActivationResponse(random.nextDouble());
            }
            else
            {
                double curWeight = neuronGeneSet.get(neuronToMutate).getActivationResponse();
                double newWeight = curWeight + (random.nextDouble() * 2.0 - 1.0) * 0.1;
                neuronGeneSet.get(neuronToMutate).setActivationResponse(newWeight);
            }
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

        //Add all the information to the string
        toReturn += "Genome ID: " + genomeID + " Genome has " + numLayers + " layers, has " + numInputNeurons + " input neurons, has " + numOutputNeurons + " output neurons and has " + numLinkGenes + " total Genes!\n";
        toReturn += "The neurons inside this genome are: \n";
        for (int i = 0; i < neuronGeneSet.size(); i++)
        {
            toReturn += "   Neuron ID: " + neuronGeneSet.get(i).getID() + " Neuron Layer: " + neuronGeneSet.get(i).getNeuronLayer() + " has an activation response of " + neuronGeneSet.get(i).getActivationResponse() + "\n";
        }
        toReturn += "The links inside this genome are: \n";
        for (int i = 0; i < linkGeneSet.size(); i++)
        {
            toReturn += "   Link ID: " + linkGeneSet.get(i).getID() + " comes from Neuron: " + linkGeneSet.get(i).getFromNeuron() + " and goes to Neuron: " + linkGeneSet.get(i).getToNeuron() + " and is " + ((linkGeneSet.get(i).getEnabled()) ? "enabled"  : "disabled") + " and has a weight of " + linkGeneSet.get(i).getWeight() + "\n";
        }
        toReturn += "\n";

        //Return the string
        return toReturn;
    }
}