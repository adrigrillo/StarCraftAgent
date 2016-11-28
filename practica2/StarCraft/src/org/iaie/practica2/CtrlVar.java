package org.iaie.practica2;

/********************************************************************************************
 * Esta clase contendrá las variables de control del juego, para poder 
 * ser accedidas por todas las clases
 *******************************************************************************************/

import java.util.ArrayList;

import jnibwapi.Unit;
import jnibwapi.types.UnitType;

public class CtrlVar {
	
	// Lista donde se guarda los recursos en nuestro poder
	public static ArrayList<Unit> claimedMinerals = new ArrayList<>();
    // Lista de trabajadores
	public static ArrayList<Unit> workers = new ArrayList<>();
    // Lista de unidades militares
	public static ArrayList<Unit> militaryUnits = new ArrayList<>();
    // Lista de edificios construidos
	public static ArrayList<Unit> buildings = new ArrayList<>();
	// Lista de refinerias construidos
	public static ArrayList<Unit> refinery = new ArrayList<>();
    // Lista de edificios a construir
	public static ArrayList<UnitType> buildqueue = new ArrayList<>();
    // Lista de unidades a entrenar
	public static ArrayList<UnitType> trainqueue = new ArrayList<>();
	// Centro de mando
	public static ArrayList<Unit> centroMando = new ArrayList<>();
    
    public void clearAll(){
		claimedMinerals.clear();
		militaryUnits.clear();
		workers.clear();
		buildings.clear();
		refinery.clear();
		buildqueue.clear();
		trainqueue.clear();
		centroMando.clear();
    }
}
