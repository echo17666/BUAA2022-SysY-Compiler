import java.io.*;
import java.util.Scanner;

public class Compiler{
public static void main(String[] args)throws Exception{
    BufferedReader filereader=new BufferedReader(new FileReader("testfile.txt"));
    PrintStream out = System.out;
    System.setOut(new PrintStream("output.txt"));
    int n=1;
    int check=0;
    String  str;
    Split sentence = new Split();
    while((str=filereader.readLine())!=null){
        sentence.setSentence(str,check);
        sentence.output();
        check=sentence.getCheck();
    }
    VocabularyMain vocabulary = new VocabularyMain(sentence.getBank());
    vocabulary.analyze();
    filereader.close();
//    sentence.checkBank();
}


}
