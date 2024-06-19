package NavalBattleGameViewer.UI.UIelements;

import NavalBattleGameViewer.UI.UIElement;
import NavalBattleGameViewer.UI.UIevents;
import NavalBattleGameViewer.UI.UIstate;

import java.util.ArrayList;

public class Button extends UIElement {

    public Button(String buttonText, int width, int height) {
        super(width, height);
        this.buttonText = buttonText;
    }

    @Override
    protected void onStateChange(UIstate newState) {
        if (currentState == UIstate.Active) {
            this.onButtonPressed();
        }
    }

    @Override
    protected void handleInvalidEvent(UIevents event) {}

    private void onButtonPressed() {
        for (var listener: listeners) {
            listener.onEvent();
        }
    }

    public void addListener(ButtonListener newListener) {
        this.listeners.add(newListener);
    }

    public String getButtonText() {
        return buttonText;
    }

    ArrayList<ButtonListener> listeners = new ArrayList<>();
    String buttonText;

}
