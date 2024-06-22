package NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView;

import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.Printable;

enum MatchMenuElements {

}

public class ConsoleMatchMenu extends ConsoleCanvas<MatchMenuElements> implements Printable, InputListener {

    public ConsoleMatchMenu(int width, int height) {
        super(width, height);
    }

    @Override
    public void onInput(String enteredText) {

    }
}
