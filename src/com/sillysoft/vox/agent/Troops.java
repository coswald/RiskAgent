import java.util.List;

import com.sillysoft.vox.Country;
import com.sillysoft.vox.UnitStack;
import com.sillysoft.vox.UnitStackGroup;
import com.sillysoft.vox.UnitStackGroupIterator;
import com.sillysoft.vox.unit.UnitPawn;
import com.sillysoft.vox.unit.UnitKnight;
import com.sillysoft.vox.unit.UnitCastle;


//This class provides useful functions for issuing placement and
//movement orders and for keeping track of the orders that have 
//been placed, via an "original," "remaining," and "incoming"
//UnitStackGroup for each country.

public class Troops {
    
    Polymath bot;
	
	UnitStackGroup[] original;  //units in country at start of turn
	UnitStackGroup[] remaining; //units which have not yet been explicitly ordered to move or stay put
	UnitStackGroup[] incoming;  //units have been ordered to show up here next turn
	                            // -this includes both those coming from nearby countries
	                            //  as well as those explicitly ordered to remain here
	
	public Troops(Polymath bot) {
		this.bot = bot;
	}
	
	public void initTurn() {
		original = new UnitStackGroup[bot.countryArray.length];
		remaining = new UnitStackGroup[bot.countryArray.length];
		incoming = new UnitStackGroup[bot.countryArray.length];
		for(int i = 0; i < bot.countryArray.length; i++) {
			UnitStackGroupIterator stacks = new UnitStackGroupIterator(bot.countryArray[i].getUnitStackGroup());
			original[i] = new UnitStackGroup();
			remaining[i] = new UnitStackGroup();
			incoming[i] = new UnitStackGroup();
			while(stacks.hasNext()) {
				UnitStack stack = stacks.next();
				original[i].add(stack);
				
				//units can't move if they are castles or if they are controlled by Boring
				if((stack.getUnit() instanceof UnitCastle) || Util2.isBoring(bot.countryArray[i])) {
					incoming[i].add(stack);
				} else {
					remaining[i].add(stack);
				}
			}
		}
	}
	
	//
	// nice placement and movement functions
	//
    
    public void placeInfantry(Country c, int number) {
    	if(number > 0) bot.world.placeUnits(new UnitStack(new UnitPawn(bot.me), number), c);
    }
    
    public void placeKnights(Country c, int number) {
    	if(number > 0) bot.world.placeUnits(new UnitStack(new UnitKnight(bot.me), number), c);
    }
    
   
    
    private void moveUnit(Country from, Country to, int count, boolean infantry) {
        if(count == 0) return;
        
    	int numAvailable = (infantry ? remainingInfantry(from).getTotalUnitCount() :
    			                       remainingKnights (from).getTotalUnitCount()  );

    	if(count < 0 || count > numAvailable) {
    		throw new RuntimeException("Bad count in moveInfantry(" + from.getName() + ", " + to.getName() + 
    				                   ", " + count + ")");
    	} else {
    		int moved = 0;
    		while(moved < count) {
    			UnitStack toBeMoved = null;
    			UnitStackGroupIterator stacks = new UnitStackGroupIterator(remaining[from.getID()]);
    			while(stacks.hasNext()) {
    				UnitStack stack = stacks.next();
    				if(stack.getCount() > 0) {
    					if(( infantry && stack.getUnit() instanceof UnitPawn) ||
    					   (!infantry && stack.getUnit() instanceof UnitKnight  )) {
    						if(toBeMoved == null ||
    						   stack.getOwner().getID() < toBeMoved.getOwner().getID()) { //arbitrary deterministic ordering criterion
    							toBeMoved = stack;
    						}
    					}
    				}
    			}

    			int numToMove = Math.min(count - moved, toBeMoved.getCount());

    			if(toBeMoved.getOwner().equals(bot.me) && !from.equals(to)) {
    				//System.out.println(bot.world.getPlayer(bot.id).getName() + 
    				//		           " ordering move of " + numToMove + (infantry ? " infantry" : " knights") +
    				// 	               "from " + from.getName() + "(" + from.getID() + ") to " + to.getName() +
    				//	               "(" + to.getID() + ")");
    				if(!bot.world.unitCanReach(toBeMoved, from, to)) {
    				    throw new RuntimeException("BAD MOVE COMMAND IN moveUnit(" + from + ", " + to + ", " + 
    				    		                   count + ", " + infantry + ")  (chose stack " + toBeMoved + ")");
    				} else {
    					bot.world.moveUnit(toBeMoved, from, to, numToMove);
    				}
    			}

    			incoming[to.getID()].add(new UnitStack(toBeMoved.getUnit(), numToMove));
    			incoming[to.getID()].consolidateUnits();

    			toBeMoved.setCount(toBeMoved.getCount() - numToMove); //updates remaining

    			moved += numToMove;
    		}
    	}

    }
   
    public void moveInfantry(Country from, Country to, int count) {
    	moveUnit(from, to, count, true);
    }
    
    public void moveKnights(Country from, Country to, int count) {
    	moveUnit(from, to, count, false);
    }
    
    public void moveRemainingInfantry(Country from, Country to) {
    	moveInfantry(from, to, remainingInfantry(from).getTotalUnitCount());
    }
    
    public void moveRemainingKnights(Country from, Country to) {
    	moveKnights(from, to, remainingKnights(from).getTotalUnitCount());
    }
    
    public void reserveInfantry(Country c, int count) {
    	moveInfantry(c, c, count);    	
    }
    
    public void reserveKnights(Country c, int count) {
    	moveKnights(c, c, count);
    }
    
    
    
    public UnitStackGroup originalGroup(Country c) {
    	return Simulation2.clone(original[c.getID()]);
    }
    
    public UnitStackGroup remainingGroup(Country c) {
    	return Simulation2.clone(remaining[c.getID()]);
    }
    
    public UnitStackGroup incomingGroup(Country c) {
    	return Simulation2.clone(incoming[c.getID()]);
    }
    
    public UnitStackGroup originalInfantry(Country c) {
    	return Util2.groupInfantry(original[c.getID()]);
    }
    
    public UnitStackGroup originalKnights(Country c) {
    	return Util2.groupKnights(original[c.getID()]);
    }

    public UnitStackGroup originalCastles(Country c) {
    	return Util2.groupCastles(original[c.getID()]);
    }

    
    public UnitStackGroup remainingInfantry(Country c) {
    	return Util2.groupInfantry(remaining[c.getID()]);
    }
    
    public UnitStackGroup remainingKnights(Country c) {
    	return Util2.groupKnights(remaining[c.getID()]);
    }
    
    public UnitStackGroup incomingInfantry(Country c) {
    	return Util2.groupInfantry(incoming[c.getID()]);
    }
    
    public UnitStackGroup incomingKnights(Country c) {
    	return Util2.groupKnights(incoming[c.getID()]);
    }
   
    public UnitStackGroup combinedOriginalGroup(List<Country> countries) {
    	UnitStackGroup ret = new UnitStackGroup();
    	for(Country c : countries) {
    		ret.add(original[c.getID()]);
    	}
    	return ret;
    }
    
    public UnitStackGroup combinedRemainingGroup(List<Country> countries) {
    	UnitStackGroup ret = new UnitStackGroup();
    	for(Country c : countries) {
    		ret.add(remaining[c.getID()]);
    	}
    	return ret;
    }
    
    public UnitStackGroup combinedIncomingGroup(List<Country> countries) {
    	UnitStackGroup ret = new UnitStackGroup();
    	for(Country c : countries) {
    		ret.add(incoming[c.getID()]);
    	}
    	return ret;
    }
}
