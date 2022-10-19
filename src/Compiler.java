import LLVM.LLvmMain;
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
    //按行读入
    while((str=filereader.readLine())!=null){
        //词法分析
        sentence.setSentence(str,check,n);
        sentence.output();
        check=sentence.getCheck();
        //行号递增
        n+=1;
    }
    //语法分析，使用词法得到的Token表格
    //VocabularyMain vocabulary = new VocabularyMain(sentence.getBank());
    //vocabulary.analyze();
    //语义分析，使用词法得到的Token表格，包含语法，语法+语义+错误一遍处理
    SemanticMain semantic = new SemanticMain(sentence.getBank());
    semantic.analyze();
    //生成中间代码
    LLvmMain llvmMain = new LLvmMain(semantic.getAst());
    llvmMain.generate();

    filereader.close();

}


}
