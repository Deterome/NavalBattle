package NavalBattleGameViewer.UI.UIelements;

import NavalBattleGameViewer.UI.UIelement;
import NavalBattleGameViewer.UI.UIevents;
import NavalBattleGameViewer.UI.UIstate;

public class TextBlock extends UIelement {

    public TextBlock(String text, int width, int height) {
        super(width, height);
        this.text = text;
    }

    @Override
    protected void onStateChange(UIstate newState) {}
    @Override
    protected void handleInvalidEvent(UIevents event) {}

    public String getText() {
        return text;
    }

    String text;
}
