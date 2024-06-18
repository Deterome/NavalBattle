package NavalBattleGameViewer.UI;

import java.util.ArrayList;

public abstract class Canvas {

    public Canvas() {



    }

    public void setFocus(int elementID) {
        if (elementID < UIelements.size() && elementID >= 0) {
            UIelements.get(focusedElementId).processEvent(UIevents.ExitedArea);
            focusedElementId = elementID;
            UIelements.get(elementID).processEvent(UIevents.EnteredArea);
        }
    }

    public ArrayList<UIelement> getUIelements() {
        return UIelements;
    }

    public int getFocusedElementId() {
        return focusedElementId;
    }

    protected int focusedElementId;

    protected ArrayList<UIelement> UIelements = new ArrayList<>();

}
