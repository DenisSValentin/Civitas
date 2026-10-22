
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel con fondo y borde redondeados, la base de cada bloque del lateral.
 */
public class Tarjeta extends JPanel {

    protected static final int RADIO = 14;

    public Tarjeta(LayoutManager layout){
        super(layout);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
    }

    public Tarjeta(){
        this(new BorderLayout());
    }

    public static JLabel titulo(String texto){
        JLabel titulo = new Etiqueta(texto.toUpperCase(), Tema::textoSecundario);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 11f));
        return titulo;
    }

    protected Color getFondo(){
        return Tema.tarjeta();
    }

    protected Color getBorde(){
        return Tema.bordeTarjeta();
    }

    protected RoundRectangle2D forma(){
        return new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RADIO, RADIO);
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getFondo());
        g2.fill(forma());
        g2.setColor(getBorde());
        g2.draw(forma());
        g2.dispose();
    }

    // En un BoxLayout vertical, que no crezca más de lo que necesita
    @Override
    public Dimension getMaximumSize(){
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}
