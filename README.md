Repositorio: https://github.com/adrigrillo/StarCraftAgent

# EXPLICACIONES DE STARCRAFT
Muchos de los métodos vienen con valor y coste doble al real en el juego, es debido a los zerg que hay cosas que valen la mitad, y el juego los dobla para usar enteros.

## MÉTODOS QUE HAY QUE ESCRIBIR:
*    **matchstart():** vale para iniciar cosas.
*    **matchframe():** cada 50 milisegundos y es donde va el codigo para las acciones de partida.
*    **matchend():** Este método se utiliza para hacer un report de la partida.

## CLASES BASICAS:
*    **Unit:** Edificios como unidades.
*    **UnitType:** Propio de cada raza.
*    **Player**
*    **Map**

## SENTENCIAS
*    **getMyUnits()** coge mis unidades.
*    **getNeutralUnits()** son los recursos.
*    **build(argumentos)** es el método que se encargará de construir cosas.
*    **train()** crea personajes en el juego.
*    **Map map = this.bwapi.getMap():** consigue el mapa.
*    **Unit u = this.bwapi.getUnit(ID):** selecciona un jugador.

Hay que conseguir la refineria sobre una mina de vespeno. Para buscar la localización de un edificio hay que hacer un método propio.

## EXPLICACIONES
### Position
Para build existen tres elementos claves, necesarios para que el agente no se quede pillado. Cada edificio tiene su tamaño por eso la importancia de los elementos de abajo. Para construir se le pasa a la función build, le tienes que pasar por parámetros la posición (investigando las casillas que ocupa) esta posición será la esquina izquierda del grid que ocupa:
*   Tile position
*   Bx
*   By

### Unidades

Esto hace saber si la unidad seleccionada es nuestra
Unit u = this.bwapi.getUnit(ID)
if(u.getPlayer().getID() == this.bwapi.getSelf().getID())

#### MatchEnd()

Este método se ejecuta con el final de la partida y recibe como argumento el resultado de la misma, que es 'true' si la partida ha resultado en victoria y 'false' si no ha sido así.

Se puede utilizar para escribir por consola estadísticas de la partida que no pueden comprobarse en el resumen general de la partida que ofrece el juego.

### METODOS Y FUNCIONES DEL JUEGO
Para saber el limite de soldados que se pueden crear: real = res/2

    bwapi.getSelf().getSupplyTotal()

Para saber los soldados creados: real = res/2

    bwapi.getSelf().getSupplyUsed()

Para saber el costo de la creacion de una unidad

    UnitTypes.UnidadDeseada.getSupplyRequired()
