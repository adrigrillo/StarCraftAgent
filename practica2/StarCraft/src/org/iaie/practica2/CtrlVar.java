package org.iaie.practica2;

/********************************************************************************************
 * Esta clase contendrá las variables de control del juego, para poder 
 * ser accedidas por todas las clases
 *******************************************************************************************/

import java.util.ArrayList;
import java.util.HashSet;

import jnibwapi.Unit;

public class CtrlVar {
	
	public static HashSet<Unit> claimedMinerals = new HashSet<>();
    public static HashSet<Unit> workers = new HashSet<>();
    public static HashSet<Unit> militaryUnits = new HashSet<>();
    public static HashSet<Unit> buildings = new HashSet<>();
    public static ArrayList<Unit> buildqueue = new ArrayList<>();   
    
    public void clearAll(){
		claimedMinerals.clear();
		militaryUnits.clear();
		workers.clear();
		buildings.clear();
		buildqueue.clear();
    }
}
