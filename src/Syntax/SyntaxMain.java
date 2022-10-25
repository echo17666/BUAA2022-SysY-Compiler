package Syntax;
import Datam.AstNode;
import Datam.Token;
import java.util.ArrayList;

public class SyntaxMain{
    public ArrayList<Token> bank=new ArrayList<>();

    public SyntaxMain(ArrayList<Token> bank){
        this.bank=bank;
    }
    SyntaxProcedure2 syntaxProcedure= null;
    public void analyze(){
//        SyntaxProcedure syntaxProcedure= new SyntaxProcedure(bank);
//        syntaxProcedure.analyze();
          syntaxProcedure= new SyntaxProcedure2(bank);
          syntaxProcedure.analyze();
    }
    public AstNode getAst(){
        return syntaxProcedure.getAst();
    }
}
