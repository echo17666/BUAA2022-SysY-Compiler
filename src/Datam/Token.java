package Datam;

public class Token{
    String content;
    Integer lineNumber;
    Integer wordNumber;
    public Token(String content,Integer lineNumber,Integer wordNumber){
        this.content=content;
        this.lineNumber=lineNumber;
        this.wordNumber=wordNumber;
    }

    public String getContent(){
        return content;
    }

    public Integer getLineNumber(){
        return lineNumber;
    }

    public Integer getWordNumber(){
        return wordNumber;
    }
}
