
package civitas;

import java.util.ArrayList;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public abstract class Sorpresa {
    
    private String texto;
    
    //Atributos de referencia
    private MazoSorpresas mazo;
    
    
    Sorpresa(String texto){ 
        this.texto = texto;
    }
    
    abstract void aplicarAJugador(int actual, ArrayList<Jugador> todos);
    
    void informe(int actual, ArrayList<Jugador> todos){
        Diario.getInstance().ocurreEvento("Se está aplicando la sorpresa '"
                + texto + "' al jugador: " + todos.get(actual).getNombre());
    }
    
    @Override
    public String toString(){
        return texto;
    }
    
}
