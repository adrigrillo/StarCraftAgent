package org.iaie.tutorial3;

import org.iaie.Agent;
import org.iaie.btree.BehavioralTree;
import org.iaie.btree.task.composite.Selector;
import org.iaie.btree.task.composite.Sequence;
import org.iaie.btree.util.GameHandler;

import jnibwapi.BWAPIEventListener;
import jnibwapi.Position;

public class PlayerTutorial0316457 extends Agent implements BWAPIEventListener{

	@Override
	public void connected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void matchStart() {
		
		String name = null;
		GameHandler gh = null;
		CollectMineral collectMineral = new CollectMineral(name, gh);
		CollectGas collectGas = new CollectGas(name, gh);
		
		FreeWorker freeWorker = new FreeWorker(name, gh);
		ChooseWorker chooseWorker = new ChooseWorker(name, gh);
		
		CheckResources checkResources = new CheckResources(name, gh);
		ChooseBuilding chooseBuilding = new ChooseBuilding(name, gh);
		TrainUnit trainUnit = new TrainUnit(name, gh);
		
		Selector collectResources = new Selector(collectMineral, collectGas);
		Sequence collect = new Sequence(freeWorker, chooseWorker, collectResources);
		Sequence train = new Sequence(checkResources, chooseBuilding, trainUnit);
		BehavioralTree collectTree = new BehaviourTree(collect, train);
	}

	@Override
	public void matchFrame() {
		// TODO Auto-generated method stub
		
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
