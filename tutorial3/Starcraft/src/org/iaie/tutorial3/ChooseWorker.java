package org.iaie.tutorial3;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;

public class ChooseWorker extends Conditional{
	public ChooseWorker(String name, GameHandler gh) {
		super(name, gh);
	}
	
	public int ID_trabajador = -1;

	@Override
	public State execute() {
        for (Unit unit : this.bwapi.getMyUnits()) {
        	if (unit.isIdle()){ 
        		ID_trabajador = unit.getID();
        		return state.SUCCESS;
        	}else{
        		return state.FAILURE;
        	}
        } 

		return state.ERROR;
	}

}
