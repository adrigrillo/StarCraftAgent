package org.iaie.tutorial3;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

import jnibwapi.Unit;
import jnibwapi.*;



public class FreeWorker extends Conditional{

	// Tener acceso al mapa
    private JNIBWAPI bwapi;

	public FreeWorker(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	@Override
	public State execute() {
        for (Unit unit : this.bwapi.getMyUnits()) {
        	if (unit.isIdle()){        		
        		return state.SUCCESS;
        	}else{
        		return state.FAILURE;
        	}
        }        		
		return state.ERROR;
	}

}
