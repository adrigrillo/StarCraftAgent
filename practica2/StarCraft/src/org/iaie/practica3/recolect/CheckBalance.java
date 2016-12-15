package org.iaie.practica3.recolect;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class CheckBalance extends Conditional{

	public CheckBalance(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Metodo que comprobara si enviar las unidades a recoger
	 * minerales o vespeno
	 * - 1 si hay que recoger gas
	 * - 0 si hay que recoger mineral
	 * - -1 si hay error
	 */
	public State execute() {
		int res = ((RecolectTree)this.handler).checkDistribution();
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
