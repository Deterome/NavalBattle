package NavalBattleGameViewer.UI.UIelements;

import NavalBattleGameViewer.UI.UIelement;
import NavalBattleGameViewer.UI.UIevents;
import NavalBattleGameViewer.UI.UIstate;
import org.w3c.dom.events.EventTarget;

import java.util.ArrayList;
import java.util.EventListener;

public class Button extends UIelement {

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
