
package civitas;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class JugadorEspeculador extends Jugador {
    
    private static int FactorEspeculador = 2;
    
    
    protected JugadorEspeculador(Jugador otro){
        super(otro);
        actualizaPropiedadesPorConversion();
    }
    
    @Override
    public int getCasasMax(){
        return CasasMax * FactorEspeculador;
    }
    
    @Override
    public int getHotelesMax(){
        return HotelesMax * FactorEspeculador;
    }
    
    private void actualizaPropiedadesPorConversion(){
        for(int i = 0; i < getPropiedades().size() ; i++){
            getPropiedades().get(i).actualizaPropietarioPorConversion(this);
        }
    }
    
    @Override
    boolean paga(float cantidad){
        return modificarSaldo((-1)*cantidad/FactorEspeculador);
    }
    
    @Override
    public boolean esEspeculador(){
        return true;
    }
    
}
