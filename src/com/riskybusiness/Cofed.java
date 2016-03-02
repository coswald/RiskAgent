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

package com.riskybusiness;

import com.riskybusiness.LuxAgentAdapter;
import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;
import com.sillysoft.lux.agent.EvilPixie;
import java.util.Random;

/**
 * <p>&nbsp&nbsp&nbsp&nbspThe {@code Cofed} agent uses ANN's to play
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
    private int fitness;

    public Cofed()
    {
        fitness = 0;
    }

    @Override
    public void setPrefs(int ID, Board board)
    {
        super.setPrefs(ID, board);
        this.agent = new EvilPixie();
        this.agent.setPrefs(ID, board);
    }

    private int attackHeuristic()
    {
        if (CanAttack)
        {
            if (PlayerID != 0)
            {
                if (OwnTroops == EnemyTroops)
                {
                    if (OwnTroops >= 15) // Odds of victory exceed 60%
                    {
                        Attack(CountryID); // ATTACK!!!!
                        Fitness += 8;
                    }

                    else if (OwnTroops >= 5) // Odds of victory between 50% and 60%
                    {
                        if (LastCountryOfOpponent)
                        {
                            if ((OwnCards + EnemyCards) >= 5)
                            {
                                Attack(CountryID); // Obliterate & take cards for a turn-in!
                                Fitness += 6;
                            }
                            else if (EnemyReinforcementsPerTurn >= OwnReinforcementsPerTurn)
                            {
                                Attack(CountryID); // Better to crush them before they become greater than you...
                                Fitness += 6;
                            }
                            else if (OpponentThatCanAttackThemTroops > EnemyTroops &&
                            (OpponentThatCanAttackThemCards + EnemyCards) >= 5)
                            {
                                Attack(CountryId); // Don't let them steal the glory and power!
                                Fitness += 5;
                            }
                            else
                            {
                                DoNothing(); // Don't Attack- not worth the RISK... .... ... Or is it?
                                Fitness += 4;
                            }
                        }

                        else if (BreakContinentBonus) // Break bonus troop gains!
                        {
                            Attack(CountryID);
                            Fitness += 7;
                        }

                        else // Save your troops and build up
                        {
                            DoNothing(); // Don't Attack
                            Fitness += 5;
                        }
                    }

                    else // Odds of victory are in favor of defender
                    {
                        DoNothing(); // Don't Attack
                        Fitness += 6;
                    }
                }

                else if (OwnTroops > EnemyTroops)
                {
                    if ((OwnTroops - EnemyTroops) >= 10) // The lowest odds are high with this....
                    {
                        Attack(CountryID); // ATTACK!!!! Slaughter!!!!
                        Fitness += 10; // Best Scenario!
                    }

                    else if ((OwnTroops - EnemyTroops) >= 6) // The lowest odds are high with this....
                    {
                        Attack(CountryID); // ATTACK!!!! Slaughter!!!!
                        Fitness += 8; // Great Scenario!
                    }

                    else if ((OwnTroops - EnemyTroops) >= 2) // The lowest odds are at 68% with this....
                    {
                        Attack(CountryID); // ATTACK!!!! Slaughter!!!!!
                        Fitness += 6;
                    }

                    else // Only one more- odds are high-ish, but not quite exceptional.
                    {
                        if (LastCountryOfOpponent)
                        {
                            if ((OwnCards + EnemyCards) >= 5)
                            {
                                Attack(CountryID); // Obliterate & take cards for a turn-in!
                                Fitness += 6;
                            }
                            else if (EnemyReinforcementsPerTurn >= OwnReinforcementsPerTurn)
                            {
                                Attack(CountryID); // Better to crush them before they become greater than you...
                                Fitness += 5;
                            }
                            else if (OpponentThatCanAttackThemTroops > EnemyTroops &&
                            (OpponentThatCanAttackThemCards + EnemyCards) >= 5)
                            {
                                Attack(CountryId); // Don't let them steal the glory and power!
                                Fitness += 4;
                            }
                            else
                            {
                                DoNothing(); // Don't Attack- not worth the RISK... .... ... Or is it?
                                Fitness += 3;
                            }
                        }

                        else if (BreakContinentBonus) // Break bonus troop gains!
                        {
                            Attack(CountryID);
                            Fitness += 7;
                        }

                        else // Save and build up troops
                        {
                            DoNothing(); // Don't Attack
                            Fitness += 2;
                        }
                    }
                }

                else // Opponent has more troops- so odds are never exceptional
                {
                    if ((EnemyTroops - OwnTroops) <= 2) //There is hope!
                    {
                        if (OwnTroops >= 20) // The Power of the Three Dice In full!
                        {
                            Attack(CountryID); // Odds begin exceeding 60% at this point
                            Fitness += 7;
                        }

                        else if (OwnTroops >= 13) // Odds are even at this point
                        {
                            if (LastCountryOfOpponent)
                            {
                                if ((OwnCards + EnemyCards) >= 5)
                                {
                                    Attack(CountryID); // Obliterate & take cards for a turn-in!
                                    Fitness += 6;
                                }

                                else if (EnemyReinforcementsPerTurn >= OwnReinforcementsPerTurn)
                                {
                                    Attack(CountryID); // Better to crush them before they become greater than you...
                                    Fitness += 5;
                                }

                                else if (OpponentThatCanAttackThemTroops > EnemyTroops &&
                                (OpponentThatCanAttackThemCards + EnemyCards) >= 5)
                                {
                                    Attack(CountryId); // Don't let them steal the glory and power!
                                    Fitness += 4;
                                }

                                else
                                {
                                    DoNothing(); // Don't Attack- not worth the RISK... .... ... Or is it?
                                    Fitness += 4;
                                }
                            }

                            else if (BreakContinentBonus) // Break bonus troop gains!
                            {
                                Attack(CountryID);
                                Fitness += 6;
                            }

                            else // Save and build up troops
                            {
                                DoNothing(); // Don't Attack
                                Fitness += 5;
                            }
                        }
                    }

                    else // The odds are almost never in your favor
                    {
                        DoNothing(); // Don't Attack for your own sake...
                        Fitness += 9;
                    }
                }
            }
        }
    }

    private int defenseHeuristic()
    {
        if (OpponentCanAttack)
        {
            if (PlayerID == 0)
            {

                if (OwnTroops == EnemyTroops)
                {
                    if (OwnTroops <= 5)
                    {
                        if (OwnLastCountry)
                        {
                            if ((OwnCards + EnemyCards) >= 5)
                            {
                                DoNothing();
                                Fitness += 4;
                            }
                            else if (EnemyReinforcementsPerTurn <= OwnReinforcementsPerTurn)
                            {
                                DoNothing();
                                Fitness += 6;
                            }
                            else
                            {
                                DoNothing();
                                Fitness += 5;
                            }
                        }
                        else if (BreakContinentBonus)
                        {
                            DoNothing();
                            Fitness += 3;
                        }

                        else
                        {
                            DoNothing();
                            Fitness += 7;
                        }
                    }

                    else if (OwnTroops <= 15)
                    {
                        Attack(CountryID);
                        Fitness += 5;
                    }

                    else
                    {
                        Attack(CountryID);
                        Fitness += 3;
                    }
                }

                else if (OwnTroops > EnemyTroops)
                {
                    if ((OwnTroops - EnemyTroops) >= 10)
                    {
                        Attack(CountryID);
                        Fitness += 10;
                    }

                    else if ((OwnTroops - EnemyTroops) >= 6)
                    {
                        Attack(CountryID);
                        Fitness += 8;
                    }

                    else if ((OwnTroops - EnemyTroops) >= 2)
                    {
                        Attack(CountryID);
                        Fitness += 6;
                    }

                    else
                    {
                        if (OwnLastCountry)
                        {
                            if ((OwnCards + EnemyCards) >= 5)
                            {
                                DoNothing();
                                Fitness += 4;
                            }
                            else if (EnemyReinforcementsPerTurn <= OwnReinforcementsPerTurn)
                            {
                                Attack(CountryID);
                                Fitness += 5;
                            }
                            else
                            {
                                DoNothing();
                                Fitness += 5;
                            }
                        }

                        else if (BreakContinentBonus)
                        {
                            DoNothing();
                            Fitness += 4;
                        }

                        else
                        {
                            DoNothing();
                            Fitness += 6;
                        }
                    }
                }

                else
                {
                    if ((EnemyTroops - OwnTroops) >= 2)
                    {
                        if (EnemyTroops >= 20)
                        {
                            DoNothing();
                            Fitness += 3;
                        }

                        else if (EnemyTroops >= 13)
                        {
                            if (OwnLastCountry)
                            {
                                if ((OwnCards + EnemyCards) >= 5)
                                {
                                    DoNothing();
                                    Fitness += 2;
                                }

                                else if (EnemyReinforcementsPerTurn <= OwnReinforcementsPerTurn)
                                {
                                    DoNothing();
                                    Fitness += 5;
                                }

                                else
                                {
                                    Attack(CountryID);
                                    Fitness += 6;
                                }
                            }

                            else if (BreakContinentBonus)
                            {
                                DoNothing();
                                Fitness += 4;
                            }

                            else
                            {
                                DoNothing();
                                Fitness += 5;
                            }
                        }
                    }

                    else
                    {
                        Attack(CountryID);
                        Fitness += 9;
                    }
                }
            }
        }
    }

    @Override
    public void attackPhase()
    {
        // Gather data from board
        // Run attackHeuristic, save results
        attackHeuristic();
        // Run defenseHeuristic, save results
        defenseHeuristic();
        // Train NeuralNet
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
        String[] answers = { "The world is now my footstool",
          "Sucks to suck, suckers!",
          "Lelouch vi Britannia has nothing on me!",
  		    "Can you smell that? That is the smell of sweet victory",
  		    "Your failure was secured from turn one",
  		    "My skills are without equal",
  		    "Next step: galactic conquest",
  		    "You ignorant fools thought you actually stood a chance?",
  		    "Join me, and I will spare you!\n \n Oh wait, you're already dead",
  		    "Even the Doctor can't save you now!",
  		    "Your head shall be added to my trophy room",
  		    "Now, I just need to make sure Goku stays out \n
            of my way and I will be unstoppable!",
          "Onward to pierce the heavens!"
        };

  	    return answers[ Random.nextInt(answers.length) ];
    }
}
