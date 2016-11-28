package org.iaie.practica2;

import java.io.File;
import java.io.PrintWriter;

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
import jnibwapi.types.UnitType.UnitTypes;

public class PlayerPractica20316457 extends Agent implements BWAPIEventListener{
	
	// Creamos el arbol de decision
	long startTime;
	long endTime;
	private BehavioralTree recollectTree;
	private BehavioralTree buildTree;
	private BehavioralTree trainTree;
	
	
	public PlayerPractica20316457() {            

        // Generacion del objeto de tipo agente

        // Creaci�n de la superclase Agent de la que extiende el agente, en este m�todo se cargan            
        // ciertas variables de de control referentes a los par�metros que han sido introducidos 
        // por teclado. 
        super();
        // Creaci�n de una instancia del connector JNIBWAPI. Esta instancia s�lo puede ser creada
        // una vez ya que ha sido desarrollada mediante la utilizaci�n del patr�n de dise�o singlenton.
        this.bwapi = new JNIBWAPI(this, true);
        // Inicia la conexi�n en modo cliente con el servidor BWAPI que est� conectado directamente al videojuego.
        // Este proceso crea una conexi�n mediante el uso de socket TCP con el servidor. 
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
					CtrlVar.centroMando.add(unit);
			}
		}
		
		// Establecemos el arbol de recoleccion
		RecolectTree recolectar = new RecolectTree(bwapi);
		TrainingTree entrenar = new TrainingTree(bwapi);
		ConstructionTree construir = new ConstructionTree(bwapi);
		
		/* Arbol de recoleccion */
		/* Collect gas: Miramos equilibrio, si se necesita gas, miramos si esta construida la refineria,
		 * cogemos un trabajador y recogemos gas */
		Sequence collectGas = new Sequence("collectGas", new CheckBalance("Balance", recolectar), new CheckRefinery("Comprobar refineria", recolectar), new FreeWorkerGas("TrabajadorGas", recolectar), new CollectGas("CollectGas", recolectar));
		/* Collect mineral: Si no se necesita gas, buscamos un trabajador libre, recogemos */
		Sequence collectMineral = new Sequence("collectMineral", new FreeWorkerMineral("TrabajadorMinera", recolectar), new CollectMineral("CollectMineral", recolectar));
		Selector<GameHandler> collectResources = new Selector<GameHandler>("collectResources", collectGas, collectMineral);
		/* Arbol de entrenamiento */
		Sequence train = new Sequence("Check", new CheckTraining("training", entrenar), new CheckPopulation("Comprobar poblacion", entrenar), new CheckBuilding("Comprobar edificios", entrenar), new CheckUnitResources("Comprobar recursos", entrenar), new TrainUnit("Entrenar", entrenar));
		
		/* Arbol de construccion */
		Sequence build = new Sequence("Build", new BuildingState("Estado", construir), new CheckBuildingResources("Recursos", construir), new SelectLocation("Location", construir), new FreeWorkerToBuild("Worker", construir), new BuildBuilding("Construir", construir));
		
		Selector<GameHandler> creation = new Selector<GameHandler>("TrainOrBuild", build, train);
		
		recollectTree = new BehavioralTree("ArbolDecision");
		recollectTree.addChild(collectResources);
		trainTree = new BehavioralTree("ArbolDecision");
		trainTree.addChild(creation);
		/*trainTree = new BehavioralTree("Arbol de decision");
		trainTree.addChild(train);
		buildTree = new BehavioralTree("ArbolDecision");
		buildTree.addChild(build);*/
		
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		CtrlVar.trainqueue.add(UnitTypes.Terran_SCV);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Barracks);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Academy);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Engineering_Bay);
		
		
		// Iniciamos el tiempo
		startTime = System.currentTimeMillis();
	}

	public void matchFrame() {
		recollectTree.run();
		trainTree.run();
	}
	
	public void matchEnd(boolean winner) {
		try {
			PrintWriter writer = new PrintWriter(new File("Resultados.txt"));
			// Resultado de la partida
			String resultado = "";
			if (winner)
				resultado = "victoria";
			else
				resultado = "derrota";
			writer.println("El resultado de la partida ha sido " + resultado + ".");
			// Sacamos el tiempo
			endTime = System.currentTimeMillis();
			long duration = (endTime - startTime);
			writer.println("La duraci�n ha sido de " + duration/1000 + "segundos.");
			writer.println("Se han construido " + CtrlVar.buildings.size() + " edificios, de los cuales fueron centros de mando " + CtrlVar.centroMando.size() + ".");
			writer.println("Se han entrenado " + CtrlVar.workers.size() + " fueron unidades no militares y " + CtrlVar.militaryUnits.size() + " fueron unidades militares.");
			writer.println("Se recogieron " + bwapi.getSelf().getMinerals() + " unidades de mineral y " + bwapi.getSelf().getGas() + " de vespeno." );
			
		} catch (Exception e) {
			System.out.println("Error al imprimir");
		}
		
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
