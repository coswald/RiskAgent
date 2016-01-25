

import com.sillysoft.vox.agent.*;
import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import java.util.*;



@SuppressWarnings("unchecked")
public class Hawk implements VoxAgent {

	VoxWorld world;
	Country[] countryArray;
	List<Country> countryList;
	Unit infantryUnitMe, knightUnitMe, castleUnitMe;
	int id;
	Player me;
	Team us;
	
	Util util;
	Pathfinder pathfinder;
	Random rand;
	
	int numCountries;
	
	UnitStackGroup[] incoming;
	UnitStackGroup[] reserved;
	
	public void declareMoves(Country[] countryArray) {
		
		initTurn(countryArray);
		
		doRecruitment();
		
		doMovement(countryArray);
		
		//if(id == 0) pause();
	}

	
	
	void initTurn(Country[] countryArray) {
		this.countryArray = countryArray;
		countryList = Arrays.asList(countryArray);
		numCountries = countryArray.length;
		
		for(int i = 0; i < numCountries; i++) {
			debug("Country " + i + ": " + countryArray[i].getName() + " - " + countryArray[i].getUnitStackGroup());
		}
		
		util.initTurn();
	}
	
	void doRecruitment() {
		Country bestCastle = safeFrontlineCastle();
	    
		if (bestCastle == null)
			buyCastle();
		else {
			int income = world.getPlayerMoney(id); 
			
			placeInRatio(bestCastle, income, 0.5);
			}
	}

	void doMovement(Country[] countryArray) {
		allocateDefensiveInfantry();
		
        List<Cluster> clusters = Cluster.teamClusters(us, countryList, util);
        for(Cluster cluster : clusters) {
        	List<Country> border = cluster.getFriendlyBorder(util);
        	
        	for(Country c : cluster) {
                if(util.remainingGroup(c).getTotalUnitCount() == 0) continue;
        		
        		hitUndefendedNeighbors(c);
        		
                if(!border.contains(c)) {
                	knightAttack(c);
                	fortifyTowardBorder(c, border, true);
                } else {
                	attackConsolidate(c);
                	attackFanOut(c);
                	knightAttack(c);
                }
        	}
        }
	}

	
	
	void allocateDefensiveInfantry() {
		int[] desiredDefenders = new int[numCountries];
		for(Country c : Util.teamCountries(us, countryList)) {
			int cid = c.getID();
			UnitStackGroup potentialAttackers = util.potentialAttackers(c);
			if(c.hasCastle(me) || 
			      (world.getContinent(c.getContinentID()).getBonus() > 0 &&
			       util.isContinentBorder(c)) && 
			       us.equals(util.continentOwner(c.getContinentID()))) {
				desiredDefenders[cid] = Math.max(2, 1+requiredInfantryDefenders(potentialAttackers, 1.5));
			} else {
				desiredDefenders[cid] = (potentialAttackers.getTotalUnitCount() > 0 ? 1 : 0);
			}
			
			
			desiredDefenders[cid] -= util.incomingInfantry(c).getTotalUnitCount();
		}
		
		boolean[] unhelpable = new boolean[numCountries];
		while(true) {
			int mostDesired = 0;
		    Country neediestCountry = null;
		    for(int i = 0; i < numCountries; i++) {
		    	if(!unhelpable[i] && desiredDefenders[i] > mostDesired) {
			    	mostDesired = desiredDefenders[i];
				    neediestCountry = countryArray[i];
			
			    }
		    }
		    
		    if(neediestCountry == null) break; //nothing to do!
		    
		    List<Country> donors = new ArrayList<Country>();
		    donors.add(neediestCountry); 
		    donors.addAll(util.realAdjoiningList(neediestCountry));
		    
		    
		    
		    int biggestSurplus = Integer.MIN_VALUE;
		    Country bestDonor = null;
		    for(Country d : donors) {
		    	int remainingInfantry = util.remainingInfantry(d).getTotalUnitCount();
		    	if(remainingInfantry > 0) {
		    		int surplus = remainingInfantry - desiredDefenders[d.getID()];
		    		if(surplus > biggestSurplus) {
		    			biggestSurplus = surplus;
		    			bestDonor = d;
		    		}
		    	}
		    }
		    
		    if(bestDonor == null) {
		    	debug(neediestCountry.getName() + " is now unhelpable");
		    	unhelpable[neediestCountry.getID()] = true;
		    } else {
		    	debug("moving a defensive infantry from " + bestDonor.getName() + " to " + neediestCountry.getName());
		    	util.moveInfantry(bestDonor, neediestCountry, 1);
		    	desiredDefenders[neediestCountry.getID()] -= 1;
		    }
		}
	}
	
	int requiredInfantryDefenders(UnitStackGroup attackers, double margin) {
		int attackerInfantry = Util.numInfantry(attackers);
		int attackerKnights  = Util.numKnights (attackers);
		
		int defenderInfantry = 1;
		while(Simulation.winnable(attackerInfantry, attackerKnights, defenderInfantry, 0, Simulation.TYPE_FAVOR_ATTACKER)) {
			defenderInfantry *= 2;
		}
		
		int step = defenderInfantry/2;
		while(step > 0) {
			if(Simulation.winnable(attackerInfantry, attackerKnights, defenderInfantry, 0, Simulation.TYPE_FAVOR_ATTACKER)) {
				defenderInfantry += step;
			} else {
				defenderInfantry -= step;
			}
			step /= 2;
		}
		
		return (int)(defenderInfantry*margin);
	}
	
	
	
	void fortifyTowardBorder(Country c, List<Country> border, boolean fortifyKnights) {
		Path toBorder = pathfinder.pathToCollection(c, border, true);
		debug("fortifying toward border along path " + toBorder);
		
        util.moveRemainingInfantry(c, toBorder.get(1));
		
        if(fortifyKnights) {
		   	if(toBorder.size() > 2) {
		   	    util.moveRemainingKnights(c, toBorder.get(2));
		    } else {
		  	    util.moveRemainingKnights(c, toBorder.get(1));
		    }
		}
	}
	
	
	
	void hitUndefendedNeighbors(Country c) {
		
		int localInfantry = util.remainingInfantry(c).getTotalUnitCount();
		int localKnights  = util.remainingKnights (c).getTotalUnitCount();
		
		List<Country> reachableHostiles = Util.hostileCountries(us, 
				                            util.reachableByKnight(c));
		reachableHostiles = sortCountryListByValue(reachableHostiles);
		
		List<Country> adjacentHostiles = util.hostileNeighbors(c);
		
		for(Country h : reachableHostiles) {
			if(h.getUnitStackGroup().getTotalUnitCount() == 0 &&
			   util.incomingGroup(h).getTotalUnitCount() == 0) {
				if(adjacentHostiles.contains(h) && localInfantry > 0) {
					debug("single infantry from " + c.getName() + " to undefended " + h.getName());
					util.moveInfantry(c, h, 1);
					localInfantry--;
				} else if(localKnights > 0) {
					debug("single knight from " + c.getName() + " to undefended " + h.getName());
					util.moveKnights(c, h, 1);
					localKnights--;
				}
			}
		}
		
	}

	boolean knightAttack(Country c) {
		List<Country> targets = Util.hostileCountries(us, util.reachableByKnight(c));
	    
		float bestScore = Integer.MIN_VALUE;
	    Country bestTarget = null;
	    for(Country target : targets) {
	    	if(!Simulation.winnable(util.remainingKnights(c), target.getUnitStackGroup(), 
	    			                Simulation.TYPE_FAVOR_DEFENDER)) {
                continue;
	    	}
	    	
	    	float score = (target.hasCastle() ? 1000 : 0) +
	    	              target.getBonus() + target.getContinentBonusPartial(world);
	    	
	    	if(score > bestScore) {
	    		bestScore = score;
	    		bestTarget = target;
	    	}
	    }
	    
	    if(bestTarget == null) {
	    	return false;
	    } else {
	    	debug("knight attack from " + c.getName() + " to " + bestTarget.getName() + " with score " + bestScore);
	    	util.moveRemainingKnights(c, bestTarget);
	    	return true;
	    }
	}

	boolean attackFanOut(Country c) {
		List<Country> hostileNeighbors = util.hostileNeighbors(c);
		int numEnemies = hostileNeighbors.size();
		
        UnitStackGroup localTroops = util.remainingGroup(c);

		for(Country n : hostileNeighbors) {
			UnitStackGroup hostileTroops = n.getUnitStackGroup();
			if(!Simulation.winnable(localTroops, hostileTroops, 1.0/numEnemies, Simulation.TYPE_FAVOR_DEFENDER))
				return false;
		}
		
		debug("attack fan out from " + c.getName());
		evenFanOut(c, hostileNeighbors, true);
		return true;
	}
	
	void evenFanOut(Country c, List<Country> neighbors, boolean includeKnights) {
		debug("splitting units from " + c.getName() + " to neighboring countries: " + Util.listToString(neighbors));
		int infantry = util.remainingInfantry(c).getTotalUnitCount();
		int knights  = util.remainingKnights (c).getTotalUnitCount();
		debug("...splitting " + infantry + " infantry and " + knights + " knights");
		int neighborsLeft = neighbors.size();
		for(Country n : neighbors) {
			debug("...sending " + (infantry/neighborsLeft) + " infantry and " + (knights /neighborsLeft) + " to " + n.getName());
			util.moveInfantry(c, n, infantry/neighborsLeft);
			if(includeKnights) util.moveKnights( c, n, knights /neighborsLeft);
			infantry -= infantry/neighborsLeft;
			knights  -= knights /neighborsLeft;
			neighborsLeft -= 1;
		}
	}
	
	boolean attackConsolidate(Country c) {
		List<Country> hostileNeighbors = util.hostileNeighbors(c);
		int numEnemies = hostileNeighbors.size();
		if(numEnemies != 1) return false;
		Country target = hostileNeighbors.get(0);
		
		
        UnitStackGroup attackers = new UnitStackGroup();
        attackers.add(util.remainingGroup(c));

		List<Country> targetNeighbors = util.realAdjoiningList(target);
		List<Country> participants = new ArrayList<Country>();
		for(Country tn : targetNeighbors) {
			if(tn.getTeam().equals(us) && util.hostileNeighbors(tn).size() == 1) {
                attackers.add(util.remainingGroup(tn));
                participants.add(tn);
			}
		}
		
		if(Simulation.winnable(attackers, target.getUnitStackGroup(), Simulation.TYPE_FAVOR_DEFENDER)) {
			debug("consolidation attack " + c.getName() + " -> " + target.getName());
			for(Country participant : participants) {
				if(participant.getOwner().equals(me)) {
					util.moveRemainingInfantry(participant, target);
			        util.moveRemainingKnights (participant, target);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	
	
	Country safeFrontlineCastle() {
		List<Country> castles = world.getCastleCountriesOwnedBy(id);
		if(castles.size() == 0) return null;
		
		Country bestCastle = null;
	    int bestDistToEnemy = 10000;
	    for(Country castle : castles) {
	    	if(!countryInDanger(castle)) {
	    		int distToEnemy = pathfinder.pathToEnemy(castle, countryList).size();
	    		if(distToEnemy < bestDistToEnemy) {
	    			bestDistToEnemy = distToEnemy;
	    			bestCastle = castle;
	    		}
	    	}
	    }
	    
	    if(bestCastle == null) bestCastle = castles.get(0);
	    
	    return bestCastle;
	}
	
	void placeInRatio(Country where, int income, double knightRatio) {
		int infantryCost = infantryUnitMe.getCost();
		int knightCost   =   knightUnitMe.getCost();
	    
	    if(where == null) {
	    	return;
	    } else {
	    	int numUnits = (int)(income / (infantryCost*(1-knightRatio) + 
	    			                         knightCost*(  knightRatio)));
	    	int numKnights = (int)(knightRatio*numUnits);
	    	int numInfantry = (income - numKnights*knightCost)/infantryCost;
	    	util.placeInfantry(where, numInfantry);
	    	util.placeKnights( where, numKnights );
	    }
	}
	
	boolean countryInDanger(Country c) {
        return Simulation.winnable(util.potentialAttackers(c), util.originalGroup(c), 
        		                   Simulation.TYPE_FAVOR_ATTACKER);
	}
	
	List<Country> sortCountryListByValue(List<Country> countries) {
		List<Country> ret = new ArrayList<Country>();
		
		for(int i = 0; i < countries.size(); i++) {
			float bestScore = Integer.MIN_VALUE;
			Country bestCountry = null;
			for(Country c : countries) {
				if(ret.contains(c)) continue;
				
				float score = (c.hasCastle() ? 100000 : 0) +
				            c.getBonus() + 
				            c.getContinentBonusPartial(world);
			
			    if(score > bestScore) {
			    	bestScore = score;
			    	bestCountry = c;
			    }
			}
			
			if(bestCountry != null) {
				ret.add(bestCountry);
			}
		}
		
		return ret;
	}

	
	
	public void setPrefs( int newID, VoxWorld theworld ){
		id = newID;
		world = theworld;
		
		me = world.getPlayer(id);
		us = me.getTeam();
		infantryUnitMe = new UnitPawn(me);
		knightUnitMe = new UnitKnight(me);
		castleUnitMe = new UnitCastle(me);
		
		util = new Util(this);
		pathfinder = new Pathfinder(util);
		rand = new Random(new Date().getTime());
	}

	public Hawk() {
	}
	
	public String name() {
		return "Hawk";
	}

	public float version() {
		return 1.0f;
	}

	public String description() {
		return "A Vox AI by Greg McGlynn.";
	}

	public String youWon() { 
		String[] messages = {
			 "Ah, distinctly I remember\n" + 
			 "It was in the bleak December\n" + 
			 "And each separate dying ember\n" + 
			 "Wrought its ghost upon the floor",
			 
			 "Tell all the Truth but tell it slant--\n" +
			 "Success in Circuit lies\n" + 
			 "Too bright for our infirm Delight\n" +
			 "The Truth's superb surprise",
			 
			 "As Lightning to the Children eased\n" +
			 "With explanation kind\n" + 
			 "The Truth must dazzle gradually\n" + 
			 "Or every man be blind--",
			 
			 "In Xanadu did Kubla Khan\n" +
			 "A stately pleasure dome decree:\n" +
			 "Where Alph, the sacred river, ran\n" +
			 "Through caverns measureless to man\n" +
			 "Down to a sunless sea",
			 
			 "Lay on Macduff\n" + 
			 "And damn'd be him that first cries,\n" +
			 "\"Hold! Enough!\"",
			 
			 "The Sea of Faith,\n" +
			 "Was once, too, at the full, ...\n" +
			 "But now I only hear\n " + 
			 "Its melancholy, long, withdrawing roar,",
			 
			 "But at my back I always hear\n" +
			 "Time's winged chariot hurrying near"
		    };
		
		return messages[rand.nextInt(messages.length)];
	}

	public String message( String message, Object data ) {
		return null;
	}

	void debug(String x) {
		//System.out.println(world.getPlayer(id).getName() + ": " + x);
	}
	
	void pause() {
		//try {
		//	System.in.read();
		//	System.in.read();
		//} catch(Exception e) {}
	}
	

/** Try to build a castle. */	
public boolean buyCastle()
	{
	Unit castle = new UnitCastle(world.getPlayer(id));
	if (world.getPlayerMoney(id) >= castle.getCost())
		{
		Country country = getBestCastleBuildCountry();
		if (country != null)
			{
			world.placeUnits(new UnitStack(castle, 1), country);
			return true;
			}
		}
	return false;
	}

/** Return a country that we own that it would make sense to build a castle on. */
public Country getBestCastleBuildCountry()
	{
	int bestValue = 0;
	Country bestCountry = null;
	for (int i = 0; i < countryArray.length; i++)
		{
		Country c = countryArray[i];
		if (c.getOwner().getID() == id && c.getUnitStackGroup().getTotalUnitCount() > 4)
			{
			int value = c.getUnitStackGroup().getCost();
			value += c.getBonus();
			
			if (value > bestValue)
				{
				bestValue = value;
				bestCountry = c;
				}
			}
		}

	return bestCountry;
	}
	
	
}
