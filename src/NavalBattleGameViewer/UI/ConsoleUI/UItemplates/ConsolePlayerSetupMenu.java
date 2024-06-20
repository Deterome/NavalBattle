package NavalBattleGameViewer.UI.ConsoleUI.UItemplates;

import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.GameUsers.User;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.Canvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;

enum PlayerSetupMenuElements {
    EnterPlayerNameText
}

public class ConsolePlayerSetupMenu extends ConsoleCanvas<PlayerSetupMenuElements> implements InputListener {

    public ConsolePlayerSetupMenu(NavalBattleGame game, int width, int height) {
        super(width, height);
        this.game = game;
        initializeUIElements();
    }
    private void initializeUIElements() {
        var enterNameText = new ConsoleTextBlock("Enter your nickname: ", 50, 20);
        enterNameText.setPosition(50,12);
        this.UIElementsMap.put(PlayerSetupMenuElements.EnterPlayerNameText, enterNameText);

    }

    @Override
    public void onInput(String enteredText) {
        if (!enteredText.isEmpty()) {
            game.setUser(new User(enteredText));
            game.processEvent(GameEvent.PlayerNameEntered);
        }
    }

    private NavalBattleGame game;
}
