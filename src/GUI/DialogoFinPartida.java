
package GUI;

import civitas.CivitasJuego;
import civitas.Formato;
import civitas.Jugador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Ranking final cuando un jugador entra en bancarrota.
 */
public class DialogoFinPartida extends JDialog {

    private boolean nuevaPartida;

    private DialogoFinPartida(Window propietario, CivitasJuego juego, List<Jugador> ranking){
        super(propietario, "Fin de la partida", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel titulo = new JLabel("Fin de la partida");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));

        String arruinados = ranking.stream().filter(j -> j.getSaldo() <= 0)
                .map(Jugador::getNombre).collect(Collectors.joining(" y "));
        JLabel motivo = new Etiqueta(arruinados + " ha entrado en bancarrota.", Tema::textoSecundario);

        JPanel filas = new JPanel(new GridLayout(0, 1, 0, 4));
        for(int i = 0; i < ranking.size(); i++){
            Jugador j = ranking.get(i);
            JLabel puesto = new Etiqueta((i + 1) + "º", i == 0 ? Tema::acento : Tema::textoSecundario);
            puesto.setFont(puesto.getFont().deriveFont(Font.BOLD, 15f));

            JLabel nombre = new JLabel(j.getNombre(), new CirculoIcon(Tema.jugador(juego.getJugadores().indexOf(j)), 12), JLabel.LEFT);
            nombre.setIconTextGap(8);
            nombre.setFont(nombre.getFont().deriveFont(i == 0 ? Font.BOLD : Font.PLAIN, 14f));

            JLabel saldo = new Etiqueta(Formato.dinero(j.getSaldo()), j.getSaldo() <= 0 ? Tema::negativo : Tema::texto);
            saldo.setFont(saldo.getFont().deriveFont(Font.BOLD, 14f));

            JPanel fila = new JPanel(new BorderLayout(12, 0));
            fila.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
            fila.add(puesto, BorderLayout.WEST);
            fila.add(nombre, BorderLayout.CENTER);
            fila.add(saldo, BorderLayout.EAST);
            filas.add(fila);
        }

        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JButton otra = new JButton("Nueva partida");
        otra.addActionListener(e -> {
            nuevaPartida = true;
            dispose();
        });
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.add(cerrar);
        botones.add(otra);

        JPanel cabecera = new JPanel(new BorderLayout(0, 4));
        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(motivo, BorderLayout.CENTER);

        JPanel contenido = new JPanel(new BorderLayout(0, 16));
        contenido.setBorder(BorderFactory.createEmptyBorder(22, 26, 18, 26));
        contenido.add(cabecera, BorderLayout.NORTH);
        contenido.add(filas, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);
        setContentPane(contenido);

        getRootPane().setDefaultButton(otra);
        pack();
        setMinimumSize(new java.awt.Dimension(360, getHeight()));
        setResizable(false);
        setLocationRelativeTo(propietario);
    }

    /** Devuelve true si el usuario quiere empezar otra partida. */
    public static boolean mostrar(Window propietario, CivitasJuego juego, List<Jugador> ranking){
        DialogoFinPartida dialogo = new DialogoFinPartida(propietario, juego, ranking);
        dialogo.setVisible(true);
        return dialogo.nuevaPartida;
    }
}
