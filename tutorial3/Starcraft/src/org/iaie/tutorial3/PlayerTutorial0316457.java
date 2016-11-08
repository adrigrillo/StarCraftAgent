package org.iaie.tutorial3;

import org.iaie.Agent;
import org.iaie.btree.BehavioralTree;
import org.iaie.btree.task.composite.Selector;
import org.iaie.btree.task.composite.Sequence;
import org.iaie.btree.util.GameHandler;

import jnibwapi.BWAPIEventListener;
import jnibwapi.Position;

public class PlayerTutorial0316457 extends Agent implements BWAPIEventListener{
	
	// Creamos el arbol de decision
	private BehavioralTree collectTree;

	@Override
	public void connected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void matchStart() {
		BehaviourTree arbol = new BehaviourTree(bwapi);
		Selector<GameHandler> collectResources = new Selector<GameHandler>("collectResources", new CollectMineral("CollectMineral", arbol), new CollectGas("CollectGas", arbol));
		Sequence collect = new Sequence("collect", new FreeWorker("Search", arbol), new ChooseWorker("Choose", arbol), collectResources);
		Sequence train = new Sequence("Entrenar", new CheckResources("Comprobar", arbol), new ChooseBuilding("Elegir", arbol), new TrainUnit("Entrenar",  arbol));
		collectTree = new BehavioralTree("ArbolDecision");
		collectTree.addChild(collect);
		collectTree.addChild(train);
	}

	@Override
	public void matchFrame() {
		collectTree.run();
	}

	@Override
	public void matchEnd(boolean winner) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(int keyCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendText(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveText(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerLeft(int playerID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nukeDetect(Position p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nukeDetect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitDiscover(int unitID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitEvade(int unitID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitShow(int unitID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitHide(int unitID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitCreate(int unitID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitDestroy(int unitID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitMorph(int unitID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitRenegade(int unitID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveGame(String gameName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unitComplete(int unitID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerDropped(int playerID) {
		// TODO Auto-generated method stub
		
	}

}
