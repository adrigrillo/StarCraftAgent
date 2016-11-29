package org.iaie.practica2.movements;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;

public class CheckStateUnit extends Action{

	public CheckStateUnit(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que comprobara el estado de la unidad
	 */
	public State execute() {
		int res = ((MovementTree)this.handler).checkStateUnit();
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
