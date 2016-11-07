package org.iaie.tutorial3;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;

import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;

public class CollectMineral extends Action{
	public CollectMineral(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}
	ChooseWorker cw = new ChooseWorker(null, null);
	
	@Override
	public State execute() {
		if(Unit unit : this.bwapi.getMyUnits()){
			if (!unit.isIdle()){
				return state.FAILURE;
			}else{
				if (Unit minerals : this.bwapi.getNeutralUnits()){
					unit.rightClick(minerals, false);
					return state.SUCCESS;
				}else{
					return state.FAILURE;					
				}
			}
		}
		return state.ERROR;
	}

}
