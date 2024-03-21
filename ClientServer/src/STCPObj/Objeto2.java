package STCPObj;
import java.io.Serializable;

public class Objeto2 implements Serializable{

    int OID;
    String nomObjeto;
    long cualidad21;
    short cualidad22;
    
    public Objeto2(int OID,String nomObjeto,long cualidad21,short cualidad22){
        this.OID = OID;
        this.nomObjeto = nomObjeto;
        this.cualidad21 = cualidad21;
        this.cualidad22 = cualidad22;
    }

    public int getOID() {
        return OID;
    }

    public String getNomObjeto() {
        return nomObjeto;
    }

    public long getCualidad21() {
        return cualidad21;
    }

    public short getCualidad22() {
        return cualidad22;
    }
    public void imprimeObjeto2(){
        System.out.println("Objeto2: OID="+OID+" Nombre="+nomObjeto+
                " Cualidad 1="+cualidad21+" Cualidad 2="+cualidad22);
    }
}
