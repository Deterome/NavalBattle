package NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView;

import NavalBattleGame.GameRound.RoundEvents;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.Coord2D;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleButton;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;
import NavalBattleGameViewer.UI.ConsoleUI.PrintConstructor;
import NavalBattleGameViewer.UI.Printable;

enum WaitingForPlayerMenuElements {
    WaitingText,
    ChangeRoleText,
    PlayButton
}

public class WaitingForPlayerMenu extends ConsoleCanvas<WaitingForPlayerMenuElements> implements Printable {
    public WaitingForPlayerMenu(NavalBattleGame game, int width, int height) {
        super(width, height);
        initializeUIElements();
        this.game = game;
    }

    private void initializeUIElements() {
        var waitingText = new ConsoleTextBlock("Waiting for users...", 10, 1);
        waitingText.setPosition(50,2);
        this.UIElementsMap.put(WaitingForPlayerMenuElements.WaitingText, waitingText);

        var changeRoleText = new ConsoleTextBlock("Change role: watcher [watch], player [player]", 10, 1);
        changeRoleText.setPosition(0,20);
        this.UIElementsMap.put(WaitingForPlayerMenuElements.ChangeRoleText, changeRoleText);

        var playButton = new ConsoleButton("Play [play]", 10,1);
        playButton.addListener(() -> {
            game.getCurrentRound().processEvent(RoundEvents.StopWaitingForPlayers);
        });
        playButton.setPosition(50, 22);
        UIElementsMap.put(WaitingForPlayerMenuElements.PlayButton, playButton);
        focusableElementsMap.put(WaitingForPlayerMenuElements.PlayButton, playButton);

    }

    @Override
    public String getPrintableString() {
        PrintConstructor printConstructor = new PrintConstructor();
        printConstructor.setSize(canvasSize.x, canvasSize.y);
        printConstructor.putTextInPosition(super.getPrintableString(), 0, 0);

        addUsersToPrint(printConstructor);
        addSessionInfoToPrint(printConstructor);

        return printConstructor.getPrint();
    }

    private void addSessionInfoToPrint(PrintConstructor printConstructor) {
        StringBuilder sessionInfoStr = new StringBuilder();

        sessionInfoStr.append("Session port: ");
        sessionInfoStr.append(game.getCurrentRound().getPortOfRound());

        printConstructor.putTextInPosition(sessionInfoStr.toString(), 95, 0);
    }

    private void addUsersToPrint(PrintConstructor printConstructor) {
        var users = game.getCurrentRound().getJoinedUsers();

        Coord2D usersListPos = new Coord2D();
        usersListPos.x = 0;
        usersListPos.y = 5;
        for (var user: users.keySet()) {
            StringBuilder userStr = new StringBuilder(user.getName());
            userStr.append(" (");
            var userRoles = users.get(user);
            for (int roleId = 0; roleId < userRoles.size(); roleId++) {
                if (roleId != 0) {
                    userStr.append(", ");
                }
                switch (userRoles.get(roleId)) {
                    case Player -> {
                        userStr.append("Player");
                    }
                    case Watcher -> {
                        userStr.append("Watcher");
                    }
                    case Admin -> {
                        userStr.append("Admin");
                    }
                }
            }
            userStr.append(")");
            printConstructor.putTextInPosition(userStr.toString(), usersListPos.x, usersListPos.y);
            usersListPos.y++;
        }
    }

    NavalBattleGame game;
}
