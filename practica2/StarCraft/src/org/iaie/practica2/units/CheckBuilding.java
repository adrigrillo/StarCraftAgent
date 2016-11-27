package org.iaie.practica2.units;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class CheckBuilding extends Conditional{
	
	public CheckBuilding(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si hay suficientes recursos para construir una unidad
	 *  - failure si no los hay
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((TrainingTree)this.handler).checkBuildingExist();
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
