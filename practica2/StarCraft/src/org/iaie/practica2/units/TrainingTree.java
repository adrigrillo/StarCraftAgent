package org.iaie.practica2.units;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;

public class TrainingTree extends GameHandler {
	
	private UnitType toTrain = null;

	public TrainingTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	public int checkBuildingExist(){
		try {
			for (UnitType unit : CtrlVar.trainqueue){
				unit.getRequiredUnits();
				unit.getWhatBuildID();
			}
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * Comprueba que hay recursos suficientes para construir una unidad
	 * @return 1 si los hay, -1 si no los hay, -2 si hay algun error 
	 */
	public int checkUnitResources(){
		try{
			for (UnitType unit : CtrlVar.trainqueue){
			}
			return 0;
		} catch (Exception e){
			return -1;
		}
	}
}
