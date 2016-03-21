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

package com.riskybusiness.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.InterruptedException;
import java.lang.Object;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * <p>A class that allows for the output of information to a debug
 * terminal. This is determined by the
 * {@link com.riskybusiness.util.Debug#DEBUGGING} switch that allows
 * a programmer to set debugging to either {@code true} or {@code false}.
 * However, once the debug window has closed, it can no longer be
 * reopened until a new instance of the application has been started
 * again.</p>
 * @author Coved W Oswald
 * @author Kaleb Luse
 * @author Weston Miller
 * @version 1.0
 * @since 1.6
 */
public class Debug
{
	/**
	 * <p>The switch that determines whether to display debug
	 * information or not. By default, this is set to
	 * {@code false}.</p>
	 */
	public static boolean DEBUGGING = false; //made public to allow overriding. I don't like this, but whatever.
	
	/**
	 * <p>The switch that determines whether or not to prompt
	 * the user to make sure that they want to exit the
	 * debugger.</p>
	 */ 
	public static boolean SHOWMESSAGE = true; 
	
	private static JTextArea area = new JTextArea();
	private static JFrame frame = new JFrame();
	private static boolean hasClosed = false;
	
	static
	{
		frame.add(new JScrollPane(area));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(800, 300);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if(!SHOWMESSAGE || (JOptionPane.showConfirmDialog(frame, "Are you sure you want to exit this window?\nBy doing this, you will no longer see debug information.",
				"Confirm Close", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION))
				{
					frame.setVisible(false);
					hasClosed = true;
				}
			}
		});
		area.setEditable(false);
	}
	
	/**
	 * <p>Outputs a {@code String} onto the console with
	 * the given amount of tabs.</p>
	 * @param s The string to output.
	 * @param indent The amount of indents to output.
	 */
	public static void output(String s, int indent)
	{
		String ind = "";
		for(int i = 0; i < indent; i++)
			ind = "\t";
		s = ind + s;
		area.append(s);
		
		if(DEBUGGING && !hasClosed)
		{
			frame.setVisible(true);
			frame.repaint();
			frame.revalidate();
			area.setCaretPosition(area.getDocument().getLength());
		}
	}
	
	/**
	 * <p>Outputs a {@code String} onto the console with
	 * no tabs.</p>
	 * @param s The string to output.
	 * @see com.riskybusiness.util.Debug#output(String, int)
	 */
	public static void output(String s)
	{
		Debug.output(s, 0);
	}
	
	/**
	 * <p>Outputs a {@code String} onto the console with
	 * the given amount of tabs and a new line after the
	 * string.</p>
	 * @param s The string to output.
	 * @param indent The amount of indents to output.
	 * @see com.riskybusiness.util.Debug#output(String, int)
	 */
	public static void println(String s, int indent)
	{
		Debug.output(s + "\n", indent);
	}
	
	/**
	 * <p>Outputs a {@code String} onto the console with
	 * a new line after the string.</p>
	 * @param s The string to output.
	 * @see com.riskybusiness.util.Debug#output(String)
	 */
	public static void println(String s)
	{
		Debug.output(s + "\n");
	}
	
	/**
	 * <p>Returns whether or not the frame associated
	 * with this class has exited.</p>
	 * @return {@code true} if the frame has exited,
	 * 	   {@code false} otherwise.
	 */
	public static boolean hasExited()
	{
		return Debug.hasClosed;
	}
	
	/**
	 * <p>Allows the application to give closing
	 * permissions to the debugger. This might
	 * be used in terminal applications with a
	 * debugger as well. This will wait until
	 * the user exits the debugger to close the
	 * application.</p>
	 */
	public static void giveClose()
	{
		while(!Debug.hasExited())
                {
                        try
                        {
                                Thread.sleep(500);
                        }
                        catch(InterruptedException ie)
                        {
                                ie.printStackTrace();
                                System.exit(1);
                        }
                }
                System.exit(0);
	}
}
