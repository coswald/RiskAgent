


import java.util.*;
import com.sillysoft.vox.*;

/* 
 * A Path is just a list of Countries. Paths can be created by
 * the Pathfinder class.
 */

public class Path2 {
    private ArrayList<Country> countries;
	
	public Path2() {
		countries = new ArrayList<Country>();		
	}
	
	public Path2(Country c) {
		countries = new ArrayList<Country>();		
        countries.add(c);		
	}
	
	public Path2(Path2 head, Country tail) {
		countries = new ArrayList<Country>(head.countries);
		countries.add(tail);
	}
	
	public Path2(ArrayList<Country> countries) {
		this.countries = new ArrayList<Country>(countries);
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
    
    public Path2 exceptLast() {
        ArrayList<Country> newCountries = new ArrayList<Country>();
        for(int i = 0; i < countries.size() - 1; i++) {
        	newCountries.add(countries.get(i));
        }
        return new Path2(newCountries); 
    }
    
    public int size() {
    	return countries.size();
    }
    
    public int turnsForKnight(Troops troops) {
    	Team startTeam = countries.get(0).getTeam();
    	
    	int ret = 0;
    	boolean skipped = false;
    	for(int i = 1; i < countries.size(); i++) {
    		Country c = countries.get(i);
    		if((i == countries.size() - 1) ||   //if this is the last country, or we skipped the last country
    		   skipped ||                       //or it's occupied by hostiles, we have to spend a turn to land here
    		   (troops.originalGroup(c).getTotalUnitCount() > 0) && !c.getTeam().equals(startTeam)) {
    			ret++;
    			skipped = false;
    		} else {
    			skipped = true; //otherwise skip this country
    		}
    	}
    	
    	return ret;
    }
    
    public int turnsForInfantry() {
    	return countries.size() - 1;
    }
    
    public UnitStackGroup getUnitStackGroup(Troops troops) {
    	UnitStackGroup ret = new UnitStackGroup();
    	
        for(Country c : countries) {
        	ret.add(troops.remainingGroup(c));
        	ret.add(troops.incomingGroup(c));
        }
        
        return ret;
    }
    
    //warning: doesn't know or care about units no longer in country.getUnitStackGroup() 
    //         due to movement orders. Probably this should only be used to get the group
    //         of troops hostile to the current player.
    public UnitStackGroup getEnemyUnitStackGroup(Team t, Troops troops) {
    	return Util2.excludeTeam(getUnitStackGroup(troops), t);
    }
    
    public Path2 reverse() {
    	Path2 ret = new Path2();
    	for(int i = countries.size()-1; i >= 0; i--) {
    		ret.add(countries.get(i));
    	}
    	return ret;
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
