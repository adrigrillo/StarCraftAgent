package org.iaie.practica2.militarControl;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;
import org.iaie.practica2.militarControl.MilitarTree;


public class CheckState extends Conditional{

	private int estado = 0;
	public CheckState(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que comprobara el estado del jugador
	 */
	public State execute() {
		int res = ((MilitarTree)this.handler).checkState();
		switch (res) {
			case -1:
				return State.ERROR;
			case 0:
				return State.FAILURE;
			default:
				estado = res;
				return State.SUCCESS;
		}
	}

}
