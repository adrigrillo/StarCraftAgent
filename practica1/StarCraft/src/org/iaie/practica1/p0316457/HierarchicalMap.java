package org.iaie.practica1.p0316457;

import java.util.HashMap;
import java.util.List;

import jnibwapi.JNIBWAPI;
import jnibwapi.Region;

public class HierarchicalMap {

	// Metemos bwapi para poder acceder a los datos del mapa
	private JNIBWAPI bwapi;
	
	// Estructuras de datos para guardar informacion del mapa
	private HashMap<Region, List<Region>> regMaps;
	
	public HierarchicalMap(JNIBWAPI map) {
		this.bwapi = map;
	}
	
	public void crearMapasRegiones(){
		List<Region> regiones = bwapi.getMap().getRegions();
		regiones.size();
	}
	
	
	
}
