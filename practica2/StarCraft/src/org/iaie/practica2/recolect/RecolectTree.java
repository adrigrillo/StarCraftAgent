package org.iaie.practica2.recolect;

import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.CtrlVar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;


public class RecolectTree extends GameHandler{

	private Unit worker = null;
	private Unit refinery = null;
	private Unit conBuilding = null; 
	
	public RecolectTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}
	
	/**
	 * Metodo para comprobar la distribucion de los trabajadores a la hora de recolectar
	 * Se busca un 70% en minerales y un 30% en vespeno
	 * @return
	 */
	public int checkDistribution(){
		try{
			int minerals = 0;
			// Comprobamos cuantos estan recogiendo minerales y cuantos estan recogiendo gas
			for (Unit unit: CtrlVar.workers){
				if (unit.isGatheringMinerals() == true)
					minerals += 1;
			}
			// Establecemos un 70% para recoger materiales y un 30% para vespeno
			if (((double) minerals/CtrlVar.workers.size()) < 70)
				return 1;
			else
				return 0;
		}
		catch (Exception e){
			return -1;
		}
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
			for (Unit unit : CtrlVar.workers){
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
                        CtrlVar.claimedMinerals.add(minerals);
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
	
	
	/**
	 * Pone una unidad a recoger vespeno tras construir una refineria si no existe
	 * @return 1 si la orden ha sido mandada correctamente, -1 si no ha podido realizarse, -2 si hay algun error 
	 */
	public int collectGas(){
		try{
			if (refinery == null){
				// Miramos si hay alguna refineria, para encolarla en la lista de construccion si no es asi
				for (Unit unit : CtrlVar.buildings){
					if (unit.getType() == UnitTypes.Terran_Refinery){
						refinery = unit;
						break;
					}
				}
				// Si la refineria no esta construida se encola
				if (refinery == null){
					CtrlVar.buildqueue.add(UnitTypes.Terran_Refinery);
					return 0;
				}
			}
			else {
				// Si ya esta contruida se manda al trabajador
				if (worker.rightClick(refinery.getBottomRight(), false)){
					liberar();
					return 1;
				}
				// Si falla al mandarlo
				else
					return 0;
			}
			return 0;
		}
		catch (Exception e){
			return -1;
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
