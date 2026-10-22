
package civitas;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class MazoSorpresas {
    
    private ArrayList<Sorpresa> sorpresas;
    private boolean barajada;
    private int usadas;
    private boolean debug;
    
    
    private void init(){
        sorpresas = new ArrayList<>();
        barajada = false;
        usadas = 0;
    }
    
    MazoSorpresas(){
        init();
        debug = false;
    }
    
    MazoSorpresas(boolean d){
        debug = d;
        init();
    }
    
    void alMazo(Sorpresa s){
        if(!barajada)
            sorpresas.add(s);
    }
    
    Sorpresa siguiente(){
        if((!barajada || usadas == sorpresas.size()) && !debug){
            Collections.shuffle(sorpresas);
            usadas = 0;
            barajada = true;
        }
        
            usadas++;
            
            Sorpresa primera = sorpresas.get(0);
            
            sorpresas.remove(0);
            
            sorpresas.add(primera);
            
            return primera;
        }
    }