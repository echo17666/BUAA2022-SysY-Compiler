import java.util.ArrayList;
import java.util.HashMap;

public class Split{
    String Sentence;
    char letter[];
    public ArrayList<String> bank=new ArrayList<>();
    String word="";
    int check;
    HashMap <Character,String> SingleCharacter = new HashMap<Character,String>();
    public void setSentence(String sentence,int check){
        this.Sentence=sentence;
        this.check=check;
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
            w.setWord(word);
            w.output();
            bank.add(word);
            word="";
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
                    wordcheck();
                }
                else if(SingleCharacter.containsKey(letter[i])){
                    wordcheck();
                    //System.out.println(SingleCharacter.get(letter[i])+" "+letter[i]);
                    bank.add(String.valueOf(letter[i]));
                }
                else if(letter[i]=='!'){
                    if(i==letter.length-1||letter[i+1]!='='){
                        wordcheck();
                        //System.out.println("NOT !");
                        bank.add("!");
                    }
                    else{
                        wordcheck();
                        //System.out.println("NEQ !=");
                        i+=1;
                        bank.add("!=");
                    }
                }
                else if(letter[i]=='/'){
                    if(i==letter.length-1||letter[i+1]!='/'&&letter[i+1]!='*'){
                        wordcheck();
                        //System.out.println("DIV /");
                        bank.add("/");
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
                        wordcheck();
                        bank.add("<");
                        //System.out.println("LSS <");
                    }
                    else{
                        wordcheck();
                       // System.out.println("LEQ <=");
                        i+=1;
                        bank.add("<=");
                    }
                }
                else if(letter[i]=='>'){
                    if(i==letter.length-1||letter[i+1]!='='){
                        wordcheck();
                       // System.out.println("GRE >");
                        bank.add(">");
                    }
                    else{
                        wordcheck();
                       //System.out.println("GEQ >=");
                        i+=1;
                        bank.add(">=");
                    }
                }
                else if(letter[i]=='='){
                    if(i==letter.length-1||letter[i+1]!='='){
                        wordcheck();
                        //System.out.println("ASSIGN =");
                        bank.add("=");
                    }
                    else{
                        wordcheck();
                        //System.out.println("EQL ==");
                        i+=1;
                        bank.add("==");
                    }
                }
                else if(letter[i]=='&'){
                    if(letter[i+1]=='&'){
                        wordcheck();
                        //System.out.println("AND &&");
                        i+=1;
                        bank.add("&&");
                    }
                }
                else if(letter[i]=='|'){
                    if(letter[i+1]=='|'){
                        wordcheck();
                        //System.out.println("OR ||");
                        i+=1;
                        bank.add("||");
                    }
                }
                else if(letter[i]=='"'){
                    wordcheck();
                    i+=1;
                    while(letter[i]!='"'){
                        word=word+letter[i];
                        i+=1;
                    }
                    //System.out.println("STRCON \""+word+"\"");
                    bank.add("\""+word+"\"");
                    word="";

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
            System.out.println(bank.get(i));
        }
    }

    public ArrayList<String> getBank(){
        return bank;
    }
}
