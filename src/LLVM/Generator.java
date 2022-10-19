package LLVM;

import Datam.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Generator{
    Token compUnit = null;
    public Generator(Token compUnit){
        this.compUnit=compUnit;
    }
    public void generate(){
        search(compUnit);
    }
    public void search(Token t){
        output(t);
        if(t.getTokenList().size()>0){
            ArrayList<Token> l=t.getTokenList();
            for(int i=0;i<l.size();i++){
                search(l.get(i));
            }
        }
    }
    public void output(Token t){
        FileWriter fw=null;
        try {
            fw=new FileWriter("llvm_ir.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw=new PrintWriter(fw);
        pw.println(t.getContent()+" "+t.getType()+" "+t.getValue());
        pw.flush();
    }
}
