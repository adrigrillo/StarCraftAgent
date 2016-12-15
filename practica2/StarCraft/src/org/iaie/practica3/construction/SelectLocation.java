package org.iaie.practica3.construction;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;

public class SelectLocation extends Action{

	public SelectLocation(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si se selecciona una posicion correctamente
	 *  - failure si no se encuentra una posicion
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((ConstructionTree)this.handler).selectPosition();
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
