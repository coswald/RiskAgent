package com.sillysoft.vox;

import com.sillysoft.tools.*;

//
//  UnitStack.java
//
//  Created by Dustin Sacks on 12/21/04.
//  Copyright 2004 Sillysoft Games. All rights reserved.
//

// This class represents a group of units that all share the same type and owner
//	and original Country.

import java.awt.*;

/** Each UnitStack represents a number of units sharing a type and owner. */
public class UnitStack implements XMLSerializable
{
public static final float unitTransparency = 0.3f;

private int count = 0;	// the number of units
private Unit unit;
private Point drawPoint;

// the country that this UG was moved from (will only be set when the UG is in attack or fortify mode)
private int originalCountryID;	
// When air units attack, they store their desired landing square 
private int landingCountryID = -1;	


private boolean excited = false;
public final static int diceSize = 6;

public UnitStack(String XML, CountriesManager master)
	{
	try
		{
		//SS.debug("create unitstack from: "+XML);
		this.count = Integer.parseInt(XMLTool.extractAttribute("number", XML));		
		this.originalCountryID = Integer.parseInt(XMLTool.extractAttribute("originalCountryID", XML));
		int owner = Integer.parseInt(XMLTool.extractAttribute("owner", XML));
		
		this.unit = UnitPack.createUnit(XMLTool.extractAttribute("unitType", XML), master.getPlayer(owner));
		
		drawPoint = GraphicsTool.pointFromString(XMLTool.extractAttribute("drawPoint", XML));
		} 
	catch (Exception e)
		{
		e.printStackTrace();
		}
	}
	
public String toStringXML()
	{
	return "<UnitStack number=\""+count+"\" unitType=\""+unit.toString()+"\" owner=\""+unit.getOwner().getID()+"\" originalCountryID=\""+originalCountryID+"\" drawPoint=\""+GraphicsTool.stringFromPoint(drawPoint)+"\" />";
	}	
	
public UnitStack(Unit u)
	{
	this(u, 1);
	}
	
public UnitStack(Unit u, int numberOfUnits)
	{
	this(u, numberOfUnits, -1, null, null, null);
	}

public UnitStack(Unit u, int numberOfUnits, int originalCountryID)
	{
	this(u, numberOfUnits, originalCountryID, null, null, null);
	}
	
public UnitStack(Unit u, int numberOfUnits, int originalCountryID, UnitStackGroup carriedUnits, UnitStackGroup carriedUnitsAir, UnitStackGroup carriedUnitsMissiles)
	{
	unit = u;
	setCount(numberOfUnits);
	this.originalCountryID = originalCountryID;
	this.carriedUnits = carriedUnits;
	this.carriedUnitsAir = carriedUnitsAir;
	this.carriedUnitsMissiles = carriedUnitsMissiles;
	}
	
public void setOriginalCountry(Country orig)
	{
	if (orig == null)
		originalCountryID = -1;
	else
		originalCountryID = orig.getID();
	}

public int getOriginalCountryID()
	{
	return originalCountryID;
	}

public Country getOriginalCountry(VoxWorld world)
	{
	return world.countryWithID(originalCountryID);
	}

public void setLandingCountry(Country orig)
	{
	if (orig == null)
		landingCountryID = -1;
	else	
		landingCountryID = orig.getID();
	}

public void setLandingCountry(int orig)
	{
	landingCountryID = orig;
	}

public int getLandingCountryID()
	{
	return landingCountryID;
	}

public void setDrawPoint(Point dp)
	{
//	SS.debug(this+" setDrawPoint: "+dp);
	drawPoint = dp;
	}

// The top left corner of where this unit is placed
public Point getDrawPoint()
	{
	return drawPoint;
	}

public Point getCenterPoint()
	{
	return new Point(drawPoint.x + (getWidth()/2), drawPoint.y + (getHeight()/2));
	}

public int getCount()
	{
	return count;
	}
	
public void addOne()
	{
	count++;
	}
	
public Unit removeOne()
	{
	count--;
	
	return unit;
	}
	
	
public void setCount(int newCount)
	{
	if (newCount < 0)
		throw new IllegalArgumentException("UnitStacks can not have a negative count");
		
	count = newCount;
	}
	
public Player getOwner()
	{
	return unit.getOwner();
	}

public Team getTeam()
	{
	return unit.getOwner().getTeam();
	}
	
public Unit getUnit()
	{
	return unit;
	}
	
public boolean isOfType(Unit u)
	{
	return (u.getType() == unit.getType());
	}
	
public String getImageFilename()
	{
	return unit.getImageFilename();
	}
	
public int getWidth()
	{
	return unit.getWidth();
	}
	
public int getHeight()
	{
	return unit.getHeight();
	}

public Dimension getDimension()
	{
	return new Dimension(getWidth(), getHeight());
	}
	
public Rectangle getBounds()
	{
	return new Rectangle(drawPoint, getDimension());
	}	
	
public void drawCenteredAtWithBubble(Graphics2D g, Point point, String theme)
	{
	Point topLeft = new Point(point.x - (getWidth()/2), point.y - (getHeight()/2));
	drawAt(g, topLeft, theme);
	
	if (! getUnit().isFort() && ! getUnit().isCastle())
		drawBubbleNumber(g, topLeft.x+unit.getNumberOffsetX(), topLeft.y+unit.getNumberOffsetY());
	}

public void draw(Graphics2D g, String theme)
	{
	//	if (excited)
	drawAt(g, drawPoint, theme);

	//		drawTwoThirdsCentered(g, new Point(drawPoint.x + (getWidth()/2), drawPoint.y + (getHeight()/2)));
	}
		
public void drawAt(Graphics2D g, Point point, String theme)
	{
	if (point == null)
		{
		SS.debug("XX UnitStack.drawAt() was called with a null point ?? "+drawPoint);
		return;
		}
	
	Color paintColor = getOwner().getColor();
	if (excited)
		 {
		 paintColor = Color.WHITE;
		 paintColor = GraphicsTool.getContrastingBase(getOwner().getColor(), 0.85f);
		 }

	// Draw twice so we get:
	// - opaque unit base
	// - covered by semi-transparent unit colored with player color
	g.drawImage(GraphicsTool.getManagedImage("Themes/"+theme+"/"+getImageFilename()), point.x, point.y, getWidth(), getHeight(), null);
	g.drawImage(GraphicsTool.getManagedImageColored("Themes/"+theme+"/"+getImageFilename(), paintColor, unitTransparency), point.x, point.y, getWidth(), getHeight(), null);
	
//	if (! getUnit().isFort() && ! getUnit().isCastle())
//		drawBubbleNumber(g, point.x+unit.getNumberOffsetX(), point.y+unit.getNumberOffsetY());
	}

public void drawBubbleNumber(Graphics2D g)
	{
	drawBubbleNumber(g, drawPoint.x+unit.getNumberOffsetX(), drawPoint.y+unit.getNumberOffsetY());
	}
	
public void drawBubbleNumber(Graphics2D g, int x, int y)
	{	
	g.drawImage(GraphicsTool.getBubbleFor(count, getOwner().getColor()), x, y, null );
	
	g.setColor(GraphicsTool.getContrastingBase(getOwner().getColor(), 0.6f));
	int textX = x+8;
	int textY = y+14;
			
	if (count < 10)
		{
		textX = x+8;
		textY = y+16;
		}
	else if (count < 100)
		textX = x+4;
	else if (count < 1000)
		textX = x+3;
	else if (count < 10000)
		textX = x+5;
	else if (count < 100000)
		textX = x+5;
	else if (count < 1000000)
		textX = x+3;
	else
		textX = x+4;
							
	if (count < 100000)
		g.drawString(String.valueOf(count), textX, textY);
	else if (count < 10000000)
		g.drawString(String.valueOf(count/1000)+"k", textX, textY);
	else 
		g.drawString(String.valueOf(count/1000000)+"M", textX, textY);
	}


		
public void drawHalfSizeCentered(Graphics2D g, Point point, String theme)
	{
	g.drawImage(GraphicsTool.getManagedImageColored("Themes/"+theme+"/"+getImageFilename(), getOwner().getColor(), unitTransparency), point.x-(getWidth()/4), point.y-(getHeight()/4), getWidth()/2, getHeight()/2, null);
	
	int x = unit.getNumberOffsetX();
	int y = unit.getNumberOffsetY();
	
	if (! getUnit().isFort() && ! getUnit().isCastle())
		GraphicsTool.drawBoxedString(g, ""+count, point.x+(x/2), point.y+(y/2), 1, 1, getOwner().getColor(), Color.BLACK, 1, Color.BLACK);
	
	if (getCarriedUnitCount() > 0)
		{
		GraphicsTool.drawBoxedString(g, ""+getCarriedUnitCount(), point.x+x, point.y+(y), 1, 1, getOwner().getColor(), Color.BLACK, 1, Color.BLACK);
		}
	}

// Used for stacks shown as part of commands
public void drawTwoThirdsCentered(Graphics2D g, Point point, String theme)
	{
	if (point == null)
		return;
		
	g.drawImage(GraphicsTool.getManagedImageColored("Themes/"+theme+"/"+getImageFilename(), getOwner().getColor(), unitTransparency), point.x-(int)(getWidth()*0.33), point.y-(int)(getHeight()*0.33), (int) (getWidth()*0.66), (int) (getHeight()*0.66), null);
	
	int x = unit.getNumberOffsetX();
	int y = unit.getNumberOffsetY();	
	}
	
public boolean equals(Object other)
	{
	if (! (other instanceof UnitStack))
		return false;
		
	UnitStack otherUG = (UnitStack) other;
	return unit.getOwner().equals(otherUG.unit.getOwner()) && 
			otherUG.isOfType(unit) &&
			originalCountryID == otherUG.originalCountryID &&
			landingCountryID == otherUG.landingCountryID;
	}
	
public boolean equalsIgnoringOriginalCountry(Object other)
	{
	if (! (other instanceof UnitStack))
		return false;
		
	UnitStack otherUG = (UnitStack) other;
	return unit.getOwner().equals(otherUG.unit.getOwner()) && 
			otherUG.isOfType(unit);
	}


public String toString()
	{
	return "<US: "+count+" "+unit+" owned by "+unit.getOwner()+
	(originalCountryID == -1 ? "" : " oCountry: "+originalCountryID)+
	(landingCountryID == -1 ? "" : " landingCountry: "+landingCountryID)+
	(drawPoint == null ? "" : " dp: "+drawPoint.x+","+drawPoint.y)+
	(carriedUnits == null || carriedUnits.getTotalUnitCount() == 0 ? "" : " carriedUnits: "+carriedUnits)+
	(carriedUnitsAir == null || carriedUnitsAir.getTotalUnitCount() == 0 ? "" : " carriedUnitsAir: "+carriedUnitsAir)+
	(carriedUnitsMissiles == null || carriedUnitsMissiles.getTotalUnitCount() == 0 ? "" : " carriedUnitsMissiles: "+carriedUnitsMissiles)+
	">";
	}


// Make the rolls for the units in this group and return the number of kills they make.
public int calculateDiceRollKills(boolean attacking)
	{
	// use the low-luck variant
	int unitValue = (attacking ? unit.getAttack() : unit.getDefend());
	int totalUnitStackPower = unitValue * count;
	
	int assuredUnitKills = totalUnitStackPower/diceSize;
	int nonAssuredRemainer = totalUnitStackPower % diceSize;
	int unitKills = assuredUnitKills;

	if ( (SS.rand.nextInt(diceSize)+1) <= nonAssuredRemainer)
			unitKills++;

	return unitKills;	
	}
	
// Remove the specified number of units from this group and return them in a new group
public UnitStack splitOffGroup(int numberOfUnits)
	{
	if (numberOfUnits > getCount())
		throw new IllegalArgumentException(this +" splitOffGroup does not contain enough units: "+numberOfUnits+" vs "+getCount());
		
	UnitStack result = new UnitStack(unit, numberOfUnits);
	
	// If we are moving a transport then we take some of the carried units with us
	if (carriedUnits != null && carriedUnits.getTotalUnitCount() > 0)
		{
		int takeWeight = numberOfUnits * unit.transportCapacity();
		if (takeWeight > 0)
			{
			UnitStackGroup carrySplit = carriedUnits.takeWeight(takeWeight);
//SS.debug("splifOffGroup is taking carried units: "+carrySplit);
			result.addCarry(carrySplit);
			}
		}
	if (carriedUnitsAir != null && carriedUnitsAir.getTotalUnitCount() > 0)
		{
		int takeWeight = numberOfUnits * unit.transportCapacityAir();
		if (takeWeight > 0)
			{
			UnitStackGroup carrySplit = carriedUnitsAir.takeWeight(takeWeight);
//SS.debug("splifOffGroup is taking carried units: "+carrySplit);
			result.addCarry(carrySplit);
			}
		}		
	if (carriedUnitsMissiles != null && carriedUnitsMissiles.getTotalUnitCount() > 0)
		{
		int takeWeight = numberOfUnits * unit.transportCapacityMissiles();
		if (takeWeight > 0)
			{
			UnitStackGroup carrySplit = carriedUnitsMissiles.takeWeight(takeWeight);
//SS.debug("splifOffGroup is taking carried units: "+carrySplit);
			result.addCarry(carrySplit);
			}
		}		
	
	// This must happen after transport stuff	???
	setCount(getCount() - numberOfUnits);

//SS.debug("splifOffGroup returning final result: "+result);
	return result;
	}

/*** Transport carry stuff ***/
UnitStackGroup carriedUnits;
UnitStackGroup carriedUnitsAir;
UnitStackGroup carriedUnitsMissiles;

public UnitStackGroup getCarriedUnits()
	{
	if (carriedUnits == null)
		carriedUnits = new UnitStackGroup();
		
	return carriedUnits;
	}

public UnitStackGroup getCarriedUnitsAir()
	{
	if (carriedUnitsAir == null)
		carriedUnitsAir = new UnitStackGroup();
		
	return carriedUnitsAir;
	}

public UnitStackGroup getCarriedUnitsMissiles()
	{
	if (carriedUnitsMissiles == null)
		carriedUnitsMissiles = new UnitStackGroup();
		
	return carriedUnitsMissiles;
	}

/** If the underlying unit is a transport then this will return the number of units being carried. */	
public int getCarriedUnitCount()
	{
	return (carriedUnits == null ? 0 : carriedUnits.getTotalUnitCount());
	}

public int getCarriedUnitCountAir()
	{
	return (carriedUnitsAir == null ? 0 : carriedUnitsAir.getTotalUnitCount());
	}

public int getCarriedUnitCountMissiles()
	{
	return (carriedUnitsMissiles == null ? 0 : carriedUnitsMissiles.getTotalUnitCount());
	}

public int getRemainingCarryCapacity()
	{
	return (unit.transportCapacity() * count) - (carriedUnits == null ? 0 : carriedUnits.getTransportWeight());
	}

public int getRemainingCarryCapacityAir()
	{
	return (unit.transportCapacityAir() * count) - (carriedUnitsAir == null ? 0 : carriedUnitsAir.getTransportWeight());
	}

public int getRemainingCarryCapacityMissiles()
	{
	return (unit.transportCapacityMissiles() * count) - (carriedUnitsMissiles == null ? 0 : carriedUnitsMissiles.getTransportWeight());
	}	
	
	
public void addCarry(UnitStack carry)
	{
	if (carry.expiresAfterAttack())
		{
		if (carriedUnitsMissiles == null)
			carriedUnitsMissiles = new UnitStackGroup();
		carriedUnitsMissiles.add(carry);
		return;
		}
	else if (carry.getUnit().isAir())
		{
		if (carriedUnitsAir == null)
			carriedUnitsAir = new UnitStackGroup();
		carriedUnitsAir.add(carry);
		return;
		}
		
	if (carriedUnits == null)
		carriedUnits = new UnitStackGroup();
	carriedUnits.add(carry);
	}

public void addCarry(UnitStackGroup carryList)
	{
	for (int i = 0; i < carryList.size(); i++)
		this.addCarry(carryList.get(i));
	}
	
public void removeCarry()
	{
	carriedUnits = null;
	}

public void removeCarryAir()
	{
	carriedUnitsAir = null;
	}
	
public void removeCarry(UnitStack carry)
	{
	for (int i = 0; i < carriedUnits.size(); i++)
		{
		if (carry.equals(carriedUnits.get(i)))
			{
			UnitStack group = carriedUnits.get(i);
			group.setCount(group.getCount() - carry.getCount());
			if (group.getCount() == 0)
				carriedUnits.remove(group);
			return;
			}
		}
	throw new IllegalArgumentException("UnitStack.removeCarry did not find the unit to remove");
	// check air units... xxxx
	}

/*** End transport carry stuff ***/


protected Object clone()
	{
	UnitStack result = new UnitStack(getUnit(), getCount(), getOriginalCountryID(), 
			(UnitStackGroup) getCarriedUnits().clone(), 
			(carriedUnitsAir == null ? null : (UnitStackGroup) carriedUnitsAir.clone()),
			(carriedUnitsMissiles == null ? null : (UnitStackGroup) carriedUnitsMissiles.clone()));
			
	if (drawPoint != null)
		result.drawPoint = (Point) drawPoint.clone();
	return result;
	}
	
public int getCost()
	{
	return getUnit().getCost() * getCount();
	}
	
public boolean isAir()
	{
	return unit.isAir();
	}

public boolean isWater()
	{
	return unit.isWater();
	}	

public int getMovement()
	{
	return unit.getMovement();
	}	

public int getAttackMovement()
	{
	return unit.getAttackMovement();
	}	

public boolean expiresAfterAttack()
	{ return unit.expiresAfterAttack(); }

public void setExcited(boolean b)
	{ 
	excited = b;
	}

public boolean containsPoint(Point p)
	{
	return new Rectangle(getDrawPoint(), getDimension()).contains(p);
	}	
	
public int getUnitAttack()
	{
	return unit.getAttack();
	}

public int getUnitDefend()
	{
	return unit.getDefend();
	}

}




//public static void drawBoxedString(Graphics2D g, String text, int centeredAtX, int centeredAtY, int paddingX, int paddingY, Color boxColor, Color outlineColor, int outlineWidth, Color textColor)
	
	/*
	if (getCarriedUnitCount() > 0)
		{
		GraphicsTool.drawBoxedString(g, ""+getCarriedUnitCount(), point.x+x-15, point.y+y+8, 1, 1, getOwner().getColor(), Color.BLACK, 1, Color.BLACK);
		}
	if (getCarriedUnitCountAir() > 0)
		{
		GraphicsTool.drawBoxedString(g, ""+getCarriedUnitCountAir(), point.x+(2*x), point.y+y-8, 1, 1, getOwner().getColor(), Color.BLACK, 1, Color.BLACK);
//		g.drawImage(getImage(), point.x, point.y+(4*y), getWidth(), getHeight(), null);		
		}		
	if (getCarriedUnitCountMissiles() > 0)
		{
		GraphicsTool.drawBoxedString(g, ""+getCarriedUnitCountMissiles(), point.x+x, point.y+(y), 1, 1, getOwner().getColor(), Color.BLACK, 1, Color.BLACK);
//		g.drawImage(getImage(), point.x, point.y+(4*y), getWidth(), getHeight(), null);		
		}		
		*/