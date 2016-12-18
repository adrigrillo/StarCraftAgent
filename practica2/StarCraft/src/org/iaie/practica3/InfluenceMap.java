package org.iaie.practica3;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import jnibwapi.JNIBWAPI;
import jnibwapi.Unit;
import jnibwapi.types.UnitType;
import jnibwapi.types.UnitType.UnitTypes;

public class InfluenceMap {
	private int[][] map;
	private int influencia;
	private int areasInfluencia;
	private int influenciaEnemigo;
	private int areasInfluenciaEnemigo;
	// El nivel de control sera positivo si tenemos mas areas que el enemigo y negativo si es viceversa
	private int nivelControl;
	private HashMap<Integer, Point> unidadesConsideradas; // Key: id de la unidad y Point posicion ultima
	
	private final int EDIFICIO_NEUTRO = 3;
	private final int EDIFICIO_OFENSIVO = 4;
	private final int EDIFICIO_DEFENSIVO = 5;
	private final int UNIDAD_INFANTERIA = 1;
	private final int UNIDAD_MECANICA = 3;
	private final int UNIDAD_AREA = 6;
	private final int UMBRAL_SEGURIDAD = 3;
	private final int DISTANCIA_PROPAGACION = 2;
	// He tomado como edificios defensivos aquellos que son capaces de atacar a unidades
	private final ArrayList<UnitType> EDIFICIOS_DEFENSIVOS = new ArrayList<UnitType>(Arrays.asList(UnitTypes.Terran_Missile_Turret,
			UnitTypes.Protoss_Photon_Cannon, UnitTypes.Zerg_Spore_Colony, UnitTypes.Terran_Bunker));
	/* Consideramos como unidades neutrales aquellas que no construyen unidades ofensivas como los centro de mando y los supply
	 * depots de cada clase, no hay edificios zerg porque todos construyen unidades ofensivas */
	private final ArrayList<UnitType> EDIFICIOS_NEUTRALES = new ArrayList<UnitType>(Arrays.asList(UnitTypes.Terran_Command_Center,
			UnitTypes.Terran_Supply_Depot, UnitTypes.Protoss_Nexus, UnitTypes.Protoss_Pylon));
	
	
	/**
	 * Generador del map con la this.map llena de ceros
	 * @param width ancho del mapa
	 * @param height height del mapa
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
		unidadesConsideradas = new HashMap<Integer, Point>();
	}
	
	
	public boolean updateMap(JNIBWAPI bwapi, int idUnidad, boolean destroy){
		try {
			Unit unidad = bwapi.getUnit(idUnidad);
			UnitType tipoUnidad = bwapi.getUnit(idUnidad).getType();
			// Si es neutral como mineral, vespeno, un trabajador o una refineria no la consideramos
			if (!tipoUnidad.isResourceContainer() && !tipoUnidad.isWorker()){
				/* 1:
				 * Calculamos el valor de la unidad dependiendo de su tipo */
				int influenciaUnidad = 0;
				if (tipoUnidad.isBuilding()){
					/* Primero calculamos su valor de influencia viendo el tipo de edificio
					 * Despues calcularemos su influencia dado su tamanyo */
					// Comprobamos si es defensivo viendo si puede atacar
					if (EDIFICIOS_DEFENSIVOS.contains(tipoUnidad))
						influenciaUnidad = EDIFICIO_DEFENSIVO;
					// Si el edificio es neutro
					else if (EDIFICIOS_NEUTRALES.contains(tipoUnidad))
						influenciaUnidad = EDIFICIO_NEUTRO;
					// Si es ofensivo
					else
						influenciaUnidad = EDIFICIO_OFENSIVO;
				}
				// Si es una unidad
				else {
					// si es una unidad aerea
					if (tipoUnidad.isFlyer())
						influenciaUnidad = UNIDAD_AREA;
					// si es mecanica
					else if (tipoUnidad.isMechanical())
						influenciaUnidad = UNIDAD_MECANICA;
					// si es infanteria
					else
						influenciaUnidad = UNIDAD_INFANTERIA;					
				}
				/* 2:
				 * comprobamos que la unidad no se encuentre
				 * en la lista de unidades consideradas ya */
				boolean considerada = false;
				for (Integer idConsiderado : unidadesConsideradas.keySet()){
					if (idUnidad == idConsiderado){
						considerada = true;
						break;
					}
				}
				/* 3:
				 * si esta considerada se borra su posicion anterior y se
				 * actualiza la posicion nueva con su influencia */
				if (considerada){
					/* 3.1:
					 * Vemos si la accion se trata de actualizar o eliminar */
					// Si es destruir
					if (destroy){
						/* Sacamos ancho y largo para recorrer las posiciones, ya que lo que tenemos es la
						 * esquina superior izquierda de la unidad */
						int ancho = unidad.getBottomRight().getBX() - unidad.getTopLeft().getBX();
						int largo = unidad.getBottomRight().getBY() - unidad.getTopLeft().getBY();
						int x = (int)unidadesConsideradas.get(idUnidad).getX();
						int y = (int)unidadesConsideradas.get(idUnidad).getY();
						for (int i = x; i <= x + ancho; i++){
							for (int j = y; j <= y + largo; j++){
								if (!updateCellInfluence(new Point(i, j), -influenciaUnidad))
									throw new Exception("Error borrar posicion antigua");
							}
						}
						// Eliminamos la unidad de la lista de consideradas
						unidadesConsideradas.remove(idUnidad);
						return updateValues();
					}
					// Si es actualizar
					else {
						// Lo primero es comprobar que las posiciones coiciden
						Point posicionUnidad = new Point(unidad.getTopLeft().getBX(), unidad.getTopLeft().getBY());
						/* Si no coinciden lo primero es eliminar la influencia del punto anterior y actualizar
						 * la lista con la nueva posicion */
						if (!unidadesConsideradas.get(idUnidad).equals(posicionUnidad)){
							/* Sacamos ancho y largo para recorrer las posiciones, ya que lo que tenemos es la
							 * esquina superior izquierda de la unidad */
							int ancho = unidad.getBottomRight().getBX() - unidad.getTopLeft().getBX();
							int largo = unidad.getBottomRight().getBY() - unidad.getTopLeft().getBY();
							int x = (int)unidadesConsideradas.get(idUnidad).getX();
							int y = (int)unidadesConsideradas.get(idUnidad).getY();
							for (int i = x; i <= x + ancho; i++){
								for (int j = y; j <= y + largo; j++){
									if (!updateCellInfluence(new Point(i, j), -influenciaUnidad))
										throw new Exception("Error borrar posicion antigua");
								}
							}
							// Actualizamos la lista con la nueva posicion
							unidadesConsideradas.put(idUnidad, posicionUnidad);
							// Se actualiza con la nueva posicion
							for (int i = unidad.getTopLeft().getBX(); i <= unidad.getBottomRight().getBX(); i++){
								for (int j = unidad.getTopLeft().getBY(); j <= unidad.getBottomRight().getBY(); j++){
									if (!updateCellInfluence(new Point(i, j), influenciaUnidad))
										throw new Exception("Error al actualizar mapa");
								}
							}
							return updateValues();
						}
						// Si la posicion antigua y la nueva coinciden no es necesario hacer dos actualizaciones
						// porque esto desequilibraria el mapa
						throw new Exception("La actualizacion no es necesaria, ya que ya se ha considerado la unidad");
					}
				}
				/* 4:
				 * Si es una unidad nueva la anyadimos a la lista de unidades
				 * consideradas y actualizamos las celdas que en las que influye */
				else {
					// Obtenemos el punto donde se situa la unidad
					Point posicionUnidad = new Point(unidad.getTopLeft().getBX(), unidad.getTopLeft().getBY());
					unidadesConsideradas.put(idUnidad, posicionUnidad);
					// Actualizamos la influencia para cada una de las celdas
					for (int i = unidad.getTopLeft().getBX(); i <= unidad.getBottomRight().getBX(); i++){
						for (int j = unidad.getTopLeft().getBY(); j <= unidad.getBottomRight().getBY(); j++){
							if (!updateCellInfluence(new Point(i, j), influenciaUnidad))
								throw new Exception("Error al actualizar mapa");
						}
					}
					return updateValues();
				}
			}
			return false;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public boolean updateValues(){
		try{
			this.influencia = getMyInfluenceLevel();
			this.areasInfluencia = getAreasInfluencia();
			this.influenciaEnemigo = getEnemyInfluenceLevel();
			this.areasInfluenciaEnemigo = getAreasInfluenciaEnemigo();
			this.nivelControl = this.areasInfluencia - this.areasInfluenciaEnemigo;
			return true;
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return false;
		}
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
			for (int i = x - DISTANCIA_PROPAGACION; i <= x + DISTANCIA_PROPAGACION; i++){
				for (int j = y - DISTANCIA_PROPAGACION; j <= y + DISTANCIA_PROPAGACION; j++){
					Point posicionInfluenciada = new Point(i, j);
					// Comprobamos que las celdas estan en el mapa y que no son la celda original
					if (i >= 0 && i < map.length && y >= 0 && y < map[0].length && !cell.equals(posicionInfluenciada)){
						// Sacamos la posicion influenciada y la anyadimos a la lista de celdas influenciadas
						celdasInfluenciadas.add(posicionInfluenciada);
						// Sacamos la influencia que ejerce la celda original a la celda influencia
						influenciaEnCelda.add((int)Math.round((influence/Math.pow(1 + cell.distance(posicionInfluenciada), 2))));
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
	 * @return the nivelControl
	 */
	public int getNivelControl() {
		return nivelControl;
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
