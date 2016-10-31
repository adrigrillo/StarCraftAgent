package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.iaie.search.Successor;
import org.iaie.search.algorithm.Astar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Position.PosType;

public class SearchP12 extends Astar{
	private JNIBWAPI bwapi;
	
	public SearchP12(JNIBWAPI map) {
		super(map.getMap());
		this.bwapi = map;

	}

	@Override
	public List<Successor> generateSuccessors(Point actualState) {
		List<Successor> listaSucesores = new ArrayList<Successor> ();	
		/* Tomamos todos los posibles sucesores de la posicion actual, comprobamos los
		 * validos y los añadimos a la lista de sucesores */
		for (int x = actualState.x - 1; x <= actualState.x + 1; x++){
			for(int y = actualState.y - 1; y <= actualState.y + 1; y++){
				// Comprobamos que no se mete como sucesor al mismo elemento
				if (!new Point(x, y).equals(new Point(actualState.x, actualState.y))){
					if(bwapi.getMap().isWalkable(new Position(x, y, PosType.WALK))){
						listaSucesores.add(new Successor(new Point(x, y)));
					}
				}
			}
		}
		
		return listaSucesores;
	}

	@Override
	public double calculateheuristic(Point state, Point goalState) {
		return Math.sqrt(Math.pow(goalState.x - state.x, 2) + Math.pow(goalState.y - state.y, 2));
	}

}