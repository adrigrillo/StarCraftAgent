package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.iaie.search.Successor;
import org.iaie.search.algorithm.Astar;

import jnibwapi.JNIBWAPI;

public class SearchP12 extends Astar{
	private JNIBWAPI map;
	
	public SearchP12(JNIBWAPI map) {
		super(map.getMap());
		this.map = map;

	}

	@Override
	public List<Successor> generateSuccessors(Point actualState) {
		List<Successor> listaSucesores = new ArrayList<Successor> ();
		Point punto = new Point ();

		for (int x = actualState.x-1; x<=x+1;x++){
			for(int y = actualState.y-1;y<=y+1;y++){
				if(x>=0 && y>=0 && x<map.getMap().getSize().getBX() && y<map.getMap().getSize().getBY()){
					punto.setLocation(x, y);
					Successor sucesor = new Successor(punto);
					listaSucesores.add(sucesor);
				}
			}
		}
				
		return listaSucesores;
	}

	@Override
	public double calculateheuristic(Point state, Point goalState) {
		double x = Math.pow((goalState.x-state.x), 2);
		double y = Math.pow((goalState.y-state.y), 2);
		double distancia = Math.sqrt((x+y));

		return distancia;
	}

}