package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;
import java.util.*;

public class AI1 extends VoxAgentBase implements VoxAgent {
  // Object instance variables

  private String CountryNames[], CountryNamesSorted[];
  private int humanID = -1;
  private int humanFriendlyID = -1;
  private Country humanFriendlyCastle;
  private int humanCastleI, humanFriendlyCastleI;
  private int Round = 0;
  private Team humanTeamName, myTeam; // name of the human team (human & bot)
  private int N_countries, N_players;
  private String AI1name;
  private Player humanFriendlyPlayer, AI1player;
  private int stop;
  private Team team_i;
  private List castleList;
  private boolean IDisAI1Bot[];
  private int edgeCost[][], pathCost[][], visitThru[][], N_neighbors[], adjacencyList[][];
  private Random rand = new Random();

  private void Round1Init(Country[] countries) {
    int c, c1, c2, i, j, k, n;
    int infinity = 9999;
    Player player_i;
    String name;
    Country ct, ct2;
    List adjoiningList;

    N_players = world.getNumberOfPlayers();
    N_countries = countries.length;
    AI1name = world.getPlayer(ID).name();
    AI1player = world.getPlayer(ID);
    myTeam = world.getTeam(ID);

    IDisAI1Bot = new boolean[N_players];
    for (c = 0; c < N_players; c++) {
      player_i = world.getPlayer(c);
      name = player_i.getAgentType();
      if (name.equals("AI1")) {
        IDisAI1Bot[c] = true;
      } else {
        boolean human = player_i.isHuman();
        if (human) {
          humanTeamName = world.getTeam(c);
          humanID = c;

        }
      }
    }

    // Find the bot friendly to the human.
    for (i = 0; i < N_players; i++) {
      team_i = world.getTeam(i);
      if ((humanID > -1) && i != humanID && team_i.equals(humanTeamName)) {
        humanFriendlyID = i;
        humanFriendlyPlayer = world.getPlayer(humanFriendlyID);
        break;  // Break out of this for-loop.
      }
    }

    humanFriendlyCastleI = get1CastleOwnedBy(humanFriendlyID);
    humanCastleI = get1CastleOwnedBy(humanID);



    edgeCost = new int[N_countries][N_countries];
    for (c = 0; c < N_countries; c++) {
      for (j = 0; j < N_countries; j++) {
        edgeCost[c][j] = infinity;
      }
    }

    CountryNames = new String[N_countries];
    CountryNamesSorted = new String[N_countries];
    for (c = 0; c < N_countries; c++) {
      ct = countries[c];
      int t1 = ct.getID();
      CountryNames[c] = ct.getName();
      CountryNamesSorted[c] = CountryNames[c] + ' ' + c;
      edgeCost[c][c] = 0;
    }
    java.util.Arrays.sort(CountryNamesSorted);

    adjacencyList = new int[N_countries][];
    for (c = 0; c < N_countries; c++) {
      ct = countries[c];
      adjoiningList = ct.getAdjoiningList();
      n = adjoiningList.size();
      adjacencyList[c] = new int[n];
      for (j = 0; j < n; j++) {
        ct2 = (Country) adjoiningList.get(j);
        k = ct2.getID();

        edgeCost[c][k] = 1;
        adjacencyList[c][j] = k;
      }
    }


    boolean t = symmetric(edgeCost);

    N_neighbors = new int[N_countries];
    for (c = 0; c < N_countries; c++) {
      N_neighbors[c] = 0;
      for (j = 0; j < N_countries; j++) {
        if (edgeCost[c][j] == 1) {
          N_neighbors[c] += 1;
        }
      }
    }

    pathCost = new int[N_countries][N_countries];
    visitThru = new int[N_countries][N_countries];
    for (c1 = 0; c1 < N_countries; c1++) {
      for (c2 = 0; c2 < N_countries; c2++) {
        pathCost[c1][c2] = edgeCost[c1][c2];
        visitThru[c1][c2] = -1;
      }
    }

    for (k = 0; k < N_countries; k++) {
      for (i = 0; i < N_countries; i++) {
        for (j = 0; j < N_countries; j++) {
          if (pathCost[i][k] + pathCost[k][j] < pathCost[i][j]) {
            pathCost[i][j] = pathCost[i][k] + pathCost[k][j];
            visitThru[i][j] = k;
          }
        }
      }
    }

  }

  private int get1CastleOwnedBy(int ID) {
    Country ct;
    castleList = world.getCastleCountriesOwnedBy(ID);
    ct = (Country) castleList.get(0);
    return ct.getID();
  }

  private String shortestPath(int i, int j) {
    int k;
    k = visitThru[i][j];
    if (k == -1) {
      return " ";
    }
    return shortestPath(i, k) + " " + k + " " + shortestPath(k, j);
  }

  private boolean symmetric(int[][] matrix) {
    int i, j, n;
    n = matrix.length;
    for (i = 0; i < n; i++) {
      for (j = 0; j < n; j++) {
        if (matrix[i][j] != matrix[j][i]) {
          return false;
        }
      }
    }
    return true;
  }
  
  public void declareMoves(Country[] countries) {
    Country ct, ourCountry, castle, moveTo, moveTo1, moveTo2, dir1, dir2;
    UnitStackGroup usg;
    UnitStack us;
    int from_c;
    int c, s, j, k, money, IKCtotal, N_usg, N_units, id, next_c;
    Player player;
    Team team;

    Round += 1;

    money = world.getPlayerMoney(ID);

    if (Round == 1) {
      Round1Init(countries);
      castle = getStrongestBase();
      buyKnights(money, castle);
      return;
    }
    if ((humanID > -1) && myTeam.equals(humanTeamName)) {
      return;
    }

    money = world.getPlayerMoney(ID);
    castle = getStrongestBase();
    if (castle != null) {
      buyKnights(money, castle);
    }


    for (from_c = 0; from_c < N_countries; from_c++) {
      ct = countries[from_c];
      usg = ct.getUnitStackGroup();
      IKCtotal = usg.getTotalUnitCount();  // # of infantry + # of knight + castle
      team_i = ct.getTeam();
      if (IKCtotal > 0 && team_i.equals(myTeam)) {

        N_usg = usg.size();
        // Loop through this country's stack group from the top
        for (s = N_usg - 1; s > -1; s--) {
          // UnitStack class represents a group of units that all share the same type and owner and original Country.
          us = usg.get(s);
          int ID2 = us.getOwner().getID();
          Unit usUnit = us.getUnit();
          // Infantry movement: 1; knight: 2
          int m = usUnit.getMovement();
          // Same team, same owner? for this stack
          if (ID2 == ID && m > 0) {
            player = countries[humanFriendlyCastleI].getOwner();
            if (player.equals(humanFriendlyPlayer)) {
              next_c = visitNext(from_c, humanFriendlyCastleI);
            } else {
              player = countries[humanCastleI].getOwner();
              id = player.getID();
              if (id == humanID) {
                next_c = visitNext(from_c, humanCastleI);
              } else {
                moveTo = getRandomEnemyBorder(countries[from_c]);
                if (moveTo == null) {
                  moveTo = getRandomBorder(countries[from_c]);
                }
                next_c = moveTo.getID();               
              }

            }
            N_units = us.getCount();
            world.moveUnit(us, ct, countries[next_c], N_units);
          }
        }
      }
    }
  }

  private int visitNext(int from, int to) {
    int i;
    String path, ct;
    if (edgeCost[from][to] == 1) {
      return to;
    } else {
      path = shortestPath(from, to);
      path = path.replaceAll("^\\s+", "");  // Trim the leading blanks.
      i = path.indexOf(" ");
      ct = path.substring(0, i);
      return Integer.valueOf(ct);
    }

  }

  public String name() {
    return "AI1";
  }

  public float version() {
    return 1.0f;
  }

  public String description() {
    return "AI1 bot.";
  }

  public String youWon() {
    return "AI won.";
  }
}	// End of AI1 class

