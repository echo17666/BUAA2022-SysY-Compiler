package Datam;

public class ErrorLine{
    int linenum;
    String type;
    public ErrorLine(int linenum,String type){
        this.linenum=linenum;
        this.type=type;
    }

    public String getType(){
        return type;
    }

    public int getLinenum(){
        return linenum;
    }
}
