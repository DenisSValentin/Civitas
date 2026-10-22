
package civitas;

import java.util.ArrayList;
import GUI.*;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class Jugador implements Comparable<Jugador> {
    
    static final private float SaldoInicial = 7500;
    static final protected float PasoPorSalida = 1000;
    static final protected int CasasMax = 4;
    static final protected int CasasPorHotel = 4;
    static final protected int HotelesMax = 4;
    private String nombre;
    private int casillaActual;
    private float saldo;
    private boolean puedeComprar;
    
    // Atributos de referencia
    private ArrayList<CasillaCalle> propiedades = new ArrayList<>();
    
    
    Jugador(String nombre){
        this.nombre = nombre;
        casillaActual = 0;
        puedeComprar = true;
        saldo = SaldoInicial;
    }
    
    protected Jugador(Jugador otro){
        nombre = otro.getNombre();
        casillaActual = otro.getCasillaActual();
        puedeComprar = otro.getPuedeComprar();
        saldo = otro.getSaldo();
        propiedades = otro.getPropiedades();   
    }
    
    public String getNombre(){
     return nombre;   
    }
    
    public int getCasillaActual(){
        return casillaActual;
    }
    
    public float getSaldo(){
        return saldo;
    }
    
    boolean getPuedeComprar(){
        return puedeComprar;
    }
    
    boolean enBancarrota(){
       return (saldo <= 0); 
    }
    
    int getCasasMax(){
        return CasasMax;
    }
    
    int getHotelesMax(){
        return HotelesMax;
    }
    
    int getCasasPorHotel(){
        return CasasPorHotel;
    }
    
    private float getPremioPasoSalida(){
        return PasoPorSalida;
    }
    
    public ArrayList<CasillaCalle> getPropiedades(){
        return propiedades;
    }
    
    public int getCantidadCasasHoteles(){
        int edificaciones = 0;
        for(int i = 0; i < propiedades.size(); i++)
                edificaciones += propiedades.get(i).cantidadCasasHoteles();

        return edificaciones;
    }
    
    boolean tieneAlgoQueGestionar(){
        return (propiedades.size() > 0);
    }
    
    private boolean existeLaPropiedad(int ip){
        return (0 <= ip && ip <= propiedades.size());
    }
    
    boolean modificarSaldo(float cantidad){
        saldo = saldo + cantidad;
        Diario.getInstance().ocurreEvento("El saldo de " + getNombre()
			                  + " ahora es: " + getSaldo());
        return true;
    }
    
    boolean recibe(float cantidad){
        return modificarSaldo(cantidad);
    }
    
    boolean paga(float cantidad){
        return modificarSaldo((-1)*cantidad);
    }
    
    boolean pagaAlquiler(float cantidad){
        return paga(cantidad);
    }
    
    private boolean puedoGastar(float precio){
        return (saldo >= precio);
    }
    
    boolean puedeComprarCasilla(){
        puedeComprar = true;
        return puedeComprar;
    }
    
    boolean comprar(CasillaCalle titulo){
        boolean result = false;
        float precio = 0;
        
        if(puedeComprar){
            precio = titulo.getPrecioCompra();
            
            if(puedoGastar(precio)){
                result = titulo.comprar(this);
                propiedades.add(titulo);
                Diario.getInstance().ocurreEvento("El jugador " + this.getNombre() +
                        " compra la propiedad " + titulo);
                puedeComprar = false;
            }
            else
                Diario.getInstance().ocurreEvento("El jugador " + getNombre() +
                        " no tiene saldo para comprar la propiedad " + titulo);   
        }
        
        return result;
    }

     private boolean puedoEdificarCasa(CasillaCalle propiedad){
        return puedoGastar(propiedad.getPrecioEdificar()) && 
                                propiedad.getNumCasas() < getCasasMax();
    }
    
    private boolean puedoEdificarHotel(CasillaCalle propiedad){
        return puedoGastar(propiedad.getPrecioEdificar()) &&
                    propiedad.getNumHoteles() < getHotelesMax() &&
                    propiedad.getNumCasas() == getCasasPorHotel();
    }
    
    boolean construirCasa(int ip){
        boolean result = false;
        boolean existe, puedoEdificar;
        CasillaCalle propiedad;
        
        existe = existeLaPropiedad(ip);
        
        if(existe){
            propiedad = propiedades.get(ip);
            puedoEdificar = puedoEdificarCasa(propiedad);
            
            if(puedoEdificar){
                result = propiedad.construirCasa(this);
                
                Diario.getInstance().ocurreEvento("El jugador " + getNombre() +
                                    " construye una casa en la propiedad "+ip);
            }
        }
        
        return result;
    }

    boolean construirHotel(int ip){
        boolean result = false;
        CasillaCalle propiedad;
        boolean puedoEdificarHotel;
        float precio;
        
        if(existeLaPropiedad(ip)){
            propiedad = propiedades.get(ip);
            puedoEdificarHotel = puedoEdificarHotel(propiedad);
            
            precio = propiedad.getPrecioEdificar();
            
            if(puedoEdificarHotel){
                
                result = propiedad.construirHotel(this);
                propiedad.derruirCasas(getCasasPorHotel(), this);
                
                Diario.getInstance().ocurreEvento("El jugador " + getNombre() +
                        " construye hotel en la propiedad " + ip); 
            }
            else
                System.out.println("Solo puedes construir un hotel si tienes 4 casas construidas.");
        }
        
        return result;
    }
    
    boolean moverACasilla(int numCasilla){
        casillaActual = numCasilla;
        puedeComprar = false;
        System.out.println("\nSe lanza un dado y sale un: " + Dado.getInstance().getUltimoResultado());
        Diario.getInstance().ocurreEvento("La casilla de " + getNombre()
			                  + " ahora es: " + getCasillaActual());
        return true;
    }
    
    boolean pasaPorSalida(){
        recibe(getPremioPasoSalida());
        Diario.getInstance().ocurreEvento("El jugador " + getNombre()
			                  + " ha pasado por salida y ha recibido " 
                                          + getPremioPasoSalida() + " monedas.");
        return true;
    }
    
    @Override
    public int compareTo(Jugador otro){
        return Float.compare(saldo, otro.getSaldo());
    }
    
    public String toString(){
     return "Este jugador es '" + getNombre() + "', está en la casilla "
            + getCasillaActual() + ", y tiene " + getSaldo() + " saldo.";   
    } 
    
    
    // MÉTODOS PARA JUGADOR ESPECULADOR
    
    JugadorEspeculador convertir(){
        JugadorEspeculador jE = new JugadorEspeculador(this);
        return jE;
    }
    
    public boolean esEspeculador(){
        return false;
    }
    
    
    // MÉTODOS PARA CASILLA CALLE SOCIAL
    
    CasillaCalleSocial hacerCalleSocial(int ip){
        
        CasillaCalleSocial calleSocial = null;
        
        if(existeLaPropiedad(ip)){
            CasillaCalle calle = propiedades.get(ip);
            calleSocial = calle.hacermeSocial(this);
            
            if(calleSocial != null){
                Diario.getInstance().ocurreEvento("Calle socializada");
                propiedades.set(ip, calleSocial);
            }
        }
        
        return calleSocial; 
    }
    
}
