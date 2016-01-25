package com.sillysoft.tools;

//
//  RangeIterator.java
//
//  Created by Dustin Sacks on 1/5/05.
//  Copyright 2005 Sillysoft Games. All rights reserved.
//
//	A RangeIterator can be used to randomly return values inside a given range
//	until they have all been used up.

import java.util.Random;

public class RangeIterator 
{
private Random randLocal;

// The top and bottom of the range (inclusive)
private int min, max;

// How many notches per integer (1 means only integers will be returned)
private int spotsPerInteger;	

// A record of what has been used up already
private boolean[] used;

public RangeIterator(int min, int max)
	{
	this(min, max, 1);
	}
	
public RangeIterator(int min, int max, int spotsPerInteger)
	{
	if (max < min)
		throw new RuntimeException("RangeIterator created with max < min");
		
	this.min = min;
	this.max = max;
	this.spotsPerInteger = spotsPerInteger;
	
	used = new boolean[((max-min)*spotsPerInteger)+1];
	}

public RangeIterator(int min, int max, int spotsPerInteger, int localRandSeed)
	{
	if (max < min)
		throw new RuntimeException("RangeIterator created with max < min");
		
	this.min = min;
	this.max = max;
	this.spotsPerInteger = spotsPerInteger;
	randLocal = new Random(localRandSeed);
	
	used = new boolean[((max-min)*spotsPerInteger)+1];
	}

public double next()
	{
	if (! hasNext())
		{
		throw new RuntimeException("RangeIterator.next() called when hasNext() == false");
		}
		
	while (true)
		{
		int r = nextIntRand(used.length);
		if (! used[r])
			{
			used[r] = true;
			return min+((double)r/spotsPerInteger);
			}
		}
	}
	
public boolean hasNext()
	{
	for (int i = 0; i < used.length; i++)
		if (! used[i])
			return true;
			
	return false;
	}

private int nextIntRand(int max)
	{
	if (randLocal != null)
		return randLocal.nextInt(max);
		
	return SS.rand.nextInt(max);
	}
	
/** Print some test data about the RangeIterator's behaviour. */
public static void test()
	{
	RangeIterator r = new RangeIterator(0, 0);
	while (r.hasNext())
		System.out.println("RangeIterator(0,0) -> "+r.next());

	System.out.println(" ");
	r = new RangeIterator(0, 0, 2);
	while (r.hasNext())
		System.out.println("RangeIterator(0,0,2) -> "+r.next());
		
	System.out.println(" ");
	r = new RangeIterator(-1, 1);
	while (r.hasNext())
		System.out.println("RangeIterator(-1,1) -> "+r.next());
		
	System.out.println(" ");
	r = new RangeIterator(-1, 1, 2);
	while (r.hasNext())
		System.out.println("RangeIterator(-1,1,2) -> "+r.next());
		
	System.out.println(" ");
	r = new RangeIterator(-1, 1, 5);
	while (r.hasNext())
		System.out.println("RangeIterator(-1,1,5) -> "+r.next());
	}
}
