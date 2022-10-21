package Semantic;
import Datam.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class SemanticMain{
    public ArrayList<Token> bank=new ArrayList<>();
    FileWriter fw1 =new FileWriter("error.txt", false);
    FileWriter fw2 =new FileWriter("TokenList.txt", false);
    SemanticProcedure semanticProcedure= null;
    public SemanticMain(ArrayList<Token> bank) throws IOException{
        this.bank=bank;
    }
    public void analyze(){
        semanticProcedure= new SemanticProcedure(bank);
        semanticProcedure.analyze();
        semanticProcedure.check();
        semanticProcedure.finalErrorOutput();
    }
    public Token getAst(){
        return semanticProcedure.getTokenAst();
    }
}
