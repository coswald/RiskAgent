


import com.sillysoft.vox.agent.*;
import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import java.util.*;



@SuppressWarnings("unchecked")
public class Polymath implements VoxAgent {

	VoxWorld world;
	
	int id;
	Player me;
	Unit infantryUnitMe, knightUnitMe, castleUnitMe;
	Team us;
	List<Team> teams, enemyTeams;
	
	Util2 util;
	Troops troops;
	Pathfinder2 pathfinder;
	Random rand;
	
	Country[] countryArray;
	List<Country> countryList, ourCountries;
	int numCountries;
	
	boolean finishedGeneralDefense; //a flag that lets functions know whether remaining infantry can be 
	                                //safely used for whatever purpose or whether we need to save them
	                                //for defense
	
	public void declareMoves(Country[] countryArray) {
		initTurn(countryArray);
		
		int[] desiredDefenders = doSpecialDefense();
		
		doSpecialAttacks();
		
		doGeneralAttacks();
		
		desiredDefenders = doGeneralDefense(desiredDefenders);
		
		doGeneralAttacks(); //a second round of general attacks now that we know what infantry are free
		
		doRecruitment(desiredDefenders);
		
		finishMovement(countryArray, desiredDefenders);
	}

	
	
	void initTurn(Country[] countryArray) {
		this.countryArray = countryArray;
		countryList = Arrays.asList(countryArray);
		numCountries = countryArray.length;
		ourCountries = Util2.teamCountries(us, countryList);
		
		teams = new ArrayList<Team>();
		for(int i = 0; i < numCountries; i++) {
		    if(!teams.contains(countryArray[i].getTeam())) teams.add(countryArray[i].getTeam());
		}
		enemyTeams = new ArrayList<Team>(teams);
		enemyTeams.remove(us);

		troops.initTurn();
		util.initMap();
		
		finishedGeneralDefense = false;
	}
	
	int[] doSpecialDefense() {
		int[] desiredDefenders = calculateDesiredDefenders();
		
		desiredDefenders = allocateDefensiveInfantry(desiredDefenders, Util2.teamCastles(us, countryList));

		//if infantry weren't enough, try using knights to defend key points (castles)
		allocateDefensiveKnights(desiredDefenders);
		
		//castles we would like to recruit from have higher priority:
		List<Country> ourCastles = Ranker.sortByRanker(Util2.teamCastles(us, countryList), recruitCastleRanker);
		
		for(Country c : ourCastles) {
			desiredDefenders = defendTwoTurnCastleThreats(c, desiredDefenders);
		}
		
		return desiredDefenders;
	}

	void doSpecialAttacks() {
		debug("DOING SPECIAL ATTACKS");
		
		//send knight armies to raid enemy castles, if they're strong enough
    	for(Country c : ourCountries) {
    		knightCastleRaid(c);    		
    	}
	}
	
	void doGeneralAttacks() {
		debug("DOING GENERAL ATTACKS");
		
		ArrayList<Country> targets = new ArrayList<Country>();
		for(Country c : Util2.hostileCountries(us, countryList)) {
			if(safeAttackers(c).getTotalUnitCount() > 0 && 
			   Util2.teamSubgroup(troops.incomingGroup(c), us).size() == 0) {
				targets.add(c);
			}
		}

		debug("" + targets.size() + " potential targets; beginning attacks");
		while(true) {
			Country target = Ranker.bestByRanker(targets, targetRanker);
			debug("popped target " + target);
		    if(target == null) break;
			if(targetRanker.rank(target) <= 0) {
				debug("finishing after popping target " + target + " with rank " + targetRanker.rank(target));
				break;
			}
		    
		    attackTarget(target);
		    targets.remove(target);
		}
	}
	
    int[] doGeneralDefense(int[] desiredDefenders) {
		debug("DOING GENERAL DEFENSE");
		
		//infantry are the primary defenders:
		desiredDefenders = allocateDefensiveInfantry(desiredDefenders, ourCountries);

		finishedGeneralDefense = true;
		
	    return desiredDefenders;
	}
	
	void doRecruitment(int[] desiredDefenders) {
		debug("DOING RECRUITMENT");
		
		Country bestCastle = safeFrontlineCastle();
		if (bestCastle == null)
			{
			buyCastle();
			}
		else {
			int income = world.getPlayerMoney(id); 
			debug("income before spending: " + world.getPlayerMoney(id));

			//int totalInfantry = Util.groupInfantry(troops.combinedOriginalGroup(ourCountries)).getTotalUnitCount();
			//int totalKnights  = Util.groupKnights (troops.combinedOriginalGroup(ourCountries)).getTotalUnitCount();
			
			int totalDesiredDefenders = 0;
			for(int i = 0; i < numCountries; i++) totalDesiredDefenders += desiredDefenders[i];
			totalDesiredDefenders -= Util2.groupInfantry(troops.combinedRemainingGroup(ourCountries)).getTotalUnitCount();
			
			debug("totalDesiredDefeners = " + totalDesiredDefenders);
			
			if(totalDesiredDefenders <= 0) {
				placeInRatio(bestCastle, income, 1);
			} else if(3*totalDesiredDefenders > income) {
				placeInRatio(bestCastle, income, 0);
			} else {
				placeInRatio(bestCastle, income, 1.0 - (3.0*totalDesiredDefenders)/income);
			}
			debug("income after spending: " + world.getPlayerMoney(id));
		}
	}

	
	void finishMovement(Country[] countryArray, int[] desiredDefenders) {
		debug("FINISHING MOVEMENT");
		
		//send unused troops to help attacks
		for(Country c : ourCountries) {
			reinforceAttacks(c);
		}
        
		int[] extraKnightsNeeded = getTargetExtraKnightsNeeded();
		
		//now fortify remaining units toward borders
		for(Country c : ourCountries) {
			fortifyTowardBorder(c, false, desiredDefenders);
			repositionKnights(c, extraKnightsNeeded);
        }       
	}

	
	
	boolean needsDefenders(Country c) {
		//defend castles
		if(c.hasCastle()) {
			debug("" + c + " is castle, so needs defenders");
			return true;
		}
		
		//defend the borders of positive continents that aren't already defended
		//by outward continents
		if(world.getContinent(c.getContinentID()).getBonus() > 0 &&
		   us.equals(util.continentOwner(c.getContinentID()))) {
			debug("" + c + " is in an owned continent, so needs defenders");
			return true;
		}
	        
		//if a country is an important continent border as above but has no hostile neighbors, 
		//defend the neighboring countries outside the continent
		for(Country n : util.realOutgoingList(c)) {
			if(!util.isBorder(n) &&
			   world.getContinent(n.getContinentID()).getBonus() > 0 &&
			   us.equals(util.continentOwner(n.getContinentID()))) {
			    debug("" + c + " defends " + n + ", so needs defenders");
				return true;				
			} else {
				debug("" + c + " doesn't defend " + n + ": " +  !util.isBorder(n) + " " +
			                 (world.getContinent(n.getContinentID()).getBonus() > 0) + " " +
			                 us.equals(util.continentOwner(n.getContinentID())));
			}
		}
		
		debug("" + c + " doesn't need defenders");
		return false;
	}
	
	int[] calculateDesiredDefenders() {
		int[] desiredDefenders = new int[numCountries];
		for(Country c : ourCountries) {
			int cid = c.getID();
			UnitStackGroup potentialAttackers = util.potentialAttackers(c);
			if(needsDefenders(c)) {
			    List<UnitStackGroup> attackGroups = Util2.splitByTeam(potentialAttackers, enemyTeams);
                UnitStackGroup biggestThreat = Ranker.bestByRanker(attackGroups, threatRanker);
                
                //in general, don't worry as nearly much about infantry as about knights
                if(!c.hasCastle()) {
                	biggestThreat = Util2.combine(Simulation2.clone(Util2.groupInfantry(biggestThreat), 0.5),
                			                       Util2.groupKnights(biggestThreat));
                }
                			    
                int simType = (c.hasCastle() ? Simulation2.TYPE_FAVOR_ATTACKER : Simulation2.TYPE_MOST_LIKELY);
			    desiredDefenders[cid] = requiredDefenders(biggestThreat, troops.incomingGroup(c), true, simType);
            } else {
				desiredDefenders[cid] = (potentialAttackers.getTotalUnitCount() > 0 ? 1 : 0);
			}
			
			if(desiredDefenders[cid] > 0) debug(c.getName() + " desires " + desiredDefenders[cid] + " defenders");
		}
		
		return desiredDefenders;
	}
	
	
	int[] allocateDefensiveInfantry(int[] desiredDefenders, List<Country> toDefend) {
		//first allocate one infantry to every country that desires defense:
		for(Country c : toDefend) {
			if(desiredDefenders[c.getID()] > 0) {
				if(allocateOneDefender(c, desiredDefenders)) {
					desiredDefenders[c.getID()] -= 1;			
				}
			}
		}
		
		boolean[] unhelpable = new boolean[numCountries];
		while(true) {
			int mostDesired = 0;
		    Country neediestCountry = null;
		    for(int i = 0; i < numCountries; i++) {
		    	if(toDefend.contains(countryArray[i]) && !unhelpable[i] && desiredDefenders[i] > mostDesired) {
			    	mostDesired = desiredDefenders[i];
				    neediestCountry = countryArray[i];			
			    }
		    }
		    
		    if(neediestCountry == null) break; //nothing to do!
		    
		    if(allocateOneDefender(neediestCountry, desiredDefenders)) {
		    	desiredDefenders[neediestCountry.getID()] -= 1;
		    } else {
		    	unhelpable[neediestCountry.getID()] = true;
		    }
		}
		
		return desiredDefenders;
	}
	
	boolean allocateOneDefender(Country c, int[] desiredDefenders) {
		List<Country> donors = new ArrayList<Country>();
	    donors.add(c); 
	    donors.addAll(util.friendlyIncomingNeighbors(c));
	    
	    int biggestSurplus = Integer.MIN_VALUE;
	    Country bestDonor = null;
	    for(Country d : donors) {
	    	int remainingInfantry = troops.remainingInfantry(d).getTotalUnitCount();
	    		    	
	    	if(remainingInfantry > 0) {
	    		int surplus = remainingInfantry - desiredDefenders[d.getID()];
	    		if(surplus > biggestSurplus) {
	    			biggestSurplus = surplus;
	    			bestDonor = d;
	    		}
	    	}
	    }
	    
	    if(bestDonor == null) {
	    	debug(c.getName() + " is now unhelpable");
	    	return false;
	    } else {
	    	debug("moving a defensive infantry from " + bestDonor.getName() + " to " + c.getName());
	    	troops.moveInfantry(bestDonor, c, 1);
	    	return true;
	    }
	}
	
	
	
	int requiredDefenders(UnitStackGroup attackers, UnitStackGroup existingDefenders, 
			              boolean infantry, int simType) {
		if(!Simulation2.winnable(attackers, existingDefenders, simType)) return 0;
		
		UnitStack extraDefenders;		
		if(infantry) {
			extraDefenders = new UnitStack(infantryUnitMe, 1);
		} else {
			extraDefenders = new UnitStack(knightUnitMe, 1);
		}

		int moreThan = 0;
		int atMost = Integer.MAX_VALUE;
		
		//establish an upper bound on how many defenders we'll need:
		while(Simulation2.winnable(attackers, Util2.combine(existingDefenders, Util2.groupFromStack(extraDefenders)),
				                  simType)) {
			extraDefenders.setCount(2*extraDefenders.getCount());
		}
		atMost = extraDefenders.getCount();
		
		//then do a binary search within this range:
		while(moreThan + 1 < atMost) {
			int testCount = (moreThan + atMost + 1)/2;
			extraDefenders.setCount(testCount);
			if(Simulation2.winnable(attackers, Util2.combine(existingDefenders, Util2.groupFromStack(extraDefenders)),
					               simType)) {
				moreThan = testCount;
			} else {
				atMost = testCount;
			}
		}
		
		return atMost;
	}
	

	
	
	void allocateDefensiveKnights(int[] desiredDefenders) {
		//find allied castles that don't have enough infantry defenders
		//but have enough knights nearby that those could be used instead, and
		//tell those knights to defend their castle
		for(Country c : ourCountries) {
			if(c.hasCastle() && desiredDefenders[c.getID()] > 0) {
				debug("allocateDefensiveKnights: distressed castle at " + c);
				
				UnitStackGroup potentialAttackers = util.potentialAttackers(c);
				List<UnitStackGroup> attackGroups = Util2.splitByTeam(potentialAttackers, enemyTeams);
                UnitStackGroup biggestThreat = Ranker.bestByRanker(attackGroups, threatRanker);
                int requiredKnights = requiredDefenders(biggestThreat, troops.incomingGroup(c), 
                		                                false, Simulation2.TYPE_FAVOR_ATTACKER);
				
                int localKnights = Util2.groupKnights(util.potentialDefenders(c)).getTotalUnitCount();
				                
                debug("localKnights: " + localKnights + "; requiredKnights: " + requiredKnights);
				if(localKnights >= requiredKnights) {
					debug("reserving knights to defend " + c);
					gatherKnightsEvenly(c, requiredKnights, Util2.teamCountries(us, util.knightSources(c)));
				}
			}
		}
	}
	

	void knightCastleRaid(Country c) {
		int knights = troops.remainingKnights(c).getTotalUnitCount();
		if(knights <= 1) return;
		
		int shortestDistance = Integer.MAX_VALUE;
		Country bestRaidMove = null;

		for(Country castle : countryList) {
			if(castle.hasCastle() && !castle.getTeam().equals(us) &&
			   Simulation2.winnable(troops.remainingKnights(c), util.potentialDefenders(castle), 
					                                       Simulation2.TYPE_MOST_LIKELY)) {
				Path2 attackPath = pathfinder.bestPathBetween(c, castle, false,
						                                     getKnightCastleRaidPathRanker(knights/10.0, knights));
				if(attackPath == null) continue;
				int defenders = attackPath.getEnemyUnitStackGroup(us, troops).getTotalUnitCount();

				if(knights > defenders && attackPath.size() < shortestDistance) {
					shortestDistance = attackPath.size();
					debug("possible knight raid with " + knights + " in " + c + " to " + castle + "!");
					if(attackPath.size() >= 3 &&
							world.unitCanReach(new UnitStack(knightUnitMe), c, attackPath.get(2))) {
						bestRaidMove = attackPath.get(2);
					} else {
						bestRaidMove = attackPath.get(1);
					}
				}
			}
		}

		if(bestRaidMove != null) {
			debug("moving all knights in " + c + " to " + bestRaidMove + " for castle raid");
			if(bestRaidMove.hasCastle()) {
				attackTarget(bestRaidMove); //get some help if this the final fight
			} else {
				troops.moveRemainingKnights(c, bestRaidMove);
			}
		}

	}

	
	void fortifyTowardBorder(Country c, boolean fortifyKnights, int[] desiredDefenders) {
		Cluster2 cluster = Cluster2.clusterFromSeed(c, ourCountries, util);
		List<Country> border = cluster.getBorder(util);
		
		Ranker<Country> borderFortifyRanker = getBorderFortifyRanker(c, desiredDefenders);
		Country borderFortifyDest = Ranker.bestByRanker(border, borderFortifyRanker);
		
		Path2 toBorder = pathfinder.shortestPathBetween(c, borderFortifyDest, true);
		debug("fortifying toward border along path " + toBorder);
		
		//if we can't find a path to the border because of one-ways, try fortifying toward the edge
		//if we can't do that, give up
        if(toBorder == null) {
        	toBorder = pathfinder.shortestPathToEnemy(c, countryList).exceptLast();
        	if(toBorder == null || toBorder.size() <= 1) return;
        }
        
        //if we're looking at a border country and decide for fortify toward the same country,
        //don't do anything
        if(toBorder.size() == 1) return; 

        troops.moveRemainingInfantry(c, toBorder.get(1));
		
        if(fortifyKnights) {
		   	if(toBorder.size() > 2) {
		   		troops.moveRemainingKnights(c, toBorder.get(2));
		    } else {
		    	troops.moveRemainingKnights(c, toBorder.get(1));
		    }
		}
	}
	
	

	void repositionKnights(Country c, int[] extraKnightsNeeded) {
		if(troops.remainingKnights(c).getTotalUnitCount() == 0) return;
		
		debug("reposition knights in " + c);
		
		Country bestDest = Ranker.bestByRanker(ourCountries, getKnightRepositionDestinationRanker(c, 
				                                                                            extraKnightsNeeded));
		if(bestDest != null) {
			Path2 fortifyPath = pathfinder.shortestPathBetween(c, bestDest, true);
			if(fortifyPath != null) {
				if(fortifyPath.size() >= 3) {
					troops.moveRemainingKnights(c, fortifyPath.get(2));
					debug("repositioning knights from " + c + " to " + bestDest + " through " + fortifyPath.get(2));
				} else if(fortifyPath.size() == 2) {
					troops.moveRemainingKnights(c, fortifyPath.get(1));
					debug("repositioning knights from " + c + " to " + bestDest + " through " + fortifyPath.get(1));
				} else {
					debug("decided it's best to leave knights in " + c + " where they are");
				}
			} else {
				debug("NULL FORTIFY PATH FOR REPOSITION " + c + " -> " + bestDest);
			}
		} else {
			debug("NULL REPOSITION DEST FOR " + c);
		}
	}
	
	
	
	void reinforceAttacks(Country c) {
		if(troops.remainingInfantry(c).getTotalUnitCount() > 0) {
			int mostDefenders = -1;
			Country bestTarget = null;
			
			for(Country target : util.hostileOutgoingNeighbors(c)) {
				if(Util2.teamSubgroup(troops.incomingGroup(target), us).getTotalUnitCount() > 0) {
					int numDefenders = troops.originalGroup(target).getTotalUnitCount();
					if(numDefenders > mostDefenders) {
						mostDefenders = numDefenders;
						bestTarget = target;
					}
				}
			}
			
			if(bestTarget != null) {
				debug("reinforcing attack on " + bestTarget.getName() + " with infantry from " + c.getName());
				troops.moveRemainingInfantry(c, bestTarget);
			}
		}
		
		if(troops.remainingKnights(c).getTotalUnitCount() > 0) {
			int mostDefenders = -1;
			Country bestTarget = null;
			
			for(Country target : Util2.hostileCountries(us, util.knightDestinations(c))) {
				if(Util2.teamSubgroup(troops.incomingGroup(target), us).getTotalUnitCount() > 0) {
					int numDefenders = troops.originalGroup(target).getTotalUnitCount();
					if(numDefenders > mostDefenders) {
						mostDefenders = numDefenders;
						bestTarget = target;
					}
				}
			}
			
			if(bestTarget != null) {
				debug("reinforcing attack on " + bestTarget.getName() + " with knights from " + c.getName());
				troops.moveRemainingKnights(c, bestTarget);
			}
		}		
	}
	
	
	
	Country safeFrontlineCastle() {
		List<Country> castles = world.getCastleCountriesOwnedBy(id);

        Country ret = Ranker.bestByRanker(castles, recruitCastleRanker);
	
	    debug("safeFrontlineCastle: " + ret);
	    return ret;
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
	    	troops.placeInfantry(where, numInfantry);
	    	troops.placeKnights( where, numKnights );
	    }
	}


	boolean countryInDanger(Country c) {
		return Simulation2.winnable(util.potentialAttackers(c), util.potentialDefenders(c),
				                   Simulation2.TYPE_FAVOR_ATTACKER);
	}
	
	
	

	void attackTarget(Country target) {
		debug("attacking target: " + target);
		
		UnitStackGroup maxDefenders = util.potentialDefenders(target);
		int desiredKnights = (int)(Math.round(Math.ceil(0.1 + 1.2*maxDefenders.getTotalUnitCount())));
		int desiredInfantry = (int)(Math.round(Math.ceil(0.1 + 1.8*maxDefenders.getTotalUnitCount())));
		debug("maxDefenders " + maxDefenders.getTotalUnitCount() + ": " + maxDefenders);
		
		UnitStackGroup maxAttackers = safeAttackers(target);
		int maxKnights = Util2.numKnights(maxAttackers);
		int maxInfantry = Util2.numInfantry(maxAttackers);
		debug("maxAttackers " + maxKnights + "K+" + maxInfantry + "I: " + maxAttackers);
		
		int realKnights = Math.min(desiredKnights, maxKnights);
		int realInfantry = Math.min(desiredInfantry, maxInfantry);
		
		List<Country> knightSources = Util2.teamCountries(us, util.knightSources(target));
		List<Country> infantrySources = infantryAttackSources(target);
		
		int numKnights = gatherKnightsEvenly(target, realKnights, knightSources);
		int numInfantry = gatherInfantryEvenly(target, realInfantry, infantrySources);
		
		debug("sent " + numKnights + "K+" + numInfantry + "I");
	}
	
	


	int gatherKnightsEvenly(Country target, int maxKnights, List<Country> sources) {
		int numKnights = 0;
		boolean depleted = false;
		while(!depleted) {
			depleted = true;
			for(Country c : sources) {
				if(troops.remainingKnights(c).getTotalUnitCount() > 0) {
					troops.moveKnights(c, target, 1);
					numKnights += 1;
					depleted = false;
					if(numKnights == maxKnights) return maxKnights;
				}
			}
		}
		return numKnights;
	}

	int gatherInfantryEvenly(Country target, int maxInfantry, List<Country> sources) {
		int numInfantry = 0;
		boolean depleted = false;
		while(!depleted) {
			depleted = true;
			for(Country c : sources) {
				if(troops.remainingInfantry(c).getTotalUnitCount() > 0) {
					troops.moveInfantry(c, target, 1);
					numInfantry += 1;
					depleted = false;
					if(numInfantry == maxInfantry) return maxInfantry;
				}
			}
		}
		return maxInfantry;
	}
	
	
	UnitStackGroup safeAttackers(Country target) {
		UnitStackGroup knights = Util2.groupKnights(Util2.teamSubgroup(util.potentialAttackers(target), us));
		UnitStackGroup infantry = Util2.groupInfantry(troops.combinedRemainingGroup(infantryAttackSources(target)));
		return Util2.combine(infantry, knights);
	}
	
	UnitStackGroup safeAttackersInContinent(Country target) {
		UnitStackGroup knights = Util2.groupKnights(Util2.teamSubgroup(
				util.potentialAttackersFromContinent(target, target.getContinentID()), us));
		UnitStackGroup infantry = Util2.groupInfantry(troops.combinedRemainingGroup(
					Util2.continentCountries(target.getContinentID(), infantryAttackSources(target))));
		return Util2.combine(infantry, knights);
	}

	ArrayList<Country> getUndistractedAllies(Country target) {
		ArrayList<Country> ret = new ArrayList<Country>();
		for(Country n : Util2.teamCountries(us, util.realIncomingList(target))) {
            //n's infantry can attack target without deserting their posts if target has a castle
			//or if target is n's only threat or if target is in the same continent and we control that continent			
			List<Country> distractors = util.hostileIncomingNeighbors(n);
			if(target.hasCastle() ||
			   distractors.size() == 0 ||
			   (distractors.size() == 1 && distractors.get(0).equals(target)) /*||
			   (n.getContinentID() == target.getContinentID() && weControlContinent(n.getContinentID(), 0))*/) {
			    ret.add(n);
			}
		}
		return ret;
	}
	
	ArrayList<Country> infantryAttackSources(Country target) {
		if(!finishedGeneralDefense) {
			return getUndistractedAllies(target);
		} else {
			return Util2.teamCountries(us, util.realIncomingList(target));
		}
	}
	
	
	
	int[] defendTwoTurnCastleThreats(Country castle, int[] desiredDefenders) {
		//find the biggest threat
		Country threat = Ranker.bestByRanker(countryList, getTwoTurnKnightThreatRanker(castle));
		debug("defendTurnTurnCastleThreats: biggest threat to " + castle + " is " + threat);
		if(threat == null) return desiredDefenders;		
		
		//allocate defensive infantry to the "core" countries surrounding the castle
		List<Country> core = util.countriesInIncomingRange(castle, 1, true);
		allocateDefensiveInfantry(desiredDefenders, core);
		
		int requiredInfantry = requiredDefenders(troops.originalKnights(threat), troops.originalCastles(castle),
 				                                 true, Simulation2.TYPE_FAVOR_ATTACKER);
		
		int reservedInfantry = Util2.groupInfantry(troops.combinedIncomingGroup(core)).getTotalUnitCount();

		debug("requiredInfantry = " + requiredInfantry + "; reservedInfantry = " + reservedInfantry);
		
		if(reservedInfantry >= requiredInfantry) return desiredDefenders;

		debug("reserving core infantry; core = " + Util2.listToString(core));
		boolean depleted;
		do {
			depleted = true;
			for(Country c : core) {
				if(troops.remainingInfantry(c).getTotalUnitCount() > 0) {
					debug("reserving infantry in " + c);
					troops.reserveInfantry(c, 1);
					reservedInfantry++;
					if(desiredDefenders[c.getID()] > 0) desiredDefenders[c.getID()]--;
					if(reservedInfantry == requiredInfantry) return desiredDefenders;
					depleted = false;
				}
		    }
		} while(!depleted);

		List<Country> outerCore = Util2.setSubtract(util.countriesInIncomingRange(castle, 2, true), core);
		debug("still need some more; puling in outer core infantry; outer core = " + Util2.listToString(outerCore));
		do {
			depleted = true;
			for(Country c : outerCore) {
				if(troops.remainingInfantry(c).getTotalUnitCount() > 0) {
					Country coreNeighbor = Util2.setIntersect(util.realOutgoingList(c), core).get(0);
					debug("sending infantry from " + c + " to " + coreNeighbor);
					troops.moveInfantry(c, coreNeighbor, 1);
					reservedInfantry++;
					if(desiredDefenders[coreNeighbor.getID()] > 0) desiredDefenders[coreNeighbor.getID()]--;
					if(reservedInfantry == requiredInfantry) return desiredDefenders;
					depleted = false;
				}
			}
		} while(!depleted);

		if(castle.getOwner().equals(me)) {
			int deficit = requiredInfantry - reservedInfantry;
			int income = world.getPlayerMoney(id);
		    debug("my castle still needs more: deficit=" + deficit + " income=" + income + " desiredDefenders="
		    	  + desiredDefenders[castle.getID()]);
			if(desiredDefenders[castle.getID()] == 0 &&
		       deficit*infantryUnitMe.getCost() <= income) {
		    	debug("filling out remaining requiredInfantry (" + requiredInfantry + ") with recruits");
		    	troops.placeInfantry(castle, income/infantryUnitMe.getCost());		    	
		    }
		}
		
		debug("didn't get enough :( - requiredInfantry = " + requiredInfantry + "; reservedInfantry = " + reservedInfantry);
		
	    return desiredDefenders;
	}
	

	boolean weControlContinent(int contID, int extraTroops) {
		List<Country> contCountries = Util2.continentCountries(contID, countryList);
		
		UnitStackGroup totalOriginalGroup = troops.combinedOriginalGroup(contCountries);
		UnitStackGroup totalRemainingGroup = troops.combinedRemainingGroup(contCountries);
		UnitStackGroup totalIncomingGroup = troops.combinedIncomingGroup(contCountries);
		
		int totalEnemies = totalOriginalGroup.getTotalUnitCount()
		                   - Util2.teamSubgroup(totalOriginalGroup, us).getTotalUnitCount();
		int totalAllies = Util2.teamSubgroup(totalRemainingGroup, us).getTotalUnitCount()
		                  + Util2.teamSubgroup(totalIncomingGroup, us).getTotalUnitCount()
		                  + extraTroops;
		                  
		//we control the continent if we have twice as many troops as our enemies
        return (totalAllies > 2*totalEnemies);
	}
	
	

	//for each potential target, determine how many extra knights we would need
	//in attack position before we decided to attack it
	int[] getTargetExtraKnightsNeeded() {
		debug("doing getTargetExtraKnights()...");
		
		int[] extraKnightsNeeded = new int[numCountries];
		for(Country target : Util2.hostileCountries(us, countryList)) {
			if(Util2.teamCountries(us, util.knightSources(target)).size() > 0) {
				int numExtra = 0;
				UnitStack attackKnights = new UnitStack(knightUnitMe);
				while(true) {
					attackKnights.setCount(numExtra);
					double rank = rankWithExtra(target, Util2.groupFromStack(attackKnights));
					
					if(rank > 0) {
						extraKnightsNeeded[target.getID()] = numExtra;
                        debug("" + target + " ranks " + rank + " with " + numExtra + " extra knights)");
						break;
					} else {
						numExtra += 1;
					}
				}
			}
		}
		
		return extraKnightsNeeded;
	}

	Ranker<Country> getKnightRepositionDestinationRanker(final Country c, final int[] extraKnightsNeeded) {
		return new Ranker<Country> () {
			public double rank(Country dest) {
				Path2 fortifyPath = pathfinder.shortestPathBetween(c, dest, true);
				if(fortifyPath == null) return Double.NEGATIVE_INFINITY;
				int fortifyTurns = fortifyPath.turnsForKnight(troops);
				
				int numKnights = troops.remainingKnights(c).getTotalUnitCount();
				
				List<Country> alreadyInRange = util.knightDestinations(c);
				
				List<Country> targets = Util2.hostileCountries(us, util.knightDestinations(dest));
				double bestScore = Double.NEGATIVE_INFINITY;
				for(Country target : targets) {
					//if c can hit target, then extraKnightsNeeded already assumes c's knights are participating
					//if c is not in range then when c's knights move to dest they will count toward extraKnightsNeeded
					int availableExtraKnights = (alreadyInRange.contains(target) ? 0 : numKnights);
					int knightDeficit = Math.max(0, extraKnightsNeeded[target.getID()] - availableExtraKnights);
					UnitStackGroup extraAttackers = Util2.groupFromStack(new UnitStack(knightUnitMe, 
					                                                         availableExtraKnights + knightDeficit));
					double rank = rankWithExtra(target, extraAttackers);
					double score = 100*rank - 1000000*knightDeficit;
					if(score > bestScore) bestScore = score;
				}
				
				double ret = bestScore - 10*fortifyTurns 
		                     + troops.remainingKnights(dest).getTotalUnitCount() 
		                     + troops.incomingKnights(dest).getTotalUnitCount();
				debug("rank " + ret + " for reposition destination " + dest + " for " + numKnights + " knights in " + c + " (" + fortifyTurns + " away)");
				return ret;
			}
		};
	}
	
	
	Ranker<Path2> getKnightCastleRaidPathRanker(final double defendersPerMove, final int maxDefenders) {
	    return new Ranker<Path2> () {
		    public double rank(Path2 path) {
			    int defenders = path.getEnemyUnitStackGroup(us, troops).getTotalUnitCount();
			    int length = path.turnsForKnight(troops);
			    return -defenders - defendersPerMove*length - (defenders > maxDefenders ? 999999999 : 0);
		    }
		};
	}
	
	
	Ranker<Country> getTwoTurnKnightThreatRanker(final Country target) {
		return new Ranker<Country> () {
			public double rank(Country source) {
				if(us.equals(source.getTeam())) return Double.NEGATIVE_INFINITY;
				if(troops.originalKnights(source).getTotalUnitCount() == 0) return Double.NEGATIVE_INFINITY; 
				
				Path2 attackPath = pathfinder.bestPathBetween(source, target, false, twoTurnKnightPathRanker);
				
				debug("getTwoTurnKnightThreatRanker(target=" + target + ", source=" + source + ") attackPath = " + attackPath);
				
				if(attackPath == null) return Double.NEGATIVE_INFINITY;
				
				double rank = troops.originalKnights(source).getTotalUnitCount()
				              - attackPath.getEnemyUnitStackGroup(source.getTeam(), troops).getTotalUnitCount();
				
				//debug("...rank = " + rank);
				return rank;
			}
		};
	}
	
	Ranker<Path2> twoTurnKnightPathRanker = new Ranker<Path2> () {
		public double rank(Path2 path) {
			if(path.turnsForKnight(troops) > 2) {
				//debug("-too long: " + path );
				return Double.NEGATIVE_INFINITY;
			} else {
				//debug("-short enough: " + path);
				return -path.getEnemyUnitStackGroup(path.get(0).getTeam(), troops).getTotalUnitCount();
			}
		}
	};
	
	
	Ranker<Country> targetRanker = new Ranker<Country> () {
        public double rank(Country target) {
        	return rankWithExtra(target, new UnitStackGroup());
        }
	};
	
	double rankWithExtra(Country target, UnitStackGroup extra) {
		//debug("ranking target " + target + " with extra: " + extra);
		
		double rank = 0;
		
		UnitStackGroup attackerGroup = Util2.combine(safeAttackers(target), extra);
		//debug("attackerGroup: " + attackerGroup);
		UnitStackGroup defenderGroup = troops.originalGroup(target);
		//debug("defenderGroup: " + defenderGroup);
		UnitStackGroup maxDefenders = util.potentialDefenders(target);
		
		if(!Simulation2.winnable(attackerGroup, defenderGroup, Simulation2.TYPE_MOST_LIKELY)) {
			//debug("...unwinnable");
			return Double.NEGATIVE_INFINITY;
		}
		
		//taking over castles is good, but they'll be well-guarded. don't attempt it unless
		//we know we can win
		if(target.hasCastle()) {
			if(!Simulation2.winnable(attackerGroup, maxDefenders, Simulation2.TYPE_MOST_LIKELY)) {
				//debug("...unwinnable castle");
				return Double.NEGATIVE_INFINITY;
			} else {
				rank += 10000;
			}
		}
		
		//taking over continents is good
		int targetCont = target.getContinentID();
		double bonusPartial = target.getContinentBonusPartial(world);
		int remainingCountries = Util2.continentCountries(targetCont, countryList).size() - 
		                         Util2.continentCountries(targetCont, ourCountries).size();
		if(bonusPartial > 0) {
			if(weControlContinent(targetCont, 0)) {  
				//best are continents that we already mostly control
		  	    rank += 2000 - 5*remainingCountries + bonusPartial;
		    } else {
               	//next are continents that we would mostly control with the troops we might be bringing into it
				UnitStackGroup localAttackerGroup = safeAttackersInContinent(target);
				int foreignAid = attackerGroup.getTotalUnitCount() - localAttackerGroup.getTotalUnitCount()
				                 + extra.getTotalUnitCount();
				
				if(weControlContinent(targetCont, foreignAid)) {
					rank += 1000 - 5*remainingCountries + bonusPartial;
				}
		    }
		}
		
		//consolidating borders is good when we have overwhelming force
		int consolidationIndex = getUndistractedAllies(target).size();
		if(consolidationIndex >= 2 && 
		   attackerGroup.getTotalUnitCount() > 2*defenderGroup.getTotalUnitCount()) {
			rank += 100 + consolidationIndex;
		} else if(consolidationIndex == 1) {
			if(attackerGroup.getTotalUnitCount() > 2*maxDefenders.getTotalUnitCount()) {
				rank += 50;
			}
		}
		
		if(rank == 0 && attackerGroup.getTotalUnitCount() > 2*Util2.numInfantry(maxDefenders)) {
			rank += 1;
		}
		
		//debug("...winnable, returning " + rank);
		return rank;
	}
	
	
	Ranker<Country> recruitCastleRanker = new Ranker<Country> () {
		public double rank(Country castle) {
			if(countryInDanger(castle)) {
				debug("recruitCastleRanker: " + castle + " in danger");
				return -999999999;
			}

            Path2 pathToEnemy = pathfinder.shortestPathToEnemy(castle, countryList);
	    	if(pathToEnemy == null) {
	    		debug("recruitCastleRanker: " + castle + " has no path to enemy");
	    		return -99999;
	    	}
	    	
	    	debug("recruitCastleRanker: " + castle + " has path to enemy of length " + pathToEnemy.size());	    	
	    	return -pathToEnemy.size();
		}
	};

	
	
	
    Ranker<UnitStackGroup> threatRanker = new Ranker<UnitStackGroup> () {
		public double rank(UnitStackGroup g) {
			return requiredDefenders(g, new UnitStackGroup(), true, Simulation2.TYPE_FAVOR_ATTACKER);
		}
	};
	
	
	
	Ranker<Country> getBorderFortifyRanker(final Country c, final int[] desiredDefenders) {
		return new Ranker<Country> () {
			public double rank(Country dest) {
				Path2 pathToDest = pathfinder.shortestPathBetween(c, dest, true);
				if(pathToDest == null) return -999999999;
				else return desiredDefenders[dest.getID()]*1000  
				        -pathToDest.size();
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	public void setPrefs( int newID, VoxWorld theworld ){
		id = newID;
		world = theworld;
		
		me = world.getPlayer(id);
		us = me.getTeam();
		infantryUnitMe = new UnitPawn(me);
		knightUnitMe = new UnitKnight(me);
		castleUnitMe = new UnitCastle(me);
		
		util = new Util2(this);
		troops = new Troops(this);
		pathfinder = new Pathfinder2(util);
		rand = new Random(new Date().getTime());
	}

	public Polymath() {
		//System.out.println("CONTENDER CREATION");
	}
	
	public String name() {
		return "Polymath";
	}

	public float version() {
		return 2.0f;
	}

	public String description() {
		return "Polymath is a Castle Vox AI by Greg McGlynn.";
	}

	
	
	
	
	public String youWon() { 
		String[] messages = {
			 "Ah, distinctly I remember\n" + 
			 "It was in the bleak December\n" + 
			 "And each separate dying ember\n" + 
			 "Wrought its ghost upon the floor...",
			 
			 "Tell all the Truth but tell it slant--\n" +
			 "Success in Circuit lies\n" + 
			 "Too bright for our infirm Delight\n" +
			 "The Truth's superb surprise.\n" +
			 "As Lightning to the Children eased\n" +
			 "With explanation kind\n" + 
			 "The Truth must dazzle gradually\n" +
			 "Or every man be blind--",
			 			 
			 "In Xanadu did Kubla Khan\n" +
			 "A stately pleasure dome decree:\n" +
			 "Where Alph, the sacred river, ran\n" +
			 "Through caverns measureless to man\n" +
			 "Down to a sunless sea.",
			 
			 "Lay on, Macduff\n" + 
			 "And damn'd be him that first cries,\n" +
			 "'Hold! Enough!'",
			 
			 "The Sea of Faith,\n" +
			 "Was once, too, at the full, ...\n" +
			 "But now I only hear\n " + 
			 "Its melancholy, long, withdrawing roar,",
			 
			 "Tiger! tiger! burning bright,\n" +
			 "In the forests of the night,\n" + 
			 "What immortal hand or eye\n" +
			 "Dare frame thy fearful symmetry?",
			 
			 "Oft, in the stilly night\n" + 
			 "Ere slumber's chain has bound me,\n" + 
			 "Fond memory brings the light\n" + 
			 "Of other days around me.",
			 
			 "Some say the world will end in fire\n" + 
			 "Some say in ice.\n" + 
			 "From what I've tasted of desire,\n" + 
			 "I hold with those who favor fire.\n" +
			 "But if it had to perish twice,\n" +
			 "I think I know enough of hate\n" + 
			 "To know that for destruction ice\n" +
			 "Is also great\n" +
			 "And would suffice.",
			 
			 "'Twas brillig, and the slithy toves\n" +
			 "Did gyre and gimble in the wabe:\n" + 
			 "All mimsy were the borogroves,\n" + 
			 "And the mome raths outgrabe.",
			 
			 "Hear the loud alarum bells --\n" +
			 "Brazen bells!\n" +
			 "What a tale of terror, now, their turbulency tells!\n" + 
			 "In the startled ear of night\n" + 
			 "How they scream out their affright!"
		    };
		
		return messages[rand.nextInt(messages.length)];
	}

	public String message( String message, Object data ) {
		//debug("MESSAGE: message = " + message);
		//debug("...data = " + data);
		
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












