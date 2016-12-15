package org.iaie.practica3.construction;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class FreeWorkerToBuild extends Conditional {

	public FreeWorkerToBuild(String name, GameHandler gh) {
		super(name, gh);
	}


	/**
	 * Metodo que devuelve:
	 *  - success si hay una unidad disponible para crear el edifici
	 *  - failure cuando no hay ninguna
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((ConstructionTree)this.handler).freeWorkerAvailable();
		switch (res) {
			case -2:
				return State.ERROR;
			case -1:
				return State.FAILURE;
			default:
				return State.SUCCESS;
		}
	}

}
