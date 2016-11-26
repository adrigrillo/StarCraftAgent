package org.iaie.practica2.recolect;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;

public class TrainUnit extends Action{

	public TrainUnit(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si se comienza a entrenar la unidad correctamente
	 *  - failure si no se comienza a entrenar la unidad
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((RecolectTree)this.handler).trainWorker();
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
