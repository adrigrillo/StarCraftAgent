package org.iaie.practica2.units;

import org.iaie.btree.util.GameHandler;

import jnibwapi.JNIBWAPI;

public class TrainingTree extends GameHandler {

	public TrainingTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	/**
	 * Comprueba que hay recursos suficientes para construir una unidad
	 * @return 1 si los hay, -1 si no los hay, -2 si hay algun error 
	 */
	public int checkUnitResources(){
		try{
			if (connector.getSelf().getMinerals() > 50)
				return 1;
			else
				return -1;
		} catch (Exception e){
			return -2;
		}
	}
}
