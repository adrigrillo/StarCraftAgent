package org.iaie.practica2.militarControl;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class DefenseMode extends Conditional {

	public DefenseMode(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que comprobara si estan atacando y entrara en modo defensa
	 */
	public State execute() {
		int res = ((MilitarTree)this.handler).defenseMode();
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
