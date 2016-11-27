package org.iaie.practica2.units;

import java.util.ArrayList;
import java.util.Iterator;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;

public class TrainingTree extends GameHandler {
	
	private UnitType toTrain = null;
	private Unit training = null;
	private Unit edificio = null;

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
				// Miramos para la unidad si los edificios necesarios estan construidos
				Boolean construido = false;
				/* Si la unidad necesita mas de un edificio para ser
				 * contruido se comprueban todos */
				Iterator<Integer> it = unit.getRequiredUnits().keySet().iterator();
				while (it.hasNext()){
					/* Comprobamos que estan todos construidos
					 * recorriendo la lista de edificios construidos */
					construido = false;
					int idEdificio = it.next().intValue();
					for (Unit edificio : CtrlVar.buildings){
						if (edificio.getType().equals(UnitType.UnitTypes.getUnitType(idEdificio))){
							construido = true;
							break;
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
				if (construido){
					toTrain = unit;
					break;
				}
			}
			// Si se ha conseguido una unidad para entrenar devuelve success
			if (toTrain != null)
				return 1;
			else
				return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * Comprueba que hay recursos suficientes para construir una unidad
	 * @return 1 si los hay, 0 si no los hay, -1 si hay algun error 
	 */
	public int checkUnitResources(){
		try {
			boolean mineral = false;
			boolean gas = false;
			boolean population = false;
			// Comprobamos si tenemos los recursos necesarios
			if (toTrain.getMineralPrice() <= connector.getSelf().getMinerals())
				mineral = true;
			if (toTrain.getGasPrice() <= connector.getSelf().getGas())
				gas = true;
			if (connector.getSelf().getSupplyUsed() + toTrain.getSupplyRequired() <= connector.getSelf().getSupplyTotal())
				population = true;
			if (mineral && gas && population){
				crearUnidad();
				return 1;
			}
			else
				return 0;
		} catch (Exception e){
			return -1;
		}
	}
	
	
	/**
	 * Metodo que se encarga de poner a entrenar a la unidad tras pasar los controles
	 * Elige el edificio con menos cola para crear la unidad
	 */
	private void crearUnidad(){
		ArrayList<Unit> posEdificios = new ArrayList<>();
		// Buscamos el edificio que construira la unidad
		for (Unit unit : CtrlVar.buildings){
			// Si el tipo de edificio es del mismo tipo que construye la unidad se suma a lista
			if (unit.getType().equals(UnitType.UnitTypes.getUnitType(toTrain.getWhatBuildID())))
				posEdificios.add(unit);
		}
		// Cogemos el que menos cola si es que hay varios
		int cola = 5;
		edificio = null;
		for (Unit unit : posEdificios){
			if (unit.getTrainingQueueSize() < cola){
				cola = unit.getTrainingQueueSize();
				edificio = unit;
			}
		}
		// Tras elegir el mejor edificio se contruye la unidad
		edificio.train(toTrain);
    }
	
	/**
	 * Comprueba el estado de la unidad que se esta entrenando
	 * @return 1 si se ha completado, 0 si esta en proceso, -1 si hay algún error
	 */
	public int trainingState(){
		try {
			/* Comprobamos que la unidad se esta construyendo para guardarla
			 * y asi comprobar posteriormente cuando se completa */
			if (edificio != null){
				for (Unit unit : connector.getMyUnits()){
					if (unit.getType() == toTrain && !unit.isCompleted())
						training = unit;
				}
			}
			/* Como la unidad tarda un poco en aparecer, primero nos fijamos
			 * en el edificio hasta que se tome la unidad */
			if (training == null){
				// Vemos que el edificio esta entrenando una unidad
				if (edificio != null && edificio.isTraining())
					return 0;
				else
					return 1;
			}
			// Se ha tomado la unidad ya
			else {
				// Si se completa se devuelve success y se vacian las variables
				if (training.isCompleted()){
					training = null;
					edificio = null;
					toTrain = null;
					return 1;
				}
				else
					return 0;
			}
		} catch (Exception e) {
			System.out.println("Error comprobando estado del entrenamiento");
			return -1;
		}
	}
}
