package org.iaie.practica3.technology;

import org.iaie.btree.state.State;
import org.iaie.btree.task.leaf.Conditional;
import org.iaie.btree.util.GameHandler;

public class CheckResources extends Conditional{

	public CheckResources(String name, GameHandler gh) {
		super(name, gh);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Metodo que mira los recursos disponibles, si son mayor
	 * de los establecidos manda desarrollar mejoras
	 * - 1 se tienen recursos
	 * - 0 no se tienen recursos
	 * - -1 si hay error
	 */
	public State execute() {
		int res = ((TechTree)this.handler).checkResources();
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
