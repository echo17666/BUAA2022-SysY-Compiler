package Datam;

import java.util.ArrayList;

public class AstNode{
    String content="";
    ArrayList<AstNode> child = new ArrayList<AstNode>();
    AstNode fatherAst = null;
    int regId=0;
    int level=0;
    String value="";
    KeyValue key = new KeyValue();
    public AstNode(String content){
        this.content=content;
    }
    public void addNode(AstNode a){
        a.setFatherAst(this);
        child.add(a);
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
