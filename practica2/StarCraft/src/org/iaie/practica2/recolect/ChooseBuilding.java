package org.iaie.practica2.recolect;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Action;
import org.iaie.btree.util.GameHandler;

public class ChooseBuilding extends Action{

	public ChooseBuilding(String name, GameHandler gh) {
		super(name, gh);
	}

	/**
	 * Metodo que devuelve:
	 *  - success si se ha seleccionado correctamente el edificio
	 *  - failure si no se ha seleccionado
	 *  - error si se produce algun error
	 */
	public State execute() {
		int res = ((RecolectTree)this.handler).chooseBuilding();
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
