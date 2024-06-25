package NavalBattleGameViewer.UI.ConsoleUI.UItemplates;

import NavalBattleGame.GameElements.GameEnums.GameEvent;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleButton;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;

enum MainMenuElements {
    Title,
    StartButton,
    JoinButton,
    ExitButton,
    InfoText,
    PlayerName
}

public class ConsoleMainMenu extends ConsoleCanvas<MainMenuElements> implements InputListener {

    public ConsoleMainMenu(NavalBattleGame navalBattle, int width, int height) {
        super(width, height);
        this.navalBattle = navalBattle;
        initializeUIElements();
    }

    private void initializeUIElements() {
        var title = new ConsoleTextBlock("NAVAL BATTLE", 10, 1);
        title.setPosition(50, 13);
        UIElementsMap.put(MainMenuElements.Title, title);

        var startButton = new ConsoleButton("Start new round [start]", 10,1);
        startButton.addListener(() -> {
            navalBattle.invokeEvent(GameEvent.RoundCreated);
        });
        startButton.setPosition(50, 15);
        UIElementsMap.put(MainMenuElements.StartButton, startButton);
        focusableElementsMap.put(MainMenuElements.StartButton, startButton);

        var joinButton = new ConsoleButton("Join to round [join]", 10,1);
        joinButton.addListener(() -> {
            navalBattle.invokeEvent(GameEvent.JoinMenuOpened);
        });
        joinButton.setPosition(50, 16);
        UIElementsMap.put(MainMenuElements.JoinButton, joinButton);
        focusableElementsMap.put(MainMenuElements.JoinButton, joinButton);

        var exitButton = new ConsoleButton("Exit [exit]", 10,1);
        exitButton.addListener(() -> {
            navalBattle.invokeEvent(GameEvent.GameExited);
        });
        exitButton.setPosition(50, 17);
        UIElementsMap.put(MainMenuElements.ExitButton, exitButton);
        focusableElementsMap.put(MainMenuElements.ExitButton, exitButton);

        var infoText = new ConsoleTextBlock("Enter word from square brackets of the desired button", 10,1);
        infoText.setPosition(0, 0);
        UIElementsMap.put(MainMenuElements.InfoText, infoText);

        var playerName = new ConsoleTextBlock("Player: NONAME", 10, 1);
        playerName.setPosition(95,0);

        UIElementsMap.put(MainMenuElements.PlayerName, playerName);
    }

    @Override
    public void onInput(String enteredText) {
        if ("start".equals(enteredText)) {
            pressButton(MainMenuElements.StartButton);
        } else if ("exit".equals(enteredText)) {
            pressButton(MainMenuElements.ExitButton);
        } else if ("join".equals(enteredText)) {
            pressButton(MainMenuElements.JoinButton);
        }
    }

    @Override
    public String getPrintableString() {
        ((ConsoleTextBlock)this.UIElementsMap.get(MainMenuElements.PlayerName))
                .setText("Player: " + navalBattle.getUser().getName());
        return super.getPrintableString();
    }

    NavalBattleGame navalBattle;

}
