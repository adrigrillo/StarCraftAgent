package org.iaie.practica3.militarControl;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;
import org.iaie.practica3.militarControl.MilitarTree;

public class AttackUnits extends Conditional{

	public AttackUnits(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que atacara a otras unidades
	 * - 1 si se ataca correctamente
	 * - 0 si no se pueden mandar las unidades
	 * - -1 si hay algun error
	 */
	public State execute() {
		int res = ((MilitarTree)this.handler).attackObjetive();
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
