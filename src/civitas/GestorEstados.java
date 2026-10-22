
package civitas;

/**
 * Máquina de estados del turno. La vista consulta qué operaciones están
 * permitidas en cada momento para activar o desactivar sus botones.
 */
class GestorEstados {
  EstadoJuego estadoInicial() {
    return (EstadoJuego.INICIO_TURNO);
  }

  boolean permite(Jugador jugador, EstadoJuego estado, OperacionJuego operacion){
    boolean permitida = false;

    switch (operacion) {
      case AVANZAR :
        permitida = (estado == EstadoJuego.INICIO_TURNO);
        break;

      case COMPRAR :
        permitida = (estado == EstadoJuego.DESPUES_AVANZAR && jugador.getPuedeComprar());
        break;

      case GESTIONAR :
        permitida = (estado != EstadoJuego.INICIO_TURNO && jugador.tieneAlgoQueGestionar());
        break;

      case PASAR_TURNO :
        permitida = (estado != EstadoJuego.INICIO_TURNO);
        break;
    }
    return permitida;
  }

  EstadoJuego siguienteEstado (EstadoJuego estado, OperacionJuego operacion) {
    EstadoJuego siguiente = estado;

    switch (operacion) {
      case AVANZAR :
        siguiente = EstadoJuego.DESPUES_AVANZAR;
        break;

      case COMPRAR :
        siguiente = EstadoJuego.DESPUES_COMPRAR;
        break;

      case GESTIONAR :
        siguiente = EstadoJuego.DESPUES_GESTIONAR;
        break;

      case PASAR_TURNO :
        siguiente = EstadoJuego.INICIO_TURNO;
        break;
    }

    return siguiente;
  }

}
