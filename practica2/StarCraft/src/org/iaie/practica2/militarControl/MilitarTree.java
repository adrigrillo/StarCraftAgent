package org.iaie.practica2.militarControl;

import java.util.ArrayList;
import java.util.Iterator;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.ChokePoint;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;

public class MilitarTree extends GameHandler {

	private Unit militar = null;
	private Position beingAtacked = null;
	private ArrayList<Position> patrullas = new ArrayList<>(); 
	
	public MilitarTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	
	/**
	 * Este metodo sera utilizado para comprobar el estado en el que se encuentra el jugador
	 * @return 1 si se encuentra patrullando o atacando, 0 si esta siendo atacado, -1 error
	 */
	public int checkState(){
		try{
			// Miramos si estamos siendo atacados
			for (Unit unit : connector.getMyUnits()){
				if (unit.isUnderAttack()){
					beingAtacked = unit.getPosition();
					return 0;
				}
			}
			// El resto esta atacando o patrullando
			return 1;
		} catch (Exception e){
			return -1;
		}
	}
	
	
	/**
	 * Si nos atacan alguna unidad mandamos a las unidades militares a defenderla
	 * @return
	 */
	public int defenseMode(){
		try{
			// Defendemos si algo esta siendo atacado mandandolo a la posicion
			for (Unit militar : CtrlVar.militaryUnits){
				// Si esta atacando no se le ordena
				if (!militar.isAttacking())
					if (!militar.move(beingAtacked, false))
						return 0;
			}
			return 1;
		} catch (Exception e){
			return -2;
		}
	}
	
	/** 
	 * Este metodo sera utilizado para mandar patrullar a las unidades
	 * cuando esten sin hacer nada y defender cuando algun edificio o unidad este
	 * siendo atacadaç
	 * @return 1 si las unidades se han mandado a patrullar, 0 si ha habido errores, -1 error
	 */
	public int ordenarUnidades(){
		try {
			// Sacamos la posiciones de patrulla
			if (patrullas.isEmpty()){
				// Obtenemos los chokepoints de la region donde empezamos y los anyadimos a la lista de posiciones para patrullar
				Iterator<ChokePoint> chockepoints = connector.getMap().getRegion(CtrlVar.buildings.get(0).getPosition()).getChokePoints().iterator();
				while (chockepoints.hasNext()){
					patrullas.add(((ChokePoint) chockepoints.next()).getCenter());
				}
			}
			if (!patrullas.isEmpty()){
				// Vamos a mandar patrullar a las unidades que esten paradas
				for (Unit unit : CtrlVar.militaryUnits){
					if (unit.isIdle())
						unit.patrol(patrullas.get((int) Math.random() * patrullas.size()), false);
				}
				return 1;
			}
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	public int attackUnits(){
		try{
			// Navegamos nuestras unidades
			for (Unit unit : CtrlVar.militaryUnits){
				// Buscamos que sea un militar y esten libres
				if (unit.getType().isAttackCapable() && unit.isIdle()){
					selectMilitar(unit.getID());
					// Buscamos las unidades enemigas
					for (Unit unitEnemy : connector.getEnemyUnits()){
						militar.attack(unitEnemy, true);
						return 1;
					}
				}
			}
			return -1;
		} catch (Exception e){
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
	public void selectMilitar(int idWorker){
		militar = connector.getUnit(idWorker);
	}
	
}
