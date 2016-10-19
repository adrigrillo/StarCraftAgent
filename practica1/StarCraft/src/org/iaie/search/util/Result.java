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
 *     * Neither the name of the IAIE  nor the names of its contributors may 
 *       be used to endorse or promote products derived from this software 
 *       without specific prior written permission.
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

package org.iaie.search.util;

import java.awt.Point;
import java.util.List;

public class Result {
	
    private final long time;
    private final int cost;
    private final int expandedNodes;
    private final List<Point> path;

    public Result(List<Point> path, int nodes, long time) {
            this.path = path;
            this.expandedNodes = nodes;
            this.time = time;
            this.cost = path.size();
    }

    public Result(List<Point> path, int nodes, int cost, long time) {
            this.path = path;
            this.expandedNodes = nodes;
            this.time = time;
            this.cost = cost;
    }

    public int getExpandedNodes() {
            return this.expandedNodes;
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
}
