package NavalBattleGameViewer.UI.ConsoleUI.UItemplates;

import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.Canvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleButton;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;
import NavalBattleGameViewer.UI.UIElement;
import NavalBattleGameViewer.UI.UIevents;

enum MainMenuElements {
    Title,
    SpButton,
    MpButton,
    ExitButton,
    InfoText
}

public class ConsoleMainMenu extends Canvas<MainMenuElements> implements InputListener {

    public ConsoleMainMenu(NavalBattleGame navalBattle) {
        initializeUIElements(navalBattle);
    }

    private void initializeUIElements(NavalBattleGame navalBattle) {
        var title = new ConsoleTextBlock("NAVAL BATTLE", 50, 10);
        title.setPosition(50, 13);
        UIElementsMap.put(MainMenuElements.Title, title);

        var spButton = new ConsoleButton("Singleplayer [sp]", 50,10);
        spButton.addListener(() -> {
            navalBattle.processEvent(GameEvent.SingleplayerGameStarted);
        });
        spButton.setPosition(50, 15);
        UIElementsMap.put(MainMenuElements.SpButton, spButton);
        focusableElementsMap.put(MainMenuElements.SpButton, spButton);

        var mpButton = new ConsoleButton("Multiplayer [mp]", 50,10);
        mpButton.addListener(() -> {
            navalBattle.processEvent(GameEvent.MultiplayerGameStarted);
        });
        mpButton.setPosition(50, 16);
        UIElementsMap.put(MainMenuElements.MpButton, mpButton);
        focusableElementsMap.put(MainMenuElements.MpButton, mpButton);

        var exitButton = new ConsoleButton("Exit [exit]", 50,10);
        exitButton.addListener(() -> {
            navalBattle.processEvent(GameEvent.GameExited);
        });
        exitButton.setPosition(50, 17);
        UIElementsMap.put(MainMenuElements.ExitButton, exitButton);
        focusableElementsMap.put(MainMenuElements.ExitButton, exitButton);

        var infoText = new ConsoleTextBlock("Enter word from square brackets of the desired button", 50,10);
        infoText.setPosition(0, 0);
        UIElementsMap.put(MainMenuElements.InfoText, infoText);
    }

    @Override
    public void onInput(String enteredText) {
        if ("mp".equals(enteredText)) {
            pressButton(MainMenuElements.MpButton);
        } else if ("sp".equals(enteredText)) {
            pressButton(MainMenuElements.SpButton);
        } else if ("exit".equals(enteredText)) {
            pressButton(MainMenuElements.ExitButton);
        }
    }

    private void pressButton(MainMenuElements button) {
        setFocus(button);
        UIElementsMap.get(focusedElement).processEvent(UIevents.Pressed);
        UIElementsMap.get(focusedElement).processEvent(UIevents.Released);
    }
}
