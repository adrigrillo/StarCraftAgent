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
import org.iaie.practica2.militarControl.AtackOrPatrol;
import org.iaie.practica2.militarControl.AttackUnits;
import org.iaie.practica2.militarControl.CheckState;
import org.iaie.practica2.militarControl.DefenseMode;
import org.iaie.practica2.militarControl.MilitarTree;
import org.iaie.practica2.militarControl.OrderPatrol;
import org.iaie.practica2.movements.CheckPositionUnit;
import org.iaie.practica2.movements.CheckStateUnit;
import org.iaie.practica2.movements.MovementTree;
import org.iaie.practica2.movements.SendUnit;
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
	private BehavioralTree explorationTree;
	private BehavioralTree militaryTree;
	
	
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
		CtrlVar.clearAll();
		MapHandler.generateMapSpaces(bwapi);
		
		// Anadimos las unidades los scv iniciales en el hashset y los edificios
		for (Unit unit : bwapi.getMyUnits()){
			if (unit.getType().isWorker()){
				CtrlVar.workers.add(unit);
			}
		}
		CtrlVar.refreshBuildings(bwapi);
		
		// Establecemos el arbol de recoleccion
		RecolectTree recolectar = new RecolectTree(bwapi);
		TrainingTree entrenar = new TrainingTree(bwapi);
		ConstructionTree construir = new ConstructionTree(bwapi);
		MovementTree explorar = new MovementTree(bwapi);
		MilitarTree militares = new MilitarTree(bwapi);
		
		/* Arbol de recoleccion */
		/* Collect gas: Miramos equilibrio, si se necesita gas, miramos si esta construida la refineria, cogemos un trabajador y recogemos gas */
		Sequence collectGas = new Sequence("collectGas", new CheckBalance("Balance", recolectar), new CheckRefinery("Comprobar refineria", recolectar), new FreeWorkerGas("TrabajadorGas", recolectar), new CollectGas("CollectGas", recolectar));
		/* Collect mineral: Si no se necesita gas, buscamos un trabajador libre, recogemos */
		Sequence collectMineral = new Sequence("collectMineral", new FreeWorkerMineral("TrabajadorMinera", recolectar), new CollectMineral("CollectMineral", recolectar));
		Selector<GameHandler> collectResources = new Selector<GameHandler>("collectResources", collectGas, collectMineral);
		
		/* Arbol de creacion */
		/* Entrenamiento de unidades */
		Sequence train = new Sequence("Check", new CheckTraining("training", entrenar), new CheckPopulation("Comprobar poblacion", entrenar), new CheckBuilding("Comprobar edificios", entrenar), new CheckUnitResources("Comprobar recursos", entrenar), new TrainUnit("Entrenar", entrenar));
		/* Construccion de edificios*/
		Sequence build = new Sequence("Build", new BuildingState("Estado", construir), new CheckBuildingResources("Recursos", construir), new SelectLocation("Location", construir), new FreeWorkerToBuild("Worker", construir), new BuildBuilding("Construir", construir));
		Selector<GameHandler> creation = new Selector<GameHandler>("TrainOrBuild", build, train);
		
		/* Arbol de exploracion */
		Sequence explore = new Sequence("Explore", new CheckStateUnit("Estado", explorar), new CheckPositionUnit("Posicion", explorar), new SendUnit("Mandar", explorar));
		
		/* Arbol para el control de los militares */
		// Comprobamos que se puede atacar, si se puede ataca
		Sequence atack = new Sequence("Atacar", new AtackOrPatrol("Comprobar", militares), new AttackUnits("Atacar", militares));
		// Si no se puede patrulla
		Selector<GameHandler> atackPatrol = new Selector<GameHandler>("Atacar o Patrullar", atack, new OrderPatrol("Patrullar", militares));
		// Se comprueba si se puede atacar o patrullar, si se puede se hace
		Sequence atacar = new Sequence("MilitarContal", new CheckState("PosibleAtacar", militares), atackPatrol);
		Selector<GameHandler> atackDefense = new Selector<GameHandler>("Atack or defense", atacar, new DefenseMode("Defensa", militares));
		// Anyadimos los arboles
		recollectTree = new BehavioralTree("ArbolDecision de recoleccion");
		recollectTree.addChild(collectResources);
		creationTree = new BehavioralTree("ArbolDecision de creacion");
		creationTree.addChild(creation);
		explorationTree = new BehavioralTree("ArbolDecision de exploracion");
		explorationTree.addChild(explore);
		militaryTree = new BehavioralTree("ArbolDecision del control de los militares");
		militaryTree.addChild(atackDefense);

		
		// Edificios a construir
		CtrlVar.buildqueue.add(UnitTypes.Terran_Barracks);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Command_Center);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Engineering_Bay);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Bunker);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Missile_Turret);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Academy);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Armory);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Bunker);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Factory);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Starport);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Missile_Turret);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Comsat_Station);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Nuclear_Silo);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Science_Facility);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Control_Tower);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Missile_Turret);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		
		// Iniciamos el tiempo
		startTime = System.currentTimeMillis();
	}

	public void matchFrame() {
		explorationTree.run();
		recollectTree.run();
		creationTree.run();
		militaryTree.run();
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
