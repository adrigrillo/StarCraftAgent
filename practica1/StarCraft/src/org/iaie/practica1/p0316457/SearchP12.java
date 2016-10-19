package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.iaie.search.Successor;
import org.iaie.search.algorithm.Astar;

import jnibwapi.JNIBWAPI;

public class SearchP12 extends Astar{

	public SearchP12(JNIBWAPI map) {
		super(map.getMap());
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Successor> generateSuccessors(Point actualState) {
		List<Successor> listaSucesores = new ArrayList<Successor> ();
		Point punto = new Point ();
		System.out.println("Punto actual P12: "+ actualState.x + " "+ actualState.y);

		for (int x = actualState.x-1; x<=x+1;x++){
			for(int y = actualState.y-1;y<=y+1;y++){
				punto.setLocation(x, y);
				Successor sucesor = new Successor(punto);
				listaSucesores.add(sucesor);
				System.out.println("Punto: "+ punto.x + " "+ punto.y);

			}
		}
				
		return listaSucesores;
	}

	@Override
	public double calculateheuristic(Point state, Point goalState) {
		// TODO Auto-generated method stub
		return 0;
	}

}