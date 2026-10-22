
package civitas;

/**
 *
 * @author Denis Valentin Stoyanov D'Antonio y Raúl Cruz Arévalo
 */
public class Civitas {

    /**
     * @param args the command line arguments
     */
     //public static void main(String[] args){ 
       
        /* ESTE ES EL MAIN DE LA PRÁCTICA 1 Y EN ESTA PRÁCTICA NO SE PIDE MAIN.
        
        // EJERCICIO 1
        System.out.println("\nEJERCICIO 1");
         
        int uno=0, dos=0, tres=0, cuatro=0, valor=0, tirada;
       
    
        for(int i=0; i<100;i++){
            
            valor = Dado.getInstance().quienEmpieza(4);
           
            if(valor == 1)
                uno++;
                else if(valor == 2)
                    dos++;
                    else if(valor == 3)
                        tres++;
                        else
                            cuatro++;
            }  
        
        System.out.println("\nHa habido " + uno + " unos ");
        System.out.println("\nHa habido " + dos + " doses ");
        System.out.println("\nHa habido " + tres + " treses ");
        System.out.println("\nHa habido " + cuatro + " cuatros ");

        
        // EJERCICIO 2
        System.out.println("\nEJERCICIO 2");
        
        // Debug true
        Dado.getInstance().setDebug(true);
        for(int i=0; i<6; i++){
            int temporal;
            temporal = Dado.getInstance().tirar();
            System.out.println("\nDebug activado, el número es: " + temporal);
        }
        
        // Debug false
        Dado.getInstance().setDebug(false);
        for(int i=0; i<6; i++){
            int temporal;
            temporal = Dado.getInstance().tirar();
            System.out.println("\nDebug desactivado, el número es: " + temporal);
        }
        
        
        // EJERCICIO 3
        System.out.println("\nEJERCICIO 3");
        
        System.out.println("\nEl último resultado es: " + Dado.getInstance().getUltimoResultado());
        
        
        // EJERCICIO 4
        System.out.println("\nEJERCICIO 4");
        
        System.out.println("\n" + TipoCasilla.CALLE);
        System.out.println("\n" + TipoSorpresa.PAGARCOBRAR);
        
        
        // EJERCICIO 5
        System.out.println("\nEJERCICIO 5");
        
        Tablero Tablero1 = new Tablero();
        
        Casilla murcia = new Casilla(TipoCasilla.CALLE, "Murcia", 500, 100, 50);
        Casilla almeria = new Casilla(TipoCasilla.CALLE, "Almeria", 700, 120, 60);
        Casilla granada = new Casilla(TipoCasilla.CALLE, "Granada", 900, 160, 80);
        
        Tablero1.aniadeCasilla(murcia);
        Tablero1.aniadeCasilla(almeria);
        Tablero1.aniadeCasilla(granada);
        
        System.out.println("\n" + murcia.toString());
        System.out.println("\n" + almeria.toString());
        System.out.println("\n" + granada.toString());
        
        
        // EJERCICIO 6
        System.out.println("\nEJERCICIO 6");
        
        Casilla cara = new Casilla(TipoCasilla.CALLE, "Cara", 0, 0, 0);
        Casilla barata = new Casilla(TipoCasilla.CALLE, "Barata", 999, 0,0);
        float preciomedio = 0;
        float comparar = 0;
        
        for(int i=1; i < Tablero1.getCasillasTotales(); i++){
            comparar = Tablero1.getCasilla(i).getPrecioCompra();
            preciomedio += comparar;
            
            if(cara.getPrecioCompra() < comparar)
                cara = Tablero1.getCasilla((i));
            if(barata.getPrecioCompra() > comparar)
                barata = Tablero1.getCasilla(i);
        }
        
        preciomedio = preciomedio/Tablero1.getCasillasTotales();
        
        System.out.println("\nEl precio de compra medio es: " + preciomedio);
        System.out.println("\nLa calle más cara es: " + cara.toString());
        System.out.println("\nLa calle mas barata es: " + barata.toString());
    
    
        // EJERCICIO 7
        System.out.println("\nEJERCICIO 7");
        
        Diario.getInstance().ocurreEvento("Probando la clase Diario");
        Diario.getInstance().ocurreEvento("Segundo evento que añado");
        Diario.getInstance().ocurreEvento("Va a mostrar eventos hasta que se acaben");
        Diario.getInstance().ocurreEvento("Este es el último que añado y va a mostrar");
        
        do {
            System.out.println("\n" + Diario.getInstance().leerEvento());
        } while (Diario.getInstance().eventosPendientes());
        

        // EJERCICIO 8
        System.out.println("\nEJERCICIO 8");

        // Primero tiramos el dado con el debug a false
        Dado.getInstance().setDebug(false);
        System.out.println("\nDEBUG DESACTIVADO");
        int pos_actual = 0;
        int pos_anterior = 0;
        for (int i =0; i<7; i++){
            tirada = Dado.getInstance().tirar();
            pos_anterior = pos_actual;
            pos_actual = Tablero1.nuevaPosicion(pos_actual,tirada);
            
            System.out.println("\nEstabamos en la posición " + pos_anterior + ", ha salido un " + tirada +  " y se avanza hasta la posición " + pos_actual);    
        
            if(Tablero1.computarPasoPorSalida())
                System.out.println("\nSe ha pasado por la casilla de salida");
        }
        
        // Ahora tiramos el dado con el debug a true para comprobar que avanza de 1 en 1
        Dado.getInstance().setDebug(true);
        System.out.println("\nDEBUG ACTIVADO");
        pos_actual = 0;
        pos_anterior = 0;
        for (int i =0; i<7; i++){
            tirada = Dado.getInstance().tirar();
            pos_anterior = pos_actual;
            pos_actual = Tablero1.nuevaPosicion(pos_actual,tirada);
            
            System.out.println("\nEstabamos en la posición " + pos_anterior + ", ha salido un " + tirada +  " y se avanza hasta la posición " + pos_actual);
        } */
   // }
}