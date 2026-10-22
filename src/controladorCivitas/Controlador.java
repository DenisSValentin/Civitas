
package controladorCivitas;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
import civitas.CivitasJuego;
import civitas.OperacionJuego;
import civitas.OperacionInmobiliaria;
import GUI.*;

public class Controlador {
    
    private CivitasJuego juego;
    private CivitasView vista;
    
    
    public Controlador(CivitasJuego juego, CivitasView vista){
        this.juego = juego;
        this.vista = vista;
    }
    
    public void juega(){
        
        OperacionJuego opJuego;
        Respuesta respuesta;
        OperacionInmobiliaria opInmob;
        
        while (!juego.finalDelJuego()){
            
            System.out.println("\n------------------------------");
            
            vista.actualiza();
            vista.pausa();
          
            opJuego = juego.siguientePaso();
            vista.mostrarSiguienteOperacion(opJuego);
            
            if(opJuego != OperacionJuego.PASAR_TURNO){
                vista.mostrarEventos();
            }
            
            if(!juego.finalDelJuego()){
                if( opJuego == OperacionJuego.COMPRAR){
                    respuesta = vista.comprar();
                    
                    if(respuesta == Respuesta.SI){
                        juego.comprar();
                        System.out.println("\nCompras esta propiedad.");
                    }
                    
                    else if(respuesta == Respuesta.NO)
                        System.out.println("\nHas decidido no comprar esta propiedad.");
                    
                    juego.siguientePasoCompletado(opJuego);
                }
                
                else if (opJuego == OperacionJuego.GESTIONAR){
                    opInmob = vista.elegirOperacion();
                    if(opInmob != OperacionInmobiliaria.TERMINAR){
                        int propiedad = vista.elegirPropiedad();
                        
                        if(opInmob == OperacionInmobiliaria.CONSTRUIR_CASA)
                            juego.construirCasa(propiedad);
                        
                        if(opInmob == OperacionInmobiliaria.CONSTRUIR_HOTEL)
                            juego.construirHotel(propiedad);
                        
                        if(opInmob == OperacionInmobiliaria.HACER_CALLE_SOCIAL)
                            juego.hacerCalleSocial(propiedad);
                    }
                    
                    if(opInmob == OperacionInmobiliaria.TERMINAR){
                        juego.siguientePasoCompletado(opJuego);
                    }
                }
            }
        }
        
        juego.ranking();

        System.out.println("\nSE HA ACABADO EL JUEGO, ALGUIEN HA ENTRADO EN BANCARROTA, LA TABLA DE PUNTUACIONES ES: \n");
        
        for(int i = 0; i < juego.getJugadores().size() ; i++){
            System.out.println("Puesto " + (i+1) + " -> " + juego.getJugadores().get(i).getNombre() + " con saldo: " +
                    juego.getJugadores().get(i).getSaldo());
        }
        
    }
    
    
}
