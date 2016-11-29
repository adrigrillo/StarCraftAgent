package org.iaie.practica2.militarControl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.ChokePoint;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;

public class MilitarTree extends GameHandler {

	private Unit militar = null;
	private ArrayList<Position> patrullas = new ArrayList<>(); 
	
	public MilitarTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	/** 
	 * Este metodo sera utilizado para mandar patrullar a las unidades
	 * cuando esten sin hacer nada y defender cuando algun edificio o unidad este
	 * siendo atacada
	 */
	public void ordenarUnidades(){
		try {
			// Sacamos la posiciones de patrulla
			if (patrullas.isEmpty()){
				// Obtenemos los chokepoints de la region donde empezamos y los anyadimos a la lista de posiciones para patrullar
				Iterator<ChokePoint> chockepoints = connector.getMap().getRegion(CtrlVar.buildings.get(0).getPosition()).getChokePoints().iterator();
				while (chockepoints.hasNext()){
					patrullas.add(((ChokePoint) chockepoints.next()).getCenter());
				}
			}
			// Vamos a mandar patrullar a las unidades que esten paradas
			for (Unit unit : CtrlVar.militaryUnits){
				if (unit.isIdle())
					unit.patrol(patrullas.get((int) Math.random() * patrullas.size()), false);
			}
			// Defendemos si algo esta siendo atacado mandandolo a la posicion
			for (Unit unit : connector.getMyUnits()){
				if (unit.isUnderAttack()){
					for (Unit militar : CtrlVar.militaryUnits){
						militar.move(unit.getPosition(), false);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error al asignar alguna tarea");
		}
	}
	
	/**
	 * Dependiendo del estado del jugador la variable estado tomara distintos valores:
	 * 1: atacando
	 * 2: defendiendo
	 * 3: atacado
	 */
	public int checkState(){
		try{
			/* Vamos a mandar patrullar a las unidades que esten paradas o defender si estamos
			 * siendo atacados */
			ordenarUnidades();
			int atacando = 0;
			int defendiendo = 0;
			int atacado = 0;
			for (Unit unit : CtrlVar.militaryUnits){
				if (unit.isAttacking()){
					atacando++;
				}
				else if (unit.isPatrolling()){
					defendiendo++;
				}
				else if (unit.isUnderAttack()){
					atacado++;
				}
			}
			if (atacado > atacando && atacado > defendiendo)
				return 1;
			if (defendiendo > atacando && defendiendo > atacado)
				return 1;
			// Si esta atacando se deja atacar
			if (atacando > defendiendo && atacando > atacado)
				return 0;
			return 1;
		} catch (Exception e){
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
	/*No se si hay una funcion que sea para poner en modo defensa al jugador*/
	public int defenseMode(){
		try{
			// Navegamos nuestras unidades
			for (Unit unit : connector.getMyUnits()){
				if (unit.isUnderAttack()){
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
