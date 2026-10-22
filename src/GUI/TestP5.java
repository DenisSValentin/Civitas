/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.*;
import civitas.*;
import controladorCivitas.*;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class TestP5 {
    
    public static void main(String[] args){
        
        CivitasView vista = new CivitasView();
        CapturaNombres nombres = new CapturaNombres(vista, true);
        ArrayList<String> jugadores = new ArrayList<>();
        
        jugadores = nombres.getNombres();
        
        Dado.createInstance(vista);
        
        CivitasJuego juego = new CivitasJuego(jugadores, false);
        
        Controlador controlador = new Controlador(juego, vista);
     
        vista.setCivitasJuego(juego);

        controlador.juega();
        
    }
}
