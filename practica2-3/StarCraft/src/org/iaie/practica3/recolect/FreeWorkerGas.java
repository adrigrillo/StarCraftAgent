package org.iaie.practica3.recolect;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;


public class FreeWorkerGas extends Action{
	
	public FreeWorkerGas(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si se consigue un trabajador
	 *  - failure si no lo hay
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((RecolectTree)this.handler).freeWorkerAvailableGas();
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
