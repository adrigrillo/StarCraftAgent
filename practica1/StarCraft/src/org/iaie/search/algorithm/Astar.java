/************************************************************************
 * Planning and Learning Group PLG,
 * Department of Computer Science,
 * Carlos III de Madrid University, Madrid, Spain
 * http://plg.inf.uc3m.es
 * 
 * Copyright 2015, Moises Martinez
 *
 * (Questions/bug reports now to be sent to Moisés Martínez)
 *
 * This file is part of IAIE.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the IAIE nor the names of its contributors may be 
 *       used to endorse or promote products derived from this software without 
 *       specific prior written permission.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with IAIE. If not, see <http://www.gnu.org/licenses/>.
 * 
 ************************************************************************/

package org.iaie.search.algorithm;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import org.iaie.search.Result;
import org.iaie.search.Successor;
import org.iaie.search.node.SearchNode;

public abstract class Astar {
    
    protected final double movementCost;
    protected final double movementCostDiag;
    protected final Object map;
    protected boolean debugMode = false;
    
    public abstract List<Successor> generateSuccessors(Point actualState);
    public abstract double calculateheuristic(Point state, Point goalState); 
    
    public Astar(Object map) {
        this.movementCost = 1;
        this.movementCostDiag = 1;
        this.map = map;
    }
    
    public Astar(double cost, Object map) {
        this.movementCost = cost;
        this.movementCostDiag = cost;
        this.map = map;
    }
    
    public Astar(double cost, double diagonalCost, Object map) {
        this.movementCost = cost;
        this.movementCostDiag = diagonalCost;
        this.map = map;
    }
    
    public void setDebug(boolean mode) {
    	this.debugMode = mode;
    }
    
    public Result search(Point start, Point end) {
		
    	int expandedNodes = 0;
        int generatedNodes = 1;
        PriorityQueue<SearchNode> openList = new PriorityQueue<>(); // Open List
        HashSet<SearchNode> closeList = new HashSet<>(); // Close List
        HashMap<Point, Double> costList = new HashMap<>();
	
        long time = System.currentTimeMillis();
        
        if (this.debugMode) System.out.println("Initial Position(" + start.x  + ", " + start.y + ")");
        
        //Add initial state
        openList.add(new SearchNode(start, 0, this.calculateheuristic(start, end), 0));
        costList.put(start, 0.0);
        
        while (!openList.isEmpty()) {
            SearchNode actualState = openList.poll();
            
            if (actualState.getPosition().equals(end)) {
            	Object[] path = this.generatePath(actualState);
            	return new Result((List<Point>) path[0], generatedNodes, expandedNodes, (int) path[1], System.currentTimeMillis() - time);
            }
            
            closeList.add(actualState);
            expandedNodes++;

            if (this.debugMode) System.out.println("Expanded Node(H:" + new DecimalFormat("#.##").format(actualState.getH()) + " G:" + new DecimalFormat("#.##").format(actualState.getG()) + "): Position(" + actualState.getPosition().x  + ", " + actualState.getPosition().y + ").");

            //GenerateSuccessors
            List<Successor> successors = this.generateSuccessors(actualState.getPosition());

            for (Successor successor: successors) {
                
                if (!closeList.contains(successor)) {
                    
                    double newg = actualState.getG() + successor.getCost();
                    
                    if (!costList.containsKey(successor.getCoordinate()) || costList.get(successor.getCoordinate()) > newg) {
                    	costList.put(successor.getCoordinate(), newg);
                    	for (Iterator<SearchNode> it = openList.iterator(); it.hasNext();) {
                    		if (it.next().getPosition().equals(successor.getCoordinate()))
                    			it.remove();
	                    }
                    	SearchNode newNode = new SearchNode (successor.getCoordinate(), actualState, newg, this.calculateheuristic(successor.getCoordinate(), end), successor.getCost());
                    	openList.add(newNode);
                        generatedNodes++;
                    }
                }
            }
        }
        return new Result(null, generatedNodes, expandedNodes, 0, System.currentTimeMillis() - time);
        
	}
    
    private Object[] generatePath(SearchNode goalState) {
    	
    	int cost = 0;
    	List<Point> path = new ArrayList<>();
    	SearchNode state = goalState;
    	
    	while (state != null) {
            path.add(0, state.getPosition());
            cost += state.getCost();
            state = state.getFromTile();
    	}
    	
    	return new Object[]{path, cost};
    }

}
