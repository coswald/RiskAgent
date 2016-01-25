



import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import java.util.*;


public class Simulation {

	public static void main(String[] args) {
		int[] result = simulate(25, 10, 25, 10, TYPE_RANDOM);
		System.out.printf("%d, %d, %d, %d\n", result[0], result[1], result[2], result[3]);
	}
	
	static final int infantryAttackPower = 1;
	static final int infantryDefendPower = 3;
	static final int knightAttackPower = 5;
	static final int knightDefendPower = 2;
	static final int castleDefendPower = 10;
	
	static final int TYPE_RANDOM = 1;
	static final int TYPE_FAVOR_ATTACKER = 2;
	static final int TYPE_FAVOR_DEFENDER = 3;
	
	static Random rand = new Random(new Date().getTime());
	
	private static void simulate(UnitStackGroup attackers, UnitStackGroup defenders, int type) {
		while(attackers.getTotalUnitCount() > 0 && defenders.getTotalUnitCount() > 0) {
			
			int attackPower = Util.numInfantry(attackers)*infantryAttackPower +
			                  Util.numKnights (attackers)*  knightAttackPower;
		    int defendPower = Util.numInfantry(defenders)*infantryDefendPower +
		                      Util.numKnights (defenders)*  knightDefendPower +
		                      Util.numCastles (defenders)*  castleDefendPower;
		
		    int defenderKills, attackerKills;
		    if(type == TYPE_RANDOM) {
		    	attackerKills = randKills(attackPower);
		    	defenderKills = randKills(defendPower);
		    } else if(type == TYPE_FAVOR_ATTACKER) {
		    	attackerKills = mostKills(attackPower);
		    	defenderKills = leastKills(defendPower);
		    } else {
		    	attackerKills = leastKills(attackPower);
		    	defenderKills = mostKills(defendPower);
		    }
		    
		    attackers.killUnits(defenderKills);
		    defenders.killUnits(attackerKills);
		}
	}
	
	static int randKills(int power) {
		return power/6 + (rand.nextInt(6) > (power%6) ? 1 : 0);
	}
	
	static int mostKills(int power) {
		return (power+5)/6;
	}
	
	static int leastKills(int power) {
		return power/6;
	}
	
	public static int[] simulate(int numAttackerInfantry, int numAttackerKnights,
			                     int numDefenderInfantry, int numDefenderKnights,
			                     int type) {
		
		UnitStackGroup attackers = Simulation.makeGroup(numAttackerInfantry, numAttackerKnights, "A");
		UnitStackGroup defenders = Simulation.makeGroup(numDefenderInfantry, numDefenderKnights, "D");
		
		Simulation.simulate(attackers, defenders, type);
		
		
		return new int[]{Util.numInfantry(attackers), Util.numKnights(attackers), 
				         Util.numInfantry(defenders), Util.numKnights(defenders)};
	}
	
	
	
	public static boolean winnable(UnitStackGroup attackers, UnitStackGroup defenders, int type) {
		return Simulation.winnable(attackers, defenders, 1, type);
	}
	
	public static boolean winnable(UnitStackGroup attackers, UnitStackGroup defenders, double ratio, int type) {
		UnitStackGroup simAttackers = Simulation.clone(attackers, ratio);
		UnitStackGroup simDefenders = Simulation.clone(defenders);
		Simulation.simulate(simAttackers, simDefenders, type);
		return (simAttackers.getTotalUnitCount() > 0);
	}
	
	public static boolean winnable(int numAttackerInfantry, int numAttackerKnights,
			                       int numDefenderInfantry, int numDefenderKnights,
			                       int type) {
		
		UnitStackGroup simAttackers = Simulation.makeGroup(numAttackerInfantry, numAttackerKnights, "A");
		UnitStackGroup simDefenders = Simulation.makeGroup(numDefenderInfantry, numDefenderKnights, "D");
		Simulation.simulate(simAttackers, simDefenders, type);
		return (simAttackers.getTotalUnitCount() > 0);
	}
	
	
	
	public static UnitStackGroup makeGroup(int numInfantry, int numKnights, String playerTag) {
		Player player = new Player(playerTag, new Team(playerTag));
		UnitStack infantry = new UnitStack(new UnitPawn(player), numInfantry);
		UnitStack knights = new UnitStack(new UnitKnight(player), numKnights);
		
		UnitStackGroup ret = new UnitStackGroup();
		ret.add(infantry);
		ret.add(knights);
		return ret;
	}
		
	public static UnitStackGroup clone(UnitStackGroup g) {
		return Simulation.clone(g, 1);
	}
	
	public static UnitStackGroup clone(UnitStackGroup g, double ratio) {
		UnitStackGroup ret = new UnitStackGroup();
		UnitStackGroupIterator stacks = new UnitStackGroupIterator(g);
		while(stacks.hasNext()) {
			UnitStack stack = stacks.next();
			ret.add(new UnitStack(stack.getUnit(), (int)(ratio*stack.getCount())));
		}
		return ret;
	}
	
	
}

