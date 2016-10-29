package org.iaie.practica1.p0316457;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position.PosType;
import jnibwapi.*;

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
		generateMapSpaces();
	}
	
	/** 
     * 	Esta m�todo se utiliza para comprobar el mapa y la posibilidad de construcci�n de 
     *  los edificios seg�n el espacio que ocupen.
     *  
     *  El eje X, que es el ancho del mapa, se guarda en la variable j de la matriz
     *  El eje Y, que es el alto del mapa, se guarda en la variable i de la matriz
     *  Es por tanto, que para su correcta impresion se debe hacer matriz[j][i]
     *  
     *  @return Genera un .txt con la cuadricula del mapa y los espacios para la construcci�n
     *  		de los edificios
     */
    public void generateMapSpaces(){
        int ancho = bwapi.getMap().getSize().getBX();
        int alto = bwapi.getMap().getSize().getBY();
        int[][] matriz = new int [ancho][alto];
        /* Primero pasaremos a analizar todo el mapa, estableciendo lo siguiente:
         * 	-	Si es 0, no se puede construir en la casilla
         * 	-	Si es 1, la casilla es construible 
         */
        try {
			for(int y = 0; y < alto; y++){
				for (int x = 0; x < ancho; x++){
					// Comprobamos si la posiciones son contruibles o no
					Position posActual = new Position(x, y, PosType.BUILD);
					if (bwapi.getMap().getRegion(posActual) != null){
						matriz[x][y] = bwapi.getMap().getRegion(posActual).getID();
					}
					else{
						matriz[x][y] = -1;
					}
					
				}
			}
		/* Pasamos a guardar la matriz del mapa en un archivo */
		
			File archivo = new File("buildingMap-0316457-0303518.txt");
			PrintWriter writer = new PrintWriter(archivo);
			for(int y = 0; y < matriz[0].length; y++){
				for (int x = 0; x < matriz.length; x++){
					writer.print(matriz[x][y]);
				}
				writer.println();
			}
			
			writer.close();
		}
		catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
    }	
}
