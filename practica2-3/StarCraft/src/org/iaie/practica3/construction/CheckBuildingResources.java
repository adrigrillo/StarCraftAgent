package org.iaie.practica3.construction;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class CheckBuildingResources extends Conditional {

	public CheckBuildingResources(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si se hay suficientes recursos para construir un edificio
	 *  - failure si no los hay
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((ConstructionTree)this.handler).checkBuildingsResources();
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
