package org.iaie.tutorial3;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;


public class FreeWorker extends Conditional{
	
	public FreeWorker(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si hay algun trabajador libre
	 *  - failure si no lo hay
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((BehaviourTree)this.handler).freeWorkerAvailable();
		switch (res) {
			case -1:
				return State.FAILURE;
			case -2:
				return State.ERROR;
			default:
				return State.SUCCESS;
		}
	}
}
