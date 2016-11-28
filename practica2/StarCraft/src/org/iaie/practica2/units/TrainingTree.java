package org.iaie.practica2.units;

import java.util.ArrayList;
import java.util.Iterator;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

public class TrainingTree extends GameHandler {
	
	private UnitType toTrain = null;
	private Unit training = null;
	private Unit edificio = null;

	public TrainingTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	
	/**
	 * Esta metodo se utilizara para comprobar si es necesario la construccion de
	 * mas supply depots, aunque tambien como control para constuir
	 * @return 	1 si hay poblacion suficiente para construir una unidad, 0 si es necesario esperar
	 * 			a un supply depot, -1 si hay error
	 */
	public int checkPopulation(){
		try {
			// Ordenamos construir el supply depot con antelacion como prioridad una vez
			if (connector.getSelf().getSupplyUsed() + 2 == connector.getSelf().getSupplyTotal() && !CtrlVar.buildqueue.contains( UnitTypes.Terran_Supply_Depot))
				CtrlVar.buildqueue.add(0, UnitTypes.Terran_Supply_Depot);
			// Si no hay espacio no se puede entrenar
			else if (connector.getSelf().getSupplyUsed() == connector.getSelf().getSupplyTotal())
				return 0;
			return 1;
		} catch (Exception e) {
			return -1;
		}
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
	 * @return 1 si se manda construir correctamente, 0 si falla y -1
	 */
	public int trainUnit(){
		try {
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
			if (edificio.train(toTrain))
				return 1;
			else
				return 0;
		} catch (Exception e) {
			return -1;
		}
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
				// Si se completa se devuelve success y se vacian las variables y se elimina de la cola
				if (training.isCompleted()){
					// Anyadimos la unidad a la lista que pertenezca
					if (training.getType().isWorker())
						CtrlVar.workers.add(training);
					else
						CtrlVar.militaryUnits.add(training);
					CtrlVar.trainqueue.remove(toTrain);
					training = null;
					edificio = null;
					toTrain = null;
					return 1;
				}
				else
					return 0;
			}
		} catch (Exception e) {
			return -1;
		}
	}
}
