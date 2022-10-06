import Lexical.Split;
import Semantic.SemanticMain;
import Syntax.VocabularyMain;

import java.io.*;

public class Compiler{
public static void main(String[] args)throws Exception{
    BufferedReader filereader=new BufferedReader(new FileReader("testfile.txt"));
    FileWriter fw =new FileWriter("output.txt", false);
    int n=1;
    int check=0;
    String  str;
    Split sentence = new Split();
    while((str=filereader.readLine())!=null){
        sentence.setSentence(str,check,n);
        sentence.output();
        check=sentence.getCheck();
        n+=1;
    }
    //VocabularyMain vocabulary = new VocabularyMain(sentence.getBank());
    //vocabulary.analyze();
    SemanticMain semantic = new SemanticMain(sentence.getBank());
    semantic.analyze();
    filereader.close();
    //sentence.checkBank();
}


}
