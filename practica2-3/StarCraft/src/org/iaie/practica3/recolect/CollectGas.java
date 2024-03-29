package org.iaie.practica3.recolect;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;

public class CollectGas extends Action{

	public CollectGas(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo para enviar el trabajador a la refineria que devuelve:
	 *  - success si se manda al trabajador a recoger vespeno
	 *  - failure si no se le manda
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((RecolectTree)this.handler).collectGas();
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
