package org.iaie.practica2.recolect;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class CheckRefinery extends Conditional{

	public CheckRefinery(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que comprobara si esta construida la refineria
	 * - Success si es asi
	 * - Failure si no esta construida
	 * - Error si hay algun error
	 */
	public State execute() {
		int res = ((RecolectTree)this.handler).refineryBuilt();
		switch (res) {
			case -1:
				return State.ERROR;
			case 0:
				return State.FAILURE;
			default:
				return State.SUCCESS;
		}
	}

}
