
package civitas;

import java.util.ArrayList;
import java.util.Collections;
import GUI.*;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class CivitasJuego {
    
    private int indiceJugadorActual;
    
    //Atributos de referencia
    private ArrayList<Jugador> jugadores = new ArrayList<>();
    private EstadoJuego estado;
    private GestorEstados gestor;
    private MazoSorpresas mazo;
    private Tablero tablero;
    
    
    public CivitasJuego(ArrayList<String> nombres, boolean debug){
       
        for(int i = 0; i < nombres.size() ; i++){
           Jugador aux = new Jugador(nombres.get(i));
           jugadores.add(aux);
       } 
       
       gestor = new GestorEstados();
       estado = gestor.estadoInicial();
       
       Dado.getInstance().setDebug(debug);
       
       indiceJugadorActual = Dado.getInstance().quienEmpieza(jugadores.size());
       
       mazo = new MazoSorpresas(debug);
       
       tablero = new Tablero();
       inicializaTablero();
       
       inicializaMazoSorpresas();
        
    }
    
    public int getIndiceJugadorActual(){
        return indiceJugadorActual;
    }
    
    public Jugador getJugadorActual(){
        return jugadores.get(indiceJugadorActual);
    }
    
    public ArrayList<Jugador> getJugadores(){
        return jugadores;
    }
    
    public Tablero getTablero(){
        return tablero;
    }
    
    public boolean comprar(){
        Jugador jugadorActual = getJugadorActual();
        int numCasillaActual = jugadorActual.getCasillaActual();
        Casilla casilla = tablero.getCasilla(numCasillaActual);
        boolean res = jugadorActual.comprar((CasillaCalle)casilla);
        
        return res;    
    }
    
    public boolean construirCasa(int ip){
        return getJugadorActual().construirCasa(ip);
    }
    
    public boolean construirHotel(int ip){
        return getJugadorActual().construirHotel(ip);
    }
    
    private void contabilizarPasosPorSalida(){
        if(tablero.computarPasoPorSalida())
            getJugadorActual().pasaPorSalida();
    }
    
    public OperacionJuego siguientePaso(){
        Jugador jugadorActual = getJugadorActual();        
        OperacionJuego operacion = gestor.siguienteOperacion(jugadorActual, estado);
        
        if(operacion == OperacionJuego.PASAR_TURNO){
            pasarTurno();
            siguientePasoCompletado(operacion);
        }
        else if(operacion == OperacionJuego.AVANZAR){
            avanzaJugador();
            siguientePasoCompletado(operacion);
        }
        
        return operacion;
    }
    
    private void avanzaJugador(){
        Jugador jugadorActual = getJugadorActual();
        int posicionActual = jugadorActual.getCasillaActual();
        int tirada = Dado.getInstance().tirar();
        int posicionNueva = tablero.nuevaPosicion(posicionActual, tirada);
        Casilla casilla = tablero.getCasilla(posicionNueva);
        
        contabilizarPasosPorSalida();
        jugadorActual.moverACasilla(posicionNueva);
        casilla.recibeJugador(indiceJugadorActual, jugadores);
    }
    
    private void  pasarTurno(){
        if( indiceJugadorActual + 1 < jugadores.size()){
            indiceJugadorActual++;
        }
        else
            indiceJugadorActual = 0;
    }
    
    public void siguientePasoCompletado(OperacionJuego operacion){
        Jugador aux = jugadores.get(indiceJugadorActual);
        estado = gestor.siguienteEstado(aux, estado, operacion);
    }
    
    public ArrayList<Jugador> ranking(){
        Collections.sort(jugadores);
        Collections.reverse(jugadores);
        
        return jugadores;
    }
    
    public boolean finalDelJuego(){
        boolean bancarrota = false;
        for(int i = 0; i < jugadores.size() ; i++){
            if (jugadores.get(i).enBancarrota()){
                bancarrota = true;
            }
        }
        return bancarrota;
    }
    
    private void inicializaMazoSorpresas(){
        SorpresaPagarCobrar s1 = new SorpresaPagarCobrar("Te encuentras un billete de 20€ por la calle -> RECIBES 20", 20);
        mazo.alMazo(s1);
        
        SorpresaPorCasaHotel s2 = new SorpresaPorCasaHotel("Tus edificaciones contaminan demasiado -> PAGAS 20 POR CADA UNA DE TUS EDIFICACIONES", -20);
        mazo.alMazo(s2);

        SorpresaPagarCobrar s3 = new SorpresaPagarCobrar("Se te rompe el móvil y tienes que comprarte otro -> PAGAS 180", -180);
        mazo.alMazo(s3);
        
        SorpresaPorCasaHotel s4 = new SorpresaPorCasaHotel("Vuelve a subir el impuesto de la luz -> PAGAS 60 POR CADA UNA DE TUS EDIFICACIONES", -60);
        mazo.alMazo(s4);

        SorpresaPagarCobrar s5 = new SorpresaPagarCobrar("Tienes que pagar el gimnasio -> PAGAS 25", -25);
        mazo.alMazo(s5);

        SorpresaPagarCobrar s6 = new SorpresaPagarCobrar( "Ganas un sorteo de instagram -> RECIBES 150", 150);
        mazo.alMazo(s6);
        
        SorpresaConvertirme s7 = new SorpresaConvertirme("Me convierto en Especulador");
        mazo.alMazo(s7);
        
        SorpresaPorCasaHotel s8 = new SorpresaPorCasaHotel("Te dan un premio a la urbanización más bonita -> RECIBES 40 POR CADA UNA DE TUS EDIFICACIONES", 40);
        mazo.alMazo(s8);

        SorpresaPagarCobrar s9 = new SorpresaPagarCobrar("Adoptas un perro -> PAGAS 50", -50);
        mazo.alMazo(s9);

        SorpresaPagarCobrar s10 = new SorpresaPagarCobrar("Te ingresan dinero desde una cuenta sueca -> RECIBES 100", 100);
        mazo.alMazo(s10);

        SorpresaPorCasaHotel s11 = new SorpresaPorCasaHotel("Organizas una fiesta en todos tus edificios a tu nombre -> RECIBES 10 POR CADA UNA DE TUS EDIFICACIONES", 10);
        mazo.alMazo(s11);
    }
    
    private void inicializaTablero(){
        CasillaCalle c1 = new CasillaCalle("Libertad", 525, 250, 100);
        tablero.aniadeCasilla(c1);
        
        CasillaCalleSocial c2 = new CasillaCalleSocial("Serrano", 430, 120, 40);
        tablero.aniadeCasilla(c2);
        
        CasillaCalle c3 = new CasillaCalle("Coso", 300, 105, 30);
        tablero.aniadeCasilla(c3);
        
        CasillaSorpresa s1 = new CasillaSorpresa("Sourprise", mazo);
        tablero.aniadeCasilla(s1);
        
        CasillaCalle c4 = new CasillaCalle("Alcalá", 700, 300, 110);
        tablero.aniadeCasilla(c4);
        
        CasillaCalleSocial c5 = new CasillaCalleSocial("Castellana", 470, 220, 45);
        tablero.aniadeCasilla(c5);
        
        CasillaCalle c6 = new CasillaCalle("Gran Via", 1200, 500, 250);
        tablero.aniadeCasilla(c6);
        
        CasillaSorpresa s2 = new CasillaSorpresa("AbraCadabra", mazo);
        tablero.aniadeCasilla(s2);
        
        CasillaCalle c7 = new CasillaCalle("Ronda", 900, 380, 125);
        tablero.aniadeCasilla(c7);
        
        Casilla d1 = new Casilla("Silla de masaje");
        tablero.aniadeCasilla(d1);
        
        CasillaCalle c8 = new CasillaCalle("Montcada", 200, 40, 15);
        tablero.aniadeCasilla(c8);
        
        CasillaCalle c9 = new CasillaCalle("Fifth", 500, 150, 50);
        tablero.aniadeCasilla(c9);
        
        CasillaSorpresa s3 = new CasillaSorpresa("¿Qué pasará?", mazo);
        tablero.aniadeCasilla(s3);
        
        CasillaCalle c10 = new CasillaCalle("Embajadores", 650, 175, 75);
        tablero.aniadeCasilla(c10);
        
        CasillaCalle c11 = new CasillaCalle("Génova", 800, 350, 115);
        tablero.aniadeCasilla(c11);
        
        CasillaCalle c12 = new CasillaCalle("Poblados", 150, 35, 10);
        tablero.aniadeCasilla(c12);
        
        CasillaSorpresa s4 = new CasillaSorpresa("Cartita", mazo);
        tablero.aniadeCasilla(s4);
        
        CasillaCalle c13 = new CasillaCalle("Benavente", 210, 47, 17);
        tablero.aniadeCasilla(c13);
        
        CasillaCalle c14 = new CasillaCalle("Champs", 600, 165, 65);
        tablero.aniadeCasilla(c14);
    }
    
    public boolean hacerCalleSocial(int ip){
        
        boolean result = false;
        Jugador jugador = getJugadorActual();
        CasillaCalleSocial calleSocial = jugador.hacerCalleSocial(ip);
        
        if(calleSocial != null){
            int casillaActual = jugador.getCasillaActual();
            ArrayList<Casilla> casillas = tablero.getCasillas();
            casillas.set(casillaActual, calleSocial);
            result = true;
        }
        
        return result;   
    }
    
}
