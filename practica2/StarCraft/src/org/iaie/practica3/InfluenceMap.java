package org.iaie.practica3;

import java.awt.Point;
import java.util.ArrayList;

import jnibwapi.Position;

public class InfluenceMap {
	private int[][] map;
	private int influencia;
	private int areasInfluencia;
	private int influenciaEnemigo;
	private int areasInfluenciaEnemigo;
	
	private final int EDIFICIO_NEUTRO = 3;
	private final int EDIFICIO_OFENSIVO = 4;
	private final int EDIFICIO_DEFENSIVO = 5;
	private final int UNIDAD_INFANTERIA = 1;
	private final int UNIDAD_MECANINCA = 3;
	private final int UNIDAD_AREA = 6;
	private final int UMBRAL_SEGURIDAD = 3;
	private final int DISTANCIA_PROPAGACION = 2;
	
	/**
	 * Generador del map con la this.map llena de ceros
	 * @param width ancho del map
	 * @param height height del map
	 */
	public InfluenceMap(int width, int height) {
		super();
		this.map = new int[width][height];
		for(int y = 0; y < height; y++){
			for (int x = 0; x < width; x++){
				this.map[x][y] = 0;
			}
		}
		this.influencia = 0;
		this.areasInfluencia = 0;
		this.influenciaEnemigo = 0;
		this.areasInfluenciaEnemigo = 0;
	}
	
	public boolean updateMap(){
		return false;
	}
	
	/**
	 * Este metodo actualizara la influencia de una celda del mapa y llamara para actualizar
	 * su influencia en el resto del mapa
	 * @param cell 		Punto del mapa a actualizar (x, y)
	 * @param influence Valor de la influencia en esa casilla
	 * @return 	True si su actualizacion y las de su influencia se realiza correctamente, False
	 * 			si ha habido algun error
	 */
	public boolean updateCellInfluence(Point cell, int influence){
		try {
			int x = (int)cell.getX();
			int y = (int)cell.getY();
			// Actualizamos la influencia en la celda de la unidad
			this.map[x][y] += influence;
			// Recorremos las celdas que influenciara y el valor que habra que anyadir a esas celdas
			ArrayList<Point> celdasInfluenciadas = new ArrayList<Point>();
			ArrayList<Integer> influenciaEnCelda = new ArrayList<Integer>();
			for (int i = x - DISTANCIA_PROPAGACION; i < x + DISTANCIA_PROPAGACION; i++){
				for (int j = y - DISTANCIA_PROPAGACION; j < y + DISTANCIA_PROPAGACION; j++){
					// Comprobamos que las celdas estan en el mapa y que no son la celda original
					if (i != x && i >= 0 && i < map.length && j != y && y >= 0 && y < map[0].length){
						// Sacamos la posicion influenciada y la anyadimos a la lista de celdas influenciadas
						Point posicionInfluenciada = new Point(i, j);
						celdasInfluenciadas.add(posicionInfluenciada);
						// Sacamos la influencia que ejerce la celda original a la celda influencia
						influenciaEnCelda.add((int)(influence/Math.pow(1 + cell.distance(posicionInfluenciada), 2)));
					}
				}
			}
			// Actualizamos la lista de celdas influenciadas con su valor
			return updateCellsInfluence(celdasInfluenciadas, influenciaEnCelda);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	
	/**
	 * Dada la lista de celdas influenciadas y su valor actualizamos el mapa de influencia, las celdas y su
	 * valor corresponden en la posicion de los arrays
	 * @param celdasInfluenciadas 	Lista de listas influenciadas por una celda
	 * @param influenciaEnCelda		Valor a anyadir en el mapa de influencia 
	 * @return True si se ha realizado correctamente, False si no ha sido correcto
	 */
	public boolean updateCellsInfluence(ArrayList<Point> celdasInfluenciadas, ArrayList<Integer> influenciaEnCelda){
		try {
			// Recorremos la lista de celdas influenciadas y actualizamos su valor en el mapa
			for (int i = 0; i < celdasInfluenciadas.size(); i++){
				int x = (int)celdasInfluenciadas.get(i).getX();
				int y = (int)celdasInfluenciadas.get(i).getY();
				this.map[x][y] += influenciaEnCelda.get(i);
			}
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	
	/**
	 * Metodo para obtener la influencia en una celda
	 * @param celda	Point(x, y) de la celda
	 * @return		Valor de influencia de la celda
	 */
	public int getInfluence(Point celda){
		return this.map[(int)celda.getX()][(int)celda.getY()];
	}
	
	/**
	 * Este metodo sirve para obtener el nivel de influencia del jugador en el mapa
	 * Como el nivel de seguridad de una celda esta definido, tomamos como celda
	 * positiva para el jugador cualquiera que este por encima de ese nivel
	 * @return Valor de la influencia del jugador
	 */
	public int getMyInfluenceLevel(){
		int influencia = 0;
		for(int y = 0; y < this.map[0].length; y++){
			for (int x = 0; x < this.map.length; x++){
				if (this.map[x][y] > UMBRAL_SEGURIDAD){
					influencia += this.map[x][y];
				}
			}
		}
		return influencia;
	}
	
	
	/**
	 * Este metodo sirve para obtener el nivel de influencia del enemigo en el mapa
	 * Como el nivel de seguridad de una celda esta definido, tomamos como celda
	 * positiva para el enemigo cualquiera que este por encima de ese nivel en negativo
	 * @return Valor de la influencia del enemigo
	 */
	public int getEnemyInfluenceLevel(){
		int influencia = 0;
		for(int y = 0; y < this.map[0].length; y++){
			for (int x = 0; x < this.map.length; x++){
				if (this.map[x][y] < -UMBRAL_SEGURIDAD){
					influencia += this.map[x][y];
				}
			}
		}
		return influencia;
	}
	
	
	/**
	 * Este metodo sirve para obtener el numero de casillas que controla el jugador en el mapa
	 * Como el nivel de seguridad de una celda esta definido, tomamos como celda
	 * positiva para el jugador cualquiera que este por encima de ese nivel
	 * @return Numero de casillas que controla el jugador
	 */
	public int getMyInfluenceArea(){
		int areas = 0;
		for(int y = 0; y < this.map[0].length; y++){
			for (int x = 0; x < this.map.length; x++){
				if (this.map[x][y] > UMBRAL_SEGURIDAD){
					areas++;
				}
			}
		}
		return areas;
	}
	
	
	/**
	 * Este metodo sirve para obtener el numero de casillas que controla el jugador en el mapa
	 * Como el nivel de seguridad de una celda esta definido, tomamos como celda
	 * positiva para el enemigo cualquiera que este por encima de ese nivel en negativo
	 * @return Numero de casillas que controla el jugador
	 */
	public int getEnemyInfluenceArea(){
		int areas = 0;
		for(int y = 0; y < this.map[0].length; y++){
			for (int x = 0; x < this.map.length; x++){
				if (this.map[x][y] < -UMBRAL_SEGURIDAD){
					areas++;
				}
			}
		}
		return areas;
	}
	
	
	/**
	 * @return the influencia
	 */
	public int getInfluencia() {
		return influencia;
	}

	/**
	 * @return the areasInfluencia
	 */
	public int getAreasInfluencia() {
		return areasInfluencia;
	}

	/**
	 * @return the influenciaEnemigo
	 */
	public int getInfluenciaEnemigo() {
		return influenciaEnemigo;
	}

	/**
	 * @return the areasInfluenciaEnemigo
	 */
	public int getAreasInfluenciaEnemigo() {
		return areasInfluenciaEnemigo;
	}

	/**
	 * Para imprimir la matriz de influencia.
	 */
	public void print(){
		for(int y = 0; y < this.map[0].length; y++){
			for (int x = 0; x < this.map.length; x++){
				System.out.print(this.map[x][y] + " ");
			}
			System.out.println();
		}
	}
}
