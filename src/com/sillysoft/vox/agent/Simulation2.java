




import com.sillysoft.vox.*;
import com.sillysoft.vox.unit.*;
import java.util.*;


public class Simulation2 {

	public static void main(String[] args) {

		UnitStackGroup attackers = new UnitStackGroup();
		UnitStackGroup defenders = new UnitStackGroup();
		
		
		Player A = new Player("A", new Team("A"));
		Player D = new Player("D", new Team("D"));
		
		attackers.add(new UnitStack(new UnitPawn(A), 8));
		attackers.add(new UnitStack(new UnitKnight  (A), 0));
		
		defenders.add(new UnitStack(new UnitPawn(D), 4));
		defenders.add(new UnitStack(new UnitKnight  (D), 0));
		defenders.add(new UnitStack(new UnitCastle  (D), 0));
		
        System.out.println("before: ");
        System.out.println("attackers: " + attackers);
        System.out.println("defenders: " + defenders);
		
		simulate(attackers, defenders, TYPE_MOST_LIKELY);

		System.out.println("after:");
        System.out.println("attackers: " + attackers);
        System.out.println("defenders: " + defenders);
	
	}
	
	static final int infantryAttackPower = 1;
	static final int infantryDefendPower = 3;
	static final int knightAttackPower = 5;
	static final int knightDefendPower = 2;
	static final int castleDefendPower = 10;
	
	static final int TYPE_RANDOM = 1;
	static final int TYPE_FAVOR_ATTACKER = 2;
	static final int TYPE_FAVOR_DEFENDER = 3;
	static final int TYPE_MOST_LIKELY = 4;
	
	static Random rand = new Random(new Date().getTime());
	
	private static void simulate(UnitStackGroup attackers, UnitStackGroup defenders, int type) {
		while(attackers.getTotalUnitCount() > 0 && defenders.getTotalUnitCount() > 0) {
			int attackPower = Util2.numInfantry(attackers)*infantryAttackPower +
			                  Util2.numKnights (attackers)*  knightAttackPower;
		    int defendPower = Util2.numInfantry(defenders)*infantryDefendPower +
		                      Util2.numKnights (defenders)*  knightDefendPower +
		                      Util2.numCastles (defenders)*  castleDefendPower;
			
		    int defenderKills, attackerKills;
		    if(type == TYPE_RANDOM) {
		    	attackerKills = randKills(attackPower);
		    	defenderKills = randKills(defendPower);
		    } else if(type == TYPE_FAVOR_ATTACKER) {
		    	attackerKills = mostKills(attackPower);
		    	defenderKills = leastKills(defendPower);
		    } else if(type == TYPE_FAVOR_DEFENDER) {
		    	attackerKills = leastKills(attackPower);
		    	defenderKills = mostKills(defendPower);
		    } else { //type == TYPE_MOST_LIKELY
		    	attackerKills = likelyKills(attackPower);
		    	defenderKills = likelyKills(defendPower);
		    }

		    if(attackerKills == 0 && defenderKills == 0) defenderKills = 1; //prevent infinite loop 
		    
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
	
	static int likelyKills(int power) {
		return (power + 2)/6;
	}
	
	public static int[] simulate(int numAttackerInfantry, int numAttackerKnights,
			                     int numDefenderInfantry, int numDefenderKnights,
			                     int type) {
		
		UnitStackGroup attackers = Simulation2.makeGroup(numAttackerInfantry, numAttackerKnights, "A");
		UnitStackGroup defenders = Simulation2.makeGroup(numDefenderInfantry, numDefenderKnights, "D");
		
		Simulation2.simulate(attackers, defenders, type);
		
		
		return new int[]{Util2.numInfantry(attackers), Util2.numKnights(attackers), 
				         Util2.numInfantry(defenders), Util2.numKnights(defenders)};
	}
	
	
	
	public static boolean winnable(UnitStackGroup attackers, UnitStackGroup defenders, int type) {
		return Simulation2.winnable(attackers, defenders, 1, type);
	}
	
	public static boolean winnable(UnitStackGroup attackers, UnitStackGroup defenders, double ratio, int type) {
		UnitStackGroup simAttackers = Simulation2.clone(attackers, ratio);
		UnitStackGroup simDefenders = Simulation2.clone(defenders);
		Simulation2.simulate(simAttackers, simDefenders, type);
		return (simAttackers.getTotalUnitCount() > 0);
	}
	
	public static boolean winnable(int numAttackerInfantry, int numAttackerKnights,
			                       int numDefenderInfantry, int numDefenderKnights,
			                       int type) {
		
		UnitStackGroup simAttackers = Simulation2.makeGroup(numAttackerInfantry, numAttackerKnights, "A");
		UnitStackGroup simDefenders = Simulation2.makeGroup(numDefenderInfantry, numDefenderKnights, "D");
		Simulation2.simulate(simAttackers, simDefenders, type);
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
		return Simulation2.clone(g, 1);
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

