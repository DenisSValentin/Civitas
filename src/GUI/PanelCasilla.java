
package GUI;

import civitas.Casilla;
import civitas.CasillaCalle;
import civitas.CasillaSorpresa;
import civitas.CivitasJuego;
import civitas.Formato;
import civitas.Jugador;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ficha de una casilla: la que está bajo el ratón o, si no, la del jugador actual.
 */
public class PanelCasilla extends Tarjeta {

    private static final int ALTO_BANDA = 6;

    private final JLabel tipo = new Etiqueta("", Tema::textoSecundario);
    private final JLabel origen = new Etiqueta("", Tema::textoSecundario);
    private final JLabel nombre = new JLabel();
    private final JPanel datos = new JPanel(new GridBagLayout());
    private final JLabel descripcion = new Etiqueta("", Tema::textoSecundario);
    private Color banda;

    public PanelCasilla(){
        super(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(14 + ALTO_BANDA, 16, 14, 16));

        tipo.setFont(tipo.getFont().deriveFont(Font.BOLD, 11f));
        origen.setFont(origen.getFont().deriveFont(11f));
        nombre.setFont(nombre.getFont().deriveFont(Font.BOLD, 17f));
        datos.setOpaque(false);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(tipo, BorderLayout.WEST);
        cabecera.add(origen, BorderLayout.EAST);

        JPanel arriba = new JPanel(new BorderLayout(0, 2));
        arriba.setOpaque(false);
        arriba.add(cabecera, BorderLayout.NORTH);
        arriba.add(nombre, BorderLayout.CENTER);

        JPanel abajo = new JPanel(new BorderLayout(0, 6));
        abajo.setOpaque(false);
        abajo.add(datos, BorderLayout.NORTH);
        abajo.add(descripcion, BorderLayout.CENTER);

        add(arriba, BorderLayout.NORTH);
        add(abajo, BorderLayout.CENTER);
    }

    public void mostrar(CivitasJuego juego, int indice, boolean bajoElRaton){
        Casilla casilla = juego.getTablero().getCasilla(indice);
        Jugador actual = juego.getJugadorActual();

        nombre.setText(casilla.getNombre());
        origen.setText(bajoElRaton ? "Bajo el ratón" : "Casilla de " + actual.getNombre());
        datos.removeAll();
        banda = null;

        if(casilla instanceof CasillaCalle calle){
            tipo.setText(calle.soySocial() ? "CALLE SOCIAL" : "CALLE");
            Jugador dueno = calle.getPropietario();
            if(dueno != null)
                banda = Tema.jugador(juego.getJugadores().indexOf(dueno));

            fila(0, "Dueño", dueno == null ? "Libre" : dueno.getNombre());
            fila(1, "Compra", Formato.dinero(calle.getPrecioCompra()));
            fila(2, "Alquiler actual", Formato.dinero(calle.getPrecioAlquilerCompleto()));
            fila(3, "Alquiler base", Formato.dinero(calle.getPrecioBaseAlquiler()));
            fila(4, "Edificar", Formato.dinero(calle.getPrecioEdificar()));
            fila(5, "Edificios", Tema.edificios(calle.getNumCasas(), calle.getNumHoteles()));
            descripcion.setText(calle.soySocial()
                    ? Tema.html("Calle social: el alquiler es un 2 % más barato.", 290) : "");
        }
        else if(casilla instanceof CasillaSorpresa){
            tipo.setText("SORPRESA");
            descripcion.setText(Tema.html("Al caer aquí se roba una carta del mazo de sorpresas.", 290));
        }
        else if(indice == 0){
            tipo.setText("SALIDA");
            descripcion.setText(Tema.html("Cada vez que pasas por aquí cobras " + Formato.dinero(1000) + ".", 290));
        }
        else {
            tipo.setText("DESCANSO");
            descripcion.setText(Tema.html("Aquí no pasa nada: descansa hasta el siguiente turno.", 290));
        }

        descripcion.setVisible(!descripcion.getText().isEmpty());
        revalidate();
        repaint();
    }

    private void fila(int y, String clave, String valor){
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = y;
        gc.insets = new Insets(2, 0, 2, 0);
        gc.anchor = GridBagConstraints.WEST;
        gc.weightx = 1;
        gc.gridx = 0;
        datos.add(new Etiqueta(clave, Tema::textoSecundario), gc);

        JLabel v = new JLabel(valor, SwingConstants.RIGHT);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.EAST;
        datos.add(v, gc);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        // Banda superior con el color del dueño, recortada a las esquinas redondeadas
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Area area = new Area(forma());
        area.intersect(new Area(new Rectangle2D.Double(0, 0, getWidth(), ALTO_BANDA)));
        g2.setColor(banda != null ? banda : Tema.bordeTarjeta());
        g2.fill(area);
        g2.dispose();
    }
}
