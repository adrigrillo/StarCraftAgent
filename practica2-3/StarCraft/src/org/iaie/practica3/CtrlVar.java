package org.iaie.practica3;

/********************************************************************************************
 * Esta clase contendr� las variables de control del juego, para poder 
 * ser accedidas por todas las clases
 *******************************************************************************************/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

public class CtrlVar {
	
	// Lista donde se guarda los recursos en nuestro poder
	public static ArrayList<Unit> claimedMinerals = new ArrayList<>();
	// Lista donde se guarda las minas de vespeno en nuestro poder
	public static ArrayList<Unit> claimedVespene = new ArrayList<>();
    // Lista de trabajadores
	public static ArrayList<Unit> workers = new ArrayList<>();
    // Lista de unidades militares
	public static ArrayList<Unit> militaryUnits = new ArrayList<>();
    // Lista de edificios construidos
	public static ArrayList<Unit> buildings = new ArrayList<>();
	// Lista de refinerias construidos y sus trabajadores
	public static HashMap<Unit, Integer> refinery = new HashMap<Unit, Integer>();
    // Lista de edificios a construir
	public static ArrayList<UnitType> buildqueue = new ArrayList<>();
    // Lista de unidades a entrenar
	public static ArrayList<UnitType> trainqueue = new ArrayList<>();
	// Centro de mando
	public static ArrayList<Unit> centroMando = new ArrayList<>();
	// contadores de unidades 0: worker 1: militares 2:edificios
	public static ArrayList<Integer> contador;
    
    public static void clearAll(){
		claimedMinerals.clear();
		claimedVespene.clear();
		militaryUnits.clear();
		workers.clear();
		buildings.clear();
		refinery.clear();
		buildqueue.clear();
		trainqueue.clear();
		centroMando.clear();
		contador = new ArrayList<Integer>(Arrays.asList(0, 0, 0));
    }
    
    
    /**
     * Metodo que actualiza la lista de edificios de los que se dispone
     * @param bwapi estado de la partida
     */
    public static void refreshBuildings(JNIBWAPI bwapi){
    	for (Unit unit : bwapi.getMyUnits()){
    		if (unit.getType().isBuilding() && unit.isCompleted() && !buildings.contains(unit)){
				buildings.add(unit);
				contador.set(2, contador.get(2) + 1);
				if (unit.getType() == UnitTypes.Terran_Command_Center)
					centroMando.add(unit);
			}
    	}
    }
    
    /**
     * Metodo que actualiza las unidades
     * @param bwapi estado de la partida
     */
    public static void refreshUnits(JNIBWAPI bwapi){
    	for (Unit unit : bwapi.getMyUnits()){
    		if (unit.getType().isWorker() && unit.isCompleted() && !workers.contains(unit)){
    			workers.add(unit);
    		}
    		else if (!unit.getType().isBuilding() && !unit.getType().isWorker() && 
    				unit.getType().isAttackCapable() && !militaryUnits.contains(unit)){
    			militaryUnits.add(unit);
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
