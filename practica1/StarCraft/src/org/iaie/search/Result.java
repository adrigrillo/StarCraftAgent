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
 
package org.iaie.search;

import java.awt.Point;
import java.util.List;

public class Result {
	
    private final long time;
    private final int cost;
    private final int expandedNodes;
    private final int generatedNodes;
    private final List<Point> path;

    public Result(List<Point> path, int generated, int expanded, long time) {
        this.path = path;
        this.expandedNodes = expanded;
        this.generatedNodes = generated;
        this.time = time;
        this.cost = path.size();
    }

    public Result(List<Point> path, int generated, int expanded, int cost, long time) {
        this.path = path;
        this.expandedNodes = expanded;
        this.generatedNodes = generated;
        this.time = time;
        this.cost = cost;
    }

    public int getExpandedNodes() {
        return this.expandedNodes;
    }
    
    public int getGeneratedNodes() {
        return this.generatedNodes;
    }

    public long getTime() {
        return this.time;
    }

    public List<Point> getPath() {
        return this.path;
    }

    public int getCost() {
        return this.cost;
    }
    
    public void print() {
        System.out.println("********************************************************");
        if (this.path != null) {
            System.out.println("Resumen del proceso de búsqueda");
            System.out.println("Solución encontrada con coste " + this.cost);
            System.out.println("Estado inicial: [" + this.path.get(0).x + "," + this.path.get(0).y + "]");
            System.out.println("Estado inicial: [" + this.path.get(this.path.size()-1).x + "," + this.path.get(this.path.size()-1).y + "]");
            System.out.println("Nodos expandidos: " + this.expandedNodes);
            System.out.println("Nodos generados: " + this.generatedNodes);
            System.out.println("Tiempo de ejecución: " + this.time + " segundos");
        }
        else {
            System.out.println("Resumen del proceso de búsqueda");
            System.out.println("Solución no encontrada");
            System.out.println("Nodos expandidos: " + this.expandedNodes);
            System.out.println("Nodos generados: " + this.generatedNodes);
            System.out.println("Tiempo de ejecución: " + this.time + " segundos");
        }
        System.out.println("********************************************************");
    }
    
    public void saveResults(String fileName) {
        
    }
}
