package org.iaie.practica3.recolect;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica3.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType.UnitTypes;


public class RecolectTree extends GameHandler{

	private Unit worker = null;
	private Unit refinery = null;

	public RecolectTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	
	/**
	 * Metodo para comprobar la distribucion de los trabajadores a la hora de recolectar
	 * Se busca un 70% en minerales y un 30% en vespeno
	 * @return 1 si se necesita gas, 0 si minerales, -1 error
	 */
	public int checkDistribution(){
		try{
			int minerals = 0;
			// Comprobamos cuantos estan recogiendo minerales y cuantos estan recogiendo gas
			for (Unit unit: CtrlVar.workers){
				if (unit.isGatheringMinerals() == true)
					minerals += 1;
			}
			// Establecemos un 70% para recoger materiales y un 30% para vespeno
			if (((double) minerals/CtrlVar.workers.size()) > 0.7)
				return 1;
			else
				return 0;
		}
		catch (Exception e){
			return -1;
		}
	}
	
	
	/** 
	 * Comprueba si hay alguna refineria construida
	 * @return 1 si esta construida, 0 si no lo esta y -1 si hay algun error
	 */
	public int refineryControl(){
		try {
			// Comprobación cuando no existe ninguna refineria construir la primera
			if (CtrlVar.refinery.size() == 0){
				// Puede que nos destruyan los edificios y nos repongamos
				CtrlVar.refinery.clear();
				// Miramos si hay alguna refineria, para encolarla en la lista de construccion si no es asi
				for (Unit unit : CtrlVar.buildings){
					if (unit.getType() == UnitTypes.Terran_Refinery){
						CtrlVar.refinery.put(unit, 0);
					}
				}
				// Si la refineria no esta construida se encola
				if (CtrlVar.refinery.size() == 0 && !CtrlVar.buildqueue.contains(UnitTypes.Terran_Refinery))
					CtrlVar.buildqueue.add(0, UnitTypes.Terran_Refinery);
				// Delvovemos error
				return 0;
			}
			// En el caso de haber una, se controla la construcción de mas
			else {
				/* Vamos a establecer un maximo de 3 trabajadores en la primera refineria
				 * si tenenemos mas vespeno descubierto */
				if (CtrlVar.refinery.size() == 1){
					// Comprobamos el vespeno descubierto
					if (CtrlVar.claimedVespene.size() > 1){
						for (Unit refineria : CtrlVar.refinery.keySet()){
							// Si hay tres trabajadores se manda crear una nueva refineria
							if (CtrlVar.refinery.get(refineria) >= 3){
								if (!CtrlVar.buildqueue.contains(UnitTypes.Terran_Refinery))
									CtrlVar.buildqueue.add(0, UnitTypes.Terran_Refinery);
								return 0;
							}
							else {
								refinery = refineria;
								return 1;
							}
						}
					}
					// Si tiene menos de tres trabajadores se selecciona para mandar al worker
					else {
						for (Unit refineria : CtrlVar.refinery.keySet()){
							refinery = refineria;
							return 1;
						}
					}
				}
				// Si tenemos mas de una refineria
				else if (CtrlVar.refinery.size() > 1) {
					// cogemos una al azar que no sea la primera
					int random = (int) Math.random() * (CtrlVar.refinery.size() - 1);
					int i = 0;
					for (Unit refineria : CtrlVar.refinery.keySet()){
						if (i == random){
							refinery = refineria;
							return 1;
						}
						i++;
					}
				}
			}
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	/**
	 * Metodo que buscara un trabajador libre si existe para recoger minerales 
	 * @return Id del trabajador si existe, -1 si no existe, -2 si hay algun error
	 */
	public int freeWorkerAvailableMineral(){
		try{
			// Navegamos nuestras unidades
			for (Unit unit : CtrlVar.workers){
				// Buscamos que sea un trabajador y esten libres
				if (unit.getType().isWorker() && unit.isIdle()){
					worker = connector.getUnit(unit.getID());
					return unit.getID();
				}
			}
			return -1;
		} catch (Exception e){
			return -2;
		}
	}
	
	
	/**
	 * Metodo que buscara un trabajador para recoger gas
	 * @return Id del trabajador si existe, -1 si no existe, -2 si hay algun error
	 */
	public int freeWorkerAvailableGas(){
		try{
			// Navegamos nuestras unidades
			for (Unit unit : CtrlVar.workers){
				// Buscamos que sea un trabajador y esten libres
				if (unit.getType().isWorker() && unit.isIdle()){
					worker = connector.getUnit(unit.getID());
					return unit.getID();
				}
			}
			/* Si no hay ningun trabajador disponible se coge uno que este
			 * tomando minerales, ya que hay menos para conseguir gas y hay que
			 * dar prioridad */
			for (Unit unit : CtrlVar.workers){
				// Buscamos que sea un trabajador y que recoja minerales (hay mas)
				if (unit.getType().isWorker() && unit.isGatheringMinerals()){
					worker = connector.getUnit(unit.getID());
					// Mandamos crear otro scv por el que quitamos
					CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
					return unit.getID();
				}
			}
			return -1;
		} catch (Exception e){
			return -2;
		}
	}
	
	
	/**
	 * Pone una unidad a recoger mineral siempre que la distancia no sea mayor a 700
	 * @return 1 si la orden ha sido mandada correctamente, 0 si no ha podido realizarse, -1 si hay algun error
	 */
	public int collectMineral(){
		try{
			// Obtenemos los minerales
			double dist = 2000;
			Unit mineral = null;
			for (Unit minerals : CtrlVar.claimedMinerals) {
                // Se calcula la distancia entre la unidad y el deposito de minerales
                double distance = worker.getDistance(minerals);
                // Tomamos la mina mas cercana
                if (distance < dist){
                	dist = distance;
                	mineral = minerals;
                }
			}
            if (mineral != null) {
                // Se ejecuta el comando para enviar a la unidad a recoger minerales del deposito seleccionado.
                worker.rightClick(mineral, false);
                worker = null;
                // Se anyade el deposito a la lista de depositos en uso.
                CtrlVar.refreshClaimed(connector);
                return 1;
            }
			// No se ha podido mandar al trabajador
			return 0;
		} catch (Exception e){
			// Error al ejecutar
			return -1;
		}
	}
	
	
	/**
	 * Pone una unidad a recoger vespeno
	 * @return 1 si la orden ha sido mandada correctamente, 0 si no ha podido realizarse, -1 si hay algun error 
	 */
	public int collectGas(){
		try{
			// Si ya esta contruida se manda al trabajador a una de las que haya
			if (worker.rightClick(refinery, false)){
				// Anyadimos el trabajador a la refineria
				CtrlVar.refinery.put(refinery, CtrlVar.refinery.get(refinery) + 1);
				refinery = null;
				return 1;
			}
			// Si falla al mandarlo
			else
				return 0;
		}
		catch (Exception e){
			return -1;
		}
	}
}
