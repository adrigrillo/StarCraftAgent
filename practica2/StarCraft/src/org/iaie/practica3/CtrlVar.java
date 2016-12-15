package org.iaie.practica3;

/********************************************************************************************
 * Esta clase contendr� las variables de control del juego, para poder 
 * ser accedidas por todas las clases
 *******************************************************************************************/

import java.util.ArrayList;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

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
    
    public static void clearAll(){
		claimedMinerals.clear();
		militaryUnits.clear();
		workers.clear();
		buildings.clear();
		refinery.clear();
		buildqueue.clear();
		trainqueue.clear();
		centroMando.clear();
    }
    
    
    /**
     * Metodo que actualiza la lista de edificios de los que se dispone
     * @param bwapi estado de la partida
     */
    public static void refreshBuildings(JNIBWAPI bwapi){
    	for (Unit unit : bwapi.getMyUnits()){
    		if (unit.getType().isBuilding() && unit.isCompleted() && !buildings.contains(unit)){
				buildings.add(unit);
				if (unit.getType() == UnitTypes.Terran_Command_Center)
					centroMando.add(unit);
			}
    	}
    }
    
    /**
     * Metodo para actualizar 
     * @param bwapi	estado de la partida
     */
    public static void refreshClaimed(JNIBWAPI bwapi){
    	for (Unit unit : bwapi.getMyUnits()){
			if (unit.getType().isWorker() && unit.isGatheringMinerals()){
				Unit mineral = unit.getTarget();
				if (mineral.getType().isMineralField() && !claimedMinerals.contains(mineral))
					claimedMinerals.add(mineral);
			}
		}
    }
}
