
package civitas;

import java.util.ArrayList;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class Casilla {

    protected String nombre;


    Casilla(String nombre){
        this.nombre = nombre;
    }

    public String getNombre(){
        return nombre;
    }

    void informe(int actual, ArrayList<Jugador> todos){
        Diario.getInstance().ocurreEvento(todos.get(actual).getNombre() + " cae en " + nombre);
    }

    void recibeJugador(int iactual, ArrayList <Jugador> todos){
            informe(iactual, todos);
    }

    public String toString(){
          return "'" + nombre + "'";
    }

}
