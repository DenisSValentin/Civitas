
package GUI;

import civitas.CivitasJuego;
import civitas.Formato;
import civitas.Jugador;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Lista de jugadores con su saldo, sus propiedades y quién tiene el turno.
 */
public class PanelJugadores extends Tarjeta {

    private static final float SALDO_BAJO = 1000;

    private final JPanel filas = new JPanel(new GridLayout(0, 1, 0, 2));

    public PanelJugadores(){
        super(new BorderLayout(0, 8));
        filas.setOpaque(false);
        add(titulo("Jugadores"), BorderLayout.NORTH);
        add(filas, BorderLayout.CENTER);
    }

    public void actualiza(CivitasJuego juego){
        filas.removeAll();
        List<Jugador> jugadores = juego.getJugadores();
        for(int i = 0; i < jugadores.size(); i++)
            filas.add(new FilaJugador(jugadores.get(i), i, i == juego.getIndiceJugadorActual()));
        revalidate();
        repaint();
    }

    private static class FilaJugador extends JPanel {

        private final boolean activo;

        FilaJugador(Jugador jugador, int indice, boolean activo){
            super(new BorderLayout(10, 0));
            this.activo = activo;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));

            JLabel nombre = new JLabel(jugador.getNombre());
            nombre.setFont(nombre.getFont().deriveFont(Font.BOLD));

            JPanel lineaNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            lineaNombre.setOpaque(false);
            lineaNombre.add(nombre);
            if(jugador.esEspeculador()){
                lineaNombre.add(Box.createHorizontalStrut(6));
                lineaNombre.add(new Insignia("Especulador", Tema::insigniaFondo, Tema::insigniaTexto));
            }

            int propiedades = jugador.getPropiedades().size();
            int edificios = jugador.getCantidadCasasHoteles();
            JLabel detalle = new Etiqueta(propiedades + (propiedades == 1 ? " propiedad · " : " propiedades · ")
                    + edificios + (edificios == 1 ? " edificio" : " edificios"), Tema::textoSecundario);
            detalle.setFont(detalle.getFont().deriveFont(12f));

            JPanel centro = new JPanel();
            centro.setOpaque(false);
            centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
            lineaNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
            detalle.setAlignmentX(Component.LEFT_ALIGNMENT);
            centro.add(lineaNombre);
            centro.add(detalle);

            boolean bajo = jugador.getSaldo() < SALDO_BAJO;
            JLabel saldo = new Etiqueta(Formato.dinero(jugador.getSaldo()), bajo ? Tema::negativo : Tema::texto);
            saldo.setFont(saldo.getFont().deriveFont(Font.BOLD, 14f));

            add(new JLabel(new CirculoIcon(Tema.jugador(indice), 12)), BorderLayout.WEST);
            add(centro, BorderLayout.CENTER);
            add(saldo, BorderLayout.EAST);

            getAccessibleContext().setAccessibleName(jugador.getNombre() + ", saldo "
                    + Formato.dinero(jugador.getSaldo()) + (activo ? ", tiene el turno" : ""));
        }

        @Override
        protected void paintComponent(Graphics g){
            if(activo){
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Tema.resaltado());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(Tema.bordeResaltado());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
