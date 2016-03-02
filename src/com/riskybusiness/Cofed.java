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

import java.util.Random;
import com.riskybusiness.LuxAgentAdapter;
import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;

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
  public Cofed()
  {
  }

  @Override
  public void attackPhase()
  {
  }

  @Override
  public void cardsPhase(Card[] cards)
  {
  }

  @Override
  public void fortifyPhase()
  {
  }

  @Override
  public int message(String message, Object data)
  {
  }

  @Override
  public int moveArmiesIn(int countryCodeAttacker, int countryCodeDefender)
  {
  }

  @Override
  public int pickCountry()
  {
  }

  @Override
  public void placeArmies()
  {
  }

  @Override
  public void placeInitialArmies(int numberOfArmies)
  {
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
