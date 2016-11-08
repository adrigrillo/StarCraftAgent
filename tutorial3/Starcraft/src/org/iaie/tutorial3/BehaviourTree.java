package org.iaie.tutorial3;

import org.iaie.btree.util.GameHandler;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;

public class BehaviourTree extends GameHandler{

	private Unit worker;
	
	public BehaviourTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	/**
	 * Método que buscara un trabajador libre si existe 
	 * @return Id del trabajador si existe, -1 si no existe, -2 si hay algún error
	 */
	public int freeWorkerAvailable(){
		try{
			// Navegamos nuestras unidades
			for (Unit unit : connector.getMyUnits()){
				// Buscamos que sea un trabajador y esten libres
				if (unit.getType().isWorker() && unit.isIdle()){
					return unit.getID();
				}
			}
			return -1;
		} catch (Exception e){
			return -2;
		}
	}
	
	public void selectWorker(int idWorker){
		this.worker = connector.getUnit(idWorker);
	}
	
	
	/**
	 * Pone una unidad a recoger mineral siempre que la distancia no sea mayor a 300
	 * @param unitId: Id de la unidad encargada de recoger el mineral
	 * @return 1 si la orden ha sido mandada correctamente, 0 si no ha podido realizarse, -1 si hay algun error 
	 */
	public int collectMineral(){
		try{
			// Obtenemos los minerales
			for (Unit minerals : connector.getNeutralUnits()) {
                // Se comprueba si la unidad es un deposito de minerales                                 
                if (minerals.getType().isMineralField()) {                                    
                    // Se calcula la distancia entre la unidad y el deposito de minerales
                    double distance = this.worker.getDistance(minerals);
                    // Se comprueba si la distancia entre la unidad y el deposito de minerales es menor a 300.
                    if (distance < 300) {
                        // Se ejecuta el comando para enviar a la unidad a recoger minerales del deposito seleccionado.
                        this.worker.rightClick(minerals, false);
                        // Se añade el deposito a la lista de depositos en uso.
                        //this.claimedMinerals.add(minerals);
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

}
