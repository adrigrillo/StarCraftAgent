package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.List;

import org.iaie.search.Result;
import org.iaie.search.Successor;
import org.iaie.search.algorithm.HierarchicalSearch;

import jnibwapi.JNIBWAPI;

public class HSearch extends HierarchicalSearch{

	public HSearch(JNIBWAPI map) {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Successor> generateSuccessor(Point actualState) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int calculateheuristic(Point state, Point goalState) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Result search(Point start, Point end) {
		// TODO Auto-generated method stub
		return null;
	}



}