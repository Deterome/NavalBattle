package NavalBattleGameViewer.UI.ConsoleUI.UItemplates;

import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleButton;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;
import NavalBattleGameViewer.UI.UIevents;

enum JoinMenuElements {
    EnterPortText,
    BackToMenuButton
}

public class ConsoleJoinMenu extends ConsoleCanvas<JoinMenuElements> implements InputListener {


    public ConsoleJoinMenu(NavalBattleGame game, int width, int height) {
        super(width, height);
        this.game = game;

        initializeUIElements();
    }

    private void initializeUIElements() {
        var enterPortText = new ConsoleTextBlock("Enter port of existing match: ", 10, 1);
        enterPortText.setPosition(50,12);
        UIElementsMap.put(JoinMenuElements.EnterPortText, enterPortText);

        var backToMenuButton = new ConsoleButton("Back to menu [back]", 10,1);
        backToMenuButton.addListener(() -> {
            game.processEvent(GameEvent.BackToMenu);
        });
        backToMenuButton.setPosition(50, 15);
        UIElementsMap.put(JoinMenuElements.BackToMenuButton, backToMenuButton);
        focusableElementsMap.put(JoinMenuElements.BackToMenuButton, backToMenuButton);
    }


    @Override
    public void onInput(String enteredText) {
        if ("back".equals(enteredText)) {
            pressButton(JoinMenuElements.BackToMenuButton);
        } else if (isNumeric(enteredText)) {
            game.joinToRound(Integer.parseInt(enteredText));
        }
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void pressButton(JoinMenuElements button) {
        setFocus(button);
        UIElementsMap.get(focusedElement).processEvent(UIevents.Pressed);
        UIElementsMap.get(focusedElement).processEvent(UIevents.Released);
        clearFocus();
    }

    NavalBattleGame game;
}
