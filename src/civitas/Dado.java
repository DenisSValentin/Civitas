
package civitas;

import java.util.Random;

/**
 * Dado del juego. Solo contiene la lógica: la animación es cosa de la vista.
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class Dado {

    static final private Dado instance = new Dado();

    private final Random random = new Random();
    private int ultimoResultado = -1;
    private boolean debug = false;


    private Dado(){
    }

    public static Dado getInstance(){
        return instance;
    }

    void setDebug(boolean debug){
        this.debug = debug;
    }

    public boolean isDebug(){
        return debug;
    }

    int tirar(){
        if(debug)
            ultimoResultado = 1;
        else
            ultimoResultado = random.nextInt(6) + 1;

        return ultimoResultado;
    }

    int quienEmpieza(int n){
        return random.nextInt(n);
    }

    public int getUltimoResultado(){
        return ultimoResultado;
    }
}
