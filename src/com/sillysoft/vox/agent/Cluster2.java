


import com.sillysoft.vox.*;

import java.util.*;


//We define a cluster as a group of countries that are all reachable from some
//seed country (a member of the cluster) by some path through the cluster. Note: Because
//of one-way connections we cannot assume that there exists a path from any 
//member of a cluster to any other member within the cluster. Just for fun
//Cluster implements Collection<Country> so that you can do for-each statements.

@SuppressWarnings("unchecked")
public class Cluster2 implements Collection<Country> {
    ArrayList<Country> countries;
    
    public Cluster2() {
    	countries = new ArrayList<Country>();
    }
    
    public static ArrayList<Cluster2> findClusters(List<Country> countries, Util2 util) {
    	ArrayList<Cluster2> ret = new ArrayList<Cluster2>();
    	for(Country seed : countries) {
    		boolean unclustered = true;
    		for(Cluster2 existing : ret) {
    			if(existing.contains(seed)) {
    				unclustered = false;
    				break;
    			}
    		}
    		
    		if(unclustered) {
    			Cluster2 newCluster = Cluster2.clusterFromSeed(seed, countries, util);
    			ret.add(newCluster);
    		} else {
    			//System.out.println("...Country " + seed + " already clustered.");
    		}
    	}
    	return ret;
    }
    
	public static Cluster2 clusterFromSeed(Country seed, List<Country> countries, Util2 util) {
    	Cluster2 ret = new Cluster2();
    	Queue<Country> newMembers = new ArrayDeque<Country>();
         
    	ret.add(seed);
        newMembers.add(seed);
        
    	while(newMembers.size() > 0) {
    		Country newMember = newMembers.remove();
    		
    		for(Country n : util.realOutgoingList(newMember)) {
    			if(!ret.contains(n) && countries.contains(n)) {
    				ret.add(n);
    				newMembers.add(n);
    			}
    		}
    	}
    	
    	return ret;
    }
	
	public static ArrayList<Cluster2> teamClusters(Team t, List<Country> countries, Util2 util) {
		return Cluster2.findClusters(Util2.teamCountries(t, countries), util);
	}
	
	public ArrayList<Country> getCountries() {
		return new ArrayList<Country>(countries);
	}
	
	public ArrayList<Country> getBorder(Util2 util) {
		ArrayList<Country> ret = new ArrayList<Country>();
		
		for(Country c : countries) {
			for(Country n : util.realIncomingList(c)) {
				if(!countries.contains(n)) {
					ret.add(c);
					break;
				}
			}
		}
		
		return ret;
	}
	
	public ArrayList<Country> getNonBorder(Util2 util) {
        ArrayList<Country> ret = new ArrayList<Country>();
		ArrayList<Country> border = getBorder(util);
		
		for(Country c : countries) {
			if(!border.contains(c)) ret.add(c);
		}
		
		return ret;
	}
	
	public ArrayList<Country> getEdge(Util2 util) {
		ArrayList<Country> ret = new ArrayList<Country>();
		
		for(Country c : countries) {
			for(Country n : util.realOutgoingList(c)) {
				if(!countries.contains(n)) {
					ret.add(c);
					break;
				}
			}
		}
		
		return ret;
	}
	
	public ArrayList<Country> getNonEdge(Util2 util) {
        ArrayList<Country> ret = new ArrayList<Country>();
		ArrayList<Country> border = getEdge(util);
		
		for(Country c : countries) {
			if(!border.contains(c)) ret.add(c);
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
    	return Util2.listToString(countries);
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
    	return new ClusterIterator2(this);
    }
}

class ClusterIterator2 implements Iterator<Country> {
	int index; //index just provided by next
	Cluster2 c;
	
	public ClusterIterator2(Cluster2 c) {
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