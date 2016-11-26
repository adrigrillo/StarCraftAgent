package org.iaie.practica2.recolect;

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
		int res = ((RecolectTree)this.handler).freeWorkerAvailable();
		switch (res) {
			case -1:
				return State.FAILURE;
			case -2:
				return State.ERROR;
			default:
				((RecolectTree)this.handler).selectWorker(res);
				return State.SUCCESS;
		}
	}
}
