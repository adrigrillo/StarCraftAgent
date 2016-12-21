package org.iaie.practica3.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica3.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

public class TrainingTree extends GameHandler {
	
	private UnitType toTrain = null;
	private ArrayList<UnitType> typeTraining = new ArrayList<UnitType>(Arrays.asList(null, null, null, null));
	private ArrayList<Unit> training = new ArrayList<Unit>(Arrays.asList(null, null, null, null));
	private ArrayList<Unit> edificios = new ArrayList<Unit>(Arrays.asList(null, null, null, null));
	private ArrayList<Integer> estadoCola = new ArrayList<Integer>(Arrays.asList(null, null, null, null));
	private int limit = 4;

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
			// Lo podemos utilizar para aumentar poco a poco la poblacion
			if (Collections.frequency(CtrlVar.trainqueue, UnitTypes.Terran_SCV) > 1 || CtrlVar.workers.size() > 11){
				CtrlVar.trainqueue.removeAll(Collections.singleton(UnitTypes.Terran_SCV));
			}
			if ((int)Math.floor(Math.random()* 1000) == 0 && CtrlVar.workers.size() < 12)
				CtrlVar.trainqueue.add(0, UnitTypes.Terran_SCV);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Firebat);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Vulture);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Medic);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Ghost);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Siege_Tank_Tank_Mode);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Goliath);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Wraith);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Dropship);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Science_Vessel);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Battlecruiser);
			if ((int)Math.floor(Math.random()* 1000) == 0)
				CtrlVar.trainqueue.add(UnitTypes.Terran_Valkyrie);
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
	 * @return 1 si los edificios estan disponibles, 0 si no lo estan, -1 error
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
						//Anyadimos el edificio que falta a la cola de construccion
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
			Unit building = null;
			for (Unit unit : posEdificios){
				if (unit.getTrainingQueueSize() < cola){
					cola = unit.getTrainingQueueSize();
					building = unit;
				}
			}
			// Comprobamos que la lista no esta llena
			if (Collections.frequency(edificios, null) > 0){
				// Tras elegir el mejor edificio se contruye la unidad
				if (building.train(toTrain)){
					// Lo anyadimos a la lista de edificios que esta trabajando
					for (int i = 0; i < edificios.size(); i++){
						if (edificios.get(i) == null){
							typeTraining.set(i, toTrain);
							edificios.set(i, building);
							break;
						}
					}
					return 1;
				}
				else
					return 0;
			}
			else
				return 0;
		} catch (Exception e) {
			return -1;
		}
    }
	
	/**
	 * Comprueba el estado del array de entrenamiento
	 * @return 1 si se pueden entrenar mas unidades, 0 si esta en proceso, -1 si hay algun error
	 */
	public int trainingState(){
		try {
			/* Aqui ahora tendremos que revisar el array completo para controlar
			 * el proceso de entrenamiento */
			for (int i = 0; i < edificios.size(); i++){
				/* Comprobamos que la unidad se esta construyendo para guardarla
				 * y asi comprobar posteriormente cuando se completa */
				if (edificios.get(i) != null && training.get(i) == null){
					for (Unit unit : connector.getMyUnits()){
						if (unit.getType() == typeTraining.get(i) && !unit.isCompleted()){
							// Se comprueba que esta unidad no sea igual que otra anterior
							// en la cola
							boolean diferente = true;
							for (Unit otras : training){
								if (otras != null && otras.getID() == unit.getID()){
									diferente = false;
									break;
								}
							}
							if (diferente)
								training.set(i, unit);
						}
					}
				}
				/* Como la unidad tarda un poco en aparecer, primero nos fijamos
				 * en el edificio hasta que se tome la unidad */
				if (training.get(i) == null && edificios.get(i) != null){
					// Vemos que el edificio esta entrenando una unidad
					if (edificios.get(i).isTraining())
						estadoCola.set(i, 0);
					else
						estadoCola.set(i, 1);
				}
				// Se ha tomado la unidad ya
				else if (training.get(i) != null){
					// Si se completa se devuelve success y se vacian las variables y se elimina de la cola
					if (training.get(i).isCompleted()){
						// Anyadimos la unidad a la lista que pertenezca
						if (training.get(i).getType().isWorker())
							CtrlVar.workers.add(training.get(i));
						else
							CtrlVar.militaryUnits.add(training.get(i));
						CtrlVar.trainqueue.remove(training.get(i).getType());
						training.set(i, null);
						edificios.set(i, null);
						typeTraining.set(i, null);
						estadoCola.set(i, null);
					}
					else
						estadoCola.set(i, 2);
				}
			}
			// Si hay espacio se puede encolar mas
			if (edificios.size() <= limit){
				CtrlVar.refreshUnits(connector);
				return 1;
			}
			else
				return 0;
		} catch (Exception e) {
			return -1;
		}
	}
}
