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

import com.sillysoft.lux.Board;
import com.sillysoft.lux.Country;
import com.sillysoft.lux.agent.LuxAgent;

/**
 * <p>&nbsp&nbsp&nbsp&nbspThe {@code LuxAgentAdapter} allows further
 * agents to implement the methods they need to without worrying
 * about gathering data from the game itself.</p>
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @see com.sillysoft.lux.agent.LuxAgent
 * @since 1.6
 */
public abstract class LuxAgentAdapter implements LuxAgent
{
    /**
     * <p>An integer that shows the ID given to the agent by the game.</p>
     */
    protected int ID;

    /**
     * <p>A float used by subclasses to refer to it's version number.</p>
     */
    protected float version = 1.0f;

    /**
     * <p>A String used by subclassses to refer to it's description.</p>
     */
    protected String description = "An AI";

    /**
     * <p>The {@code Board} the agent is in.</p>
     */
    protected Board board;

    /**
     * <p>The {@code Country} list that contains all of the countries
     * on the board.</p>
     */
    protected Country[] countries;

    /**
     * <p>The {@code LuxAgent} to take other behaviours from.</p>
     */
    protected LuxAgent agent;

    /**
     * <p>A constructor to determine version number, other behaviour,
     * description.</p>
     * @param luxAgent The {@code LuxAgent} to use for the behaviours
     *                 That the agent doesn't want to specify
     */
    public LuxAgentAdapter(LuxAgent agent)
    {
        this.agent = agent;
    }

    /**
     * <p>This method should not be overridden, and allows this class
     * to pass on the ID and {@code Board} from the game to the
     * agent without having to worry about it themselves.</p>
     * @param ID The ID given to the agent by the game.
     * @param board The {@code Board} the agent is in.
     * @see com.sillysoft.lux.agent.LuxAgent#setPrefs(int,Board)
     */
    @Override
    public void setPrefs(int ID, Board board)
    {
      this.ID = ID;
      this.board = board;
      this.countries = board.getCountries();
      this.agent.setPrefs(ID, board);
    }

    /**
     * <p>Tells the game what the class name of the object is.
     * This is done by calling the current class's (represented
     * as a {@code Class} object) {@link java.lang.Class#getName()}
     * method.</p>
     * @return The classname of this object.
     */
    @Override
    public String name()
    {
      return this.getClass().getSimpleName();
    }

    /**
     * <p>Tells the game what the current version of our AI is.</p>
     * @return The {@link #version} float.
     */
    @Override
    public float version()
    {
      return this.version;
    }

    /**
     * <p>A short description of this class as it relates to
     * LuxDelux.</p>
     * @return The {@link #description} string.
     */
    @Override
    public String description()
    {
      return this.description;
    }
}
