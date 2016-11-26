package org.iaie.practica2.units;

import org.iaie.btree.util.GameHandler;

import jnibwapi.JNIBWAPI;

public class TrainingTree extends GameHandler {

	public TrainingTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}

}
