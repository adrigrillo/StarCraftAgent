package org.iaie.tutorial3;

import org.iaie.btree.BehavioralTree;
import org.iaie.btree.task.Task;
import org.iaie.btree.task.composite.*;
import org.iaie.btree.util.GameHandler;

import jnibwapi.JNIBWAPI;

public class BehaviourTree extends GameHandler{

	public BehaviourTree(JNIBWAPI bwapi) {
		super(bwapi);
		// TODO Auto-generated constructor stub
	}
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
