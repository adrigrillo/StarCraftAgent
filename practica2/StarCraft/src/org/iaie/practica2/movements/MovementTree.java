package org.iaie.practica2.movements;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;

public class MovementTree extends GameHandler{

	private Unit worker = null;
	private Position position = null;

	
	public MovementTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	
	/**
	 * Metodo que buscara un trabajador libre si existe para recoger minerales 
	 * @return Id del trabajador si existe, -1 si no existe, -2 si hay algun error
	 */
	public int checkPositionUnit(){
		try{
			// Navegamos nuestras unidades
			for (Unit unit : CtrlVar.workers){
				// Buscamos que sea un trabajador y esten libres
				if (unit.getType().isWorker() && unit.isIdle()){
					selectWorker(unit.getID());
					if(worker.getPosition().isValid()){
						return worker.getID();
					}
				}
			}
			return -1;
		} catch (Exception e){
			return -2;
		}
	}
	
	
	public int checkStateUnit(){
		try{
			selectWorker(checkPositionUnit());
			if(!worker.isMoving()){
					return 1;
			}else{
				/*Consideramos que si la unidad esta en movimiento
				 * habrá que esperar a que pare para poder volver moverla*/
				return -1;
			}
		} catch (Exception e){
			// Error al ejecutar
			return -2;
		}
	}
	
	public int moveUnit (){
		try{
			worker = connector.getUnit(checkPositionUnit());
			if(position.isValid()){
				worker.move(position,false);
					return 1;
			}else{
				return -1;
			}
		} catch (Exception e){
			// Error al ejecutar
			return -2;
		}
	}
	
	
	/*************************************************
	 * 			METODOS AUXILIARES
	 ************************************************/
    
    /**
     * Metodo para seleccionar un trabajador
     * @param idWorker id del trabajador
     */
	public void selectWorker(int idWorker){
		worker = connector.getUnit(idWorker);
	}
}
