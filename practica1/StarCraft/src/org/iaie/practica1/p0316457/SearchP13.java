package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.List;

import org.iaie.search.Successor;
import org.iaie.search.algorithm.Astar;

import jnibwapi.JNIBWAPI;

public class SearchP13 extends Astar{

	public SearchP13(JNIBWAPI map) {
		super(map.getMap());
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Successor> generateSuccessors(Point actualState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double calculateheuristic(Point state, Point goalState) {
		// TODO Auto-generated method stub
		return 0;
	}

}