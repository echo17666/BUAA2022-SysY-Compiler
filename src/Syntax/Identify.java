package Syntax;

import java.util.ArrayList;
import java.util.HashMap;
import Datam.Token;

public class Identify{
    static HashMap<String,String> ReservedCharacter = new HashMap<String,String>();
    public ArrayList <Token> bank;
    int current=0;
    String sym="";
    public Identify(ArrayList<Token> bank){
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
    public void nextsym(){
        output(this.sym);
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
        CompUnit();
    }

    public void CompUnit(){
        while(sym.equals("const")||sym.equals("int")&&isIdent(getnextsym())&&!getnextnextsym().equals("(")){
            if(sym.equals("const")){ConstDecl();}
            else{VarDecl();}
        }
        while(sym.equals("void")||sym.equals("int")&&isIdent(getnextsym())&&getnextnextsym().equals("(")){FuncDef();}
        if(sym.equals("int")&&getnextsym().equals("main")){MainFuncDef();}
        else{}
        System.out.println("<CompUnit>");
    }
    public void ConstDecl(){
        if(sym.equals("const")){nextsym();
            if(sym.equals("int")){nextsym();ConstDef();
                while(sym.equals(",")){nextsym();ConstDef();}
                if(sym.equals(";")){nextsym();}
                else{}
            }
            else{}
        }
        else{}
        System.out.println("<ConstDecl>");
    }
    public void ConstDef(){
        if(isIdent(sym)){nextsym();
            while(sym.equals("[")){nextsym();ConstExp();
                if(sym.equals("]")){nextsym();}
                else{}
            }
            if(sym.equals("=")){nextsym();ConstInitVal();
                System.out.println("<ConstDef>");}
            else{}
        }
        else{}
    }
    public void ConstInitVal(){
        //判断(、ident/number/+/-/!
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){ConstExp();}
        else if(sym.equals("{")){nextsym();
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)||sym.equals("{")){ConstInitVal();
                while(sym.equals(",")){nextsym();ConstInitVal();}
            }
            if(sym.equals("}")){nextsym();}
        }
        else{}
        System.out.println("<ConstInitVal>");
    }
    public void VarDecl(){
        if(sym.equals("int")){nextsym();VarDef();
            while(sym.equals(",")){nextsym();VarDef();}
            if(sym.equals(";")){nextsym();
                System.out.println("<VarDecl>");}
            else{}
        }
        else{}
    }
    public void VarDef(){
        if(isIdent(sym)){nextsym();
            while(sym.equals("[")){nextsym();ConstExp();
                if(sym.equals("]")){nextsym();}
                else{}
            }
            if(sym.equals("=")){nextsym();InitVal();}
            System.out.println("<VarDef>");
        }
        else{}
    }
    public void InitVal(){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp();}
        else if(sym.equals("{")){nextsym();
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)||sym.equals("{")){InitVal();
                while(sym.equals(",")){nextsym();InitVal();}
            }
            if(sym.equals("}")){nextsym();}
        }
        else{}
        System.out.println("<InitVal>");
    }
    public void FuncDef(){
        if(sym.equals("int")||sym.equals("void")){FuncType();
            if(isIdent(sym)){nextsym();
                if(sym.equals("(")){nextsym();
                    if(sym.equals("int")){FuncFParams();}
                    if(sym.equals(")")){nextsym();Block();}
                    else{}
                }
                else{}
            }
            else{}
        }
        else{}
        System.out.println("<FuncDef>");
    }
    public void MainFuncDef(){
        if(sym.equals("int")){nextsym();
            if(sym.equals("main")&&getnextsym().equals("(")&&getnextnextsym().equals(")")){nextsym();nextsym();nextsym();Block();}
            else{}
        }
        else{}
        System.out.println("<MainFuncDef>");
    }
    public void FuncType(){
        if(sym.equals("void")){nextsym();}
        else if(sym.equals("int")){nextsym();}
        else{}
        System.out.println("<FuncType>");
    }
    public void FuncFParams(){
        if(sym.equals("int")){FuncFParam();
            while(sym.equals(",")){nextsym();FuncFParam();}
        }
        else{}
        System.out.println("<FuncFParams>");
    }
    public void FuncFParam(){
        if(sym.equals("int")){nextsym();
            if(isIdent(sym)){nextsym();
                if(sym.equals("[")){nextsym();
                    if(sym.equals("]")){nextsym();
                        if(sym.equals("[")){nextsym();ConstExp();
                            if(sym.equals("]")){nextsym();}
                            else{}
                        }
                    }
                    else{}
                }
            }
            else{}
        }
        else{}
        System.out.println("<FuncFParam>");
    }
    public void Block(){
        if(sym.equals("{")){nextsym();
            while(sym.equals("const")||sym.equals("int")||isIdent(sym)||sym.equals(";")||sym.equals("if")||sym.equals("while")||sym.equals("break")||sym.equals("continue")||sym.equals("return")||sym.equals("printf")||
                    sym.equals("{")||sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isNumber(sym)){BlockItem();}
            if(sym.equals("}")){nextsym();}
            else{}
        }
        else{}
        System.out.println("<Block>");
    }
    public void BlockItem(){
        if(sym.equals("const")||sym.equals("int")){
            if(sym.equals("const")){ConstDecl();}
            else{VarDecl();}
        }
        else if(isIdent(sym)||sym.equals(";")||sym.equals("if")||sym.equals("while")||sym.equals("continue")||sym.equals("break")||sym.equals("return")||sym.equals("printf")||
                sym.equals("{")||sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isNumber(sym)){Stmt();}
        else{}
    }
    public void Stmt(){
        if(sym.equals("{")){Block();}
        else if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isNumber(sym)){Exp();
            if(sym.equals(";")){nextsym();}
            else{}
        }
        else if(sym.equals(";")){nextsym();}
        else if(sym.equals("if")){nextsym();
            if(sym.equals("(")){nextsym();Cond();
                if(sym.equals(")")){nextsym();Stmt();
                    if(sym.equals("else")){nextsym();Stmt();}
                }
                else{}
            }
            else{}
        }
        else if(sym.equals("while")){nextsym();
            if(sym.equals("(")){nextsym();Cond();
                if(sym.equals(")")){nextsym();Stmt();}
                else{}
            }
            else{}
        }
        else if(sym.equals("break")){nextsym();
            if(sym.equals(";")){nextsym();}
            else{}
        }
        else if(sym.equals("continue")){nextsym();
            if(sym.equals(";")){nextsym();}
            else{}
        }
        else if(sym.equals("return")){nextsym();
            if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp();}
            if(sym.equals(";")){nextsym();}
            else{}
        }
        else if(sym.equals("printf")){nextsym();
            if(sym.equals("(")){nextsym();nextsym();
                while(sym.equals(",")){nextsym();Exp();}
                if(sym.equals(")")){nextsym();
                    if(sym.equals(";")){nextsym();}
                    else{}
                }
                else{}
            }
            else{}
        }
        else if(isIdent(sym)){
            if(isLVal()){LVal();
                if(sym.equals("=")){nextsym();
                    if(sym.equals("getint")){nextsym();
                        if(sym.equals("(")){nextsym();
                            if(sym.equals(")")){nextsym();
                                if(sym.equals(";")){nextsym();}
                                else{}
                            }
                            else{}
                        }
                        else{}
                    }
                    else if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp();
                        if(sym.equals(";")){nextsym();}
                        else{}
                    }
                    else{}
                }
                else{}
            }
            else{Exp();
                if(sym.equals(";")){nextsym();}
                else{}
            }
        }
        else{}
        System.out.println("<Stmt>");
    }
    public void Exp(){
        AddExp();
        System.out.println("<Exp>");
    }
    public void Cond(){
        LOrExp();
        System.out.println("<Cond>");
    }
    public void LVal(){
        if(isIdent(sym)){nextsym();
            while(sym.equals("[")){nextsym();Exp();
                if(sym.equals("]")){nextsym();}
                else{}
            }
        }
        else{}
        System.out.println("<LVal>");
    }
    public void PrimaryExp(){
        if(sym.equals("(")){nextsym();Exp();
            if(sym.equals(")")){nextsym();}
            else{}
        }
        else if(isNumber(sym)){Number1();}
        else if(isIdent(sym)){LVal();}
        else{}
        System.out.println("<PrimaryExp>");
    }
    public void Number1(){
        if(isNumber(sym)){nextsym();}
        System.out.println("<Number>");
    }
    public void UnaryExp(){
        if(sym.equals("+")||sym.equals("-")||sym.equals("!")){UnaryOp();UnaryExp1();}
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
        System.out.println("<UnaryExp>");
        if(sym.equals("*")||sym.equals("/")||sym.equals("%")){
            System.out.println("<MulExp>");
        }

    }
    public void UnaryExp1(){
        if(sym.equals("+")||sym.equals("-")||sym.equals("!")){UnaryOp();UnaryExp1();}
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
        System.out.println("<UnaryExp>");
    }
    public void UnaryOp(){
        if(sym.equals("!")||sym.equals("+")||sym.equals("-")){nextsym();}
        else{}
        System.out.println("<UnaryOp>");
    }
    public void FuncRParams(){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){Exp();}
        while(sym.equals(",")){nextsym();Exp();}
        System.out.println("<FuncRParams>");
    }
    public void MulExp(){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){UnaryExp();
            while(sym.equals("*")||sym.equals("/")||sym.equals("%")){nextsym();UnaryExp();}
        }
        else{}
        System.out.println("<MulExp>");
        if(sym.equals("+")||sym.equals("-")){
            System.out.println("<AddExp>");
        }


    }
    public void AddExp(){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){MulExp();
            while(sym.equals("+")||sym.equals("-")){nextsym();MulExp();}
        }
        else{}
        System.out.println("<AddExp>");
        if(sym.equals("<")||sym.equals(">")||sym.equals("<=")||sym.equals(">=")){
            System.out.println("<RelExp>");
        }



    }
    public void RelExp(){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){AddExp();
            while(sym.equals("<")||sym.equals(">")||sym.equals("<=")||sym.equals(">=")){nextsym();AddExp();}
        }
        else{}
        System.out.println("<RelExp>");
        if(sym.equals("==")||sym.equals("!=")){
            System.out.println("<EqExp>");
        }



    }
    public void EqExp(){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){RelExp();
            while(sym.equals("==")||sym.equals("!=")){nextsym();RelExp();}
        }
        else{}
        System.out.println("<EqExp>");
        if(sym.equals("&&")){
            System.out.println("<LAndExp>");
        }



    }
    public void LAndExp(){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){EqExp();
            while(sym.equals("&&")){nextsym();EqExp();}
        }
        else{}
        System.out.println("<LAndExp>");
        if(sym.equals("||")){
            System.out.println("<LOrExp>");
        }



    }
    public void LOrExp(){
        if(sym.equals("(")||sym.equals("+")||sym.equals("-")||sym.equals("!")||isIdent(sym)||isNumber(sym)){LAndExp();
            while(sym.equals("||")){nextsym();LAndExp();}
        }
        else{}
        System.out.println("<LOrExp>");
    }
    public void ConstExp(){AddExp();
        System.out.println("<ConstExp>");
    }


    public void output(String sym){
            if(ReservedCharacter.containsKey(sym)){
                System.out.println(ReservedCharacter.get(sym)+" "+sym);
            }
            else{
                if(sym.charAt(0)=='"'){
                    System.out.println("STRCON "+sym);
                }
                else if(isNumber(sym)){
                    System.out.println("INTCON "+sym);
                }
                else{
                    System.out.println("IDENFR "+sym);
                }
            }

    }
}
