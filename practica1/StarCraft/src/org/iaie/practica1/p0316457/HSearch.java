package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.List;

import org.iaie.search.Result;
import org.iaie.search.Successor;
import org.iaie.search.algorithm.HierarchicalSearch;

import jnibwapi.JNIBWAPI;

public class HSearch extends HierarchicalSearch{

	private JNIBWAPI bwapi;
	private HierarchicalMap obtainData;
	private SearchP13 regionFinder;
	
	public HSearch(JNIBWAPI map) {
		super();
		this.bwapi = map;
        // Llamamos a la ejecucion para obtener datos del mapa
        obtainData = new HierarchicalMap(bwapi);
        regionFinder = new SearchP13(this.bwapi);
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
		
		SearchP12 pathFinder = new SearchP12(bwapi);
		regionFinder.generateSuccessors(start);
		regionFinder.calculateheuristic(start, end);
		return null;
	}



}