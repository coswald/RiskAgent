



import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import java.util.*;

/* 
 * Util provides utility methods. You can e.g. find stacks of a desired type in a country. You can also
 * get a list of a player or team's countries and for a given country you can find
 * its neighbors that are hostile or that border hostile countries.
 */

@SuppressWarnings("unchecked")
public class Util2 {    
    
	Polymath bot;
	
	Country[][] incomingConnections; //incomingConnections[i] is the list of countries with a link toward #i
	
	boolean inited = false;
	
	public Util2(Polymath bot) {
    	this.bot = bot;
    }

	public void initMap() {
		if(inited) return;
		
		incomingConnections = new Country[bot.countryArray.length][];
		for(Country dest : bot.countryArray) {
			ArrayList<Country> connections = new ArrayList<Country>();
			for(Country source : bot.countryArray) {
				if(realOutgoingList(source).contains(dest)) {
					 connections.add(source);
				}
			}
			incomingConnections[dest.getID()] = connections.toArray(new Country[connections.size()]);
		}
		
		inited = true;
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
    
    public static UnitStackGroup teamSubgroup(UnitStackGroup g, Team t) {
    	UnitStackGroup ret = new UnitStackGroup();
    	UnitStackGroupIterator stacks = new UnitStackGroupIterator(g);
    	while(stacks.hasNext()) {
    		UnitStack stack = stacks.next();
    		if(stack.getTeam().equals(t)) {
    			ret.add(stack);
    		}
    	}
    	return ret;
    }
    
    public static UnitStackGroup excludeTeam(UnitStackGroup g, Team t) {
    	UnitStackGroup ret = new UnitStackGroup();
    	UnitStackGroupIterator stacks = new UnitStackGroupIterator(g);
    	while(stacks.hasNext()) {
    		UnitStack stack = stacks.next();
    		if(!stack.getTeam().equals(t)) {
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
     
    public static ArrayList<UnitStackGroup> splitByTeam(UnitStackGroup g, List<Team> teams) {
    	ArrayList<UnitStackGroup> ret = new ArrayList<UnitStackGroup>(teams.size());
    	for(int i = 0; i < teams.size(); i++) {
    		ret.add(teamSubgroup(g, teams.get(i)));
    	}
    	return ret;
    }
    
    public static UnitStackGroup combine(UnitStackGroup a, UnitStackGroup b) {
    	UnitStackGroup ret = new UnitStackGroup();
    	ret.add(a);
    	ret.add(b);
    	return ret;
    }
    
    
    
    //
    // Functions for finding specific subsets of a list of countries:
    //
    
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
    // Functions that look at neighbors:
    //    
    
    public List<Country> realOutgoingList(Country c) {
    	List<Country> fakeOutgoingList = (List<Country>)c.getAdjoiningList();
    	List<Country> ret = new ArrayList<Country>();
    	for(Country f : fakeOutgoingList) {
    		ret.add(bot.countryArray[f.getID()]);
    	}
    	return ret;
    }
    
    public List<Country> realIncomingList(Country c) {
    	Country[] fakeIncomingList = incomingConnections[c.getID()];
    	List<Country> ret = new ArrayList<Country>();
    	for(Country f : fakeIncomingList) {
    		ret.add(bot.countryArray[f.getID()]);
    	}
    	return ret;
    }
    
    
    public ArrayList<Country> countriesInIncomingRange(Country c, int range, boolean safe) {
    	ArrayList<Country> ret = new ArrayList<Country>();
    	ret.add(c);
    	ret.addAll(realIncomingList(c)); 
    	if(safe) ret = teamCountries(c.getTeam(), ret);
    	for(int i = 2; i <= range; i++) {
    		List<Country> newRet = new ArrayList<Country>();
    		for(Country r : ret) {
    			for(Country n : realIncomingList(r)) {
    				if(!newRet.contains(n) && !ret.contains(n) && !n.equals(c) 
    				   && (!safe || n.getTeam().equals(c.getTeam()))) {
    					newRet.add(n);
    				}
    			}
    		}
    		ret.addAll(newRet);
    	}
    	return ret;
    }
    
    public ArrayList<Country> countriesInOutgoingRange(Country c, int range, boolean safe) {
    	ArrayList<Country> ret = new ArrayList<Country>();
    	ret.add(c);
    	ret.addAll(realOutgoingList(c));    	
    	if(safe) ret = teamCountries(c.getTeam(), ret);
        for(int i = 2; i <= range; i++) {
    		List<Country> newRet = new ArrayList<Country>();
    		for(Country r : ret) {
    			for(Country n : realOutgoingList(r)) {
    				if(!newRet.contains(n) && !ret.contains(n) && !n.equals(c)
   					   && (!safe || n.getTeam().equals(c.getTeam()))) {
    					newRet.add(n);
    				}
    			}
    		}
    		ret.addAll(newRet);
    	}
    	return ret;
    }
    
    
    public UnitStackGroup potentialArrivers(Country c) {
    	UnitStackGroup ret = bot.troops.incomingGroup(c);
    	
    	List<Country> nearby = countriesInIncomingRange(c, 2, false);
    	for(Country n : nearby) {
    		UnitStackGroupIterator units = new UnitStackGroupIterator(bot.troops.remainingGroup(n));
    		while(units.hasNext()) {
    			UnitStack unit = units.next();
    			if(c.equals(n) || bot.world.unitCanReach(unit, n, c)) ret.add(unit); 
    		}
    	}
    	
    	return ret;
    }
    

    public UnitStackGroup potentialArriversFromContinent(Country c, int contID) {
    	UnitStackGroup ret = new UnitStackGroup();
    	
    	List<Country> nearby = countriesInIncomingRange(c, 2, false);
    	for(Country n : nearby) {
    		if(n.getContinentID() == contID) {
    			UnitStackGroupIterator units = new UnitStackGroupIterator(bot.troops.remainingGroup(n));
    		    while(units.hasNext()) {
    		   	    UnitStack unit = units.next();
    			    if(c.equals(n) || bot.world.unitCanReach(unit, n, c)) ret.add(unit); 
    		    }
    		}
    	}
    	
    	return ret;
    }

    public UnitStackGroup potentialAttackers(Country c) {
    	return excludeTeam(potentialArrivers(c), c.getTeam());
    }
    
    public UnitStackGroup potentialAttackersFromContinent(Country c, int contID) {
    	return excludeTeam(potentialArriversFromContinent(c, contID), c.getTeam());
    }
    
    public UnitStackGroup potentialDefenders(Country c) {
    	return teamSubgroup(potentialArrivers(c), c.getTeam());
    }
    
    public ArrayList<Country> hostileIncomingNeighbors(Country c) {
    	return hostileCountries(c.getTeam(), realIncomingList(c));
    }

    public ArrayList<Country> hostileOutgoingNeighbors(Country c) {
    	return hostileCountries(c.getTeam(), realOutgoingList(c));
    }

    public ArrayList<Country> friendlyIncomingNeighbors(Country c) {
    	return teamCountries(c.getTeam(), realIncomingList(c));
    }
    
    public boolean isBorder(Country c) {
    	return hostileIncomingNeighbors(c).size() > 0;
    }
    
    public boolean isEdge(Country c) {
    	return hostileOutgoingNeighbors(c).size() > 0;
    }
    
    public ArrayList<Country> friendlyIncomingNeighborBorders(Country c) {
    	ArrayList<Country> ret = new ArrayList<Country>();
        for(Country n : teamCountries(c.getTeam(), realIncomingList(c))) {
        	if(isBorder(n)) {
        		ret.add(n);
        	}
        }
        return ret;
    }

    public ArrayList<Country> knightDestinations(Country origin) {
    	List<Country> neighbors = realOutgoingList(origin);
    	ArrayList<Country> ret = new ArrayList<Country>(neighbors);
        for(Country n : neighbors) {
        	if(n.getTeam().equals(origin.getTeam()) || 
        	   bot.troops.originalGroup(n).getTotalUnitCount() == 0) {
        		for(Country n2 : realOutgoingList(n)) {
        		    if(!ret.contains(n2)) {
        		    	ret.add(n2);
        		    }
        		}
        	}
        }
        return ret;
    }

    public ArrayList<Country> knightSources(Country target) {
    	List<Country> neighbors = realIncomingList(target);
    	ArrayList<Country> ret = new ArrayList<Country>(neighbors);
    	for(Country n : neighbors) {
    		for(Country n2 : realIncomingList(n)) {
    			if(n.getTeam().equals(n2.getTeam()) || 
    			   bot.troops.originalGroup(n).getTotalUnitCount() == 0) {
    				if(!ret.contains(n2)) {
    					ret.add(n2);
    				}
    			}
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
    	for(Country n : realIncomingList(c)) {
    		if(n.getContinentID() != c.getContinentID()) return true;
    	}
    	return false;
    }

    public boolean isContinentEdge(Country c) {
    	for(Country n : realOutgoingList(c)) {
    		if(n.getContinentID() != c.getContinentID()) return true;
    	}
    	return false;
    }

    //does not check to see if we own c's continent
    public boolean weOwnIncomingNeighborContinents(Country c) {
    	for(Country n : realIncomingList(c)) {
    		if(c.getContinentID() != n.getContinentID() &&
    		   !bot.us.equals(continentOwner(n.getContinentID()))) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    public static ArrayList<Country> continentCountries(int contID, List<Country> countries) {
    	ArrayList<Country> ret = new ArrayList<Country>();
    	for(Country c : countries) {
    		if(c.getContinentID() == contID) {
    			ret.add(c);
    		}
    	}
    	return ret;
    }
    
    //
    // MISC
    //
    
    
    public static ArrayList<Country> teamCastles(Team t, List<Country> countries) {
    	ArrayList<Country> ret = new ArrayList<Country>();
    	for(Country c : countries) {
    		if(c.hasCastle() && c.getTeam().equals(t)) ret.add(c);
    	}
    	return ret;
    }
    
    public static <T> List<T> setSubtract(List<T> a, List<T> b) {
    	List<T> ret = new ArrayList<T>();
    	for(T t : a) {
    		if(!b.contains(t)) ret.add(t);
    	}
    	return ret;
    }

    public static <T> List<T> setIntersect(List<T> a, List<T> b) {
    	List<T> ret = new ArrayList<T>();
    	for(T t : a) {
    		if(b.contains(t)) ret.add(t);
    	}
    	return ret;
    }
    
    public static boolean isBoring(Player p) {
    	return (!p.isHuman() && p.getBrain().equals("Boring"));
    }
    
    public static boolean isBoring(Country c) {
    	return isBoring(c.getOwner());
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
