package Datam;

import java.util.ArrayList;

public class KeyValue{
    int dim=0;
    int d1=0;
    int d2=0;
    String intVal="";
    ArrayList <String> value = new ArrayList<>();

    public void setD1(int d1){
        this.d1=d1;
    }

    public void setD2(int d2){
        this.d2=d2;
    }

    public void setDim(int dim){
        this.dim=dim;
    }

    public int getDim(){
        return dim;
    }

    public void setValue(ArrayList<String> value){
        this.value=value;
    }

    public ArrayList<String> getValue(){
        return value;
    }

    public void setIntVal(String intVal){
        this.intVal=intVal;
    }

    public String getIntVal(){
        return intVal;
    }
}
