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

import static java.lang.Math.abs;

import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;
import java.util.*;


public class Genome implements Serializable
{
    
    //Represents the users input
    private transient String userInput;
    //Represents the scanner to get user input
    private transient Scanner input   = new Scanner(System.in);

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
    private double                  amountToSpawn = 1;
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

    //This constructor creates a genome from an ArrayList of links, an ArrayList of neurons, an ID number, an innovation database,
    //and the number of input and output neurons
    public Genome(int id, ArrayList<NeuronGene> neurons, ArrayList<LinkGene> links, int inputs, int outputs, InnovationDB innovation)
    {
        //Represents the ID of the genome
        genomeID         = id;

        //Create a deep copy of the passed in ArrayList of neurons
        for (int i = 0; i < neurons.size(); i++)
        {
            neuronGeneSet.add(new NeuronGene(neurons.get(i).getID(), neurons.get(i).getNeuronType(), neurons.get(i).getLayerType(), false, neurons.get(i).getActivationResponse(), neurons.get(i).getNeuronLayer()));
        }

        //Create a deep copy of the passed in ArrayList of links
        for (int i = 0; i < links.size(); i++)
        {
            linkGeneSet.add(new LinkGene(links.get(i).getFromNeuron(), links.get(i).getToNeuron(), links.get(i).getID(), links.get(i).getWeight(), false));
        }

        //Set the genome parameters with information passed in
        numInputNeurons  = inputs;
        numOutputNeurons = outputs;

        //Set some parameters using some of the metadata provided by the inputs
        numLinkGenes     = links.size();
        numLayers        = neurons.get((neurons.size() - 1)).getNeuronLayer();
 
        //Add the neurons from the new genome to the innovation database
        for (int i = 0; i < neuronGeneSet.size(); i++)
        {
            innovation.addInnovation(InnovationType.NEW_NEURON, -1, -1, (i + 1));
        }
        
        //Add the links from the new genome to the innovation database
        for (int i = 0;i < linkGeneSet.size(); i++)
        {
            innovation.addInnovation(InnovationType.NEW_LINK, linkGeneSet.get(i).getFromNeuron(), linkGeneSet.get(i).getToNeuron(), -1);
        }
    }

    public Genome()
    {
        genomeID = -1;
    }

    public ArrayList<NeuronGene> getNeurons()
    {
        return this.neuronGeneSet;
    }

    public ArrayList<LinkGene> getLinks()
    {
        return this.linkGeneSet;
    }

    public int getNumInputs()
    {
        return this.numInputNeurons;
    }

    public int getNumOutputs()
    {
        return this.numOutputNeurons;
    }

    //Get the number of neurons in the genome
    public int getSizeNeuron()
    {
        return neuronGeneSet.size();
    }

    //Get the number of links in the genome
    public int getSizeLink()
    {
        return linkGeneSet.size();
    }
     
    public int getID()
    {
       return this.genomeID;
    }

    public double getAdjustedFitness()
    {
        return this.genomeAdjFitness;
    }

    public double getFitness()
    {
        return this.genomeFitness;
    }

    /**
    Fix this
    **/
    //Links theoretically know there innovation ID's, theoretically

    public int getInnovationNum(LinkGene link, InnovationDB innovation)
    {
        return innovation.getInnovationID(link.getFromNeuron(), link.getToNeuron());
    }

    public int getNumLinkGenes()
    {
        return this.numLinkGenes;
    }

    public double getNumSpawns()
    {
       return this.amountToSpawn;
    }

    //Calculates the compatibility score between this genome and another genome
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

            if (index2 == toCompare.getLinks().size() && !skip)
            {
                index1++;
                numExcess++;
                skip = true;
            }

            //If we don't need to skip then continue with the if statement
            if (!skip)
            {
                //Grab the innovation ID's of the current links
                innovID1 = this.linkGeneSet.get(index1).getID();
                innovID2 = toCompare.getLinks().get(index2).getID();

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

    public void setID(int id)
    {
       this.genomeID = id;
    }

    //Returns true if the specified link is already part of the genome
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

    //Creates a nueron from a neuron gene
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

                weights[numInputs] = 0.6f;
                return new com.riskybusiness.neural.SigmoidNeuron(weight, weights);
            }
            else if (neuron.getNeuronType().equals("Step")) 
            {
                float[] weights = new float[numInputs + 1];

                for (int i = 0; i < numInputs; i++)
                {
                    weights[i] = 1.0f;
                }

                weights[numInputs] = 0.6f;
                return new com.riskybusiness.neural.StepNeuron(weight, weights);
            }
            else
            {
                //throw some error
                return new com.riskybusiness.neural.StepNeuron(weight, numInputs);
            }
        }
    }

    //Creates a synapse from a synapse gene
    //Throws an excpetion if the link is disabled
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

        // if (!link.getEnabled()){
        //     //Throw error("You dumb idiot. You sent in a disabled link")
        // }

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
            neuronSet[toNeuronID].setWeight(linkPos, (float)link.getWeight());
            //throw some error
            return new com.riskybusiness.neural.Synapse (link.getID(), neuronSet[fromNeuronID], neuronSet[toNeuronID]);
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
    **/ 
    
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
                    //recurrent    = true;
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
                numTrysToAddLink--;
            }
        }
        //If either neuronid is less than 0 then we can't create a link so exit by returning
        if (toNeuronID < 0 || fromNeuronID < 0)
        {
            return;
        }


        
        //Why can't I just add the innovation and have my add innovation check to see if it already exists? Seems silly to seperate the two
        
        //int id = innovation.innovationExists(InnovationType.NEW_LINK, fromNeuronID, toNeuronID, -1);

        int innovationCheck = innovation.addInnovation(InnovationType.NEW_LINK, fromNeuronID, toNeuronID, -1); //Need to figure out what to do with the innovation id -1
        
        if (innovationCheck == 0 || innovationCheck == -1)
        {
            //Push the new gene into the array
            linkGeneSet.add(new LinkGene(fromNeuronID, toNeuronID, (linkGeneSet.size() + 1), random.nextDouble(), false));
            GenomeHelper.sortLinkArray(neuronGeneSet, linkGeneSet);
            numLinkGenes++;
        }
    }



    //Add a neuron to the genome dependent upon the mutation rate
    //I need to find a way to create a pointer to the innovation db
     
    public void addNeuron(double mutationRate, InnovationDB innovation, int numTrysToFindOldLink) 
    {
        //If a valid link is found to add a neuron to then this will be set to true
        boolean linkFound = false;

        //This is the ID of the chosen link to test
        int chosenLinkID = -1;
        
        //Represent the index of the chosen link 
        int chosenLink = 0;
        
        //Represents the weight of the original link
        double originalWeight;
        
        //These two variables respresent the id's of the neurons the original link connects
        int toNeuronID;
        int fromNeuronID;
        
        //This represents the maximum amount of neurons allowed in the genome
        int sizeThreshold = 30;

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
                //Prevents the chaining problem
                chosenLinkID = random.nextInt(numLinkGenes - 1 - ((int)Math.sqrt(numLinkGenes)));

                for (int j = 0; j < linkGeneSet.size(); j++)
                {
                    if (linkGeneSet.get(j).getID() == chosenLinkID)
                    {
                        chosenLink = j;
                        break;
                    }
                }

                //If the link is enabled then we have found the link we are going to disable
                if ((linkGeneSet.get(chosenLink).getEnabled()))
                {
                    linkFound = true;
                    numTrysToFindOldLink = 0;
                }

                //If we didn't find a link then exit by returning
                if (!linkFound)
                {
                    return;
                }
                
                //Disable the original link gene
                linkGeneSet.get(chosenLink).setLink(false);

                //Grab the weight of the original link
                originalWeight = linkGeneSet.get(chosenLink).getWeight();

                //Get the id's of the neurons the original link connected
                fromNeuronID = linkGeneSet.get(chosenLink).getFromNeuron();
                toNeuronID   = linkGeneSet.get(chosenLink).getToNeuron();

                //Check to see if this innovation exists in another genome
                //int id = innovation.innovationExists(NEW_LINK, toNeuronID, fromNeuronID)


                //pg. 377 provides info about a problem that may need to be implemented.

                /**
                Add link
                **/
                int innovationCheck = innovation.addInnovation(InnovationType.NEW_NEURON, fromNeuronID, toNeuronID, (neuronGeneSet.size() + 1));

                if (innovationCheck == 0 || innovationCheck == -1)
                {
                    //Determine Nueron layer
                    NeuronGene fromNeuron = new NeuronGene();

                    //Find the   
                    for (int j = 0; j < neuronGeneSet.size(); j++)
                    {
                        if (neuronGeneSet.get(j).getID() == fromNeuronID)
                        {
                           fromNeuron = neuronGeneSet.get(j);
                        }
                    }

                    //Determine the layey of the fromNeuron and add 1 to get the neuron to be added layer
                    int newNeuronLayer = fromNeuron.getNeuronLayer() + 1;

                    //Add the new neuron to the gene set
                    neuronGeneSet.add(new NeuronGene((neuronGeneSet.size() + 1), "Sigmoid", "Hidden", false, random.nextDouble(), newNeuronLayer));
                    linkGeneSet.add(new LinkGene(fromNeuronID, neuronGeneSet.size(), (linkGeneSet.size() + 1 ), 1.0, false));
                    linkGeneSet.add(new LinkGene(neuronGeneSet.size(), toNeuronID, (linkGeneSet.size() + 1), originalWeight, false));

                    //Push back any neurons that were affected by the addition
                    GenomeHelper.pushNeurons(neuronGeneSet, linkGeneSet, neuronGeneSet.get((neuronGeneSet.size() - 1)));

                    for (int j = 0; j < neuronGeneSet.size(); j++)
                    {
                        if(neuronGeneSet.get(j).getNeuronLayer() > numLayers)
                        {
                            numLayers++;
                        }
                    }

                    //System.out.println(this);

                    //Sort the neuron array
                    GenomeHelper.sortNeuronArray(neuronGeneSet, numLayers);

                    //Sort the link genes
                    GenomeHelper.sortLinkArray(neuronGeneSet, linkGeneSet);

                    //Add to the innovation database
                    innovationCheck = innovation.addInnovation(InnovationType.NEW_LINK, fromNeuronID, neuronGeneSet.size(), -1);
                    innovationCheck = innovation.addInnovation(InnovationType.NEW_LINK, neuronGeneSet.size(), toNeuronID, -1);
                    numLinkGenes++;numLinkGenes++;
                    return;
                }
                else //the innovation already exists
                {
                    //Complicated innovation stuff exists here?
                    int z = 0;
                }
            }
        }
    }

    public void setAdjustedFitness(double fitness)
    {
        this.genomeAdjFitness = fitness;
    }

    public double determineFitness()
    {
        //For this we will have to create the scenarios we talked about ealier in
        //the year and use those scenarios as inputs. We will then input these 
        //scenarios into the network and see how the network responds
        //We wil use wes' heuristic to give the neural network a fitness score 
        //based on the result of its actions


        float[]     result  = new float[]{0};

        float[][]   inputs  = new float[][]{{0},{0}};

        float[]     output  = new float[4];

        int         i       = 0;

        //Need to create inputs for situations

        //Turn the genome into a neural net and compare the output to Wes's heuristic
        //I think we are to take the output and simulate the output and feed the results
        //into the heuristic. Need more info. 

        //For practice the fitness will essentially be how close the output is to 10.
        //Because of the strict and definite nature of the fitness function we should
        //see results and the genome conforming to 1.
        // result = myNetwork.fire(inputs);
        // genomeFitness = 2 - result[0];
        // return genomeFitness;

        this.createPhenotype();

        for(float x = 0; x < 2; x++)
        {
            for(float y = 0; y < 2; y++)
            {
                output[i] = myNetwork.fire(new float[][] {new float[] {x}, new float[] {y}})[0];
                i++;
            }
        }

        genomeFitness = 0;
        genomeFitness += 1 - output[0];
        genomeFitness += 0 + output[1];
        genomeFitness += 0 + output[2];
        genomeFitness += 1 - output[3];
        // genomeFitness += 0 + output[0];
        // genomeFitness += 1 - output[1];
        // genomeFitness += 0 + output[2];
        // genomeFitness += 1 - output[3];

        //System.out.println(genomeFitness);
        return genomeFitness;

        //Compare the results to the expected output, in our case we plug the 
        //results into wes' heuristic.
    }

    public Genome crossover(Genome dad, InnovationDB innovation)
    {
       int best; //0 = Mom, 1 = Dad;
       int dadIndex = 0;
       int momIndex = 0;

       boolean selected = false;

       ArrayList<NeuronGene> babyNeuronGenes = new ArrayList<NeuronGene>();
       ArrayList<LinkGene> babyLinkGenes = new ArrayList<LinkGene>();
       ArrayList<Integer> neuronIDS = new ArrayList<Integer>();

       LinkGene selectedLink = new LinkGene();

       this.determineFitness();
       dad.determineFitness();

       //System.out.println(this.getFitness());
       //System.out.println(dad.getFitness());

        if (this.getFitness() == dad.getFitness())
        {
            if (this.getNumLinkGenes() == dad.getNumLinkGenes())
            {
                best = random.nextInt(2);
                //System.out.println(best);
            }
            else
            {
                if (this.getNumLinkGenes() < dad.getNumLinkGenes())
                {
                    //System.out.println("MOM");
                    best = 0; //Mom
                }
                else
                {
                    //System.out.println("DAD");
                    best = 1; //Dad
                }
            }
        }
        else
        {
            if (this.getFitness() > dad.getFitness())
            {
                //System.out.println("Mom has the best fitness");
                best = 0; //Mom
            }
            else
            {
                //System.out.println("Dad has the best fitness");
                best = 1; //Dad
            }
        }

        while(!((momIndex == this.linkGeneSet.size()) && dadIndex == dad.linkGeneSet.size()))
        {
            if ((momIndex == this.linkGeneSet.size() && dadIndex != dad.linkGeneSet.size()))
            {
                if (best == 1)
                {
                    //System.out.println("Selected Dad's genes");
                    selectedLink = dad.linkGeneSet.get(dadIndex);
                    selected = true;
                }

                dadIndex++;
            }
            else if ((dadIndex == dad.linkGeneSet.size()) && momIndex != this.linkGeneSet.size())
            {
                if (best == 0)
                {
                    //System.out.println("Selected Mom's genes");
                    selectedLink = this.linkGeneSet.get(momIndex);
                    selected = true;
                }

                momIndex++;
            }
            else if (this.getInnovationNum(this.linkGeneSet.get(momIndex), innovation) < dad.getInnovationNum(dad.linkGeneSet.get(dadIndex), innovation))
            {
                if (best == 0)
                {
                    //System.out.println("Selected Mom's genes");
                    selectedLink = this.linkGeneSet.get(momIndex);
                    selected = true;
                }

                momIndex++;
            }
            else if (this.getInnovationNum(this.linkGeneSet.get(momIndex), innovation) > dad.getInnovationNum(dad.linkGeneSet.get(dadIndex), innovation))
            {
                if (best == 1)
                {
                    //System.out.println("Selected Dad's genes");
                    selectedLink = dad.linkGeneSet.get(dadIndex);
                    selected = true;
                }

                dadIndex++;
            }
            else if (this.getInnovationNum(this.linkGeneSet.get(momIndex), innovation) == dad.getInnovationNum(dad.linkGeneSet.get(dadIndex), innovation))
            {
                if (random.nextDouble() < 0.5)
                {
                    //System.out.println("Selected Mom's genes");
                    selectedLink = this.linkGeneSet.get(momIndex);
                    selected = true;
                }
                else
                {
                    //System.out.println("Selected Dad's genes");
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
                    //System.out.println("Add link");
                    babyLinkGenes.add(selectedLink);
                }
                else
                {
                    //if (babyLinkGenes.get(babyLinkGenes.size() - 1) != selectedLink.getInnovationNum())
                    if(true)
                    {
                        //System.out.println("Add link");
                        babyLinkGenes.add(selectedLink);
                    }
                }

                //System.out.println("Adding neurons");
                neuronIDS.add(selectedLink.getFromNeuron());
                neuronIDS.add(selectedLink.getToNeuron());
            }
        }

        for (int i = 0; i < babyLinkGenes.size(); i++)
        {
            //System.out.println(babyLinkGenes.get(i));
        }

        //sort neurons
        //Add to the innovation database?

        //create the genome.
        return dad;

    }
    

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
                }
            }
        }
    }

    public void mutateNeuronWeights()
    {
        for (int i = 0; i < neuronGeneSet.size(); i++)
        {
            if (random.nextDouble() < 0.33)
            {
                if (random.nextDouble() < 0.1)
                {
                    neuronGeneSet.get(i).setActivationResponse(random.nextFloat());
                }
                else
                {
                    double curWeight = neuronGeneSet.get(i).getActivationResponse();
                    double newWeight = curWeight + (random.nextDouble() * 2.0 - 1.0) * 0.1;
                    neuronGeneSet.get(i).setActivationResponse(newWeight);
                }
            }
        }
    }

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

    public void mutateLinkWeights()
    {
        for (int i = 0; i < linkGeneSet.size(); i++)
        {
            if (random.nextDouble() < 0.33)
            {
                if (random.nextDouble() < 0.1)
                {
                    linkGeneSet.get(i).setWeight(random.nextFloat());
                }
                else
                {
                    double curWeight = linkGeneSet.get(i).getWeight();
                    double newWeight = curWeight + (random.nextDouble() * 2.0 - 1.0) * 0.1;
                    linkGeneSet.get(i).setWeight(newWeight);
                }
            }
        }
    }

    @Override
    public String toString()
    {
        //The string to return
        String toReturn = "";

        //Add all the information to the string
        toReturn += "Genome ID: " + genomeID + " Genome has " + numLayers + " layers, has " + numInputNeurons + " input neurons, has " + numOutputNeurons + " and has " + numLinkGenes + " total Genes!\n";
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