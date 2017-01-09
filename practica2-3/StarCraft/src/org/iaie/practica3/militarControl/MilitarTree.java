package org.iaie.practica3.militarControl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica3.CtrlVar;
import org.iaie.practica3.InfluenceMap;

import jnibwapi.ChokePoint;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.Position.PosType;
import jnibwapi.types.UnitType.UnitTypes;

public class MilitarTree extends GameHandler {

	private Position beingAtacked = null;
	private Unit objetive = null;
	private ArrayList<Position> patrullas = new ArrayList<>();
	private ArrayList<Unit> army = new ArrayList<>();
	
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
			// Si no tenemos unidades esto arbol no es valido
			if (CtrlVar.militaryUnits.isEmpty())
				return -1;
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
	 * Este metodo se utilizara para ver si es conveniente atacar o mejor patrullar
	 * @return 1 si se puede atacar, 0 si es mejor patrullar, -1 error
	 */
	public int attackPatrol(){
		try {
			// Vamos a atacar si nuestra influencia es mayor y el ejercito es
			// suficientemente grande
			if (InfluenceMap.getNivelControl() > 5 && CtrlVar.militaryUnits.size() > 30){
				return 1;
			}
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	/**
	 * Si nos atacan alguna unidad mandamos a las unidades militares a defenderla
	 * @return 1 si se mandan correctamente, 0 si hay algun error mandando las unidades, -1 error
	 */
	public int defenseMode(){
		try{
			/* Si estamos siendo atacados, comprobamos la influencia en esa posición
			 * no es demasiado alta para poder atacar y el numero de unidades que
			 * tenemos */
			Point defensa = new Point(beingAtacked.getBX(), beingAtacked.getBY());
			int influencia = -InfluenceMap.getInfluence(defensa);
			// Calculamos las posibilidades de triunfar. Si esta muy lejos, la influencia
			// es muy negativa o tenemos pocas unidades no se va a defender
			double capacidad = influencia + (CtrlVar.centroMando.get(0).getDistance(beingAtacked)/100) / CtrlVar.militaryUnits.size();
			if (capacidad > 20)
				return 0;
			// Defendemos si algo esta siendo atacado mandandolo a la posicion
			for (Unit militar : CtrlVar.militaryUnits){
				// Si esta atacando no se le ordena
				if (!militar.isAttacking())
					if (!militar.attack(beingAtacked, false))
						return 0;
			}
			return 1;
		} catch (Exception e){
			return -2;
		}
	}
	
	/** 
	 * Este metodo sera utilizado para mandar patrullar a las unidades
	 * cuando esten sin hacer nada
	 * @return 1 si las unidades se han mandado a patrullar, 0 si ha habido errores, -1 error
	 */
	public int orderPatrol(){
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
	
	
	/**
	 * Este metodo es utilizado para mandar unidades a atacar al enemigo, en concreto el centro de mando
	 * @return 1 Si se mandan correctamente, 0 si falla al mandarse, -1 error
	 */
	public int attackObjetive(){
		try{
			for (Unit enemy : connector.getAllUnits()){
				if (enemy.getType().isBuilding()){
					if (!CtrlVar.buildings.contains(enemy) && (enemy.getType() == UnitTypes.Terran_Command_Center || enemy.getType() == UnitTypes.Zerg_Infested_Command_Center || enemy.getType() == UnitTypes.Zerg_Hatchery)){
						objetive = enemy;
					}
				}
			}
			if (objetive != null){
				// Buscamos la posicion donde mas probabilidades tengamos de ganar
				Position mejor = null;
				int influencia = -100000;
				for (int x = objetive.getPosition().getBX() - 10; x < objetive.getPosition().getBX() + 10; x++){
					for (int y = objetive.getPosition().getBY() - 10; x < objetive.getPosition().getBY() + 10; y++){
						int inf = InfluenceMap.getInfluence(new Point(x, y));
						if (inf > influencia){
							influencia = inf;
							mejor = new Position(x, y, PosType.BUILD);
						}
					}
				}
				// Tomamos la mitad de las unidades para atacar
				int size = CtrlVar.militaryUnits.size();
				// Navegamos nuestras unidades			
				for (int i = 0; i < CtrlVar.militaryUnits.size(); i++){
					army.add(CtrlVar.militaryUnits.remove(i));
					if (i == size)
						break;
				}
				// Con el ejercito elegido atacamos
				for (Unit militar : army){
					if (!militar.attack(mejor, false))
						return 0;
				}
				return 1;
			}
			return 0; 
		} catch (Exception e){
			return -1;
		}
	}	
}
