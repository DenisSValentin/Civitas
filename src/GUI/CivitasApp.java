
package GUI;

import civitas.CivitasJuego;
import controladorCivitas.Controlador;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada de Civitas.
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class CivitasApp {

    public static void main(String[] args){
        // En macOS: menú en la barra del sistema y nombre de la aplicación
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "Civitas");
        System.setProperty("apple.awt.application.appearance", "system");

        SwingUtilities.invokeLater(() -> {
            Tema.instalar(false);

            DialogoNuevaPartida.Resultado datos = DialogoNuevaPartida.pedir(null);
            if(datos == null)
                System.exit(0);

            CivitasJuego juego = new CivitasJuego(datos.nombres(), datos.debug());
            CivitasView vista = new CivitasView();
            Controlador controlador = new Controlador(juego, vista);
            vista.setControlador(controlador);
            controlador.iniciar();
            vista.setVisible(true);
        });
    }
}
