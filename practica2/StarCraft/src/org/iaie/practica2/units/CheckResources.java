package org.iaie.practica2.units;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class CheckResources extends Conditional{

	public CheckResources(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que devuelve:
	 *  - success si hay suficientes recursos para construir una unidad
	 *  - failure si no los hay
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((TrainingTree)this.handler).checkUnitResources();
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
