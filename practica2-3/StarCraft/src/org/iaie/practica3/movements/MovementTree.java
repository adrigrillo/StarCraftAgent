package org.iaie.practica3.movements;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica3.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.UnitType.UnitTypes;
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
	 * Metodo que comprueba la posicion del jugador
	 * @return 1 si consigue, 0 si no se consigue, -1 si hay algun error
	 */
	public int checkPositionUnit(){
		try{
			if (unitChecked != null) {
				position = unitChecked.getPosition();
				if (position != null)
					return 1;
			}
			return 0;
		} catch (Exception e){
			return -1;
		}
	}
	
	
	/**
	 * Metodo que comprueba si hay una unidad disponible para explorar
	 * @return 1 si esta parada, 0 si se encuentra en movimiento y -1 si hay error
	 */
	public int checkStateUnit(){
		try{
			// Si no hay ninguno fijado
			if (unitChecked == null){
				// Navegamos nuestras unidades
				for (Unit unit : CtrlVar.workers){
					/* Buscamos que este libre, lo eliminamos de la lista de
					 * trabajadores disponibles y lo sustituimos con otro nuevo*/
					if  (unit.isIdle()){
						unitChecked = unit;
						CtrlVar.workers.remove(unit);
						CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
						return 1;
					}
				}
				return 0;
			}
			else {
				/*Consideramos que si la unidad esta en movimiento
				 * habra que esperar a que pare para poder volver moverla*/
				if(unitChecked.isMoving())
					return 0;
				else
					return 1;
			}
		} catch (Exception e){
			// Error al ejecutar
			return -1;
		}
	}
	
	
	/** 
	 * Utilizaremos este arbol para explorar el mapa en esta practica
	 * para ello mandaremos al elegido a diferentes posiciones del mapa
	 * @return 1 si se encuentra una posicion y se mueve, 0 si no se encuentra o no se manda, -1 error
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
