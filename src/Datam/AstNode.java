package Datam;

import java.util.ArrayList;

public class AstNode{
    String content="";
    ArrayList<AstNode> child = new ArrayList<AstNode>();
    AstNode fatherAst = null;
    int regId=0;
    int level=0;
    int StmtId=0;
    int YesId=0;
    int NoId=0;
    boolean inStack=true;
    String value="";
    String returnType="";
    KeyValue key = new KeyValue();
    public AstNode(String content){
        this.content=content;
    }
    public void addNode(AstNode a){
        a.setFatherAst(this);
        child.add(a);
    }

    public boolean isInStack(){
        return inStack;
    }

    public void setInStack(boolean inStack){
        this.inStack=inStack;
    }

    public ArrayList<AstNode> getChild(){
        return child;
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int level){
        this.level=level;
    }

    public String getReturnType(){
        return returnType;
    }


    public int getStmtId(){
        return StmtId;
    }

    public void setStmtId(int stmtId){
        StmtId=stmtId;
    }

    public int getYesId(){
        return YesId;
    }

    public int getNoId(){
        return NoId;
    }

    public void setYesId(int yesId){
        YesId=yesId;
    }

    public void setNoId(int noId){
        NoId=noId;
    }


    public void setReturnType(String returnType){
        this.returnType=returnType;
    }

    public void setFatherAst(AstNode fatherAst){
        this.fatherAst=fatherAst;
    }

    public AstNode getFatherAst(){
        return fatherAst;
    }

    public String getContent(){
        return content;
    }

    public void setChild(ArrayList<AstNode> child){
        this.child=child;
    }
    public AstNode changeNode(){
        AstNode s = this.child.get(this.child.size()-1);
        this.child.remove(this.child.size()-1);
        return s;
    }

    public int getRegId(){
        return regId;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value=value;
    }

    public void setRegId(int regId){
        this.regId=regId;
    }

    public void setKey(KeyValue key){
        this.key=key;
    }

    public KeyValue getKey(){
        return key;
    }
}
