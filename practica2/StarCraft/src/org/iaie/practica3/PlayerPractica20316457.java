package org.iaie.practica3;

import java.awt.Point;
import java.io.File;
import java.io.PrintWriter;

import org.iaie.Agent;
import org.iaie.btree.BehavioralTree;
import org.iaie.btree.task.composite.Selector;
import org.iaie.btree.task.composite.Sequence;
import org.iaie.btree.util.GameHandler;
import org.iaie.practica3.construction.BuildBuilding;
import org.iaie.practica3.construction.BuildingState;
import org.iaie.practica3.construction.CheckBuildingResources;
import org.iaie.practica3.construction.ConstructionTree;
import org.iaie.practica3.construction.FreeWorkerToBuild;
import org.iaie.practica3.construction.SelectLocation;
import org.iaie.practica3.militarControl.AtackOrPatrol;
import org.iaie.practica3.militarControl.AttackUnits;
import org.iaie.practica3.militarControl.CheckState;
import org.iaie.practica3.militarControl.DefenseMode;
import org.iaie.practica3.militarControl.MilitarTree;
import org.iaie.practica3.militarControl.OrderPatrol;
import org.iaie.practica3.movements.CheckPositionUnit;
import org.iaie.practica3.movements.CheckStateUnit;
import org.iaie.practica3.movements.MovementTree;
import org.iaie.practica3.movements.SendUnit;
import org.iaie.practica3.recolect.*;
import org.iaie.practica3.units.*;
import org.iaie.tools.Options;

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
		// flags
		// Revisar. 
        // Mediante esté metodo se puede obtener información del usuario. 
        if (Options.getInstance().getUserInput()) this.bwapi.enableUserInput();
        // Mediante este método se activa la recepción completa de información.
        if (Options.getInstance().getInformation()) this.bwapi.enablePerfectInformation();
        // Mediante este método se define la velocidad de ejecución del videojuego. 
        // Los valores posibles van desde 0 (velocidad estándar) a 10 (velocidad máxima).
        this.bwapi.setGameSpeed(Options.getInstance().getSpeed());
        
        
		// Iniciamos las variables de control y el mapa
		CtrlVar.clearAll();
		MapHandler.generateMapSpaces(bwapi);
		InfluenceMap mapaInfluencia = new InfluenceMap(bwapi.getMap().getSize().getBX(), bwapi.getMap().getSize().getBY());
		
		// Anadimos las unidades los scv iniciales en el hashset y los edificios
		for (Unit unit : bwapi.getMyUnits()){
			if (unit.getType().isWorker()){
				CtrlVar.workers.add(unit);
			}
		}
		CtrlVar.refreshBuildings(bwapi);
		
		// Establecemos los diferentes arboles
		RecolectTree recolectar = new RecolectTree(bwapi);
		TrainingTree entrenar = new TrainingTree(bwapi);
		ConstructionTree construir = new ConstructionTree(bwapi);
		MovementTree explorar = new MovementTree(bwapi);
		MilitarTree militares = new MilitarTree(bwapi);
		
		/* Arbol de recoleccion */
		/* Collect gas: Miramos equilibrio, si se necesita gas, miramos si esta construida la refineria, cogemos un trabajador y recogemos gas */
		Sequence collectGas = new Sequence("RecolectarGas", new CheckBalance("Balance", recolectar), new CheckRefinery("Comprobar refineria", recolectar), new FreeWorkerGas("TrabajadorGas", recolectar), new CollectGas("CollectGas", recolectar));
		/* Collect mineral: Si no se necesita gas, buscamos un trabajador libre, recogemos minerales */
		Sequence collectMineral = new Sequence("RecolectarMineral", new FreeWorkerMineral("TrabajadorMineria", recolectar), new CollectMineral("CollectMineral", recolectar));
		Selector<GameHandler> collectResources = new Selector<GameHandler>("Recolectar recursos", collectGas, collectMineral);
		
		/* Arbol de creacion */
		/* Entrenamiento de unidades */
		Sequence train = new Sequence("Entrenar Unidad", new CheckTraining("Comprobar proceso", entrenar), new CheckPopulation("Comprobar poblacion", entrenar), new CheckBuilding("Comprobar edificios", entrenar), new CheckUnitResources("Comprobar recursos", entrenar), new TrainUnit("Entrenar", entrenar));
		/* Construccion de edificios */
		Sequence build = new Sequence("Construir edificio", new BuildingState("Comprobar proceso", construir), new CheckBuildingResources("Comprobar recursos", construir), new SelectLocation("Obtener localizacion", construir), new FreeWorkerToBuild("Buscar trabajador", construir), new BuildBuilding("Construir", construir));
		Selector<GameHandler> creation = new Selector<GameHandler>("Entrenar o construir", build, train);
		
		/* Arbol de exploracion */
		Sequence explore = new Sequence("Explorar", new CheckStateUnit("Comprobar el estado de la unidad", explorar), new CheckPositionUnit("Tomar posicion", explorar), new SendUnit("Mandar a la posicion", explorar));
		
		/* Arbol para el control de los militares */
		// Comprobamos que se puede atacar, si se puede ataca
		Sequence atack = new Sequence("Atacar", new AtackOrPatrol("Comprobar posibilidad", militares), new AttackUnits("Atacar", militares));
		// Si no se puede patrulla
		Selector<GameHandler> atackPatrol = new Selector<GameHandler>("Atacar o Patrullar", atack, new OrderPatrol("Patrullar", militares));
		// Se comprueba si se puede atacar o patrullar, si se puede se hace
		Sequence atacar = new Sequence("Control Militar", new CheckState("Atacar o defender", militares), atackPatrol);
		Selector<GameHandler> atackDefense = new Selector<GameHandler>("Atack or defense", atacar, new DefenseMode("Defensa", militares));
		// Anyadimos los arboles
		recollectTree = new BehavioralTree("Arbol decision de recoleccion");
		recollectTree.addChild(collectResources);
		creationTree = new BehavioralTree("Arbol decision de creacion");
		creationTree.addChild(creation);
		explorationTree = new BehavioralTree("Arbol decision de exploracion");
		explorationTree.addChild(explore);
		militaryTree = new BehavioralTree("Arbol decision del control de los militares");
		militaryTree.addChild(atackDefense);

		
		// Edificios a construir
		CtrlVar.buildqueue.add(UnitTypes.Terran_Barracks);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Engineering_Bay);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Command_Center);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Bunker);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Academy);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Armory);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Bunker);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Barracks);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Starport);
		CtrlVar.buildqueue.add(UnitTypes.Terran_Science_Facility);
		
		// Unidades a construir
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		CtrlVar.trainqueue.add(UnitTypes.Terran_Marine);
		
		// Iniciamos el tiempo
		startTime = System.currentTimeMillis();
	}

	public void matchFrame() {
		// Llamadas a los arboles
		explorationTree.run();
		recollectTree.run();
		creationTree.run();
		militaryTree.run();
	}
	
	public void matchEnd(boolean winner) {
		try {
			// Aqui creamos el archivo de resultados y lo rellenamos con lo necesario
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
	
	
	/*
	 * Esta sale cada vez que la unidad descubre o ve algo es igual que show
	 * y me fio mas de show
	 * @see jnibwapi.BWAPIEventListener#unitDiscover(int)
	 */
	public void unitDiscover(int unitID) {
		System.out.println("He descubierto algo " + bwapi.getUnit(unitID).getType().getName() + unitID);	
	}
	
	public void unitEvade(int unitID) {
		// TODO Auto-generated method stub
	}
	
	/*
	 * (non-Javadoc)
	 * @see jnibwapi.BWAPIEventListener#unitShow(int)
	 */
	public void unitShow(int unitID) {
		System.out.println("algo ha salido " + bwapi.getUnit(unitID).getType().getName() + unitID);	
	}
	
	/*
	 * No entiendo muy bien, porque cuando quieres acceder a la unidad falla, ya 
	 * que el jugador pasa a no tener informacion sobre ella. 
	 * Funciona si tenemos puesto el acceso a la informacion completa
	 * @see jnibwapi.BWAPIEventListener#unitHide(int)
	 */
	public void unitHide(int unitID) {}
	
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
