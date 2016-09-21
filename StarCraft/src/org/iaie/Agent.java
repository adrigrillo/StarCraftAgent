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
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Pelea.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ************************************************************************/

package org.iaie;

import jnibwapi.JNIBWAPI;
import org.iaie.tools.Options;

public abstract class Agent {
    
    protected JNIBWAPI bwapi;
    protected int speed;
    protected boolean userInput;
    protected boolean perfectInfo;
    
    public Agent() {
        this.speed = Options.getInstance().getSpeed();
        this.userInput = Options.getInstance().getUserInput();
        this.perfectInfo = Options.getInstance().getInformation();
    }
    
    public int getSpeed() {
        return this.speed;
    }
    
    public boolean enableUserInput() {
        return this.userInput;
    }
    
    public boolean enablePerfectInformation() {
        return this.perfectInfo;
    }
}
