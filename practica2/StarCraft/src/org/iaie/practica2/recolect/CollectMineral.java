package org.iaie.practica2.recolect;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;


public class CollectMineral extends Action{
	public CollectMineral(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Metodo que devuelve:
	 *  - success si se hay suficientes recursos para entrenar una unidad
	 *  - failure si no los hay
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((RecolectTree)this.handler).collectMineral();
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
