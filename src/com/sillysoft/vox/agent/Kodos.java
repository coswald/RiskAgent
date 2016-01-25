package com.sillysoft.vox.agent;

import java.util.ArrayList;
import java.util.Iterator;
import com.sillysoft.vox.unit.*;
import com.sillysoft.vox.*;
import java.util.*;

/* Authour: Tony Y. T. Chan
 * Date: 2010 6 24
 */
public class Kodos extends VoxAgentBase implements VoxAgent {

  //private int debugVersion = 102;
  private Random rand = new Random();
  private int infantryAttackValue = 1, knightAttackValue = 5;
  private int infantryDefenceValue = 3, knightDefenceValue = 2;
  private int infantryCost = 3, knightCost = 5;
  private Country Nodes[];
  private String PlayerName[], Brain[], nodeNames[], nodeNamesSorted[];
  private int Home;  // Original castle at the start of the game
  private int myTeamID;
  private int N_attributes = 6, playerAttributes[][];
  private int Round = 0;
  private int N_nodes, N_players;
  private String whoAmI;
  private int stop;
  private int occupationTable0[][], occupationTable1[][];
  // next = visitNext[source][destination]
  private int visitNext[][];
  // edgeCost[i][j] = the cost of going from Node i to Node j
  // Square but may not be symmetric. Similar to adjacency matrix.
  private int edgeCost[][];
  private int pathCost[][], visitFrequency[],   adjacencyList[][] ;
  private static final int infinity = 99999;
  private boolean myBorderFacingEnemies[];
  // In order of distance from node C
  // nodesNear[C][0], nodesNear[C][1], ..., nodesNear[C][N_nodes-1]
  private int nodesNear[][];
  private int nodeIncomeAt[];

  public void declareMoves(Country[] countries) {
    super.declareMoves(countries);
    Round++;
    whoAmI = world.getPlayer(ID).name();
    //if (!whoAmI.equals("Zonlor")) {      return;    }
    //if (!whoAmI.equals("Bad Guy")) {      return;    }
    //if (!whoAmI.equals("britain")) {      return;    }
    Nodes = countries;
    if (Round == 1) {
      Round1Init();
      makeOccupationTable0();
      initOccupationTable1();
    }
    if (Round > 1) {
      makeOccupationTable0();
      calOccupationTable1();
    }
    getPlayersAttributes();
    myBorderFacingEnemies = getMyBorderFacingEnemies();
    reinforceCastles();
    spreadInfantry();
    spreadKnight();
    buyKnightAndInfantryForMyCastles();
  }

  private void Round1Init() {
    N_nodes = Nodes.length;
    makeNodeNamesSorted();
    N_players = world.getNumberOfPlayers();
    playerAttributes = new int[N_players][N_attributes];
    getPlayersHomeCastle();
    getPlayersTeamID();
    Home = playerAttributes[ID][0];
    myTeamID = playerAttributes[ID][1];
    getBrainAndPlayerName();
    buildShortestPathArray();
    make_NodesNear();
    makeNodeIncomeAt();
  }

  private void makeNodeIncomeAt() {
    int income;
    nodeIncomeAt = new int[N_nodes];
    for (int c = 0; c < N_nodes; c++) {
      income = (int) getIncome(Nodes[c]);
      nodeIncomeAt[c] = income;
    }
  }

  private void buyKnightAndInfantryForMyCastles() {
    double knightPercentage = 0.9;  // Spend 90% of money for buying knights.
    int Money, moneyC, moneyK, moneyI, need;
    int N_castles = getN_MyCastles();
    if (N_castles == 0) {
	  buyCastle();
      return;
    }
    Money = world.getPlayerMoney(ID);
    moneyC = Money / N_castles;
    moneyK = (int) (knightPercentage * moneyC);
    moneyI = moneyC - moneyK;
    int N_knight = moneyK / knightCost;
    int N_Infantry = moneyI / infantryCost;
    for (int c = 0; c < N_nodes; c++) {
      if (!castleIsMine(c)) {
        continue;
      }
      need = calInfantryNeededForDefenceIn1At(c);
      int N_infantry = getN_infantryAt(c);
      if (N_infantry < need) {
        // Do not buy knight. It may be too late.
        continue;
      }
      buyKnightIn(c, N_knight);
      buyPawnsIn(c, N_Infantry);
    }
  }

  private int getAttackValueIn2Against(int C) {
    int r = getAttackValueAgainst(C, 2);
    return r;
  }

  private int getAttackValueAgainst(int C, int moves) {
    // threat against Node C in this the following
    // rounds specified by moves
    int near, distance = 0;
    int total = 0;
    for (int i = 0; i < N_nodes - 1; i++) {
      near = nodesNear[C][i];
      String botName = brainAt(near);
      if (botName.equals("Boring")) {
        continue;
      }
      if (friendly(C, near)) {  // not hostile
        continue;
      }
      distance = pathCost[C][near];
      if (distance <= moves) {
        total += getHostileAttackValueAt(near);
        continue;
      }
      if (distance <= moves * 2) {
        total += getHostileKnightAttackValueAt(near, C);
        continue;

      } else {
        return total;
      }
    }
    return -1; // This statement should never get executed.
  }

  private int getHostileKnightAttackValueAt(int c, int C) {
    // Attack comes from: c is hostile
    // Attack on: C is mine
    if (friendly(c)) {
      return 0;
    }
    int nKnight = getKnightCountAt(c);
    int attack = nKnight * knightAttackValue;
    int defence;
    int next = visitNext[c][C];
    while (next != C & attack > 0) {
      defence = getFriendlyDefenceValueAt(next);
      attack -= defence;  // reduce the hostile attack strength on C
      next = visitNext[next][C];
    }
    if (attack <= 0) {
      return 0;
    }
    return attack;
  }

  private int getHostileAttackValueAt(int c) {
    int nInfantry = getInfantryCountAt(c);
    int nKnight = getKnightCountAt(c);
    int total = nInfantry * infantryAttackValue + nKnight * knightAttackValue;
    return total;
  }

  private int getHostileInfantryAttackValueFrom(int c) {
    int nInfantry = occupationTable0[c][1] + occupationTable0[c][5];
    int total = nInfantry * infantryAttackValue;
    return total;
  }

  private void reinforceCastle(int C) {
    int attack = getAttackValueIn2Against(C);
    if (attack == 0) {
      int N_infantry = getN_infantryAt(C);
      spreadInfantryFrom(C, N_infantry);
      return;
    }
    int myDefence = getInfantryDefenceValueAt(C);
    float excess = myDefence - attack;
    if (excess < 0) {
      // I may need this many in the following round
      //    |
      int needed = (int) (Math.abs(excess) / infantryDefenceValue);
      needed *= .9;  // spare some money to purchase offensive knight
      buyPawnsIn(C, needed);
      return;
    }
    // Remove some excess infantry
    int unneeded = (int) (0.8 * excess / infantryDefenceValue);
    spreadInfantryFrom(C, unneeded);
  }

  private void buyCastleAt(int c) {
    Unit castle = new UnitCastle(world.getPlayer(ID));
    UnitStack castle2 = new UnitStack(castle, 1);
    world.placeUnits(castle2, Nodes[c]);
  }

  private void reinforceCastles() {
    int N_castles = getN_MyCastles();
    if (N_castles == 0) {
      int node = findMyNodeAwayFromEnemies();
      buyCastleAt(node);
    }
    reinforceCastle(Home);
    for (int c = 0; c < N_nodes; c++) {
      if (c == Home) {
        continue;
      }
      if (castleIsNotMine(c)) {
        continue;
      }
      reinforceCastle(c);
    }
  }

  private int findMyNodeAwayFromEnemies() {
    int currentFurtherest = -1;
    int safestNode = -1;
    for (int c = 0; c < N_nodes; c++) {
      if (IdontOwn(c)) {
        continue;
      }
      int count = getMyKnightAndInfantryCountAt(c);
      if (count < 5) { // must have at least 5 units to build a castle
        continue;
      }
      int closest = findClosestEnemyFrom(c);
      int dist = pathCost[c][closest];
      if (dist > currentFurtherest) {
        currentFurtherest = dist;
        safestNode = c;
      }
    }
    return safestNode;
  }

  private int findClosestEnemyFrom(int C) {
    for (int i = 0; i < N_nodes - 1; i++) {
      int near = nodesNear[C][i];
      if (unfriendly(near)) {
        return C;
      }
    }
    return -1;
  }

  private int[] showCol(int[][] matrix, int j) {
    int M = matrix.length;
    int[] col = new int[M];
    for (int i = 0; i < M; i++) {
      col[i] = matrix[i][j];
    }
    return col;
  }

  private void getPlayersAttributes() {
    for (int PID = 0; PID < N_players; PID++) {
      // Current number of nodes owned
      playerAttributes[PID][2] = world.getPlayerLandCount(PID);
      // Current total army value + castles
      playerAttributes[PID][3] = world.getPlayerArmyCount(PID);
      // Current income
      playerAttributes[PID][4] = world.getPlayerIncome(PID);
      playerAttributes[PID][5] = world.getPlayerMoney(PID);
    }
  }

  private void getPlayersHomeCastle() {
    int c, ownerID;
    boolean castle;
    for (c = 0; c < N_nodes; c++) {
      castle = Nodes[c].hasCastle();
      if (!castle) {
        continue;
      }
      ownerID = Nodes[c].getOwner().getID();
      playerAttributes[ownerID][0] = c;   // 0: home castle of this player
    }
  }

  private void getPlayersTeamID() {
    Team TEAM[];
    TEAM = new Team[N_players];
    for (int i = 0; i < N_players; i++) {
      Team team_i = world.getTeam(i);
      TEAM[i] = team_i;
    }
    for (int i = 0; i < N_players; i++) {
      for (int j = 0; j < N_players; j++) {
        if (TEAM[i].equals(TEAM[j])) {
          playerAttributes[i][1] = j;     // 1: team ID: j
          break;   // next i
        }
      }
    }
  }

  private boolean isAcastle(int C) {
    if (occupationTable0[C][3] == 1) {
      return true;
    }
    return false;
  }

  private ArrayList getHostiles1From(int c) {
    // get unfriendly & occupied neighbors
    ArrayList list = new ArrayList();
    int N_neighbors = adjacencyList[c].length;
    for (int i = 0; i < N_neighbors; i++) {
      int neighbor = adjacencyList[c][i];
      if (occupiedIn(neighbor) & unfriendly(c, neighbor)) {
        list.add(new Integer(neighbor));
      }
    }
    return list;
  }

  private boolean unfriendly1(int c1, int c2) {
    if (friendly1(c1, c2)) {
      return false;
    }
    return true;
  }

  private boolean unfriendly(int c1, int c2) {
    if (friendly(c1, c2)) {
      return false;
    }
    return true;
  }

  private boolean myInfantryIn(int C) {
    if (occupationTable1[C][1] > 0) {
      return true;
    }
    return false;
  }

  private boolean myKnightIn(int C) {
    if (occupationTable1[C][2] > 0) {
      return true;
    }
    return false;
  }

  private int getNextUnfriendly1NodeFrom(int C) {
    int c;
    for (int i = 0; i < N_nodes - 1; i++) {
      c = nodesNear[C][i];
      if (unfriendly1(c)) {
        return c;
      }
    }
    return -1;
  }

  private int find1nearestBorderNodeFaceEnemy(int C) {
    int nearest[];
    nearest = new int[2];
    nearest = find2nearestBorderNodesFaceEnemy(C);
    return nearest[0];
  }

  private int[] find2nearestBorderNodesFaceEnemy(int C) {
    int c, k;
    int[] nearest2 = {-1, -1};
    nearest2 = new int[2];
    k = 0;
    for (int i = 0; i < N_nodes - 1; i++) {
      c = nodesNear[C][i];
      if (!myBorderFacingEnemies[c]) {
        continue;
      }
      if (!neighborIsUnfriendlyFrom(c)) {
        continue;
      }
      if (resistance(C, c)) {  // not safe to travel to c
        continue;
      }
      nearest2[k] = c;
      k++;
      if (k == 2) {
        return nearest2;
      }
    }
    return nearest2;
  }

  private boolean neighborIsUnfriendlyFrom(int C) {
    // C has at least one unfriendly neighbor?
    int N_neighbors = adjacencyList[C].length;
    for (int i = 0; i < N_neighbors; i++) {
      int neighbor = adjacencyList[C][i];
      if (unfriendly(neighbor)) {
        return true;
      }
    }
    return false;
  }

  private ArrayList<String> getMyBorderList() {
    ArrayList<String> border = new ArrayList<String>();
    String node;
    for (int c = 0; c < N_nodes; c++) {
      if (myBorderFacingEnemies[c]) {
        node = c + " " + nodeNames[c];
        border.add(node);
      }
    }
    return border;
  }

  private boolean[] getMyBorderFacingEnemies() {
    int N, i, neighbor;
    boolean[] border = new boolean[N_nodes];
    for (int c = 0; c < N_nodes; c++) {
      if (IdontOwn(c)) {
        continue;
      }
      if (!isConnected(c)) {
        // If c is a singleton and disconnected, do not count as part of the border
        continue;
      }
      N = adjacencyList[c].length;
      for (i = 0; i < N; i++) {
        neighbor = adjacencyList[c][i];
        if (!friendly(neighbor)) {
          border[c] = true;
          break;   // check next node
        }
      }
    }
    return border;
  }

  private boolean isConnected(int C) {
    // Node C is connected to my nodes
    for (int i = 0; i < N_nodes - 1; i++) {
      int c = nodesNear[C][i];
      if (edgeCost[C][c] > 1) {
        return false;
      }
      if (Iown(c)) {
        return true;
      }
    }
    return false; // Should never be executed
  }

  private int getN_MyCastles() {
    int N = 0;
    for (int c = 0; c < N_nodes; c++) {
      if (castleIsMine(c)) {
        N += 1;
      }
    }
    return N;
  }

  private boolean castleIsNotMine(int C) {
    if (castleIsMine(C)) {
      return false;
    }
    return true;
  }

  private void moveInfantry(int N, int from, int to) {
    move(N, "Infantry", from, to);
  }

  private int calInfantryNeededForDefenceIn1At(int c) {
    int attack = getAttackValueIn1Against(c);
    int defence = getInfantryDefenceValueAt(c);
    int defenceNeed = attack - defence;
    if (defenceNeed <= 0) {
      return 0;
    }
    return defenceNeed / infantryDefenceValue + 5;
  }

  private int getAttackValueIn1Against(int C) {
    int r = getAttackValueAgainst(C, 1);
    return r;
  }

  private int getMyKnightAndInfantryCountAt(int C) {
    if (IdontOwn(C)) {
      return 0;
    }
    return occupationTable0[C][1] + occupationTable0[C][2];
  }

  private int getKnightCountAt(int C) {
    return occupationTable0[C][2] + occupationTable0[C][6];
  }

  private int getInfantryCountAt(int C) {
    return occupationTable0[C][1] + occupationTable0[C][5];
  }

  private int getFriendlyDefenceValueAt(int C) {
    if (friendly(C)) {
      return getDefenceValueAt(C);
    }
    return 0;
  }

  private boolean resistance(int from, int to) {
    // clear sailing or resistance (fighting) on the way?
    int next;
    boolean unf, occ;
    next = visitNext[from][to];
    while (next != to) {
      unf = unfriendly1(from, next);
      occ = occupiedIn(next);
      if (unf & occ) {
        return true;
      }
      next = visitNext[next][to];
    }
    return false;
  }

  private int getDefenceValueAt(int c) {
    int nInfantry = occupationTable0[c][1] + occupationTable0[c][5];
    int nKnight = occupationTable0[c][2] + occupationTable0[c][6];
    int total = nInfantry * infantryDefenceValue + nKnight * knightDefenceValue;
    return total;
  }

  private int getDefenceValueAtHostile(int c) {
    int nInfantry = occupationTable0[c][1] + occupationTable0[c][5];
    int nKnight = occupationTable0[c][2] + occupationTable0[c][6];
    int total = nInfantry * infantryDefenceValue + nKnight * knightDefenceValue;
    return total;
  }

  private int getKnightDefenceValueAtHostile(int c) {
    int nKnight = occupationTable0[c][2] + occupationTable0[c][6];
    int total = nKnight * knightDefenceValue;
    return total;
  }

  private int getDefenceValueAtHostile(int C, int moves) {
    // Hostile team defence value at Node C in this the following
    // rounds specified by moves
    String botName;
    int near, distance = 0;
    // defence value already at C
    int total = getDefenceValueAtHostile(C);
    // defence values that might come
    for (int i = 0; i < N_nodes - 1; i++) {
      near = nodesNear[C][i];
      botName = brainAt(near);
      if (botName.equals("Boring")) {
        continue;
      }
      if (unfriendly(C, near)) {
        continue;
      }
      distance = pathCost[C][near];
      if (distance <= moves) {
        total += getDefenceValueAtHostile(near);
        continue;
      }
      if (distance <= moves * 2) {
        total += getKnightDefenceValueAtHostile(near);
        continue;

      } else {
        return total;
      }
    }
    return -1; // This statement should never get executed.
  }

  private int getAttackValue1Bagainst(int c) {
    // threat against Node c in the immediate coming round.
    int between;
    String botName;
    int total = 0;
    for (int n = 0; n < N_nodes; n++) {  // find neighbors
      botName = brainAt(n);
      if (botName.equals("Boring")) {
        continue;
      }
      if (friendly(c, n)) {  // not hostile
        continue;
      }
      if (pathCost[n][c] == 1) { // near c
        total += getHostileAttackValueAt(n);
      }
      if (pathCost[n][c] == 2) { // check knights
        between = visitNext[n][c];
        // Knight cannot cross a defenced unfriendly node rightaway.
        if (friendly(between, n) | unoccupiedIn(between)) {
          total += getKnightAttackValueFrom(n);
        }
      }
    }
    return total;
  }

  private boolean IdontOwn(int c) {
    return !Iown(c);
  }

  private int calKnightNeededAgainst(int c) {
    int defence = getDefenceValueAt(c);
    int needed = defence / knightAttackValue;
    int extra = (int) (needed * 0.1);
    int extra2 = Math.max(extra, 3);
    return needed + extra2;
  }

  private ArrayList getFreebies1From(int c) {
    // get unfriendly & unoccupied neighbors
    ArrayList list = new ArrayList();
    int N_neighbors = adjacencyList[c].length;
    for (int i = 0; i < N_neighbors; i++) {
      int neighbor = adjacencyList[c][i];
      if (unoccupiedIn(neighbor) & unfriendly(c, neighbor)) {
        list.add(new Integer(neighbor));
      }
    }
    return list;
  }

  private ArrayList getFreebies2From(int f) {
    ArrayList list = new ArrayList();
    list = getFreebies1From(f);
    for (int c = 0; c < N_nodes; c++) {
      if (pathCost[f][c] != 2) {
        continue;
      }
      if (occupiedIn(c)) {
        continue;
      }
      if (friendly(c)) {
        continue;
      }
      int between = visitNext[f][c];
      // Knight cannot cross a defenced unfriendly node rightaway.
      if (unfriendly(f, between)) {
        continue;
      }
      list.add(new Integer(c));
    }
    return list;
  }

  private void spreadInfantry() {
    int N_infantry, next;
    for (int c = 0; c < N_nodes; c++) {
      if (unfriendly(c)) {
        continue;
      }
      if (isAcastle(c)) {  // Do not move infantry from a castle here.
        continue;
      }
      if (IdontOwn(c)) {  // A friend owns this
        if (occupationTable1[c][4] != ID) {  // I have no infantry here
          continue;
        }
        N_infantry = occupationTable1[c][5];
        int nearest = find1nearestBorderNodeFaceEnemy(c);
        next = visitNext[c][nearest];
        moveInfantry(N_infantry, c, next);
        continue;
      }
      N_infantry = occupationTable1[c][1];
      if (N_infantry == 0) {
        continue;
      }
      spreadInfantryFrom(c, N_infantry);
    }
  }

  private void spreadInfantryFrom(int C, int N_infantry) {
    int nearest2[];
    int i, neighbor;
    nearest2 = new int[2];
    //N_infantry = (int) (N_infantry - needed * 1.4);
    ArrayList<Integer> freebies = new ArrayList<Integer>();
    freebies = getFreebies1From(C);
    int N_freebies = freebies.size();
    if (N_freebies == 0) {   // no freebies
      int needed = calInfantryNeededForDefenceIn1At(C);
      N_infantry -= needed;
      if (N_infantry < 0) {
        // no surplus, I need all my infantry here for defence
        return;
      }
      nearest2 = find2nearestBorderNodesFaceEnemy(C);
      int N_infantry0 = (int) Math.ceil(N_infantry / 2.0);
      sendInfantry(N_infantry0, C, nearest2[0]);
      int N_infantry1 = N_infantry - N_infantry0;
      sendInfantry(N_infantry1, C, nearest2[1]);
      return;
    }
    if (N_infantry < N_freebies) {
      // not enough infantry for every neighbor
      int[] sortedFreebieList = new int[N_freebies];
      sortedFreebieList = sortNodeList(freebies);
      for (i = 0; i < N_infantry; i++) {
        neighbor = sortedFreebieList[i];
        move(1, "Infantry", C, neighbor);
        Ioccupy(neighbor);
      }
      occupationTable0[C][1] = 0;
      return;
    }
    // at least 1 infantry for every neighbor
    int portion = N_infantry / N_freebies;
    for (i = 0; i < N_freebies - 1; i++) {
      neighbor = freebies.get(i);
      move(portion, "Infantry", C, neighbor);
      Ioccupy(neighbor);
    }
    neighbor = freebies.get(N_freebies - 1);
    move("Infantry", C, neighbor);
    Ioccupy(neighbor);
    occupationTable0[C][1] = 0;
  }

  private void Ioccupy(int hostile) {
    occupationTable1[hostile][0] = ID;
    occupationTable1[hostile][1] = -1;
    occupationTable1[hostile][7] = myTeamID;
  }

  private void sendInfantry(int N_units, int from, int destination) {
    if (destination == -1) {
      return;
    }
    if (N_units == 0) {
      return;
    }
    int next = visitNext[from][destination];
    if (next == from) {
      return;
    }
    moveInfantry(N_units, from, next);
    return;
  }

  private void spreadKnight() {
    int N_knight;
    for (int c = 0; c < N_nodes; c++) {
      if (unfriendly(c)) {
        continue;
      }
      if (IdontOwn(c)) {  // A friend owns this
        if (occupationTable0[c][4] != ID) {  // I have no knight here
          continue;
        }
        N_knight = occupationTable0[c][6];
      } else {
        N_knight = occupationTable0[c][2];
      }
      if (N_knight == 0) {
        continue;
      }
      speadKnightFrom(c, N_knight);
    }
  }

  private void speadKnightFrom(int C, int N_knight) {
    // C does not belong to enemy
    int i, neighbor;
    ArrayList<Integer> freebies = new ArrayList<Integer>();
    freebies = getFreebies2From(C);
    int N_freebies = freebies.size();
    i = 0;
    while (N_freebies > 0 & N_knight > 0) {
      neighbor = freebies.get(i);
      i++;
      N_freebies--;
      move(1, "Knight", C, neighbor);
      N_knight--;
      occupationTable1[neighbor][0] = ID;
      occupationTable1[neighbor][2] = 1;
      occupationTable1[neighbor][7] = myTeamID;
      occupationTable1[C][8]--;
    }
    if (N_knight > 0) {
      advanceKnightFrom(C, N_knight);
    }
  }

  private int findBestImmediateBeatableHostileFrom(int C, int N_knight) {
    int c, needed, nodeWorth;
    int bestNodeWorth = -1;
    int suitable = -1;
    for (int i = 0; i < N_nodes - 1; i++) {
      c = nodesNear[C][i];
      if (friendly1(c)) {
        continue;
      }
      if (resistance(C, c)) {
        continue;
      }
      if (pathCost[C][c] > 2) {
        break;
      }
      needed = calKnightNeededAgainst(c);
      if (needed > N_knight) { // not enough knights
        continue;
      }
      if (isAcastle(c)) {
        return c;  // first priority, sack a castle
      }
      nodeWorth = nodeIncomeAt[c];
      if (nodeWorth > bestNodeWorth) {
        bestNodeWorth = nodeWorth;
        suitable = c;
      }
    }
    return suitable;
  }

  private int find1stBeatableHostileFrom(int C, int N_knight) {
    int c, needed;
    for (int i = 0; i < N_nodes - 1; i++) {
      c = nodesNear[C][i];
      if (friendly1(c)) {
        continue;
      }
      if (resistance(C, c)) {
        continue;
      }
      needed = calKnightNeededAgainst(c);
      if (N_knight >= needed) {
        return c;
      }
    }
    return -1;
  }

  private void advanceKnightFrom(int C, int N_knight) {
    int next, nearest, needed, hostile;
    needed = -1;
    hostile = -1;
    // Dispense knights to immediate beatable hostiles
    while (N_knight > 0) {
      hostile = findBestImmediateBeatableHostileFrom(C, N_knight);
      if (hostile == -1) {
        // no more easy target
        break;  // out of while loop
      }
      needed = calKnightNeededAgainst(hostile);
      moveKnight(needed, C, hostile);
      occupationTable0[C][2] -= needed;
      N_knight -= needed;
      Ioccupy(hostile);
    }
    // Dispense knights to other hostiles
    while (N_knight > 0) {
      hostile = find1stBeatableHostileFrom(C, N_knight);
      if (hostile == -1) {
        // no easy target
        break;  // out of while loop
      }
      needed = calKnightNeededAgainst(hostile);
      next = visitNext[C][hostile];
      if (next != hostile) {
        next = visitNext[next][hostile];
      }
      moveKnight(needed, C, next);
      occupationTable0[C][2] -= needed;
      N_knight -= needed;
      if (next == hostile) {
        Ioccupy(hostile);
      }
    }
    // Not enough knight left to attack a hostile. Add knights.
    if (isAcastle(C) & hostile != -1) {
      needed = calKnightNeededAgainst(hostile);
      buyKnightIn(C, needed);
      return;
    }
    // Not at a castle. Send leftover knights to the border
    nearest = find1nearestBorderNodeFaceEnemy(C);
    next = visitNext[C][nearest];
    if (next != nearest) {
      next = visitNext[next][nearest];
    }
    moveKnight(N_knight, C, next);
  }

  private void moveKnight(int N, int from, int to) {
    move(N, "Knight", from, to);
  }

  private boolean unfriendly(int c) {
    return !friendly(c);
  }

  private boolean unfriendly1(int c) {
    return !friendly1(c);
  }

  private boolean occupiedIn(int c) {
    return !unoccupiedIn(c);
  }

  private int getN_knightAt(int c) {
    return occupationTable0[c][2];
  }

  private int getN_infantryAt(int c) {
    return occupationTable0[c][1];
  }

  private String brainAt(int c) {
    int PID = occupationTable1[c][0];
    return Brain[PID];
  }

  private void getBrainAndPlayerName() {
    Brain = new String[N_players];
    PlayerName = new String[N_players];
    for (int i = 0; i < N_players; i++) {
      PlayerName[i] = world.getPlayer(i).getName();
      Brain[i] = world.getPlayer(i).getAgentType();
    }
  }

  private boolean IhaveKnightAt(int c) {
    if (Iown(c) & occupationTable1[c][2] > 0) {
      return true;
    }
    if (occupationTable1[c][4] == ID & occupationTable1[c][6] > 0) {
      return true;
    }
    return false;
  }

  private void make_NodesNear() {
    nodesNear = new int[N_nodes][N_nodes - 1];
    for (int C = 0; C < N_nodes; C++) {
      nodesNear[C] = getNodesNear(C);
    }
  }

  private int[] getNodesNear(int C) {
    int[] nodesNearC = new int[N_nodes - 1];
    int i = 0;
    int d = 0;
    while (i < N_nodes - 1) {
      d += 1;
      for (int c = 0; c < N_nodes; c++) {
        if (pathCost[C][c] == d) {
          nodesNearC[i] = c;
          i += 1;
        }
      }
    }
    return nodesNearC;
  }

  private int[] sortNodeList(ArrayList<Integer> nodeList) {
    int N = nodeList.size();
    int[] sortedList = new int[N];
    int[] threatBefore = new int[N];
    int[] threatAfter = new int[N];
    int threat;
    int i, j, k, c;
    for (i = 0; i < N; i++) {
      c = nodeList.get(i);
      threatBefore[i] = getAttackValue1Bagainst(c);
      threatAfter[i] = threatBefore[i];
    }
    java.util.Arrays.sort(threatAfter);
    boolean[] taken = new boolean[N];
    k = 0;
    for (i = 0; i < N; i++) {
      threat = threatAfter[i];
      for (j = 0; j < N; j++) {
        if (!taken[j] & threat == threatBefore[j]) {
          sortedList[k] = nodeList.get(j);
          taken[j] = true;
          k += 1;
          break;  // next i
        }
      }
    }
    return sortedList;
  }

  private boolean friendly1(int c1, int c2) {
    int team1 = getTeamIDAt(c1);
    int team2 = getTeamIDAt(c2);
    if (team1 == team2) {
      return true;
    }
    return false;
  }

  private int getTeamIDAt(int c) {
    return occupationTable0[c][7];
  }

  private void makeOccupationTable0() {
    int c, i;
    int N_cols = 10;
    Country ct;
    UnitStackGroup IKCs;
    UnitStack IKorC;
    Player player;
    String IKxorC;
    int N_IKCs, unitOwnerID, N_units, nodeOwnerID;
    occupationTable0 = new int[N_nodes][N_cols];
    for (c = 0; c < N_nodes; c++) {
      ct = Nodes[c];
      nodeOwnerID = ct.getOwner().getID();
      occupationTable0[c][0] = nodeOwnerID;               // 0
      IKCs = ct.getUnitStackGroup();
      N_IKCs = IKCs.size();
      for (int s = 0; s < N_IKCs; s++) {
        IKorC = IKCs.get(s);
        player = IKorC.getOwner();
        unitOwnerID = player.getID();
        N_units = IKorC.getCount();
        IKxorC = IKorC.getUnit().toString();
        if (nodeOwnerID == unitOwnerID) {
          if (IKxorC.equals("Infantry")) {
            occupationTable0[c][1] = N_units;           // 1
          }
          if (IKxorC.equals("Pawn")) {
            occupationTable0[c][1] = N_units;           // 1
          }
          if (IKxorC.equals("Knight")) {
            occupationTable0[c][2] = N_units;           // 2
            if (unitOwnerID == ID) {
              occupationTable0[c][8] = N_units;           // 8
            }
          }
          if (IKxorC.equals("Castle")) {
            occupationTable0[c][3] = 1;                 // 3
          }
        } else {
          occupationTable0[c][4] = unitOwnerID;         // 4
          if (IKxorC.equals("Infantry")) {
            occupationTable0[c][5] = N_units;            // 5
          }
          if (IKxorC.equals("Pawn")) {
            occupationTable0[c][5] = N_units;            // 5
          }
          if (IKxorC.equals("Knight")) {
            occupationTable0[c][6] = N_units;            // 6
          }
        }
      }                                          // 7 team ID
      occupationTable0[c][7] = playerAttributes[nodeOwnerID][1];
    }
  }

  private void initOccupationTable1() {
    int N_cols = occupationTable0[0].length;
    occupationTable1 = new int[N_nodes][N_cols];
    for (int c = 0; c < N_nodes; c++) {
      for (int i = 0; i < N_cols; i++) {
        occupationTable1[c][i] = occupationTable0[c][i];
      }
    }
  }

  private void calOccupationTable1() {
    int N_cols = 9;
    for (int c = 0; c < N_nodes; c++) {
      for (int i = 0; i < N_cols; i++) {
        // Copy the first 9 cloumns
        occupationTable1[c][i] = occupationTable0[c][i];
      }
      //  no. of my knights situated on c at the end of the last round
      //                                           at the begining of the new round
      occupationTable1[c][9] += occupationTable1[c][8] - occupationTable0[c][8];
      // Accumulate total no. of my knights killed on c
    }
  }

  private boolean unoccupiedIn(int c) {
    // no forces, no castle?
    int IKCs = occupationTable1[c][1] + occupationTable1[c][2]
       + occupationTable1[c][3]
       + occupationTable1[c][5] + occupationTable1[c][6];
    if (IKCs == 0) {
      return true;
    }
    return false;
  }

  public void gatherTo(int C, int... exceptions) {
    UnitStack unitForce;
    int next;
    for (int from = 0; from < N_nodes; from++) {
      if (in(from, exceptions)) {
        continue;
      }
      unitForce = getKnight(from);
      if (unitForce != null) {
        next = visitNext[from][C];
        next = visitNext[next][C];
        moveKnight(from, next);
      }
      unitForce = getInfantry(from);
      if (unitForce != null) {
        next = visitNext[from][C];
        moveInfantry(from, next);
      }
    }
  }

  public void gatherKnightTo(int c, int... exceptions) {
    UnitStack unitForce;
    int next;
    for (int from = 0; from < N_nodes; from++) {
      if (in(from, exceptions)) {
        continue;
      }
      unitForce = getKnight(from);
      if (unitForce != null) {
        next = visitNext[from][c];
        next = visitNext[next][c];
        moveKnight(from, next);
      }

    }
  }

  private boolean in(int k, int[] S) {
    for (int s : S) {
      if (k == s) {
        return true;
      }
    }
    return false;
  }

  private boolean Iown(int c) {
    if (occupationTable0[c][0] == ID) {
      return true;
    }
    return false;
  }

  private int getInfantryDefenceValueAt(int c) {
    int nInfantry = occupationTable0[c][1]
       + occupationTable0[c][5];
    return nInfantry * infantryDefenceValue;
  }

  private int getKnightDefenceValueAt(int c) {
    int nKnight = occupationTable0[c][2] + occupationTable0[c][6];
    int total = nKnight * knightDefenceValue;
    return total;
  }

  private void makeNodeNamesSorted() {
    nodeNames = new String[N_nodes];
    nodeNamesSorted = new String[N_nodes];
    for (int c = 0; c < N_nodes; c++) {
      Country ct = Nodes[c];
      int t1 = ct.getID();
      nodeNames[c] = ct.getName();
      nodeNamesSorted[c] = nodeNames[c] + ' ' + c;
    }
    java.util.Arrays.sort(nodeNamesSorted);
  }

  private int getMyInfantryAttackValueFrom(int c) {
    int nInfantry, total;
    int cOwnerID = occupationTable0[c][0];
    if (friendly(c)) {
      return 0;
    }
    if (cOwnerID == ID) {
      nInfantry = occupationTable0[c][1];
      total = nInfantry * infantryAttackValue;
      return total;
    }
    nInfantry = occupationTable0[c][5];
    total = nInfantry * infantryAttackValue;
    return total;
  }

  private int getMyKnightAttackValueAt(int C) {
    int N_knight, total = 0;
    if (occupationTable1[C][0] == ID) {
      N_knight = occupationTable0[C][2];
      total = N_knight * knightAttackValue;
    }
    if (occupationTable1[C][4] == ID) {
      N_knight = occupationTable0[C][6];
      total = N_knight * knightAttackValue;
    }
    return total;
  }

  private int getMyAttackValueFrom(int c) {
    int total = getMyInfantryAttackValueFrom(c);
    total += getMyKnightAttackValueAt(c);
    return total;
  }

  private int getKnightAttackValueFrom(int c) {
    int nKnight = occupationTable0[c][2] + occupationTable0[c][6];
    int total = nKnight * knightAttackValue;
    return total;
  }

  private void buyCastleFor(int c) {
    Unit castle = new UnitCastle(world.getPlayer(ID));
    UnitStack castle2 = new UnitStack(castle, 1);
    world.placeUnits(castle2, Nodes[c]);
  }

  private void move(String KnightOrInfantry, int from, int to) {
    move(infinity, KnightOrInfantry, from, to);
  }

  private void moveInto(int c) {
    moveInfantriesInto(c);
    moveKnightInto(c);
  }

  private void move(int from, int to) {
    move("Infantry", from, to);
    move("Knight", from, to);
  }

  private void moveInfantry(int from, int to) {
    move("Infantry", from, to);
  }

  private void moveInfantriesInto(int c) {
    int n = adjacencyList[c].length;
    for (int i = 0; i < n; i++) {
      int neighbor = adjacencyList[c][i];
      move("Infantry", neighbor, c);
    }
  }

  private void moveKnight(int from, int to) {
    move("Knight", from, to);
  }

  private void moveKnightInto(int C) {
    for (int c = 0; c < N_nodes; c++) {
      if (IdontOwn(c)) {
        continue;
      }
      if (pathCost[c][C] < 3) {
        move("Knight", c, C);
      }
    }
  }

  private UnitStack getKnightOrInfantry(int c, int movement) {
    Country ct = Nodes[c];
    // Castle, Knight, and Infantry units
    UnitStackGroup CKIs = ct.getUnitStackGroup();
    int N_usg = CKIs.size();
    for (int s = 0; s
       < N_usg; s++) {
      UnitStack CKorI = CKIs.get(s);
      int ID2 = CKorI.getOwner().getID();
      if (ID2 != ID) {  // does not belong to me
        continue;
      }
      int m = CKorI.getUnit().getMovement();
      if (m == movement) {
        return CKorI;
      }
    }
    return null;
  }

  private UnitStack getKnight(int c) {
    return getKnightOrInfantry(c, 2);
  }

  private UnitStack getInfantry(int c) {
    return getKnightOrInfantry(c, 1);
  }

  private boolean enemyForceAt(int c) {
    // If not enemy territory, no enemy force
    if (friendly(c)) {
      return false;
    }
    int total = occupationTable0[c][1] + occupationTable0[c][2]
       + occupationTable0[c][3]
       + occupationTable0[c][5] + occupationTable0[c][6];

    if (total == 0) {
      return false;
    }
    return true;
  }

  private boolean castleIsMine(int c) {
    if (occupationTable0[c][3] == 1 & occupationTable0[c][0] == ID) {
      return true;
    }
    return false;
  }

  private boolean friendly1(int c) {
    if (myTeamOwns1(c)) {
      return true;
    }
    return false;
  }

  private boolean friendly(int c) {
    if (myTeamOwns(c)) {
      return true;
    }
    return false;
  }

  private boolean myTeamOwns1(int c) {
    if (occupationTable1[c][7] == playerAttributes[ID][1]) {
      return true;
    }
    return false;
  }

  private boolean myTeamOwns(int c) {
    if (occupationTable0[c][7] == playerAttributes[ID][1]) {
      return true;
    }
    return false;
  }

  private boolean friendly(int c1, int c2) {
    if (occupationTable0[c1][7] == occupationTable0[c2][7]) {
      return true;
    }
    return false;
  }

  private void buildShortestPathArray() {
    int i, j, k, c, n, visitThru[][], shortestPath[][][];
    Country ct, ct2;
    List adjoiningList;
    edgeCost = new int[N_nodes][N_nodes];
    for (i = 0; i < N_nodes; i++) {
      for (j = 0; j < N_nodes; j++) {
        edgeCost[i][j] = infinity;
      }
      edgeCost[i][i] = 0;
    }
    adjacencyList = new int[N_nodes][];
    for (c = 0; c < N_nodes; c++) {
      ct = Nodes[c];
      adjoiningList = ct.getAdjoiningList();
      n = adjoiningList.size();
      adjacencyList[c] = new int[n];
      for (j = 0; j < n; j++) {
        ct2 = (Country) adjoiningList.get(j);
        k = ct2.getID();
        edgeCost[c][k] = 1;
        adjacencyList[c][j] = k;
        stop = 0;
      }
    }
    pathCost = new int[N_nodes][N_nodes];
    visitThru = new int[N_nodes][N_nodes];
    for (i = 0; i < N_nodes; i++) {
      for (j = 0; j < N_nodes; j++) {
        pathCost[i][j] = edgeCost[i][j];
        visitThru[i][j] = -1;
      }
    }
    for (k = 0; k < N_nodes; k++) {
      for (i = 0; i < N_nodes; i++) {
        for (j = 0; j < N_nodes; j++) {
          if (pathCost[i][k] + pathCost[k][j] < pathCost[i][j]) {
            pathCost[i][j] = pathCost[i][k] + pathCost[k][j];
            visitThru[i][j] = k;
          }
        }
      }
    }
    shortestPath = new int[N_nodes][N_nodes][];
    visitNext = new int[N_nodes][N_nodes];
    for (i = 0; i < N_nodes; i++) {
      for (j = 0; j < N_nodes; j++) {
        if (i == j) {
          visitNext[i][j] = i;
        } else if (edgeCost[i][j] == 1) {
          visitNext[i][j] = j;
        } else {
          String path = shortestPath(i, j, visitThru);
          path = path.replaceAll("^\\s+", "");  // Trim the leading blanks.
          int i2 = path.indexOf(" ");
          String nextS = path.substring(0, i2);
          int next = Integer.valueOf(nextS);
          visitNext[i][j] = next;
          path = path.substring(i2);
          n = pathCost[i][j] - 1;
          shortestPath[i][j] = new int[n];
          shortestPath[i][j][0] = next;
          for (k = 1; k < n; k++) {
            path = path.replaceAll("^\\s+", "");
            i2 = path.indexOf(" ");
            nextS = path.substring(0, i2);
            next = Integer.valueOf(nextS);
            shortestPath[i][j][k] = next;
            path = path.substring(i2);
          }
        }
      }
    }
    visitFrequency = new int[N_nodes];
    for (i = 0; i < N_nodes; i++) {
      for (j = 0; j < i; j++) {
        k = visitNext[i][j];
        visitFrequency[k] += 1;
      }
    }
  }

  private String shortestPath(int i, int j, int[][] visitThru) {
    int k;
    k = visitThru[i][j];
    if (k == -1) {
      return " ";
    }
    return shortestPath(i, k, visitThru) + " " + k + " " + shortestPath(k, j,
       visitThru);
  }

  public String name() {
    return "Kodos";
  }

  public float version() {
    return 1.0f;
  }

  public String description() {
    return "Kodos";
  }

  public String youWon() {
	String[] answers = new String[] {
	"We have loved the stars too fondly to be fearful of the night.",
	"Mortal as I am, I know that I am born for a day. \nBut when I follow at my pleasure the serried multitude of the stars in their circular course, my feet no longer touch the earth.\n          - Ptolemy",
	"For I dipped into the Future, far as the human eye could see; saw the vision of the world, and all the wonder that would be.\n- Alfred, Lord Tennyson",
	"Magnificent Desolation",
	"Another day, another ball of fire rising in the sky",
	"We live in a changing universe, and few things are changing faster than our conception of it.\n          - Timothy Ferris",
	"No known roof is as beautiful as the skies above.\n          - Michael O'Muircheartaigh ",
	"A star shines on the hour of our meeting.",
	"The meek shall inherit the Earth. \nThe rest of us will go to the stars.",
	"Chances are, when we meet intelligent life forms in outer space, they're going to be descended from predators.\n          - Michio Kaku",
	"The sign of our extinction would be no more than a match flaring for a second in the heavens.\n          - Stanley Kubrick",
	"The earth is simply too small and fragile a basket for the human race to keep all its eggs in.\n          - Arthur C. Clarke",
	"Setting loose on the battlefield weapons that are able to learn may be one of the biggest mistakes mankind has ever made. It could also be one of the last.\n          - Richard Forsyth",
	"The only way of discovering the limits of the possible is to venture a little way past them into the impossible.\n          - Arthur C. Clarke",
	"Any sufficiently advanced technology is indistinguishable from magic.\n          - Arthur C. Clarke",
	"I wonder why. \nI wonder why. \nI wonder why I wonder. \nI wonder why I wonder why I wonder.\n          - Richard Feynman",
	"Man is now able to soar into outer space and reach up to the moon; but he is not moral enough to live at peace with his neighbor!\n          - Sri Sathya Sai Baba",
	"Language is a virus from outer space.\n          - William S. Burroughs",
	"When once you have tasted flight, you will forever walk the earth with your  Eyes Turned Skyward, for there you have been, and there you will always long to return.",
	"One small step for man; \none giant leap for mankind. \n          - Neil Armstrong",
	"The universe is one giant, hackable computer. \n          - Seth Lloyd"
	};

	return answers[ rand.nextInt(answers.length) ];
  }

  private void move(int N_units, String KnightOrInfantry, int from, int to) {
    // The only world.moveUnit statment is in here.
    UnitStack unitForce;
    if (from == to) {
      return;
    }
    if (to == -1) {
      return;
    }
    Country ct = Nodes[from];
    if (KnightOrInfantry.equals("Knight")) {
      unitForce = getKnight(from);
      if (from == -1 | to == -1) {
        stop = 0;
      }
      if (pathCost[from][to] == 2) {
        int via = visitNext[from][to];
        if (enemyForceAt(via)) {
          to = via;
        }
      }
    } else {   // move infantry
      unitForce = getInfantry(from);
    }
    if (unitForce == null) {
      return;
    }
    int ID2 = unitForce.getOwner().getID();
    if (ID2 != ID) {  // security check for proper ID
      return;
    }
    if (N_units == infinity) {
      N_units = unitForce.getCount();
    }
    world.moveUnit(unitForce, ct, Nodes[to], N_units);
    if (KnightOrInfantry.equals("Knight")) {
      occupationTable1[from][8] -= N_units;
      occupationTable1[to][8] += N_units;
    }
  }

  private void buyKnightIn(int C, int N_units) {
    // The only buyKnights statments are in here.
    if (N_units == 0) {
      return;
    }
    int m = N_units * knightCost;
    buyKnights(m, Nodes[C]);
    occupationTable1[C][8] += N_units;
  }

  private void buyPawnsIn(int c, int units) {
    if (units == 0) {
      return;
    }
    int m = units * infantryCost;
    int Money = world.getPlayerMoney(ID);
    if (Money >= m) {
      buyPawns(m, Nodes[c]);
    } else {
      buyPawns(Money, Nodes[c]);
    }
  }
}

