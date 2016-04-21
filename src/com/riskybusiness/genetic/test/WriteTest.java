package com.riskybusiness.genetic.test;

import java.lang.InterruptedException;
import java.lang.Object;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.riskybusiness.genetic.Genome;

public class WriteTest extends Object
{
	public static void main(String... args) throws InterruptedException
	{
		System.out.println("I am going to save a simple genome.");

		System.out.println("Here is the new Genome:");
		Genome g = new Genome();
		System.out.println();
		System.out.println(g);

		ObjectOutputStream oos = null;
		try
		{
			System.out.println("\tAttempting to create a new file and save it...");
			oos = new ObjectOutputStream(new FileOutputStream("g.txt"));
			oos.writeObject(g);

			if(oos != null)
				oos.close();
		}
		catch(IOException io)
		{
			System.out.println("\tError!");
			io.printStackTrace();
			System.err.println(io.toString());
			System.exit(1);
		}
		finally
		{
			System.out.println("\tOutput Successful!");
		}

		Thread.sleep(2000); //just for a nice delay.
		System.out.println("\nAttempting to load the same file!");

		Genome g2 = null;
		ObjectInputStream ois = null;
		try
		{
			System.out.println("\tAttempting to load a file and save it to an object!");
			ois = new ObjectInputStream(new FileInputStream("g.txt"));
			g2 = (Genome) ois.readObject();

			if(ois != null)
				ois.close();
		}
		catch(Exception e)
		{
			System.out.println("\tError!");
			e.printStackTrace();
			System.err.println(e.toString());
			System.exit(1);
		}
		finally
		{
			System.out.println("\tInput Successful!");
		}

		System.out.println("\nThis is the loaded Genome: ");
		System.out.println(g2);
	}
}