
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

    public String getTexto(){
        return texto;
    }

    abstract void aplicarAJugador(int actual, ArrayList<Jugador> todos);

    void informe(int actual, ArrayList<Jugador> todos){
        Diario.getInstance().ocurreEvento("Sorpresa para " + todos.get(actual).getNombre()
                + ": " + texto);
    }

    @Override
    public String toString(){
        return texto;
    }

}
