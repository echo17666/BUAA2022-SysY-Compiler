package Datam;

import java.util.ArrayList;

public class AstNode{
    String content="";
    ArrayList<AstNode> child = new ArrayList<AstNode>();
    String regId="";
    int level=0;
    int StmtId=0;
    int YesId=0;
    int NoId=0;
    int BreakId=0;
    int ContinueId=0;
    String value="";
    String returnType="";
    KeyValue key = new KeyValue();
    public AstNode(String content){
        this.content=content;
    }
    public void addNode(AstNode a){
        child.add(a);
    }


    public int getBreakId(){
        return BreakId;
    }

    public void setBreakId(int breakId){
        BreakId=breakId;
    }

    public int getContinueId(){
        return ContinueId;
    }

    public void setContinueId(int continueId){
        ContinueId=continueId;
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

    public String getContent(){
        return content;
    }

    public AstNode changeNode(){
        AstNode s = this.child.get(this.child.size()-1);
        this.child.remove(this.child.size()-1);
        return s;
    }

    public String getRegId(){
        return regId;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value=value;
    }

    public void setRegId(String regId){
        this.regId=regId;
    }

    public void setKey(KeyValue key){
        this.key=key;
    }

    public KeyValue getKey(){
        return key;
    }
}
