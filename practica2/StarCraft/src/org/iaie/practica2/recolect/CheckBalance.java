package org.iaie.practica2.recolect;

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
	 */
	public State execute() {
		int res = ((RecolectTree)this.handler).checkDistribution();
		switch (res) {
			case 0:
				return State.FAILURE;
			case -2:
				return State.ERROR;
			default:
				return State.SUCCESS;
		}
	}

}
