package org.iaie.practica2;

import java.util.HashSet;

import org.iaie.Agent;
import org.iaie.btree.BehavioralTree;
import org.iaie.btree.task.composite.Selector;
import org.iaie.btree.task.composite.Sequence;
import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.recolect.CheckResources;
import org.iaie.practica2.recolect.ChooseBuilding;
import org.iaie.practica2.recolect.CollectGas;
import org.iaie.practica2.recolect.CollectMineral;
import org.iaie.practica2.recolect.FreeWorker;
import org.iaie.practica2.recolect.RecolectTree;
import org.iaie.practica2.recolect.TrainUnit;

import jnibwapi.BWAPIEventListener;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.UnitType.UnitTypes;

public class PlayerPractica20316457 extends Agent implements BWAPIEventListener{
	
	// Creamos el arbol de decision
	private BehavioralTree collectTree;
	
	/* Esta variable se usa para almacenar aquellos depositos de minerales 
     *  que han sido seleccionados para ser explotados por las unidades 
     *  recolectoras. */
    public HashSet<Unit> claimedMinerals = new HashSet<>();
    public HashSet<Unit> worker = new HashSet<>();
    public HashSet<Unit> militaryUnits = new HashSet<>();
    public HashSet<Unit> buildings = new HashSet<>();
	
	public PlayerPractica20316457() {            

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

	
	public void connected() {
		System.out.println("IAIE: Conectando con BWAPI");
	}

	
	public void matchStart() {
		// Iniciamos las variables de control y el mapa
		MapHandler.generateMapSpaces(bwapi);
		claimedMinerals.clear();
		militaryUnits.clear();
		worker.clear();
		buildings.clear();
		
		// Anadimos las unidades los scv iniciales en el hashset y los edificios
		for (Unit unit : bwapi.getMyUnits()){
			if (unit.getType().isWorker()){
				worker.add(unit);
			}
			else if (unit.getType().isBuilding() && unit.isCompleted()){
				buildings.add(unit);
			}
		}
		
		// Establecemos el arbol de recoleccion
		RecolectTree arbol = new RecolectTree(bwapi);
		Selector<GameHandler> collectResources = new Selector<GameHandler>("collectResources", new CollectMineral("CollectMineral", arbol), new CollectGas("CollectGas", arbol));
		Sequence collect = new Sequence("collect", new FreeWorker("Search", arbol), collectResources);
		Sequence train = new Sequence("Entrenar", new CheckResources("Comprobar", arbol), new ChooseBuilding("Elegir", arbol), new TrainUnit("Entrenar",  arbol));
		Selector<GameHandler> recolOrTrain = new Selector<GameHandler>("RecolectarOEntrenar", collect, train);
		collectTree = new BehavioralTree("ArbolDecision");
		collectTree.addChild(recolOrTrain);
	}

	public void matchFrame() {
		collectTree.run();
	}
	
	public void matchEnd(boolean winner) {
		// TODO Auto-generated method stub	
	}
    
	public void keyPressed(int keyCode) {
		// TODO Auto-generated method stub
	}
	
	public void sendText(String text) {
		// TODO Auto-generated method stub	
	}
	
	public void receiveText(String text) {
		// TODO Auto-generated method stub	
	}
	
	public void playerLeft(int playerID) {
		// TODO Auto-generated method stub	
	}

	public void nukeDetect(Position p) {
		// TODO Auto-generated method stub		
	}

	public void nukeDetect() {
		// TODO Auto-generated method stub		
	}
	
	public void unitDiscover(int unitID) {
		// TODO Auto-generated method stub		
	}
	
	public void unitEvade(int unitID) {
		// TODO Auto-generated method stub
	}

	public void unitShow(int unitID) {
		// TODO Auto-generated method stub	
	}
	
	public void unitHide(int unitID) {
		// TODO Auto-generated method stub		
	}
	
	public void unitCreate(int unitID) {
		// TODO Auto-generated method stub
	}
	
	public void unitDestroy(int unitID) {
		// TODO Auto-generated method stub
	}
	
	public void unitMorph(int unitID) {
		// TODO Auto-generated method stub
	}
	
	public void unitRenegade(int unitID) {
		// TODO Auto-generated method stub
	}
	
	public void saveGame(String gameName) {
		// TODO Auto-generated method stub
	}
	
	public void unitComplete(int unitID) {
		// TODO Auto-generated method stub
	}

	public void playerDropped(int playerID) {
		// TODO Auto-generated method stub
	}
}
