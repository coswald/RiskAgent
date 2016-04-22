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
import java.io.Serializable;

/**
 * <p>&nbsp&nbsp&nbsp&nbsp&nbspThis enumeration describes the different
 * types of innovations that can be entered into a database. This
 * includes, for now, {@link com.riskybusiness.genetic.InnovationType#NEW_LINK}
 * as well as {@ink com.riskybusiness.genetic.InnovationType#NEW_NEURON}.</p>.
 * @author Kaleb Luse
 * @author Coved W Oswald
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 * @see com.riskybusiness.genetic.Innovation
 */
public enum InnovationType implements Serializable
{
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspDescribes a new link added to 
	 * a {@code Genome}.
	 */
	NEW_LINK,
	
	/**
	 * <p>&nbsp&nbsp&nbsp&nbsp&nbspDescribes a new neuron added to
	 * a {@code Genome}.
	 */
	NEW_NEURON
}
