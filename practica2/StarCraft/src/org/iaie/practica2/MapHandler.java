package org.iaie.practica2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import jnibwapi.JNIBWAPI;
import jnibwapi.Position;
import jnibwapi.Unit;
import jnibwapi.Position.PosType;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

public class MapHandler {
	/** 
     * 	Esta metodo se utiliza para comprobar el mapa y la posibilidad de construccion de 
     *  los edificios segun el espacio que ocupen.
     *  
     *  El eje X, que es el ancho del mapa, se guarda en la variable j de la matriz
     *  El eje Y, que es el alto del mapa, se guarda en la variable i de la matriz
     *  Es por tanto, que para su correcta impresion se debe hacer matriz[j][i]
     *  
     *  @param bwapi	 BWAPI para obtener los datos de la partida 
     *  @return Genera un .txt con la cuadricula del mapa y los espacios para la construccion
     *  		de los edificios
     */
    public static void generateMapSpaces(JNIBWAPI bwapi){
        int ancho = bwapi.getMap().getSize().getBX();
        int alto = bwapi.getMap().getSize().getBY();
        char[][] matriz = new char [ancho][alto];
        /* Primero pasaremos a analizar todo el mapa, estableciendo lo siguiente:
         * 	-	Si es 0, no se puede construir en la casilla
         * 	-	Si es 1, la casilla es construible 
         */
		for(int y = 0; y < alto; y++){
			for (int x = 0; x < ancho; x++){
				// Comprobamos si la posiciones son contruibles o no
				Position posActual = new Position(x, y, PosType.BUILD);
				if (bwapi.isBuildable(posActual, true)){
					/* Si es construible, minimo cabe un edificio en esa posicion */
					matriz[x][y] = '1';
				}
				else{
					matriz[x][y] = '0';
				}
			}
		}
		/* Pasamos a la comprobacion en el mapa de los minerales y el vespeno
		 * 	-	Si son minerales, se pone 'M'
		 * 	-	Si es vespeno, se pone 'V'
		 */
		for (Unit u : bwapi.getNeutralUnits()){
			// Reiniciamos valores por si acaso
			int topIzqX = 0, topIzqY = 0, botDerX = 0, botDerY = 0;
			// Minerales
			if (u.getType() == UnitTypes.Resource_Mineral_Field || u.getType() == UnitTypes.Resource_Mineral_Field_Type_2 || u.getType() == UnitTypes.Resource_Mineral_Field_Type_3 ){
				topIzqX = u.getTopLeft().getBX();
				topIzqY = u.getTopLeft().getBY();
				botDerX = u.getBottomRight().getBX();
				botDerY = u.getBottomRight().getBY();
				for (int x = topIzqX; x <= botDerX; x++){
					for (int y = topIzqY; y <= botDerY; y++){
						matriz[x][y] = 'M';
					}
				}
			}
			// Vespeno
			else if (u.getType() == UnitTypes.Resource_Vespene_Geyser){
				topIzqX = u.getTopLeft().getBX();
				topIzqY = u.getTopLeft().getBY();
				botDerX = u.getBottomRight().getBX();
				botDerY = u.getBottomRight().getBY();
				for (int x = topIzqX; x <= botDerX; x++){
					for (int y = topIzqY; y <= botDerY; y++){
						matriz[x][y] = 'V';
					}
				}
			}
		}
		/* Pasamos a comprobar el tamano de edificio que se puede construir.
		 * Debido a que ya hemos comprobado las casillas que son construibles
		 * Ahora pasamos a comprobar cuantas de esas casillas hay juntas
         * 	-	4: Si existen 3 o mas casillas a la derecha y hacia abajo.
         * 	-	3: Si existen 2 o mas casillas a la derecha y hacia abajo.
         * 	-	2: Si existen 1 o mas casillas a la derecha y hacia abajo.  
		 */
		for(int y = 0; y < alto; y++){
			for (int x = 0; x < ancho; x++){
				if (matriz[x][y] == '1'){
					// Examinamos el terreno desde 2 a 4 espacios
					for (int espacio = 2; espacio <= 4; espacio++){
						// Creamos una variable para indicar si la comprobacion ha sido correcta y hay espacio
						boolean valido = true;
						// Navegamos por las casillas adyacentes 
						for (int i = x; i < x + espacio; i++){
							for (int j = y; j < y + espacio; j++){
								if (i < ancho && j < alto){
									/* Si se encuentra una mina, geiser o edificio pasa a ser invalido
									 * dependiendo de la distacia de busqueda
									 */
									if (matriz[i][j] == '0' || matriz[i][j] == 'M' || matriz[i][j] == 'V' ){
										valido = false;
									}
								}
								// Si se sale del tablero falla tambien
								else{
									valido = false;
								}
							}
						}
						// Si hay espacio para dos se cambia la casilla
						if (espacio == 2 && valido == true){
							matriz[x][y] = '2';
						}
						// Si hay espacio para dos se cambia la casilla
						else if (espacio == 3 && valido == true){
							matriz[x][y] = '3';
						}
						// Si hay espacio para dos se cambia la casilla
						else if (espacio == 4 && valido == true){
							matriz[x][y] = '4';
						}
					}
				}
			}
		}
		writeMapFile(matriz);
    }
    
    /** Metodo para guardar la matriz generada por el estudio del mapa 
     * 	en un fichero de texto
     *  
     * @param matriz	Matriz con el contenido del mapa
     */
    public static void writeMapFile(char matriz[][]){
    	/* Pasamos a guardar la matriz del mapa en un archivo */
		try {
			File archivo = new File("map.txt");
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
    
    /** Metodo para leer los archivos con los estudios del mapa y guardarlos en una matriz
     * 
     * @param bwapi	 BWAPI para obtener los datos de la partida
     * @return 		 Matriz del mapa para poder ser usada por los metodos de modificacion
     */
    public static char[][] readMapFile(JNIBWAPI bwapi){
    	try{
    		// Abrimos el archivo
    		FileReader archivo = new FileReader("map.txt");
    		BufferedReader lector = new BufferedReader(archivo);
    		//Establecemos el tamano del array para comprobar que el fichero es del mismo mapa
    		int ancho = bwapi.getMap().getSize().getBX();
            int alto = bwapi.getMap().getSize().getBY();
            char[][] matriz = new char [ancho][alto];
            // Tomamos las lineas del fichero, pasandolas a un array de chars y copiamos en la matriz
            String linea;
            int fila = 0;
            while((linea = lector.readLine()) != null){
            	char [] a = linea.toCharArray();
            	for (int i = 0; i < a.length; i++){
            		matriz[i][fila] = a[i];
            	}
            	fila++;
            }
            lector.close();
            return matriz;    		
    	}
    	catch (Exception e) {
    		System.out.println("Error: " + e.getMessage());
    		return null;
		}
    }
    
    public static Position searchPointToBuild(JNIBWAPI bwapi, Position posicion, UnitType edificio){
    	// Obtenemos el mapa para examinar
    	char [][] map = readMapFile(bwapi);
    	// Obtenemos el espacio necesario para construir el edificio
    	char needSpace = Character.forDigit(edificio.getTileWidth(), 10);
    	// Parametros para el espacio de busqueda
    	int searchSpace = 15;
    	if (edificio == UnitTypes.Terran_Supply_Depot){
    		searchSpace = 10;
    	}
    	Position valid = null;
    	int i = 0;
    	while (valid == null && i < 50){
    		int x = (int) Math.floor(Math.random() * (2*searchSpace)) - searchSpace;
    		int y = (int) Math.floor(Math.random() * (2*searchSpace)) - searchSpace;
    		x = posicion.getBX() - x;
    		y = posicion.getBY() - y;
    		if ((x >= 0 && x < map.length) && (y >= 0 && y < map[0].length)){
				if (!(map[x][y] == 'M' || map[x][y] == 'V' || map[x][y] == '0')){
					if (Character.compare(map[x][y], needSpace) == 0 || Character.compare(map[x][y], needSpace) > 0){
	    				valid = new Position(x, y, PosType.BUILD);
	    			}
				}	
			}
    		i++;
    	}
		return valid;
    }
    
    /**
     * Metodo que actualiza la matriz del mapa tras recibir la posicion que ocupa
     * el nuevo edificio a construir y la guarda en el archivo.
     * 
     * Ademas tambien actualiza los alrededores de la nueva construccion para actualizar el mapa
     * 
     * @param bwapi	 BWAPI para obtener los datos de la partida
     * @param topIzq Position de la esquina superior izquierda del edificio
     * @param botDer Position de la esquina inferior derecha del edificio
     */
    public static void updateMap(JNIBWAPI bwapi, Position topIzq, Position botDer){
    	// Leemos el mapa
    	char [][] map = readMapFile(bwapi);
    	// Recorremos la matriz del edificio para establecer los nuevos '0'
    	for (int x = topIzq.getBX(); x <= botDer.getBX(); x++){
    		for (int y = topIzq.getBY(); y <= botDer.getBY(); y++){
    			map [x][y] = '0';
    		}
    	}
    	/* Ahora pasamos a comprobar los alrededores del edificio nuevo para
    	 * comprobar los nuevos espacios */
		for(int y = topIzq.getBY() - 4; y <= botDer.getBY(); y++){
			for (int x = topIzq.getBX() - 4; x <= botDer.getBX(); x++){
				if ((x >= 0 && x < map.length) && (y >= 0 && y < map[0].length)){
					if (map[x][y] == '1' || map[x][y] == '2' || map[x][y] == '3' || map[x][y] == '4'){
						// Examinamos el terreno desde 2 a 4 espacios
						for (int espacio = 1; espacio <= 4; espacio++){
							// Creamos una variable para indicar si la comprobacion ha sido correcta y hay espacio
							boolean valido = true;
							// Navegamos por las casillas adyacentes 
							for (int i = x; i < x + espacio; i++){
								for (int j = y; j < y + espacio; j++){
									if (i < map.length && j < map[0].length){
										/* Si se encuentra una mina, geiser o edificio pasa a ser invalido
										 * dependiendo de la distacia de busqueda
										 */
										if (map[i][j] == '0' || map[i][j] == 'M' || map[i][j] == 'V' ){
											valido = false;
										}
									}
									else{
										valido = false;
									}
								}
							}
							// Si hay espacio para uno se cambia la casilla
							if (espacio == 1 && valido == true){
								map[x][y] = '1';
							}
							// Si hay espacio para dos se cambia la casilla
							if (espacio == 2 && valido == true){
								map[x][y] = '2';
							}
							// Si hay espacio para dos se cambia la casilla
							else if (espacio == 3 && valido == true){
								map[x][y] = '3';
							}
							// Si hay espacio para dos se cambia la casilla
							else if (espacio == 4 && valido == true){
								map[x][y] = '4';
							}
						}
					}
				}
			}
		}
    	writeMapFile(map);
    }
}
