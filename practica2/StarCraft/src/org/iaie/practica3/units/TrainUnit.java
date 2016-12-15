package org.iaie.practica3.units;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;

public class TrainUnit extends Action {

	public TrainUnit(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si la unidad se ha terminado de crear y se puede crear otra
	 *  - running si la unidad se esta entrenando
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((TrainingTree)this.handler).trainUnit();
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
