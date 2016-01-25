package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;
import java.util.*;
import com.sillysoft.vox.unit.*;
import java.util.ArrayList;
import java.util.Iterator;
/* Authour: Tony Y. T. Chan
 * Date: 2010 5 4
 */

public class AI3 extends VoxAgentBase implements VoxAgent {

  //static String share;
  private Random rand = new Random();
  private int infantryAttackValue = 1, knightAttackValue = 5;
  private int infantryDefenceValue = 3, knightDefenceValue = 2;
  private int infantryCost = 3, knightCost = 5;
  private Country Nodes[];
  private String brain[], nodeNames[], nodeNamesSorted[];
  private int Home0, Home1, Home2, myTeamID;
  private int nodeBonusAt[];
  private int playerAttributes[][];
  private int friendsID;
  private int Round = 0;
  private int N_nodes, N_players;
  private String whoAmI;
  private int stop;
  private int occupationTable0[][], occupationTable1[][];
  private int edgeCost[][], pathCost[][], visitNext[][], visitFrequency[],
     adjacencyList[][], shortestPath[][][];
  private int infinity = 99999;
  private int money;
  private boolean myBorder[];
  private int nodesNear[][];

  public void declareMoves(Country[] countries) {
    Nodes = countries;
    money = world.getPlayerMoney(ID);
    Round += 1;
    whoAmI = world.getPlayer(ID).name();
    if (Round == 1) {
      Round1Init();
      myBorder = getMyBorder();
      spreadInfantry();
      buyKnightIn(Home0);
      return;
    }
    makeOccupationTables();
    myBorder = getMyBorder();
    if (Round == 2) {
      round2attack();
      spreadInfantry();
      buyKnightIn(Home0);
      return;
    }
    if (Round == 3) {
      reinforceHomes();
      spreadInfantryDefensively();
      advanceKnight();
      buyKnightIn(Home0);
      return;
    }
    if (Round > 3) {
      reinforceHomes();
      advanceKnight();
      spreadInfantryDefensively();
      buyKnightIn(Home0, Round);
      return;
    }
  }
private boolean isAcastle(int C){
  if (occupationTable0[C][3]==1) return true;
  return false;
}
  private void spreadInfantryDefensivelyFrom(int C) {
    // Friendly node C
    int N_infantry, nearest, i, neighbor, next;
    if (isAcastle(C)) {
      return;
    }
    if (IdontOwn(C)) {  // A friend owns this
      if (occupationTable1[C][4] != ID) {  // I have no forces here
        return;
      }
      N_infantry = occupationTable1[C][5];
      stop = 0;
      return;
    }
    // my infantry
    N_infantry = occupationTable1[C][1];
    if (N_infantry == 0) {
      return;
    }
    int needed = calInfantryNeededIn1For(C);
    int diff = N_infantry - needed;
    if (diff < 0) {  // I need all my infantry here for defence
      return;
    }
    N_infantry = (int) (N_infantry - needed * 1.4);
    ArrayList<Integer> freebieNeighbors = new ArrayList<Integer>();
    freebieNeighbors = getFreebies1From(C);
    int N_neighbors = freebieNeighbors.size();
    if (N_neighbors == 0) {   // no freebies
      ArrayList<Integer> hostilesNeighbors = new ArrayList<Integer>();
      hostilesNeighbors = getHostiles1From(C);
      N_neighbors = hostilesNeighbors.size();
      if (N_neighbors == 0) {   // no hostiles neighbors
        // dash to the closest unfriedly node
        nearest = getNextUnfriendlyNodeFrom(C);
        next = visitNext[C][nearest];
        moveInfantry(N_infantry, C, next);
        return;
      }
      // hostile neighbors
      int hostile = hostilesNeighbors.get(0);
      float ADratio = getMyADratioAgainst(hostile);
      if (ADratio > 1.3) {
        moveInfantry(C, hostile);
        occupationTable0[C][1] = 0;
        occupationTable1[hostile][0] = ID;
        occupationTable1[hostile][1] = -1;
        occupationTable1[hostile][7] = myTeamID;
      }
      return;
    }
    // not enough infantry for every neighbor
    if (N_infantry < N_neighbors) {
      int[] sortedFreebieList = new int[N_neighbors];
      sortedFreebieList = sortNodeList(freebieNeighbors);
      for (i = 0; i < N_infantry; i++) {
        neighbor = sortedFreebieList[i];
        move(1, "Infantry", C, neighbor);
        occupationTable1[neighbor][0] = ID;
        occupationTable1[neighbor][1] = 1;
        occupationTable1[neighbor][7] = myTeamID;
      }
      occupationTable0[C][1] = 0;
      return;
    }
    // at least 1 infantry for every neighbor
    int portion = N_infantry / N_neighbors;
    for (i = 0; i < N_neighbors - 1; i++) {
      neighbor = freebieNeighbors.get(i);
      move(portion, "Infantry", C, neighbor);
      occupationTable1[neighbor][0] = ID;
      occupationTable1[neighbor][1] = portion;
      occupationTable1[neighbor][7] = myTeamID;
    }
    neighbor = freebieNeighbors.get(N_neighbors - 1);
    move("Infantry", C, neighbor);
    occupationTable1[neighbor][0] = ID;
    occupationTable1[neighbor][1] = portion;
    occupationTable1[neighbor][7] = myTeamID;
    occupationTable0[C][1] = 0;
  }

  private void buyKnightIn(int c, int units) {
    int m = units * knightCost;
    if (money >= m) {
      buyKnight(m, c);
    } else {
      buyKnightIn(c);
    }
  }

  private void buyKnight(int mon, int C) {
    buyKnights(mon, Nodes[C]);
  }

  private void midgame() {
    int c;
    for (c = 0; c < N_nodes; c++) {
      if (IdontOwn(c)) {
        continue;
      }
      if (unoccupiedIn(c)) {
        continue;
      }
      if (myKnightIn(c)) {
        // stop
      }
      if (myInfantryIn(c)) {
        spreadInfantryFrom(c);
      }
    }
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

  private int getNextUnfriendlyNodeFrom(int C) {
    int c;
    for (int i = 0; i < N_nodes - 1; i++) {
      c = nodesNear[C][i];
      if (unfriendly(c)) {
        return c;
      }
    }
    return -1;
  }

  private void spreadInfantryFrom(int C) {
    // Friendly node C
    int N_infantry, nearest, i, neighbor, next;
    if (IdontOwn(C)) {  // A friend owns this
      if (occupationTable1[C][4] != ID) {  // I have no forces here
        return;
      }
      N_infantry = occupationTable1[C][5];
    } else {
      N_infantry = occupationTable1[C][1];
    }
    if (N_infantry == 0) {
      return;
    }
    ArrayList<Integer> freebieNeighbors = new ArrayList<Integer>();
    freebieNeighbors = getFreebies1From(C);
    int N_neighbors = freebieNeighbors.size();
    if (N_neighbors == 0) {   // no freebies
      ArrayList<Integer> hostilesNeighbors = new ArrayList<Integer>();
      hostilesNeighbors = getHostiles1From(C);
      N_neighbors = hostilesNeighbors.size();
      if (N_neighbors == 0) {   // no hostiles neighbors
        // dash to the closest unfriedly node
        nearest = getNextUnfriendlyNodeFrom(C);
        next = visitNext[C][nearest];
        moveInfantry(C, next);
        return;
      }
      // hostile neighbors
      int hostile = hostilesNeighbors.get(0);
      int defence = getDefenceValueAt(hostile);
      int myAttack = N_infantry * infantryAttackValue;
      if (myAttack > .8 * defence) {
        moveInfantry(C, hostile);
        occupationTable0[C][1] = 0;
        occupationTable1[hostile][0] = ID;
        occupationTable1[hostile][1] = -1;
        occupationTable1[hostile][7] = myTeamID;
      }
      return;
    }
    // not enough infantry for every neighbor
    if (N_infantry < N_neighbors) {
      int[] sortedFreebieList = new int[N_neighbors];
      sortedFreebieList = sortNodeList(freebieNeighbors);
      for (i = 0; i < N_infantry; i++) {
        neighbor = sortedFreebieList[i];
        move(1, "Infantry", C, neighbor);
        occupationTable1[neighbor][0] = ID;
        occupationTable1[neighbor][1] = 1;
        occupationTable1[neighbor][7] = myTeamID;
      }
      occupationTable0[C][1] = 0;
      return;
    }
    // at least 1 infantry for every neighbor
    int portion = N_infantry / N_neighbors;
    for (i = 0; i < N_neighbors - 1; i++) {
      neighbor = freebieNeighbors.get(i);
      move(portion, "Infantry", C, neighbor);
      occupationTable1[neighbor][0] = ID;
      occupationTable1[neighbor][1] = portion;
      occupationTable1[neighbor][7] = myTeamID;
    }
    neighbor = freebieNeighbors.get(N_neighbors - 1);
    move("Infantry", C, neighbor);
    occupationTable1[neighbor][0] = ID;
    occupationTable1[neighbor][1] = portion;
    occupationTable1[neighbor][7] = myTeamID;
    occupationTable0[C][1] = 0;
  }

  private float getMyADratioAgainst(int C) {
    int attack = getMyAttackValueAgainst(C);
    if (attack == 0) {
      return 0;
    }
    int defence = getDefenceValueAt(C);
    if (defence == 0) {
      return infinity;
    }
    float ADratio = attack / (float) defence;
    return ADratio;
  }

  private int getMyNearestBorderFrom(int C) {
    int d, min, nearest;
    min = infinity;
    nearest = 0;
    for (int c = 0; c < N_nodes; c++) {
      if (!myBorder[c]) {
        continue;
      }
      d = pathCost[C][c];
      if (d < min) {
        min = d;
        nearest = c;
      }
    }
    return nearest;
  }

  private int getNearestUnfriendlyBorderFrom(int C) {
    int d, min, nearest;
    min = infinity;
    nearest = -1;
    for (int c = 0; c < N_nodes; c++) {
      if (!myBorder[c]) {
        continue;
      }
      if (!neighborIsUnfriendlyFrom(c)) {
        continue;
      }
      d = pathCost[C][c];
      if (d < min) {
        min = d;
        nearest = c;
      }
    }
    return nearest;
  }

  private boolean neighborIsUnfriendlyFrom(int C) {
    int N_neighbors = adjacencyList[C].length;
    for (int i = 0; i < N_neighbors; i++) {
      int neighbor = adjacencyList[C][i];
      if (unfriendly(neighbor)) {
        return true;
      }
    }
    return false;
  }

  private int getMyAttackValueAgainst(int a) {
    int total = 0;
    for (int c = 0; c < N_nodes; c++) {
      if (unfriendly(c)) {
        continue;
      }
      if (IdontOwn(c)) {
        if (occupationTable1[c][4] != ID) {
          continue;
        }
        continue;
      }
      if (pathCost[c][a] > 2) {
        continue;
      }
      if (pathCost[c][a] == 2) {
        total += getKnightAttackValueFrom(c);
        continue;
      }
      total += getHostileAttackValueFrom(c);
    }
    return total;
  }

  private ArrayList<String> getMyBorderList() {
    ArrayList<String> border = new ArrayList<String>();
    String node;
    for (int c = 0; c < N_nodes; c++) {
      if (myBorder[c]) {
        node = c + " " + nodeNames[c];
        border.add(node);
      }
    }
    return border;
  }

  private boolean[] getMyBorder() {
    int N, i, neighbor;
    boolean[] border = new boolean[N_nodes];
    for (int c = 0; c < N_nodes; c++) {
      if (IdontOwn(c)) {
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

  private void reinforceHomes() {
    reinforceHome0();
    for (int c = 0; c < N_nodes; c++) {
      if (c==Home0)continue;
      if (castleIsNotMine(c)) {
        continue;
      }
      reinforceCastle(c);
    }
  }

  private void reinforceCastle(int C) {

    int attack = getAttackValueIn2Against(C);
    int myDefence = getInfantryDefenceValueAt(C);
    float diff = attack - myDefence;
    if (diff > 0) {
      int needed = (int) (1.3 * Math.abs(diff) / infantryDefenceValue);
      buyPawnsIn(C, needed);
      return;
    }
    if (attack == 0) {
      int neighbor = adjacencyList[C][0];
    // move infantry out
      moveInfantry(C, neighbor);
    }
  }

  private int getN_forceUnits0At(int C) {
    return occupationTable0[C][1] + occupationTable0[C][2];
  }

  private boolean castleIsNotMine(int C) {
    if (castleIsMine(C)) {
      return false;
    }
    return true;
  }

  private void reinforceHome0() {
    int attack = getAttackValueIn2Against(Home0);
    int defence = getInfantryDefenceValueAt(Home0);
    float diff = attack - defence;
    if (diff > 0) {
      buyPawnsIn(Home0, (int) (diff * 1.3));
      return;
    }
    double diff2 = diff * -0.7;
    int N_infantry = (int) (diff2 / infantryDefenceValue);
    moveInfantry(N_infantry, Home0, Home1);
  }

  private void moveInfantry(int N, int from, int to) {
    move(N, "Infantry", from, to);
  }

  private int calInfantryNeededIn1For(int c) {
    int attack = getAttackValueIn1AgainstMy(c);
    int defence = getInfantryDefenceValueAt(c);
    int defenceNeed = attack - defence;
    if (defenceNeed <= 0) {
      return 0;
    }
    return defenceNeed / infantryDefenceValue + 5;
  }

  private int getAttackValueIn1AgainstMy(int C) {
    // threat against Node C right now
    int i = 0, near, distance = 0;
    int total = 0;
    String botName;
    while (distance < 3) {
      near = nodesNear[C][i];
      botName = brainAt(near);
      if (friendly(C, near)) {  // not hostile
        i += 1;
        continue;
      }
      if (botName.equals("Boring")) {
        i += 1;
        continue;
      }
      distance = pathCost[C][near];
      if (distance < 3) {
        total += getHostileAttackValueFrom(near);
        i += 1;
        continue;
      }
    }
    return total;
  }

  private int calInfantryNeededIn2For(int c) {
    int attack = getAttackValueIn2Against(c);
    int defence = getInfantryDefenceValueAt(c);
    int defenceNeed = attack - defence;
    if (defenceNeed <= 0) {
      return 0;
    }
    return defenceNeed / infantryDefenceValue + 5;
  }

  private int getAttackValueIn2Against(int C) {
    // threat against Node C in the round following.
    int i = 0, near, distance = 0;
    int total = 0;
    while (distance < 5) {
      near = nodesNear[C][i];
      String botName = brainAt(near);
      if (botName.equals("Boring")) {
        i += 1;
        continue;
      }
      if (friendly(C, near)) {  // not hostile
        i += 1;
        continue;
      }
      distance = pathCost[C][near];
      if (distance < 3) {
        total += getHostileAttackValueFrom(near);
        i += 1;
        continue;
      }
      if (distance < 5) {
        total += getKnightAttackValueFrom(near);
        i += 1;
        continue;
      }
    }
    return total;
  }

  private float getADratioAtMy(int c) {
    int attack = getAttackValue1againstMy(c);
    if (attack == 0) {
      return 0;
    }
    float defence = getDefenceValueAtMy(c);
    return attack / defence;
  }

  private int getDefenceValueAtMy(int c) {
    return getDefenceValueAt(c);
  }

  private int getAttackValue1againstMy(int c) {
    // threat against Node c in the immediate coming round.
    int between;
    String botName;

    int total = 0;
    for (int n = 0; n < N_nodes; n++) {  // find neighbors
      if (pathCost[n][c] > 2) {
        continue;
      }
      if (friendly(n)) {
        continue;
      }
      botName = brainAt(n);
      if (botName.equals("Boring")) {
        continue;
      }
      if (pathCost[n][c] == 1) { // near c
        total += getHostileAttackValueFrom(n);
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

  private float getADratioAt(int c) {
    int attack = getAttackValue1Bagainst(c);
    if (attack == 0) {
      return 0;
    }
    float defence = getDefenceValueAt(c);
    return attack / defence;
  }

  private int getDefenceValueAt(int c) {
    int nInfantry = occupationTable0[c][1] + occupationTable0[c][5];
    int nKnight = occupationTable0[c][2] + occupationTable0[c][6];
    int total = nInfantry * infantryDefenceValue + nKnight * knightDefenceValue;
    return total;
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
        total += getHostileAttackValueFrom(n);
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
    return 3 + defence / knightAttackValue;
  }

  private void advanceKnightFrom(int C) {
    // Friendly node C
    int N_knight, nearest, i, neighbor, next;
    if (IdontOwn(C)) {  // A friend owns this
      if (occupationTable1[C][4] != ID) {  // I have no forces here
        return;
      }
      N_knight = occupationTable1[C][6];
    } else {
      N_knight = occupationTable1[C][2];
    }
    if (N_knight == 0) {
      return;
    }
    ArrayList<Integer> freebieNeighbors = new ArrayList<Integer>();
    freebieNeighbors = getFreebies2From(C);
    int N_neighbors = freebieNeighbors.size();
    if (N_neighbors == 0) {   // no freebies
      while (N_knight > 0) {
        int hostile = getNextUnfriendlyNodeFrom(C);
        int needed = calKnightNeededAgainst(hostile);
        if (N_knight >= needed) {
          next = visitNext[C][hostile];
          if (next != hostile) {
            next = visitNext[next][hostile];
          }
          moveKnight(N_knight, C, next);
          N_knight -= needed;
          if (next == hostile) {
            occupationTable1[hostile][0] = ID;
            occupationTable1[hostile][1] = -1;
            occupationTable1[hostile][7] = myTeamID;
          }
        }
        float bonus = Nodes[hostile].getContinentBonusPartial(world);
        if (bonus >= 3) {  // hostile is a goldmine
          float ADratio = getMyADratioAgainst(hostile);
          if (ADratio > 1.3) {
            moveKnightInto(hostile);
            return;
          }
        }
        // not enough knight to attack a hostile, sent them to the border
        nearest = getMyNearestBorderFrom(C);
        next = visitNext[C][nearest];
        if (next != nearest) {
          next = visitNext[next][nearest];
        }
        moveKnight(N_knight, C, next);
        return;
      }
    }
    // freebies: 1 knight for every neighbor
    for (i = 0; i < N_neighbors; i++) {
      neighbor = freebieNeighbors.get(i);
      move(1, "Knight", C, neighbor);
      occupationTable1[neighbor][0] = ID;
      occupationTable1[neighbor][1] = 1;
      occupationTable1[neighbor][7] = myTeamID;
    }
  }

  private void moveKnight(int N, int from, int to) {
    move(N, "Knight", from, to);
  }

  private int get1stUnfriendly2from(int C) {
    for (int c = 0; c < N_nodes; c++) {
      if (pathCost[C][c] > 2) {
        continue;
      }
      if (friendly(c)) {
        continue;
      }
      return c;
    }
    return -1;
  }

  private ArrayList getFreebies2From(int f) {
    ArrayList list = new ArrayList();

    for (int c = 0; c < N_nodes; c++) {
      if (pathCost[f][c] > 2) {
        continue;
      }
      if (occupiedIn(c)) {
        continue;
      }
      if (friendly(c)) {
        continue;
      }
      list.add(new Integer(c));
    }
    return list;
  }

  private boolean unfriendly(int c) {
    return !friendly(c);
  }

  private boolean occupiedIn(int c) {
    return !unoccupiedIn(c);
  }

  private int getN_knightAt(int c) {
    return occupationTable0[c][2];
  }

  private String brainAt(int c) {
    int PID = occupationTable1[c][0];
    return brain[PID];
  }

  private void Round1Init() {
    N_nodes = Nodes.length;
    makeNodeNamesSorted();
    makeNodeBonusAt();
    N_players = world.getNumberOfPlayers();
    makePlayerAttributes();
    makeOccupationTables();
    getBrain();
    buildShortestPathArray();
    getHome0();
    makeNodesNear();
    getHome12();
    //boolean[] myBorder = new boolean[N_nodes];
  }

  private void makePlayerAttributes() {
    int c, ownerID, N_attributes = 2;
    boolean castle;
    playerAttributes = new int[N_players][N_attributes];
    for (c = 0; c < N_nodes; c++) {
      castle = Nodes[c].hasCastle();
      if (!castle) {
        continue;
      }
      ownerID = Nodes[c].getOwner().getID();
      playerAttributes[ownerID][0] = c;   // 0: home castle of this player
    }

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
    myTeamID = playerAttributes[ID][1];
  }

  private void getBrain() {
    brain = new String[N_players];
    for (int i = 0; i < N_players; i++) {
      String brainAt = world.getPlayer(i).getAgentType();
      brain[i] = brainAt;
    }
  }

  private void getHome0() {
    int c;
    for (c = 0; c < N_nodes; c++) {
      if (occupationTable0[c][0] == ID & occupationTable0[c][3] == 1) {
        Home0 = c;
        return;
      }
    }
  }

  private int gethostileID() {
    int i, home_i;
    int[] castleDistances = new int[N_players];
    for (i = 0; i < N_players; i++) {
      home_i = playerAttributes[i][0];
      if (home_i == -1) {
        castleDistances[i] = infinity;
      } else {
        castleDistances[i] = pathCost[Home0][home_i];
      }
    }
    int d = 1;
    while (true) {
      for (i = 0; i < N_players; i++) {
        if (d == castleDistances[i]) {
          if (i != friendsID) {
            return i;
          }
        }
        d += 1;
      }
    }
  }

  private void spreadInfantry() {
    for (int c = 0; c < N_nodes; c++) {
      if (unfriendly(c)) {
        continue;
      }
      spreadInfantryFrom(c);
    }
  }

  private void spreadInfantryDefensively() {
    for (int c = 0; c < N_nodes; c++) {
      if (unfriendly(c)) {
        continue;
      }
      spreadInfantryDefensivelyFrom(c);
    }
  }

  private void spreadForces() {
    spreadInfantry();
    spreadKnight();
  }

  private void spreadKnight() {
    for (int c = 0; c < N_nodes; c++) {
      if (unfriendly(c)) {
        continue;
      }
      advanceKnightFrom(c);
    }
  }

  private void advanceKnight() {
    for (int c = 0; c < N_nodes; c++) {
      if (Iown(c)) {
        advanceKnightFrom(c);
      }
    }
  }

  private void round2attack() {
    for (int i = 0; i < N_nodes - 1; i++) {
      int c = nodesNear[Home0][i];
      if (!friendly(c) & !unoccupiedIn(c)) {
        float bonus = Nodes[c].getContinentBonusPartial(world);
        if (bonus >= 3) {  // c is a goldmine
          moveKnightInto(c);
          return;
        }
      }
    }
  }

  private void makeNodeBonusAt() {
    nodeBonusAt = new int[N_nodes];
    for (int c = 0; c < N_nodes; c++) {
      int bonus = (int) Nodes[c].getContinentBonusPartial(world);
      //int b= Nodes[c].getBonus();
      nodeBonusAt[c] = bonus;
    }
  }

  private void getHome12() {
    int c;
    for (int i = 0; i < N_nodes - 1; i++) {
      c = nodesNear[Home0][i];
      if (occupationTable0[c][0] == ID) {
        if (Home1 == 0) {
          Home1 = c;
        } else {
          Home2 = c;
          return;
        }
      }
    }
  }

  private void makeNodesNear() {
    nodesNear = new int[N_nodes][N_nodes - 1];
    for (int c = 0; c < N_nodes; c++) {
      nodesNear[c] = getNodesNear(c);
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

  private ArrayList getUnfriendlyNoIKCs(int c) {
    ArrayList list = new ArrayList();
    int N_neighbors = adjacencyList[c].length;
    for (int i = 0; i < N_neighbors; i++) {
      int neighbor = adjacencyList[c][i];
      if (unoccupiedIn(neighbor) & !friendly1(c, neighbor)) {
        list.add(new Integer(neighbor));
      }
    }
    return list;
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
    return occupationTable1[c][7];
  }

  private void makeOccupationTables() {
    // inputs: N_nodes
    // outputs: occupationTable0, occupationTable1
    int c, i;
    int N_cols = 8;
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
          if (IKxorC.equals("Knight")) {
            occupationTable0[c][2] = N_units;           // 2
          }
          if (IKxorC.equals("Castle")) {
            occupationTable0[c][3] = 1;                 // 3
          }
        } else {
          occupationTable0[c][4] = unitOwnerID;         // 4
          if (IKxorC.equals("Infantry")) {
            occupationTable0[c][5] = N_units;            // 5
          }
          if (IKxorC.equals("Knight")) {
            occupationTable0[c][6] = N_units;            // 6
          }
        }
      }                                          // 7 team ID
      occupationTable0[c][7] = playerAttributes[nodeOwnerID][1];
    }
    occupationTable1 = new int[N_nodes][N_cols];
    for (c = 0; c < N_nodes; c++) {
      for (i = 0; i < N_cols; i++) {
        occupationTable1[c][i] = occupationTable0[c][i];
      }
    }
  }

  private boolean unoccupiedIn(int c) {
    int IKCs = occupationTable1[c][1] + occupationTable1[c][2]
       + occupationTable1[c][3] + occupationTable1[c][3]
       + occupationTable1[c][5] + occupationTable1[c][6];
    if (IKCs == 0) {
      return true;
    }
    return false;
  }

  public void gatherTo(int c, int... exceptions) {
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
      unitForce = getInfantry(from);
      if (unitForce != null) {
        next = visitNext[from][c];
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

  private void buyPawnsIn(int c, int units) {
    int m = units * infantryCost;
    if (money >= m) {
      buyPawns(m, Nodes[c]);
    } else {
      buyPawns(money, Nodes[c]);
    }
  }

  private void buyPawnsIn(int c) {
    buyPawns(money, Nodes[c]);
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

  private int getHostileAttackValueFrom(int c) {
    int nInfantry = occupationTable0[c][1] + occupationTable0[c][5];
    int nKnight = occupationTable0[c][2] + occupationTable0[c][6];
    int total = nInfantry * infantryAttackValue + nKnight * knightAttackValue;
    return total;
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

  private int getOwnerID(int c) {
    Player player = Nodes[c].getOwner();
    return player.getID();
  }

  private void buyKnightIn(int c) {
    buyKnights(money, Nodes[c]);
  }

  private void buyKnight() {
    buyKnights(money, Nodes[Home0]);
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

  private boolean friendly(int c) {
    if (myTeamOwns(c)) {
      return true;
    }
    return false;
  }

  private boolean myTeamOwns(int c) {
    if (occupationTable1[c][7] == playerAttributes[ID][1]) {
      return true;
    }
    return false;
  }

  private boolean friendly(int c1, int c2) {
    if (occupationTable1[c1][7] == occupationTable1[c2][7]) {
      return true;
    }
    return false;
  }

  private void buildShortestPathArray() {
    int i, j, k, c, n, visitThru[][];
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
      }
    }

    pathCost = new int[N_nodes][N_nodes];
    visitThru = new int[N_nodes][N_nodes];
    for (i = 0; i
       < N_nodes; i++) {
      for (j = 0; j
         < N_nodes; j++) {
        pathCost[i][j] = edgeCost[i][j];
        visitThru[i][j] = -1;
      }
    }

    for (k = 0; k
       < N_nodes; k++) {
      for (i = 0; i
         < N_nodes; i++) {
        for (j = 0; j
           < N_nodes; j++) {
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
          //shortestPath[i][j] = new int[1];
          //shortestPath[i][j][0] = i;
        } else if (edgeCost[i][j] == 1) {
          visitNext[i][j] = j;
          //shortestPath[i][j] = new int[1];
          //shortestPath[i][j][0] = j;
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
          for (k = 1; k
             < n; k++) {
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
    return "TonyAI";
  }

  public float version() {
    return 1.0f;
  }

  public String description() {
    return "AI bot.";
  }

  public String youWon() {
    return "AI won.";
  }

  private void move(int N_units, String KnightOrInfantry, int from, int to) {
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
    } else {
      unitForce = getInfantry(from);
    }
    if (unitForce == null) {
      return;
    }
    int ID2 = unitForce.getOwner().getID();
    if (ID2 != ID) {
      return;
    }
    if (N_units == infinity) {
      N_units = unitForce.getCount();
    }
    world.moveUnit(unitForce, ct, Nodes[to], N_units);

    if (from == 46 & to == 74) {
      stop = 0;
    }

  }
}


