package NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements;

import NavalBattleGameViewer.UI.Printable;
import NavalBattleGameViewer.UI.UIelements.Button;
import NavalBattleGameViewer.UI.UIstate;

public class ConsoleButton extends Button implements Printable {

    public ConsoleButton(String buttonText, int width, int height) {
        super(buttonText, width, height);
    }

    @Override
    public String getPrintableString() {
        String outputString = "";
        outputString += this.getButtonText();
        if (this.getCurrentState() == UIstate.Hover) {
            outputString += " #";
        }
        return outputString;
    }
}
