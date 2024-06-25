package NavalBattleGameViewer.UI.ConsoleUI.UItemplates;

import NavalBattleGame.GameElements.GameEnums.GameEvent;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleButton;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        var enterPortText = new ConsoleTextBlock("Enter address with port of existing match\n(enter only port if address is localhost)", 10, 1);
        enterPortText.setPosition(40,12);
        UIElementsMap.put(JoinMenuElements.EnterPortText, enterPortText);

        var backToMenuButton = new ConsoleButton("Back to menu [back]", 10,1);
        backToMenuButton.addListener(() -> {
            game.invokeEvent(GameEvent.BackToMenu);
        });
        backToMenuButton.setPosition(45, 15);
        UIElementsMap.put(JoinMenuElements.BackToMenuButton, backToMenuButton);
        focusableElementsMap.put(JoinMenuElements.BackToMenuButton, backToMenuButton);
    }


    @Override
    public void onInput(String enteredText) {
        if ("back".equals(enteredText)) {
            pressButton(JoinMenuElements.BackToMenuButton);
        } else {
            String[] address = enteredText.split(":");
            if (address.length > 1 && isValidAddress(enteredText)) {
                game.joinToRound(address[0], Integer.parseInt(address[1]));
                game.invokeEvent(GameEvent.ConnectedToRound);
            } else if (isNumeric(address[0])){
                game.joinToRound(Integer.parseInt(enteredText));
                game.invokeEvent(GameEvent.ConnectedToRound);
            }
        }
    }

    private boolean isValidAddress(String address) {
        if (address == null) return false;
        String regex = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(?::(?:[0-9]|[1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))?$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    NavalBattleGame game;
}
