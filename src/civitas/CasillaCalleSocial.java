/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civitas;

/**
 *
 * @author denisvs
 */
public class CasillaCalleSocial extends CasillaCalle {
    
    private int maxCasasSociales = 1;
    private int maxHotelesSociales = 1;

    public CasillaCalleSocial(String nombre, float precioCompra, float precioEdificar, float precioBaseAlquiler) {
        super(nombre, precioCompra, precioEdificar, precioBaseAlquiler);
    }
    
    @Override
    public CasillaCalleSocial hacermeSocial(Jugador jugador){
        return null;
    }
   
    @Override
    public String toString(){
        return (super.toString() + "\nSoy social, descuento del 2%");
    }
    
    public boolean soySocial(){
        return true;
    }

    @Override
    public float  getPrecioAlquilerCompleto(){
        float aux;
        aux = super.getPrecioAlquilerCompleto();
        aux = aux - (aux * 0.02f);
        return aux;
    }     
    
    
    
    
}
