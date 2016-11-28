package org.iaie.practica2.movements;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class SendUnit extends Conditional{

	public SendUnit(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que comprobara si se ha enviado la unidad
	 */
	public State execute() {
		int res = ((MovementTree)this.handler).moveUnit();
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
