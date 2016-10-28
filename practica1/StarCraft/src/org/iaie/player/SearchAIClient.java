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

package org.iaie.player;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import jnibwapi.BWAPIEventListener;
import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import org.iaie.Agent;
import org.iaie.practica1.p0316457.HierarchicalMap;
import org.iaie.search.Result;
import org.iaie.search.algorithm.Astar;
import org.iaie.search.algorithm.HierarchicalSearch;
import org.iaie.tools.Options;

/**
 * Jugador Automático para la realización de la práctica 1 de IAIE.
 */
public class SearchAIClient extends Agent implements BWAPIEventListener {
		
    private Astar algorithmLevel1;
    private HierarchicalSearch algorithmLevel2;
    
    private ArrayList<Point> goals;
    private int goal;
    private Point initial;
    
    private int center;
    
    public SearchAIClient() {            
        super();
        this.bwapi = new JNIBWAPI(this, true); 
        this.bwapi.start();
    }

    @Override
    public void connected() {
        System.out.println("IAIE: Conectando con BWAPI");
    }

    @Override
    public void matchStart() {

        String line;
        String[] coordinates;
        
        Position location = this.bwapi.getPlayer(this.bwapi.getSelf().getID()).getStartLocation();
        
        HierarchicalMap a = new HierarchicalMap(bwapi);
        a.crearMapasRegiones();
        
        this.initial = new Point(location.getWX(), location.getWY());
        this.goals = goals = new ArrayList<>();
        this.goal = 0;
        
        System.out.println("IAIE: Iniciando juego");

        if (Options.getInstance().getUserInput()) this.bwapi.enableUserInput();
        if (Options.getInstance().getInformation()) this.bwapi.enablePerfectInformation();
        this.bwapi.setGameSpeed(Options.getInstance().getSpeed());
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File (Options.getInstance().getFile())));
                
            while ((line = reader.readLine()) != null) {                
                coordinates = line.split(" ");                
                goals.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
            }
            
            switch (Options.getInstance().getSearchType()) {
                case 1: this.algorithmLevel1 = (Astar) (Class.forName("org.iaie.practica1." + Options.getInstance().getPacketName() + ".SearchP11").getConstructor(JNIBWAPI.class)).newInstance(this.bwapi);
                        this.algorithmLevel1.setDebug(Options.getInstance().isDebug());
                        break;
                case 2: this.algorithmLevel1 = (Astar) (Class.forName("org.iaie.practica1." + Options.getInstance().getPacketName() + ".SearchP12").getConstructor(JNIBWAPI.class)).newInstance(this.bwapi);
                        this.algorithmLevel1.setDebug(Options.getInstance().isDebug());
                        break;                
                case 3: this.algorithmLevel2 = (HierarchicalSearch) (Class.forName("org.iaie.practica1." + Options.getInstance().getPacketName() + ".HSearch").getConstructor(JNIBWAPI.class)).newInstance(this.bwapi);
                        this.algorithmLevel2.setDebug(Options.getInstance().isDebug());
                        break;
                default:this.algorithmLevel1 = (Astar) Class.forName("org.iaie.practica1." + Options.getInstance().getPacketName() + ".SearchP11").newInstance();
            }
        }
        catch(IOException | NumberFormatException ex){
            System.out.println("Error: Fichero " + Options.getInstance().getFile() + " no encontrado");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error: Clase no encontrada");
            System.out.println(ex.toString());
        } catch (InstantiationException ex) {
            System.out.println("Error: Instanciación");
        } catch (IllegalAccessException ex) {
            System.out.println("Error: Acceso incorrecto a la clase");
        } catch (NoSuchMethodException ex) {
            System.out.println("Error: Método de creación desconocido");
        } catch (SecurityException ex) {
            System.out.println("Error: Excepción de seguridad. Ahora si que tenemos un problema");
        } catch (IllegalArgumentException ex) {
            System.out.println("Error: Número de argumentos incorrectos en la instanciación de la clase");
        } catch (InvocationTargetException ex) {
            System.out.println("Error: Error de invocación");
        }
    }

    @Override
    public void matchFrame() {
    
        Result searchResult = null;
        
        if (this.goal < this.goals.size()) {
            
            if (Options.getInstance().getSearchType() < 3) {
                searchResult = this.algorithmLevel1.search(this.initial, this.goals.get(goal));
            }
            else {
                searchResult = this.algorithmLevel2.search(this.initial, this.goals.get(goal));
            }
            
            searchResult.print();
            
            this.goal++;
        }
    }
    @Override
    public void keyPressed(int keyCode) {}
    @Override
    public void matchEnd(boolean winner) {}	
    @Override
    public void sendText(String text) {}
    @Override
    public void receiveText(String text) {}
    @Override
    public void nukeDetect(Position p) {}
    @Override
    public void nukeDetect() {}
    @Override
    public void playerLeft(int playerID) {}
    @Override
    public void unitCreate(int unitID) {}
    @Override
    public void unitDestroy(int unitID) {}
    @Override
    public void unitDiscover(int unitID) {}
    @Override
    public void unitEvade(int unitID) {}
    @Override
    public void unitHide(int unitID) {}
    @Override
    public void unitMorph(int unitID) {}
    @Override
    public void unitShow(int unitID) {}
    @Override
    public void unitRenegade(int unitID) {}
    @Override
    public void saveGame(String gameName) {}
    @Override
    public void unitComplete(int unitID) {}
    @Override
    public void playerDropped(int playerID) {}
}
