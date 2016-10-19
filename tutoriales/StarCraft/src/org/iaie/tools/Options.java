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


package org.iaie.tools;

import java.util.HashMap;
import org.iaie.exception.BadParametersException;

public class Options {
    
    private static Options instance = new Options();
    
    public static Options getInstance() {
	return instance;
    }
    
    private final HashMap<String, Integer> paramTree;
    private final HashMap<String, String> options;
    private String optionsAsString;
    
    private Options() {
        this.options = new HashMap<>();
        this.optionsAsString = "";
        
        this.paramTree = new HashMap<>();
        this.paramTree.put("-a", 2);
        this.paramTree.put("-s", 2);
        this.paramTree.put("-i", 1);
        this.paramTree.put("-u", 1);
    }
    
    public void readOptions(String[] args) throws Exception {
        
        int position = 0;
        
        if (args != null) {
            if (args.length > 0) {
                while(position < args.length) {
                    if (this.paramTree.get(args[position]) == 2) {
                        if (args[position+1].contains("-")) {
                            throw new BadParametersException();
                        }
                        else {
                            this.optionsAsString += args[position] + " " + args[position+1] + " ";
                            this.options.put(args[position].trim(), args[position+1].trim());
                        }
                        position+=2;
                    }
                    else {
                        this.optionsAsString += args[position] + " ";
                        this.options.put(args[position].trim(), "");
                        position++;
                    }
                }         
            }
            else {
                throw new Exception();
            }
        }
        else {
            throw new NullPointerException();
        }
    }
    
    public String getOption(String key) {
        return this.options.get(key);
    }
    
    public String getAgent() {
        return this.options.get("-a");
    }
    
    public String asString() {
        return this.optionsAsString;
    }
    
    public int getSpeed() {
        return Integer.parseInt(this.options.get("-s"));
    }
    
    public boolean getUserInput() {
        return Boolean.parseBoolean(this.options.get("-u"));
    }
    
    public boolean getInformation() {
        return Boolean.parseBoolean(this.options.get("-i"));
    }    
    
    public void printOptions() {
        
        System.out.println("PROGRAM");
        System.out.println();
        System.out.print("java -jar program -a agent -s speed -u -i");
        System.out.println();
    }         
}