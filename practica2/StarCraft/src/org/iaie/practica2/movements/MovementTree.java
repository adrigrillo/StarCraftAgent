package org.iaie.practica2.movements;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.Position.PosType;

public class MovementTree extends GameHandler{

	private Unit unitChecked = null;
	private Position position = null;
	private Position destiny = null;

	
	public MovementTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	
	/**
	 * Metodo que buscara un trabajador libre si existe y guarda su posicion
	 * @return Id del trabajador si existe, -1 si no existe, -2 si hay algun error
	 */
	public int checkPositionUnit(){
		try{
			// Si no hay ninguno fijado
			if (unitChecked == null){
				// Navegamos nuestras unidades
				for (Unit unit : CtrlVar.workers){
					// Buscamos que sea un trabajador y esten libres
					if (unit.getType().isWorker() && unit.isIdle()){
						unitChecked = unit;
						if(unitChecked.getPosition().isValid()){
							position = unitChecked.getPosition();
							return 1;
						}
					}
				}
			}
			return 0;
		} catch (Exception e){
			return -1;
		}
	}
	
	
	/**
	 * Metodo que comprueba el estado en el que se encuentra la unidad examinada
	 * @return 1 si esta parada, 0 si se encuentra en movimiento y -1 si hay error
	 */
	public int checkStateUnit(){
		try{
			/*Consideramos que si la unidad esta en movimiento
			 * habrá que esperar a que pare para poder volver moverla*/
			if(unitChecked.isMoving())
				return 0;
			else
				return 1;
		} catch (Exception e){
			// Error al ejecutar
			return -1;
		}
	}
	
	
	/** 
	 * Utilizaremos este arbol para explorar el mapa en esta practica
	 * para ello mandaremos al elegido a diferentes posiciones del mapa
	 * @return
	 */
	public int moveUnit (){
		try{
			// Elegimos una posicion al azar del mapa para mandarle alli
			boolean posPosition = false;
			while (!posPosition){
				destiny = new Position(((int) (Math.random() * connector.getMap().getSize().getBX())), ((int) (Math.random() * connector.getMap().getSize().getBY())), PosType.BUILD);
				// Si es accesible la tomamos
				if (position != destiny && destiny.isValid())
					posPosition = true;
			}
			if (unitChecked.move(destiny, false))
				return 1;
			return 0;
		} catch (Exception e){
			// Error al ejecutar
			return -1;
		}
	}

}
