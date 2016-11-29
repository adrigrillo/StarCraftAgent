package org.iaie.practica2.militarControl;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;

public class MilitarTree extends GameHandler {

	private Unit militar = null;
	private int estado = 0;
	
	public MilitarTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	
	/**
	 * Dependiendo del estado del jugador la variable estado tomara distintos valores:
	 * 1: atacando
	 * 2: defendiendo
	 * 3: atacado
	 */
	public int checkState(){
		try{
			for (Unit unit : connector.getMyUnits()){
				if(unit.isAttacking()){
					estado = 1;
					return 1;
				}else if(unit.isDefenseMatrixed()){
					estado = 2;
					return 2;
				}/*elseif(unit.isBeingHealed())*/
				/*¿Como poner si ha sido atacado?*/
			}
			return -1;

		} catch (Exception e){
			return -2;
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
						estado = 1;
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
					estado = 2;
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
