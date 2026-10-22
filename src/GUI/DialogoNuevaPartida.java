
package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Pide los nombres de 2 a 4 jugadores antes de empezar una partida.
 */
public class DialogoNuevaPartida extends JDialog {

    public record Resultado(ArrayList<String> nombres, boolean debug) {}

    private static final int MAX_JUGADORES = 4;
    private static final int MIN_JUGADORES = 2;

    private final JTextField[] campos = new JTextField[MAX_JUGADORES];
    private final JCheckBox debug = new JCheckBox("Modo debug: el dado siempre saca 1");
    private final JButton comenzar = new JButton("Comenzar");
    private final JLabel aviso = new Etiqueta(" ", Tema::textoSecundario);
    private Resultado resultado;


    private DialogoNuevaPartida(Window propietario){
        super(propietario, "Nueva partida", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel logo = new Etiqueta("CIVITAS", Tema::acento);
        logo.setFont(logo.getFont().deriveFont(Font.BOLD, 30f).deriveFont(Map.of(TextAttribute.TRACKING, 0.15f)));
        JLabel subtitulo = new Etiqueta("Escribe los nombres de " + MIN_JUGADORES + " a " + MAX_JUGADORES
                + " jugadores.", Tema::textoSecundario);

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 0, 4, 10);

        DocumentListener alEscribir = new DocumentListener(){
            @Override public void insertUpdate(DocumentEvent e){ actualizarBoton(); }
            @Override public void removeUpdate(DocumentEvent e){ actualizarBoton(); }
            @Override public void changedUpdate(DocumentEvent e){ actualizarBoton(); }
        };

        for(int i = 0; i < MAX_JUGADORES; i++){
            campos[i] = new JTextField(20);
            campos[i].putClientProperty("JTextField.placeholderText",
                    "Jugador " + (i + 1) + (i >= MIN_JUGADORES ? " (opcional)" : ""));
            campos[i].getDocument().addDocumentListener(alEscribir);
            campos[i].getAccessibleContext().setAccessibleName("Nombre del jugador " + (i + 1));

            gc.gridy = i;
            gc.gridx = 0;
            gc.weightx = 0;
            gc.fill = GridBagConstraints.NONE;
            formulario.add(new JLabel(new CirculoIcon(Tema.jugador(i), 14)), gc);
            gc.gridx = 1;
            gc.weightx = 1;
            gc.fill = GridBagConstraints.HORIZONTAL;
            formulario.add(campos[i], gc);
        }

        JButton salir = new JButton("Salir");
        salir.addActionListener(e -> dispose());
        comenzar.addActionListener(e -> {
            resultado = new Resultado(nombres(), debug.isSelected());
            dispose();
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.add(salir);
        botones.add(comenzar);

        JPanel pie = new JPanel(new BorderLayout(0, 14));
        pie.add(debug, BorderLayout.NORTH);
        pie.add(aviso, BorderLayout.CENTER);
        pie.add(botones, BorderLayout.SOUTH);

        JPanel cabecera = new JPanel(new BorderLayout(0, 2));
        cabecera.add(logo, BorderLayout.NORTH);
        cabecera.add(subtitulo, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        contenido.add(cabecera, BorderLayout.NORTH);
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(pie, BorderLayout.SOUTH);
        setContentPane(contenido);

        getRootPane().setDefaultButton(comenzar);
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        actualizarBoton();
        pack();
        setResizable(false);
        setLocationRelativeTo(propietario);
    }

    private ArrayList<String> nombres(){
        ArrayList<String> nombres = new ArrayList<>();
        for(JTextField campo : campos){
            String nombre = campo.getText().trim();
            if(!nombre.isEmpty())
                nombres.add(nombre);
        }
        return nombres;
    }

    private void actualizarBoton(){
        int n = nombres().size();
        comenzar.setEnabled(n >= MIN_JUGADORES);
        aviso.setText(n >= MIN_JUGADORES ? "Jugarán " + n + " jugadores." : "Faltan " + (MIN_JUGADORES - n)
                + (MIN_JUGADORES - n == 1 ? " nombre" : " nombres") + " para poder empezar.");
    }

    /** Muestra el diálogo y devuelve los datos de la partida, o null si se cancela. */
    public static Resultado pedir(Window propietario){
        DialogoNuevaPartida dialogo = new DialogoNuevaPartida(propietario);
        dialogo.setVisible(true);
        return dialogo.resultado;
    }
}
