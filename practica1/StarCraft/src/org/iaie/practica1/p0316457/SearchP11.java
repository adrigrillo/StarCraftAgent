package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.iaie.search.Successor;
import org.iaie.search.algorithm.Astar;

import jnibwapi.JNIBWAPI;

public class SearchP11 extends Astar{

	public SearchP11(JNIBWAPI map) {
		super(map.getMap());
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Successor> generateSuccessors(Point actualState) {
		System.out.println("Punto actual: "+ actualState.x + " "+ actualState.y);
		List<Successor> listaSucesores = new ArrayList<Successor> ();
		/*Punto de arriba*/
		Point punto = new Point ();
		punto.setLocation(actualState.x, actualState.y-1);		
		Successor sucesor = new Successor(punto);
		listaSucesores.add(sucesor);
		//System.out.println("Punto arriba: "+ punto.getX() + " "+ punto.getY());
		/*Punto de abajo*/
		punto.setLocation(actualState.x, actualState.y+1);		
		sucesor = new Successor(punto);
		listaSucesores.add(sucesor);
		//System.out.println("Punto abajo: "+ punto.getX() + " "+ punto.getY());
		/*Punto de la izquierda*/
		punto.setLocation(actualState.x-1, actualState.y);		
		sucesor = new Successor(punto);
		listaSucesores.add(sucesor);
		//System.out.println("Punto izquiera: "+ punto.getX() + " "+ punto.getY());
		/*Punto de la derecha*/
		punto.setLocation(actualState.x+1, actualState.y);		
		sucesor = new Successor(punto);
		listaSucesores.add(sucesor);
		//System.out.println("Punto derecha: "+ punto.getX() + " "+ punto.getY());
		/**/
		
		return listaSucesores;
	}

	@Override
	public double calculateheuristic(Point state, Point goalState) {
		// TODO Auto-generated method stub
		return 0;
	}

}
