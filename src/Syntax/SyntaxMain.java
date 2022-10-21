package Syntax;
import Datam.Token;
import java.util.ArrayList;

public class SyntaxMain{
    public ArrayList<Token> bank=new ArrayList<>();

    public SyntaxMain(ArrayList<Token> bank){
        this.bank=bank;
    }
    public void analyze(){
        SyntaxProcedure syntaxProcedure= new SyntaxProcedure(bank);
        syntaxProcedure.analyze();
    }
}
