/* Muchos de los métodos vienen con valor y coste doble al real en el juego, es
   debido a los zerg que hay cosas que valen la mitad, y el juego los dobla para
   usar enteros */

// Para saber el limite de soldados que se pueden crear: real= res/2
bwapi.getSelf().getSupplyTotal()
// Para saber los soldados creados: real= res/2
bwapi.getSelf().getSupplyUsed()
// Para saber el costo de la creacion de una unidad
UnitTypes.UnidadDeseada.getSupplyRequired()
