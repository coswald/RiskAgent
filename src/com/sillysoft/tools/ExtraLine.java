package com.sillysoft.tools;

//
//  ExtraLine.java
//  Lux
//
//  A line. It stores a color and a width. It can have multiple segments to it.
//

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.*;

import java.util.StringTokenizer;
import java.util.Vector;


public class ExtraLine 
{
private GeneralPath shape;

public Color color;
public int width = 1;
public boolean above = false;	// true if the line should be drawn above the countries
	
public ExtraLine(String xml)
	{
	String position = XMLTool.extract("position", xml);
	StringTokenizer tok = new StringTokenizer(position, " ,");

	shape = new GeneralPath();
	shape.moveTo(Integer.parseInt(tok.nextToken()),Integer.parseInt(tok.nextToken()));
	while (tok.hasMoreTokens())
		{
		shape.lineTo(Integer.parseInt(tok.nextToken()),Integer.parseInt(tok.nextToken()));
		}

	color = GraphicsTool.colorFromString(XMLTool.extract("color", xml));	// may return null

	String widthString = XMLTool.extract("width", xml); 
	if (widthString != null)
		width = Integer.parseInt(widthString);

	if ("true".equals(XMLTool.extract("above", xml)))
		above = true;
		
//	System.out.println("ExtraLine  -> created with XML: "+xml);
	}

public ExtraLine(Point from, Point to, int width, Color color, boolean above)
	{
	shape = new GeneralPath();
	shape.moveTo(from.x,from.y);
	shape.lineTo(to.x,to.y);

	this.color = color;	// it's OK if it's null
	this.width = width;

	this.above = above;
	}

public void draw(Graphics2D g)
	{
	g.setStroke(new BasicStroke(width));
	g.draw(shape);
	}

public void flipY(int height)
	{
//	System.out.println("ExtraLine flip Y at height:"+height);
	shape = GraphicsTool.flipY(shape, height);
//	System.out.println("           -> shape: "+GraphicsTool.stringFromGeneralPath(shape, height));
	}

public double distanceToPoint(Point p)
	{
	return GraphicsTool.distanceFromPointToGeneralPath(p, shape);
	}

public String getXML(int height)
	{
	StringBuffer result = new StringBuffer();
	result.append("<line><position>");
	result.append(GraphicsTool.stringFromGeneralPath(shape, height));
	result.append("</position>");
	if (color != null)
		{
		result.append("<color>");
		result.append(GraphicsTool.stringFromColor(color));
		result.append("</color>");
		}
	if (width != 1)
		{
		result.append("<width>");
		result.append(width);
		result.append("</width>");
		}
	if (above)
		{
		result.append("<above>true</above>");
		}

	result.append("</line>");

//	System.out.println("ExtraLine returning XML: "+result+" from height:"+height);
	return result.toString();
	}
	
private GeneralPath shapeOriginal;

public void resize(double percent)
	{
	if (shapeOriginal == null)
		{
		shapeOriginal = shape;
		}	
	shape = GraphicsTool.resizeShape(shapeOriginal, percent);
	}


public String toString()
	{
	return "<ExtraLine width:"+width+" above:"+above+" shape:"+shape+">";
	}

public Point from()	
	{
	PathIterator it = shape.getPathIterator(new AffineTransform());
	float[] point = new float[2];
	int segType;

	while (! it.isDone())
		{
		segType = it.currentSegment(point);
		if (segType == PathIterator.SEG_MOVETO)
			{
			return new Point((int) point[0], (int)point[1]);
			}
		it.next();			
		}
	return null;
	}


public Point to()	
	{
	PathIterator it = shape.getPathIterator(new AffineTransform());
	float[] point = new float[2];
	int segType;

	while (! it.isDone())
		{
		segType = it.currentSegment(point);
		if (segType == PathIterator.SEG_LINETO)
			{
			return new Point((int) point[0], (int)point[1]);
			}
		it.next();			
		}
	return null;
	}
}
