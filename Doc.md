# 编译器设计文档

## 1.参考编译器介绍

## 2.编译器总体设计

## 3.词法分析设计


### 1.总述
词法分析的总任务是从源程序中识别出单词，记录单词类别和单词值。在词法分析的设计中给了一张表，其中有三项是加粗说明的，分别是 **`变量名（Ident）`**，**`整常数（IntConst）`**和 **`格式字符串（FormatString）`**而剩下都可以认为是 **`特殊字符`**。

仔细思考之后，发现，词法分析的作业本质上就是把文件转换成一个个的词，在之后的文章中我们称之为 **`Token`**。然后再把分出来的Token进行类别的判断，最后输出。

### 2.编码前的设计
编码的第一个关键在于，如何将读入的文件流转换成一行行读入，因为日后进行错误处理的时候，我们需要存储每一个Token的行号。所以最后经过查询后，确定采用**readline()函数**来进行读取。而文件的读入采用BufferedReader的方式进行读取，输出则直接图方便改变输出流来实现文件写入。
```java
BufferedReader filereader=new BufferedReader(new FileReader("testfile.txt"));
PrintStream out = System.out;
System.setOut(new PrintStream("output.txt"));
```

第二个关键在于，如何对分词，现在我们有一行行的字符串，我们要将其分成一个个词，这个时候我们需要一个 **`分词器`**，我们只要向其中输入整一行的字符串即可。
```java
int n=1;//行号
Split sentence = new Split();
//按行读入
while((str=filereader.readLine())!=null){
    //词法分析
    sentence.setSentence(str,n);
    sentence.output();
    check=sentence.getCheck();
    //行号递增
    n+=1;
}
```
然后对于最关键的分词部分，由于可变的Token只有Ident，IntConst和FormatString，发现这三类都是可变的单词，而分割Token的时候大多以 **`分界符`**和 **`空格`**（包括空格，\n，\r，\t）为界，所以我们可以将分界符和空格都看作是 **`分隔符`**。而可以将整个一行字符串分成一个 **char型数组**，然后一个个字符去扫描。然后创建一个动态字符串word，正常情况下，每扫描到一个字符，就将其接到word中。
```java
String Sentence;
String word="";
char letter[];
letter=Sentence.toCharArray();

// void output()
word=word+letter[i];
```
如果**分隔符是空格**，那么word里面是一个 **`单词`**，这时候word就是一个Token。然后我们只需要对word进行 **单词判断**，然后清空word字符串，接着继续输出即可。

```java
if(letter[i]==' '||letter[i]=='\n'||letter[i]=='\r'||letter[i]=='\t'){wordcheck();}
```

如果**分隔符是分界符**，那么此时word一定也是 **`单词`**，我们只需要先对word进行处理，再对我们当前的分界符进行处理，即可获得两个Token。然后清空word字符串，接着继续输出即可。
```java
public void wordcheck(){
    if(word!=""){
        WordCheck w = new WordCheck();
        w.setWord(word);
        w.output();   
        word="";
    }
}
```
对于 **`特殊字符`**，可以采用**哈希表**预先存起来，然后只需要直接调用**containsKey()方法**判断，即可知道该token是不是特殊字符。
```java
//存储特殊字符
HashMap <Character,String> SingleCharacter = new HashMap<Character,String>();
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

//判断是否是特殊字符
else if(SingleCharacter.containsKey(letter[i])){
    wordcheck();
    System.out.println(SingleCharacter.get(letter[i])+" "+letter[i]);
}
```
按照上面的算法流程，我们可以发现，word内不可能存在分隔符，如此看来，word内的 **`单词`**就包含三种情况：**`保留字，标识符，整型常量。`**，因为分隔符内不包含引号，所以只要读到引号，我们就可以直接读取 **`格式字符串`**，直到下一个引号。
```java
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
```

最后，对于word我们只需要判断，他是不是 **`保留字`**，是不是 **`整常数`**即可，如果都不是，那么就是 **`标识符`**。
```java
    public void output(){
            if(ReservedWords.containsKey(word)){
                System.out.println(ReservedWords.get(word)+" "+word);
            }
            else{
                char letter[]=word.toCharArray();
                if(letter[0]>='0'&&letter[0]<='9'){
                    if(isNumber(word)){
                       System.out.println("INTCON "+word);
                    }
                }
                else{
                    System.out.println("IDENFR "+word);
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
```

### 3.编码后的修改
在实际的编码过程中，我们发现，由于本程序是单个字符读取，故对于如 `<` 和 `<=` 这样的符号，我们无法判断，所以在遇到这种双字符的符号时，我们就会采取**预读**的措施
```java
else if(letter[i]=='<'){
    if(i==letter.length-1||letter[i+1]!='='){
        now=i;
        wordcheck();
        System.out.println("LSS <");
    }
    else{
        now=i;
        wordcheck();
        System.out.println("LEQ <=");
        i+=1;
    }
}
```

最后得到的完整代码结果如下
```java
//Split.java 分词器
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

//WordCheck.java 单词判断
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
        ReservedWords.put("getint","GETINTTK");
        ReservedWords.put("printf","PRINTFTK");
        ReservedWords.put("return","RETURNTK");
        ReservedWords.put("void","VOIDTK");
    }

    public void output(){
            if(ReservedWords.containsKey(word)){
                System.out.println(ReservedWords.get(word)+" "+word);
            }
            else{
                char letter[]=word.toCharArray();
                if(letter[0]>='0'&&letter[0]<='9'){
                    if(isNumber(word)){
                        System.out.println("INTCON "+word);
                    }
                }
                else{
                    System.out.println("IDENFR "+word);
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
```