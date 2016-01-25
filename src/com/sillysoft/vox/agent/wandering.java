package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;
import java.util.*;

/* Authour: Tony Y. T. Chan
 * Date: 2010 3 17
 * A mindless wandering bot for the map Castle Lux IP
 */
public class wandering extends VoxAgentBase implements VoxAgent {

  public void declareMoves(Country[] countries) {

    Team myTeam, team_i;
    Country c, ourCountry, moveTo, dir1, dir2;
    UnitStackGroup usg;
    UnitStack us;
    int money, IKCtotal, i, j;
    List castleList;

    myTeam = world.getTeam(ID);
    money = world.getPlayerMoney(ID);
    castleList = world.getCastleCountriesOwnedBy(ID);
    i = rand.nextInt(castleList.size());   // Get a random castle
    c = (Country) castleList.get(i);
    // Buy 1 infantry, then 1 knight, as much as money allows.
    // Place them on castle c.
    buyUnitsAlternating(money, c);

    for (i = 0; i < countries.length; i++) {
      // Check out this country's force.
      c = countries[i];
      usg = c.getUnitStackGroup();
      IKCtotal = usg.getTotalUnitCount();  // # of infantry + # of knight + castle
      team_i = c.getTeam();
      if (IKCtotal > 0 && team_i.equals(myTeam)) {
        ourCountry = c;
        // Loop through this country's stack group
        int N_usg = usg.size();
        for (j = N_usg-1; j > -1; j--) {
          // UnitStack class represents a group of units that all share the same type and owner and original Country.
          us = usg.get(j);
          int ID2 = us.getOwner().getID();
          Unit usUnit = us.getUnit();
          // Infantry movement stepSize: 1 country; knight: 2 countries
          int stepSize = usUnit.getMovement();
          // Same team, same player? for this stack unit
          if (ID2 == ID && stepSize > 0) {
            moveTo = getRandomEnemyBorder(ourCountry);
            if (moveTo == null) {
              // We can't reach any enemy country. Move them towards the front-lines
              dir1 = directionToEnemy(ourCountry);
              dir2 = directionToEnemy(dir1);
              if (dir2.equals(ourCountry)) {
                moveTo = dir1;
              }
            }
            // Number of units of infantry xor knight
            int N_units = us.getCount();
            world.moveUnit(us, ourCountry, moveTo, N_units);
          }
        }
      }
    }
  }

  public String name() {
    return "wandering";
  }

  public float version() {
    return 1.0f;
  }

  public String description() {
    return "A mindless wandering bot.";
  }

  public String youWon() {
    return "I won.";
  }
}	// End of class

