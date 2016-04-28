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

import com.riskybusiness.util.GenomeGUI;
import com.riskybusiness.util.GenomeGUIOutputStream;

import java.io.PrintStream;

import java.lang.Object;
import java.lang.Runnable;

import javax.swing.SwingUtilities;

public class Main extends Object
{
	public static void main(String... args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				GenomeGUI ggui = new GenomeGUI();
				GenomeGUIOutputStream gguios = new GenomeGUIOutputStream(ggui, 0);
				//GenomeGUIOutputStream gguiose = new GenomeGUIOutputStream(ggui, 1);
				
				//System.setErr(new PrintStream(gguiose));
				System.setOut(new PrintStream(gguios));
				
				ggui.start();
			}
		});
	}
}