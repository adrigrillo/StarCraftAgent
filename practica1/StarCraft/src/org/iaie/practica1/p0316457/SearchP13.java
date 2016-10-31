package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iaie.search.Successor;
import org.iaie.search.algorithm.Astar;

import jnibwapi.ChokePoint;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Position.PosType;

public class SearchP13 extends Astar{

	private JNIBWAPI bwapi;
	
	public SearchP13(JNIBWAPI map) {
		super(map.getMap());
		this.bwapi = map;
	}
	
	private HierarchicalMap hm = new HierarchicalMap(bwapi);

	@Override
	public List<Successor> generateSuccessors(Point actualState) {
		// Creamos la lista de sucesores
		List<Successor> sucesores = new ArrayList<Successor>();
		// Buscamos la region del punto actual
		Position actual = new Position(actualState.x, actualState.y, PosType.WALK);
		int idRegion = hm.regionOfPosition(actual.getWX(), actual.getWY());
		// Obtenemos la lista de regiones con sus adyacentes y los puntos que las unen
		HashMap<Integer, HashMap<Integer, ChokePoint>> conectionpoints = hm.getConnectionPoints();
		if(conectionpoints.containsKey(idRegion)){
			HashMap<Integer, ChokePoint> adjConections = conectionpoints.get(idRegion);
			for(ChokePoint conexion : adjConections.values()){
				if (conexion.getFirstRegion().getID() == idRegion){
					sucesores.add(new Successor(new Point(conexion.getSecondSide().getWX(), conexion.getSecondSide().getWY())));
				}
				else{
					sucesores.add(new Successor(new Point(conexion.getSecondSide().getWX(), conexion.getSecondSide().getWY())));
				}
			}
		}
		return sucesores;
	}

	@Override
	public double calculateheuristic(Point state, Point goalState) {
		return Math.sqrt(Math.pow(goalState.x - state.x, 2) + Math.pow(goalState.y - state.y, 2));
	}

}