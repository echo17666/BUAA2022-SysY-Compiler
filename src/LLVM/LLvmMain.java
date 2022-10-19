package LLVM;

import Datam.Token;

import java.io.FileWriter;
import java.io.IOException;

public class LLvmMain{
    Token compUnit = null;
    FileWriter fw1 =new FileWriter("llvm_ir.txt", false);
    Generator generator=null;
    public LLvmMain(Token t) throws IOException{
        this.compUnit=t;
    }
    public void generate(){
        generator = new Generator(compUnit);
        generator.generate();
    }
}
