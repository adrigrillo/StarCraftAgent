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
	private BehavioralTree creationTree;
	
	
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
		creationTree = new BehavioralTree("ArbolDecision");
		creationTree.addChild(creation);
		
		
		// Edificios a construir
		CtrlVar.buildqueue.add(UnitTypes.Terran_Barracks);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Barracks);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Command_Center);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Engineering_Bay);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Academy);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Bunker);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Factory);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Starport);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Armory);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Science_Facility);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Control_Tower);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Missile_Turret);
		
		// Iniciamos el tiempo
		startTime = System.currentTimeMillis();
	}

	public void matchFrame() {
		recollectTree.run();
		creationTree.run();
	}
	
	public void matchEnd(boolean winner) {
		try {
			File archivo = new File("Resultados.txt");
			PrintWriter writer = new PrintWriter(archivo);
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
			writer.println("La duracion ha sido de " + duration/1000 + " segundos.");
			writer.println("Se han construido " + CtrlVar.buildings.size() + " edificios, de los cuales fueron centros de mando " + CtrlVar.centroMando.size() + ".");
			writer.println("Se han entrenado " + CtrlVar.workers.size() + " fueron unidades no militares y " + CtrlVar.militaryUnits.size() + " fueron unidades militares.");
			writer.println("Se recogieron " + bwapi.getSelf().getCumulativeMinerals() + " unidades de mineral y " + bwapi.getSelf().getCumulativeGas() + " de vespeno." );
			writer.close();
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
