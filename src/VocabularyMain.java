import java.util.ArrayList;

public class VocabularyMain{
    public ArrayList<String> bank=new ArrayList<>();

    public VocabularyMain(ArrayList<String> bank){
        this.bank=bank;
    }
    public void analyze(){
        Identify identify = new Identify(bank);
        identify.analyze();
    }
}
