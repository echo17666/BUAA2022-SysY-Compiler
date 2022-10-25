package Datam;

import java.util.ArrayList;
import java.util.HashMap;

public class Token{
    String content;
    Integer lineNumber;
    Integer wordNumber;
    String type="Var";
    String value="none";
    boolean hasReturn=false;
    String syntax="";
    Integer dimension=0;
    Token FatherToken=null;
    ArrayList<Token> TokenList=new ArrayList<Token>();
    public Token(String content,Integer lineNumber,Integer wordNumber){
        this.content=content;
        this.lineNumber=lineNumber;
        this.wordNumber=wordNumber;
    }

    public void setFatherToken(Token fatherToken){
        FatherToken=fatherToken;
    }

    public void setSyntax(String syntax){
        this.syntax=syntax;
    }

    public String getSyntax(){
        return syntax;
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
