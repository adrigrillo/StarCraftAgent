package org.iaie.practica2;

import java.util.ArrayList;
import java.util.HashSet;

import org.iaie.Agent;
import org.iaie.btree.BehavioralTree;
import org.iaie.btree.task.composite.Selector;
import org.iaie.btree.task.composite.Sequence;
import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.construction.BuildBuilding;
import org.iaie.practica2.construction.BuildingState;
import org.iaie.practica2.construction.CheckBuildingResources;
import org.iaie.practica2.construction.ConstructionTree;
import org.iaie.practica2.construction.FreeWorkerToBuild;
import org.iaie.practica2.construction.SelectLocation;
import org.iaie.practica2.recolect.*;
import org.iaie.practica2.units.*;

import jnibwapi.BWAPIEventListener;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

public class PlayerPractica20316457 extends Agent implements BWAPIEventListener{
	
	// Creamos el arbol de decision
	private BehavioralTree recollectTree;
	private BehavioralTree buildTree;
	private BehavioralTree trainTree;
	
	
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

		
		// Anadimos las unidades los scv iniciales en el hashset y los edificios
		for (Unit unit : bwapi.getMyUnits()){
			if (unit.getType().isWorker()){
				CtrlVar.workers.add(unit);
			}
			else if (unit.getType().isBuilding() && unit.isCompleted()){
				CtrlVar.buildings.add(unit);
				if (unit.getType() == UnitTypes.Terran_Command_Center)
					CtrlVar.centroMando = unit;
			}
		}
		
		// Establecemos el arbol de recoleccion
		RecolectTree recolectar = new RecolectTree(bwapi);
		TrainingTree entrenar = new TrainingTree(bwapi);
		ConstructionTree construir = new ConstructionTree(bwapi);
		
		/* Arbol de recoleccion */
		Sequence collectGas = new Sequence("collectGas", new CheckBalance("Balance", recolectar), new CollectGas("CollectGas", recolectar));
		Selector<GameHandler> collectResources = new Selector<GameHandler>("collectResources", collectGas, new CollectMineral("CollectMineral", recolectar));
		Sequence collect = new Sequence("Recolectar", new FreeWorkerToRecollect("Trabajador", recolectar), collectResources);
		/* Arbol de entrenamiento */
		Sequence train = new Sequence("Check", new CheckTraining("training", entrenar), new CheckBuilding("Build", entrenar), new CheckUnitResources("resources", entrenar), new TrainUnit("entrenar", entrenar));
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		
		/* Arbol de construccion */
		Sequence build = new Sequence("Build", new BuildingState("Estado", construir), new CheckBuildingResources("Recursos", construir), new SelectLocation("Location", construir), new FreeWorkerToBuild("Worker", construir), new BuildBuilding("Construir", construir));
		
		recollectTree = new BehavioralTree("ArbolDecision");
		recollectTree.addChild(collect);
		buildTree = new BehavioralTree("ArbolDecision");
		buildTree.addChild(build);
		trainTree = new BehavioralTree("Arbol de decision");
		trainTree.addChild(train);
	}

	public void matchFrame() {
		recollectTree.run();
		trainTree.run();
		buildTree.run();
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
