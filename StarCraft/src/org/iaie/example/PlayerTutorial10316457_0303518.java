/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3m.es
 * 
 * Copyright 2015, Moises Martinez
 *
 * (Questions/bug reports now to be sent to MoisÃ©s MartÃ­nez)
 *
 * This file is part of IAIE.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Pelea.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ************************************************************************/

package org.iaie.example;

import java.util.HashSet;
import jnibwapi.BWAPIEventListener;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Position.PosType;
import jnibwapi.Unit;
import jnibwapi.types.TechType;
import jnibwapi.types.TechType.TechTypes;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;
import jnibwapi.types.UpgradeType;
import jnibwapi.types.UpgradeType.UpgradeTypes;
import org.iaie.Agent;
import org.iaie.tools.Options;


/**
 * Cliente de IA que utiliza JNI-BWAPI.
 * 
 * Creado por:
 * - Adrian Rodriguez Grillo: 100316457
 * - Paula Ruiz-Olivares de la Calle: 100303518
 * 
 * Clase Terran
 */
public class PlayerTutorial10316457_0303518 extends Agent implements BWAPIEventListener {
	
    /** Esta variable se usa para almacenar aquellos depositos de minerales 
     *  que han sido seleccionados para ser explotados por las unidades 
     *  recolectoras. */
    private final HashSet<Unit> claimedMinerals = new HashSet<>();

    /** Esta variable se utiliza para comprobar cuando debe ser generada un 
     *  nuevo overlord con el fin de poder entrenar otras unidades.*/
    private int supplyCap;
    
    /** Variable para que no haya más de dos personas en la refineria */
    int uRef = 0;
    
    /** Posicion del centro de mando */
    Position centroMando = null;
    
    /** Tarea a realizar */
    int refineria = 0;
    int supply = 0;
    int barraca = 0;

    public PlayerTutorial10316457_0303518() {            

        // Generacion del objeto de tipo agente

        // CreaciÃ³n de la superclase Agent de la que extiende el agente, en este mÃ©todo se cargan            
        // ciertas variables de de control referentes a los parÃ¡metros que han sido introducidos 
        // por teclado. 
        super();
        // CreaciÃ³n de una instancia del connector JNIBWAPI. Esta instancia sÃ³lo puede ser creada
        // una vez ya que ha sido desarrollada mediante la utilizaciÃ³n del patrÃ³n de diseÃ±o singlenton.
        this.bwapi = new JNIBWAPI(this, true);
        // Inicia la conexiÃ³n en modo cliente con el servidor BWAPI que estÃ¡ conectado directamente al videojuego.
        // Este proceso crea una conexiÃ³n mediante el uso de socket TCP con el servidor. 
        this.bwapi.start();
    }

    /**
     * Este evento se ejecuta una vez que la conexiÃ³n con BWAPI se ha estabilidad. 
     */
    @Override
    public void connected() {
        System.out.println("IAIE: Conectando con BWAPI");
    }

    /**
     * Este evento se ejecuta al inicio del juego una Ãºnica vez, en el se definen ciertas propiedades
     * que han sido leidas como parÃ¡metros de entrada.
     * Velocidad del juego (Game Speed): Determina la velocidad a la que se ejecuta el videojuego. Cuando el juego 
     * se ejecuta a mÃ¡xima velocidad algunas eventos pueden ser detectados posteriormente a su ejecuciÃ³n real. Esto
     * es debido a los retrasos en las comunicacion y el retardo producido por la tiempo de ejecuciÃ³n del agente. En 
     * caso de no introducir ningun valor el jugador 
     * InformaciÃ³n perfecta (Perfect informaciÃ³n): Determina si el agente puede recibir informaciÃ³n completa del 
     * juego. Se consedira como informaciÃ³n perfecta cuando un jugador tiene acceso a toda la informaciÃ³n del entorno, 
     * es decir no le afecta la niebla de guerra.
     * Entrada de usuarios (UserInput)
     */
    @Override
    public void matchStart() {

        System.out.println("IAIE: Iniciando juego");
        

        // Revisar. 
        // Mediante estÃ© metodo se puede obtener informaciÃ³n del usuario. 
        if (Options.getInstance().getUserInput()) this.bwapi.enableUserInput();
        // Mediante este mÃ©todo se activa la recepciÃ³n completa de informaciÃ³n.
        if (Options.getInstance().getInformation()) this.bwapi.enablePerfectInformation();
        // Mediante este mÃ©todo se define la velocidad de ejecuciÃ³n del videojuego. 
        // Los valores posibles van desde 0 (velocidad estÃ¡ndar) a 10 (velocidad mÃ¡xima).
        this.bwapi.setGameSpeed(Options.getInstance().getSpeed());

        // Iniciamos las variables de control
        // Se establece el contador de objetos a cero y se eliminan todas las
        // referencias previas a los objetos anteriormente aÃ±adidos.
        claimedMinerals.clear();
        supplyCap = 0;
        
        /** Calculamos la posicion del centro de comando */
        for (Unit unit : bwapi.getMyUnits()) {
            // Se compruba si existe alguna centro de control y si esta construido
            if (unit.getType() == UnitTypes.Terran_Command_Center && unit.isCompleted()) {
            	centroMando = unit.getPosition();
            }
        }
                
    }

    /**
     * Evento Maestro
     */
    @Override
    public void matchFrame() {

        String msg = "=";

        /* Comprobación de las investigaciones */
        for (TechType t : TechTypes.getAllTechTypes()) {
            if (this.bwapi.getSelf().isResearching(t)) {
                msg += "Investigando " + t.getName() + "=";
            }
            // Exclude tech that is given at the start of the game
            UnitType whatResearches = t.getWhatResearches();
            if (whatResearches == UnitTypes.None) {
                continue;
            }
            if (this.bwapi.getSelf().isResearched(t)) {
                msg += "Investigado " + t.getName() + "=";
            }
        }

        /* Comprobacion de actualizaciones sobre las unidades */
        for (UpgradeType t : UpgradeTypes.getAllUpgradeTypes()) {
            if (this.bwapi.getSelf().isUpgrading(t)) {
                msg += "Actualizando " + t.getName() + "=";
            }
            if (this.bwapi.getSelf().getUpgradeLevel(t) > 0) {
                int level = this.bwapi.getSelf().getUpgradeLevel(t);
                msg += "Actualizado " + t.getName() + " a nivel " + level + "=";
            }
        }

        this.bwapi.drawText(new Position(0, 20), msg, true);
        this.bwapi.getMap().drawTerrainData(bwapi);
        
        /* Comprobador de la creacion del supply depot y la barraca
         * Así solo creamos un edificio de cada uno
         */
        for (Unit unit : this.bwapi.getMyUnits()) {
        	if (unit.getType() == UnitTypes.Terran_Refinery && refineria == 0){
    			refineria++;
    			continue;
    		}
    		if (unit.getType() == UnitTypes.Terran_Supply_Depot && supply == 0){
    			supply++;
    			continue;
    		}
    		else if (unit.getType() == UnitTypes.Terran_Barracks && barraca == 0){
    			barraca++;
    			continue;
    		}
        }
        
        /* Proceso para recoger minerales */
        for (Unit unit : this.bwapi.getMyUnits()) {
            if (unit.getType() == UnitTypes.Terran_SCV && unit.isIdle()) {
                // Se comprueban para todas las unidades de tipo neutral, aquella
                // que no pertenencen a ningun jugador. 
                for (Unit minerals : this.bwapi.getNeutralUnits()) {
                    // Se comprueba si la unidad es un deposito de minerales y si es
                    // no ha sido seleccionada previamente.                                 
                    if (minerals.getType().isMineralField()) {                                    
                        // Se calcula la distancia entre la unidad y el deposito de minerales
                        double distance = unit.getDistance(minerals);
                        // Se comprueba si la distancia entre la unidad 
                        // y el deposito de minerales es menor a 300.
                        if (distance < 300) {
                            // Se ejecuta el comando para enviar a la unidad a recolertar
                            // minerales del deposito seleccionado.
                            unit.rightClick(minerals, false);
                            // Se aÃ±ade el deposito a la lista de depositos en uso.
                            this.claimedMinerals.add(minerals);
                            break;
                        }
                    }
                }
            }
        }
        
        /* Metodo para crear dos constructores 'Terran_CSV' */
        if (bwapi.getSelf().getMinerals() >= 50 && bwapi.getSelf().getSupplyUsed() <= 12) {
            for (Unit unit : bwapi.getMyUnits()) {
                // Se compruba si existe alguna centro de control y si esta construido
                if (unit.getType() == UnitTypes.Terran_Command_Center && unit.isCompleted()) {
                    if (bwapi.getSelf().getSupplyTotal() >= (bwapi.getSelf().getSupplyUsed() + UnitTypes.Terran_SCV.getSupplyRequired())){
                    	crearUnidad(unit.getID(), UnitTypes.Terran_SCV);
                    }
                }
            }
        }
        
        
        /* Proceso para la creación de la refineria
         * Esta se creará cuando haya recursos suficientes y no exista una anterior */
        if(bwapi.getSelf().getMinerals() >= 100 && refineria == 0){
        	Unit constructor = null;
        	for (Unit unit : this.bwapi.getMyUnits()) {
        		if (unit.getType() == UnitTypes.Terran_SCV){
        			constructor = unit;
        		}
        	}
            if (constructor != null) {
            	crearEdificio(constructor.getID(), UnitTypes.Terran_Refinery, buscarUbicacion(UnitTypes.Terran_Refinery));
            }
        }  
        
        /* Método para tener dos scv extrayendo gas de la refineria 
        if (uRef < 2){
	        for (Unit refineria : this.bwapi.getMyUnits()) {
	            if (refineria.getType() == UnitTypes.Terran_Refinery) {
	            	if (refineria.isCompleted()){
	            		Unit recolector = null;
	                	for (Unit unit : this.bwapi.getMyUnits()) {
	                		if (!unit.getOrder().getName().equals("ReturnGas") && !unit.getOrder().getName().equals("HarvestGas") && !unit.getOrder().getName().equals("MoveToGas")){
	                			recolector = unit;
	                			uRef = 2;
	                		}
	                	}
	                    if (recolector != null && recolector.getType() == UnitTypes.Terran_SCV) {
	                    	recolector.rightClick(refineria, false);
	                    	break;
	                    }
	            	}
	          	}
	        }
        }
        */
        
        /* Metodo para construir el supply depot */
        if(bwapi.getSelf().getMinerals() >= 100 && refineria == 1 && supply == 0){
        	Unit constructor = null;
        	Position pos = buscarUbicacion(UnitTypes.Terran_Supply_Depot);
        	for (Unit unit : this.bwapi.getMyUnits()) {
        		if (unit.getType() == UnitTypes.Terran_SCV){
        			constructor = unit;
        			break;
        		}
        	}
        	if (constructor != null && pos != null){
        		crearEdificio(constructor.getID(), UnitTypes.Terran_Supply_Depot, pos);
        	}
        }
        
        /* Método para crear la barraca */
        if(bwapi.getSelf().getMinerals() >= 100 && supply == 1 && barraca == 0){
        	Unit constructor = null;
        	Position pos = buscarUbicacion(UnitTypes.Terran_Barracks);
        	// Buscamos un constructor
        	for (Unit unit : this.bwapi.getMyUnits()) {
        		if (unit.getType() == UnitTypes.Terran_SCV){
        			constructor = unit;
        			break;
        		}
        	}
        	// Construimos si hay un constructor y una posicion
        	if (constructor != null && pos != null){
        		crearEdificio(constructor.getID(), UnitTypes.Terran_Barracks, pos);
        	}
        }
        
        /* Método para construir un marine */
        if (bwapi.getSelf().getMinerals() >= 50 && barraca == 1){
        	for (Unit unit : bwapi.getMyUnits()) {
                // Se compruba si existe alguna centro de control y si esta construido
                if (unit.getType() == UnitTypes.Terran_Barracks && unit.isCompleted()) {
                	if (16 >= (bwapi.getSelf().getSupplyUsed() + UnitTypes.Terran_Marine.getSupplyRequired())){
                    	crearUnidad(unit.getID(), UnitTypes.Terran_Marine);
                	}
                }
        	}
        }
    }
    
    
    /**
     * Método para entrenar una unidad en un edificio. 
     * 
     * Se introducen como parámetros: 
     * @param edifid	ID del edificio donde se construirá la unidad
     * @param unidad	Tipo de unidad que se desea construir
     * @return 			True si se ha creado correctamente
     */
    public boolean crearUnidad(int edifid, UnitType unidad){
    	Unit edificio = bwapi.getUnit(edifid);
    	return edificio.train(unidad);
    }
    
    /**
     * Método para la creación de edificios por una unidad
     * 
     * Se introducen como parámetros
     * @param trabaid		ID del trabajador
     * @param edificio		Tipo de edificio a construir
     * @param pos			Posicion para la construccion
     * @return 				True si el edificio se ha creado correctamente, False si no es posible su creación
     */
    public boolean crearEdificio(int trabaid, UnitType edificio, Position pos){
    	if (pos == null || edificio == null){
    		return false;
    	} else {
	    	if (bwapi.canBuildHere(pos, edificio, false)){
	    		Unit trabajador = bwapi.getUnit(trabaid);
	    		return trabajador.build(pos, edificio);
	    	}
	    	return false;
    	}
    }
    
    /**
     * Método para obtener localización para la construcción de edificios
     * 
     * @param edificio		Tipo de edificio que se desea construir
     * @param centroMando		Al principio de la partida se calcula la posición del centro de mando que se usara para calcular distancias
	 *
     * @return Position		La posicion del edificio donde se puede construir
     */
    public Position buscarUbicacion(UnitType edificio){
    	Position pos = null;
    	int distancia = 0;
    	int maxBusqueda = 50;
    	// Si el edificio es una refineria, buscamos vespeno
    	if (edificio.getID() == UnitTypes.Terran_Refinery.getID()){
    		// Comprobamos que es una unidad neutral
            for (Unit vespeno : this.bwapi.getNeutralUnits()){
            	// Comprobamos que es un geyser de vespeno
            	if (vespeno.getType() == UnitTypes.Resource_Vespene_Geyser){
            		// Cogemos la posicion del vespeno para construir el edificio encima
            		pos = vespeno.getTilePosition();
            		return pos;
            	}
            }
    	} else {
    		while (distancia < maxBusqueda && pos == null){
    			for(int i = centroMando.getBX(); i < centroMando.getBX() + maxBusqueda; i++){
    				for (int j = centroMando.getBY(); j < centroMando.getBY() + maxBusqueda; j++){
    					System.out.println("i " + i + " j " + j);
    					if (bwapi.canBuildHere(new Position(i, j, PosType.BUILD), edificio, false)){
    						return new Position(i, j, PosType.BUILD);
    					}
    				}
    			}
    			distancia++;
    		}
    		if (pos == null){
    			return pos;
    		}
    	}
		return null;
    }
    
    @Override
    public void keyPressed(int keyCode) {}
    @Override
    public void matchEnd(boolean winner) {}
    @Override
    public void sendText(String text) {}
    @Override
    public void receiveText(String text) {}
    @Override
    public void nukeDetect(Position p) {}
    @Override
    public void nukeDetect() {}
    @Override
    public void playerLeft(int playerID) {}
    @Override
    public void unitCreate(int unitID) {}
    @Override
    public void unitDestroy(int unitID) {}
    @Override
    public void unitDiscover(int unitID) {}
    @Override
    public void unitEvade(int unitID) {}
    @Override
    public void unitHide(int unitID) {}
    @Override
    public void unitMorph(int unitID) {}
    @Override
    public void unitShow(int unitID) {}
    @Override
    public void unitRenegade(int unitID) {}
    @Override
    public void saveGame(String gameName) {}
    @Override
    public void unitComplete(int unitID) {}
    @Override
    public void playerDropped(int playerID) {}
}
