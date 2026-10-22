
package civitas;

import java.util.ArrayList;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class SorpresaPagarCobrar extends Sorpresa {
    
    private int valor;

    public SorpresaPagarCobrar(String texto, int valor) {
        super(texto);
        this.valor = valor;
    }
    
    void aplicarAJugador(int actual, ArrayList<Jugador> todos){
        informe(actual, todos);
        todos.get(actual).modificarSaldo(valor);
    }
    
}
