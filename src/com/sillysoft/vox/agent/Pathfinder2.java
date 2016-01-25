





import com.sillysoft.vox.*;

import java.util.*;




/*
 * Pathfinder helps find minimal Paths between countries. 
 * It can find the shortest path from a country to a hostile country.
 * It can find the shortest path from one country to another, optionally
 *   avoiding enemy countries.
 * It can find the shortest path from a country to a Collection<Country>.
 */

public class Pathfinder2 {
	
	Util2 util;
	
	public Pathfinder2(Util2 util) {
		this.util = util;
	}
	
	static Ranker<Path2> shortestPathRanker = new Ranker<Path2> () {
		public double rank(Path2 p) {
			return -p.size();
		}
	};
	
	static Ranker<Path2> fewestEnemyDefendersRanker(final Team team, final Troops troops) {
		return new Ranker<Path2>() {
			public double rank(Path2 path) {
				return -path.getEnemyUnitStackGroup(team, troops).getTotalUnitCount();
			}		
		};
	}
	
	
	public Path2 bestPathToCollection(Country start, Collection<Country> col, boolean safe, Ranker<Path2> ranker) {
		RankerPriorityQueue<Path2> pathQueue = new RankerPriorityQueue<Path2>(ranker);
		pathQueue.push(new Path2(start));
		
		boolean[] done = new boolean[util.bot.countryArray.length];
		Team startTeam = start.getTeam();
		
		while(pathQueue.size() > 0) {
			Path2 partial = pathQueue.pop();
			
			Country last = partial.getLast(); 
			if(safe && !startTeam.equals(last.getTeam())) continue;
		    if(done[last.getID()]) continue;   //otherwise partial must be an optimal path to last
			
			if(col.contains(last)) return partial;
			done[last.getID()] = true; 
			
			for(Country n : util.realOutgoingList(last)) {
				pathQueue.push(new Path2(partial, n));
			}
		}
		
		return null;
	}

	
	public Path2 shortestPathBetween(Country start, Country end, boolean safe) {
		return bestPathToCollection(start, Collections.singletonList(end), safe, shortestPathRanker);
	}	
	
	public Path2 shortestPathToEnemy(Country start, List<Country> countries) {
		return bestPathToCollection(start, Util2.hostileCountries(start.getTeam(), countries), false, shortestPathRanker);
	}
	
	public Path2 leastDefendedPathBetween(Country start, Country end, Team t, Troops troops) {
		return bestPathToCollection(start, Collections.singletonList(end), false, fewestEnemyDefendersRanker(t, troops));
	}
	
	public Path2 leastDefendedPathToGroup(Country start, List<Country> group, Team t, Troops troops) {
		return bestPathToCollection(start, group, false, fewestEnemyDefendersRanker(t, troops));
	}
	
	public Path2 bestPathBetween(Country start, Country end, boolean safe, Ranker<Path2> ranker) {
		return bestPathToCollection(start, Collections.singletonList(end), safe, ranker);
	}
}