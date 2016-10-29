package org.iaie.practica1.p0316457;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jnibwapi.Position.PosType;
import jnibwapi.*;

public class HierarchicalMap {

	// Metemos bwapi para poder acceder a los datos del mapa
	private JNIBWAPI bwapi;
	
	// Estructuras de datos para guardar informacion del mapa
	private HashMap<Integer, List<Integer>> adjReg;
	private HashMap<Integer, HashMap<Integer, ChokePoint>> conectionPoints;
	private int [][] positionRegion;
	
	public HierarchicalMap(JNIBWAPI map) {
		this.bwapi = map;
	}
	
	public void crearMapasRegiones(){
		adjacentRegions();
		conectionWAdjRegions();
		linkPositionToRegion();
	}
	
	
	/**
	 * Este metodo crea un hashmap que relaciona una region con sus colindantes
	 */
	private void adjacentRegions(){
		adjReg = new HashMap<Integer, List<Integer>>();
		List<Region> regions = bwapi.getMap().getRegions();
		for(int i = 0; i < regions.size(); i++){
			List<Integer> conectadas = new ArrayList<Integer>(); 
			Iterator<Region> itr = regions.get(i).getConnectedRegions().iterator();
			while(itr.hasNext()){
				conectadas.add(itr.next().getID());
			}
			adjReg.put(regions.get(i).getID(), conectadas);
		}
	}
	

	private void conectionWAdjRegions(){
		conectionPoints = new HashMap<Integer, HashMap<Integer, ChokePoint>>();
		for(Map.Entry<Integer, List<Integer>> elem : adjReg.entrySet()){
			List<Integer> regionesConectadas = elem.getValue();
			for(int i = 0; i < regionesConectadas.size(); i++){
				HashMap<Integer, ChokePoint> regionPlusChoke = new HashMap<Integer, ChokePoint>();
				Iterator<ChokePoint> it = bwapi.getMap().getRegion(elem.getKey()).getChokePoints().iterator();
				while(it.hasNext()){
					ChokePoint chk = it.next();
					if(regionesConectadas.get(i).getChokePoints().contains(chk)){
						regionPlusChoke.put(regionesConectadas.get(i).getID(), chk);
					}
				}
				conectionPoints.put(elem.getKey(), regionPlusChoke);
			}
		}
	}
	
	
	/** 
     * 	Esta metodo se utiliza para establecer un mapa con las posiciones que tiene cada
     *  region
     *  
     *  El eje X, que es el ancho del mapa, se guarda en la variable j de la positionRegion
     *  El eje Y, que es el alto del mapa, se guarda en la variable i de la positionRegion
     *  Es por tanto, que para su correcta impresion se debe hacer positionRegion[j][i]
     */
    private void linkPositionToRegion(){
        int ancho = bwapi.getMap().getSize().getBX();
        int alto = bwapi.getMap().getSize().getBY();
        positionRegion = new int [ancho][alto];
        // Analizamos todo el mapa, estableciendo la region de cada posicion
		for(int y = 0; y < alto; y++){
			for (int x = 0; x < ancho; x++){
				// Comprobamos si la posiciones son contruibles o no
				Position posActual = new Position(x, y, PosType.WALK);
				if (bwapi.getMap().getRegion(posActual) != null){
					positionRegion[x][y] = bwapi.getMap().getRegion(posActual).getID();
				}
				else{
					positionRegion[x][y] = -1;
				}
			}
		}
    }
    
    
    /**
     * Metodo encargado de devolver la region de una posicion
     * 
     * @param x Eje x de la posicion
     * @param y Eje Y de la posicion
     * @return ID de la region
     */
    public int regionOfPosition(int x, int y){
    	return positionRegion[x][y];
    }
    
}
