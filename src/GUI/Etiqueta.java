
package GUI;

import java.awt.Color;
import java.util.function.Supplier;
import javax.swing.JLabel;

/**
 * JLabel cuyo color se pide al tema cada vez que se pinta, para que siga
 * siendo correcto al cambiar entre tema claro y oscuro.
 */
public class Etiqueta extends JLabel {

    private final Supplier<Color> color;

    public Etiqueta(String texto, Supplier<Color> color){
        super(texto);
        this.color = color;
    }

    @Override
    public Color getForeground(){
        return color != null ? color.get() : super.getForeground();
    }
}
