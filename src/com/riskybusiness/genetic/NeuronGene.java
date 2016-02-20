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
import java.io.Serializable; //Don't forget to import.

public class NeuronGene implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    //I apologize for anything I have changed that will offend you.
    private int iId;
    private String neuronType;
    private boolean bRecurrent;
    private double dActivationResponse;

    public NeuronGene(int id, String type, boolean recur, double activate)
    {
        //Is their a way to make this unique and not have to provide it? Like an autoincrement -K
        /*
         * Short answer? No.
         * Long asnwer? Well....It depends on how we implement certain aspects of not only innovation
         * database, but the recurring population as well. It is possible, but you have to be
         * persnicity, and as you can see in the NeuralNet class, this can lead to some problems.
         * -C
         */
        iId = id;
        neuronType = type; //input, hidden, bias, output, none -K
         
        //Do we want to add something to signify whether it is a step or sigmoid
        /*
         * The neuronType will be the classpath for the given neuron we want to use,
         * and it won't describe input, hidden, or output, but will describe the type
         * of neuron (SigmoidNeuron, StepNeuron). Unless you don't think that this is
         * necessitated. There is no way to say input/output/hidden/output right now, 
         * and I don't feel that it is necessary. Let's talk.
         * -C
         */
        
        //As for this sucker, I'm getting there. See the doc for the NeuralNet -C
        bRecurrent = recur;
        
        //What is this for? -C
        /*
         * Just answered my own question. See below.
         * This, with my current understanding, is either the threshold for the StepNeuron
         * or the divisor for the SigmoidNeuron. It makes sense.
         */
        dActivationResponse = activate;
    }

    //Get the id of the neuron
    public int getID()
    {
        return iId;
    }

    //Get the neuron type
    public String getNeuronType()
    {
        return neuronType;
    }

    //Set the neuron type
    public void setNeuronType(String type)
    {
        this.neuronType = type;
    }

    //Return whether the link is recurrent
    public boolean isRecurrent()
    {
        return bRecurrent;
    }
    
    /*
     * I feel that the next two methods can be
     * combined with a parameter. But it's your
     * class; do with it what you will.
     */
    //Disable the recurrency of the neuron
    public void disableRecurrency()
    {
        this.bRecurrent = false; //make sure lowercase
    }
    
    //Enables the recurrency of the neuron
    public void enableRecurrency()
    {
        this.bRecurrent = true;
    }
    
    //Get the activation response of the neuron
    public double getActivationResponse()
    {
        return dActivationResponse;
    }
    
    //Set the activation response of the neuron
    public void setActivationResponse(double activate)
    {
        this.dActivationResponse = activate;
    }
    
    //Make it return a Neuron
    public Neuron createNeuron()
    {
        //I'm not sure if this is correct -K
        //Let's make it right! -C
        //com.riskybusiness.neural.SigmoidNeuron(/*Possible issues with double vs float*/
                                                   //this.dActivationResponse,
                                                  /*We don't currently have anything to express num inputs in the gene*/
                                                  //5);
        Neuron n;
        //loops through all of the constructors for the given class and finds one with a float and int parameter combo.
        try
        {
            java.lang.reflect.Constructor[] constructors = Class.forName(neuronType).getDeclaredConstructors();
            java.lang.reflect.Constructor floatInt = constructors[0]; //initial setting
            for(java.lang.reflect.Constructor c : constructors)
            {
                Class<?>[] parameters = c.getParameterTypes();
                if(parameters.length == 2 && parameters[0] == float.class && parameters[1] == int.class)
                {
                   floatInt = c;
                   break;
                }
            }
            n = new com.riskybusiness.neural.SigmoidNeuron(4); //(Class.forName(neuronType)).cast(obj.newInstance(new Float(this.dActivationResponse), new Integer(5)));
            //I tried to implement this over 2 hours...I tried.
            return n;
        }
        catch(ReflectiveOperationException roe) //Trust me, this isn't going to be great.
        {
            roe.printStackTrace();
            return null;
        }

    }
    
}