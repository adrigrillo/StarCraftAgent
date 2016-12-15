package org.iaie.practica3.construction;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class BuildingState extends Conditional {

	public BuildingState(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si el edificio se ha terminado de construir y se puede construir otro
	 *  - running si el edificio se esta construyendo
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((ConstructionTree)this.handler).buildState();
		switch (res) {
			case -1:
				return State.ERROR;
			case 0:
				return State.RUNNING;
			default:
				return State.SUCCESS;
		}
	}
}
