# EXPLICACIONES DE STARCRAFT
## MÉTODOS QUE HAY QUE ESCRIBIR:
*    **matchstart():** vale para iniciar cosas.
*    **matchframe():** cada 50 milisegundos y es donde va el codigo para las acciones de partida.
*    **matchend():** Este método se utiliza para hacer un report de la partida.

## CLASES BASICAS:
*    **Unit:** Edificios como aldeanos.
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

Hay que conseguir la refineria sobre una mina de oro. Para buscar la localización de un edificio hay que hacer un método hecho por nosotros.

### Position
Para build existen tres elementos claves, necesarios para que el agente no se quede pillado. Cada edificio tiene su tamaño por eso la importancia de los elementos de abajo. Para construir se le pasa a la función build, le tienes que pasar por parámetros la posición (investigando las casillas que ocupa) esta posición será la esquina izquierda del grid que ocupa:
*   Tile position
*   Bx
*   By

### Unidades

Esto hace saber si la unidad seleccionada es nuestra
Unit u = this.bwapi.getUnit(ID)
if(u.getPlayer().getID() == this.bwapi.getSelf().getID())
