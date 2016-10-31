package org.iaie.practica1.p0316457;

import java.awt.Point;
import java.text.DecimalFormat;
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
        PriorityQueue<SearchNode> openList = new PriorityQueue<>(new Comparator<SearchNode>() {
        	public int compare(SearchNode node1, SearchNode node2){
        		return (node1.getH() + node1.getG()) < (node2.getH() + node2.getG()) ? -1 : (node1.getH() + node1.getG()) == (node2.getH() + node2.getG()) ? 0 : 1;
        	}
		}); // Open List
        HashSet<SearchNode> closeList = new HashSet<>(); // Close List
        HashMap<Point, Double> costList = new HashMap<>();
        Result resFinal = null;
        
        // Iniciamos la lista donde pondremos lo nodos ampliados
        openList.add(new SearchNode(start, 0, regionFinder.calculateheuristic(start, end), 0));
        
        while (!openList.isEmpty()) {
        	// Sacamos la primera posicion de la lista
            SearchNode actualState = openList.poll();
            
            // Si no es el mismo nodo en el que estamos calculamos el camino
            if(actualState.getPosition() != start){
            	
            }
            
            // Si la region de la posicion es igual que la region de la meta se busca el camino directo
            if (obtainData.regionOfPosition(actualState.getPosition().x, actualState.getPosition().y) == obtainData.regionOfPosition(end.x, end.y)) {
            	//LLamar a p12 para buscar camino
            }
            
            closeList.add(actualState);
            expandedNodes++;

            if (this.debugMode) System.out.println("Expanded Node(H:" + new DecimalFormat("#.##").format(actualState.getH()) + " G:" + new DecimalFormat("#.##").format(actualState.getG()) + "): Position(" + actualState.getPosition().x  + ", " + actualState.getPosition().y + ").");

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
                    	for (Iterator<SearchNode> it = openList.iterator(); it.hasNext();) {
                    		if (it.next().getPosition().equals(successor.getCoordinate()))
                    			it.remove();
	                    }
                    	SearchNode newNode = new SearchNode (successor.getCoordinate(), actualState, newg, regionFinder.calculateheuristic(successor.getCoordinate(), end), successor.getCost());
                    	openList.add(newNode);
                        generatedNodes++;
                    }
                }
            }
        }
		return null;
	}



}