package org.iaie.practica3.technology;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;

public class Upgrade extends Action {

	public Upgrade(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que manda a desarrollar una mejora si el edificio
	 * seleccionado puede
	 * - 1 si se manda correctamente a desarrollar
	 * - 0 si no ha podido (por falta de recursos generalmente)
	 * - -1 si hay error
	 */
	public State execute() {
		int res = ((TechTree)this.handler).checkDevelopments();
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
