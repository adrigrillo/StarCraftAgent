package org.iaie.practica2.units;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

public class TrainingTree extends GameHandler {
	
	private UnitType toTrain = null;

	public TrainingTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	/**
	 * Este metodo comprueba los edificios necesarios para construir la unidad
	 * @return
	 */
	public int checkBuildingExist(){
		try {
			
			toTrain = null;
			/* Recorremos la cola de edificios a construir para ver si alguno
			 * puede ser construido */
			for (UnitType unit : CtrlVar.trainqueue){
				/* Si la unidad necesita mas de un edificio para ser
				 * contruido se comprueban todos */
				Iterator<Integer> it = unit.getRequiredUnits().keySet().iterator();
				while (it.hasNext()){
					/* Comprobamos que estan todos construidos
					 * recorriendo la lista de edificios construidos */
					Boolean construido = false;
					int idEdificio = it.next().intValue();
					for (Unit edificio : CtrlVar.buildings){
						if (edificio.getType().equals(UnitType.UnitTypes.getUnitType(idEdificio))){
							construido = true;
						}
					}
					// Si falta algun edificio para construir la unidad se pasa al siguiente
					if (!construido){
						//Añadimos el edificio que falta a la cola de construccion
						//CtrlVar.buildqueue.add(UnitType.UnitTypes.getUnitType(idBuilding));
						break;
					}
				}
				// Si tiene la posibilidad de ser entrenado se guarda para comprobar sus recursos
				if (toTrain != null){
					toTrain = unit;
					break;
				}
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
