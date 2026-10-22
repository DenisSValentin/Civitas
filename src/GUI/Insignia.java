
package GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.function.Supplier;
import javax.swing.BorderFactory;

/**
 * Etiqueta pequeña con fondo redondeado (por ejemplo, "Especulador").
 */
public class Insignia extends Etiqueta {

    private final Supplier<Color> fondo;

    public Insignia(String texto, Supplier<Color> fondo, Supplier<Color> color){
        super(texto, color);
        this.fondo = fondo;
        setFont(getFont().deriveFont(Font.BOLD, 10f));
        setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fondo.get());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
        g2.dispose();
        super.paintComponent(g);
    }
}
