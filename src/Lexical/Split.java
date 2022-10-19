package Lexical;

import Datam.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class Split{
    String Sentence;
    char letter[];
    Integer lineNumber;
    public ArrayList<Token> bank=new ArrayList<>();
    String word="";
    int check;
    int startCharacter;
    int now;
    HashMap <Character,String> SingleCharacter = new HashMap<Character,String>();
    public void setSentence(String sentence,int check,int n){
        this.Sentence=sentence;
        this.check=check;
        this.startCharacter=0;
        this.lineNumber=n;
        SingleCharacter.put('+',"PLUS");
        SingleCharacter.put('-',"MINU");
        SingleCharacter.put('*',"MULT");
        SingleCharacter.put('%',"MOD");
        SingleCharacter.put(';',"SEMICN");
        SingleCharacter.put(',',"COMMA");
        SingleCharacter.put('(',"LPARENT");
        SingleCharacter.put(')',"RPARENT");
        SingleCharacter.put('[',"LBRACK");
        SingleCharacter.put(']',"RBRACK");
        SingleCharacter.put('{',"LBRACE");
        SingleCharacter.put('}',"RBRACE");
    }
    public void wordcheck(){
        if(word!=""){
            WordCheck w = new WordCheck();
            Token t = new Token(word,lineNumber,this.startCharacter);
            w.setWord(word);
            w.output();
            bank.add(t);
            word="";
            this.startCharacter=this.now;
        }
    }


    public int getCheck(){
        return this.check;
    }

    public void output(){
        letter=Sentence.toCharArray();
        for(int i=0;i<letter.length;i++){
            if(this.check==1){
                if(i==letter.length-1||!(letter[i]=='*'&&letter[i+1]=='/')){
                   continue;
                }
                else{
                    i=i+1;
                    this.check=0;
                }
            }
            else{
                if(letter[i]==' '||letter[i]=='\n'||letter[i]=='\r'||letter[i]=='\t'){
                    now=i+1;
                    wordcheck();
                }
                else if(SingleCharacter.containsKey(letter[i])){
                    now=i;
                    wordcheck();
                    //System.out.println(SingleCharacter.get(letter[i])+" "+letter[i]);
                    Token t = new Token(String.valueOf(letter[i]),lineNumber,this.startCharacter);
                    bank.add(t);
                    this.startCharacter=i+1;
                }
                else if(letter[i]=='!'){
                    if(i==letter.length-1||letter[i+1]!='='){
                        now=i;
                        wordcheck();
                        Token t = new Token("!",lineNumber,this.startCharacter);
                        //System.out.println("NOT !");
                        bank.add(t);
                        this.startCharacter=i+1;
                    }
                    else{
                        now=i;
                        wordcheck();
                        Token t = new Token("!=",lineNumber,this.startCharacter);
                        //System.out.println("NEQ !=");
                        this.startCharacter=i+2;
                        i+=1;
                        bank.add(t);
                    }
                }
                else if(letter[i]=='/'){
                    if(i==letter.length-1||letter[i+1]!='/'&&letter[i+1]!='*'){
                        now=i;
                        wordcheck();
                        Token t = new Token("/",lineNumber,this.startCharacter);
                        //System.out.println("DIV /");
                        bank.add(t);
                        this.startCharacter=i+1;
                    }
                    else{
                        wordcheck();
                        if(letter[i+1]=='/'){
                            break;
                        }
                        else if(letter[i+1]=='*'){
                            this.check=1;
                            i+=1;
                        }
                    }
                }
                else if(letter[i]=='<'){
                    if(i==letter.length-1||letter[i+1]!='='){
                        now=i;
                        wordcheck();
                        Token t = new Token("<",lineNumber,this.startCharacter);
                        bank.add(t);
                        this.startCharacter=i+1;
                        //System.out.println("LSS <");
                    }
                    else{
                        now=i;
                        wordcheck();
                        Token t = new Token("<=",lineNumber,this.startCharacter);
                        bank.add(t);
                        this.startCharacter=i+2;
                        // System.out.println("LEQ <=");
                        i+=1;

                    }
                }
                else if(letter[i]=='>'){
                    if(i==letter.length-1||letter[i+1]!='='){
                        now=i;
                        wordcheck();
                        Token t = new Token(">",lineNumber,this.startCharacter);
                        bank.add(t);
                        this.startCharacter=i+1;

                       // System.out.println("GRE >");

                    }
                    else{
                        now=i;
                        wordcheck();
                        Token t = new Token(">=",lineNumber,this.startCharacter);
                        bank.add(t);
                        this.startCharacter=i+2;
                       //System.out.println("GEQ >=");
                        i+=1;
                    }
                }
                else if(letter[i]=='='){
                    if(i==letter.length-1||letter[i+1]!='='){
                        now=i;
                        wordcheck();
                        Token t = new Token("=",lineNumber,this.startCharacter);
                        bank.add(t);
                        this.startCharacter=i+1;
                        //System.out.println("ASSIGN =");

                    }
                    else{
                        now=i;
                        wordcheck();
                        Token t = new Token("==",lineNumber,this.startCharacter);
                        bank.add(t);
                        this.startCharacter=i+2;
                        //System.out.println("EQL ==");
                        i+=1;

                    }
                }
                else if(letter[i]=='&'){
                    if(letter[i+1]=='&'){
                        now=i;
                        wordcheck();
                        Token t = new Token("&&",lineNumber,this.startCharacter);
                        bank.add(t);
                        this.startCharacter=i+2;
                        i+=1;
                        //System.out.println("AND &&");
                    }
                }
                else if(letter[i]=='|'){
                    if(letter[i+1]=='|'){
                        now=i;
                        wordcheck();
                        Token t = new Token("||",lineNumber,this.startCharacter);
                        bank.add(t);
                        this.startCharacter=i+2;
                        i+=1;
                        //System.out.println("OR ||");
                    }
                }
                else if(letter[i]=='"'){
                    now=i;
                    wordcheck();
                    i+=1;
                    while(letter[i]!='"'){
                        word=word+letter[i];
                        i+=1;
                    }
                    //System.out.println("STRCON \""+word+"\"");
                    Token t = new Token("\""+word+"\"",lineNumber,this.startCharacter);
                    bank.add(t);
                    word="";
                    this.startCharacter=i+1;
                }
                else{
                    word=word+letter[i];
                }
            }
        }
        wordcheck();
    }
    public void checkBank(){
        for(int i=0;i<bank.size();i++){
            System.out.println(bank.get(i).getContent()+","+bank.get(i).getLineNumber()+","+bank.get(i).getWordNumber());
        }
    }

    public ArrayList<Token> getBank(){
        return bank;
    }
}
