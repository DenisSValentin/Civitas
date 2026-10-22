
package GUI;

import civitas.Casilla;
import civitas.CasillaCalle;
import civitas.CivitasJuego;
import civitas.Formato;
import civitas.Jugador;
import controladorCivitas.Controlador;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Turno del jugador actual: qué puede hacer, los botones de cada operación,
 * la carta sorpresa que acaba de salir y la gestión de propiedades.
 */
public class PanelAcciones extends Tarjeta {

    private static final int ANCHO_TEXTO = 290;

    private final JLabel titulo = new JLabel();
    private final JLabel ayuda = new Etiqueta("", Tema::textoSecundario);
    private final AvisoSorpresa aviso = new AvisoSorpresa();
    private final JButton tirar = new JButton("Tirar dado");
    private final JButton comprar = new JButton("Comprar");
    private final JButton gestionar = new JButton("Gestionar");
    private final JButton pasar = new JButton("Pasar turno");
    private final JPanel listaGestion = new JPanel();

    private Controlador controlador;
    private CivitasJuego juego;
    private boolean animando;
    private boolean gestionando;


    public PanelAcciones(){
        super(new BorderLayout(0, 12));

        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 16f));
        titulo.setIconTextGap(8);
        JPanel cabecera = new JPanel(new BorderLayout(0, 4));
        cabecera.setOpaque(false);
        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(ayuda, BorderLayout.CENTER);

        configurar(tirar, KeyEvent.VK_T, "Tira el dado y mueve tu ficha (Intro)");
        configurar(comprar, KeyEvent.VK_C, "Compra la calle en la que estás");
        configurar(gestionar, KeyEvent.VK_G, "Construye casas y hoteles o haz social una calle");
        configurar(pasar, KeyEvent.VK_P, "Termina tu turno");

        tirar.addActionListener(e -> controlador.tirarDado());
        comprar.addActionListener(e -> controlador.comprar());
        gestionar.addActionListener(e -> {
            gestionando = !gestionando;
            actualiza(juego, animando);
        });
        pasar.addActionListener(e -> {
            gestionando = false;
            controlador.pasarTurno();
        });

        JPanel botones = new JPanel(new GridLayout(2, 2, 8, 8));
        botones.setOpaque(false);
        botones.add(tirar);
        botones.add(comprar);
        botones.add(gestionar);
        botones.add(pasar);

        listaGestion.setOpaque(false);
        listaGestion.setLayout(new BoxLayout(listaGestion, BoxLayout.Y_AXIS));

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        for(JComponent c : new JComponent[]{aviso, botones, listaGestion})
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        cuerpo.add(aviso);
        cuerpo.add(botones);
        cuerpo.add(listaGestion);
        aviso.setVisible(false);

        add(cabecera, BorderLayout.NORTH);
        add(cuerpo, BorderLayout.CENTER);
    }

    private void configurar(JButton boton, int atajo, String ayuda){
        boton.setMnemonic(atajo);
        boton.setToolTipText(ayuda);
        boton.setMargin(new Insets(9, 12, 9, 12));
        boton.setFocusable(true);
    }

    public void setControlador(Controlador controlador){
        this.controlador = controlador;
    }

    public JButton getBotonTirar(){
        return tirar;
    }

    public void reiniciar(){
        gestionando = false;
        aviso.setVisible(false);
    }

    public void actualiza(CivitasJuego juego, boolean animando){
        this.juego = juego;
        this.animando = animando;
        Jugador actual = juego.getJugadorActual();

        titulo.setText("Turno de " + actual.getNombre());
        titulo.setIcon(new CirculoIcon(Tema.jugador(juego.getIndiceJugadorActual()), 12));
        ayuda.setText(Tema.html(textoAyuda(), ANCHO_TEXTO));

        // Al empezar un turno nuevo deja de tener sentido la carta del turno anterior
        if(juego.puedeAvanzar())
            aviso.setVisible(false);

        tirar.setEnabled(!animando && juego.puedeAvanzar());

        boolean puedeComprar = !animando && juego.puedeComprar();
        comprar.setEnabled(puedeComprar);
        comprar.setText(puedeComprar ? "Comprar · " + Formato.dinero(precioCompraActual()) : "Comprar");

        boolean puedeGestionar = !animando && juego.puedeGestionar();
        if(!puedeGestionar)
            gestionando = false;
        gestionar.setEnabled(puedeGestionar);
        gestionar.setText(gestionando ? "Cerrar gestión" : "Gestionar");

        pasar.setEnabled(!animando && juego.puedePasarTurno());

        reconstruirGestion();
        revalidate();
        repaint();
    }

    public void mostrarSorpresa(String texto){
        aviso.mostrar(texto);
        revalidate();
    }

    private float precioCompraActual(){
        Jugador actual = juego.getJugadorActual();
        Casilla casilla = juego.getTablero().getCasilla(actual.getCasillaActual());
        float precio = ((CasillaCalle) casilla).getPrecioCompra();
        return actual.esEspeculador() ? precio / 2 : precio;
    }

    private String textoAyuda(){
        if(juego.finalDelJuego())
            return "La partida ha terminado.";
        if(animando)
            return "Moviendo la ficha…";
        if(juego.puedeAvanzar())
            return "Tira el dado para mover tu ficha.";

        Casilla casilla = juego.getTablero().getCasilla(juego.getJugadorActual().getCasillaActual());
        if(juego.puedeComprar())
            return casilla.getNombre() + " está libre: puedes comprarla, gestionar tus propiedades o pasar el turno.";
        if(juego.puedeGestionar())
            return "Puedes construir en tus propiedades o pasar el turno.";
        return "Pasa el turno al siguiente jugador.";
    }

    private void reconstruirGestion(){
        listaGestion.removeAll();
        listaGestion.setVisible(gestionando);

        if(gestionando){
            Jugador jugador = juego.getJugadorActual();
            List<CasillaCalle> propiedades = jugador.getPropiedades();
            for(int i = 0; i < propiedades.size(); i++)
                listaGestion.add(filaPropiedad(i, propiedades.get(i), jugador));
        }
    }

    private JComponent filaPropiedad(int ip, CasillaCalle calle, Jugador jugador){
        JLabel nombre = new JLabel(calle.getNombre());
        nombre.setFont(nombre.getFont().deriveFont(Font.BOLD));

        String detalle = Tema.edificios(calle.getNumCasas(), calle.getNumHoteles()) + " · alquiler "
                + Formato.dinero(calle.getPrecioAlquilerCompleto()) + (calle.soySocial() ? " · social" : "");
        JLabel textoDetalle = new Etiqueta(detalle, Tema::textoSecundario);
        textoDetalle.setFont(textoDetalle.getFont().deriveFont(11f));

        float coste = jugador.esEspeculador() ? calle.getPrecioEdificar() / 2 : calle.getPrecioEdificar();

        JButton casa = botonPequeno("+ Casa " + Formato.dinero(coste), juego.puedeConstruirCasa(ip),
                "Máximo " + jugador.getCasasMax() + " casas por calle");
        JButton hotel = botonPequeno("+ Hotel", juego.puedeConstruirHotel(ip),
                "Necesitas " + jugador.getCasasPorHotel() + " casas para construir un hotel; cuesta "
                + Formato.dinero(coste));
        JButton social = botonPequeno("Social", juego.puedeHacerCalleSocial(ip),
                "Convierte la calle en social: cobra un 2 % menos de alquiler");
        casa.addActionListener(e -> controlador.construirCasa(ip));
        hotel.addActionListener(e -> controlador.construirHotel(ip));
        social.addActionListener(e -> controlador.hacerCalleSocial(ip));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        botones.setOpaque(false);
        botones.add(casa);
        botones.add(hotel);
        botones.add(social);

        JPanel info = new JPanel(new BorderLayout(0, 1));
        info.setOpaque(false);
        info.add(nombre, BorderLayout.NORTH);
        info.add(textoDetalle, BorderLayout.CENTER);

        JPanel fila = new JPanel(new BorderLayout(0, 6)){
            @Override
            public java.awt.Dimension getMaximumSize(){
                return new java.awt.Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Tema.bordeTarjeta()),
                BorderFactory.createEmptyBorder(10, 0, 10, 0)));
        fila.add(info, BorderLayout.NORTH);
        fila.add(botones, BorderLayout.CENTER);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        return fila;
    }

    private JButton botonPequeno(String texto, boolean activo, String ayuda){
        JButton boton = new JButton(texto);
        boton.setFont(boton.getFont().deriveFont(12f));
        boton.setMargin(new Insets(4, 8, 4, 8));
        boton.setEnabled(!animando && activo);
        boton.setToolTipText(ayuda);
        return boton;
    }


    /** Carta sorpresa que acaba de salir, sin bloquear la partida con un diálogo. */
    private static class AvisoSorpresa extends Tarjeta {

        private final JLabel texto = new JLabel();
        private final JLabel efecto = new JLabel();
        private Color colorEfecto = Tema.texto();

        AvisoSorpresa(){
            super(new BorderLayout(10, 0));
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

            JLabel etiqueta = new Etiqueta("SORPRESA", Tema::avisoTitulo);
            etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 11f));
            efecto.setFont(efecto.getFont().deriveFont(Font.BOLD));

            JPanel textos = new JPanel();
            textos.setOpaque(false);
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            textos.add(etiqueta);
            textos.add(Box.createVerticalStrut(3));
            textos.add(texto);
            textos.add(Box.createVerticalStrut(4));
            textos.add(efecto);

            JButton vale = new JButton("Vale");
            vale.setMargin(new Insets(3, 10, 3, 10));
            vale.addActionListener(e -> setVisible(false));
            JPanel derecha = new JPanel(new BorderLayout());
            derecha.setOpaque(false);
            derecha.add(vale, BorderLayout.NORTH);

            add(textos, BorderLayout.CENTER);
            add(derecha, BorderLayout.EAST);
        }

        void mostrar(String carta){
            // Las cartas tienen la forma "Descripción -> EFECTO"
            String[] partes = carta.split("\\s*->\\s*", 2);
            texto.setText(Tema.html(partes[0], ANCHO_TEXTO - 90));

            if(partes.length > 1){
                String e = partes[1].trim();
                efecto.setText(Tema.html(e.substring(0, 1) + e.substring(1).toLowerCase(), ANCHO_TEXTO - 90));
                efecto.setVisible(true);
                if(e.startsWith("RECIBES"))
                    colorEfecto = Tema.positivo();
                else if(e.startsWith("PAGAS"))
                    colorEfecto = Tema.negativo();
                else
                    colorEfecto = Tema.acento();
                efecto.setForeground(colorEfecto);
            }
            else
                efecto.setVisible(false);

            setVisible(true);
        }

        @Override
        protected Color getFondo(){
            return Tema.avisoFondo();
        }

        @Override
        protected Color getBorde(){
            return Tema.avisoBorde();
        }
    }
}
