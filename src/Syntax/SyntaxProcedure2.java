package Syntax;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import Datam.AstNode;
import Datam.Token;

public class SyntaxProcedure2{
    static HashMap<String,String> ReservedCharacter = new HashMap<String,String>();
    public ArrayList <Token> bank;
    AstNode RootAst = new AstNode("<CompUnit>");
    int current=0;
    String sym="";
    public SyntaxProcedure2(ArrayList<Token> bank){
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
        if(str.matches("^[_a-zA-Z0-9]+$")&&!ReservedCharacter.containsKey(str)){return true;}
        else{return false;}
    }
    public boolean isLVal(){
        int j=this.current;
        int check=0;
        for(;j<this.bank.size()&&check==0;j++){
            if(check==0&&bank.get(j).getContent().equals("=")){return true;}
            if(bank.get(j).getContent().equals(";")){check=1;}
        }
        return false;
    }
    public void nextsym(AstNode ast){
        output(this.sym);
        ast.addNode(new AstNode(this.sym));
        if(this.current<bank.size()-1){
            this.current+=1;
            this.sym=bank.get(this.current).getContent();
        }
    }
    public String getnextsym(){
        return bank.get(this.current+1).getContent();
    }
    public String getbeforesym(){return bank.get(this.current-1).getContent();}
    public String getnextnextsym(){
        return bank.get(this.current+2).getContent();
    }
    public void analyze(){

        CompUnit(RootAst);
        outputAst(RootAst);
    }

    public void CompUnit(AstNode ast){

        while(sym.equals("const")||sym.equals("int")&&isIdent(getnextsym())&&!getnextnextsym().equals("(")){
            if(sym.equals("const")){ConstDecl(ast);}
            else{VarDecl(ast);}
        }
        while(sym.equals("void")||sym.equals("int")&&isIdent(getnextsym())&&getnextnextsym().equals("(")){FuncDef(ast);}
        if(sym.equals("int")&&getnextsym().equals("main")){MainFuncDef(ast);}
        else{}
        output("<CompUnit>");
    }
    public void ConstDecl(AstNode ast){
        AstNode a =new AstNode("<ConstDecl>");
        if(sym.equals("const")){nextsym(a);
            if(sym.equals("int")){nextsym(a);ConstDef(a);
                while(sym.equals(",")){nextsym(a);ConstDef(a);}
                if(sym.equals(";")){nextsym(a);}
                else{}
            }
            else{}
        }
        else{}
        ast.addNode(a);
        output("<ConstDecl>");
    }
    public void ConstDef(AstNode ast){
        AstNode a =new AstNode("<ConstDef>");
        if(isIdent(sym)){nextsym(a);
            while(sym.equals("[")){nextsym(a);ConstExp(a);
                if(sym.equals("]")){nextsym(a);}
                else{}
            }
            if(sym.equals("=")){nextsym(a);ConstInitVal(a);
                }
            else{}
        }
        else{}
        ast.addNode(a);
        output("<ConstDef>");
    }
    public void ConstInitVal(AstNode ast){
        AstNode a =new AstNode("<ConstInitVal>");
        //判断(、ident/number/+/-/!
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){ConstExp(a);}
        else if(sym.equals("{")){nextsym(a);
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)||sym.equals("{")){ConstInitVal(a);
                while(sym.equals(",")){nextsym(a);ConstInitVal(a);}
            }
            if(sym.equals("}")){nextsym(a);}
        }
        else{}
        ast.addNode(a);
        output("<ConstInitVal>");
    }
    public void VarDecl(AstNode ast){
        AstNode a =new AstNode("<VarDecl>");
        if(sym.equals("int")){nextsym(a);VarDef(a);
            while(sym.equals(",")){nextsym(a);VarDef(a);}
            if(sym.equals(";")){nextsym(a);}
            else{}
        }
        else{}
        ast.addNode(a);
        output("<VarDecl>");
    }
    public void VarDef(AstNode ast){
        AstNode a =new AstNode("<VarDef>");
        if(isIdent(sym)){nextsym(a);
            while(sym.equals("[")){nextsym(a);ConstExp(a);
                if(sym.equals("]")){nextsym(a);}
                else{}
            }
            if(sym.equals("=")){nextsym(a);InitVal(a);}
        }
        else{}
        ast.addNode(a);
        output("<VarDef>");
    }
    public void InitVal(AstNode ast){
        AstNode a =new AstNode("<InitVal>");
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp(a);}
        else if(sym.equals("{")){nextsym(a);
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)||sym.equals("{")){InitVal(a);
                while(sym.equals(",")){nextsym(a);InitVal(a);}
            }
            if(sym.equals("}")){nextsym(a);}
        }
        else{}
        ast.addNode(a);
        output("<InitVal>");
    }
    public void FuncDef(AstNode ast){
        AstNode a =new AstNode("<FuncDef>");
        if(sym.equals("int")||sym.equals("void")){FuncType(a);
            if(isIdent(sym)){nextsym(a);
                if(sym.equals("(")){nextsym(a);
                    if(sym.equals("int")){FuncFParams(a);}
                    if(sym.equals(")")){nextsym(a);Block(a);}
                    else{}
                }
                else{}
            }
            else{}
        }
        else{}
        ast.addNode(a);
        output("<FuncDef>");
    }
    public void MainFuncDef(AstNode ast){
        AstNode a =new AstNode("<MainFuncDef>");
        if(sym.equals("int")){nextsym(a);
            if(sym.equals("main")&&getnextsym().equals("(")&&getnextnextsym().equals(")")){nextsym(a);nextsym(a);nextsym(a);Block(a);}
            else{}
        }
        else{}
        ast.addNode(a);
        output("<MainFuncDef>");
    }
    public void FuncType(AstNode ast){
        AstNode a =new AstNode("<FuncType>");
        if(sym.equals("void")){nextsym(a);}
        else if(sym.equals("int")){nextsym(a);}
        else{}
        ast.addNode(a);
        output("<FuncType>");
    }
    public void FuncFParams(AstNode ast){
        AstNode a =new AstNode("<FuncFParams>");
        if(sym.equals("int")){FuncFParam(a);
            while(sym.equals(",")){nextsym(a);FuncFParam(a);}
        }
        else{}
        ast.addNode(a);
        output("<FuncFParams>");
    }
    public void FuncFParam(AstNode ast){
        AstNode a =new AstNode("<FuncFParam>");
        if(sym.equals("int")){nextsym(a);
            if(isIdent(sym)){nextsym(a);
                if(sym.equals("[")){nextsym(a);
                    if(sym.equals("]")){nextsym(a);
                        if(sym.equals("[")){nextsym(a);ConstExp(a);
                            if(sym.equals("]")){nextsym(a);}
                            else{}
                        }
                    }
                    else{}
                }
            }
            else{}
        }
        else{}
        ast.addNode(a);
        output("<FuncFParam>");
    }
    public void Block(AstNode ast){
        AstNode a =new AstNode("<Block>");
        if(sym.equals("{")){nextsym(a);
            while(sym.equals("const")||sym.equals("int")||isIdent(sym)||sym.equals(";")||sym.equals("if")||sym.equals("while")||sym.equals("break")||sym.equals("continue")||sym.equals("return")||sym.equals("printf")||
                    sym.equals("{")||sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isNumber(sym)){BlockItem(a);}
            if(sym.equals("}")){nextsym(a);}
            else{}
        }
        else{}
        ast.addNode(a);
        output("<Block>");
    }
    public void BlockItem(AstNode ast){
        if(sym.equals("const")||sym.equals("int")){
            if(sym.equals("const")){ConstDecl(ast);}
            else{VarDecl(ast);}
        }
        else if(isIdent(sym)||sym.equals(";")||sym.equals("if")||sym.equals("while")||sym.equals("continue")||sym.equals("break")||sym.equals("return")||sym.equals("printf")||
                sym.equals("{")||sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isNumber(sym)){Stmt(ast);}
        else{}
    }
    public void Stmt(AstNode ast){
        AstNode a =new AstNode("<Stmt>");
        if(sym.equals("{")){Block(a);}
        else if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isNumber(sym)){Exp(a);
            if(sym.equals(";")){nextsym(a);}
            else{}
        }
        else if(sym.equals(";")){nextsym(a);}
        else if(sym.equals("if")){nextsym(a);
            if(sym.equals("(")){nextsym(a);Cond(a);
                if(sym.equals(")")){nextsym(a);Stmt(a);
                    if(sym.equals("else")){nextsym(a);Stmt(a);}
                }
                else{}
            }
            else{}
        }
        else if(sym.equals("while")){nextsym(a);
            if(sym.equals("(")){nextsym(a);Cond(a);
                if(sym.equals(")")){nextsym(a);Stmt(a);}
                else{}
            }
            else{}
        }
        else if(sym.equals("break")){nextsym(a);
            if(sym.equals(";")){nextsym(a);}
            else{}
        }
        else if(sym.equals("continue")){nextsym(a);
            if(sym.equals(";")){nextsym(a);}
            else{}
        }
        else if(sym.equals("return")){nextsym(a);
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp(a);}
            if(sym.equals(";")){nextsym(a);}
            else{}
        }
        else if(sym.equals("printf")){nextsym(a);
            if(sym.equals("(")){nextsym(a);nextsym(a);
                while(sym.equals(",")){nextsym(a);Exp(a);}
                if(sym.equals(")")){nextsym(a);
                    if(sym.equals(";")){nextsym(a);}
                    else{}
                }
                else{}
            }
            else{}
        }
        else if(isIdent(sym)){
            if(isLVal()){LVal(a);
                if(sym.equals("=")){nextsym(a);
                    if(sym.equals("getint")){nextsym(a);
                        if(sym.equals("(")){nextsym(a);
                            if(sym.equals(")")){nextsym(a);
                                if(sym.equals(";")){nextsym(a);}
                                else{}
                            }
                            else{}
                        }
                        else{}
                    }
                    else if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp(a);
                        if(sym.equals(";")){nextsym(a);}
                        else{}
                    }
                    else{}
                }
                else{}
            }
            else{Exp(a);
                if(sym.equals(";")){nextsym(a);}
                else{}
            }
        }
        else{}
        ast.addNode(a);
        output("<Stmt>");
    }
    public void Exp(AstNode ast){
        AstNode a =new AstNode("<Exp>");
        AddExp(a);
        ast.addNode(a);
        output("<Exp>");
    }
    public void Cond(AstNode ast){
        AstNode a =new AstNode("<Cond>");
        LOrExp(a);
        ast.addNode(a);
        output("<Cond>");
    }
    public void LVal(AstNode ast){
        AstNode a =new AstNode("<LVal>");
        if(isIdent(sym)){nextsym(a);
            while(sym.equals("[")){nextsym(a);Exp(a);
                if(sym.equals("]")){nextsym(a);}
                else{}
            }
        }
        else{}
        ast.addNode(a);
        output("<LVal>");
    }
    public void PrimaryExp(AstNode ast){
        AstNode a =new AstNode("<PrimaryExp>");
        if(sym.equals("(")){nextsym(a);Exp(a);
            if(sym.equals(")")){nextsym(a);}
            else{}
        }
        else if(isNumber(sym)){Number1(a);}
        else if(isIdent(sym)){LVal(a);}
        else{}
        ast.addNode(a);
        output("<PrimaryExp>");
    }
    public void Number1(AstNode ast){
        AstNode a =new AstNode("<Number>");
        if(isNumber(sym)){nextsym(a);}
        ast.addNode(a);
        output("<Number>");
    }
    public void UnaryExp(AstNode ast){
        AstNode a =new AstNode("<UnaryExp>");
        if(sym.equals("+")||sym.equals("-")||sym.equals("!")){UnaryOp(a);UnaryExp(a);}
        else if(sym.equals("(")||isNumber(sym)){PrimaryExp(a);}
        else if(isIdent(sym)){
            if(getnextsym().equals("(")){nextsym(a);nextsym(a);
                if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){FuncRParams(a);}
                if(sym.equals(")")){nextsym(a);}
                else{}
            }
            else{PrimaryExp(a);}
        }
        else{}
        ast.addNode(a);
        output("<UnaryExp>");
    }

    public void UnaryOp(AstNode ast){
        AstNode a =new AstNode("<UnaryOp>");
        if(sym.equals("!")||sym.equals("+")||sym.equals("-")){nextsym(a);}
        else{}
        ast.addNode(a);
        output("<UnaryOp>");
    }
    public void FuncRParams(AstNode ast){
        AstNode a =new AstNode("<FuncRParams>");
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp(a);}
        while(sym.equals(",")){nextsym(a);Exp(a);}
        ast.addNode(a);
        output("<FuncRParams>");
    }
    public void MulExp(AstNode ast){
        AstNode a =new AstNode("<MulExp>");
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){UnaryExp(a);
            while(sym.equals("*")||sym.equals("/")||sym.equals("%")){
                AstNode tmp=a.changeNode();
                AstNode b =new AstNode("<MulExp>");
                b.addNode(tmp);
                a.addNode(b);
                output("<MulExp>");
                nextsym(a);UnaryExp(a);
                if(getbeforesym().equals("*")||getbeforesym().equals("/")||getbeforesym().equals("%")){
                    AstNode tmp1=a.changeNode();
                    AstNode b1 =new AstNode("<MulExp>");
                    b1.addNode(tmp1);
                    a.addNode(b1);
                    output("<MulExp>");
                }
            }
        }
        else{}
        ast.addNode(a);
        output("<MulExp>");
    }
    public void AddExp(AstNode ast){
        AstNode a =new AstNode("<AddExp>");
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){MulExp(a);
            while(sym.equals("+")||sym.equals("-")){
                AstNode tmp=a.changeNode();
                AstNode b =new AstNode("<AddExp>");
                b.addNode(tmp);
                a.addNode(b);
                output("<AddExp>");
                nextsym(a);MulExp(a);
                if(getbeforesym().equals("+")||getbeforesym().equals("-")){
                    AstNode tmp1=a.changeNode();
                    AstNode b1 =new AstNode("<AddExp>");
                    b1.addNode(tmp1);
                    a.addNode(b1);
                    output("<AddExp>");
                }
            }
        }
        else{}
        ast.addNode(a);
        output("<AddExp>");
    }
    public void RelExp(AstNode ast){
        AstNode a =new AstNode("<RelExp>");
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){AddExp(a);
            while(sym.equals("<")||sym.equals(">")||sym.equals("<=")||sym.equals(">=")){
                AstNode tmp=a.changeNode();
                AstNode b =new AstNode("<RelExp>");
                b.addNode(tmp);
                a.addNode(b);
                output("<RelExp>");
                nextsym(a);AddExp(a);
                if(getbeforesym().equals("<")||getbeforesym().equals(">")||getbeforesym().equals("<=")||getbeforesym().equals(">=")){
                    AstNode tmp1=a.changeNode();
                    AstNode b1 =new AstNode("<RelExp>");
                    b1.addNode(tmp1);
                    a.addNode(b1);
                    output("<RelExp>");
                }
            }
        }
        else{}
        ast.addNode(a);
        output("<RelExp>");
    }
    public void EqExp(AstNode ast){
        AstNode a =new AstNode("<EqExp>");
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){RelExp(a);
            while(sym.equals("==")||sym.equals("!=")){
                AstNode tmp=a.changeNode();
                AstNode b =new AstNode("<EqExp>");
                b.addNode(tmp);
                a.addNode(b);
                output("<EqExp>");
                nextsym(a);RelExp(a);
                if(getbeforesym().equals("==")||getbeforesym().equals("!=")){
                    AstNode tmp1=a.changeNode();
                    AstNode b1 =new AstNode("<EqExp>");
                    b1.addNode(tmp1);
                    a.addNode(b1);
                    output("<EqExp>");
                }
            }
        }
        else{}
        ast.addNode(a);
        output("<EqExp>");
    }
    public void LAndExp(AstNode ast){
        AstNode a =new AstNode("<LAndExp>");
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){EqExp(a);
            while(sym.equals("&&")){
                AstNode tmp=a.changeNode();
                AstNode b =new AstNode("<LAndExp>");
                b.addNode(tmp);
                a.addNode(b);
                output("<LAndExp>");
                nextsym(a);EqExp(a);
                if(getbeforesym().equals("&&")){
                    AstNode tmp1=a.changeNode();
                    AstNode b1 =new AstNode("<LAndExp>");
                    b1.addNode(tmp1);
                    a.addNode(b1);
                    output("<LAndExp>");
                }
            }
        }
        else{}
        ast.addNode(a);
        output("<LAndExp>");
    }
    public void LOrExp(AstNode ast){
        AstNode a =new AstNode("<LOrExp>");
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){LAndExp(a);
            while(sym.equals("||")){
                AstNode tmp=a.changeNode();
                AstNode b =new AstNode("<LOrExp>");
                b.addNode(tmp);
                a.addNode(b);
                output("<LOrExp>");
                nextsym(a);LAndExp(a);
                if(getbeforesym().equals("||")){
                    AstNode tmp1=a.changeNode();
                    AstNode b1 =new AstNode("<LOrExp>");
                    b1.addNode(tmp1);
                    a.addNode(b1);
                    output("<LOrExp>");
                }
            }
        }
        else{}
        ast.addNode(a);
        output("<LOrExp>");
    }
    public void ConstExp(AstNode ast){
        AstNode a =new AstNode("<ConstExp>");
        AddExp(a);
        ast.addNode(a);
        output("<ConstExp>");
    }
    public void output(String sym){
//        FileWriter fw =null;
//        try {
//            fw=new FileWriter("output.txt", true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        PrintWriter pw = new PrintWriter(fw);
//        if(sym.charAt(0)=='<'&&sym.length()>1&&sym.charAt(1)-'A'>=0&&sym.charAt(1)-'A'<26){
//            pw.println(sym);
//            pw.flush();
//        }
//        else if(ReservedCharacter.containsKey(sym)){
//            pw.println(ReservedCharacter.get(sym)+" "+sym);
//            pw.flush();
//        }
//        else{
//            if(sym.charAt(0)=='"'){
//                pw.println("STRCON "+sym);
//                pw.flush();
//            }
//            else if(isNumber(sym)){
//                pw.println("INTCON "+sym);
//                pw.flush();
//            }
//            else{
//                pw.println("IDENFR "+sym);
//                pw.flush();
//            }
//        }
    }
    public void outputAst(AstNode ast){
        FileWriter fw =null;
        try {
            fw=new FileWriter("output.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        if(ast.getChild().size()!=0){
            for(int i=0;i<ast.getChild().size();i++){
                outputAst(ast.getChild().get(i));
            }
            pw.println(ast.getContent());
            pw.flush();
        }
        else{
            if(ReservedCharacter.containsKey(ast.getContent())){
            pw.println(ReservedCharacter.get(ast.getContent())+" "+ast.getContent());
            pw.flush();
        }
        else{
            if(ast.getContent().charAt(0)=='"'){
                pw.println("STRCON "+ast.getContent());
                pw.flush();
            }
            else if(isNumber(ast.getContent())){
                pw.println("INTCON "+ast.getContent());
                pw.flush();
            }
            else{
                pw.println("IDENFR "+ast.getContent());
                pw.flush();
            }
            }
        }

    }
    public AstNode getAst(){
        return this.RootAst;
    }
}
