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
import java.io.Serializable;
import java.util.Random;

public class Genome implements Serializable
{
    
    private static final long serialVersionUID = 1L;

    private int                   genomeID;
    private ArrayList<NeuronGene> neuronGeneSet;
    private ArrayList<LinkGene>   linkGeneSet;
    private double                genomeFitness;
    private double                genomeAdjFitness;
    private double                amountToSpawn;
    private int                   numInputs;
    private int                   numOutputs;
    private int                   species;

    //Returns true if the specified link is already part of the genome
    public boolean duplicateLink(int neuronIn, int neuronOut)
    {
        return true;
    }

    //Given a neuron id this function just finds its position in m_vecNeurons
    public int getElementPos(int neuronId)
    {
        return 0;
    }

    //Tests if the passed ID is the same as any existing neuron IDs. Used in AddNeuron
     public boolean alreadyHaveThisNeuronID(int ID)
     {
         return true;
     }

    public Neuron createNeuron(NeuronGene neuron)
    {
        //Need to figure out a way to determine amount of inputs. If I know my ID I
        //may be able to look and see how many active links point to me but we would 
        //have to account for links not yet created so maybe create neurons last.
        if (neuron.getNeuronType() == "Sigmoid")
        {
            return new com.riskybusiness.neural.SigmoidNeuron (neuron.getActivationResponse(), 5);
        }
        else if (neuron.getNeuronType() == "Step") 
        {
            return new com.riskybusiness.neural.StepNeuron (neuron.getActivationResponse(), 5);
        }
    }

    public Synapse createSynapse(LinkGene link)
    {
        //Variables
        private Neuron toNeuron;
        private Neuron fromNeuron;
        private Neuron currentNeuron;

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
            if (currentNeuron.getID() == link.getToNeuron() && currentNeuron.getID() == link.getFoNeuron())
            {
                toNeuron = currentNeuron;
                fromNeuron = currentNeuron;
            } 
            else if (currentNeuron.getID() == link.getToNeuron())
            {
                toNeuron = createNeuron (currentNeuron);
                toNeuronSet = true;
            }
            else if (currentNeuron.getID() == link.getFromNeuron())
            {
                fromNeuron = createNeuron (currentNeuron);
                fromNeuronSet = true;
            }
            if (toNeuronIsSet && fromNeuronIsSet)
            {
                break;
            }
            //}
        }
        if (toNeuronIsSet && fromNeuronIsSet)
        {
            return new com.riskybusiness.neural.Synapse (link.getID(), fromNeuron, toNeuron);
        }
        else
        {
            null;
        }
        
    }
     
     //This constructor creates a genome from a vector of SLinkGenes a vector of SNeuronGenes and an ID number
     //public Genome(int id, Vector neurons, Vector genes, int inputs, int outputs);


     /**
      * <p>This function converts the respective genome into a 
      * neural network.</p>
      **/
     public NeuralNet createPhenotype()
     {
        //Variables
        //These arrays hold the actual neurons and synapses of the neural network
        private ArrayList<Neuron>  neuronSet; //Do these need to be array's and not array lists?
        private ArrayList<Synapse> linkSet;   //------------------------------------------------

        //This loop loops through the global neuronGeneSet and creates neurons from these genes
        //and then adds them to the neuronSet
        for (i = 0;i < neuronGeneSet.size(); i++)
        {
            neuronSet.add(i, createNeuron(neuronGeneSet.get(i)));
        }
        
        //This loop does the same as above but converts the link genes into synapses
        for (j = 0;j < linkGeneSet.size(); j++)
        {
            linkSet.add(j, createSynapse(linkGeneSet.get(j)));
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

    
     public void addLink(double mutationRate, double chanceOfLooped, CInnovation innovation, int numTrysToFindLoop, int numTrysToAddLink)
     {
        //This variable describes a function to create random numbers
        Random random = new Random();
        //This variable contains a random value used to determine if a link or loop should happen
        double randomValue = randomGenerator.nextDouble();
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
            while(numTrysToFindLoop--)
            {
                /**
                //This needs to be a number between the number of inputs + 1 and the size of the neuron arraylist - 1
                //Also will there be type issues with double and int will fix
                **/
                int neuronIndex = random.nextDouble();

                /**
                We need to find a way to determine if a neuron is input, output, etc. I may add this to the neuron gene
                This if statement is supposed to ensure that the gene we are adding a looped link to isn't an input gene or bias gene
                **/
                if (!neuronGeneSet.get(neuronIndex))
                {
                    toNeuronID   = neuronGeneSet.get(neuronIndex).getID;
                    fromNeuronID = neuronGeneSet.get(neuronIndex).getID;
                    recurrent    = true;
                    //If we find a good neuron that satisfies our conditions then we don't need to loop anymore
                    numTrysToFindLoop = 0;
                }
            }
        }
        //If the random value didn't exceed the chance to create a looped link then we still need to mutate the genome
        else
        {
            //This loop will loop through and try to create a link until it has created a link or has exceed its number of tries
            while(numTrysToAddLink--)
            {
                //Find two random neurons and determine if a link can be made between them
                //Needs to be inbetween 0 and the size of the neuron array - 1
                neuronIndex  = random.nextDouble(); 
                fromNeuronID = neuronGeneSet.get(neuronIndex).getId;
                //Needs to be inbetween the number of input neurons + 1 and the size of the neuron array - 1
                neuronIndex  = random.nextDouble();
                toNeuronID   = neuronGeneSet.get(neuronIndex).getID;

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
            }
        }
        //If either neuronid is less than 0 then we can't create a link so exit by returning
        if (toNeuronID < 0 || fromNeuronID < 0)
        {
            return;
        }


        /**
        Why can't I just add the innovation and have my add innovation check to see if it already exists? Seems silly to seperate the two
        **/
        int id = innovation.innovationExists(NEW_LINK, fromNeuronID, toNeuronID, -1);

        //The algortihm in the book uses the y values to determine if the link is recurrent so im gonna skip this part
        /**
        Complete the code to determine if a link is recurrent
        **/

        if (id < 0)
        {
            innovation.addInnovation(NEW_LINK, fromNeuronID, toNeuronID);
            int id = innovation.nextNumber() - 1;
            LinkGene newLinkGene = LinkGene(fromNeuronID, toNeuronID, id, random.nextDouble(), recurrent);
            //Push the new gene into the array?
            /**
             I'm gonna wait until I know what order the genes are in or if it matters
            **/
        }

     }



     //Add a neuron to the genome dependent upon the mutation rate
     //I need to find a way to create a pointer to the innovation db
     public void addNeuron(double mutationRate, Innovation innovation, int numTrysToFindOldLink) 
     {
        //This variable describes a function to create random numbers
        Random random = new Random();
        //This variable contains a random value used to determine if a link or loop should happen
        double randomValue = randomGenerator.nextDouble();
        //If a valid link is found to add a neuron to then this will be set to true
        boolean linkFound = false;
        //This is the index of the chosen link to test
        int chosenLink = 0;
        //Represents the weight of the original link
        double originalWeight;
        //These two variables respresent the id's of the neurons the original link connects
        int toNeuronID;
        int fromNeuronID;


        //If the random value doesn't exceed the probability threshold then exit by returning
        if (random.nextDouble() > mutationRate)
        {
            return;
        }

        //Not quite sure what the size threshold is yet but it prevents the chaining effect so I will implement it
        if (linkGeneSet.size() < sizeThreshold)
        {
            //Loop through and try to find an old link to add a neuron to
            while(numTrysToFindOldLink--)
            {
                /**
                This needs to be a value between 0 and some weird number that I will determine later
                **/
                chosenLink = random.nextDouble();

                int fromNeuron =  linkGeneSet.get(chosenLink).fromNeuron;

                if ((linkGeneSet.get(chosenLink).getEnabled) &&
                    (!linkGeneSet.get(chosenLink).getRecurrent) &&
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
                else
                {
                    while(!linkFound)
                    {
                        chosenLink = radnom.nextDouble(); //needs to be a number between 0 and the number of genes - 1

                        //Grab the neuron attached to the link
                        int fromNeuron = linkGeneSet.get(chosenLink); 

                        //Check that the link is enabled and not recurrent
                        //If the link is enabled and not recurrent then we have found a candidate
                        if ((linkGeneSet.get(chosenLink).getEnabled()) &&
                            (linkGeneSet.get(chosenLink).getRecurrent()))
                        {
                            linkFound = true;
                        }
                    }
                }

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

                /**
                Add link
                **/





                    }
                }
                




            }
        }






     }

     //Function to mutate the connection weights
     //public void mutateWeights(double mutationRate, double probNewMutation, double dMaxPertubation){?}

     //Perturbs the activation responses of the neurons
     //public void mutateActivationResponse(double mutationRate, double MaxPertubation){?}

     //Calculates the compatibility score between this genome and another genome
     //public double getCompatibilityScore(const CGenome &genome?);
     
     //Not sure
     //public void sortGenes();
}