# 编译器设计文档
<!-- TOC -->

- [编译器设计文档](#编译器设计文档)
  - [1.参考编译器介绍](#1参考编译器介绍)
  - [2.编译器总体设计](#2编译器总体设计)
  - [3.词法分析设计](#3词法分析设计)
    - [1.总述](#1总述)
    - [2.编码前的设计](#2编码前的设计)
    - [3.编码后的修改](#3编码后的修改)
  - [4.语法分析设计](#4语法分析设计)
    - [1.总述](#1总述-1)
    - [2.编码前的设计](#2编码前的设计-1)
    - [3.编码后的修改](#3编码后的修改-1)
  - [附录 期中考试解析](#附录-期中考试解析)
    - [1.题目说明](#1题目说明)
    - [2.repeat/until](#2repeatuntil)
    - [3.Hexadecimal](#3hexadecimal)

<!-- /TOC -->
## 1.参考编译器介绍

## 2.编译器总体设计

## 3.词法分析设计


### 1.总述
词法分析的总任务是从源程序中识别出单词，记录单词类别和单词值。在词法分析的设计中给了一张表，其中有三项是加粗说明的，分别是 **`变量名（Ident）`**，**`整常数（IntConst）`** 和 **`格式字符串（FormatString）`** 而剩下都可以认为是 **`特殊字符`**。

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
    sentence.setSentence(str,n);
    sentence.output();
    //行号递增
    n+=1;
}
```
然后对于最关键的分词部分，由于可变的Token只有Ident，IntConst和FormatString，发现这三类都是可变的单词，而分割Token的时候大多以 **`分界符`** 和 **`空格`**（包括空格，\n，\r，\t）为界，所以我们可以将分界符和空格都看作是 **`分隔符`**。而可以将整个一行字符串分成一个 **char型数组**，然后一个个字符去扫描。然后创建一个动态字符串word，正常情况下，每扫描到一个字符，就将其接到word中。
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
按照上面的算法流程，我们可以发现，word内不可能存在分隔符，如此看来，word内的 **`单词`** 就包含三种情况：**`保留字，标识符，整型常量。`**，因为分隔符内不包含引号，所以只要读到引号，我们就可以直接读取 **`格式字符串`**，直到下一个引号。
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

最后，对于word我们只需要判断，他是不是 **`保留字`**，是不是 **`整常数`** 即可，如果都不是，那么就是 **`标识符`**。
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

同时，对于 **`/* */`** 和 **`//`** 的注释写法有所不同，因为我们是一行行进入分词器的，对于 **`//`** 的判断，只要读取到，则可以直接结束该行的分词。而对于 **`/* */`** ，由于注释可以**跨行**，故我们需要外加一个**check**来判断此时**是否需要进行分词**
```java
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
```

如果此时是注释且没有读到 **`*/`** ，则直接读下一个字符，否则进行**分词**
```java
if(this.check==1){
    if(i==letter.length-1||!(letter[i]=='*'&&letter[i+1]=='/')){
        continue;
    }
    else{
        i=i+1;
        this.check=0;
    }
}
```

相应的，最外层也要增加这个参数
```java
while((str=filereader.readLine())!=null){
    sentence.setSentence(str,check,n);
    sentence.output();
    check=sentence.getCheck();
    //行号递增
    n+=1;
}
```

由此， **`词法分析`完成**

## 4.语法分析设计
### 1.总述
语法分析的总任务将源程序，通过给定的文法，分析其程序运行的顺序。在语法分析中，我们采用最朴素的 **`递归下降子程序`** 的方法去运行我们的程序。而这种做法就需要我们把之前获取到的Token传入文档中，所以需要对之前的文法进行一定的修改。
### 2.编码前的设计
首先对文法进行一定的小修改，首要目标是要**有序存储Token**，故建立Token类，包含**Token内容（content）**，**Token所在行号（lineNumber）**，**首字母在该行的字符数（wordNumber）**
```java
public class Token{
    String content;
    Integer lineNumber;
    Integer wordNumber;
    public Token(String content,Integer lineNumber,Integer wordNumber){
        this.content=content;
        this.lineNumber=lineNumber;
        this.wordNumber=wordNumber;
    }
}
```
然后在 **`词法分析`** 进行修改，我们用 **startCharacter** 来记录word字符串的首个字符的wordNumber，然后再在分完词后再实时更新每个词的起始位置。为了达到**有序存储**，我们可以采用**ArrayList**来存储Token。由于语法分析的输出和词法输出是在**一遍**内完成，所以我们可以将输出统一放到 **`语法分析`** 那一遍内完成。
```java
public ArrayList<Token> bank=new ArrayList<>();
//增加存储Token
if(SingleCharacter.containsKey(letter[i])){
    now=i;
    wordcheck();
    //System.out.println(SingleCharacter.get(letter[i])+" "+letter[i]);
    Token t = new Token(String.valueOf(letter[i]),lineNumber,this.startCharacter);
    bank.add(t);
    this.startCharacter=i+1;
}
```
然后设计语法分析的入口函数
```java
public class SyntaxMain{
    public ArrayList<Token> bank=new ArrayList<>();
    public SyntaxMain(ArrayList<Token> bank){
        this.bank=bank;
    }
    public void analyze(){
        SyntaxProcedure syntaxProcedure= new SyntaxProcedure(bank);
        syntaxProcedure.analyze();
    }
}
```
然后再在主函数增加语法分析
```java
SyntaxMain syntax = new SyntaxMain(sentence.getBank());
syntax.analyze();
```

那么接下来我们只需要在**SyntaxProcedure**内编写 **`递归下降子程序`** 即可。可以事先准备好一个指针**current**，表示目前读到第几个Token，同时用**sym**用于判断目前的Token内容，便于程序判断。仿照递归下降子程序，我们可以编写**nextsym()**函数，用于读取下一个Token，顺便进行**词法的输出**。同时，我们预留读取下一个，辖两个，上一个Token的函数，用于**预读和重读**的使用。然后入口函数选择**CompUnit()**，即从程序开始处开始分析。
```java
public void nextsym(){
    output(this.sym);
    if(this.current<bank.size()-1){
        this.current+=1;
        this.sym=bank.get(this.current).getContent();
    }
}
public String getnextsym(){return bank.get(this.current+1).getContent();}
public String getbeforesym(){return bank.get(this.current-1).getContent();}
public String getnextnextsym(){return bank.get(this.current+2).getContent();}
public void analyze(){CompUnit();}
```
根据**递归下降子程序**的实现方法，我们只需要自顶向下分析每一条文法中每一个Token是否符合要求即可，并在最后输出文法左值即可。我们以**CompUnit()**为例
```java
//CompUnit → {Decl} {FuncDef} MainFuncDef
public void CompUnit(){
    while(sym.equals("const")||sym.equals("int")&&isIdent(getnextsym())&&!getnextnextsym().equals("(")){
        if(sym.equals("const")){ConstDecl();}
        else{VarDecl();}
    }
    while(sym.equals("void")||sym.equals("int")&&isIdent(getnextsym())&&getnextnextsym().equals("(")){FuncDef();}
    if(sym.equals("int")&&getnextsym().equals("main")){MainFuncDef();}
    else{}
    output("<CompUnit>");
}
```

那么根据递归下降子程序的规定，我们首先要对文法做处理，即
- 消除左递归
- 解决回溯问题

对于包含左递归的文法，我们只需要修改文法即可，如
```java
AddExp → MulExp | AddExp ('+' | '−') MulExp
可以改成
AddExp → MulExp { ('+' | '−') MulExp }
```
而解决回溯的问题，我们需要先获取所有的 **`First集`** 和 **`Follow集`** ，然后再根据 **`First集`** 和 **`Follow集`** 来判断是否有回溯问题，如果有回溯问题，我们需要对文法进行修改。
对于有左公因子的文法，我们可以提取左公因子，如
```java
VarDef → Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
可以改成
VarDef → Ident { '[' ConstExp ']' } [ '=' InitVal ]
```

然而，在实际操作中，我们可以进行 **预读** 操作，即可以预先读取多个字符，然后再进行判断，这样就可以巧妙地解决回溯问题。例如，对于下面的文法
```java
UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')'
PrimaryExp → '(' Exp ')' | LVal | Number
LVal → Ident {'[' Exp ']'} 
```
如果执行**UnaryExp**的First集，不难发现 `UnaryExp → PrimaryExp → LVal → Ident {'[' Exp ']'}`和 `UnaryExp → Ident '(' [FuncRParams] ')'`的First集合都包含**Ident**，此时提取左公因子修改文法显然十分麻烦，所以我们可以**预读**，即读完Ident这个Token后**再读一个Token**。由于默认给的代码都是不带错误的，所以读完Ident后只可能是两种情况，即如果是**小括号`（`**，则执行后者，否则执行前者。这样就可以巧妙地解决回溯问题。
```java
public void UnaryExp(){
    if(sym.equals("+")||sym.equals("-")||sym.equals("!")){UnaryOp();UnaryExp();}
    else if(sym.equals("(")||isNumber(sym)){PrimaryExp();}
    else if(isIdent(sym)){
        if(getnextsym().equals("(")){nextsym();nextsym();
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){FuncRParams();}
            if(sym.equals(")")){nextsym();}
            else{}
        }
        else{PrimaryExp();}
    }
    else{}
    output("<UnaryExp>");
}
```
最后，由于文法和词法一起输出，我们需要对输出函数output()进行修改，使之能同时按顺序输出**文法和词法**
```java
public void output(String sym){
    FileWriter fw =null;
    try {fw=new FileWriter("output.txt", true);} //输出到同一个文件
    catch (IOException e) {e.printStackTrace();}
    PrintWriter pw = new PrintWriter(fw);
    //语法输出，首字符一定是"<",且为了防止其和"< LSS","<= LEQ"冲突
    //首字母后一定是大写英文字母，由此判断这是语法输出
    if(sym.charAt(0)=='<'&&sym.length()>1&&sym.charAt(1)-'A'>=0&&sym.charAt(1)-'A'<26){
        pw.println(sym);
        pw.flush();
    }
    //此后与之前的词法输出一样
    else if(ReservedCharacter.containsKey(sym)){
        pw.println(ReservedCharacter.get(sym)+" "+sym);
        pw.flush();
    }
    else{
        if(sym.charAt(0)=='"'){
            pw.println("STRCON "+sym);
            pw.flush();
        }
        else if(isNumber(sym)){
            pw.println("INTCON "+sym);
            pw.flush();
        }
        else{
            pw.println("IDENFR "+sym);
            pw.flush();
        }
    }
}
```
### 3.编码后的修改
实际编码的时候，我们发现，在**消除左递归文法**的时候，变相更改了期望输出，例如
```java
1+2
文法1：
AddExp → MulExp | AddExp ('+' | '−') MulExp

Output：
INTCON 1
<MulExp> // 1
<AddExp> // 1
PLUSTK +
INTCON 2
<MulExp> // 2
<AddExp> // 1+2

文法2：
AddExp → MulExp { ('+' | '−') MulExp }

Output：
INTCON 1
<MulExp> // 1
PLUSTK +
INTCON 2
<MulExp> // 2
<AddExp> // 1+2
```
我们发现，更改后虽然不妨碍递归下降子程序的运行，但是输出少了一个 **\<AddExp>**，这时候我们就发现，只要其不是AddExp的**最后一项**，我们就一定要输出一遍 **\<MulExp>** 后再输出一遍 **\<AddExp>** 。故在函数调用后我们需要**额外判断**是否为最后一项
```java
public void AddExp(){
    if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){MulExp();
        while(sym.equals("+")||sym.equals("-")){
            output("<AddExp>");
            nextsym();MulExp();
            if(getbeforesym().equals("+")||getbeforesym().equals("-")){
                output("<AddExp>");
            }
        }
    }
    else{}
    output("<AddExp>");
}
```
同理，从**LOrExp**一步步到**MulExp**这些消除左递归的文法，都需要额外判断一遍。

同时，在编写 **`Stmt`** 的时候，有以下三条导出式

- Stmt → LVal `'='` Exp `';'` // 每种类型的语句都要覆盖
- Stmt → [Exp] `';'` //有无Exp两种情况
- Stmt → LVal `'='` `'getint'` `'('` `')'` `';'`
- LVal → Ident
- Exp → AddExp → MulExp → UnaryExp → Ident `'('` [FuncRParams] `')'`

我们发现，**`Stmt`** 的导出式子中，**`LVal`** 和 **`Exp`** 都有可能包含**Ident**的首字符，这时候我们就需要采取**预读**的方法，我们发现，**`Stmt`** 的导出式子中，含有 **`LVal`** 的导出式在 **`LVal`** 之后一定包含 **`'='`** ，故我们只需要往后预读，只要读到 **`'='`** 就可以判断这个**Ident**是否是 **`Lval`** ，由于上述三个式子的末尾一定都有 **`';'`** 且在此之前不可能包含其他 **`'='`** 或者 **`';'`** ，所以只要在第一次读到 **`';'`** 前读到 **`'='`** 就返回**true**，否则返回**false**

```java
public boolean isLVal(){
    int j=this.current;// 记录起始位置
    int check=0;
    for(;j<this.bank.size()&&check==0;j++){
        if(check==0&&bank.get(j).getContent().equals("=")){return true;} //只要读到=就可以判断是否为LVal
        if(bank.get(j).getContent().equals(";")){check=1;}
    }
    return false;
}
```
在Stmt中便可以如下判断
```java
else if(isIdent(sym)){
    if(isLVal()){LVal();
        if(sym.equals("="))
        ···
    }
    else{Exp();
        if(sym.equals(";")){nextsym();}
        else{}
    }
}
```

至此，语法分析完成

项目结构如下

```bash
src
│  Compiler.java
│
├─Datam
│      Token.java
│
├─Lexical
│      Split.java
│      WordCheck.java
│
└─Syntax
       SyntaxMain.java
       SyntaxProcedure.java
```
## 附录 期中考试解析
> 这部分是考试完写的，题目准确率90%，可能有点小问题，仅供参考
> 由于其他部分是需要上交的，这部分不用，所以这部分可能就~~夹带点私货~~了
### 1.题目说明
期中考试增加了两个地方，第一个是在 **`Stmt`** 增加了文法，第二个是增加了新的 **`数字类型`**

- 文法增加：
Stmt → `'repeat'` Stmt `'until'`  `'('`  Cond  `')'` 

- 文法修改：
Number → IntConst | `HexadecimalConst`

- 保留字增加：
  - `repeat` REPEATTK
  - `until` UNTILTK
  - 十六进制数 HEXCON

- 十六进制说明：
  - HexadecimalConst → HexadecimalPrefix HexadecimalDigit | HexadecimalConst HexadecimalDigit
  - HexadecimalPrefix → `'0x'` | `'0X'`
  - HexadecimalDigit → `'0'` |`'1'` |`'2'` |`'3'` |`'4'` |`'5'` |`'6'` |`'7'` |`'8'` |`'9'` |`'A'` |`'B'` |`'C'` |`'D'` |`'E'` |`'F'` |`'a'` |`'b'` |`'c'` |`'d'` |`'e'` |`'f'` 

一共十个样例点（这个具体怎么分的忘记了，但是应该不重要）
- **testfile 1-3** 只判断`源程序`
- **testfile 4-5** 只添加`repeat/until`
- **testfile 6-8** 只添加`十六进制`
- **testfile 9-10** 添加`repeat/until`和`十六进制`
### 2.repeat/until
一般的评分标准都有 **`什么都不加的源程序评分`** （简称送分题）第一步先直接下下来提交一版，看看能不能过送分题，如果过了，那么就可以开始写代码了；如果没过，那么就要看看源程序哪里出问题（不过应该大概率不会吧QAQ）

首先是词法的修改，即 **`repeat`** 和 **`until`** 俩保留字，所以可以先去 **`词法分析`** 增加两个保留字的识别

（然而我直接偷懒，因为testfile给的一定是**没有错误的文件**，故只要是个词我都扔进Token表中，统一放到Syntax下判断）

接下来是对 **`语法分析`** 的修改，首先是在 **`特殊字符`** 表中添加 **`repeat`** 和 **`until`** .
```java
ReservedCharacter.put("repeat","REPEATTK");
ReservedCharacter.put("until","UNTILTK");
```
然后是在 **`Stmt`** 中添加文法
```java
public void Stmt(){
    if(sym.equals("{")){Block();}
    else if···

    else if(sym.equals("repeat")){nextsym();Stmt();
        if(sym.equals("until")){nextsym();
            if(sym.equals("(")){nextsym();Cond();
                if(sym.equals(")")){nextsym();}
            }
        }
    }
    
    else if···
    output("<Stmt>");
}
```
然后在所有用到**Stmt的FIRST集**的地方添加**repeat**判断，包含
- `BlockItem` → Decl | `Stmt` 
- `Stmt` →  'if' '(' Cond ')' `Stmt` [ 'else' `Stmt` ]
- `Stmt` → 'while' '(' Cond ')' `Stmt`

仔细判断可以发现，后面两个文法中，我们实际写的时候**不需要判断Stmt的首字符**，因为Stmt的前面都是 **`终结符`**，所以不需要作修改。而 **`BlockItem`** 则需要判断首字符集来判断是 **`Decl`** 还是 **`Stmt`** ，所以需要添加 **`repeat`** 的判断。

然后，我们需要判断哪些地方用到了**BlockItem的首字符**集合
- `Block` → '{' { `BlockItem` } '}'

由于 **`BlockItem`** 是可以**重复0次或任意次**，故递归下降子程序中，我们要用 **`while`** 和 **`First集合`** 判断。所以再这里也需要添加 **`repeat`判断**

所以在上述**两处地方**递归下降子程序中增加
```java
||sym.equals("repeat")
```

（**小技巧**：因为只有Stmt里面用到了 **`repeat`** ，同理，只有Stmt里面用到了 **`return`** ，所以可以在`IDEA`中直接 **`Ctrl+F`** 搜索 **`return`** ，只要有 **`return`** 判断的地方，加上 **`repeat`** 判断就可以了）

此时提交一版，看看过了没有，如果过了，说明这个部分没有问题，可以继续开始下一部分。

### 3.Hexadecimal
这一部分是十六进制的数字，所以我们同样需要修改 **`词法分析`** 和 **`语法分析`** 两部分。

首先，一开始我们只需要判断 **`Number`** 类型是不是**整型数字**，而此时，Number不仅包含了整形，还包含了**十六进制**的判断，所以我们需要有一个能够判断十六进制的函数。

顺带一提，判断Number的时候，Java有**isDigit**的方法判断字符是否是数字，当然你可以去使用 [**`正则表达式`**](https://llleo-li-github-io.vercel.app/posts/a95e.html) （这里附上一篇同为小学期助教的李昊哥哥的博客，感兴趣的可以去学习一下）

这里我们采用最简单的判别方法，首先进行文法的修改，即消除左递归。修改后文法为

- HexadecimalConst → ( `0x` | `0X` ) HexadecimalDigit { HexadecimalDigit }

首先判断是否是 **`0`** ，如果是，判断下一个字符是否是 **`x/X`** ，如果是，那么接下来判断是否是十六进制数，即0-9/a-f/A-F，如果是，则返回true，否则返回false。

```java
public static boolean isHexadecimal(String str){
    if(str.length() < 2) { return false; }
    if(str.charAt(0) == '0'){
        if(str.charAt(1) == 'x' || str.charAt(1) == 'X'){
            for(int i = 2; i < str.length(); i++){
                if(!((str.charAt(i) >= '0' && str.charAt(i) <= '9') || (str.charAt(i) >= 'a' && str.charAt(i) <= 'f') || (str.charAt(i) >= 'A' && str.charAt(i) <= 'F'))){
                    return false;
                }
            }
            return true;
        }
    }
    return false;
}
```
其实还是可以投机取巧，因为给的文法**一定是正确的**，所以甚至只要判断 **`0x`** 或 **`0X`** 就可以了，因为**不可能有第二种以0x或0X打头的Token**

所以，假设之前判断是否是整数的函数叫**isNumber()**，那么我们只需要把之前的判断内容换成另一个函数，在原函数下增加一个判断即可，这样递归下降子程序可以不用更改，即
```java
public static boolean isInt(String str){
    for (int i=0;i<str.length();i++) {
        if (!Character.isDigit(str.charAt(i))) {
            return false;
        }
    }
    return true;
}
public static boolean isNumber(String str){
    return (isInt(str) || isHexadecimal(str));
}
```

最后，在输出的时候，只需要别忘了判断是输出 **`INTCON`** 还是 **`HEXCON`** 即可
```java
if(sym.charAt(0)=='<'&&sym.length()>1&&sym.charAt(1)-'A'>=0&&sym.charAt(1)-'A'<26){
    pw.println(sym);
    pw.flush();
}
else if(ReservedCharacter.containsKey(sym)){
    pw.println(ReservedCharacter.get(sym)+" "+sym);
    pw.flush();
}
else{
    if(sym.charAt(0)=='"'){
        pw.println("STRCON "+sym);
        pw.flush();
    }
    else if(isInt(sym)){
        pw.println("INTCON "+sym);
        pw.flush();
    }
    else if(isHexadecimal(sym)){
        pw.println("HEXCON "+sym);
        pw.flush();
    }
    else{
        pw.println("IDENFR "+sym);
        pw.flush();
    }
}
```
至此，十个样例点全部通过，考试完成，如果顺利的话**15-20分钟**就可以完成，所以不用慌张。
