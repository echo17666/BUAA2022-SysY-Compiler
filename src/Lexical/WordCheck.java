package Lexical;

import java.util.HashMap;

public class WordCheck{
    String word="";
    HashMap<String,String> ReservedWords = new HashMap<String,String>();
    public void setWord(String word){
        this.word=word;
        ReservedWords.put("main","MAINTK");
        ReservedWords.put("const","CONSTTK");
        ReservedWords.put("int","INTTK");
        ReservedWords.put("break","BREAKTK");
        ReservedWords.put("continue","CONTINUETK");
        ReservedWords.put("if","IFTK");
        ReservedWords.put("else","ELSETK");
        ReservedWords.put("while","WHILETK");
        ReservedWords.put("for","FORTK");
        ReservedWords.put("getint","GETINTTK");
        ReservedWords.put("printf","PRINTFTK");
        ReservedWords.put("return","RETURNTK");
        ReservedWords.put("void","VOIDTK");
    }

    public void output(){
            if(ReservedWords.containsKey(word)){
                //System.out.println(ReservedWords.get(word)+" "+word);
            }
            else{
                char letter[]=word.toCharArray();
                if(letter[0]>='0'&&letter[0]<='9'){
                    if(isNumber(word)){
                       // System.out.println("INTCON "+word);
                    }
                }
                else{
                    //System.out.println("IDENFR "+word);
                }
            }
        }
    public static boolean isNumber(String str) {
        for (int i=0;i<str.length();i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
