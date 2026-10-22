
package civitas;

import java.util.ArrayList;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class CasillaSorpresa extends Casilla {
    
    // Atributos de referencia
    private MazoSorpresas mazo = null;
    private Sorpresa sorpresa = null;
    
    
    CasillaSorpresa(String nombre, MazoSorpresas mazo){
        super(nombre);
        this.mazo = mazo;
    }
    @Override
    void recibeJugador(int iactual, ArrayList<Jugador> todos){
        Sorpresa sorpresa;
        sorpresa = mazo.siguiente();
        informe(iactual, todos);
        sorpresa.aplicarAJugador(iactual, todos); 
    }
    
}
