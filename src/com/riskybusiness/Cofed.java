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
 * MERCHANTABILITY or fitness FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.riskybusiness;

import com.riskybusiness.LuxAgentAdapter;
import com.riskybusiness.neural.NeuralNet;
import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;
import com.sillysoft.lux.agent.EvilPixie;
import java.util.ArrayList;
import java.util.Random;

/**
 * <p>&nbsp&nbsp&nbsp&nbspThe {@code Cofed} agent uses ANNs to play
 * a game of RISK.</p>
 * @author Weston Miller
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @version 1.0
 * @see com.riskybusiness.LuxAgentAdapter
 * @since 1.6
 */
public class Cofed extends LuxAgentAdapter
{
    protected NeuralNet nn;

    public Cofed()
    {
    }

    @Override
    public void setPrefs(int ID, Board board)
    {
        super.setPrefs(ID, board);
        this.agent = new EvilPixie();
        this.agent.setPrefs(ID, board);
	//Setup NeuralNet

	/*
	 * Finds the number of country neurons.
	 * Each country must have a CountryID,
	 * a ContinentID, a PlayerID, the number
	 * of troops in that country, and whether
	 * or not we can attack it. 6 total inputs
	 * for each country. In the network, these
	 * inputs are each their own neuron,
	 * ordered in the manner presented above.
	 */
	int inputNeurons = this.countries.length * 6;

	/*
	 * Finds the number of player neurons.
	 * These are after the country neurons
	 * and take the same order that they do.
	 * Each player needs a PlayerID, the amount
	 * of cards that player has, and the amount
	 * of reinforcements that player has per
	 * turn.
	 */
	inputNeurons += this.board.getNumberOfPlayers() * 3;

	/*
	 * Simple algorithm to calculate how many
	 * bits it will take to represent the amount
	 * of countries given to the board. Then add
	 * one to say attack or not. This will be
	 * the output layer of the network.
	 */
	int outputNeurons = 1;
	int countryCounter = this.countries.length;
	while(countryCounter > 0)
	{
	    outputNeurons++;
	    countryCounter = countryCounter >> 1;
	}

	//Create a neural network
	//For now, the network will have 6 layers
	//in total. All four of the hidden layers
	//will contain 12 neurons for consistencies
	//sake.
	this.nn = new NeuralNet(inputNeurons, outputNeurons, 12, 12, 12, 12);
    }

    private int[] attackHeuristic(Country country)
    {
	//setup variables.
        int fitness = 0;
        int attackValue = 0; // Default to not attack
	boolean canAttack = false;
	outer:
	for(int i : country.getHostileAdjoiningCodeList())
	{
	    for(Country j : this.countries)
	    {
	        if(j.getOwner() == this.ID && j.getCode() != i && j.canGoto(i))
		{
		    canAttack = true;
		    break outer;
		}
	    }
	}
	int ownTroops = BoardHelper.getPlayerArmies(this.ID, this.countries);
	int ownCards = this.board.getPlayerCards(this.ID);
	int enemyTroops = country.getArmies();
  int enemyCards = this.board.getPlayerCards(country.getOwner());
  boolean opponentLastCountry = (BoardHelper.getPlayerCountries(country.getOwner(), this.countries) == 1) ? true : false;
  int ownReinforcementsPerTurn = this.getPlayerIncome(this.ID);
  int enemyReinforcementsPerTurn = this.getPlayerIncome(country.getOwner());

        if(canAttack)
        {
            if(ownTroops == enemyTroops)
            {
                if(ownTroops >= 15) // Odds of victory exceed 60%
                {
                    attackValue += 1; //Attack
                    fitness += 8;
                }
                else if(ownTroops >= 5) // Odds of victory between 50% and 60%
                {
                    if(opponentLastCountry)
                    {
                        if((ownCards + enemyCards) >= 5)
                        {
                            attackValue += 1; //Attack
                            fitness += 6;
                        }
                        else if(enemyReinforcementsPerTurn >= ownReinforcementsPerTurn)
                        {
                            attackValue += 1; //Attack
                            fitness += 6;
                        }
                        else if(OpponentThatCanAttackThemTroops > enemyTroops &&
                        (OpponentThatCanAttackThemCards + enemyCards) >= 5)
                        {
                            attackValue += 1; //Attack
                            fitness += 5;
                        }
                        else
                        {
                            //Don't Attack
                            fitness += 4;
                        }
                    }
                    else if(BreakContinentBonus) // Break bonus troop gains!
                    {
                        attackValue += 1; //Attack
                        fitness += 7;
                    }
                    else // Save your troops and build up
                    {
                        //Don't Attack
                        fitness += 5;
                    }
                }
                else // Odds of victory are in favor of defender
                {
                    //Don't Attack
                    fitness += 6;
                }
            }
            else if(ownTroops > enemyTroops)
            {
                if((ownTroops - enemyTroops) >= 10) // The lowest odds are high with this....
                {
                    attackValue += 1; //Attack
                    fitness += 10; // Best Scenario!
                }
                else if((ownTroops - enemyTroops) >= 6) // The lowest odds are high with this....
                {
                    attackValue += 1; //Attack
                    fitness += 8; // Great Scenario!
                }
                else if((ownTroops - enemyTroops) >= 2) // The lowest odds are at 68% with this....
                {
                    attackValue += 1; //Attack
                    fitness += 6;
                }
                else // Only one more- odds are high-ish, but not quite exceptional.
                {
                    if(opponentLastCountry)
                    {
                        if((ownCards + enemyCards) >= 5)
                        {
                            attackValue += 1; //Attack
                            fitness += 6;
                        }
                        else if(enemyReinforcementsPerTurn >= ownReinforcementsPerTurn)
                        {
                            attackValue += 1; //Attack
                            fitness += 5;
                        }
                        else if(OpponentThatCanAttackThemTroops > enemyTroops &&
                        (OpponentThatCanAttackThemCards + enemyCards) >= 5)
                        {
                            attackValue += 1; //Attack
                            fitness += 4;
                        }
                        else
                        {
                            //Don't Attack
                            fitness += 3;
                        }
                    }
                    else if(BreakContinentBonus) // Break bonus troop gains!
                    {
                        attackValue += 1; //Attack
                        fitness += 7;
                    }
                    else // Save and build up troops
                    {
                        //Don't Attack
                        fitness += 2;
                    }
                }
            }
            else // Opponent has more troops- so odds are never exceptional
            {
                if((enemyTroops - ownTroops) <= 2) //There is hope!
                {
                    if(ownTroops >= 20) // The Power of the Three Dice In full!
                    {
                        attackValue += 1; //Attack
                        fitness += 7;
                    }
                    else if(ownTroops >= 13) // Odds are even at this point
                    {
                        if(opponentLastCountry)
                        {
                            if((ownCards + enemyCards) >= 5)
                            {
                                attackValue += 1; //Attack
                                fitness += 6;
                            }
                            else if(enemyReinforcementsPerTurn >= ownReinforcementsPerTurn)
                            {
                                attackValue += 1; //Attack
                                fitness += 5;
                            }
                            else if(OpponentThatCanAttackThemTroops > enemyTroops &&
                            (OpponentThatCanAttackThemCards + enemyCards) >= 5)
                            {
                                attackValue += 1; //Attack
                                fitness += 4;
                            }
                            else
                            {
                                //Don't Attack
                                fitness += 4;
                            }
                        }
                        else if(BreakContinentBonus) // Break bonus troop gains!
                        {
                            attackValue += 1; //Attack
                            fitness += 6;
                        }
                        else // Save and build up troops
                        {
                            //Don't Attack
                            fitness += 5;
                        }
                    }
                }
                else // The odds are almost never in your favor
                {
                    //Don't Attack
                    fitness += 9;
                }
            }
        }

        return new int[] {id, fitness, attackValue};
    }

    private int defenseHeuristic(id)
    {
        private int fitness = 0;
        private int attackValue = 0; // Default to not attack

        if (canAttack(id))
        {
            if (PlayerID == 0)
            {

                if (ownTroops == enemyTroops)
                {
                    if (ownTroops <= 5)
                    {
                        if (OwnLastCountry)
                        {
                            if ((ownCards + enemyCards) >= 5)
                            {
                                //Don't Attack
                                fitness += 4;
                            }
                            else if (enemyReinforcementsPerTurn <= ownReinforcementsPerTurn)
                            {
                                //Don't Attack
                                fitness += 6;
                            }
                            else
                            {
                                //Don't Attack
                                fitness += 5;
                            }
                        }
                        else if (BreakContinentBonus)
                        {
                            //Don't Attack
                            fitness += 3;
                        }

                        else
                        {
                            //Don't Attack
                            fitness += 7;
                        }
                    }

                    else if (ownTroops <= 15)
                    {
                        attackValue += 1; //Attack
                        fitness += 5;
                    }

                    else
                    {
                        attackValue += 1; //Attack
                        fitness += 3;
                    }
                }

                else if (ownTroops > enemyTroops)
                {
                    if ((ownTroops - enemyTroops) >= 10)
                    {
                        attackValue += 1; //Attack
                        fitness += 10;
                    }

                    else if ((ownTroops - enemyTroops) >= 6)
                    {
                        attackValue += 1; //Attack
                        fitness += 8;
                    }

                    else if ((ownTroops - enemyTroops) >= 2)
                    {
                        attackValue += 1; //Attack
                        fitness += 6;
                    }

                    else
                    {
                        if (OwnLastCountry)
                        {
                            if ((ownCards + enemyCards) >= 5)
                            {
                                //Don't Attack
                                fitness += 4;
                            }
                            else if (enemyReinforcementsPerTurn <= ownReinforcementsPerTurn)
                            {
                                attackValue += 1; //Attack
                                fitness += 5;
                            }
                            else
                            {
                                //Don't Attack
                                fitness += 5;
                            }
                        }

                        else if (BreakContinentBonus)
                        {
                            //Don't Attack
                            fitness += 4;
                        }

                        else
                        {
                            //Don't Attack
                            fitness += 6;
                        }
                    }
                }

                else
                {
                    if ((enemyTroops - ownTroops) >= 2)
                    {
                        if (enemyTroops >= 20)
                        {
                            //Don't Attack
                            fitness += 3;
                        }

                        else if (enemyTroops >= 13)
                        {
                            if (OwnLastCountry)
                            {
                                if ((ownCards + enemyCards) >= 5)
                                {
                                    //Don't Attack
                                    fitness += 2;
                                }

                                else if (enemyReinforcementsPerTurn <= ownReinforcementsPerTurn)
                                {
                                    //Don't Attack
                                    fitness += 5;
                                }

                                else
                                {
                                    attackValue += 1; //Attack
                                    fitness += 6;
                                }
                            }

                            else if (BreakContinentBonus)
                            {
                                //Don't Attack
                                fitness += 4;
                            }

                            else
                            {
                                //Don't Attack
                                fitness += 5;
                            }
                        }
                    }

                    else
                    {
                        attackValue += 1; //Attack
                        fitness += 9;
                    }
                }
            }
        }

        return (id, fitness, attackValue);
    }

    @Override
    public void attackPhase()
    {
        // declare lists to save the results of the Heuristics
        // stored in form (countryID, fitnessValue, attackValue)
        //ArrayList<Integer[]> attackList = new ArrayList<Integer[]>();
        ArrayList<Integer[]> attackList = new ArrayList<Integer[]>();
        ArrayList<Integer[]> defenseList = new ArrayList<Integer[]>();
        // Gather data from board



        // Run & Save Heuristics
        for (int id = 0; id < board.(countries.length()); id++)
        {
            if (canAtack(countries[id])) //if the country borders
            {
                // Run attackHeuristic, save results to attackList
                //attackList.add(new Integer(attackHeuristic(id)))
                attackList.add(new Integer(attackHeuristic(countries[id])));
                // Run defenseHeuristic, save results to defenseList
                defenseList.add(new Integer(defenseHeuristic(countries[id])));
            }
        }

        // Train NeuralNet
            // TODO
        // Attack or don't attack

    }

    @Override
    public void cardsPhase(Card[] cards)
    {
        return this.agent.cardsPhase(cards);
    }

    @Override
    public void fortifyPhase()
    {
        return this.agent.fortifyPhase();
    }

    @Override
    public int message(String note, Object data)
    {
        return this.agent.message(note, data);
    }

    @Override
    public int moveArmiesIn(int countryCodeAttacker, int countryCodeDefender)
    {
        return this.agent.moveArmiesIn(countryCodeAttacker, countryCodeDefender);
    }

    @Override
    public int pickCountry()
    {
        return this.agent.pickCountry();
    }

    @Override
    public void placeArmies()
    {
        return this.agent.placeArmies();
    }

    @Override
    public void placeInitialArmies(int numberOfArmies)
    {
        return this.agent.placeInitialArmies(numberOfArmies);
    }

    @Override
    public String youWon()
    {
        String[] answers = { "The world is my footstool- and my feet stink",
          "Sucks to suck, suckers!",
          "Lelouch vi Britannia has nothing on me!",
  		    "Can you smell that? That is the smell of sweet victory",
  		    "Your failure was secured from turn one",
  		    "My skills are without equal",
  		    "Next step: galactic conquest",
  		    "You ignorant fools thought you actually stood a chance?",
  		    "Join me, and I will spare you!\n \n Oh wait, you're already dead",
  		    "Even the Doctor can't save you now!",
  		    "Next I shall become King of the Pirates!",
  		    "Now, I just need to make sure Goku stays out \n
            of my way and I will be unstoppable!",
          "I am the drill that will pierce the heavens!"
        };

  	    return answers[ Random.nextInt(answers.length) ];
    }
}
