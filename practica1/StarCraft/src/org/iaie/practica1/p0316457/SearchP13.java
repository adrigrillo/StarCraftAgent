package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.List;

import org.iaie.search.Successor;
import org.iaie.search.algorithm.Astar;

import jnibwapi.JNIBWAPI;

public class SearchP13 extends Astar{

	private JNIBWAPI bwapi;
	
	public SearchP13(JNIBWAPI map) {
		super(map.getMap());
		this.bwapi = map;
	}

	@Override
	public List<Successor> generateSuccessors(Point actualState) {
		
		return null;
	}

	@Override
	public double calculateheuristic(Point state, Point goalState) {
		// TODO Auto-generated method stub
		return 0;
	}

}