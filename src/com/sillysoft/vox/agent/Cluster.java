

import com.sillysoft.vox.*;

import java.util.*;


/* A Cluster is a group of connected countries. Clusters can easily be
 * created from the set of countries owned by a player or team. This class
 * makes it easy to find the edge of a cluster and its border with the
 * enemy. Just for fun Cluster implements Collection<Country> so that you
 * can do for-each statements with Clusters.
 */

@SuppressWarnings("unchecked")
public class Cluster implements Collection<Country> {
    ArrayList<Country> countries;
    
    public Cluster() {
    	countries = new ArrayList<Country>();
    }
    
    public static ArrayList<Cluster> findClusters(List<Country> countries, Util util) {
    	ArrayList<Cluster> ret = new ArrayList<Cluster>();
    	for(Country seed : countries) {
    		boolean unclustered = true;
    		for(Cluster existing : ret) {
    			if(existing.contains(seed)) {
    				unclustered = false;
    				break;
    			}
    		}
    		
    		if(unclustered) {
    			Cluster newCluster = Cluster.clusterFromSeed(seed, countries, util);
    			ret.add(newCluster);
    		} else {
    			//System.out.println("...Country " + seed + " already clustered.");
    		}
    	}
    	return ret;
    }
    
	public static Cluster clusterFromSeed(Country seed, List<Country> countries, Util util) {
    	Cluster ret = new Cluster();
    	Queue<Country> newMembers = new ArrayDeque<Country>();
         
    	ret.add(seed);
        newMembers.add(seed);
        
    	while(newMembers.size() > 0) {
    		Country newMember = newMembers.remove();
    		
    		for(Country n : util.realAdjoiningList(newMember)) {
    			if(!ret.contains(n) && countries.contains(n)) {
    				ret.add(n);
    				newMembers.add(n);
    			}
    		}
    	}
    	
    	return ret;
    }
	
	public static ArrayList<Cluster> playerClusters(Player p, List<Country> countries, Util util) {
		return Cluster.findClusters(Util.playerCountries(p, countries), util);
	}
	
	public static ArrayList<Cluster> teamClusters(Team t, List<Country> countries, Util util) {
		return Cluster.findClusters(Util.teamCountries(t, countries), util);
	}
	
	public ArrayList<Country> getEdge(Util util) {
		ArrayList<Country> ret = new ArrayList<Country>();
		
		for(Country c : countries) {
			for(Country n : util.realAdjoiningList(c)) {
				if(!countries.contains(n)) {
					ret.add(c);
					break;
				}
			}
		}
		
		return ret;
	}
	
	public ArrayList<Country> getFriendlyBorder(Util util) {
		ArrayList<Country> ret = new ArrayList<Country>();
		for(Country c : countries) {
			if(util.isBorder(c)) {
				ret.add(c);
			}
		}
		return ret;
	}
	
	public ArrayList<Country> getHostileBorder(Util util) {
		ArrayList<Country> ret = new ArrayList<Country>();
		Team clusterTeam = countries.get(0).getTeam();
		for(Country c : countries) {
			for(Country n : util.hostileNeighbors(c)) {
				if(!n.getTeam().equals(clusterTeam) && !ret.contains(n)) {
					ret.add(n);
					break;
				}
			}
		}
		
		return ret;
	}
	
    public boolean add(Country c) {
    	return countries.add(c);
    }
    
    public boolean remove(Object c) {
    	return countries.remove(c);
    }
    
    public void remove(int i) {
    	countries.remove(i);
    }
    
    public int size() {
    	return countries.size();
    }
    
    public Country get(int i) {
    	return countries.get(i);
    }
    
    public boolean contains(Object c) {
    	return countries.contains(c);
    }
    
    public String toString() {
    	return Util.listToString(countries);
    }
    
    
    //Collection methods:
    public void clear() {
    	countries.clear();
    }
    
    public boolean isEmpty() {
    	return countries.isEmpty();
    }
    
    public Object[] toArray() {
    	return countries.toArray();
    }
    
    public <T> T[] toArray(T[] a) {
    	return countries.toArray(a);
    }
    
    public boolean removeAll(Collection c) {
    	return countries.removeAll(c);
    }
    
    public boolean retainAll(Collection c) {
    	return countries.retainAll(c);
    }
    
    public boolean containsAll(Collection c) {
    	return countries.containsAll(c);
    }
    
    public boolean addAll(Collection<? extends Country> c) {
    	return countries.addAll(c);
    }
    
    public Iterator<Country> iterator() {
    	return new ClusterIterator(this);
    }
}

class ClusterIterator implements Iterator<Country> {
	int index; //index just provided by next
	Cluster c;
	
	public ClusterIterator(Cluster c) {
		this.c = c;
		index = -1;
	}
	
	public void remove() {
		if(index == -1) throw new IllegalStateException();
		
		c.remove(index);
		index--;
	}
	
	public boolean hasNext() {
		return index < c.size() - 1;
	}
	
	public Country next() {
		if(!hasNext()) throw new NoSuchElementException();
		
		index++;
		return c.get(index);
	}
}