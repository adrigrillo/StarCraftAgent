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
 * Ejemplo de cliente IA que utiliza JNI-BWAPI.
 * 
 * Este agente utiliza información perfecta.
 * Este es un agente experimental para el aprendizaje del funcionamiento básico
 * de JNI-BWAPI en la asignatura Inteligencia Artificial en la Industria del 
 * Entretenimiento.
 * 
 * Nota: Este agente a menudo se queda bloqueado y no realiza todas las acciones 
 * que han sido programadas. Se recomiendo utilizar escenarios en los cuales exista 
 * el mayor espacio libre alrededor de los overlords.
 */
public class ExampleAIClient extends Agent implements BWAPIEventListener {
	
    /** Esta variable se usa para almacenar aquellos depositos de minerales 
     *  que han sido seleccionados para ser explotados por las unidades 
     *  recolectoras. */
    private final HashSet<Unit> claimedMinerals = new HashSet<>();

    /** Esta variable se utiliza la generación de drones */
    private boolean morphedDrone;

    /** Esta variable se utiliza como contenedor temporal para selecionar
     *  una unidad que va ser transformada en una piscina de drones.*/
    private Unit poolDrone;

    /** Esta variable se utiliza para comprobar cuando debe ser generada un 
     *  nuevo overlord con el fin de poder entrenar otras unidades.*/
    private int supplyCap;
    
    public int contador = 0;

    public ExampleAIClient() {            

        // Generación del objeto de tipo agente

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
        // Se establecen los valores por defecto de las variables de control.
        morphedDrone = false;
        poolDrone = null;
        supplyCap = 0;
    }

    /**
     * Evento Maestro
     */
    @Override
    public void matchFrame() {

        String msg = "=";

        // Mediante este bucle se comprueba si el jugador está investigando
        // algún tipo de tecnología y lo muestra por pantalla
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

        // Mediante este bucle se comprueba si se está realizando una actualización
        // sobre algún tipo de unidad. 
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

        // Mediante este método se 
        this.bwapi.getMap().drawTerrainData(bwapi);

        // Proceso para engendrar un drone
        for (Unit unit : this.bwapi.getMyUnits()) {
            // Se comprueba para cada unidad del jugador que está siendo 
            // controlado si este de tipo lava. 
            if (unit.getType() == UnitTypes.Zerg_Larva) {
                // Se comprueba si el número de minerales es superios a 50 unidades
                // y si no se ha engendrado ningún dron.
                if (this.bwapi.getSelf().getMinerals() >= 50 && !this.morphedDrone) {                            
                    // Mediante este método se metamorfosea una unidad de tipo larva a drone
                    unit.morph(UnitTypes.Zerg_Drone);
                    this.morphedDrone = true;
                }
            }
        }

        // Proceso para la recolección de minerales
        for (Unit unit : this.bwapi.getMyUnits()) {
            // Se comprueba para cada unidad del jugador que está siendo 
            // controlado si este de tipo drone
            if (unit.getType() == UnitTypes.Zerg_Drone) {
                // Se comprueba si la unidad no está realizando ninguna tarea (isIdle)
                // y si es de tipo poolDrone
                if (unit.isIdle() && unit != this.poolDrone) {
                    // Se comprueban para todas las unidades de tipo neutral, aquella
                    // que no pertenencen a ningun jugador. 
                    for (Unit minerals : this.bwapi.getNeutralUnits()) {
                        // Se comprueba si la unidad es un deposito de minerales y si es
                        // no ha sido seleccionada previamente.                                 
                        if (minerals.getType().isMineralField() && !this.claimedMinerals.contains(minerals)) {                                    
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
        }

        // Proceso de contrucción de una pisicina de generación
        // Se comprueba si el número de minerales disponibles es superior a 200
        // y si no se ha generado ninguna piscina de drones.
        if (this.bwapi.getSelf().getMinerals() >= 200 && this.poolDrone == null) {
            for (Unit unit : this.bwapi.getMyUnits()) {
                // Se comprueba si la unidad es de tipo drone.
                if (unit.getType() == UnitTypes.Zerg_Drone) {
                    // Se asigna la unidad como unidad para generar una piscina
                    // de drones.
                    this.poolDrone = unit;
                    break;
                }
            }

            // Construcción de la piscina de drones en la posición de un 
            // overlord. Los overlords son unidades voladoras, esto permite
            // utilizarlas como faros para la construcción de edificios. 
            for (Unit unit : this.bwapi.getMyUnits()) {
                // Se comprueba si la unidad es de tipo Overlord.
                if (unit.getType() == UnitTypes.Zerg_Overlord) {
                    // Se construye una piscina de drones en la posición del overlord 
                    // seleccionado utilizando como base el drone seleccionado previamente.
                    this.poolDrone.build(unit.getPosition(), UnitTypes.Zerg_Spawning_Pool);
                }
            }
        }

        // Proceso de generación de overlords
        // Se comprueba si el número de unidades generadas (getSupplyUsed) es mayor que el
        // número de unidades disponibles (getSupplyTotal) y si el número de unidades 
        // disponibles es mayor que el valor de la variable supplyCap. 
        if (bwapi.getSelf().getSupplyUsed() + 2 >= bwapi.getSelf().getSupplyTotal()
                        && bwapi.getSelf().getSupplyTotal() > supplyCap) {
            // Se comprueba si el número de minerales disponibles es 
            // mayor o igual a 100.
            if (bwapi.getSelf().getMinerals() >= 100) {
                for (Unit larva : bwapi.getMyUnits()) {
                    // Se comprueba si la unidades es de tipo larba.
                    if (larva.getType() == UnitTypes.Zerg_Larva) {
                        // Se transforma la unidades de tipo larva en un overlord.
                        larva.morph(UnitTypes.Zerg_Overlord);
                        // Se asigna un nuevo valor a la variable supplyCap
                        supplyCap = bwapi.getSelf().getSupplyTotal();
                    }
                }
            }
        }
        // Proceso de generación de zerglings
        // Se comprueba si el número de minerales disponibles es 
        // mayor o igual a 50.
        else if (bwapi.getSelf().getMinerals() >= 50) {
            for (Unit unit : bwapi.getMyUnits()) {
                // Se compruba si existe alguna piscina de drones y si está ha sido completada
                if (unit.getType() == UnitTypes.Zerg_Spawning_Pool && unit.isCompleted()) {
                    for (Unit larva : bwapi.getMyUnits()) {
                        // Se comprueba si la unidad es de tipo larva
                        if (larva.getType() == UnitTypes.Zerg_Larva) {
                            // Se Transforma la larva en un zergling.
                            larva.morph(UnitTypes.Zerg_Zergling);
                        }
                    }
                }
            }
        }        
        // Proceso de movimiento y ataque
        for (Unit unit : bwapi.getMyUnits()) {
            // Se comprubea si la unidad es de zergling y si la unidad no tiene ninguna 
            // tarea asignaga (isIdle).
            if (unit.getType() == UnitTypes.Zerg_Zergling && unit.isIdle()) {
                for (Unit enemy : bwapi.getEnemyUnits()) {
                    // Se selecciona la posición de una unidad enemiga y 
                    // se envia a la unidad seleccionada previamente a atacar. 
                    unit.attack(enemy.getPosition(), false);
                    break;
                }
            }
        }
    }

    @Override
    public void keyPressed(int keyCode) {}
    @Override
    public void matchEnd(boolean winner) {
    	if (winner == true){
    		System.out.println("Has ganado");
    	}
    	else{
    		System.out.println("has perdio");
    	}
    }
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
    public void unitCreate(int unitID) {
    	Unit a = bwapi.getUnit(unitID);
    	for (Unit b : bwapi.getMyUnits()){
    		if (a == b){
    			System.out.println("Se crea una unidad de las mias. --> " + bwapi.getUnit(unitID).getType());
    		}
    	}
    }
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
    public void unitComplete(int unitID) {
    	Unit a = bwapi.getUnit(unitID);
    	for (Unit b : bwapi.getMyUnits()){
    		if (a == b){
    			System.out.println("Se completa una unidad de las mias. --> " + bwapi.getUnit(unitID).getType());
    		}
    	}
    }
    @Override
    public void playerDropped(int playerID) {}
}
