




import com.sillysoft.vox.*;

import java.util.*;




/*
 * Pathfinder helps find minimal Paths between countries. 
 * It can find the shortest path from a country to a hostile country.
 * It can find the shortest path from one country to another, optionally
 *   avoiding enemy countries.
 * It can find the shortest path from a country to a Collection<Country>.
 */

public class Pathfinder {
	
	Util util;
	
	public Pathfinder(Util util) {
		this.util = util;
	}
	
	public Path pathToCollection(Country start, Collection<Country> col, boolean safe) {
		if(col.contains(start)) return new Path(start);
		
		Queue<Path> pathQueue = new ArrayDeque<Path>();
    	pathQueue.add(new Path(start));
    	
    	boolean[] seen = new boolean[util.bot.countryArray.length];
    	seen[start.getID()] = true;
    	
    	Team startTeam = start.getTeam();
    	
    	while(pathQueue.size() > 0) {
    		Path partial = pathQueue.remove();
    		Country last = partial.getLast();
    		
    		for(Country n : util.realAdjoiningList(last)) {
    			if(!seen[n.getID()] &&
    			   (!safe || n.getTeam().equals(startTeam))) { 
    				
    				Path expanded = new Path(partial, n);
    				if(col.contains(n)) {
    					return expanded;
    				} else {
    					pathQueue.add(expanded);
    					seen[n.getID()] = true;
    				}
    			}
    		}
    	}
    	
    	return null;
	}

	public Path pathBetween(Country start, Country end, boolean safe) {
		return pathToCollection(start, Collections.singletonList(end), safe);
	}	
	
	public Path pathToEnemy(Country start, List<Country> countries) {
		return pathToCollection(start, Util.hostileCountries(start.getTeam(), countries), false);
	}
	    
}
