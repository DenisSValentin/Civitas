
package civitas;


import java.util.ArrayList;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class Tablero {
    
    private boolean porSalida;
    
    // Atributos de referencia
    private ArrayList<Casilla> casillas = new ArrayList<>();
    
    
    public Tablero(){
        Casilla salida = new Casilla("SALIDA");
        casillas.add(salida);
        porSalida = false;
    }
    
    void aniadeCasilla(Casilla casilla){
        casillas.add(casilla);
    }
    
    int getCasillasTotales(){
       return casillas.size();
    }
    
    public ArrayList<Casilla> getCasillas(){
        return casillas;
    }
    
    private boolean correcto(int numCasilla){
        return (numCasilla >= 0 && numCasilla < casillas.size());
    }
    
    public Casilla getCasilla(int numCasilla){       
        if(correcto(numCasilla))
            return casillas.get(numCasilla);
        else
            return null;
    }
    
    int nuevaPosicion(int actual, int tirada){
        
        int nueva;
        
        nueva = actual + tirada;
        
        if(nueva > casillas.size()){
            nueva = nueva % casillas.size();
            porSalida = true;
        }
        
        return nueva;
    }
    
    boolean computarPasoPorSalida(){
        boolean almacenamiento;
        almacenamiento = porSalida;
        porSalida = false;
        return almacenamiento;  
    }
    
}
