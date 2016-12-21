package org.iaie.practica3.construction;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica3.CtrlVar;
import org.iaie.practica3.InfluenceMap;
import org.iaie.practica3.MapHandler;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Position.PosType;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

public class ConstructionTree extends GameHandler{

	private Unit worker = null;
	private UnitType toBuild = null;
	private Position pos = null;
	private Unit building = null;
	
	public ConstructionTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	/**
	 * Comprueba que hay recursos suficientes para construir el edificio
	 * @return 1 si los hay, 0 si no los hay, -1 si hay algun error 
	 */
	public int checkBuildingsResources(){
		try {
			// Comprobamos que la cola no este vacia
			if (!CtrlVar.buildqueue.isEmpty()){
				for (UnitType unit : CtrlVar.buildqueue){
					boolean mineral = false;
					boolean gas = false;
					// Comprobamos si tenemos los recursos necesarios
					if (unit.getMineralPrice() <= connector.getSelf().getMinerals())
						mineral = true;
					if (unit.getGasPrice() <= connector.getSelf().getGas())
						gas = true;
					if (mineral && gas){
						toBuild = unit;
						return 1;
					}
				}
			}
			return 0;
		} catch (Exception e){
			return -1;
		}
	}
	
	
	/**
	 * Este metodo elige la posicion para construir el edificio. Si es una refineria busca geiseres,
	 * si es un supply depot lo crea cerca de una mina y el resto de edificios los crea en torno
	 * al centro de mando principal
	 * @return 1 si encuentra la posicion, 0 si no la encuentra y -1 si hay error
	 */
	public int selectPosition(){
		try {
			// Si es una refineria
			if (toBuild == UnitTypes.Terran_Refinery){
				// Buscamos en los minerales descubiertos
				for (Unit vespeno : CtrlVar.claimedVespene){
					// Comprobamos que no tengamos una refineria en esa posicion ya
					if (CtrlVar.refinery.size() > 0){
						for (Unit ref : CtrlVar.refinery.keySet()){
							if (vespeno.getPosition().getBDistance(ref.getPosition()) > 5){
			            		// Comprobamos que no estan en el otro punto del mapa
			            		if (worker.getDistance(vespeno) < 5000){
			            			// Cogemos la posicion del vespeno para construir el edificio encima
			            			pos = vespeno.getTilePosition();
				            		if (pos != null){
					            		return 1;
				            		}
				            		else
				            			return 0;
			            		}
				            }
						}
					}
					else {
						if (worker.getDistance(vespeno) < 700){
							pos = vespeno.getTilePosition();
		            		if (pos != null){
			            		return 1;
		            		}
		            		else
		            			return 0;
						}
					}
	            }
			}
			else if (toBuild == UnitTypes.Terran_Supply_Depot){
				// Lo construimos cerca de un vespeno 'cercano'
				Unit vespeno = null;
				while (vespeno == null){
					Unit valid = CtrlVar.claimedVespene.get((int) Math.random()*CtrlVar.claimedVespene.size());
					if (valid.getDistance(worker) < 2500)
						vespeno = valid;
				}
				pos = MapHandler.searchPointToBuild(connector, vespeno.getPosition(), toBuild);
				if (pos != null)
					return 1;
				else
        			return 0;
			}
			else {
				// Lo construimos en base a la unidad elegida
				pos = MapHandler.searchPointToBuild(connector, worker.getPosition().makeValid(), toBuild);
				if (pos != null)
					return 1;
				else
					return 0;
			}
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	/**
	 * Metodo que buscara un trabajador libre si existe, si no toma uno que este picando
	 * que hay mas para despues volver a picar
	 * @return Id del trabajador si existe, -1 si no existe, -2 si hay algun error
	 */
	public int freeWorkerAvailable(){
		try{
			// Navegamos nuestras unidades
			for (Unit unit : CtrlVar.workers){
				// Buscamos que sea un trabajador y esten libres
				if (unit.getType().isWorker() && unit.isIdle()){
					worker = connector.getUnit(unit.getID());
					return unit.getID();
				}
			}
			// Si no conseguimos ningun trabajador cogemos uno al azar
			int count = 0;
			for (Unit unit : CtrlVar.workers){
				if (unit.getType().isWorker() && unit.isGatheringMinerals()){
					count++;
				}
			}
			int ramdon = (int) Math.floor(Math.random() * count);
			int i = 0;
			for (Unit unit : CtrlVar.workers){
				// Buscamos que sea un trabajador y que recoja minerales (hay mas)
				if (unit.getType().isWorker() && unit.isGatheringMinerals()){
					if (i == ramdon){
						worker = connector.getUnit(unit.getID());
						return unit.getID();
					}
					i++;
				}
			}
			return -1;
		} catch (Exception e){
			return -2;
		}
	}
	

	/**
	 * Metodo que realiza la construccion del edificio
	 * @return 1 si se empieza a construir correctamente, 0 si hay algun error, -1 si falla todo
	 */
	public int buildBuilding(){
		try {
			// Comprobamos que la orden no se haya dado anteriormente
			if (toBuild != null && worker != null){
				// Comprobamos que puede construir en la posicion
	    		if (connector.canBuildHere(pos, toBuild, false)){
	    			// Mandamos construir
	    			if (worker.build(pos, toBuild))
	    				return 1;
	    			// Si falla devolvemos error
	    			else
	    				return 0;
	    		}
	    		else
	    			return 0;
			}
    		else
    			return 0;			
		} catch (Exception e) {
			return -1;
		}
	}
	
	
	/**
	 * Comprueba el estado del edificio que se esta construyendo
	 * @return 1 si se ha completado, 0 si esta en proceso, -1 si hay alg�n error
	 */
	public int buildState(){
		try {
			/* Comprobamos que la unidad se esta construyendo para guardarla
			 * y asi comprobar posteriormente cuando se completa */
			if (worker != null && building == null){
				building = worker.getBuildUnit();
			}
			/* Como la unidad tarda un poco en aparecer, primero nos fijamos
			 * en el scv hasta que se tome la unidad */
			if (building == null){
				// Vemos que trabajador esta trabajando
				if (worker != null && worker.isConstructing())
					return 0;
				else
					return 1;
			}
			// Se ha tomado la unidad ya
			else {
				// Si se completa se devuelve success y se vacian las variables y se elimina de la cola
				if (building.isCompleted()){
					// Si es correcto actualizamos el mapa
    				MapHandler.updateMap(connector, pos, new Position((pos.getBX() + (toBuild.getTileWidth()-1)), (pos.getBY() + (toBuild.getTileHeight()-1)), PosType.BUILD));
					CtrlVar.buildqueue.remove(toBuild);
					CtrlVar.refreshBuildings(connector);
					// Si es una refineria metemos al trabajador que se pone directo
					if (building.getType().equals(UnitTypes.Terran_Refinery)){
						CtrlVar.refinery.put(building, 1);
						InfluenceMap.updateMap(connector, building.getID(), false);
					}
					pos = null;
					worker = null;
					toBuild = null;
					building = null;
					return 1;
				}
				else
					return 0;
			}
		} catch (Exception e) {
			return -1;
		}
	}
}
