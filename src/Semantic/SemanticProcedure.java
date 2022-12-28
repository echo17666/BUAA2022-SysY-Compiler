package Semantic;

import Datam.ErrorLine;
import Datam.Token;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SemanticProcedure{
    static HashMap<String,String> ReservedCharacter = new HashMap<String,String>();
    public ArrayList <Token> bank;
    public ArrayList <ErrorLine> error=new ArrayList<ErrorLine>();;
    public ArrayList <Integer> existLine=new ArrayList<Integer>();;
    int current=0;
    Token compUnit = new Token("<CompUnit>",-1,-1);
    Token mainFuncDef = new Token("<MainFuncDef>",-1,-1);
    String sym="";
    public SemanticProcedure(ArrayList<Token> bank){

        this.bank=bank;
        sym=bank.get(current).getContent();
        ReservedCharacter.put("main","MAINTK");
        ReservedCharacter.put("const","CONSTTK");
        ReservedCharacter.put("int","INTTK");
        ReservedCharacter.put("break","BREAKTK");
        ReservedCharacter.put("continue","CONTINUETK");
        ReservedCharacter.put("if","IFTK");
        ReservedCharacter.put("else","ELSETK");
        ReservedCharacter.put("while","WHILETK");
        ReservedCharacter.put("getint","GETINTTK");
        ReservedCharacter.put("printf","PRINTFTK");
        ReservedCharacter.put("return","RETURNTK");
        ReservedCharacter.put("void","VOIDTK");
        ReservedCharacter.put("!","NOT");
        ReservedCharacter.put("&&","AND");
        ReservedCharacter.put("||","OR");
        ReservedCharacter.put("+","PLUS");
        ReservedCharacter.put("-","MINU");
        ReservedCharacter.put("*","MULT");
        ReservedCharacter.put("/","DIV");
        ReservedCharacter.put("%","MOD");
        ReservedCharacter.put("<","LSS");
        ReservedCharacter.put("<=","LEQ");
        ReservedCharacter.put(">","GRE");
        ReservedCharacter.put(">=","GEQ");
        ReservedCharacter.put("==","EQL");
        ReservedCharacter.put("!=","NEQ");
        ReservedCharacter.put("=","ASSIGN");
        ReservedCharacter.put(";","SEMICN");
        ReservedCharacter.put(",","COMMA");
        ReservedCharacter.put("(","LPARENT");
        ReservedCharacter.put(")","RPARENT");
        ReservedCharacter.put("[","LBRACK");
        ReservedCharacter.put("]","RBRACK");
        ReservedCharacter.put("{","LBRACE");
        ReservedCharacter.put("}","RBRACE");
    }
    public static boolean isNumber(String str) {
        for (int i=0;i<str.length();i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    public static boolean isIdent(String str) {
        if(str.matches("^[_a-zA-Z0-9]+$")&&!ReservedCharacter.containsKey(str)&&!isNumber(str)){return true;}
        else{return false;}
    }
    public boolean isLVal(){
        int j=this.current;
        int check=0;
        for(;j<this.bank.size()&&check==0;j++){
            if(check==0&&bank.get(j).getContent().equals("=")&&bank.get(j).getLineNumber()==bank.get(this.current).getLineNumber()){return true;}
            if(bank.get(j).getContent().equals(";")){check=1;}
        }
        return false;
    }
    public void nextsym(){
        output(this.sym);
        if(this.current<bank.size()-1){
            this.current+=1;
            this.sym=bank.get(this.current).getContent();
        }
    }public String getnextsym(){
        return bank.get(this.current+1).getContent();
    }
    public String getbeforesym(){return bank.get(this.current-1).getContent();}
    public String getnextnextsym(){
        return bank.get(this.current+2).getContent();
    }
    public void analyze(){
        CompUnit();
    }

    public void CompUnit(){
        while(sym.equals("const")||sym.equals("int")&&isIdent(getnextsym())&&!getnextnextsym().equals("(")){
            if(sym.equals("const")){ConstDecl(compUnit);}
            else{VarDecl(compUnit);}
        }
        while(sym.equals("void")||sym.equals("int")&&isIdent(getnextsym())&&getnextnextsym().equals("(")){FuncDef();}
        if(sym.equals("int")&&getnextsym().equals("main")){MainFuncDef();}
        else{}
        output("<CompUnit>");
    }
    public void ConstDecl(Token t){
        if(sym.equals("const")){nextsym();
            if(sym.equals("int")){nextsym();ConstDef(t);
                while(sym.equals(",")){nextsym();ConstDef(t);}
                if(sym.equals(";")){nextsym();}
                else{errorOutput("i",bank.get(this.current-1));}
            }
            else{}
        }
        else{}
        output("<ConstDecl>");
    }
    public void ConstDef(Token t){
        int d=0;
        if(isIdent(sym)){
            Token n = bank.get(this.current);
            n.setFatherToken(t);
            n.setType("const");
            ArrayList<Token> c = t.getTokenList();
            if(reDefine(t,sym)){errorOutput("b",bank.get(this.current));}
            nextsym();
            while(sym.equals("[")){nextsym();ConstExp(t);
                if(sym.equals("]")){nextsym();d++;}
                else{errorOutput("k",bank.get(this.current-1));}
            }
            n.setDimension(d);
            c.add(n);
            t.setTokenList(c);
            if(sym.equals("=")){nextsym();ConstInitVal(t);
                }
            else{}
        }
        else{}
        output("<ConstDef>");
    }
    public void ConstInitVal(Token t){
        //判断(、ident/number/+/-/!
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){ConstExp(t);}
        else if(sym.equals("{")){nextsym();
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)||sym.equals("{")){ConstInitVal(t);
                while(sym.equals(",")){nextsym();ConstInitVal(t);}
            }
            if(sym.equals("}")){nextsym();}
        }
        else{}
        output("<ConstInitVal>");
    }
    public void VarDecl(Token t){
        if(sym.equals("int")){nextsym();VarDef(t);
            while(sym.equals(",")){nextsym();VarDef(t);}
            if(sym.equals(";")){nextsym();
                }
            else{errorOutput("i",bank.get(this.current-1));}
        }
        else{}
        output("<VarDecl>");
    }
    public void VarDef(Token t){
        int d=0;
        if(isIdent(sym)){
            Token n = bank.get(this.current);
            n.setFatherToken(t);
            if(reDefine(t,sym)){errorOutput("b",bank.get(this.current));}
            nextsym();
            ArrayList<Token> c = t.getTokenList();
            while(sym.equals("[")){nextsym();ConstExp(t);
                if(sym.equals("]")){nextsym();d++;}
                else{errorOutput("k",bank.get(this.current-1));}
            }
            n.setDimension(d);
            c.add(n);
            t.setTokenList(c);
            if(sym.equals("=")){nextsym();InitVal(t);}
        }
        else{}
        output("<VarDef>");
    }
    public void InitVal(Token t){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp(t);}
        else if(sym.equals("{")){nextsym();
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)||sym.equals("{")){InitVal(t);
                while(sym.equals(",")){nextsym();InitVal(t);}
            }
            if(sym.equals("}")){nextsym();}
        }
        else{}
        output("<InitVal>");
    }
    public void FuncDef(){
        if(sym.equals("int")||sym.equals("void")){
            Token t = new Token("a",-1,-1);
            t.setFatherToken(compUnit);
            FuncType(t);
            if(isIdent(sym)){
                t.setContent(sym);
                if(reDefine(compUnit,sym)){errorOutput("b",bank.get(this.current));}
                nextsym();
                if(sym.equals("(")){nextsym();
                    if(sym.equals("int")){FuncFParams(t);}
                    t.setType("Function");
                    ArrayList<Token> c = compUnit.getTokenList();
                    c.add(t);
                    compUnit.setTokenList(c);
                    if(sym.equals(")")){nextsym();}
                    else{errorOutput("j",bank.get(this.current-1));}
                    Block(t);
                }
                else{}
            }
            else{}
            t.setType("Function");
            ArrayList<Token> c = compUnit.getTokenList();
            c.remove(c.size()-1);
            c.add(t);
            compUnit.setTokenList(c);
            if(!t.getHasReturn()&&(t.getValue().equals("int"))){errorOutput("g",bank.get(this.current-1));}
            if(t.getHasReturn()&&(t.getValue().equals("void"))){errorOutput("f",bank.get(this.current-2));}
        }
        else{}
        output("<FuncDef>");
    }
    public void MainFuncDef(){
        if(sym.equals("int")){nextsym();
            mainFuncDef.setFatherToken(compUnit);
            mainFuncDef.setType("Function");
            mainFuncDef.setValue("int");
            if(sym.equals("main")&&getnextsym().equals("(")){
                nextsym();nextsym();}
            else{}
            if(sym.equals(")")){nextsym();}
            else{errorOutput("j",bank.get(this.current));}
            Block(mainFuncDef);
        }
        else{}
        ArrayList<Token> l=compUnit.getTokenList();

        l.add(mainFuncDef);
        compUnit.setTokenList(l);
        if(!mainFuncDef.getHasReturn()){
            errorOutput("g",bank.get(this.current));}
        output("<MainFuncDef>");
    }
    public void FuncType(Token t){
        if(sym.equals("void")){nextsym();t.setValue("void");}
        else if(sym.equals("int")){nextsym();t.setValue("int");}
        else{}
        output("<FuncType>");
    }
    public void FuncFParams(Token t){
        if(sym.equals("int")){FuncFParam(t);
            while(sym.equals(",")){nextsym();FuncFParam(t);}
        }
        else{}
        output("<FuncFParams>");
    }
    public void FuncFParam(Token t){
        if(sym.equals("int")){nextsym();
            if(isIdent(sym)){
                if(reDefine(t,sym)){errorOutput("b",bank.get(this.current));}
                ArrayList<Token> l = t.getTokenList();
                Token n = bank.get(this.current);
                n.setType("Param");
                n.setFatherToken(t);
                nextsym();
                int d=0;
                if(sym.equals("[")){nextsym();
                    if(sym.equals("]")){nextsym();d+=1;}
                    else{errorOutput("k",bank.get(this.current-1));}
                    if(sym.equals("[")){nextsym();ConstExp(t);
                        if(sym.equals("]")){nextsym();d+=1;}
                        else{errorOutput("k",bank.get(this.current-1));}
                    }
                }
                n.setDimension(d);
                l.add(n);
                t.setTokenList(l);
            }
            else{}
        }
        else{}
        output("<FuncFParam>");
    }
    public void Block(Token t){
        if(sym.equals("{")){
            nextsym();
            Token b=bank.get(this.current);
            b.setFatherToken(t);
            b.setContent("block");
            b.setType("block");
            while(sym.equals("const")||sym.equals("int")||isIdent(sym)||sym.equals(";")||sym.equals("if")||sym.equals("while")||sym.equals("break")||sym.equals("continue")||sym.equals("return")||sym.equals("printf")||
                    sym.equals("{")||sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isNumber(sym)){
                BlockItem(b);
            }
            if(sym.equals("}")){nextsym();}
            else{}
            ArrayList<Token> l=t.getTokenList();
            l.add(b);
            t.setTokenList(l);
            if(b!=null&&b.getHasReturn()){t.setHasReturn(true);}
        }
        else{}
        output("<Block>");
    }
    public void BlockItem(Token t){
        if(sym.equals("const")||sym.equals("int")){
            if(sym.equals("const")){ConstDecl(t);}
            else{VarDecl(t);}
        }
        else if(isIdent(sym)||sym.equals(";")||sym.equals("if")||sym.equals("while")||sym.equals("continue")||sym.equals("break")||sym.equals("return")||sym.equals("printf")||
                sym.equals("{")||sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isNumber(sym)){Stmt(t);}
        else{}
    }
    public void Stmt(Token t){
        if(sym.equals("{")){Block(t);}
        else if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isNumber(sym)){Exp(t);
            if(sym.equals(";")){nextsym();}
            else{errorOutput("i",bank.get(this.current-1));}
        }
        else if(sym.equals(";")){nextsym();}
        else if(sym.equals("if")){nextsym();
            if(sym.equals("(")){nextsym();Cond(t);
                if(sym.equals(")")){nextsym();}
                else{errorOutput("j",bank.get(this.current-1));}
                Stmt(t);
                if(sym.equals("else")){nextsym();Stmt(t);}
            }
            else{}
        }
        else if(sym.equals("while")){nextsym();
            if(sym.equals("(")){nextsym();Cond(t);
                if(sym.equals(")")){nextsym();}
                else{errorOutput("j",bank.get(this.current-1));}
                Token s=new Token("repeating",bank.get(this.current).getLineNumber(),bank.get(this.current).getWordNumber());
                s.setFatherToken(t);
                s.setValue("repeat");
                Stmt(s);
            }
            else{}
        }
        else if(sym.equals("break")){
            if(!hasRepeat(t)){errorOutput("m",bank.get(this.current));}
            nextsym();
            if(sym.equals(";")){nextsym();}
            else{errorOutput("i",bank.get(this.current-1));}
        }
        else if(sym.equals("continue")){
            if(!hasRepeat(t)){errorOutput("m",bank.get(this.current));}
            nextsym();
            if(sym.equals(";")){nextsym();}
            else{errorOutput("i",bank.get(this.current-1));}
        }
        else if(sym.equals("return")){
            if(!hasReturn(t)){errorOutput("f",bank.get(this.current));}
            nextsym();
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){
                t.setHasReturn(true);
                Exp(t);}
            if(sym.equals(";")){nextsym();}
            else{errorOutput("i",bank.get(this.current-1));}
        }
        else if(sym.equals("printf")){
            int printfpoint=this.current;
            nextsym();
            if(sym.equals("(")){nextsym();
                int now=this.current;
                int intnum=FormatString();
                int parnum=0;
                int check=1;
                if(intnum<0){check=-1;intnum=-1*intnum;}
                while(sym.equals(",")){nextsym();Exp(t);parnum++;}
                if(sym.equals(")")){nextsym();
//                    if(check==-1){errorOutput("a",bank.get(now));}
                    if(intnum!=parnum){errorOutput("l",bank.get(printfpoint));}

                    if(sym.equals(";")){nextsym();}
                    else{errorOutput("i",bank.get(this.current-1));}
                }
                else{errorOutput("j",bank.get(this.current-1));}
            }
            else{}
        }
        else if(isIdent(sym)){
            if(isLVal()){
                if(isConst(t,sym)){errorOutput("h",bank.get(this.current));}
                LVal(t);
                if(sym.equals("=")){nextsym();
                    if(sym.equals("getint")){nextsym();
                        if(sym.equals("(")){nextsym();
                            if(sym.equals(")")){nextsym();}
                            else{errorOutput("j",bank.get(this.current-1));}
                            if(sym.equals(";")){nextsym();}
                            else{errorOutput("i",bank.get(this.current-1));}
                        }
                        else{}
                    }
                    else if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp(t);
                        if(sym.equals(";")){nextsym();}
                        else{errorOutput("i",bank.get(this.current-1));}
                    }
                    else{}
                }
                else{}
            }
            else{Exp(t);
                if(sym.equals(";")){nextsym();}
                else{errorOutput("i",bank.get(this.current-1));}
            }
        }
        else{}
        output("<Stmt>");
    }
    public int FormatString(){
        String s=sym;
        char[] letter=s.toCharArray();
        int i=0;
        int num=0;
        int check=1;
        if(letter[i]=='\"'){
            i++;

            while(letter[i]!='\"'){

                if(letter[i]==32||letter[i]==33||letter[i]>=40&&letter[i]<=126||letter[i]=='%'){
                    if(letter[i]==92&&(i>=letter.length-2||letter[i+1]!='n')){
                        check=-1;
                        errorOutput("a",bank.get(this.current));
                    }
                    if(letter[i]=='%'&&(i>=letter.length-2||letter[i+1]!='d')){
                        check=-1;
                        errorOutput("a",bank.get(this.current));
                    }
                    if(letter[i]=='%'&&i<letter.length-2&&letter[i+1]=='d'){num++;}

                    }
                else{errorOutput("a",bank.get(this.current));
                    check=-1;
                }
                i++;
            }

        }
        nextsym();
        return check*num;
    }
    public void Exp(Token t){
        AddExp(t);
        output("<Exp>");
    }
    public void Cond(Token t){
        LOrExp(t);
        output("<Cond>");
    }
    public void LVal(Token t){
        if(isIdent(sym)){
            if(notDefine(t,sym)){errorOutput("c",bank.get(this.current));}
            nextsym();
            while(sym.equals("[")){nextsym();Exp(t);
                if(sym.equals("]")){nextsym();}
                else{errorOutput("k",bank.get(this.current-1));}
            }
        }
        else{}
        output("<LVal>");
    }
    public void PrimaryExp(Token t){
        if(sym.equals("(")){nextsym();Exp(t);
            if(sym.equals(")")){nextsym();}
            else{errorOutput("j",bank.get(this.current-1));}
        }
        else if(isNumber(sym)){Number1();}
        else if(isIdent(sym)){LVal(t);}
        else{}
        output("<PrimaryExp>");
    }
    public void Number1(){
        if(isNumber(sym)){nextsym();}
        output("<Number>");
    }
    public void UnaryExp(Token t){
        if(sym.equals("+")||sym.equals("-")||sym.equals("!")){UnaryOp();UnaryExp(t);}
        else if(sym.equals("(")||isNumber(sym)){PrimaryExp(t);}
        else if(isIdent(sym)){
            int now=this.current;
            String funcName=sym;
            if(getnextsym().equals("(")){
                if(notDefine(t,sym)){errorOutput("c",bank.get(this.current));}
                nextsym();nextsym();
                if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){
                    if(!checkParamNum(this.current,funcName)){errorOutput("d",bank.get(now));}
                    else{
                        if(!checkParamType(t,this.current,funcName)){
                            errorOutput("e",bank.get(now));
                        }
                    }
                    FuncRParams(t);
                }
                if(sym.equals(")")){nextsym();}
                else{errorOutput("j",bank.get(this.current-1));}
            }
            else{PrimaryExp(t);}
        }
        else{}
        output("<UnaryExp>");
    }
    public void UnaryOp(){
        if(sym.equals("!")||sym.equals("+")||sym.equals("-")){nextsym();}
        else{}
        output("<UnaryOp>");
    }
    public void FuncRParams(Token t){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp(t);}
        while(sym.equals(",")){nextsym();Exp(t);}
        output("<FuncRParams>");
    }
    public void MulExp(Token t){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){UnaryExp(t);
            while(sym.equals("*")||sym.equals("/")||sym.equals("%")){
                output("<MulExp>");
                nextsym();UnaryExp(t);
                if(getbeforesym().equals("*")||getbeforesym().equals("/")||getbeforesym().equals("%")){
                    output("<MulExp>");
                }
            }
        }
        else{}
        output("<MulExp>");
    }
    public void AddExp(Token t){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){MulExp(t);
            while(sym.equals("+")||sym.equals("-")){
                output("<AddExp>");
                nextsym();MulExp(t);
                if(getbeforesym().equals("+")||getbeforesym().equals("-")){
                    output("<AddExp>");
                }
            }
        }
        else{}
        output("<AddExp>");
    }
    public void RelExp(Token t){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){AddExp(t);
            while(sym.equals("<")||sym.equals(">")||sym.equals("<=")||sym.equals(">=")){
                output("<RelExp>");
                nextsym();AddExp(t);
                if(getbeforesym().equals("<")||getbeforesym().equals(">")||getbeforesym().equals("<=")||getbeforesym().equals(">=")){
                    output("<RelExp>");
                }
            }
        }
        else{}
        output("<RelExp>");
    }
    public void EqExp(Token t){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){RelExp(t);
            while(sym.equals("==")||sym.equals("!=")){
                output("<EqExp>");
                nextsym();RelExp(t);
                if(getbeforesym().equals("==")||getbeforesym().equals("!=")){
                    output("<EqExp>");
                }
            }
        }
        else{}
        output("<EqExp>");
    }
    public void LAndExp(Token t){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){EqExp(t);
            while(sym.equals("&&")){
                output("<LAndExp>");
                nextsym();EqExp(t);
                if(getbeforesym().equals("&&")){
                    output("<LAndExp>");
                }
            }
        }
        else{}
        output("<LAndExp>");
    }
    public void LOrExp(Token t){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){LAndExp(t);
            while(sym.equals("||")){
                output("<LOrExp>");
                nextsym();LAndExp(t);
                if(getbeforesym().equals("||")){
                    output("<LOrExp>");
                }
            }
        }
        else{}
        output("<LOrExp>");
    }
    public void ConstExp(Token t){AddExp(t);
        output("<ConstExp>");
    }
    public void output(String sym){
        FileWriter fw =null;
        try {
            fw=new FileWriter("output.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
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
    public boolean reDefine(Token t,String sym){
        ArrayList <Token>l=t.getTokenList();
        for(int i=0;i<l.size();i++){
            if(sym.equals(l.get(i).getContent())){return true;}
        }
        return false;
    }
    public boolean notDefine(Token t,String sym){
        ArrayList <Token>l=t.getTokenList();
        for(int i=0;i<l.size();i++){
            if(sym.equals(l.get(i).getContent())){return false;}
        }
        if(t==compUnit){return true;}
        return notDefine(t.getFatherToken(),sym);
    }
    public int returnDimention(Token t,String sym){
        ArrayList <Token>l=t.getTokenList();
        for(int i=0;i<l.size();i++){
            if(sym.equals(l.get(i).getContent())){return l.get(i).getDimension();}
        }
        if(t==compUnit){return 0;}
        return returnDimention(t.getFatherToken(),sym);
    }
    public String returnParamType(Token t,String sym){
        ArrayList <Token>l=t.getTokenList();
        for(int i=0;i<l.size();i++){
            if(sym.equals(l.get(i).getContent())){return l.get(i).getValue();}
        }
        if(t==compUnit){return "none";}
        return returnParamType(t.getFatherToken(),sym);
    }
    public boolean isConst(Token t,String sym){
        ArrayList <Token>l=t.getTokenList();
        for(int i=0;i<l.size();i++){
            if(sym.equals(l.get(i).getContent())&&Objects.equals(l.get(i).getType(),"const")){return true;}
        }
        if(t==compUnit){return false;}
        else{
            return isConst(t.getFatherToken(),sym);
        }
    }
    public boolean hasReturn(Token t){
        if(t.getType().equals("Function")){
            if(t.getValue().equals("int")||t.getValue().equals("void")){
                return true;
            }
            else{
                    return false;
            }
        }
        else{
            return hasReturn(t.getFatherToken());
        }
    }
    public boolean hasRepeat(Token t){
        if(t.getValue().equals("repeat")){
                return true;
        }
        if(t.getFatherToken()==null){return false;}
        return hasRepeat(t.getFatherToken());
    }
    public boolean checkParamNum(int current,String funcName){
        int i=current;
        ArrayList <Token> funcParams=new ArrayList<>();
        ArrayList <Token> compunit = compUnit.getTokenList();
        for(int j=0;j<compunit.size();j++){
            if(compunit.get(j).getContent().equals(funcName)){
                Token func=compunit.get(j);
                funcParams=func.getTokenList();
                break;
            }
        }
        int num=0;
        for(int j=0;j<funcParams.size();j++){
            if(funcParams.get(j).getType().equals("Param")){
                num++;
            }
            else{break;}
        }
        int parnum=0;
        int stack=0;
        if(bank.get(i).getContent().equals(")")){parnum=0;}
        else{
            parnum=1;
            for(;;i++){
                if(bank.get(i).getContent().equals(")")&&stack==0){
                    break;
                }
                if(bank.get(i).getContent().equals(",")&&stack==0){
                    parnum++;
                }
                if(bank.get(i).getContent().equals("(")){
                    stack++;
                }
                if(bank.get(i).getContent().equals(")")&&stack>0){stack--;}
                if(bank.get(i).getContent().equals("}")){break;}
            }
        }
        if(parnum==num){return true;}
        else{
            //System.out.println(num+" "+parnum+" "+funcName);
            return false;}
    }
    public boolean checkParamType(Token t,int current,String funcName){
        int i=current;
        ArrayList<Token> funcParams=new ArrayList<>();
        ArrayList<Token> compunit=compUnit.getTokenList();
        for(int j=0;j<compunit.size();j++){
            if(compunit.get(j).getContent().equals(funcName)){
                Token func=compunit.get(j);
                funcParams=func.getTokenList();
                break;
            }
        }
        int num=0;
        for(int j=0;j<funcParams.size();j++){
            if(funcParams.get(j).getType().equals("Param")){
                num++;
            }
            else{
                break;
            }
        }
        if(num==0){
            return true;
        }
        int a[]=new int[num+3];
        int b[]=new int[num+3];
        int anum=0;
        for(int j=0;j<funcParams.size();j++){
            if(funcParams.get(j).getType().equals("Param")){
                a[anum]=funcParams.get(j).getDimension();
                anum++;
            }
            else{
                break;
            }
        }

        int nownum=0;
        int parnum=i;
        int stack=0;
        for(;;i++){
            if(bank.get(i).getContent().equals(")")&&stack==0){
                b[nownum]=checkDimention(t,parnum,i-1);
                nownum++;
                parnum=i-1;
                break;
            }
            if(bank.get(i).getContent().equals(",")&&stack==0){
                b[nownum]=checkDimention(t,parnum,i-1);
                nownum++;
                parnum=i+1;
            }
            if(bank.get(i).getContent().equals("(")){
                stack++;
            }
            if(bank.get(i).getContent().equals(")")&&stack>0){stack--;}
            if(bank.get(i).getContent().equals("}")){break;}
        }
        for(int z=0;z<num;z++){
            if(a[z]!=b[z]){return false;}
        }
        return true;
    }
    public int checkDimention(Token t,int l,int r){
        int dim=0;
        int oridim=0;
        boolean check=true;
        for(int i=l;i<=r;i++){
            if(bank.get(i).getContent().equals("[")){
                dim=1;
            }
            if(bank.get(i).getContent().equals("]")&&bank.get(i+1).getContent().equals("[")){
                dim=2;
                i++;
            }
            if(isIdent(bank.get(i).getContent())&&check){
                if(returnParamType(t,bank.get(i).getContent()).equals("void")){return -9999;}
                oridim=returnDimention(t,bank.get(i).getContent());

                check=false;
            }

        }
        return oridim-dim;
    }
    public void check(){
        FileWriter fw =null;
        try {
            fw=new FileWriter("TokenList.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        listprint(compUnit,pw);
    }
    public void listprint(Token t,PrintWriter pw){
        ArrayList<Token> l=t.getTokenList();
        for(int i=0;i<l.size();i++){
            Token n=l.get(i);
            pw.println(n.getContent()+" "+n.getType()+" "+n.getDimension()+" "+n.getFatherToken().getContent());
            pw.flush();
            if(n.getTokenList().size()>0){
                pw.println("---");
                pw.flush();
                listprint(n,pw);
            }
        }
    }
    public void errorOutput(String type,Token s){
        if(!existLine.contains(s.getLineNumber())){
            ErrorLine e=new ErrorLine(s.getLineNumber(),type);
            error.add(e);
            existLine.add(s.getLineNumber());
        }
    }
    public Token getTokenAst(){
        return compUnit;
    }

    public void finalErrorOutput(){
        FileWriter fw=null;
        try {
            fw=new FileWriter("error.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw=new PrintWriter(fw);
        for(int i=0;i<error.size();i++){
            pw.println(error.get(i).getLinenum()+" "+error.get(i).getType());
            pw.flush();
        }
    }
}
