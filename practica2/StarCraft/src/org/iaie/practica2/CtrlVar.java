package org.iaie.practica2;

/********************************************************************************************
 * Esta clase contendrá las variables de control del juego, para poder 
 * ser accedidas por todas las clases
 *******************************************************************************************/

import java.util.ArrayList;
import java.util.HashSet;

import jnibwapi.Unit;
import jnibwapi.types.UnitType;

public class CtrlVar {
	
	// Lista donde se guarda los recursos en nuestro poder
	public static HashSet<Unit> claimedMinerals = new HashSet<>();
    // Lista de trabajadores
	public static HashSet<Unit> workers = new HashSet<>();
    // Lista de unidades militares
	public static HashSet<Unit> militaryUnits = new HashSet<>();
    // Lista de edificios construidos
	public static HashSet<Unit> buildings = new HashSet<>();
    // Lista de edificios a construir
	public static ArrayList<UnitType> buildqueue = new ArrayList<>();
    // Lista de unidades a entrenar
	public static ArrayList<UnitType> trainqueue = new ArrayList<>();
    
    public void clearAll(){
		claimedMinerals.clear();
		militaryUnits.clear();
		workers.clear();
		buildings.clear();
		buildqueue.clear();
		trainqueue.clear();
    }
}
