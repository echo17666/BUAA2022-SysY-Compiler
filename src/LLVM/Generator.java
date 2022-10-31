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
        else if(ast.getContent().equals("<Cond>")){Cond(ast);}
        else if(ast.getContent().equals("<LVal>")){LVal(ast);}
        else if(ast.getContent().equals("<FuncRParams>")){FuncRParams(ast);}
        else if(ast.getContent().equals("<PrimaryExp>")){PrimaryExp(ast);}
        else if(ast.getContent().equals("<UnaryExp>")){UnaryExp(ast);}
        else if(ast.getContent().equals("<MulExp>")){AddMulExp(ast);}
        else if(ast.getContent().equals("<AddExp>")){AddMulExp(ast);}
        else if(ast.getContent().equals("<RelExp>")){RelEqExp(ast);}
        else if(ast.getContent().equals("<EqExp>")){RelEqExp(ast);}
        else if(ast.getContent().equals("<LAndExp>")){LAndExp(ast);}
        else if(ast.getContent().equals("<LOrExp>")){LOrExp(ast);}
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
            ident.setRegId("%v"+this.regId);
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
                output(tags()+"store i32 "+a.get(2).getValue()+", i32* "+ident.getRegId()+"\n");
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
                ident.setRegId("%v"+this.regId);
                this.regId++;
            }
            k.setDim(0);
            if(a.size()==3){
                generate(a.get(2));
                k.setIntVal(a.get(2).getKey().getIntVal());
                if(level!=0){
                    output(tags()+"store i32 "+a.get(2).getValue()+", i32* "+ident.getRegId()+"\n");
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
                        if(stack.get(j).getRegId().equals("")&&stack.get(j).getLevel()==1){
                            output(tags()+"%v"+this.regId+" = alloca i32\n");
                            stack.get(j).setRegId("%v"+this.regId);
                            output(tags()+"store i32 "+stack.get(j).getValue()+", i32* "+stack.get(j).getRegId()+"\n");
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
            else{
                a.get(i).setContinueId(ast.getContinueId());
                a.get(i).setBreakId(ast.getBreakId());
                generate(a.get(i));
            }
        }
    }
    public void Stmt(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        if(a.get(0).getContent().equals("<Block>")){
            a.get(0).setContinueId(ast.getContinueId());
            a.get(0).setBreakId(ast.getBreakId());
            generate(a.get(0));
        }
        else if(a.get(0).getContent().equals("return")){
            if(a.get(1).getContent().equals(";")){
                output(tags()+"ret void\n");
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
                output(tags()+"store i32 "+a.get(2).getValue()+", i32* "+a.get(0).getRegId()+"\n");
            }
            else if(a.get(2).getContent().equals("getint")){
                output(tags()+"%v"+this.regId+" = call i32 @getint()"+"\n");
                output(tags()+"store i32 "+"%v"+this.regId+", i32* "+a.get(0).getRegId()+"\n");
                this.regId++;
            }
        }
        else if(a.get(0).getContent().equals("<Exp>")){generate(a.get(0));}
        else if(a.get(0).getContent().equals("printf")){
            int parNum=4;
            String s=a.get(2).getContent();
            for(int i=1;i<s.length()-1;i++){
                if(s.charAt(i)=='%'&&s.charAt(i+1)=='d'){
                    i++;
                    generate(a.get(parNum));
                    parNum+=2;
                }
            }
            parNum=4;
            for(int i=1;i<s.length()-1;i++){
                if(s.charAt(i)=='%'&&s.charAt(i+1)=='d'){
                    i++;
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
        else if(a.get(0).getContent().equals("if")){
            output(tags()+"br label %v"+this.regId+"\n");
            output("\nv"+this.regId+":\n");
            a.get(2).setYesId(this.regId+1);
            int YesId = this.regId+1;
            int NoId=0;
            int StmtId=0;
            if(a.size()>5){
                a.get(2).setNoId(this.regId+2);
                a.get(2).setStmtId(this.regId+3);
                a.get(4).setStmtId(this.regId+3);
                a.get(4).setContinueId(ast.getContinueId());
                a.get(4).setBreakId(ast.getBreakId());
                a.get(6).setStmtId(this.regId+3);
                a.get(6).setContinueId(ast.getContinueId());
                a.get(6).setBreakId(ast.getBreakId());
                NoId = this.regId+2;
                StmtId = this.regId+3;
                this.regId+=4;
            }
            else{
                a.get(2).setNoId(this.regId+2);
                a.get(2).setStmtId(this.regId+2);
                a.get(4).setStmtId(this.regId+2);
                a.get(4).setContinueId(ast.getContinueId());
                a.get(4).setBreakId(ast.getBreakId());
                StmtId = this.regId+2;
                this.regId+=3;
            }
            generate(a.get(2));
            output("\nv"+YesId+":\n");
            generate(a.get(4));
            if(a.size()>5){
                output("\nv"+NoId+":\n");
                generate(a.get(6));
            }
            output("\nv"+StmtId+":\n");
        }
        else if(a.get(0).getContent().equals("while")){
            output(tags()+"br label %v"+this.regId+"\n");
            output("\nv"+this.regId+":\n");
            int YesId = this.regId+1;
            int NoId=this.regId+2;
            int StmtId=this.regId+2;
            a.get(2).setYesId(this.regId+1);
            a.get(2).setNoId(this.regId+2);
            a.get(2).setStmtId(this.regId+2);
            a.get(4).setStmtId(this.regId);
            a.get(4).setBreakId(this.regId+2);
            a.get(4).setContinueId(this.regId);
            this.regId+=3;
            generate(a.get(2));
            output("\nv"+YesId+":\n");
            generate(a.get(4));
            output("\nv"+StmtId+":\n");
        }
        else if(a.get(0).getContent().equals("break")){
            ast.setStmtId(ast.getBreakId());
        }
        else if(a.get(0).getContent().equals("continue")){ast.setStmtId(ast.getContinueId());}
        if(ast.getStmtId()!=0){
            output(tags()+"br label %v"+ast.getStmtId()+"\n");
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
                    output(tags()+"%v"+this.regId+" = load i32, i32* "+stack.get(i).getRegId()+"\n");
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
//                output(tags()+"%v"+this.regId+" = alloca i32"+"\n");
//                ident.setValue("%v"+this.regId);
//                ident.setRegId(this.regId);
//                ident.setLevel(this.level);
//                ast.setRegId(ident.getRegId());
//                if(ast.isInStack()){
//                    stack.add(ident);
//                }
//                this.regId++;
            }
            output(tags()+"%v"+this.regId+" = load i32, i32* "+"@"+identName+"\n");
            ast.setValue("%v"+this.regId);
            ast.setRegId("@"+identName);
            this.regId++;
        }
    }
    public void Exp(AstNode ast){
        generate(ast.getChild().get(0));//AddExp
        ast.setValue(ast.getChild().get(0).getValue());
    }
    public void Cond(AstNode ast){
        ast.getChild().get(0).setNoId(ast.getNoId());
        ast.getChild().get(0).setYesId(ast.getYesId());
        ast.getChild().get(0).setStmtId(ast.getStmtId());
        ast.getChild().get(0).setInStack(false);
        generate(ast.getChild().get(0));//LOrExp
        ast.setValue("%v"+this.regId);
        this.regId++;
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
            a.get(0).setInStack(ast.isInStack());
            generate(a.get(0));//LVal
            ast.setValue(a.get(0).getValue());
        }
    }
    public void Number1(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        ast.setValue(a.get(0).getContent());
    }

    public void UnaryExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        if(a.get(0).getContent().equals("<UnaryOp>")){
            generate(a.get(1));//UnaryExp
            if(a.get(0).getChild().get(0).getContent().equals("-")){
                if(level>0){
                    output(tags()+"%v"+this.regId+" = sub i32 0, "+a.get(1).getValue()+"\n");
                    ast.setRegId("%v"+this.regId);
                    ast.setValue("%v"+this.regId);
                    this.regId++;
                }
                else{
                    ast.setValue(mathCalculate("0","-",a.get(1).getValue()));
                }
            }
            else if(a.get(0).getChild().get(0).getContent().equals("+")){ast.setValue(a.get(1).getValue());}
            else if(a.get(0).getChild().get(0).getContent().equals("!")){
                output(tags()+"%v"+this.regId+" = icmp eq i32 0, "+a.get(1).getValue()+"\n");
                this.regId++;
                output(tags()+"%v"+this.regId+" = sext i1 %v"+(this.regId-1)+" to i32\n");
                ast.setRegId("%v"+this.regId);
                ast.setValue("%v"+this.regId);
                this.regId++;
            }
        }
        else if(a.get(0).getContent().equals("<PrimaryExp>")){
            a.get(0).setInStack(ast.isInStack());
            generate(a.get(0));//PrimaryExp
            ast.setValue(a.get(0).getValue());
        }
        else if(a.size()>2&&a.get(1).getContent().equals("(")){
            AstNode ident=a.get(0);
            String identName = ident.getContent();
            AstNode identGlobe=global.get(identName);
            ident.setReturnType(identGlobe.getReturnType());
            if(ident.getReturnType().equals("i32")){
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
                }
            }
            else if(ident.getReturnType().equals("void")){
                if(a.get(2).getContent().equals(")")){
                    output(tags()+"call "+ident.getReturnType()+" @"+ident.getContent()+"()\n");
                }
                else{
                    generate(a.get(2));
                    output(tags()+"call "+ident.getReturnType()+" @"+ident.getContent()+"("+a.get(2).getValue()+")\n");
                }
            }

        }
    }

    public void FuncRParams(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        generate(a.get(0));
        String Value;
        Value ="i32 "+a.get(0).getValue();
        for(int i=2;i<a.size();i+=2){
            generate(a.get(i));
        }
        for(int i=2;i<a.size();i+=2){
            Value = Value +", i32 "+a.get(i).getValue();
        }
        ast.setValue(Value);
    }
    public void AddMulExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        a.get(0).setInStack(ast.isInStack());
        generate(a.get(0));//AddExp/MulExp
        String left=a.get(0).getValue();
        if(a.size()>1){
            for(int i=1;i<a.size();i+=2){
                String op=a.get(i).getContent();
                a.get(i+1).setInStack(ast.isInStack());
                generate(a.get(i+1));
                String right=a.get(i+1).getValue();
                String opt=Operator(op);
                if(level>0){
                    output(tags()+"%v"+this.regId+" = "+opt+" i32 "+left+", "+right+"\n");
                    a.get(i+1).setRegId("%v"+this.regId);
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
    public void RelEqExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        a.get(0).setInStack(ast.isInStack());
        generate(a.get(0));//RelExp/EqExp
        String left=a.get(0).getValue();
        if(a.size()>1){
            for(int i=1;i<a.size();i+=2){
                String op=a.get(i).getContent();
                a.get(i+1).setInStack(ast.isInStack());
                generate(a.get(i+1));
                String right=a.get(i+1).getValue();
                String opt=Operator(op);
                if(level>0){
                    output(tags()+"%v"+this.regId+" = icmp "+opt+" i32 "+left+", "+right+"\n");
                    this.regId++;
                    output(tags()+"%v"+this.regId+" = sext i1 %v"+(this.regId-1)+" to i32\n");
                    a.get(i+1).setRegId("%v"+this.regId);
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
    public void LOrAndExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        generate(a.get(0));//LAndExp
        String left=a.get(0).getValue();
        if(a.size()>1){
            for(int i=1;i<a.size();i+=2){
                String op=a.get(i).getContent();
                generate(a.get(i+1));
                String right=a.get(i+1).getValue();
                String opt=Operator(op);

                output(tags()+"%v"+this.regId+" = "+opt+" i32 "+left+", "+right+"\n");
                a.get(i+1).setRegId("%v"+this.regId);
                a.get(i+1).setValue("%v"+this.regId);
                this.regId++;

                left=a.get(i+1).getValue();
            }
            ast.setValue(a.get(a.size()-1).getValue());
        }
        else{
            ast.setValue(left);
        }
    }
    public void LAndExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        if(a.size()==1){
            a.get(0).setInStack(false);
            generate(a.get(0));
            ast.setValue(a.get(0).getValue());
        }
        else{
            for(int i=0;i<a.size()-2;i+=2){
                a.get(i).setInStack(false);
                generate(a.get(i));//LAndExp
                output(tags()+"%v"+this.regId+" = icmp ne i32 0, "+a.get(i).getValue()+"\n");
                output(tags()+"br i1 %v"+this.regId+", label %v"+(this.regId+1)+", label %v"+ast.getNoId()+"\n");
                this.regId+=2;
                output("\nv"+(this.regId-1)+":\n");
            }
            int max=a.size()-1;
            a.get(max).setInStack(false);
            generate(a.get(max));
            if(a.size()==1){
                ast.setValue(a.get(max).getValue());
            }

            output(tags()+"%v"+this.regId+" = icmp ne i32 0, "+a.get(max).getValue()+"\n");
            output(tags()+"br i1 %v"+this.regId+", label %v"+ast.getYesId()+", label %v"+ast.getNoId()+"\n");
            this.regId+=1;

        }
    }

    public void LOrExp(AstNode ast){
        ArrayList<AstNode> a=ast.getChild();
        for(int i=0;i<a.size()-2;i+=2){
            if(a.get(i).getChild().get(0).getChild().size()==1){
                a.get(i).getChild().get(0).setInStack(false);
                generate(a.get(i).getChild().get(0));//LOrExp
                output(tags()+"%v"+this.regId+" = icmp ne i32 0, "+a.get(i).getChild().get(0).getValue()+"\n");
                output(tags()+"br i1 %v"+this.regId+", label %v"+ast.getYesId()+", label %v"+(this.regId+1)+"\n");
                this.regId+=2;
                output("\nv"+(this.regId-1)+":\n");
            }
            else{
                a.get(i).setYesId(ast.getYesId());
                a.get(i).setStmtId(ast.getStmtId());

                a.get(i).setNoId(this.regId);
                a.get(i).setInStack(false);
                this.regId++;
                generate(a.get(i));//特殊
                output("\nv"+a.get(i).getNoId()+":\n");
            }
        }
        int max=a.size()-1;
        if(a.get(max).getChild().size()==1){
            a.get(max).setInStack(false);
            generate(a.get(max));//LOrExp
            output(tags()+"%v"+this.regId+" = icmp ne i32 0, "+a.get(max).getValue()+"\n");
            output(tags()+"br i1 %v"+this.regId+", label %v"+ast.getYesId()+", label %v"+ast.getNoId()+"\n");
            this.regId+=1;
        }
        else{
            a.get(max).setYesId(ast.getYesId());
            a.get(max).setStmtId(ast.getStmtId());
            a.get(max).setNoId(ast.getNoId());
            a.get(max).setInStack(false);
            this.regId++;
            generate(a.get(max));//特殊

        }

    }









    public String Operator(String op){
        String opt="";
        switch(op){
            case "+": opt="add";break;
            case "-": opt="sub";break;
            case "*": opt="mul";break;
            case "/": opt="sdiv";break;
            case "%": opt="srem";break;
            case "==": opt="eq";break;
            case "!=": opt="ne";break;
            case ">": opt="sgt";break;
            case ">=": opt="sge";break;
            case "<": opt="slt";break;
            case "<=": opt="sle";break;
            case "&&": opt="and";break;
            case "||": opt="or";break;
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
            case "==": ans=(a==b)?1:0;break;
            case "!=": ans=(a!=b)?1:0;break;
            case ">": ans=(a>b)?1:0;break;
            case ">=": ans=(a>=b)?1:0;break;
            case "<": ans=(a<b)?1:0;break;
            case "<=": ans=(a<=b)?1:0;break;
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
