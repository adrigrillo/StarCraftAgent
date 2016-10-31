package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.iaie.search.Result;
import org.iaie.search.Successor;
import org.iaie.search.algorithm.HierarchicalSearch;
import org.iaie.search.node.SearchNode;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Position.PosType;

public class HSearch extends HierarchicalSearch{

	private JNIBWAPI bwapi;
	private HierarchicalMap obtainData;
	private SearchP13 regionFinder;
	private SearchP12 pathFinder;
	
	public HSearch(JNIBWAPI map) {
		super();
		this.bwapi = map;
        // Llamamos a la ejecucion para obtener datos del mapa
        obtainData = new HierarchicalMap(bwapi);
        regionFinder = new SearchP13(bwapi);
        pathFinder = new SearchP12(bwapi);
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
		// Creamos los objetos y estructuras que serán necesarias
		obtainData.exec();
		int expandedNodes = 0;
        int generatedNodes = 1;
        SearchNode actualState;
        SearchNode nextState;
        List<SearchNode> openList = new ArrayList<>();
        HashSet<SearchNode> closeList = new HashSet<>(); // Close List
        HashMap<Point, Double> costList = new HashMap<>();
        Result resFinal = null;
        long time = 0;
        int cost = 0;
        int expandedNodesGlobal = 0;
        int generatedNodesGlobal = 0;
        List<Point> path = new ArrayList<>();
        boolean solucion = false;
        
        // Iniciamos la lista donde pondremos lo nodos ampliados
        openList.add(new SearchNode(start, 0, regionFinder.calculateheuristic(start, end), 0));
        actualState = new SearchNode(start, 0, regionFinder.calculateheuristic(start, end), 0);
        
        while (!openList.isEmpty()) {
            // Si la region de la posicion es igual que la region de la meta se busca el camino directo
            if (obtainData.regionOfPosition(actualState.getPosition().x, actualState.getPosition().y) == obtainData.regionOfPosition(end.x, end.y)) {
            	// Calculamos el camino desde el chokepoint hasta el punto
            	resFinal = pathFinder.search(actualState.getPosition().getLocation(), end);
            	solucion = true;
            	break;
            }
        	
        	// Sacamos la primera posicion de la lista
            nextState = openList.remove(0);
            
            // Si no es el mismo nodo en el que estamos calculamos el camino
            if(actualState.getPosition() != nextState.getPosition()){
            	resFinal = pathFinder.search(actualState.getPosition(), nextState.getPosition());
            	time += resFinal.getTime();
            	cost += resFinal.getCost();
            	expandedNodesGlobal += resFinal.getExpandedNodes();
            	generatedNodesGlobal += resFinal.getGeneratedNodes();
            	path.addAll(resFinal.getPath());
            }
            
            // Metemos el estado actual en la lista de recorridos y pasamos el siguiente al estado actual
            closeList.add(actualState);
            actualState = nextState;
            expandedNodes++;

            // Obtenemos los sucesores
            List<Successor> successors = regionFinder.generateSuccessors(actualState.getPosition());
            
            // Recorremos los sucesores
            for (Successor successor: successors) {
            	// Si el sucesor no esta en la lista ya cerrada, lo examinamos
                if (!closeList.contains(successor)) {
                	// Calculamos el coste hasta el momento
                    double newg = actualState.getG() + successor.getCost();
                    if (!costList.containsKey(successor.getCoordinate()) || costList.get(successor.getCoordinate()) > newg) {
                    	costList.put(successor.getCoordinate(), newg);
                    	for (int i = 0; i < openList.size(); i++){
                    		if (openList.get(i).getPosition().equals(successor.getCoordinate())){
                    			openList.remove(i);
                    		}
                    	}
                    	SearchNode newNode = new SearchNode (successor.getCoordinate(), actualState, newg, regionFinder.calculateheuristic(successor.getCoordinate(), end), successor.getCost());
                    	boolean menor = false;
                    	for(int i = 0; i < openList.size(); i++){
                    		if ((newNode.getH() + newNode.getG()) < (openList.get(i).getH() + openList.get(i).getG())){
                    			openList.add(i, newNode);
                    			menor = true;
                    			break;
                    		}
                    	}
                    	if (menor == false){
                    		openList.add(newNode);
                    	}	
                        generatedNodes++;
                    }
                }
            }
        }
        // Sumamos los resultados y creamos la solucion final
        time += resFinal.getTime();
    	cost += resFinal.getCost();
    	expandedNodesGlobal += resFinal.getExpandedNodes();
    	generatedNodesGlobal += resFinal.getGeneratedNodes();
        if (solucion == true){
        	path.addAll(resFinal.getPath());
        }
        else {
        	path = null;
        }
        resFinal = new Result(path, generatedNodesGlobal, expandedNodesGlobal, cost, time);
		return resFinal;
	}



}