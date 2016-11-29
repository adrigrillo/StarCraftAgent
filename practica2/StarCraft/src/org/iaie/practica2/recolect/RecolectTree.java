package org.iaie.practica2.recolect;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType.UnitTypes;


public class RecolectTree extends GameHandler{

	private Unit worker = null;

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
	public int refineryBuilt(){
		try {
			if (CtrlVar.refinery.size() == 0){
				// Miramos si hay alguna refineria, para encolarla en la lista de construccion si no es asi
				for (Unit unit : CtrlVar.buildings){
					if (unit.getType() == UnitTypes.Terran_Refinery){
						CtrlVar.refinery.add(unit);
					}
				}
				// Si la refineria no esta construida se encola
				if (CtrlVar.refinery.size() == 0 && !CtrlVar.buildqueue.contains(UnitTypes.Terran_Refinery))
					CtrlVar.buildqueue.add(0, UnitTypes.Terran_Refinery);
				// Delvovemos error
				return 0;
			}
			return 1;
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
			for (Unit minerals : connector.getNeutralUnits()) {
                // Se comprueba si la unidad es un deposito de minerales                                 
                if (minerals.getType().isMineralField()) {
                    // Se calcula la distancia entre la unidad y el deposito de minerales
                    double distance = worker.getDistance(minerals);
                    // Se comprueba si la distancia entre la unidad y el deposito de minerales es menor a 300.
                    if (distance < 700) {
                        // Se ejecuta el comando para enviar a la unidad a recoger minerales del deposito seleccionado.
                        worker.rightClick(minerals, false);
                        worker = null;
                        // Se añade el deposito a la lista de depositos en uso.
                        CtrlVar.refreshClaimed(connector);
                        return 1;
                    }
                }
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
			Unit refineria = CtrlVar.refinery.get((int) Math.random() * CtrlVar.refinery.size());
			if (worker.rightClick(refineria, false)){
				if (!worker.isGatheringGas())
					return 0;
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
