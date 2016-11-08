package org.iaie.tutorial3;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;


public class ChooseWorker extends Conditional{
	public ChooseWorker(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success selecciona un trabajador
	 *  - failure si no puede seleccionarlo
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
				((BehaviourTree)this.handler).selectWorker(res);
				return State.SUCCESS;
		}
	}

}
