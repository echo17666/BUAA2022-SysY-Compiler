package LLVM;

import Datam.AstNode;
import Datam.KeyValue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Generator{
    int level=0;
    AstNode Rootast = null;
    int regId=1;
    int nowtag=0;
    ArrayList <AstNode> stack = new ArrayList<>();
    HashMap <String,AstNode> global= new HashMap();
    public Generator(AstNode ast){
       this.Rootast=ast;

    }

    public String tags(){
        String s="";
        for(int i=0;i<4*nowtag;i++){
            s+=" ";
        }
        return s;
    }


    public void generating(){
        generate(this.Rootast);
    }


    public void generate(AstNode ast){
        if(ast.getContent().equals("<ConstDef>")){ConstDef(ast);}
        else if(ast.getContent().equals("<ConstInitVal>")){ConstInitVal(ast);}
        else if(ast.getContent().equals("<ConstExp>")){ConstExp(ast);}
        else if(ast.getContent().equals("<VarDef>")){VarDef(ast);}
        else if(ast.getContent().equals("<InitVal>")){InitVal(ast);}
        else if(ast.getContent().equals("<FuncDef>")){FuncDef(ast);}
        else if(ast.getContent().equals("<FuncFParams>")){FuncFParams(ast);}
        else if(ast.getContent().equals("<FuncFParam>")){FuncFParam(ast);}
        else if(ast.getContent().equals("<MainFuncDef>")){MainFuncDef(ast);}
        else if(ast.getContent().equals("<Block>")){Block(ast);}
        else if(ast.getContent().equals("<Stmt>")){Stmt(ast);}
        else if(ast.getContent().equals("<Number>")){Number1(ast);}
        else if(ast.getContent().equals("<Exp>")){Exp(ast);}
        else if(ast.getContent().equals("<LVal>")){LVal(ast);}
        else if(ast.getContent().equals("<FuncRParams>")){FuncRParams(ast);}
        else if(ast.getContent().equals("<PrimaryExp>")){PrimaryExp(ast);}
        else if(ast.getContent().equals("<UnaryExp>")){UnaryExp(ast);}
        else if(ast.getContent().equals("<MulExp>")){AddMulExp(ast);}
        else if(ast.getContent().equals("<AddExp>")){AddMulExp(ast);}
        else{
            for(int i=0;i<ast.getChild().size();i++){
                generate(ast.getChild().get(i));
            }
        }
    }
    public void ConstDef(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        AstNode ident = a.get(0);
        KeyValue k=ident.getKey();
        if(level!=0){
            output(tags()+"%v"+this.regId+" = alloca i32\n");
            ident.setValue("%v"+this.regId);
            ident.setRegId(this.regId);
            this.regId++;
        }
        if(a.size()==3){
            k.setDim(0);
            generate(a.get(2));
            k.setIntVal(a.get(2).getKey().getIntVal());
            if(level==0){
                output("@"+ident.getContent()+" = dso_local global i32 "+k.getIntVal()+"\n");
            }
            else{
                output(tags()+"store i32 "+a.get(2).getValue()+", i32* %v"+ident.getRegId()+"\n");
            }
        }
        if(level==0){
            global.put(ident.getContent(),ident);
        }
        else{
            ident.setLevel(this.level);
            stack.add(ident);
        }
    }
    public void ConstInitVal(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        if(a.size()==1){
            generate(a.get(0));
            ast.getKey().setIntVal(a.get(0).getValue());
            ast.setValue(a.get(0).getValue());
        }
    }
    public void ConstExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        if(a.size()==1){
            generate(a.get(0));
            ast.setValue(a.get(0).getValue());
        }
    }
    public void VarDef(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        AstNode ident = a.get(0);
        KeyValue k=ident.getKey();
        if(a.size()==1||a.size()==3){
            if(level!=0){
                output(tags()+"%v"+this.regId+" = alloca i32\n");
                ident.setValue("%v"+this.regId);
                ident.setRegId(this.regId);
                this.regId++;
            }
            k.setDim(0);
            if(a.size()==3){
                generate(a.get(2));
                k.setIntVal(a.get(2).getKey().getIntVal());
                if(level!=0){
                    output(tags()+"store i32 "+a.get(2).getValue()+", i32* %v"+ident.getRegId()+"\n");
                }
            }
            else if(a.size()==1){
                k.setIntVal("0");
            }
            if(level==0){
                output("@"+ident.getContent()+" = dso_local global i32 "+k.getIntVal()+"\n");
            }



            ident.setKey(k);
            if(level==0){
                global.put(ident.getContent(),ident);
            }
            else{
                ident.setLevel(this.level);
                stack.add(ident);
            }
        }
    }
    public void InitVal(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        if(a.size()==1){
            generate(a.get(0));
            ast.getKey().setIntVal(a.get(0).getValue());
            ast.setValue(a.get(0).getValue());
        }
    }
    public void FuncDef(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        String Type = a.get(0).getChild().get(0).getContent();
        AstNode ident = a.get(1);
        if(Type.equals("int")){Type="i32";}
        else if(Type.equals("void")){Type="void";}
        ident.setReturnType(Type);
        output("define dso_local "+Type+" @"+ident.getContent());
        global.put(ident.getContent(),ident);
        if(a.get(2).getContent().equals("(")){
            output("(");
            if(a.get(4).getContent().equals(")")){
                generate(a.get(3));
                output(") {\n");
                generate(a.get(5));
            }
            else{
                output(") {\n");
                generate(a.get(4));
            }
        }
        if(Type.equals("void")){
            this.nowtag+=1;
            output(tags()+"ret void\n");
            this.nowtag-=1;
        }
        output("}\n");

    }
    public void FuncFParams(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        generate(a.get(0));
        for(int i=2;i<a.size();i+=2){
            output(", ");
            generate(a.get(i));
        }
    }
    public void FuncFParam(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        AstNode ident = a.get(1);
        ident.setLevel(1);
        if(a.size()==2){
            output("i32 %v"+this.regId);
            ident.setValue("%v"+this.regId);
            this.regId++;
            stack.add(ident);
        }

    }
    public void MainFuncDef(AstNode ast){
        output("\ndefine dso_local i32 @main() {\n");
        generate(ast.getChild().get(4));//Block
        output("}\n");
    }
    public void Block(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        for(int i=0;i<a.size();i++){
            if(a.get(i).getContent().equals("{")){
                if(level==0){
                    nowtag+=1;
                }
                level+=1;
                if(level==1){
                    for(int j=stack.size()-1;j>=0;j--){
                        if(stack.get(j).getRegId()==0&&stack.get(j).getLevel()==1){
                            output(tags()+"%v"+this.regId+" = alloca i32\n");
                            stack.get(j).setRegId(this.regId);
                            output(tags()+"store i32 "+stack.get(j).getValue()+", i32* %v"+stack.get(j).getRegId()+"\n");
                            this.regId++;
                        }
                    }
                }
            }
            else if(a.get(i).getContent().equals("}")){
                for(int j=stack.size()-1;j>=0;j--){
                    if(stack.get(j).getLevel()==this.level){stack.remove(j);}
                }
                level-=1;
                if(level==0){
                    nowtag-=1;
                }
            }
            else{generate(a.get(i));}
        }
    }
    public void Stmt(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        if(a.get(0).getContent().equals("<Block>")){generate(a.get(0));}
        if(a.get(0).getContent().equals("return")){
            if(a.get(1).getContent().equals(";")){
            }
            else{
                generate(a.get(1));//Exp
                output(tags()+"ret i32 "+a.get(1).getValue()+"\n");
            }

        }
        else if(a.get(0).getContent().equals("<LVal>")){
            generate(a.get(0));//LVal
            if(a.get(2).getContent().equals("<Exp>")){
                generate(a.get(2));//Exp
                output(tags()+"store i32 "+a.get(2).getValue()+", i32* %v"+a.get(0).getRegId()+"\n");
            }
            else if(a.get(2).getContent().equals("getint")){
                output(tags()+"%v"+this.regId+" = call i32 @getint()"+"\n");
                output(tags()+"store i32 "+"%v"+this.regId+", i32* %v"+a.get(0).getRegId()+"\n");
                this.regId++;
            }
        }
        else if(a.get(0).getContent().equals("printf")){
            int parNum=4;
            String s=a.get(2).getContent();
            for(int i=1;i<s.length()-1;i++){
                if(s.charAt(i)=='%'&&s.charAt(i+1)=='d'){
                    i++;
                    generate(a.get(parNum));
                    output(tags()+"call void @putint(i32 "+a.get(parNum).getValue()+")\n");
                    parNum+=2;
                }
                else if(s.charAt(i)=='\\'&&s.charAt(i+1)=='n'){
                    i++;
                    output(tags()+"call void @putch(i32 10)\n");
                    parNum+=2;
                }
                else{
                    output(tags()+"call void @putch(i32 "+(int) s.charAt(i)+")\n");
                }
            }
        }
    }
    public void LVal(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        AstNode ident = a.get(0);
        String identName=ident.getContent();
        int check=0;
        for(int i=stack.size()-1;i>=0;i--){
            if(stack.get(i).getContent().equals(identName)){
                if(stack.get(i).getValue().charAt(0)!='%'){ast.setValue(stack.get(i).getValue());}
                else{
                    output(tags()+"%v"+this.regId+" = load i32, i32* %v"+stack.get(i).getRegId()+"\n");
                    ast.setValue("%v"+this.regId);
                    ast.setRegId(stack.get(i).getRegId());
                    this.regId++;
                }
                check=1;
                break;
            }
        }
        if(check==0){
            if(level>=1){
                output(tags()+"%v"+this.regId+" = alloca i32"+"\n");
                ident.setValue("%v"+this.regId);
                ident.setRegId(this.regId);
                ident.setLevel(this.level);
                ast.setRegId(ident.getRegId());
                stack.add(ident);
                this.regId++;
            }
            output(tags()+"%v"+this.regId+" = load i32, i32* "+"@"+identName+"\n");
            ast.setValue("%v"+this.regId);
            this.regId++;
        }
    }
    public void Exp(AstNode ast){
        generate(ast.getChild().get(0));//AddExp
        ast.setValue(ast.getChild().get(0).getValue());
    }
    public void AddMulExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        generate(a.get(0));//AddExp
        String left=a.get(0).getValue();
        if(a.size()>1){
            for(int i=1;i<a.size();i+=2){
                String op=a.get(i).getContent();
                generate(a.get(i+1));
                String right=a.get(i+1).getValue();
                String opt=Operator(op);
                if(level>0){
                    output(tags()+"%v"+this.regId+" = "+opt+" i32 "+left+", "+right+"\n");
                    a.get(i+1).setRegId(this.regId);
                    a.get(i+1).setValue("%v"+this.regId);
                    this.regId++;
                }
                else{
                    a.get(i+1).setValue(mathCalculate(left,op,right));
                }
                left=a.get(i+1).getValue();
            }
            ast.setValue(a.get(a.size()-1).getValue());
        }
        else{
            ast.setValue(left);
        }
    }
    public void UnaryExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        if(a.get(0).getContent().equals("<UnaryOp>")){
            generate(a.get(1));//UnaryExp
            if(a.get(0).getChild().get(0).getContent().equals("-")){
                if(level>0){
                    output(tags()+"%v"+this.regId+" = sub i32 0, "+a.get(1).getValue()+"\n");
                    ast.setRegId(this.regId);
                    ast.setValue("%v"+this.regId);
                    this.regId++;
                }
                else{
                    ast.setValue(mathCalculate("0","-",a.get(1).getValue()));
                }
            }
            else if(a.get(0).getChild().get(0).getContent().equals("+")){ast.setValue(a.get(1).getValue());}
        }
        else if(a.get(0).getContent().equals("<PrimaryExp>")){
            generate(a.get(0));//PrimaryExp
            ast.setValue(a.get(0).getValue());
        }
        else if(a.size()>2&&a.get(1).getContent().equals("(")){
            AstNode ident=a.get(0);
            String identName = ident.getContent();
            AstNode identGlobe=global.get(identName);
            ident.setReturnType(identGlobe.getReturnType());

            if(a.get(2).getContent().equals(")")){
                output(tags()+"%v"+this.regId+" = call "+ident.getReturnType()+" @"+ident.getContent()+"()\n");
                ast.setValue("%v"+this.regId);
                this.regId++;
            }
            else{
                generate(a.get(2));
                output(tags()+"%v"+this.regId+" = call "+ident.getReturnType()+" @"+ident.getContent()+"("+a.get(2).getValue()+")\n");
                ast.setValue("%v"+this.regId);
                this.regId++;
            };

        }
    }
    public void FuncRParams(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        generate(a.get(0));
        String Value = ast.getValue();
        Value ="i32 "+a.get(0).getValue();
        for(int i=2;i<a.size();i+=2){
            generate(a.get(i));
        }
        for(int i=2;i<a.size();i+=2){
            Value = Value +", i32 "+a.get(i).getValue();
        }
        ast.setValue(Value);
    }
    public void PrimaryExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();

        if(a.get(0).getContent().equals("<Number>")){
            generate(a.get(0));//Number
            ast.setValue(a.get(0).getValue());
        }
        else if(a.get(0).getContent().equals("(")){
            generate(a.get(1));//Exp
            ast.setValue(a.get(1).getValue());
        }
        else if(a.get(0).getContent().equals("<LVal>")){
            generate(a.get(0));//LVal
            ast.setValue(a.get(0).getValue());
        }



    }
    public void Number1(AstNode ast){

        ArrayList<AstNode> a=ast.getChild();
        ast.setValue(a.get(0).getContent());
    }








    public String Operator(String op){
        String opt="";
        switch(op){
            case "+": opt="add";break;
            case "-": opt="sub";break;
            case "*": opt="mul";break;
            case "/": opt="sdiv";break;
            case "%": opt="srem";break;
        }
        return opt;
    }
    public String mathCalculate(String left,String op,String right){
        int a=Integer.parseInt(left);
        int b=Integer.parseInt(right);
        int ans=0;
        switch(op){
            case "+":ans=a+b;break;
            case "-":ans=a-b;break;
            case "*":ans=a*b;break;
            case "/":ans=a/b;break;
            case "%":ans=a%b;break;
        }
        return ans+"";
    }
    public void init(){
        FileWriter fw=null;
        try {
            fw=new FileWriter("llvm_ir.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw=new PrintWriter(fw);
        pw.println("declare i32 @getint()");
        pw.flush();
        pw.println("declare void @putint(i32)");
        pw.flush();
        pw.println("declare void @putch(i32)");
        pw.flush();
        pw.println("declare void @putstr(i8*)");
        pw.flush();
    }
    public void output(String str){
        FileWriter fw=null;
        try {
            fw=new FileWriter("llvm_ir.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw=new PrintWriter(fw);
        pw.print(str);
        pw.flush();
    }
}
