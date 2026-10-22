
package civitas;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class GestionInmobiliaria {
    
    private OperacionInmobiliaria operacion;
    private int propiedad;

    
    public GestionInmobiliaria(OperacionInmobiliaria gest, int ip){
        propiedad = ip;
        operacion = gest;
    }
    
    OperacionInmobiliaria getOperacion(){
        return operacion;
    }
    
    int getPropiedad(){
        return propiedad;
    }
}
