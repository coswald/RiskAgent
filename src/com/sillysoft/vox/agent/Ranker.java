
import java.util.ArrayList;
import java.util.List;


public abstract class Ranker<T> {
	abstract public double rank(T t);

	static <T> List<T> sortByRanker(List<T> list, Ranker<T> ranker) {
		List<T> ret = new ArrayList<T>(list.size());
		
		double[] ranks = new double[list.size()];
		boolean[] used = new boolean[list.size()];
		
		for(int i = 0; i < list.size(); i++) {
			ranks[i] = ranker.rank(list.get(i));
			used[i] = false;
		}
		
		for(int i = 0; i < list.size(); i++) {
			int bestIndex = -1;
			double bestRank = Double.NEGATIVE_INFINITY;
			for(int j = 0; j < list.size(); j++) {
				if(!used[j] && ranks[j] > bestRank) {
					bestRank = ranks[j];
					bestIndex = j;
				}
			}
			
			if(bestIndex == -1) {
				break; 
			} else {
				ret.add(list.get(bestIndex));
				used[bestIndex] = true;
			}
		}
		
		return ret;
	}
	
	static <T> T bestByRanker(List<T> list, Ranker<T> ranker) {
		T bestT = null;
		double bestRank = Double.NEGATIVE_INFINITY;
		
		for(T t : list) {
			double rank = ranker.rank(t);
			if(rank > bestRank) {
				bestRank = rank;
				bestT = t;
			}
		}
		
		return bestT;
	}
}




class RankerPriorityQueue<T> {
    ArrayList<T> elements = new ArrayList<T>();
    ArrayList<Double> ranks = new ArrayList<Double>();
    Ranker<T> ranker;
    
    public RankerPriorityQueue(Ranker<T> ranker) {
    	this.ranker = ranker;
    }
    
    public void push(T t) {
    	double rank = ranker.rank(t);
    	if(rank > Double.NEGATIVE_INFINITY) {
    		elements.add(t);
       	    ranks.add(ranker.rank(t));
    	}
    }
    
    public T pop() {
    	if(size() == 0) return null;
    	
    	double bestRank = ranks.get(0);
        int bestIndex = 0;

    	for(int i = 1; i < ranks.size(); i++) {
    		if(ranks.get(i) > bestRank) {
    			bestRank = ranks.get(i);
    			bestIndex = i;
    		}
    	}
    	
    	T bestT = elements.get(bestIndex);
    	elements.remove(bestIndex);
    	ranks.remove(bestIndex);
    	return bestT;
    }
    
    public int size() {
    	return elements.size();
    }
}