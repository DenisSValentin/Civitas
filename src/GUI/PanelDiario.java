
package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

/**
 * Diario de la partida: lo último que ha pasado, arriba del todo.
 */
public class PanelDiario extends Tarjeta {

    private static final int MAXIMO = 200;

    private final DefaultListModel<String> eventos = new DefaultListModel<>();
    private final JList<String> lista = new JList<>(eventos);

    public PanelDiario(){
        super(new BorderLayout(0, 8));

        lista.setCellRenderer(new Renderizador());
        lista.setOpaque(false);
        lista.setFocusable(false);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(0, 220));

        add(titulo("Diario"), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void agregar(List<String> nuevos){
        for(String e : nuevos)
            eventos.add(0, e);
        while(eventos.size() > MAXIMO)
            eventos.remove(eventos.size() - 1);
        lista.ensureIndexIsVisible(0);
    }

    public void limpiar(){
        eventos.clear();
    }

    private static Color tono(String evento){
        if(evento.contains(" paga ") || evento.contains("PAGAS") || evento.contains("no tiene saldo"))
            return Tema.negativo();
        if(evento.contains(" cobra ") || evento.contains(" compra ") || evento.contains(" construye ")
                || evento.contains("RECIBES") || evento.contains("calle social"))
            return Tema.positivo();
        if(evento.startsWith("Turno de") || evento.startsWith("Empieza"))
            return Tema.acento();
        return Tema.neutro();
    }

    private static class Renderizador extends JLabel implements ListCellRenderer<String> {

        Renderizador(){
            setIconTextGap(8);
            setVerticalAlignment(TOP);
            setVerticalTextPosition(TOP);
            setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> lista, String evento,
                int indice, boolean seleccionado, boolean foco){
            setIcon(new CirculoIcon(tono(evento), 8));
            setText(Tema.html(evento, 270));
            setForeground(Tema.texto());
            setFont(lista.getFont().deriveFont(12f));
            return this;
        }
    }
}
