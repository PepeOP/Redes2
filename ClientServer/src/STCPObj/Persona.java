package STCPObj;

import java.io.Serializable;

public class Persona implements Serializable{
    private int id;
    private long boleta;
    private String nombre;
    private int puerto;
    
    public Persona(int id, long boleta, String nombre, int puerto){
        this.id = id;
        this.boleta = boleta;
        this.nombre = nombre;
        this.puerto = puerto;
    }
    int getId(){
        return id;
    }
    long getBoleta(){
        return boleta;
    }
    String getNombre(){
        return nombre;
    }
    int getPuerto(){
        return puerto;
    }
    
    void setId(int id){
        this.id = id;
    }
    void setBoleta(long boleta){
        this.boleta = boleta;
    }
    void setNombre(String nombre){
        this.nombre =  nombre;
    }
    void setPuerto(int puerto){
        this.puerto = puerto;
    } 
}
