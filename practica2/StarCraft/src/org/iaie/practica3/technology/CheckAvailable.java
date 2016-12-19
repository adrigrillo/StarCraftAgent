package org.iaie.practica3.technology;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class CheckAvailable extends Conditional{

	public CheckAvailable(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que comprobara si tenemos edificios en los que desarollar
	 * mejoras
	 * - 1 si existe el edificio
	 * - 0 si no existe
	 * - -1 si hay error
	 */
	public State execute() {
		int res = ((TechTree)this.handler).checkBuildings();
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
