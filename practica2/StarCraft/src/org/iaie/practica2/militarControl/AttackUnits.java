package org.iaie.practica2.militarControl;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.militarControl.MilitarTree;

public class AttackUnits extends Conditional{

	public AttackUnits(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que atacara a otras unidades
	 */
	public State execute() {
		int res = ((MilitarTree)this.handler).attackUnits();
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
