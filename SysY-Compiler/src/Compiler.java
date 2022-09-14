import java.io.*;
import java.util.Scanner;

public class Compiler{
//    public static void main(String[] args){
//        Scanner input=new Scanner(System.in);
//        while(input.hasNext()){
//            String a=input.nextLine();
//            Split sentence = new Split();
//            sentence.setSentence(a);
//            sentence.output();
//        }
//
//    }
public static void main(String[] args)throws Exception{
    BufferedReader filereader=new BufferedReader(new FileReader("testfile.txt"));
    PrintStream out = System.out;
    System.setOut(new PrintStream("output.txt"));
    int n=1;
    int check=0;
    String  str;
    while((str=filereader.readLine())!=null){
        Split sentence = new Split();
        sentence.setSentence(str,check);
        sentence.output();
        check=sentence.getCheck();
    }
    filereader.close();
}


}
