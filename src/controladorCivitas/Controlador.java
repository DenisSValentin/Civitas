
package controladorCivitas;

/**
 * Controlador por eventos: la vista llama a estos métodos cuando el usuario
 * pulsa un botón, y el controlador ejecuta la operación en el modelo y le dice
 * a la vista que se actualice.
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
import civitas.CivitasJuego;
import civitas.Diario;
import civitas.Sorpresa;
import GUI.Vista;
import java.util.ArrayList;

public class Controlador {

    private CivitasJuego juego;
    private final Vista vista;
    private boolean animando = false;


    public Controlador(CivitasJuego juego, Vista vista){
        this.juego = juego;
        this.vista = vista;
    }

    public CivitasJuego getJuego(){
        return juego;
    }

    public boolean isAnimando(){
        return animando;
    }

    public void iniciar(){
        vista.setCivitasJuego(juego);
        despuesDeOperacion();
    }

    public void nuevaPartida(ArrayList<String> nombres, boolean debug){
        descartarEventos();
        juego = new CivitasJuego(nombres, debug);
        animando = false;
        iniciar();
    }

    public void tirarDado(){
        if(animando || !juego.puedeAvanzar())
            return;

        int indice = juego.getIndiceJugadorActual();
        int desde = juego.getJugadorActual().getCasillaActual();
        int tirada = juego.avanzar();
        Sorpresa sorpresa = juego.getUltimaSorpresa();

        // El modelo ya ha movido al jugador; la vista enseña la animación
        // y solo entonces se muestran los eventos y el nuevo estado.
        animando = true;
        vista.actualizaBotones();
        vista.animarTirada(indice, desde, tirada, () -> {
            animando = false;
            if(sorpresa != null)
                vista.mostrarSorpresa(sorpresa.getTexto());
            despuesDeOperacion();
        });
    }

    public void comprar(){
        if(!animando && juego.comprar())
            despuesDeOperacion();
    }

    public void construirCasa(int ip){
        if(!animando && juego.construirCasa(ip))
            despuesDeOperacion();
    }

    public void construirHotel(int ip){
        if(!animando && juego.construirHotel(ip))
            despuesDeOperacion();
    }

    public void hacerCalleSocial(int ip){
        if(!animando && juego.hacerCalleSocial(ip))
            despuesDeOperacion();
    }

    public void pasarTurno(){
        if(!animando && juego.pasarTurno())
            despuesDeOperacion();
    }

    private void despuesDeOperacion(){
        ArrayList<String> eventos = new ArrayList<>();
        while(Diario.getInstance().eventosPendientes())
            eventos.add(Diario.getInstance().leerEvento());

        if(!eventos.isEmpty())
            vista.mostrarEventos(eventos);

        vista.actualiza();

        if(juego.finalDelJuego())
            vista.mostrarFinDelJuego(juego.ranking());
    }

    private void descartarEventos(){
        while(Diario.getInstance().eventosPendientes())
            Diario.getInstance().leerEvento();
    }

}
