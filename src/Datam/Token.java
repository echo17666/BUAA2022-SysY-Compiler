package Datam;

import java.util.ArrayList;
import java.util.HashMap;

public class Token{
    String content;  //内容
    Integer lineNumber; //行号
    Integer wordNumber; //该行第几个字符
    String type="Var"; //参数类型（Const/Var/Func/Param/Block）
    String value="none"; //Block类型（int/void/repeat）（由于只考虑函数末尾是否存在return，所以循环和函数返回值不冲突）
    boolean hasReturn=false; //函数体内部是否有返回值
    Integer dimension=0; //函数维度
    Token FatherToken=null; //父节点
    ArrayList<Token> TokenList=new ArrayList<Token>(); //子节点（语法树）
    public Token(String content,Integer lineNumber,Integer wordNumber){
        this.content=content;
        this.lineNumber=lineNumber;
        this.wordNumber=wordNumber;
    }

    public void setFatherToken(Token fatherToken){
        FatherToken=fatherToken;
    }



    public Token getFatherToken(){
        return FatherToken;
    }

    public void setType(String type){
        this.type=type;
    }

    public String getType(){
        return type;
    }

    public void setDimension(Integer dimension){
        this.dimension=dimension;
    }

    public Integer getDimension(){
        return dimension;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content=content;
    }

    public Integer getLineNumber(){
        return lineNumber;
    }

    public Integer getWordNumber(){
        return wordNumber;
    }

    public ArrayList<Token> getTokenList(){
        return TokenList;
    }

    public void setValue(String value){
        this.value=value;
    }

    public String getValue(){
        return value;
    }

    public void setHasReturn(boolean hasReturn){
        this.hasReturn=hasReturn;
    }

    public boolean getHasReturn(){
        return hasReturn;
    }

    public void setTokenList(ArrayList<Token> tokenList){
        TokenList=tokenList;
    }
}
