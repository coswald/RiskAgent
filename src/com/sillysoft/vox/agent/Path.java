

import java.util.*;
import com.sillysoft.vox.*;

/* 
 * A Path is just a list of Countries. Paths can be created by
 * the Pathfinder class.
 */

public class Path {
    private ArrayList<Country> countries;
	
	public Path() {
		countries = new ArrayList<Country>();		
	}
	
	public Path(Country c) {
		countries = new ArrayList<Country>();		
        countries.add(c);		
	}
	
	public Path(Path head, Country tail) {
		countries = new ArrayList<Country>(head.countries);
		countries.add(tail);
	}
    
    public void add(Country c) {
    	countries.add(c);
    }
    
    public Country get(int i) {
    	return countries.get(i);
    }
    
    public Country getLast() {
    	return countries.get(countries.size()-1);
    }
    
    public int size() {
    	return countries.size();
    }
    
    public String toString() {
    	String ret = "<Path:";
    	for(int i = 0; i < countries.size(); i++) {
    		ret += " " + countries.get(i);
    		if(i < countries.size()-1) ret += " ->";
    	}
    	return ret;
    }
}
