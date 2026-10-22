
package GUI;

import civitas.Casilla;
import civitas.CasillaCalle;
import civitas.CasillaSorpresa;
import civitas.CivitasJuego;
import civitas.Dado;
import civitas.Formato;
import civitas.Jugador;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.IntConsumer;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

/**
 * Tablero en perspectiva isométrica dibujado con Graphics2D.
 *
 * Las casillas se colocan en el perímetro de una cuadrícula de lado x lado y se
 * proyectan con P(u, v) = ((u - v) * 75, (u + v) * 37.5). Cada casilla es una
 * losa con grosor; las casas, los hoteles, el dado y las fichas se dibujan con
 * polígonos de atrás hacia delante (algoritmo del pintor).
 */
public class TableroIsometrico extends JPanel {

    private static final double MEDIO_ANCHO = 75;
    private static final double MEDIO_ALTO = 37.5;
    private static final double GROSOR = 14;
    private static final double MARGEN_CASILLA = 0.05;

    private static final int MS_DADO = 750;
    private static final int MS_CARA = 75;
    private static final int MS_SALTO = 290;
    private static final int MS_PAUSA = 250;

    // Dónde se coloca cada ficha y cada edificio respecto al centro de la casilla
    // Las cuatro fichas van en fila en la mitad delantera de la losa, sin salirse de ella
    private static final double[][] POSICION_FICHA = {{-21, 18}, {-7, 18}, {7, 18}, {21, 18}};
    private static final double TAMANO_PEON = 0.75;
    private static final double[][] HUECOS_EDIFICIO = {{0, -25}, {-21, -18}, {21, -18}, {-37, -10}, {37, -10}};

    // Puntos del dado en una cuadrícula 3x3 (0..8) para cada cara
    private static final int[][] CARAS = {
        {}, {4}, {0, 8}, {0, 4, 8}, {0, 2, 6, 8}, {0, 2, 4, 6, 8}, {0, 2, 3, 5, 6, 8}
    };

    private static final Color CASA_IZQ = new Color(0xF4EFE6);
    private static final Color CASA_DER = new Color(0xD8D0C0);
    private static final Color TEJADO_IZQ = new Color(0x2E8B57);
    private static final Color TEJADO_DER = new Color(0x21683F);
    private static final Color HOTEL_IZQ = new Color(0xD2453A);
    private static final Color HOTEL_DER = new Color(0xA5322A);
    private static final Color HOTEL_TECHO = new Color(0xE8766C);
    private static final Color ARISTA = new Color(0, 0, 0, 46);
    private static final Color DADO_SUPERIOR = Color.WHITE;
    private static final Color DADO_IZQ = new Color(0xE1E5EA);
    private static final Color DADO_DER = new Color(0xC6CCD4);
    private static final Color DADO_ARISTA = new Color(0x9CA5B0);
    private static final Color DADO_PUNTO = new Color(0x1F2328);

    private CivitasJuego juego;
    private int lado = 6;
    private Path2D.Double[] suelos = new Path2D.Double[0];
    private Integer[] ordenPintado = new Integer[0];
    private AffineTransform vista = new AffineTransform();
    private String mensaje = "";

    private int hover = -1;
    private IntConsumer alCambiarHover = i -> {};

    // Animación de la tirada
    private final Timer reloj = new Timer(40, e -> tick());
    private final Random random = new Random();
    private int animJugador = -1;
    private int animDesde;
    private int animPasos;
    private long animInicio;
    private long ultimaCara;
    private Runnable alTerminar;
    private int caraDado = 1;
    private boolean dadoRodando;


    public TableroIsometrico(){
        setOpaque(false);
        setPreferredSize(new Dimension(900, 600));

        MouseAdapter raton = new MouseAdapter(){
            @Override
            public void mouseMoved(MouseEvent e){
                actualizarHover(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e){
                cambiarHover(-1);
            }
        };
        addMouseListener(raton);
        addMouseMotionListener(raton);
        getAccessibleContext().setAccessibleName("Tablero de Civitas");
    }

    @Override
    public void addNotify(){
        super.addNotify();
        reloj.start();
    }

    @Override
    public void removeNotify(){
        reloj.stop();
        super.removeNotify();
    }

    public void setCivitasJuego(CivitasJuego juego){
        this.juego = juego;
        int n = juego.getTablero().getCasillas().size();
        lado = n / 4 + 1;
        suelos = new Path2D.Double[n];
        ordenPintado = new Integer[n];

        for(int i = 0; i < n; i++){
            int[] fc = filaColumna(i);
            double f = fc[0], c = fc[1], m = MARGEN_CASILLA;
            suelos[i] = poligono(P(c + m, f + m), P(c + 1 - m, f + m), P(c + 1 - m, f + 1 - m), P(c + m, f + 1 - m));
            ordenPintado[i] = i;
        }
        Arrays.sort(ordenPintado, Comparator.comparingDouble(i -> {
            int[] fc = filaColumna(i);
            return fc[0] + fc[1] + fc[1] * 0.01;
        }));

        animJugador = -1;
        alTerminar = null;
        dadoRodando = false;
        caraDado = Math.max(1, Dado.getInstance().getUltimoResultado());
        hover = -1;
        repaint();
    }

    public void setMensaje(String mensaje){
        this.mensaje = mensaje;
        repaint();
    }

    public void setAlCambiarHover(IntConsumer alCambiarHover){
        this.alCambiarHover = alCambiarHover;
    }

    public int getHover(){
        return hover;
    }

    public boolean isAnimando(){
        return animJugador >= 0;
    }

    /**
     * Hace rodar el dado y después mueve la ficha casilla a casilla, saltando.
     * Cuando termina llama a alTerminar.
     */
    public void animarTirada(int jugador, int desde, int tirada, Runnable alTerminar){
        this.animJugador = jugador;
        this.animDesde = desde;
        this.animPasos = tirada;
        this.alTerminar = alTerminar;
        animInicio = System.currentTimeMillis();
        ultimaCara = 0;
        dadoRodando = true;
        reloj.setDelay(16);
    }

    private void tick(){
        if(animJugador >= 0){
            long ahora = System.currentTimeMillis();
            long t = ahora - animInicio;

            if(t < MS_DADO){
                if(ahora - ultimaCara >= MS_CARA){
                    caraDado = 1 + random.nextInt(6);
                    ultimaCara = ahora;
                }
            }
            else {
                dadoRodando = false;
                caraDado = animPasos;

                if(t >= MS_DADO + (long) animPasos * MS_SALTO + MS_PAUSA){
                    animJugador = -1;
                    reloj.setDelay(40);
                    Runnable fin = alTerminar;
                    alTerminar = null;
                    if(fin != null)
                        fin.run();
                }
            }
        }
        repaint();
    }


    // GEOMETRÍA

    private static Point2D.Double P(double u, double v){
        return new Point2D.Double((u - v) * MEDIO_ANCHO, (u + v) * MEDIO_ALTO);
    }

    private static Point2D.Double pt(double x, double y){
        return new Point2D.Double(x, y);
    }

    private static Point2D.Double abajo(Point2D p, double d){
        return new Point2D.Double(p.getX(), p.getY() + d);
    }

    private static Path2D.Double poligono(Point2D... puntos){
        Path2D.Double p = new Path2D.Double();
        p.moveTo(puntos[0].getX(), puntos[0].getY());
        for(int i = 1; i < puntos.length; i++)
            p.lineTo(puntos[i].getX(), puntos[i].getY());
        p.closePath();
        return p;
    }

    // Recorre el perímetro: la Salida en la esquina delantera y después en el sentido de las agujas del reloj
    private int[] filaColumna(int i){
        int k = lado - 1;
        if(i == 0)
            return new int[]{k, k};
        if(i <= k)
            return new int[]{k, k - i};
        if(i <= 2 * k)
            return new int[]{k - (i - k), 0};
        if(i <= 3 * k)
            return new int[]{0, i - 2 * k};
        return new int[]{i - 3 * k, k};
    }

    private Point2D centro(int i){
        int[] fc = filaColumna(i);
        return P(fc[1] + 0.5, fc[0] + 0.5);
    }


    // PINTADO

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g2.setColor(Tema.fondoTablero());
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));

        if(juego == null){
            g2.dispose();
            return;
        }

        double ancho = 2 * lado * MEDIO_ANCHO + 50;
        double alto = 2 * lado * MEDIO_ALTO + GROSOR + 120;
        double escala = Math.min(getWidth() / ancho, getHeight() / alto);

        vista = new AffineTransform();
        vista.translate((getWidth() - ancho * escala) / 2, (getHeight() - alto * escala) / 2);
        vista.scale(escala, escala);
        vista.translate(ancho / 2, 80);
        g2.transform(vista);

        Font base = UIManager.getFont("Label.font");
        Font fuenteNombre = base.deriveFont(Font.BOLD, 13f);
        Font fuenteDetalle = base.deriveFont(Font.PLAIN, 11f);

        dibujarPlaza(g2, base);
        for(int i : ordenPintado)
            dibujarCasilla(g2, i, fuenteNombre, fuenteDetalle);
        dibujarDado(g2);
        dibujarFichas(g2);

        g2.dispose();
    }

    private void cara(Graphics2D g, Color relleno, Color arista, float grosor, Point2D... puntos){
        Path2D p = poligono(puntos);
        g.setColor(relleno);
        g.fill(p);
        if(arista != null){
            g.setColor(arista);
            g.setStroke(new BasicStroke(grosor, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(p);
        }
    }

    // a = vértice de arriba, b = derecha, c = abajo, d = izquierda
    private void losa(Graphics2D g, Point2D a, Point2D b, Point2D c, Point2D d, double grosor,
            Color izquierda, Color derecha, Color superior){
        cara(g, izquierda, null, 0, d, c, abajo(c, grosor), abajo(d, grosor));
        cara(g, derecha, null, 0, c, b, abajo(b, grosor), abajo(c, grosor));
        cara(g, superior, null, 0, a, b, c, d);
    }

    private void dibujarPlaza(Graphics2D g, Font base){
        double i0 = 1.12, i1 = lado - 1.12;
        Tema.Losa plaza = Tema.losaPlaza();
        Point2D a = P(i0, i0), b = P(i1, i0), c = P(i1, i1), d = P(i0, i1);
        losa(g, a, b, c, d, 10, plaza.izquierda(), plaza.derecha(), plaza.superior());
        g.setColor(Tema.bordeLosa());
        g.setStroke(new BasicStroke(1f));
        g.draw(poligono(a, b, c, d));

        double mitad = lado / 2.0;
        Font logo = base.deriveFont(Font.BOLD, 40f).deriveFont(Map.of(TextAttribute.TRACKING, 0.2f));
        textoSobreElSuelo(g, "CIVITAS", logo, P(mitad, mitad - 0.65), Tema.acento());
        textoSobreElSuelo(g, mensaje, base.deriveFont(Font.PLAIN, 15f), P(mitad, mitad - 0.28), Tema.textoSecundario());
    }

    // Texto "pintado" en el suelo: se proyecta con la misma transformación que el tablero
    private void textoSobreElSuelo(Graphics2D g, String texto, Font fuente, Point2D en, Color color){
        if(texto == null || texto.isEmpty())
            return;
        AffineTransform previa = g.getTransform();
        g.transform(new AffineTransform(0.894, 0.447, -0.894, 0.447, en.getX(), en.getY()));
        TextLayout tl = new TextLayout(texto, fuente, g.getFontRenderContext());
        g.setColor(color);
        tl.draw(g, -tl.getAdvance() / 2, 0f);
        g.setTransform(previa);
    }

    // Texto horizontal con un halo para que se lea encima de edificios y fichas
    private void textoConHalo(Graphics2D g, String texto, Font fuente, double cx, double y, Color color){
        if(texto.isEmpty())
            return;
        TextLayout tl = new TextLayout(texto, fuente, g.getFontRenderContext());
        Shape forma = tl.getOutline(AffineTransform.getTranslateInstance(cx - tl.getAdvance() / 2, y));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Tema.haloTexto());
        g.draw(forma);
        g.setColor(color);
        g.fill(forma);
    }

    private void dibujarCasilla(Graphics2D g, int i, Font fuenteNombre, Font fuenteDetalle){
        Casilla casilla = juego.getTablero().getCasilla(i);
        int[] fc = filaColumna(i);
        double f = fc[0], c = fc[1], m = MARGEN_CASILLA;
        Point2D a = P(c + m, f + m), b = P(c + 1 - m, f + m), cc = P(c + 1 - m, f + 1 - m), d = P(c + m, f + 1 - m);

        Tema.Losa estilo;
        if(casilla instanceof CasillaCalle)
            estilo = Tema.losaCalle();
        else if(casilla instanceof CasillaSorpresa)
            estilo = Tema.losaSorpresa();
        else if(i == 0)
            estilo = Tema.losaSalida();
        else
            estilo = Tema.losaNeutra();

        Color izquierda = estilo.izquierda();
        Color derecha = estilo.derecha();
        Color colorDetalle = Tema.textoSecundario();
        String detalle;

        if(casilla instanceof CasillaCalle calle){
            Jugador dueno = calle.getPropietario();
            if(dueno != null){
                // Los laterales de la losa se pintan del color del dueño
                int indice = juego.getJugadores().indexOf(dueno);
                izquierda = Tema.jugador(indice);
                derecha = Tema.jugadorSombra(indice);
                colorDetalle = Tema.esOscuro() ? Tema.jugador(indice) : Tema.jugadorSombra(indice);
                detalle = Formato.dinero(calle.getPrecioAlquilerCompleto());
            }
            else
                detalle = Formato.dinero(calle.getPrecioCompra());

            if(calle.soySocial())
                detalle += " · social";
        }
        else if(casilla instanceof CasillaSorpresa)
            detalle = "Sorpresa";
        else if(i == 0)
            detalle = "+" + Formato.dinero(1000);
        else
            detalle = "Descanso";

        Color superior = (i == hover) ? Tema.sueloResaltado() : estilo.superior();
        losa(g, a, b, cc, d, GROSOR, izquierda, derecha, superior);

        boolean casillaActual = !isAnimando() && juego.getJugadorActual().getCasillaActual() == i;
        if(casillaActual){
            g.setColor(Tema.acento());
            g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
        else if(i == hover){
            g.setColor(Tema.acento());
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        }
        else {
            g.setColor(Tema.bordeLosa());
            g.setStroke(new BasicStroke(1f));
        }
        g.draw(suelos[i]);

        Point2D centro = centro(i);
        if(casilla instanceof CasillaCalle calle)
            dibujarEdificios(g, centro, calle, fuenteDetalle);

        textoConHalo(g, casilla.getNombre(), fuenteNombre, centro.getX(), centro.getY() - 5, Tema.textoTablero());
        textoConHalo(g, detalle, fuenteDetalle, centro.getX(), centro.getY() + 7, colorDetalle);
    }

    private void dibujarEdificios(Graphics2D g, Point2D centro, CasillaCalle calle, Font fuente){
        List<double[]> edificios = new ArrayList<>();   // {x, y, esHotel}
        int total = calle.getNumHoteles() + calle.getNumCasas();

        for(int k = 0; k < total && k < HUECOS_EDIFICIO.length; k++){
            boolean hotel = k < calle.getNumHoteles();
            edificios.add(new double[]{
                centro.getX() + HUECOS_EDIFICIO[k][0], centro.getY() + HUECOS_EDIFICIO[k][1], hotel ? 1 : 0
            });
        }
        edificios.sort(Comparator.comparingDouble(e -> e[1]));

        for(double[] e : edificios){
            if(e[2] == 1)
                dibujarHotel(g, e[0], e[1]);
            else
                dibujarCasa(g, e[0], e[1]);
        }

        if(total > HUECOS_EDIFICIO.length)
            textoConHalo(g, "+" + (total - HUECOS_EDIFICIO.length), fuente,
                    centro.getX() + 52, centro.getY() - 10, Tema.textoTablero());
    }

    private void dibujarCasa(Graphics2D g, double x, double y){
        double a = 8, b = 4, h = 9, tejado = 8;
        Point2D izq = pt(x - a, y), frente = pt(x, y + b), der = pt(x + a, y);
        Point2D izqA = pt(x - a, y - h), frenteA = pt(x, y + b - h), derA = pt(x + a, y - h);
        Point2D cima = pt(x, y - h - tejado);

        cara(g, CASA_IZQ, ARISTA, 0.6f, izq, frente, frenteA, izqA);
        cara(g, CASA_DER, ARISTA, 0.6f, frente, der, derA, frenteA);
        cara(g, TEJADO_IZQ, ARISTA, 0.6f, izqA, frenteA, cima);
        cara(g, TEJADO_DER, ARISTA, 0.6f, frenteA, derA, cima);
    }

    private void dibujarHotel(Graphics2D g, double x, double y){
        double a = 13, b = 6.5, h = 24;
        Point2D izq = pt(x - a, y), frente = pt(x, y + b), der = pt(x + a, y), fondo = pt(x, y - b);

        cara(g, HOTEL_IZQ, ARISTA, 0.6f, izq, frente, pt(x, y + b - h), pt(x - a, y - h));
        cara(g, HOTEL_DER, ARISTA, 0.6f, frente, der, pt(x + a, y - h), pt(x, y + b - h));
        cara(g, HOTEL_TECHO, ARISTA, 0.6f,
                pt(fondo.getX(), fondo.getY() - h), pt(der.getX(), der.getY() - h),
                pt(frente.getX(), frente.getY() - h), pt(izq.getX(), izq.getY() - h));
    }

    private void dibujarDado(Graphics2D g){
        double mitad = lado / 2.0;
        Point2D base = P(mitad + 0.3, mitad + 0.7);
        double bote = dadoRodando ? -16 * Math.abs(Math.sin(System.currentTimeMillis() / 95.0)) : 0;
        double x = base.getX(), y = base.getY() + bote;
        double a = 26, b = 13, h = 26;

        // La sombra se queda en el suelo mientras el dado bota
        g.setColor(new Color(0, 0, 0, 36));
        g.fill(new Ellipse2D.Double(x - 30, base.getY() - 11, 60, 26));

        Point2D arriba = pt(x, y - b - h), der = pt(x + a, y - h), frente = pt(x, y + b - h), izq = pt(x - a, y - h);
        cara(g, DADO_IZQ, DADO_ARISTA, 1f, izq, frente, pt(x, y + b), pt(x - a, y));
        cara(g, DADO_DER, DADO_ARISTA, 1f, frente, der, pt(x + a, y), pt(x, y + b));
        cara(g, DADO_SUPERIOR, DADO_ARISTA, 1f, arriba, der, frente, izq);

        // Los puntos se dibujan en un cuadrado unidad proyectado sobre la cara de arriba
        AffineTransform previa = g.getTransform();
        g.transform(new AffineTransform(a, b, -a, b, arriba.getX(), arriba.getY()));
        g.setColor(DADO_PUNTO);
        for(int k : CARAS[caraDado]){
            double s = 0.22 + 0.28 * (k % 3);
            double t = 0.22 + 0.28 * (k / 3);
            g.fill(new Ellipse2D.Double(s - 0.085, t - 0.085, 0.17, 0.17));
        }
        g.setTransform(previa);
    }

    private record Ficha(int jugador, double x, double y, double salto) {}

    private void dibujarFichas(Graphics2D g){
        List<Jugador> jugadores = juego.getJugadores();
        long ahora = System.currentTimeMillis();
        List<Ficha> fichas = new ArrayList<>();

        for(int j = 0; j < jugadores.size(); j++){
            double[] desplazamiento = POSICION_FICHA[j % POSICION_FICHA.length];
            double x, y, salto = 0;

            if(j == animJugador){
                double t = (ahora - animInicio - MS_DADO) / (double) MS_SALTO;
                t = Math.max(0, Math.min(t, animPasos));
                int paso = (int) Math.floor(t);
                double fraccion = t - paso;
                if(paso >= animPasos){
                    paso = animPasos;
                    fraccion = 0;
                }
                int n = suelos.length;
                Point2D desde = centro((animDesde + paso) % n);
                Point2D hasta = centro((animDesde + paso + 1) % n);
                double e = fraccion * fraccion * (3 - 2 * fraccion);
                x = desde.getX() + (hasta.getX() - desde.getX()) * e;
                y = desde.getY() + (hasta.getY() - desde.getY()) * e;
                salto = 24 * Math.sin(Math.PI * fraccion);
            }
            else {
                Point2D c = centro(jugadores.get(j).getCasillaActual());
                x = c.getX();
                y = c.getY();
            }
            fichas.add(new Ficha(j, x + desplazamiento[0], y + desplazamiento[1], salto));
        }

        fichas.sort(Comparator.comparingDouble(Ficha::y));
        for(Ficha f : fichas)
            dibujarPeon(g, f, f.jugador() == juego.getIndiceJugadorActual(), ahora);
    }

    private void dibujarPeon(Graphics2D g, Ficha f, boolean activo, long ahora){
        Color color = Tema.jugador(f.jugador());
        Color borde = Tema.jugadorSombra(f.jugador());
        double x = f.x(), y = f.y();

        // Sombra en el suelo, más pequeña cuanto más alto está el salto
        double escala = (1 - f.salto() / 80) * TAMANO_PEON;
        g.setColor(new Color(0, 0, 0, 56));
        g.fill(new Ellipse2D.Double(x - 10 * escala, y - 5 * escala, 20 * escala, 10 * escala));

        // El jugador del turno lleva un anillo en el suelo que late
        if(activo){
            double r = 11 + 1.5 * Math.sin(ahora / 180.0);
            g.setColor(Tema.acento());
            g.setStroke(new BasicStroke(2f));
            g.draw(new Ellipse2D.Double(x - r, y - r / 2, 2 * r, r));
        }

        // El peón se dibuja con el pie en (0, 0) y después se coloca y se escala
        AffineTransform previa = g.getTransform();
        g.translate(x, y - f.salto());
        g.scale(TAMANO_PEON, TAMANO_PEON);

        Shape[] piezas = {
            new Ellipse2D.Double(-9, -6, 18, 8),
            poligono(pt(-6.5, -3), pt(-3, -17), pt(3, -17), pt(6.5, -3)),
            new Ellipse2D.Double(-6.5, -28, 13, 13)
        };
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for(Shape pieza : piezas){
            g.setColor(color);
            g.fill(pieza);
            g.setColor(borde);
            g.draw(pieza);
        }
        g.setColor(new Color(255, 255, 255, 140));
        g.fill(new Ellipse2D.Double(-3.8, -25.3, 3.6, 3.6));
        g.setTransform(previa);
    }


    // RATÓN

    private void actualizarHover(Point punto){
        if(juego == null)
            return;

        int nuevo = -1;
        try {
            Point2D q = vista.inverseTransform(punto, null);
            for(int k = ordenPintado.length - 1; k >= 0 && nuevo < 0; k--){
                int i = ordenPintado[k];
                if(suelos[i].contains(q))
                    nuevo = i;
            }
        }
        catch(NoninvertibleTransformException e){
            // Panel sin tamaño todavía: no hay casilla bajo el ratón
        }
        cambiarHover(nuevo);
    }

    private void cambiarHover(int nuevo){
        if(nuevo == hover)
            return;
        hover = nuevo;
        setCursor(nuevo >= 0 ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        alCambiarHover.accept(nuevo);
        repaint();
    }
}
