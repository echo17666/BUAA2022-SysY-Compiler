package LLVM;

import Datam.AstNode;
import Datam.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LLvmMain{
    AstNode compUnit = null;
    FileWriter fw1 =new FileWriter("llvm_ir.txt", false);
    Generator generator=null;
    public LLvmMain(AstNode t) throws IOException{
        this.compUnit=t;
    }
    public void generate(){
        generator = new Generator(compUnit);
        generator.init();
        generator.generating();
    }
}
