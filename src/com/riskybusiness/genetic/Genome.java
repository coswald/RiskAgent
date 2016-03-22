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

import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;


public class Genome implements Serializable
{
    
    private static final long serialVersionUID = 1L;

    //Represents the ID of the genome
    private int                     genomeID;
    //Represents the list of links
    private ArrayList<NeuronGene>   neuronGeneSet = new ArrayList<NeuronGene>();
    //Represents the list of links
    private ArrayList<LinkGene>     linkGeneSet   = new ArrayList<LinkGene>();
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
    private int                     numGenes;
    //Represents the score given to speciate the genome
    private int                     compatibilityScore;
    //Represents the package that rpovide the genome with helper functions
    private GenomeHelper            genomeHelper = new GenomeHelper();

     //This constructor creates a genome from a vector of LinkGenes a vector of NeuronGenes and an ID number
     public Genome(int id, ArrayList<NeuronGene> neurons, ArrayList<LinkGene> links, int inputs, int outputs, InnovationDB innovation)
     {
        genomeID         = id;
        //Create a deep copy of the passed in array
        for (int i = 0; i < neurons.size(); i++)
        {
            neuronGeneSet.add(neurons.get(i));
        }
        //Create a deep copy of the passed in array
        for (int i = 0; i < links.size(); i++)
        {
            linkGeneSet.add(links.get(i));
        }
        numInputNeurons  = inputs;
        numOutputNeurons = outputs;
        numGenes         = links.size();
        numLayers        = neurons.get((neurons.size() - 1)).getNeuronLayer();

        for (int i = 0;i < neuronGeneSet.size(); i++)
        {
            innovation.addInnovation(InnovationType.NEW_NEURON, -1, -1, (i + 1));
        }
        
        //This loop does the same as above but converts the link genes into synapses
        for (int i = 0;i < linkGeneSet.size(); i++)
        {
            innovation.addInnovation(InnovationType.NEW_LINK, linkGeneSet.get(i).getFromNeuron(), linkGeneSet.get(i).getToNeuron(), -1);
        }
     }

    public void print()
    { 
        System.out.println("Genome ID: " + genomeID + " Genome has " + numLayers + " layers, has " + numInputNeurons + " input neurons, has " + numOutputNeurons + " and has " + numGenes + " total Genes!");
        System.out.println("The neurons inside this genome are: ");
        for (int i = 0; i < neuronGeneSet.size(); i++)
        {
            System.out.println("   Neuron ID: " + neuronGeneSet.get(i).getID() + " Neuron Layer: " + neuronGeneSet.get(i).getNeuronLayer());
        }
        System.out.println("The links inside this genome are: ");
        for (int i = 0; i < linkGeneSet.size(); i++)
        {
            System.out.println("   Link ID: " + linkGeneSet.get(i).getID() + " comes from Neuron: " + linkGeneSet.get(i).getFromNeuron() + " and goes to Neuron: " + linkGeneSet.get(i).getToNeuron() + " and is " + ((linkGeneSet.get(i).getEnabled()) ? "enabled!" : "disabled!"));
        }
        System.out.println();
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

    //Given a neuron id this function just finds its position in the neuron list
    // public int getElementPos(int neuronId)
    // {
    //     return 0;
    // }

    //Tests if the passed ID is the same as any existing neuron IDs. Used in AddNeuron
    public boolean alreadyHaveThisNeuronID(int ID)
    {
       return true;
    }

    //Creates a nueron from a neuron gene
    public Neuron createNeuron(NeuronGene neuron)
    {
        //Represents the number of input links to a neuron
        int numInputs = 0;
        //Loop Control Variable
        int i;

        //Set the input neurons number of inputs to 1
        if (neuron.getID() <= numInputNeurons)
        {
            numInputs = 1;
        }
        else
        {
            //Loop through the link gene set and see how many links point to the neuron ID
            for (i = 0; i < linkGeneSet.size(); i++)
            {
                if (linkGeneSet.get(i).getToNeuron() == neuron.getID())
                {
                    if (linkGeneSet.get(i).getEnabled()) 
                    {
                        numInputs++;
                    }
                }
            }    
        }

        //Create a neuron dependent on its type
        if (neuron.getNeuronType().equals("Sigmoid"))
        {
            return new com.riskybusiness.neural.SigmoidNeuron (neuron.getActivationResponse(), numInputs);
        }
        else if (neuron.getNeuronType().equals("Step")) 
        {
            return new com.riskybusiness.neural.StepNeuron (neuron.getActivationResponse(), numInputs);
        }
        else
        {
            //throw some error
            return new com.riskybusiness.neural.StepNeuron (neuron.getActivationResponse(), numInputs);
        }
    }

    //Creates a synapse from a synapse gene
    public Synapse createSynapse(LinkGene link, Neuron[] neuronSet)
    {
        //Represents the neuron the synapse is going to and coming from
        //Neuron      toNeuron        = null;
        //Neuron      fromNeuron      = null;
        int         toNeuronID = 0;
        int         fromNeuronID = 0;
        //Represents the ID of the surrent neuron to be checked
        NeuronGene  currentNeuron;
        //Represents whether the neurons have been found and set yet
        boolean     toNeuronIsSet   = false;
        boolean     fromNeuronIsSet = false;
        //Loop Control Variable
        int         i;
        //Turns to false after the first link has been found
        boolean     firstLink       = true;
        //Represetns the position of the link in the neuron
        int         linkPos         = 0;

        for (i = 0;i < neuronGeneSet.size(); i++)
        {
            currentNeuron = neuronGeneSet.get(i);
            //What happens if the link is recursive??
            //if (link.isRecurrent)
            //{
            //  if (currentNeuron.getID() == link.getToNeuron())
            //{
            //    toNeuron = currentNeuron;
            //    fromNeuron = currentNeuron;
            //}
            //} else {
            //What if the link doesn't know its recursive??
            if (currentNeuron.getID() == link.getToNeuron() && currentNeuron.getID() == link.getFromNeuron())
            {
                // toNeuron = createNeuron(currentNeuron);
                // fromNeuron = createNeuron(currentNeuron);
                toNeuronID = i;
                fromNeuronID = i;
                toNeuronIsSet = true;
                fromNeuronIsSet = true;
            } 
            else if (currentNeuron.getID() == link.getToNeuron())
            {
                //toNeuron = createNeuron(currentNeuron);
                toNeuronID = i;
                toNeuronIsSet = true;
            }
            else if (currentNeuron.getID() == link.getFromNeuron())
            {
                fromNeuronID = i;
                fromNeuronIsSet = true;
            }
            if (toNeuronIsSet && fromNeuronIsSet)
            {
                break;
            }
            //}
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
                        //Do I need to check for a disabled link here?
                        linkPos++;
                    }
                }
            }
            //System.out.println("Created Link: " + link.getID() + " with a link pos of: " + linkPos);
            return new com.riskybusiness.neural.Synapse (linkPos, neuronSet[fromNeuronID], neuronSet[toNeuronID]);
        }
        else
        {
            //throw some error
            return new com.riskybusiness.neural.Synapse (link.getID(), neuronSet[fromNeuronID], neuronSet[toNeuronID]);
        }
        
    }
     
    /**
     * <p>This function converts the respective genome into a 
     * neural network.</p>
     **/
    public NeuralNet createPhenotype()
    {
        //Variables
        //These arrays hold the actual neurons and synapses of the neural network
        Synapse[] linkSet = new Synapse[linkGeneSet.size()];
        Neuron[] neuronSet = new Neuron[neuronGeneSet.size()];
        
        //Loop Control Variable
        int i;

        //This loop loops through the global neuronGeneSet and creates neurons from these genes
        //and then adds them to the neuronSet
        for (i = 0;i < neuronGeneSet.size(); i++)
        {
            neuronSet[i] = createNeuron(neuronGeneSet.get(i));
        }
        
        //This loop does the same as above but converts the link genes into synapses
        for (i = 0;i < linkGeneSet.size(); i++)
        {
            linkSet[i] = createSynapse(linkGeneSet.get(i), neuronSet);
        }

        //Combines the neurons and synapses into a neural network and returns the network
        return new com.riskybusiness.neural.NeuralNet(neuronSet, linkSet);
    }


    //Delete the neural network
    //public void deletePhenotype(){?}


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

        //This variable determines if the link is recurrent.
        /**
        Not Currently implemented
        **/
        boolean recurrent = false;



        //If the random value doesn't exceed the probability threshold then exit by returning
        if (random.nextDouble() > mutationRate)
        {
            return;
        }

        //If we made it here then we are going to attempt to mutate the genome
        //If the random value exceeds the chance of a looped link then attempt to create a looped link
        if (random.nextDouble() > chanceOfLooped)
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
                int neuronIndex;
                //Find two random neurons and determine if a link can be made between them
                //Needs to be inbetween 0 and the size of the neuron array - 1
                neuronIndex  = random.nextInt(neuronGeneSet.size() - 1); 
                fromNeuronID = neuronGeneSet.get(neuronIndex).getID();
                //Needs to be inbetween the number of input neurons + 1 and the size of the neuron array - 1
                neuronIndex = random.nextInt(neuronGeneSet.size() - numInputNeurons - 1) + numInputNeurons;
                toNeuronID   = neuronGeneSet.get(neuronIndex).getID();

                //The book had this and I don't know why?
                //if(toNeuronID == 2)
                //{
                //continue?
                //}
                //Check to see if a link exists and if it doesn't then break from the loop else continue looping
                if(!(duplicateLink(fromNeuronID,toNeuronID)) || (fromNeuronID == toNeuronID))
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

        //The algortihm in the book uses the y values to determine if the link is recurrent so im gonna skip this part
        /**
        Complete the code to determine if a link is recurrent
        **/

        int innovationCheck = innovation.addInnovation(InnovationType.NEW_LINK, toNeuronID, fromNeuronID, -1); //Need to figure out what to do with the innovation id -1
        if (innovationCheck == 0)
        {
            //Push the new gene into the array
            linkGeneSet.add(new LinkGene(fromNeuronID, toNeuronID, (linkGeneSet.size() + 1), random.nextDouble(), recurrent));
            numGenes++;
        }
    }



    //Add a neuron to the genome dependent upon the mutation rate
    //I need to find a way to create a pointer to the innovation db
     
    public void addNeuron(double mutationRate, InnovationDB innovation, int numTrysToFindOldLink) 
    {
        //This variable describes a function to create random numbers
        Random random = new Random();
        //If a valid link is found to add a neuron to then this will be set to true
        boolean linkFound = false;
        //This is the index of the chosen link to test
        int chosenLink = 0;
        //Represents the weight of the original link
        double originalWeight;
        //These two variables respresent the id's of the neurons the original link connects
        int toNeuronID;
        int fromNeuronID;

        this.print();

        //If the random value doesn't exceed the probability threshold then exit by returning
        if (random.nextDouble() > mutationRate)
        {
            return;
        }

        int sizeThreshold = numInputNeurons + numOutputNeurons + 500;

        //Not quite sure what the size threshold is yet but it prevents the chaining effect so I will implement it
        if (linkGeneSet.size() < sizeThreshold)
        {
            //Loop through and try to find an old link to add a neuron to
            for (int i = numTrysToFindOldLink; i > 0; i--)
            {
                //Prevents the chaining problem
                chosenLink = random.nextInt(numGenes - 1 - ((int)Math.sqrt(numGenes)));

                //int fromNeuron =  linkGeneSet.get(chosenLink).getFromNeuron(); //?

                System.out.println("Link :" + chosenLink);

                if ((linkGeneSet.get(chosenLink).getEnabled())) //&&
                    //(!linkGeneSet.get(chosenLink).getRecurrent()))
                    //(neuronGeneSet.get(fromNeuron).getNeuronType != bias)) can't be a bias gene
                {
                    linkFound = true;

                    numTrysToFindOldLink = 0;
                }

                //If we didn't find a link then exit by returning
                if (!linkFound)
                {
                    return;
                }
                //I'm not entirely sure why this else statement is here
                // else
                // {
                //     while(!linkFound)
                //     {
                //         chosenLink = random.nextInt(numGenes - 1); //needs to be a number between 0 and the number of genes - 1

                //         //Check that the link is enabled and not recurrent
                //         //If the link is enabled and not recurrent then we have found a candidate
                //         if ((linkGeneSet.get(chosenLink).getEnabled())) //&&
                //             //(linkGeneSet.get(chosenLink).getRecurrent()))
                //         {
                //             linkFound = true;
                //         }
                //     }
                // }

                //Disable the original link gene
                linkGeneSet.get(chosenLink).setLink(false);

                //Grab the weight of the original link
                originalWeight = linkGeneSet.get(chosenLink).getWeight();

                //Get the id's of the neurons the original link connected
                fromNeuronID = linkGeneSet.get(chosenLink).getFromNeuron();
                toNeuronID   = linkGeneSet.get(chosenLink).getToNeuron();

                //If we want to add the x and y coords we could do so here// pg. 376

                //Check to see if this innovation exists in another genome
                //int id = innovation.innovationExists(NEW_LINK, toNeuronID, fromNeuronID)


                //pg. 377 provides info about a problem that may need to be implemented.

                /**
                Add link
                **/
                int innovationCheck = innovation.addInnovation(InnovationType.NEW_NEURON, fromNeuronID, toNeuronID, (neuronGeneSet.size() + 1));

                if (innovationCheck == 0)
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
                    neuronGeneSet.add(new NeuronGene((neuronGeneSet.size() + 1), "Sigmoid", false, random.nextFloat(), newNeuronLayer));
                    linkGeneSet.add(new LinkGene(fromNeuronID, neuronGeneSet.size(), (linkGeneSet.size() + 1 ), 1.0, false));
                    linkGeneSet.add(new LinkGene(neuronGeneSet.size(), toNeuronID, (linkGeneSet.size() + 1), originalWeight, false));

                    //Push back any neurons that were affected by the addition
                    numLayers = genomeHelper.pushNeurons(neuronGeneSet, linkGeneSet, neuronGeneSet.get((neuronGeneSet.size() - 1)), numLayers);
                    //Sort the neuron array
                    genomeHelper.sortNeuronArray(neuronGeneSet, numLayers);

                    //Sort the link genes
                    genomeHelper.sortLinkArray(neuronGeneSet, linkGeneSet);

                    innovationCheck = innovation.addInnovation(InnovationType.NEW_LINK, fromNeuronID, neuronGeneSet.size(), -1);
                    innovationCheck = innovation.addInnovation(InnovationType.NEW_LINK, neuronGeneSet.size(), toNeuronID, -1);
                    numGenes++;numGenes++;
                    System.out.println("Created Stuff");
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
     
     //Function to mutate the connection weights
     //public void mutateWeights(double mutationRate, double probNewMutation, double dMaxPertubation){?}

     //Perturbs the activation responses of the neurons
     //public void mutateActivationResponse(double mutationRate, double MaxPertubation){?}

     //Calculates the compatibility score between this genome and another genome
     public double getCompatibilityScore(Genome toCompare)
     {
        return 1.0;
     }
     
     //Not sure
     //public void sortGenes();

    // //Represents the ID     compatibilityScore;
     
    public int getID()
    {
       return this.genomeID;
    }

    public void setID(int id)
    {
       this.genomeID = id;
    }

    public double getAdjustedFitness()
    {
        return this.genomeAdjFitness;
    }

    public void setAdjustedFitness(double fitness)
    {
        this.genomeAdjFitness = fitness;
    }

    public double getFitness()
    {
       return this.genomeFitness;
    }

    public void determineFitness()
    {
        NeuralNet   toFire  = this.createPhenotype();

        float[]     result  = new float[]{0};

        float[][]   inputs  = new float[][]{{0},{0}};

        //Turn the genome into a neural net and compare the output to Wes's heuristic
        //I think we are to take the output and simulate the output and feed the results
        //into the heuristic. Need more info. 

        //For practice the fitness will essentially be how close the output is to 10.
        //Because of the strict and definite nature of the fitness function we should
        //see results and the genome conforming to 1.
        result = toFire.fire(inputs);
        System.out.println("Results: " + result[0]);
    }

    public double getNumSpawns()
    {
       return this.amountToSpawn;
    }

    public Genome makeBabies(Genome dad)
    {
       return dad;
    }

    public ArrayList<NeuronGene> getNeurons()
    {
        return this.neuronGeneSet;
    }

    public ArrayList<LinkGene> getLinks()
    {
        return this.linkGeneSet;
    }
}