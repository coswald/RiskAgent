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
import java.util.*; //NOTE: I HATE this. But that's okay. -C
//Whats the proper way, I just saw it on a website and it worked. -K

public class Genome implements Serializable
{
    
    private static final long serialVersionUID = 1L;

    private int m_GenomeID;
    /*
     * Okay, I'll start off by saying that a "Vector"
     * in Java should almost always be an ArrayList.
     * That being stated, you also need to state the
     * type of the elements inside the ArrayList. So
     * i'll change it for you.
     */
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
        //Idk what to do yet; -K
        //null;
        return true;
    }

    //Given a neuron id this function just finds its position in m_vecNeurons
    public int getElementPos(int neuronId)
    {
        //Nope -K
        //null;
        return 0;
    }

    //Tests if the passed ID is the same as any existing neuron IDs. Used in AddNeuron
     public boolean alreadyHaveThisNeuronID(int ID) //can't have const
     {
         //Meh? -K
         //null;
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
            //if (currentNeuron.getID() == link.getToNeuron() && currentNeuron.getID() == link.getFoNeuron()){
            //    toNeuron = currentNeuron;
            //    fromNeuron = currentNeuron;
            //    link.setRecurrency(true); 
            //} else
            if (currentNeuron.getID() == link.getToNeuron())
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
            null; //Raise some kind of missing neuron error.
        }
        
    }
     //We'll talk about these -C
     
     //This constructor creates a genome from a vector of SLinkGenes a vector of SNeuronGenes and an ID number
     //public Genome(int id, Vector neurons, Vector genes, int inputs, int outputs);

     //Create a neural network from the genome
     public NeuralNet createPhenotype(int depth)
     {
        //Variables
        private ArrayList<Neuron>  neuronSet; //Do these need to be array's and not array lists?
        private ArrayList<Synapse> linkSet;   //------------------------------------------------

        for (i = 0;i < neuronGeneSet.size(); i++)
        {
            neuronSet.add(i, createNeuron(neuronGeneSet.get(i)));
        }
        for (j = 0;j < linkGeneSet.size(); j++)
        {
            linkSet.add(j, createSynapse(linkGeneSet.get(j)));
        }
        return new com.riskybusiness.neural.NeuralNet(neuronSet, linkSet);
     }



     //Delete the neural network
     //public void deletePhenotype(){?}

     //Add a link to the genome dependent upon the mutation rate
     //public void addLink(double mutationRate, double chanceOfRecurrent, CInnovation innovation?, int numeTrysToFindLoop, int numTrysToAddLink){?}

     //Add a neuron to the genome dependent upon the mutation rate
     //public void addNeuron(double mutationRate, Innovation &innovation?, int numTrysToFindOldLink){?}

     //Function to mutate the connection weights
     //public void mutateWeights(double mutationRate, double probNewMutation, double dMaxPertubation){?}

     //Perturbs the activation responses of the neurons
     //public void mutateActivationResponse(double mutationRate, double MaxPertubation){?}

     //Calculates the compatibility score between this genome and another genome
     //public double getCompatibilityScore(const CGenome &genome?);
     
     //Not sure
     //public void sortGenes();

}