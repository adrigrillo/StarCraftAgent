/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3m.es
 * 
 * Copyright 2015, Moises Martinez
 *
 * (Questions/bug reports now to be sent to Moisés Martínez)
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

import jdk.nashorn.internal.objects.annotations.Function;

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
    
    /** Variable para que no haya m�s de dos personas en la refineria
     */
    int uRef = 0;

    public PlayerTutorial10316457_0303518() {            

        // Generacion del objeto de tipo agente

        // Creación de la superclase Agent de la que extiende el agente, en este método se cargan            
        // ciertas variables de de control referentes a los parámetros que han sido introducidos 
        // por teclado. 
        super();
        // Creación de una instancia del connector JNIBWAPI. Esta instancia sólo puede ser creada
        // una vez ya que ha sido desarrollada mediante la utilización del patrón de diseño singlenton.
        this.bwapi = new JNIBWAPI(this, true);
        // Inicia la conexión en modo cliente con el servidor BWAPI que está conectado directamente al videojuego.
        // Este proceso crea una conexión mediante el uso de socket TCP con el servidor. 
        this.bwapi.start();
    }

    /**
     * Este evento se ejecuta una vez que la conexión con BWAPI se ha estabilidad. 
     */
    @Override
    public void connected() {
        System.out.println("IAIE: Conectando con BWAPI");
    }

    /**
     * Este evento se ejecuta al inicio del juego una única vez, en el se definen ciertas propiedades
     * que han sido leidas como parámetros de entrada.
     * Velocidad del juego (Game Speed): Determina la velocidad a la que se ejecuta el videojuego. Cuando el juego 
     * se ejecuta a máxima velocidad algunas eventos pueden ser detectados posteriormente a su ejecución real. Esto
     * es debido a los retrasos en las comunicacion y el retardo producido por la tiempo de ejecución del agente. En 
     * caso de no introducir ningun valor el jugador 
     * Información perfecta (Perfect información): Determina si el agente puede recibir información completa del 
     * juego. Se consedira como información perfecta cuando un jugador tiene acceso a toda la información del entorno, 
     * es decir no le afecta la niebla de guerra.
     * Entrada de usuarios (UserInput)
     */
    @Override
    public void matchStart() {

        System.out.println("IAIE: Iniciando juego");
        

        // Revisar. 
        // Mediante esté metodo se puede obtener información del usuario. 
        if (Options.getInstance().getUserInput()) this.bwapi.enableUserInput();
        // Mediante este método se activa la recepción completa de información.
        if (Options.getInstance().getInformation()) this.bwapi.enablePerfectInformation();
        // Mediante este método se define la velocidad de ejecución del videojuego. 
        // Los valores posibles van desde 0 (velocidad estándar) a 10 (velocidad máxima).
        this.bwapi.setGameSpeed(Options.getInstance().getSpeed());

        // Iniciamos las variables de control
        // Se establece el contador de objetos a cero y se eliminan todas las
        // referencias previas a los objetos anteriormente añadidos.
        claimedMinerals.clear();
        supplyCap = 0;
    }

    /**
     * Evento Maestro
     */
    @Override
    public void matchFrame() {

        String msg = "=";

        /* Comprobaci�n de las investigaciones */
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
        
        /* Metodo para localizar los scv disponibles
        for (Unit myUnit : this.bwapi.getMyUnits()){
        	//print TilePosition and Position of my SCVs
        	if (myUnit.getType() == UnitTypes.Terran_SCV) {
        		this.bwapi.drawText(new Position(0, 20), "TilePos: " + myUnit.getTilePosition().toString()+" Pos: " + myUnit.getPosition().toString(), true);
        	}                   
        }*/
        
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
                            // Se añade el deposito a la lista de depositos en uso.
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
        
        
        /* Proceso para la creaci�n de la refineria */
        if(bwapi.getSelf().getMinerals() >= 100){
        	Unit constructor = null;
        	for (Unit unit : this.bwapi.getMyUnits()) {
        		constructor = unit;
        	}
            if (constructor != null && constructor.getType() == UnitTypes.Terran_SCV) {
                // Comprobamos que es una unidad neutral
                for (Unit vespeno : this.bwapi.getNeutralUnits()){
                	// Comprobamos que es un geyser de vespeno
                	if (vespeno.getType() == UnitTypes.Resource_Vespene_Geyser){
                		// Cogemos la posicion del vespeno para construir el edificio encima
                		Position pos = vespeno.getTilePosition();
                		crearEdificio(constructor.getID(), UnitTypes.Terran_Refinery, pos);
                		break;
                	}
                }
            }
        }  
        
        if (uRef < 2){
	        for (Unit refineria : this.bwapi.getMyUnits()) {
	            if (refineria.getType() == UnitTypes.Terran_Refinery) {
	            	if (refineria.isCompleted()){
	            		Unit recolector = null;
	                	for (Unit unit : this.bwapi.getMyUnits()) {
	                		if (!unit.getOrder().getName().equals("ReturnGas") && !unit.getOrder().getName().equals("HarvestGas") && !unit.getOrder().getName().equals("MoveToGas")){
	                			recolector = unit;
	                			uRef = 2;
	                			break;
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
    }
    
    
    /**
     * M�todo para entrenar una unidad en un edificio. 
     * 
     * Se introducen como par�metros: 
     * @param edifid	ID del edificio donde se construir� la unidad
     * @param unidad	Tipo de unidad que se desea construir
     * @return 			True si se ha creado correctamente
     */
    public boolean crearUnidad(int edifid, UnitType unidad){
    	Unit edificio = bwapi.getUnit(edifid);
    	return edificio.train(unidad);
    }
    
    /**
     * M�todo para la creaci�n de edificios por una unidad
     * 
     * Se introducen como par�metros
     * @param trabaid		ID del trabajador
     * @param edificio		ID del tipo de edificio a construir
     * @param pos			Posicion para la construccion
     * @return 				True si el edificio se ha creado correctamente
     */
    public boolean crearEdificio(int trabaid, UnitType edificio, Position pos){
    	Unit trabajador = bwapi.getUnit(trabaid);
    	if (bwapi.canBuildHere(pos, edificio, false)){
    		return trabajador.build(pos, edificio);
    	}
    	else{
    		return false;
    	}
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
