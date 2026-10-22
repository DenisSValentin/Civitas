
package civitas;

import java.util.ArrayList;
import java.util.Collections;

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
    private Sorpresa ultimaSorpresa;


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

       Diario.getInstance().ocurreEvento("Empieza la partida. Turno de " + getJugadorActual().getNombre());
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

    public boolean isDebug(){
        return Dado.getInstance().isDebug();
    }

    // Carta sacada en la última tirada, o null si no se cayó en una sorpresa
    public Sorpresa getUltimaSorpresa(){
        return ultimaSorpresa;
    }


    // OPERACIONES PERMITIDAS: la vista las consulta para activar sus botones

    private boolean permite(OperacionJuego operacion){
        return !finalDelJuego() && gestor.permite(getJugadorActual(), estado, operacion);
    }

    public boolean puedeAvanzar(){
        return permite(OperacionJuego.AVANZAR);
    }

    public boolean puedeComprar(){
        Casilla casilla = tablero.getCasilla(getJugadorActual().getCasillaActual());
        return permite(OperacionJuego.COMPRAR) && casilla instanceof CasillaCalle
                && getJugadorActual().tieneSaldoPara((CasillaCalle) casilla);
    }

    public boolean puedeGestionar(){
        return permite(OperacionJuego.GESTIONAR);
    }

    public boolean puedePasarTurno(){
        return permite(OperacionJuego.PASAR_TURNO);
    }

    public boolean puedeConstruirCasa(int ip){
        return puedeGestionar() && getJugadorActual().puedeConstruirCasa(ip);
    }

    public boolean puedeConstruirHotel(int ip){
        return puedeGestionar() && getJugadorActual().puedeConstruirHotel(ip);
    }

    public boolean puedeHacerCalleSocial(int ip){
        return puedeGestionar() && getJugadorActual().puedeHacerSocial(ip);
    }


    // OPERACIONES DEL TURNO

    /**
     * Tira el dado y mueve al jugador actual.
     * @return el resultado del dado, o 0 si ahora no se puede avanzar
     */
    public int avanzar(){
        if(!puedeAvanzar())
            return 0;

        avanzaJugador();
        operacionCompletada(OperacionJuego.AVANZAR);
        return Dado.getInstance().getUltimoResultado();
    }

    public boolean comprar(){
        if(!puedeComprar())
            return false;

        Jugador jugadorActual = getJugadorActual();
        int numCasillaActual = jugadorActual.getCasillaActual();
        Casilla casilla = tablero.getCasilla(numCasillaActual);
        boolean res = jugadorActual.comprar((CasillaCalle)casilla);

        operacionCompletada(OperacionJuego.COMPRAR);
        return res;
    }

    public boolean construirCasa(int ip){
        if(!puedeConstruirCasa(ip))
            return false;

        boolean res = getJugadorActual().construirCasa(ip);
        operacionCompletada(OperacionJuego.GESTIONAR);
        return res;
    }

    public boolean construirHotel(int ip){
        if(!puedeConstruirHotel(ip))
            return false;

        boolean res = getJugadorActual().construirHotel(ip);
        operacionCompletada(OperacionJuego.GESTIONAR);
        return res;
    }

    public boolean hacerCalleSocial(int ip){
        if(!puedeHacerCalleSocial(ip))
            return false;

        Jugador jugador = getJugadorActual();
        CasillaCalle calle = jugador.getPropiedades().get(ip);
        int posicionEnTablero = tablero.getCasillas().indexOf(calle);
        CasillaCalleSocial calleSocial = jugador.hacerCalleSocial(ip);

        if(calleSocial == null)
            return false;

        // Se sustituye la casilla de la propiedad, no la casilla en la que está el jugador
        tablero.getCasillas().set(posicionEnTablero, calleSocial);
        operacionCompletada(OperacionJuego.GESTIONAR);
        return true;
    }

    public boolean pasarTurno(){
        if(!puedePasarTurno())
            return false;

        siguienteJugador();
        operacionCompletada(OperacionJuego.PASAR_TURNO);
        Diario.getInstance().ocurreEvento("Turno de " + getJugadorActual().getNombre());
        return true;
    }

    private void operacionCompletada(OperacionJuego operacion){
        estado = gestor.siguienteEstado(estado, operacion);
    }

    private void contabilizarPasosPorSalida(){
        if(tablero.computarPasoPorSalida())
            getJugadorActual().pasaPorSalida();
    }

    private void avanzaJugador(){
        Jugador jugadorActual = getJugadorActual();
        int posicionActual = jugadorActual.getCasillaActual();
        int tirada = Dado.getInstance().tirar();
        int posicionNueva = tablero.nuevaPosicion(posicionActual, tirada);
        Casilla casilla = tablero.getCasilla(posicionNueva);

        Diario.getInstance().ocurreEvento(jugadorActual.getNombre() + " saca un " + tirada);
        contabilizarPasosPorSalida();
        jugadorActual.moverACasilla(posicionNueva);
        casilla.recibeJugador(indiceJugadorActual, jugadores);

        if(casilla instanceof CasillaSorpresa)
            ultimaSorpresa = ((CasillaSorpresa) casilla).getUltimaSorpresa();
        else
            ultimaSorpresa = null;
    }

    private void siguienteJugador(){
        if( indiceJugadorActual + 1 < jugadores.size()){
            indiceJugadorActual++;
        }
        else
            indiceJugadorActual = 0;
    }

    // Devuelve una copia ordenada: el orden de turnos no se toca
    public ArrayList<Jugador> ranking(){
        ArrayList<Jugador> ranking = new ArrayList<>(jugadores);
        ranking.sort(Collections.reverseOrder());
        return ranking;
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

        SorpresaConvertirme s7 = new SorpresaConvertirme("Me convierto en Especulador -> PAGO LA MITAD Y PUEDO CONSTRUIR EL DOBLE");
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

}
