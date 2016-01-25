package com.sillysoft.vox.agent;

import com.sillysoft.vox.*;
import java.util.*;
import com.sillysoft.vox.unit.*;
import java.util.ArrayList;
import java.util.Iterator;
/* Authour: Tony Y. T. Chan
 * Date: 2010 3 17
 *
 */

public class AI2 extends VoxAgentBase implements VoxAgent {

  private Random rand = new Random();
  private int infantryAttackValue = 1, knightAttackValue = 5;
  private int infantryDefenceValue = 3, knightDefenceValue = 2;
  private int infantryCost = 3;
  private int SitcaCastle = 88, CentralGoldmine = 59, SitcaSW = 102,
     SitcaBridgeS = 13, SitcaBridgeN = 56, SitcaNW = 67,
     SitcaGoldmine = 3;
  private int SartrusCastle = 57, SartrusSE = 101;
  private int ZonlorCastle = 46;
  private int LaceCastle = 12;
  private Country Countries[];
  private String CountryNames[], CountryNamesSorted[];
  private int N_teams = 4, ID2teamID[] = {0, 0, 1, 1, 2, 2, 3};
  private int friendID, myTeamID;
  private int humanID = -1, humanFriendlyID = -1, humanCastleI, humanFriendlyCastleI;
  private boolean humanPlaysNomads;
  private int Round = 0;
  private Team humanTeamName, myTeam; // name of the human team (human & bot)
  private int N_Countries, N_players, N_SitcaHomeCountrySet;
  private String whoAmI;
  private Player humanFriendlyPlayer, AIplayer;
  private int stop;
  private Team ID2Team[];
  private boolean IDisaTonyAI[];
  private int occupationTable[][];
  private boolean SitcaBridgeScastleIsMine, CentralGoldmineCastleIsMine;
  private int edgeCost[][], pathCost[][], visitNext[][],
     N_neighbors[], adjacencyList[][], shortestPath[][][];
  private int infinity = 99999;
  private int money;
  private int SitcaHome[] = {3, 97, 98, 87, 88, 89, 13, 84, 85, 54,
    71, 72, 73, 102, 69, 68, 67, 59};
  private ArrayList CountriesBelongTo[];

  public void declareMoves(Country[] countries) {
    Countries = countries;
    money = world.getPlayerMoney(ID);
    Round += 1;
    if (Round == 1) {
      Round1Init();
    }
    if (humanPlaysNomads) {
      wandering();
      return;
    }
    makeCountriesBelongTo();
    makeOccupationTable();
    switch (ID) {
      case 0:    // Geography: center
        //gatherTo(SitcaBridgeS);
        buyKnightIn(ZonlorCastle);
        //wandering();
        //playZonlor();
        break;
      case 1:        // Geography: top right
        //gatherTo(CentralGoldmine);
        //wandering();
        break;
      case 3:        // Geography: left
        //gatherTo(CentralGoldmine);
        //gatherTo(SitcaSW);
        buyKnightIn(LaceCastle);
        //wandering();
        break;
      case 4:        // Geography: bottom right
        playSitca();
        break;
      case 5:       // Geography: bottom left
        //wandering();
        //gatherTo(CentralGoldmine);
        buyKnightIn(SartrusCastle);
        //playSartrus();
        break;
      default:
      //wandering();
    }
  }

  private void playSitca() {
    String status;
    int n;
    if (Round == 1) {
      SitcaRound1();
      return;
    }
    if (Round == 2) {
      SitcaRound2();
      return;
    }
    if (Round == 3) {
      SitcaRound3();
      return;
    }
    if (Round == 4) {
      SitcaRound4();
      return;
    }
    // I own castle in SitcaNW.
     n = calInfantryNeededIn1(SitcaBridgeS);
    buyPawnsIn(SitcaNW, n);

    // Reinforce SitcaBridgeS
    // n = calInfantryNeeded(SitcaBridgeS);
    move(n, "Infantry", SitcaNW, SitcaBridgeS);
    //return;

    if (!friendly(CentralGoldmine)) {
      moveInto(CentralGoldmine);
      gatherTo(CentralGoldmine, SitcaBridgeS);
      return;
    }
     n = calInfantryNeededIn1(CentralGoldmine);
    buyPawnsIn(SitcaNW, n);

    // Reinforce  CentralGoldmine
    //n = calInfantryNeeded(CentralGoldmine);
    move(n, "Infantry", SitcaNW, CentralGoldmine);

    // Reinforce SitcaSW
    n = calInfantryNeededIn1(SitcaSW);
    buyPawnsIn(SitcaSW, n);

  }

  private int calInfantryNeededIn1(int c) {
    int attack = getAttackValueIn1Against(c);
    int defence = getInfantryDefenceValueAt(c);
    int defenceNeed = attack - defence;
    if (defenceNeed <= 0) {
      return 0;
    }
    return defenceNeed / infantryDefenceValue + 5;
  }

  private int getAttackValueIn1Against(int c) {
    // threat against Country c in the round following.
    int neighborTeamID, distance;
    String controller;

    int total = 0;
    int cTeamID = getTeamID(c);
    for (int n = 0; n < N_Countries; n++) {  // find neighbors
      controller = getControllerAt(n);
      if (controller.equals("Boring")) {
        continue;
      }
      neighborTeamID = getTeamID(n);
      if (cTeamID == neighborTeamID) {  // not hostile
        continue;
      }
      distance = pathCost[n][c];

      if (distance < 3) {
        total += getAttackValueFrom(n);
        continue;
      }
      if (distance <5) {
        total += getKnightAttackValueFrom(n);
        continue;
      }


    }
    return total;
  }

  private boolean noObstacleInPath(int c1, int c2) {
    // no obstacle to advance from c1 to attack c2?
    int next = visitNext[c1][c2];
    while (next != c2) {
      if (!NoIKCsIn(next) & !friendly(c1, next)) {
        return false;
      }
      next = visitNext[next][c2];
    }
    return true;

  }

  private String reinforceCentralGoldmine() {
    float ADratio = getADratioAt(CentralGoldmine);
    if (ADratio <= 0.6) {
      return "low threat";
    }
    if (ADratio <= 0.8) {
      moveInto(CentralGoldmine);
      buyPawnsIn(CentralGoldmine, 5);
      return "medium threat";
    }
    gatherTo(CentralGoldmine);
    buyKnightIn(SitcaBridgeS);
    return "high threat";
  }

  private void attackCentralGoldmine() {
    int ownerID = occupationTable[CentralGoldmine][0];
    if (ownerID == 6) {
      attackNomadsCentralGoldmine();
    }
    String status = reinforceSitcaBridgeS();
    if (status.equals("low threat")) {
      int defenceG = getDefenceValueAt(CentralGoldmine);
      int aKnight = (int) (defenceG * 2.5 / knightAttackValue);
      int dKnight = occupationTable[SitcaBridgeS][2] - aKnight;
      int InfantryDefenceB = getInfantryDefenceValueAt(SitcaBridgeS);
      int KnightDefenceB = dKnight * knightDefenceValue;
      float attackB = getAttackValueAgainst(SitcaBridgeS);
      float ADratio = attackB / (InfantryDefenceB + KnightDefenceB);
      if (ADratio > .8) {  // not enough defence at the bridge
        gatherTo(SitcaBridgeS);
        buyPawnsIn(SitcaBridgeS);
        return;
      }
      if (dKnight < 0) {  // not enought knights
        gatherTo(SitcaBridgeS);
        buyKnightIn(SitcaBridgeS);
        return;
      }
      // Attack goldmine
      move(aKnight, "Knight", SitcaBridgeS, CentralGoldmine);
      gatherTo(CentralGoldmine, SitcaBridgeS);
    }
  }

  private void attackNomadsCentralGoldmine() {
    buyKnightIn(SitcaBridgeS);
    moveInto(CentralGoldmine);
  }

  private int calInfantryNeeded(int c) {
    int attack = getAttackValueAgainst(c);
    int defence = getInfantryDefenceValueAt(c);
    int defenceNeed = attack - defence;
    if (defenceNeed <= 0) {
      return 0;
    }
    return defenceNeed / infantryDefenceValue + 5;
  }

  private int getAttackValueAgainst(int c) {
    // threat against Country c in the immediate coming round.
    int neighborTeamID, between;
    String controller;

    int total = 0;
    int cTeamID = getTeamID(c);
    for (int n = 0; n
       < N_Countries; n++) {  // find neighbors
      controller = getControllerAt(n);
      if (controller.equals("Boring")) {
        continue;
      }
      neighborTeamID = getTeamID(n);
      if (cTeamID == neighborTeamID) {  // not hostile
        continue;
      }
      if (pathCost[n][c] == 1) { // near c
        total += getAttackValueFrom(n);
      }
      if (pathCost[n][c] == 2) { // check knights
        between = visitNext[n][c];
        // Knight cannot cross a defenced unfriendly country rightaway.
        if (friendly(between, n) | NoIKCsIn(between)) {
          total += getKnightAttackValueFrom(n);
        }
      }
    }
    return total;
  }

  private boolean NoIKCsIn(int c) {
    int IKCs = occupationTable[c][1] + occupationTable[c][2]
       + occupationTable[c][3] + occupationTable[c][3] + occupationTable[c][5]
       + occupationTable[c][6];
    if (IKCs == 0) {
      return true;
    }
    return false;
  }

  private String reinforceSitcaBridgeS() {
    float ADratio = getADratioAt(SitcaBridgeS);
    if (ADratio <= 0.6) {
      return "low threat";
    }
    if (ADratio <= 0.8) {
      buyPawnsIn(SitcaBridgeS, 5);
      return "medium threat";
    }
    buyPawnsIn(SitcaBridgeS);
    return "high threat";
  }

  private void SitcaRound1() {
    move(5, "Infantry", SitcaCastle, 84);
    move(2, "Knight", SitcaCastle, SitcaGoldmine);
    move(1, "Infantry", SitcaCastle, 89);
    move("Infantry", SitcaCastle, SitcaGoldmine);
    move(1, "Infantry", 87, 97);
    move(1, "Infantry", 87, 98);
    move(4, "Infantry", 87, 54);
    move(1, "Infantry", 85, 71);
    move(2, "Infantry", 85, 72);
    move(3, "Infantry", 85, 88);
    buyKnightIn(SitcaCastle);
  }

  private void SitcaRound2() {
    moveInfantry(84, 73);
    moveInfantry(SitcaCastle, 89);
    moveInfantriesInto(SitcaSW);
    move(1, "Infantry", 72, 69);
    move(1, "Infantry", 72, 68);
    move(SitcaGoldmine, 89);
    gatherTo(SitcaGoldmine, 89);
    buyPawnsIn(SitcaCastle);
  }

  private void SitcaRound3() {
    move(73, SitcaNW);
    buyCastleFor(SitcaSW);
    moveInto(SitcaBridgeS);
    gatherTo(SitcaNW);
    buyPawnsIn(SitcaCastle);
  }

  private void SitcaRound4() {
    buyCastleFor(SitcaNW);
    //buyKnightIn(SitcaCastle);
    float ADratio = getADratioAt(SitcaBridgeS);
    if (ADratio > .6) {
      gatherTo(SitcaBridgeS);
      return;
    }
    gatherTo(SitcaNW);
  }

  private void buildSitcaNWCastle() {
    // I own the country.
    String status = reinforceSitcaBridgeS();
    float ADratio = getADratioFor(CentralGoldmine);
    if (ADratio <= 0.6) {
      buyCastleFor(CentralGoldmine);
      // Prepare ahead for the rounds after
      int spare = getSpareKnightAt(SitcaBridgeS);
      move(spare, "Knight", SitcaBridgeS, 72);
      return;
    }
    gatherTo(CentralGoldmine, SitcaBridgeS);
  }

  private void buildCentralGoldmineCastle() {
    // I own the country.
    String status = reinforceSitcaBridgeS();
    float ADratio = getADratioFor(CentralGoldmine);
    if (ADratio <= 0.6) {
      buyCastleFor(CentralGoldmine);
      // Prepare ahead for the rounds after
      int spare = getSpareKnightAt(SitcaBridgeS);
      move(spare, "Knight", SitcaBridgeS, 72);
      return;
    }
    gatherTo(CentralGoldmine, SitcaBridgeS);
  }

  private String reinforceSitcaBridgeS0() {
    // I own the castle here.
    float ADratio = getADratioAt(SitcaBridgeS);
    if (ADratio <= 0.6) {
      return "low threat";
    }
    if (ADratio <= 0.8) {
      buyPawnsIn(SitcaBridgeS, 5);
      return "medium threat";
    }
    buyPawnsIn(SitcaBridgeS);
    return "high threat";
  }

  private String reinforceSitcaSW() {
    // I own the castle here.
    int c = SitcaSW;
    float ADratio = getADratioAt(c);
    if (ADratio <= 0.6) {
      return "low threat";
    }
    if (ADratio <= 0.8) {
      moveInto(c);
      buyPawnsIn(c, 5);
      return "medium threat";
    }
    gatherTo(c);
    int n = calInfantryNeeded(c);
    buyPawnsIn(c, n);
    return "high threat";
  }

  private void buildSitcaSWcastle() {
    String status;
    status = reinforceSitcaBridgeS();
    if (status.equals("high threat")) {
      return;
    }
    status = reinforceCentralGoldmine();
    if (status.equals("high threat")) {
      return;
    }
    float ADratio = getADratioAt(SitcaSW);
    if (ADratio <= 0.6) {
      buyCastleFor(SitcaSW);
    }
    gatherTo(SitcaSW);
  }

  private String doCentralGoldmine() {
    float ADratio;
    if (castlePeaceful(CentralGoldmine)) {
      return "peaeful";
    }
    if (!friendly(CentralGoldmine)) {  // Enemy owns it
      int defence = getDefenceValueFor(CentralGoldmine);
      int myAttack = getMyAttackValueAgainst(CentralGoldmine);
      int totalAttack = getAttackValueAgainst(CentralGoldmine);
      if (myAttack > 1.2 * defence & myAttack / totalAttack > .6) {
        gatherTo(CentralGoldmine, SitcaBridgeS);
        move(
           "Knight", SitcaBridgeS, CentralGoldmine);
        return "attack it";
      } else {
        gatherTo(67, SitcaBridgeS);
        return "prepare to attack";
      }
    }
    if (Iown(CentralGoldmine)) {  // I own it
      ADratio = getADratioAt(CentralGoldmine);
      if (ADratio > 0.8) {
        gatherTo(CentralGoldmine);
        return "under threat";
      }
    }
    // My friend owns Central Goldmine
    return "friendly";
  }

  private void buildSitcaBridgeScastle() {
    float ADratio = getADratioFor(SitcaBridgeS);
    gatherTo(SitcaBridgeS);
    if (ADratio <= 0.8) {
      buyCastleFor(SitcaBridgeS);
    }
    buyPawnsIn(SitcaCastle);
  }

  private int getSpareKnightAt(int c) {
    int attack = getAttackValueAgainst(c);
    float infantryDefence = getInfantryDefenceValueAt(c);
    float ADratio = attack / infantryDefence;
    if (ADratio < .8) {  // infantry defence is sufficient
      int spare = occupationTable[c][2];
      return spare;
    }
    return 0;
  }

  private void attackSitcaBridgeS() {
    gatherTo(SitcaBridgeS);
    int C = getMyCastleNear(SitcaBridgeS);
    if (C == -1) {
      int c = getMyCountryNear(SitcaBridgeS);
      buyCastleFor(c);
    } else {
      buyKnightIn(C);
    }
  }

  private int getMyCastleNear(int c) {
    ArrayList<Integer> myCastleList = new ArrayList<Integer>();
    myCastleList = getCastleList(ID);
    int d = infinity;
    int nearestCastle = -1;
    for (Integer C : myCastleList) {
      if (pathCost[c][C] < d) {
        nearestCastle = C;
      }
    }
    return nearestCastle;
  }

  private int getMyCountryNear(int c) {
    ArrayList<Integer> clist = new ArrayList<Integer>();
    clist = CountriesBelongTo[ID];
    int d = infinity;
    int nearestCountry = -1;
    for (Integer C : clist) {
      if (pathCost[c][C] < d) {
        nearestCountry = C;
      }
    }
    return nearestCountry;
  }

  private ArrayList getCastleList(int id) {
    ArrayList castleList = new ArrayList();
    for (int c = 0; c < N_Countries; c++) {
      if (occupationTable[c][0] == id & occupationTable[c][3] == 1) {
        castleList.add(new Integer(c));
      }
    }
    return castleList;
  }

  private void attackSitcaSW() {
    float ADratio = getADratioAt(SitcaSW);
    gatherTo(SitcaSW, CentralGoldmine, SitcaBridgeS);
  }

  private float getADratioAt(int c) {
    int attack = getAttackValueAgainst(c);
    if (attack == 0) {
      return 0;
    }
    float defence = getDefenceValueAt(c);
    return attack / defence;
  }

  private float getADratioFor(int c) {
    int attack = getAttackValueAgainst(c);
    if (attack == 0) {
      return 0;
    }
    float defence = getDefenceValueFor(c);
    return attack / defence;
  }

  private boolean castle(int c) {
    if (occupationTable[c][3] == 1) {
      return true;
    }
    return false;
  }

  private boolean castlePeaceful(int c) {
    float ADratio = getADratioAt(c);
    if (castleIsMine(c) & ADratio <= .2) {
      return true;
    }
    return false;
  }

  private boolean castleLowThreat(int c) {
    float ADratio = getADratioAt(c);
    if (ADratio > 0.2 & ADratio <= 0.6) {
      if (castleIsMine(c)) {
        buyPawnsIn(c);
      } else {
        buyCastleFor(c);
      }
      return true;
    }
    return false;
  }

  public void gatherTo(int c, int... exceptions) {
    UnitStack unitForce;
    int next;
    for (int from = 0; from
       < N_Countries; from++) {
      if (in(from, exceptions)) {
        continue;
      }
      unitForce = getKnight(from);
      if (unitForce != null) {
        next = visitNext[from][c];
        next = visitNext[next][c];
        moveKnight(
           from, next);
      }
      unitForce = getInfantry(from);
      if (unitForce != null) {
        next = visitNext[from][c];
        moveInfantry(
           from, next);
      }
    }
  }

  private void buyPawnsIn(int c, int units) {
    int m = units * infantryCost;
    if (money >= m) {
      buyPawns(m, Countries[c]);
    } else {
      buyPawns(money, Countries[c]);
    }
  }

  private void buyPawnsIn(int c) {
    buyPawns(money, Countries[c]);
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
    if (occupationTable[c][0] == ID) {
      return true;
    }
    return false;
  }

  private int getInfantryDefenceValueAt(int c) {
    int nInfantry = occupationTable[c][1]
       + occupationTable[c][5];
    return nInfantry * infantryDefenceValue;
  }

  private String getControllerAt(int c) {
    // Controller is the class name of the bot.
    // Example: "Boring"
    return Countries[c].getOwner().getControllerName();
  }

  private int getDefenceValueFor(int c) {
    // total defence value for Country c in the immediate coming round.
    int total = getDefenceValueAt(c);
    int cTeamID = getTeamID(c);
    for (int n = 0; n < N_Countries; n++) {  // find neighbors
      int neighborTeamID = getTeamID(n);
      if (cTeamID == neighborTeamID) {  // not hostile
        if (pathCost[n][c] == 1) { // near c
          total += getDefenceValueAt(n);
        }
        if (pathCost[n][c] == 2) { // check knights
          total += getKnightDefenceValueAt(n);
        }
      }
    }
    return total;
  }

  private int getKnightDefenceValueAt(int c) {
    int nKnight = occupationTable[c][2] + occupationTable[c][6];
    int total = nKnight * knightDefenceValue;
    return total;
  }

  private int getDefenceValueAt(int c) {
    int nInfantry = occupationTable[c][1] + occupationTable[c][5];
    int nKnight = occupationTable[c][2] + occupationTable[c][6];
    int total = nInfantry * infantryDefenceValue + nKnight * knightDefenceValue;
    return total;
  }

  private void Round1Init() {
    myTeamID = ID2teamID[ID];
    N_players = world.getNumberOfPlayers();
    N_Countries = Countries.length;
    CountriesBelongTo = new ArrayList[N_players];
    N_SitcaHomeCountrySet = SitcaHome.length;
    friendID = getFriendID();
    makeID2Team();
    makeCountryNamesSorted();
    getHumanFriend();
    buildShortestPathArray();
    getHumanID();
    whoAmI = world.getPlayer(ID).name();
    AIplayer = world.getPlayer(ID);
    myTeam = world.getTeam(ID);
  }

  private void makeID2Team() {
    ID2Team = new Team[N_players];
    for (int i = 0; i < N_players; i++) {
      Team team_i = world.getTeam(i);
      ID2Team[i] = team_i;
    }
  }

  private void getHumanFriend() {
    // Find the bot friendly to the human.
    for (int i = 0; i
       < N_players; i++) {
      Team team_i = world.getTeam(i);
      if ((humanID > -1) && i != humanID && team_i.equals(humanTeamName)) {
        humanFriendlyID = i;
        humanFriendlyPlayer = world.getPlayer(humanFriendlyID);
        break;  // Break out of this for-loop.
      }
    }
    if (humanFriendlyID == -1) {
      humanFriendlyCastleI = -1;
    } else {
      humanFriendlyCastleI = get1CastleOwnedBy(humanFriendlyID);
    }
    humanCastleI = get1CastleOwnedBy(humanID);
  }

  private int getFriendID() {
    for (int i = 0; i < N_players; i++) {
      if (i != ID & myTeamID == ID2teamID[i]) {
        return i;
      }
    }
    return -1;
  }

  private void makeCountryNamesSorted() {
    CountryNames = new String[N_Countries];
    CountryNamesSorted = new String[N_Countries];
    for (int c = 0; c
       < N_Countries; c++) {
      Country ct = Countries[c];
      int t1 = ct.getID();
      CountryNames[c] = ct.getName();
      CountryNamesSorted[c] = CountryNames[c] + ' ' + c;
    }
    java.util.Arrays.sort(CountryNamesSorted);
  }

  private void getHumanID() {
    IDisaTonyAI = new boolean[N_players];
    for (int i = 0; i
       < N_players; i++) {
      Player player_i = world.getPlayer(i);
      String name = player_i.getAgentType();
      if (name.equals("TonyAI")) {
        IDisaTonyAI[i] = true;
      } else {
        boolean human = player_i.isHuman();
        if (human) {
          humanTeamName = world.getTeam(i);
          humanID = i;
          if (i == 6) {
            humanPlaysNomads = true;
          }
        }
      }
    }
  }

  private void playSartrus() {
    switch (Round) {
      case 1:
        move(2, "Knight", 57, 101);
        gatherTo(
           CentralGoldmine);
        buyKnightIn(
           57);
        break;
      case 2:

        break;
    }
  }

  private void playZonlor() {
  }

  private int getTeamID(int c) {
    int OwnerID = getOwnerID(c);
    int OwnerTeamID = ID2teamID[OwnerID];
    return OwnerTeamID;
  }

  private int getMyAttackValueAgainst(int c) {
    // My threat against Country c in the immediate coming round.
    int total = 0;
    int cTeamID = getTeamID(c);
    for (int n = 0; n
       < N_Countries; n++) {  // find neighbors
      int neighborTeamID = getTeamID(n);
      if (cTeamID == neighborTeamID) {  // not hostile
        continue;
      }
      if (pathCost[n][c] == 1) { // near c

        total += getMyAttackValueFrom(n);
      }
      if (pathCost[n][c] == 2) { // check knights
        total += getMyKnightAttackValueFrom(n);
      }
    }
    return total;
  }

  private int getAttackValueFrom(int c) {
    int nInfantry = occupationTable[c][1] + occupationTable[c][5];
    int nKnight = occupationTable[c][2] + occupationTable[c][6];
    int total = nInfantry * infantryAttackValue + nKnight * knightAttackValue;
    return total;
  }

  private int getMyInfantryAttackValueFrom(int c) {
    int nInfantry, total;
    int cOwnerID = occupationTable[c][0];
    int cTeamID = ID2teamID[cOwnerID];
    if (cTeamID != myTeamID) {
      return 0;
    }
    if (cOwnerID == ID) {
      nInfantry = occupationTable[c][1];
      total = nInfantry * infantryAttackValue;
      return total;
    }
    nInfantry = occupationTable[c][5];
    total = nInfantry * infantryAttackValue;
    return total;
  }

  private int getMyKnightAttackValueFrom(int c) {
    int nKnight, total;
    int cOwnerID = occupationTable[c][0];
    int cTeamID = ID2teamID[cOwnerID];
    if (cTeamID != myTeamID) {
      return 0;
    }
    if (cOwnerID == ID) {
      nKnight = occupationTable[c][2];
      total = nKnight * knightAttackValue;
      return total;
    }
    nKnight = occupationTable[c][6];
    total = nKnight * knightAttackValue;
    return total;
  }

  private int getMyAttackValueFrom(int c) {
    int total = getMyInfantryAttackValueFrom(c);
    total += getMyKnightAttackValueFrom(c);
    return total;
  }

  private int getKnightAttackValueFrom(int c) {
    int nKnight = occupationTable[c][2] + occupationTable[c][6];
    int total = nKnight * knightAttackValue;
    return total;
  }

  private int getOwnerID(int c) {
    Player player = Countries[c].getOwner();
    return player.getID();
  }

  private void makeCountriesBelongTo() {
    Player player;
    int i, id;
    // Initialize the lists to 0s.
    for (i = 0; i < N_players; i++) {
      CountriesBelongTo[i] = new ArrayList();
    }
    for (i = 0; i < N_Countries; i++) {
      player = Countries[i].getOwner();
      id = player.getID();
      CountriesBelongTo[id].add(new Integer(i));
    }
  }

  private void buyKnightIn(int c) {
    buyKnights(money, Countries[c]);
  }

  private void buyCastleFor(int c) {
    Unit castle = new UnitCastle(world.getPlayer(ID));
    UnitStack castle2 = new UnitStack(castle, 1);
    world.placeUnits(castle2, Countries[c]);
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
    for (int i = 0; i
       < n; i++) {
      int neighbor = adjacencyList[c][i];
      move(
         "Infantry", neighbor, c);
    }
  }

  private void moveKnight(int from, int to) {
    move("Knight", from, to);
  }

  private void moveKnightInto(int c) {
    for (int i = 0; i
       < N_Countries; i++) {
      if (pathCost[i][c] < 3) {
        move("Knight", i, c);
      }
    }
  }

  private UnitStack getKnightOrInfantry(int c, int movement) {
    Country ct = Countries[c];
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
    int total = occupationTable[c][1] + occupationTable[c][2]
       + occupationTable[c][3]
       + occupationTable[c][5] + occupationTable[c][6];

    if (total == 0) {
      return false;
    }
    return true;
  }

  private void makeOccupationTable() {
    Country ct;
    UnitStackGroup IKCs;
    UnitStack IKorC;
    Player player;
    String IKxorC;
    int N_IKCs, unitOwnerID, teamID, N_units, cOwnerID;
    occupationTable = new int[N_Countries][7];
    for (int c = 0; c
       < N_Countries; c++) {
      ct = Countries[c];
      cOwnerID = ct.getOwner().getID();
      occupationTable[c][0] = cOwnerID;               // 0
      IKCs = ct.getUnitStackGroup();
      N_IKCs = IKCs.size();
      for (int s = 0; s
         < N_IKCs; s++) {
        IKorC = IKCs.get(s);
        player = IKorC.getOwner();
        unitOwnerID = player.getID();
        N_units = IKorC.getCount();
        IKxorC = IKorC.getUnit().toString();
        if (cOwnerID == unitOwnerID) {
          if (IKxorC.equals("Infantry")) {
            occupationTable[c][1] = N_units;           // 1
          }
          if (IKxorC.equals("Knight")) {
            occupationTable[c][2] = N_units;           // 2
          }
          if (IKxorC.equals("Castle")) {
            occupationTable[c][3] = 1;                 // 3
          }
        } else {
          occupationTable[c][4] = unitOwnerID;         // 4
          if (IKxorC.equals("Infantry")) {
            occupationTable[c][5] = N_units;            // 5
          }
          if (IKxorC.equals("Knight")) {
            occupationTable[c][6] = N_units;            // 6
          }
        }
      }
    }
    SitcaBridgeScastleIsMine = castleIsMine(SitcaBridgeS);
    CentralGoldmineCastleIsMine = castleIsMine(CentralGoldmine);

  }

  private boolean castleIsMine(int c) {
    if (occupationTable[c][3] == 1 & occupationTable[c][0] == ID) {
      return true;
    }
    return false;
  }

  private boolean friendOwn(int c) {
    if (occupationTable[c][0] == friendID) {
      return true;
    }
    return false;
  }

  private boolean friendly(int c) {
    if (Iown(c)) {
      return true;
    }
    if (friendOwn(c)) {
      return true;
    }
    return false;
  }

  private boolean friendly(int c1, int c2) {
    int owner1 = occupationTable[c1][0];
    int team1 = ID2teamID[owner1];
    int owner2 = occupationTable[c2][0];
    int team2 = ID2teamID[owner2];
    if (team1 == team2) {
      return true;
    }
    return false;
  }

  private void wandering() {
    Country c, ourCountry, moveTo, dir1, dir2;
    UnitStackGroup usg;
    UnitStack us;
    int IKCtotal, i, j;
    List castleList;
    castleList = world.getCastleCountriesOwnedBy(ID);
    int n = castleList.size();
    if (n > 0) {
      i = rand.nextInt(n);   // Get a random castle
      c = (Country) castleList.get(i);
      // Buy 1 infantry, then 1 knight, as much as money allows.
      // Place them on castle c.
      buyUnitsAlternating(money, c);
    }
    for (i = 0; i < Countries.length; i++) {
      // Check out this country's force.
      c = Countries[i];
      usg = c.getUnitStackGroup();
      IKCtotal = usg.getTotalUnitCount();  // # of infantry + # of knight + castle
      Team team_i = c.getTeam();
      if (IKCtotal > 0 && team_i.equals(myTeam)) {
        ourCountry = c;
        // Loop through this country's stack group
        int N_usg = usg.size();
        for (j = N_usg - 1; j > -1; j--) {
          // UnitStack class represents a group of units that all share the same type and owner and original Country.
          us = usg.get(j);
          int ID2 = us.getOwner().getID();
          Unit usUnit = us.getUnit();
          // Infantry movement stepSize: 1 country; knight: 2 countries
          int stepSize = usUnit.getMovement();
          // Same team, same player? for this stack unit
          if (ID2 == ID && stepSize > 0) {
            moveTo = getRandomBorder(ourCountry);
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

  private int get1CastleOwnedBy(int ID) {
    Country ct;
    List castleList = world.getCastleCountriesOwnedBy(ID);
    if (castleList.size() == 0) {
      return -1;
    }
    ct = (Country) castleList.get(0);
    return ct.getID();
  }

  private void buildShortestPathArray() {

    int i, j, k, c, n, visitThru[][];
    Country ct, ct2;
    List adjoiningList;

    edgeCost = new int[N_Countries][N_Countries];
    for (i = 0; i
       < N_Countries; i++) {
      for (j = 0; j
         < N_Countries; j++) {
        edgeCost[i][j] = infinity;
      }
      edgeCost[i][i] = 0;
    }

    adjacencyList = new int[N_Countries][];
    for (c = 0; c
       < N_Countries; c++) {
      ct = Countries[c];
      adjoiningList = ct.getAdjoiningList();
      n = adjoiningList.size();
      adjacencyList[c] = new int[n];
      for (j = 0; j
         < n; j++) {
        ct2 = (Country) adjoiningList.get(j);
        k = ct2.getID();
        edgeCost[c][k] = 1;
        adjacencyList[c][j] = k;
      }
    }

    N_neighbors = new int[N_Countries];
    for (c = 0; c
       < N_Countries; c++) {
      N_neighbors[c] = 0;
      for (j = 0; j
         < N_Countries; j++) {
        if (edgeCost[c][j] == 1) {
          N_neighbors[c] += 1;
        }
      }
    }

    pathCost = new int[N_Countries][N_Countries];
    visitThru = new int[N_Countries][N_Countries];
    for (i = 0; i
       < N_Countries; i++) {
      for (j = 0; j
         < N_Countries; j++) {
        pathCost[i][j] = edgeCost[i][j];
        visitThru[i][j] = -1;
      }
    }

    for (k = 0; k
       < N_Countries; k++) {
      for (i = 0; i
         < N_Countries; i++) {
        for (j = 0; j
           < N_Countries; j++) {
          if (pathCost[i][k] + pathCost[k][j] < pathCost[i][j]) {
            pathCost[i][j] = pathCost[i][k] + pathCost[k][j];
            visitThru[i][j] = k;
          }
        }
      }
    }

    shortestPath = new int[N_Countries][N_Countries][];
    visitNext = new int[N_Countries][N_Countries];
    for (i = 0; i
       < N_Countries; i++) {
      for (j = 0; j
         < N_Countries; j++) {
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

  private boolean symmetric(int[][] matrix) {
    int i, j, n;
    n = matrix.length;
    for (i = 0; i
       < n; i++) {
      for (j = 0; j
         < n; j++) {
        if (matrix[i][j] != matrix[j][i]) {
          return false;
        }
      }
    }
    return true;
  }

  private void move(int N_units, String KnightOrInfantry, int from, int to) {
    UnitStack unitForce;
    if (from == to) {
      return;
    }
    Country ct = Countries[from];
    if (KnightOrInfantry.equals("Knight")) {
      unitForce = getKnight(from);
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
    world.moveUnit(unitForce, ct, Countries[to], N_units);

  }
}	

