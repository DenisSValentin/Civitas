
package civitas;

import java.util.ArrayList;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class SorpresaConvertirme extends Sorpresa {
    
    SorpresaConvertirme(String texto){
        super(texto);
    }
    
    @Override
    public void aplicarAJugador(int actual, ArrayList<Jugador> todos){
        informe(actual, todos);
        JugadorEspeculador aux = todos.get(actual).convertir();
        todos.remove(actual);
        todos.add(actual, aux);
    }
    
}
