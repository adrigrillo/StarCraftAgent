package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.iaie.search.Successor;
import org.iaie.search.algorithm.Astar;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Position.PosType;

public class SearchP11 extends Astar{
	private JNIBWAPI bwapi;

	public SearchP11(JNIBWAPI map) {
		super(map.getMap());
		this.bwapi = map;
	}

	@Override
	public List<Successor> generateSuccessors(Point actualState) {
		/* Creamos en tipo posicion para usar isWalkable() */
		List<Position> posiciones = new ArrayList<Position>();
		posiciones.add(new Position(actualState.x, actualState.y - 1, PosType.WALK));
		posiciones.add(new Position(actualState.x, actualState.y + 1, PosType.WALK));
		posiciones.add(new Position(actualState.x + 1, actualState.y, PosType.WALK));
		posiciones.add(new Position(actualState.x - 1, actualState.y, PosType.WALK));

		/* Comprobamos las posiciones y las metemos en la lista */
		List<Successor> listaSucesores = new ArrayList<Successor> ();
		for (int i = 0; i < posiciones.size(); i++){
			if (bwapi.getMap().isWalkable(posiciones.get(i))){
				listaSucesores.add(new Successor(new Point(posiciones.get(i).getWX(), posiciones.get(i).getWY())));
			}
		}
		return listaSucesores;
	}

	@Override
	public double calculateheuristic(Point state, Point goalState) {
		double distancia = Math.abs(state.x-goalState.x) + Math.abs(state.y-goalState.y);
		return distancia;
	}

}
