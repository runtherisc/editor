package game.items;

public class CreateRequirement {


    public CreateRequirement(int positionInList, boolean autocreate, int timeSet) {
        this.positionInList = positionInList;
        this.autocreate = autocreate;
        this.timeSet = autocreate && timeSet==0 ? 1 : timeSet;
    }

    private int timePassed = 0;
    private int positionInList;
    private boolean autocreate;
    private int timeSet;

}
