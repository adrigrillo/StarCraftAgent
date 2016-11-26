package org.iaie.practica2.construction;

import org.iaie.btree.util.GameHandler;

import jnibwapi.JNIBWAPI;

public class ConstructionTree extends GameHandler{

	public ConstructionTree(JNIBWAPI bwapi) {
		super(bwapi);
		this.connector = bwapi;
	}

}
