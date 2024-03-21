package STCPObj;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

public class Objeto1 implements Serializable{

    int OID;
    String nomObjeto;
    String cualidad11;
    double cualidad12;
    
    public Objeto1(int OID,String nomObjeto,String cualidad11,double cualidad12){
        this.OID = OID;
        this.nomObjeto = nomObjeto;
        this.cualidad11 = cualidad11;
        this.cualidad12 = cualidad12;
    }

    public int getOID() {
        return OID;
    }

    public String getNomObjeto() {
        return nomObjeto;
    }

    public String getCualidad11() {
        return cualidad11;
    }

    public double getCualidad12() {
        return cualidad12;
    }
    
    public void muestraObjeto1(){
        System.out.println("Objeto1, OID:"+OID+", nombre="+nomObjeto+", cualidad11="
                +cualidad11+", cualidad12="+cualidad12);
    }
}

class ListaObjetos1 implements Serializable{
    private LinkedList lista = new LinkedList();
    int valor;
    
    ListaObjetos1(int tamano,int[] OID, String[] nombre, String[] cualidad1,double[] cualidad2){
        for(int i =0; i < tamano; i++)
            lista.add(new Objeto1(OID[i],nombre[i],cualidad1[i],cualidad2[i]));
    }
    
    ListaObjetos1(int tamano, Objeto1[] o1){
        for(int i=0; i<tamano;i++)
            lista.add(o1[i]);
    }
    
    public int getTamano(){
        return lista.size();
    }
    
    public Objeto1 getObjeto(int i){
        return (Objeto1) lista.get(i);
    }
    
    public void muestraObjeto1(){
        ListIterator li = lista.listIterator();
        Objeto1 o1;
        
        System.out.println("Objeto1["+lista.size()+"]=");
        while(li.hasNext()){
            o1 = (Objeto1) li.next();
            o1.muestraObjeto1();
        }
    }
}