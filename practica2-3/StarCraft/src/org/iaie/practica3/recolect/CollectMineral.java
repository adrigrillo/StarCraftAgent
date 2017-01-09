package org.iaie.practica3.recolect;

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
	 *  - success si se manda a la unidad a recoger minerales
	 *  - failure si no se le manda
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((RecolectTree)this.handler).collectMineral();
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
