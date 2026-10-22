
package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;

/**
 * Círculo de color: identifica a cada jugador y el tono de cada evento del diario.
 */
public class CirculoIcon implements Icon {

    private final Color color;
    private final int diametro;

    public CirculoIcon(Color color, int diametro){
        this.color = color;
        this.diametro = diametro;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(x, y, diametro, diametro);
        g2.dispose();
    }

    @Override
    public int getIconWidth(){
        return diametro;
    }

    @Override
    public int getIconHeight(){
        return diametro;
    }
}
