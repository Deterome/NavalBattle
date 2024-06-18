package NavalBattleGameViewer.UI.UItemplates;

import NavalBattleGame.GameEnums.GameEvent;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.UI.Canvas;
import NavalBattleGameViewer.UI.UIelements.Button;
import NavalBattleGameViewer.UI.UIelements.TextBlock;

public class MainMenu extends Canvas {

    public MainMenu(NavalBattleGame navalBattle) {
        initializeUIelements(navalBattle);
        setFocus(1);
    }

    private void initializeUIelements(NavalBattleGame navalBattle) {
        var title = new TextBlock("NAVAL BATTLE GAME", 50, 10);
        title.setPosition(50, 13);
        UIelements.add(title);

        var spButton = new Button("Singleplayer", 50,10);
        spButton.addListener(() -> {
            navalBattle.processEvent(GameEvent.SingleplayerGameStarted);
        });
        spButton.setPosition(50, 15);
        UIelements.add(spButton);

        var mpButton = new Button("Multiplayer", 50,10);
        mpButton.addListener(() -> {
            navalBattle.processEvent(GameEvent.MultiplayerGameStarted);
        });
        mpButton.setPosition(50, 16);
        UIelements.add(mpButton);

        var exitButton = new Button("Exit", 50,10);
        exitButton.addListener(() -> {
            navalBattle.processEvent(GameEvent.GameExited);
        });
        exitButton.setPosition(50, 17);
        UIelements.add(exitButton);
    }

}
