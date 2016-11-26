package org.iaie.tutorial3;

import org.iaie.btree.util.GameHandler;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;


public class BehaviourTree extends GameHandler{

	private Unit worker = null;
	private Unit refinery = null;
	private Unit conBuilding = null; 
	
	public BehaviourTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	
	/**
	 * Metodo para liberar al trabajador tras hacer una tarea
	 */
    private void liberar(){
    	worker = null;
    }
    
    
    /**
     * Metodo para seleccionar un trabajador
     * @param idWorker id del trabajador
     */
	public void selectWorker(int idWorker){
		worker = connector.getUnit(idWorker);
	}
    
	
	/**
	 * Metodo que buscara un trabajador libre si existe 
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
	
	
	/**
	 * Pone una unidad a recoger mineral siempre que la distancia no sea mayor a 300
	 * @return 1 si la orden ha sido mandada correctamente, -1 si no ha podido realizarse, -2 si hay algun error 
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
                    if (distance < 300) {
                        // Se ejecuta el comando para enviar a la unidad a recoger minerales del deposito seleccionado.
                        worker.rightClick(minerals, false);
                        liberar();
                        // Se añade el deposito a la lista de depositos en uso.
                        //this.claimedMinerals.add(minerals);
                        return 1;
                    }
                }
            }
			// No se ha podido mandar al trabajador
			return -1;
		} catch (Exception e){
			// Error al ejecutar
			return -2;
		}
	}
	
	
	/**
	 * Pone una unidad a recoger vespeno tras construir una refineria si no existe
	 * @return 1 si la orden ha sido mandada correctamente, -1 si no ha podido realizarse, -2 si hay algun error 
	 */
	public int collectGas(){
		try{
			// Si la refineria no esta contruida y hay recursos se construye
			if (refinery == null && connector.getSelf().getMinerals() >= 100){
				Position pos = null;
				// Comprobamos que es una unidad neutral
	            for (Unit vespeno : connector.getNeutralUnits()){
	            	// Comprobamos que es un geyser de vespeno
	            	if (vespeno.getType() == UnitTypes.Resource_Vespene_Geyser && worker.getDistance(vespeno) < 300){
	            		// Cogemos la posicion del vespeno para construir el edificio encima
	            		pos = vespeno.getTilePosition();
	            		break;
	            	}
	            }
	            boolean a = crearEdificio(UnitTypes.Terran_Refinery, pos);
				if (a){
					// Navegamos nuestras unidades
					for (Unit unit : connector.getMyUnits()){
						// Buscamos que sea una refineria
						if (unit.getType().isRefinery()){
							refinery = unit;
							liberar();
							return 1;
						}
					}
				}
			}
			// Si la refineria no esta construida pero tampoco hay recursos
			else if (refinery == null && connector.getSelf().getMinerals() >= 100){
				return -1;
			}
			else if (refinery != null){
				// Si ya esta contruida se manda al trabajador
				if (worker.rightClick(refinery.getBottomRight(), false)){
					liberar();
					return 1;
				}
				// Si falla al mandarlo
				else
					return -1;
			}
			return -1;
		}
		catch (Exception e){
			return -2;
		}
	}
	
	/**
	 * Comprueba que hay recursos suficientes para construir una unidad
	 * @return 1 si los hay, -1 si no los hay, -2 si hay algun error 
	 */
	public int checkResources(){
		try{
			if (connector.getSelf().getMinerals() > 50)
				return 1;
			else
				return -1;
		} catch (Exception e){
			return -2;
		}
	}
	
	/**
	 * Selecciona un edificio
	 * @return 1 si se ha seleccionado, -1 si no se ha seleccionado, -2 si hay algun error 
	 */
	public int chooseBuilding(){
		try {
			for (Unit unit : connector.getMyUnits()){
				// Buscamos que sea una refineria
				if (unit.getType() == UnitTypes.Terran_Command_Center){
					conBuilding = unit;
					return 1;
				}
			}
			return -1;
		} catch (Exception e){
			return -2;
		}
	}
	
	
	/**
	 * Entrena un terran en un edificio
	 * @return 1 si lo comienza a entrenar, -1 si no se comienza el entrenamiento, -2 si hay algun error 
	 */
	public int trainWorker(){
		try {
			if (crearUnidad(UnitTypes.Terran_SCV))
				return 1;
			else
				return -1;
		} catch (Exception e){
			return -2;
		}
	}
	
	
	/*** METODOS AUXILIARES **/
	/**
     * Metodo para entrenar una unidad en un edificio. 
     * 
     * Se introducen como parametros:
     * @param unidad	Tipo de unidad que se desea construir
     * @return 			True si se ha creado correctamente
     */
    public boolean crearUnidad(UnitType unidad){
    	return conBuilding.train(unidad);
    }
    
    
    /**
     * Metodo para la creacion de edificios por una unidad
     * 
     * Se introducen como parametros
     * @param edificio		Tipo de edificio a construir
     * @param pos			Posicion para la construccion
     * @return 				True si el edificio se ha creado correctamente, False si no es posible su creacion
     */
    public boolean crearEdificio(UnitType edificio, Position pos){
    	if (pos == null || edificio == null){
    		return false;
    	} else {
	    	if (connector.canBuildHere(pos, edificio, false)){
	    		return worker.build(pos, edificio);
	    	}
	    	return false;
    	}
    }
}
