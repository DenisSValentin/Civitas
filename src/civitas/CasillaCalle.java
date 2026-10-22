
package civitas;

import java.util.ArrayList;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class CasillaCalle extends Casilla{
    
    private static float FACTORALQUILERCALLE = 1.0f;
    private static float FACTORALQUILERCASA = 1.0f;
    private static float FACTORALQUILERHOTEL = 4.0f;
    
    private float precioCompra;
    private float precioEdificar;
    private float precioBaseAlquiler;
    private int numCasas;
    private int numHoteles;
    
    // Atributos de referencia
    private Jugador propietario;

    
    CasillaCalle(String nombre, float precioCompra, float precioEdificar,
            float precioBaseAlquiler){
        
        super(nombre);
        this.precioCompra = precioCompra;
        this.precioEdificar = precioEdificar;
        this.precioBaseAlquiler = precioBaseAlquiler;
        this.numCasas = 0;
        this.numHoteles = 0;
        propietario = null;
    }
    
    public int getNumCasas(){
        return numCasas;
    }
    
    public int getNumHoteles(){
        return numHoteles;
    }
    
    float getPrecioCompra(){
        return precioCompra;
    }
    
    float getPrecioEdificar(){
        return precioEdificar;
    }
    
    float getPrecioBaseAlquiler(){
        return precioBaseAlquiler;
    }
    
    float getPrecioAlquilerCompleto(){
        return (precioBaseAlquiler * ( FACTORALQUILERCALLE + numCasas * FACTORALQUILERCASA +
                numHoteles * FACTORALQUILERHOTEL));
    }
    
    public int cantidadCasasHoteles(){
        return (numCasas + numHoteles);
    }
    
    public boolean tienePropietario(){
        return (propietario != null);
    }
    
    public boolean esEsteElPropietario(Jugador jugador){
        return (propietario == jugador);
    }
    
    boolean comprar(Jugador jugador){
       propietario = jugador; 
       return propietario.paga(precioCompra); 
    }

    boolean construirCasa(Jugador jugador){
        jugador.paga(precioEdificar);
        numCasas++;
        return true;
    }

    boolean construirHotel(Jugador jugador){
        propietario.paga(precioEdificar);
        numHoteles++;
        return true;
    }
    
    boolean derruirCasas(int numero, Jugador jugador){
        boolean demolicion = false;
        
        if(esEsteElPropietario(jugador) && numCasas >= numero){
            numCasas = numCasas - numero;
            demolicion = true;
        }
        
        return demolicion;
    }
     
    public void tramitarAlquiler(Jugador jugador){
        if(tienePropietario() && !esEsteElPropietario(jugador)){
            jugador.pagaAlquiler(getPrecioAlquilerCompleto());
            propietario.recibe(getPrecioAlquilerCompleto());
        }
    }
     
    @Override
    void recibeJugador(int iactual, ArrayList <Jugador> todos){
        Jugador aux = todos.get(iactual);
        informe(iactual, todos);
        
        if(!tienePropietario())
            aux.puedeComprarCasilla();
        else
            tramitarAlquiler(aux); 
    }
    
    @Override
    public String toString(){
        if(tienePropietario()){
        return "Calle '" + getNombre() + "', Dueño '" + propietario.getNombre() +
            "', PRECIOS: Compra: " + precioCompra + ", Edificar: "
            + precioEdificar + ", Alquiler base: " + precioBaseAlquiler +
            ", PrecioAlquilerCompleto: " + getPrecioAlquilerCompleto()
            + ", Casas: " + numCasas + ", Hoteles: " + numHoteles;
      }
      else {
          return "Calle '" + getNombre() + "', PRECIOS: Compra: " + precioCompra +
            ", Edificar: " + precioEdificar + ", Alquiler base: " +
            precioBaseAlquiler + ", Casas: " + numCasas + ", Hoteles: " +
            numHoteles;
      }
    }
    
    
    // MÉTODOS PARA JUGADOR ESPECULADOR
    public void actualizaPropietarioPorConversion(JugadorEspeculador jugador){
        propietario = jugador;
    }
    
    // MÉTODOS PARA CASILLA CALLE SOCIAL
    public CasillaCalleSocial hacermeSocial(Jugador jugador){
        CasillaCalleSocial calleSocial = new CasillaCalleSocial(this.nombre,
        this.precioCompra, this.precioEdificar, this.precioBaseAlquiler);
        
        calleSocial.setPropietario(jugador);
        
        return calleSocial;
    }
    
    public void setPropietario(Jugador propietario){
        this.propietario = propietario;
    }
    
    public boolean soySocial(){
        return false;
    }
    
    public void setNumCasas(int numCasas){
        this.numCasas = numCasas;
    }
    
    public void setNumHoteles(int numHoteles){
        this.numHoteles = numHoteles;
    }
    
}
