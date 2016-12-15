package org.iaie.practica3.militarControl;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;
import org.iaie.practica3.militarControl.MilitarTree;


public class CheckState extends Conditional{

	public CheckState(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que comprobara el estado del jugador
	 * Devolvera 1 si se puede atacar, 0 si hay que defender. -1 error
	 */
	public State execute() {
		int res = ((MilitarTree)this.handler).checkState();
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
