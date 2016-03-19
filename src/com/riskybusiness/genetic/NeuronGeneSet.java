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
import com.riskybusiness.neural.StepNeuron;
import com.riskybusiness.neural.SigmoidNeuron;

import java.io.Serializable;

public class NeuronGeneSet implements Serializable //extends ArrayList
{
    
    private static final long serialVersionUID = 1L;

    private ArrayList<NeuronGene>   geneSet = new ArrayList<NeuronGene>();

    public NeuronGeneSet(ArrayList<NeuronGene> neurons)
    {
        geneSet = neurons;
    }

    public void addGene(NeuronGene gene, int pos)
    {
        //Represents the first half of the neuron gene set split on the given position
        ArrayList<NeuronGene>   firstHalf = new ArrayList<NeuronGene>();
        //Represents the last half of the neuron gene set split on the given position
        ArrayList<NeuronGene>   lastHalf  = new ArrayList<NeuronGene>();

        //Split the geneset into the first half gene set
        firstHalf.addAll(geneSet.subList(0, (pos - 1)));
        //Split the gene set into the last half gene set
        lastHalf.addAll(geneSet.subList(pos, geneSet.size()));
        //Clear the gene set to make room for the new gene
        geneSet.clear();
        //Add back the first half
        geneSet.addAll(firstHalf);
        //Add the given gene to the requested position
        geneSet.add(gene);
        //Add the last half of the gene set back
        geneSet.addAll(lastHalf);
    }

    public void add(NeuronGene gene)
    {
        //Just append the gene to the end of the gene set
        this.geneSet.add(gene);
    }

    public NeuronGene getByID(int id)
    {
        //Return the neuron gene given its ID
        for (int i = 0; i < geneSet.size(); i++)
        {
             if (geneSet.get(i)getID() == id)
             {
                return geneSet.get(i);
             }
        }
    }

    public NeuronGene get(int pos)
    {
        //Return the gene given a position in the array
        return this.geneSet.get(pos);
    }

    public int size()
    {
        //Returns the size of the neuron gene set
        return this.geneSet.size();
    }
}