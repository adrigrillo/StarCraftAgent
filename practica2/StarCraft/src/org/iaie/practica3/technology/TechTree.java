package org.iaie.practica3.technology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica3.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.TechType;
import jnibwapi.types.UnitType;
import jnibwapi.types.TechType.TechTypes;
import jnibwapi.types.UnitType.UnitTypes;
import jnibwapi.types.UpgradeType.UpgradeTypes;
import jnibwapi.types.UpgradeType;

public class TechTree extends GameHandler{
	
	public TechTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	// Esta lista contendra todos los edificios que tengan para realizar mejoras
	private ArrayList<UnitType> researchBuildings = new ArrayList<UnitType>(Arrays.asList(UnitTypes.Terran_Engineering_Bay,
			UnitTypes.Terran_Academy, UnitTypes.Terran_Armory, UnitTypes.Terran_Machine_Shop, UnitTypes.Terran_Comsat_Station,
			UnitTypes.Terran_Science_Facility, UnitTypes.Terran_Control_Tower, UnitTypes.Terran_Physics_Lab, 
			UnitTypes.Terran_Covert_Ops, UnitTypes.Terran_Nuclear_Silo));
	private ArrayList<TechType> technologies = new ArrayList<TechType>(TechTypes.getAllTechTypes());
	private ArrayList<UpgradeType> upgrades = new ArrayList<UpgradeType>(UpgradeTypes.getAllUpgradeTypes());
	Unit building = null;
	
	
	/**
	 * Metodo que se encargara de comprobar si tenemos
	 * algun edificio que permita desarrollar mejoras researchBuildings
	 * @return 1 si existe, 0 si no existe, -1 error
	 */
	public int checkBuildings(){
		try {
			// Cogemos la lista de edificios construidos y los mezclamos para que no siempre
			// empiece por los mismos y se desarrollen diferentes mejoras
			ArrayList<Unit> buildingsBuilt = CtrlVar.buildings;
			Collections.shuffle(buildingsBuilt);
			for (Unit buildAvailable : buildingsBuilt){
				// Comprobamos que esta contruido y no esta desarollando nada ya
				if (researchBuildings.contains(buildAvailable.getType()) && 
						(buildAvailable.getRemainingResearchTime() == 0 || buildAvailable.getRemainingUpgradeTime() == 0)){
					building = buildAvailable;
					return 1;
				}
			}
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	/**
	 * Para no ser tan agresivos vamos a necesitar mas de 100 de cada 
	 * recurso para mandar una mejora
	 * @return 1 si tenemos recursos, 0 si no los tenemos, -1 error
	 */
	public int checkResources(){
		try {
			if (this.connector.getSelf().getMinerals() > 100 && this.connector.getSelf().getGas() > 100)
				return 1;
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * Comprueba que la unidad elegida puede desarollar algo y 
	 * si puedo lo empieza a desarrollar
	 * @return 1 si la unidad se ha puesto a desarrollar, 0 si no ha podido, -1 error
	 */
	public int checkDevelopments(){
		try {
			// Comprobamos si puede desarrollar alguna tecnologia
			for (TechType tecnologia : technologies){
				if (building.research(tecnologia))
					return 1;
			}
			// Comprobamos si puede desarrollar alguna mejora
			for (UpgradeType mejora : upgrades){
				if (building.upgrade(mejora))
					return 1;
			}		
			return 0;
		} catch (Exception e) {
			return -1;		
		}
	}
}
