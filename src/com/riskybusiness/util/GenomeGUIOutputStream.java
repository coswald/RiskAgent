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

import com.riskybusiness.util.GenomeGUI;

import java.io.IOException;
import java.io.OutputStream;

public class GenomeGUIOutputStream extends OutputStream
{
	private final GenomeGUI destination;
	private int importance = 0;
	
	public GenomeGUIOutputStream(GenomeGUI destination, int importance)
	{
		if(destination == null)
			throw new IllegalArgumentException("Desination is null!");
		if(importance < 0 || importance >= 3)
			throw new IllegalArgumentException("Importance is Invalid!");
		
		this.destination = destination;
		this.importance = importance;
	}
	
	@Override
	public synchronized void write(byte[] buffer, int offset, int length) throws IOException
	{
		try
		{
			Thread.sleep(5);
		}
		catch(InterruptedException ie)
		{
		}
		
		String s = new String(buffer, offset, length);
		switch(importance)
		{
			case 0:
				destination.print(s);
				break;
			case 1:
				destination.printErr(s);
				break;
			case 2:
				destination.printSucc(s);
				break;
		}
	}
	
	@Override
	public synchronized void write(int b) throws IOException
	{
		this.write(new byte[] {(byte)b}, 0, 1);
	}
}
		