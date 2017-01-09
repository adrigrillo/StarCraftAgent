package org.iaie.practica3.militarControl;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;

public class OrderPatrol extends Action{

	public OrderPatrol(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo para ordenar patrullar a las partidas
	 * Devolvera 1 si se mandan correctamente, devolvera 0 si falla alguna unidad, -1 error
	 */
	public State execute() {
		int res = ((MilitarTree)this.handler).orderPatrol();
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
