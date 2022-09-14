import java.util.HashMap;

public class Split{
    String Sentence;
    char letter[];
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
                    System.out.println(SingleCharacter.get(letter[i])+" "+letter[i]);
                }
                else if(letter[i]=='!'){
                    if(i==letter.length-1||letter[i+1]!='='){
                        wordcheck();
                        System.out.println("NOT !");
                    }
                    else{
                        wordcheck();
                        System.out.println("NEQ !=");
                        i+=1;
                    }
                }
                else if(letter[i]=='/'){
                    if(i==letter.length-1||letter[i+1]!='/'&&letter[i+1]!='*'){
                        wordcheck();
                        System.out.println("DIV /");
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
                        System.out.println("LSS <");
                    }
                    else{
                        wordcheck();
                        System.out.println("LEQ <=");
                        i+=1;
                    }
                }
                else if(letter[i]=='>'){
                    if(i==letter.length-1||letter[i+1]!='='){
                        wordcheck();
                        System.out.println("GRE >");
                    }
                    else{
                        wordcheck();
                        System.out.println("GEQ >=");
                        i+=1;
                    }
                }
                else if(letter[i]=='='){
                    if(i==letter.length-1||letter[i+1]!='='){
                        wordcheck();
                        System.out.println("ASSIGN =");
                    }
                    else{
                        wordcheck();
                        System.out.println("EQL ==");
                        i+=1;
                    }
                }
                else if(letter[i]=='&'){
                    if(letter[i+1]=='&'){
                        wordcheck();
                        System.out.println("AND &&");
                        i+=1;
                    }
                }
                else if(letter[i]=='|'){
                    if(letter[i+1]=='|'){
                        wordcheck();
                        System.out.println("OR ||");
                        i+=1;
                    }
                }
                else if(letter[i]=='"'){
                    wordcheck();
                    i+=1;
                    while(letter[i]!='"'){
                        word=word+letter[i];
                        i+=1;
                    }
                    System.out.println("STRCON \""+word+"\"");
                    word="";
                }
                else{
                    word=word+letter[i];
                }
            }
        }
        wordcheck();
    }
}
