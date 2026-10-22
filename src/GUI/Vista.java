
package GUI;

import civitas.CivitasJuego;
import civitas.Jugador;
import java.util.List;

/**
 * Lo que el controlador necesita de la vista.
 */
public interface Vista {

    public void setCivitasJuego(CivitasJuego juego);

    /** Vuelve a leer el modelo y refresca toda la interfaz. */
    public void actualiza();

    /** Refresca solo qué botones están activos (por ejemplo, al empezar una animación). */
    public void actualizaBotones();

    /** Anima el dado y el movimiento de la ficha; llama a alTerminar al acabar. */
    public void animarTirada(int jugador, int desde, int tirada, Runnable alTerminar);

    public void mostrarSorpresa(String texto);

    public void mostrarEventos(List<String> eventos);

    public void mostrarFinDelJuego(List<Jugador> ranking);
}
