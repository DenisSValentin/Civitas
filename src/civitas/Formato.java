
package civitas;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formato común para las cantidades de dinero (diario y vista).
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public final class Formato {

    private static final NumberFormat NUMERO = NumberFormat.getNumberInstance(Locale.forLanguageTag("es-ES"));

    static {
        NUMERO.setMaximumFractionDigits(2);
        NUMERO.setGroupingUsed(true);
    }

    private Formato(){
    }

    public static String dinero(float cantidad){
        return NUMERO.format(cantidad) + " €";
    }
}
