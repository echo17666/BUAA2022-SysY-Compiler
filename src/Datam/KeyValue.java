package Datam;

import java.util.ArrayList;

public class KeyValue{
    int dim=0;
    int d1=0;
    int d2=0;
    String AddrType="i32";
    String intVal="";
    String [] d1Value = null;
    String [][] d2Value = null;
    public void setD1(int d1){
        this.d1=d1;
        if(d1==0){d1Value = new String[10000];}
        else{d1Value = new String[d1];}
    }

    public void setD2(int d2){
        this.d2=d2;
        if(this.d1==0){d2Value = new String[10000][d2];}
        else{d2Value = new String[this.d1][d2];}
    }

    public void setAddrType(String addType){
        AddrType=addType;
    }

    public String getAddrType(){
        return AddrType;
    }

    public void setDim(int dim){
        this.dim=dim;
    }

    public int getDim(){
        return dim;
    }

    public int getD1(){
        return d1;
    }

    public int getD2(){
        return d2;
    }

    public void setD1Value(String[] d1Value){
        this.d1Value=d1Value;
    }

    public void setD2Value(String[][] d2Value){
        this.d2Value=d2Value;
    }

    public String[] getD1Value(){
        return d1Value;
    }

    public String[][] getD2Value(){
        return d2Value;
    }

    public void setIntVal(String intVal){
        this.intVal=intVal;
    }

    public String getIntVal(){
        return intVal;
    }
}
