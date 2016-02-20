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
    private ArrayList<Neuron> m_vecNeurons;
    private ArrayList<Synapse> m_vecLinks;
    //Phenotype?
    private double m_dFitness;
    private double m_dAdjustedFitness;
    private double m_dAmountToSpawn;
    private int m_iNumInputs;
    private int m_iNumOutputs;
    private int m_iSpecies;

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

     //We'll talk about these -C
     
     //This constructor creates a genome from a vector of SLinkGenes a vector of SNeuronGenes and an ID number
     //public Genome(int id, Vector neurons, Vector genes, int inputs, int outputs);

     //Create a neural network from the genome
     //public NeuralNet createPhenotype(int depth){?}

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