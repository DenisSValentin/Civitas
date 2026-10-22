
package GUI;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.ColorFunctions;
import java.awt.Color;
import java.awt.Insets;
import java.util.Collections;
import javax.swing.UIManager;

/**
 * Colores de la interfaz y del tablero, en versión clara y oscura.
 * Los componentes piden los colores al pintarse, así que cambiar de tema
 * no obliga a reconstruir nada.
 */
public final class Tema {

    public record Losa(Color superior, Color izquierda, Color derecha) {}

    private static final Color[] JUGADORES = {
        new Color(0x2F74D0), new Color(0xE07B1F), new Color(0x13A08C), new Color(0x8458D8)
    };
    private static final Color[] JUGADORES_SOMBRA = {
        new Color(0x1B4F96), new Color(0x9E520C), new Color(0x0B6B5D), new Color(0x56339E)
    };

    private static boolean oscuro;


    private Tema(){
    }

    public static void instalar(boolean oscuro){
        Tema.oscuro = oscuro;
        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#2675BF"));

        if(oscuro)
            FlatMacDarkLaf.setup();
        else
            FlatMacLightLaf.setup();

        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.width", 10);
    }

    public static void cambiar(boolean oscuro){
        instalar(oscuro);
        FlatLaf.updateUI();
    }

    public static boolean esOscuro(){
        return oscuro;
    }

    // Escapa el texto y lo envuelve para que JLabel lo parta en varias líneas.
    // Se usa "pt" porque el HTML de Swing trata cada "px" como 1,3 píxeles.
    public static String html(String texto, int ancho){
        String seguro = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return "<html><div style='width:" + ancho + "pt'>" + seguro + "</div></html>";
    }

    public static String edificios(int casas, int hoteles){
        return casas + (casas == 1 ? " casa · " : " casas · ") + hoteles + (hoteles == 1 ? " hotel" : " hoteles");
    }


    // JUGADORES

    public static Color jugador(int indice){
        return JUGADORES[Math.floorMod(indice, JUGADORES.length)];
    }

    public static Color jugadorSombra(int indice){
        return JUGADORES_SOMBRA[Math.floorMod(indice, JUGADORES_SOMBRA.length)];
    }


    // INTERFAZ

    public static Color acento(){
        Color c = UIManager.getColor("Component.accentColor");
        return c != null ? c : new Color(0x2675BF);
    }

    public static Color texto(){
        return UIManager.getColor("Label.foreground");
    }

    public static Color textoSecundario(){
        return oscuro ? new Color(0xA3ADBB) : new Color(0x5B6370);
    }

    public static Color tarjeta(){
        return oscuro ? ColorFunctions.lighten(UIManager.getColor("Panel.background"), 0.05f) : Color.WHITE;
    }

    public static Color bordeTarjeta(){
        return oscuro ? ColorFunctions.lighten(UIManager.getColor("Panel.background"), 0.13f) : new Color(0xD1D5DB);
    }

    public static Color resaltado(){
        return oscuro ? new Color(0x26344A) : new Color(0xE8F1FA);
    }

    public static Color bordeResaltado(){
        return oscuro ? new Color(0x3D5A85) : new Color(0x9CC3E8);
    }

    public static Color positivo(){
        return oscuro ? new Color(0x4ADE80) : new Color(0x2E8B57);
    }

    public static Color negativo(){
        return oscuro ? new Color(0xF87171) : new Color(0xC0392B);
    }

    public static Color neutro(){
        return new Color(0x8A929C);
    }

    public static Color avisoFondo(){
        return oscuro ? new Color(0x3A3320) : new Color(0xFFF7E0);
    }

    public static Color avisoBorde(){
        return oscuro ? new Color(0x6E5A25) : new Color(0xF0D58A);
    }

    public static Color avisoTitulo(){
        return oscuro ? new Color(0xE5C463) : new Color(0x7A5A00);
    }

    public static Color insigniaFondo(){
        return oscuro ? new Color(0x4A3A17) : new Color(0xFCE9C5);
    }

    public static Color insigniaTexto(){
        return oscuro ? new Color(0xF4C76A) : new Color(0x7A4A00);
    }


    // TABLERO

    public static Color fondoTablero(){
        return oscuro ? new Color(0x1B1F26) : new Color(0xE3E7EC);
    }

    public static Losa losaCalle(){
        return oscuro ? new Losa(new Color(0x2E343E), new Color(0x454D5A), new Color(0x383F4A))
                      : new Losa(Color.WHITE, new Color(0xC7CDD5), new Color(0xAEB6C0));
    }

    public static Losa losaSorpresa(){
        return oscuro ? new Losa(new Color(0x3D3522), new Color(0x8A7230), new Color(0x6E5A25))
                      : new Losa(new Color(0xFFF4CF), new Color(0xE5C463), new Color(0xC9A548));
    }

    public static Losa losaSalida(){
        return oscuro ? new Losa(new Color(0x233A2C), new Color(0x3F7A52), new Color(0x336445))
                      : new Losa(new Color(0xE2F3E6), new Color(0x8CC79C), new Color(0x6BAA7E));
    }

    public static Losa losaNeutra(){
        return oscuro ? new Losa(new Color(0x292F39), new Color(0x3E4654), new Color(0x333A46))
                      : new Losa(new Color(0xEEF1F5), new Color(0xB9C0CB), new Color(0x9EA7B4));
    }

    public static Losa losaPlaza(){
        return oscuro ? new Losa(new Color(0x252B34), new Color(0x383F4B), new Color(0x2E343F))
                      : new Losa(new Color(0xF4F6F8), new Color(0xC3CAD3), new Color(0xAEB6C1));
    }

    public static Color sueloResaltado(){
        return oscuro ? new Color(0x33405A) : new Color(0xE4EEF9);
    }

    public static Color bordeLosa(){
        return oscuro ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 31);
    }

    public static Color textoTablero(){
        return oscuro ? new Color(0xE6E9EE) : new Color(0x1F2328);
    }

    public static Color haloTexto(){
        return oscuro ? new Color(0x2E343E) : Color.WHITE;
    }
}
