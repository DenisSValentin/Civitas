
package GUI;

import civitas.Casilla;
import civitas.CivitasJuego;
import civitas.Jugador;
import controladorCivitas.Controlador;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

/**
 * Ventana principal: el tablero isométrico a la izquierda y, a la derecha,
 * los jugadores, las acciones del turno, la ficha de la casilla y el diario.
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class CivitasView extends JFrame implements Vista {

    private static final int ANCHO_LATERAL = 380;

    // Atributos de referencia
    private CivitasJuego juego;
    private Controlador controlador;

    private final TableroIsometrico tablero = new TableroIsometrico();
    private final PanelJugadores panelJugadores = new PanelJugadores();
    private final PanelAcciones panelAcciones = new PanelAcciones();
    private final PanelCasilla panelCasilla = new PanelCasilla();
    private final PanelDiario panelDiario = new PanelDiario();
    private final JLabel estado = new Etiqueta(" ", Tema::textoSecundario);
    private final JLabel modo = new Etiqueta("", Tema::textoSecundario);
    private final PanelLateral lateral = new PanelLateral();


    public CivitasView(){
        super("Civitas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(crearMenu());

        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 12));
        JComponent[] bloques = {panelJugadores, panelAcciones, panelCasilla, panelDiario};
        for(int i = 0; i < bloques.length; i++){
            bloques[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            if(i > 0)
                lateral.add(Box.createVerticalStrut(12));
            lateral.add(bloques[i]);
        }

        JScrollPane scroll = new JScrollPane(lateral, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(ANCHO_LATERAL, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        centro.add(tablero, BorderLayout.CENTER);

        JPanel barraEstado = new JPanel(new BorderLayout());
        barraEstado.setBorder(BorderFactory.createEmptyBorder(4, 14, 6, 14));
        barraEstado.add(estado, BorderLayout.WEST);
        barraEstado.add(modo, BorderLayout.EAST);

        getContentPane().add(centro, BorderLayout.CENTER);
        getContentPane().add(scroll, BorderLayout.EAST);
        getContentPane().add(barraEstado, BorderLayout.SOUTH);

        tablero.setAlCambiarHover(i -> refrescarFicha());
        getRootPane().setDefaultButton(panelAcciones.getBotonTirar());

        setMinimumSize(new Dimension(1100, 720));
        setSize(1400, 880);
        setLocationRelativeTo(null);
    }

    public void setControlador(Controlador controlador){
        this.controlador = controlador;
        panelAcciones.setControlador(controlador);
    }

    private JMenuBar crearMenu(){
        int atajo = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenuItem nueva = new JMenuItem("Nueva partida…");
        nueva.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, atajo));
        nueva.addActionListener(e -> pedirNuevaPartida());
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));
        JMenu partida = new JMenu("Partida");
        partida.add(nueva);
        partida.addSeparator();
        partida.add(salir);

        JCheckBoxMenuItem oscuro = new JCheckBoxMenuItem("Tema oscuro", Tema.esOscuro());
        oscuro.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, atajo | KeyEvent.SHIFT_DOWN_MASK));
        oscuro.addActionListener(e -> {
            Tema.cambiar(oscuro.isSelected());
            if(juego != null)
                actualiza();
        });
        JMenu ver = new JMenu("Ver");
        ver.add(oscuro);

        JMenuItem reglas = new JMenuItem("Cómo se juega");
        reglas.addActionListener(e -> mostrarReglas());
        JMenuItem acerca = new JMenuItem("Acerca de Civitas");
        acerca.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Civitas — PDOO 2021-22\nDenis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo",
                "Acerca de Civitas", JOptionPane.INFORMATION_MESSAGE));
        JMenu ayuda = new JMenu("Ayuda");
        ayuda.add(reglas);
        ayuda.add(acerca);

        JMenuBar barra = new JMenuBar();
        barra.add(partida);
        barra.add(ver);
        barra.add(ayuda);
        return barra;
    }

    private void mostrarReglas(){
        String reglas = "<html><div style='width:380px'>"
                + "<b>Objetivo.</b> No arruinarte: la partida acaba cuando un jugador se queda sin saldo, "
                + "y gana quien tenga más dinero.<br><br>"
                + "<b>Tu turno.</b> Tira el dado y avanza. Si caes en una calle libre puedes comprarla; "
                + "si tiene dueño, le pagas el alquiler. Las casillas sorpresa roban una carta.<br><br>"
                + "<b>Gestionar.</b> En tus calles puedes construir hasta 4 casas; con 4 casas puedes "
                + "cambiarlas por un hotel. Una calle social cobra un 2 % menos de alquiler.<br><br>"
                + "<b>Especulador.</b> Si te toca esa carta, pagas la mitad y puedes construir el doble.<br><br>"
                + "<b>Salida.</b> Cada vez que pasas por ella cobras 1.000 €.<br><br>"
                + "<b>Atajos.</b> Intro tira el dado; pasa el ratón por una casilla para ver su ficha."
                + "</div></html>";
        JOptionPane.showMessageDialog(this, reglas, "Cómo se juega", JOptionPane.PLAIN_MESSAGE);
    }

    private void pedirNuevaPartida(){
        DialogoNuevaPartida.Resultado datos = DialogoNuevaPartida.pedir(this);
        if(datos != null){
            panelDiario.limpiar();
            panelAcciones.reiniciar();
            controlador.nuevaPartida(datos.nombres(), datos.debug());
        }
    }

    private boolean animando(){
        return controlador != null && controlador.isAnimando();
    }

    private void refrescarFicha(){
        int hover = tablero.getHover();
        if(hover >= 0)
            panelCasilla.mostrar(juego, hover, true);
        else if(!animando())
            panelCasilla.mostrar(juego, juego.getJugadorActual().getCasillaActual(), false);
        lateral.revalidate();
    }

    private String mensajeTablero(){
        Jugador actual = juego.getJugadorActual();
        if(juego.finalDelJuego())
            return "Fin de la partida";
        if(animando())
            return actual.getNombre() + " está tirando…";
        if(juego.puedeAvanzar())
            return "Turno de " + actual.getNombre();

        Casilla casilla = juego.getTablero().getCasilla(actual.getCasillaActual());
        if(juego.puedeComprar())
            return casilla.getNombre() + " está libre";
        return "Gestiona o pasa el turno";
    }

    private String textoEstado(){
        if(juego.finalDelJuego())
            return "Partida terminada";
        if(animando())
            return "Moviendo ficha";
        if(juego.puedeAvanzar())
            return "Esperando tirada";
        return "Fase de gestión";
    }


    // VISTA

    @Override
    public void setCivitasJuego(CivitasJuego juego){
        this.juego = juego;
        tablero.setCivitasJuego(juego);
        modo.setText(juego.isDebug() ? "Modo debug: el dado siempre saca 1" : "");
    }

    @Override
    public void actualiza(){
        panelJugadores.actualiza(juego);
        actualizaBotones();
        refrescarFicha();
    }

    @Override
    public void actualizaBotones(){
        panelAcciones.actualiza(juego, animando());
        tablero.setMensaje(mensajeTablero());
        estado.setText(textoEstado());
        lateral.revalidate();
        lateral.repaint();
    }

    @Override
    public void animarTirada(int jugador, int desde, int tirada, Runnable alTerminar){
        tablero.animarTirada(jugador, desde, tirada, alTerminar);
    }

    @Override
    public void mostrarSorpresa(String texto){
        panelAcciones.mostrarSorpresa(texto);
    }

    @Override
    public void mostrarEventos(List<String> eventos){
        panelDiario.agregar(eventos);
    }

    @Override
    public void mostrarFinDelJuego(List<Jugador> ranking){
        SwingUtilities.invokeLater(() -> {
            if(DialogoFinPartida.mostrar(this, juego, ranking))
                pedirNuevaPartida();
        });
    }


    /** Lateral que se ajusta al ancho del scroll y solo se desplaza en vertical. */
    private static class PanelLateral extends JPanel implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize(){
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visible, int orientacion, int direccion){
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visible, int orientacion, int direccion){
            return visible.height - 32;
        }

        @Override
        public boolean getScrollableTracksViewportWidth(){
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight(){
            return false;
        }
    }
}
