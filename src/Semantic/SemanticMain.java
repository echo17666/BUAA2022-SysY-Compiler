package Semantic;
import Datam.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class SemanticMain{
    public ArrayList<Token> bank=new ArrayList<>();
    FileWriter fw1 =new FileWriter("error.txt", false);
    FileWriter fw2 =new FileWriter("TokenList.txt", false);
    public SemanticMain(ArrayList<Token> bank) throws IOException{
        this.bank=bank;
    }
    public void analyze(){
        Meaning meaning = new Meaning(bank);
        meaning.analyze();
        meaning.check();
        meaning.finalErrorOutput();
    }
}
