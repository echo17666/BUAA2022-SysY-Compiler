package Syntax;
import Datam.Token;
import java.util.ArrayList;

public class VocabularyMain{
    public ArrayList<Token> bank=new ArrayList<>();

    public VocabularyMain(ArrayList<Token> bank){
        this.bank=bank;
    }
    public void analyze(){
        Identify identify = new Identify(bank);
        identify.analyze();
    }
}
