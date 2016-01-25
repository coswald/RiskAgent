


import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import java.util.*;

/* 
 * Util provides utility methods. There are functions that make it slightly nicer to
 * move units around or to find stacks of a desired type in a country. You can also
 * get a list of a player or team's countries and for a given country you can find
 * its neighbors that are hostile or that border hostile countries.
 */

@SuppressWarnings("unchecked")
public class Util {    
    
	Hawk bot;
	
	UnitStackGroup[] original;  //units in country at start of turn
	UnitStackGroup[] remaining; //units which have not yet been explicitly ordered to move or stay put
	UnitStackGroup[] incoming;  //units have been ordered to show up here next turn
	                            // -this includes both those coming from nearby countries
	                            //  as well as those explicitly ordered to remain here
	
	public Util(Hawk bot) {
    	this.bot = bot;
    }

	
	public void initTurn() {
		original = new UnitStackGroup[bot.countryArray.length];
		remaining = new UnitStackGroup[bot.countryArray.length];
		incoming = new UnitStackGroup[bot.countryArray.length];
		for(int i = 0; i < bot.countryArray.length; i++) {
			if(bot.countryArray[i].getTeam().equals(bot.us)) {
				original[i]  = Simulation.clone(bot.countryArray[i].getUnitStackGroup());
				remaining[i] = Simulation.clone(bot.countryArray[i].getUnitStackGroup());
			} else {
				original[i] = new UnitStackGroup();
				remaining[i] = new UnitStackGroup();
			}
			incoming[i] = new UnitStackGroup();
		}
	}
	
	//
	// nice placement and movement functions
	//
    
    public void placeInfantry(Country c, int number) {
    	bot.world.placeUnits(new UnitStack(new UnitPawn(bot.me), number), c);
    }
    
    public void placeKnights(Country c, int number) {
    	bot.world.placeUnits(new UnitStack(new UnitKnight(bot.me), number), c);
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
    				//System.out.println("ordering move of " + numToMove + (infantry ? " infantry" : " knights") +
    				// 	               "from " + from.getName() + "(" + from.getID() + ") to " + to.getName() +
    				//	               "(" + to.getID() + ")");
    				bot.world.moveUnit(toBeMoved, from, to, numToMove);
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
    	return original[c.getID()];
    }
    
    public UnitStackGroup remainingGroup(Country c) {
    	return remaining[c.getID()];
    }
    
    public UnitStackGroup incomingGroup(Country c) {
    	return incoming[c.getID()];
    }
    
    public UnitStackGroup originalInfantry(Country c) {
    	return groupInfantry(original[c.getID()]);
    }
    
    public UnitStackGroup originalKnights(Country c) {
    	return groupKnights(original[c.getID()]);
    }
    
    public UnitStackGroup remainingInfantry(Country c) {
    	return groupInfantry(remaining[c.getID()]);
    }
    
    public UnitStackGroup remainingKnights(Country c) {
    	return groupKnights(remaining[c.getID()]);
    }
    
    public UnitStackGroup incomingInfantry(Country c) {
    	return groupInfantry(incoming[c.getID()]);
    }
    
    public UnitStackGroup incomingKnights(Country c) {
    	return groupKnights(incoming[c.getID()]);
    }
   
    
    
    //
    // UnitStackGroup utilities:   
    //
    
    public static UnitStack stackFromGroup(UnitStackGroup g, Unit u) {
    	UnitStackGroupIterator stacks = new UnitStackGroupIterator(g);
    	while(stacks.hasNext()) {
    		UnitStack stack = stacks.next();
    		if(stack.getOwner().equals(u.getOwner())) {
                if((u instanceof UnitPawn && stack.getUnit() instanceof UnitPawn) ||
                   (u instanceof UnitKnight   && stack.getUnit() instanceof UnitKnight  ) ||
                   (u instanceof UnitCastle   && stack.getUnit() instanceof UnitCastle  )) {
            	    return stack;
                }
            }
    	}
    	return new UnitStack(u, 0);
    }
    
    public static int numInfantry(UnitStackGroup g) {
    	return groupInfantry(g).getTotalUnitCount();
    }
    
    public static int numKnights(UnitStackGroup g) {
    	return groupKnights(g).getTotalUnitCount();
    }

    public static int numCastles(UnitStackGroup g) {
    	return groupCastles(g).getTotalUnitCount();
    }
    
    public static UnitStackGroup playerSubgroup(UnitStackGroup g, Player p) {
    	UnitStackGroup ret = new UnitStackGroup();
    	UnitStackGroupIterator stacks = new UnitStackGroupIterator(g);
    	while(stacks.hasNext()) {
    		UnitStack stack = stacks.next();
    		if(stack.getOwner().equals(p)) {
    			ret.add(stack);
    		}
    	}
    	return ret;
    }
    
    public static UnitStackGroup groupFromStack(UnitStack stack) {
    	UnitStackGroup ret = new UnitStackGroup();
    	ret.add(stack);
    	return ret;
    }
    
    public static UnitStackGroup groupInfantry(UnitStackGroup g) {
    	UnitStackGroup ret = new UnitStackGroup();
    	UnitStackGroupIterator units = new UnitStackGroupIterator(g);
    	while(units.hasNext()) {
    		UnitStack stack = units.next();
    		if(stack.getUnit() instanceof UnitPawn) ret.add(stack);
    	}
    	return ret;
    }
    
    public static UnitStackGroup groupKnights(UnitStackGroup g) {
    	UnitStackGroup ret = new UnitStackGroup();
    	UnitStackGroupIterator units = new UnitStackGroupIterator(g);
    	while(units.hasNext()) {
    		UnitStack stack = units.next();
    		if(stack.getUnit() instanceof UnitKnight) ret.add(stack);
    	}
    	return ret;
    }
    
    public static UnitStackGroup groupCastles(UnitStackGroup g) {
    	UnitStackGroup ret = new UnitStackGroup();
    	UnitStackGroupIterator units = new UnitStackGroupIterator(g);
    	while(units.hasNext()) {
    		UnitStack stack = units.next();
    		if(stack.getUnit() instanceof UnitCastle) ret.add(stack);
    	}
    	return ret;
    }
    
    //
    // Functions that look at neighbors:
    //    
    
    public List<Country> realAdjoiningList(Country c) {
    	List<Country> fakeAdjoiningList = (List<Country>)c.getAdjoiningList();
    	List<Country> ret = new ArrayList<Country>();
    	for(Country f : fakeAdjoiningList) {
    		ret.add(bot.countryArray[f.getID()]);
    	}
    	return ret;
    }
    
    public UnitStackGroup potentialAttackers(Country c) {
    	if(!c.getTeam().equals(bot.us)) {
    		throw new RuntimeException("potentialAttackers should only be called on team countries!");
    	}
    	
    	UnitStackGroup ret = new UnitStackGroup();
    	
    	List<Country> nearby = countriesInRange(c, 2);
    	for(Country n : nearby) {
    		if(n.getTeam().equals(c.getTeam())) continue;
    		
    		UnitStackGroupIterator units = new UnitStackGroupIterator(n.getUnitStackGroup());
    		while(units.hasNext()) {
    			UnitStack unit = units.next();
    			if(bot.world.unitCanReach(unit, n, c)) ret.add(unit); 
    		}
    	}
    	
    	return ret;
    }
       
    public ArrayList<Country> hostileNeighbors(Country c) {
    	ArrayList<Country> ret = new ArrayList<Country>();
    	Team countryTeam = c.getTeam();
    	for(Country n : realAdjoiningList(c)) {
    		if(!n.getTeam().equals(countryTeam)) {
    			ret.add(n);
    		}
    	}
    	return ret;
    }
    
    public boolean isPlayerEdge(Country c) {
        Player countryOwner = c.getOwner();
    	for(Country n : realAdjoiningList(c)) {
        	if(!n.getOwner().equals(countryOwner)) {
        		return true;
        	}
        }
    	return false;
    }
    
    public boolean isBorder(Country c) {
    	Team countryTeam = c.getTeam();
    	for(Country n : realAdjoiningList(c)) {
        	if(!n.getTeam().equals(countryTeam)) {
        		return true;
        	}
        }
    	return false; 
    }
    
    public ArrayList<Country> neighboringBorders(Country c) {
    	ArrayList<Country> ret = new ArrayList<Country>();
        Player countryOwner = c.getOwner();
    	for(Country n : realAdjoiningList(c)) {
        	if(n.getOwner().equals(countryOwner) && isBorder(n)) {
        		ret.add(n);
        	}
        }
        return ret;
    }
    
    public List<Country> countriesInRange(Country c, int range) {
    	List<Country> ret = new ArrayList<Country>();
    	ret.addAll(realAdjoiningList(c));    	
    	for(int i = 2; i <= range; i++) {
    		List<Country> newRet = new ArrayList<Country>();
    		for(Country r : ret) {
    			for(Country n : realAdjoiningList(r)) {
    				if(!newRet.contains(n) && !ret.contains(n) && !n.equals(c)) {
    					newRet.add(n);
    				}
    			}
    		}
    		ret.addAll(newRet);
    	}
    	return ret;
    }
    
    public ArrayList<Country> reachableByKnight(Country origin) {
    	List<Country> neighbors = realAdjoiningList(origin);
    	ArrayList<Country> ret = new ArrayList<Country>(neighbors);
        for(Country n : neighbors) {
        	if(n.getTeam().equals(origin.getTeam()) || 
        	   n.getUnitStackGroup().getTotalUnitCount() == 0) {
        		for(Country n2 : realAdjoiningList(n)) {
        		    if(!ret.contains(n2)) {
        		    	ret.add(n2);
        		    }
        		}
        	}
        }
        return ret;
    }
    
    
    //
    // Functions for finding specific subsets of a list of countries:
    //
    
    public static ArrayList<Country> playerCountries(Player p, List<Country> countries) {
    	ArrayList<Country> ret = new ArrayList<Country>();
    	for(Country c : countries) {
    		if(c.getOwner().equals(p)) {
    			ret.add(c);
    		}
    	}
    	return ret;
    }
    
    public static ArrayList<Country> teamCountries(Team t, List<Country> countries) {
    	ArrayList<Country> ret = new ArrayList<Country>();
    	for(Country c : countries) {
    		if(c.getTeam().equals(t)) {
    			ret.add(c);
    		}
    	}
    	return ret;
    }
    
    public static ArrayList<Country> hostileCountries(Team t, List<Country> countries) {
    	ArrayList<Country> ret = new ArrayList<Country>();
    	for(Country c : countries) {
    		if(!c.getTeam().equals(t)) {
    			ret.add(c);
    		}
    	}
    	return ret;
    }
    
    
    //
    // Continent utilities
    //
    
    public Team continentOwner(int contID) {
    	Team owner = null;
    	for(Country c : bot.countryList) {
    		if(c.getContinentID() == contID) {
    			if(owner == null) {
    				owner = c.getTeam();
    			} else {
    				if(!owner.equals(c.getTeam())) {
    					return null;
    				}
    			}
    		}
    	}
    	return owner;
    }
    
    public boolean isContinentBorder(Country c) {
    	for(Country n : realAdjoiningList(c)) {
    		if(n.getContinentID() != c.getContinentID()) return true;
    	}
    	return false;
    }
    
    public boolean weOwnNeighboringContinents(Country c) {
    	for(Country n : realAdjoiningList(c)) {
    		if(!bot.us.equals(continentOwner(n.getContinentID()))) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    
    
    
    //
    // Debugging:
    //
    
    public static String listToString(List list) {
    	String ret = "{";
    	for(int i = 0; i < list.size(); i++) {
    		if(i > 0) ret += ", ";
    		ret += list.get(i).toString();
    	}
    	ret += "}";
    	return ret;
    }
}
