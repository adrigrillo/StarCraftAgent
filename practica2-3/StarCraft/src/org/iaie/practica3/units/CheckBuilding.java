package org.iaie.practica3.units;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class CheckBuilding extends Conditional{
	
	public CheckBuilding(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si los edificios necesarios estan creados
	 *  - failure si no lo estan
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
