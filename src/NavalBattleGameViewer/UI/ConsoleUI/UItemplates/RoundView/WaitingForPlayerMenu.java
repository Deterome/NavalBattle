package NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView;

import NavalBattleGame.GameRound.RoundEvents;
import NavalBattleGame.GameRound.UserRole;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.Coord2D;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleButton;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleUIElements.ConsoleTextBlock;
import NavalBattleGameViewer.UI.ConsoleUI.PrintConstructor;
import NavalBattleGameViewer.UI.Printable;

enum WaitingForPlayerMenuElements {
    WaitingText,
    ChangeRoleText,
    CloseRound
}

public class WaitingForPlayerMenu extends ConsoleCanvas<WaitingForPlayerMenuElements> implements Printable, InputListener {
    public WaitingForPlayerMenu(NavalBattleGame game, int width, int height) {
        super(width, height);
        initializeUIElements();
        this.game = game;
    }

    private void initializeUIElements() {
        var waitingText = new ConsoleTextBlock("Waiting for users...", 10, 1);
        waitingText.setPosition(50,2);
        this.UIElementsMap.put(WaitingForPlayerMenuElements.WaitingText, waitingText);

        var changeRoleText = new ConsoleTextBlock("Change role: watcher [watcher], player [player]", 10, 1);
        changeRoleText.setPosition(0,20);
        this.UIElementsMap.put(WaitingForPlayerMenuElements.ChangeRoleText, changeRoleText);

        var closeRoundButton = new ConsoleButton("Close round [close]", 10,1);
        closeRoundButton.addListener(() -> {
            game.getCurrentRound().processEvent(RoundEvents.MatchEnd);
        });
        closeRoundButton.setPosition(0, 0);
        UIElementsMap.put(WaitingForPlayerMenuElements.CloseRound, closeRoundButton);
        focusableElementsMap.put(WaitingForPlayerMenuElements.CloseRound, closeRoundButton);
    }

    @Override
    public String getPrintableString() {
        PrintConstructor printConstructor = new PrintConstructor();
        printConstructor.setSize(canvasSize.x, canvasSize.y);
        printConstructor.putTextInPosition(super.getPrintableString(), 0, 0);

        addUsersToPrint(printConstructor);

        if (game.getCurrentRound().hasUserRole(game.getUser(), UserRole.Admin)) {
            addSessionInfoToPrint(printConstructor);
            addPlayButton(printConstructor);
            addLanButtons(printConstructor);
        }

        return printConstructor.getPrint();
    }

    private void addPlayButton(PrintConstructor printConstructor) {
        printConstructor.putTextInPosition("Play [play]", 50, 22);
    }

    private void addLanButtons(PrintConstructor printConstructor) {
        if (!game.getCurrentRound().isLanOpened()) {
            printConstructor.putTextInPosition("Open LAN [lan]", 95, 0);
        } else {
            printConstructor.putTextInPosition("Close LAN [lan]", 95, 0);
        }
    }

    private void addSessionInfoToPrint(PrintConstructor printConstructor) {
        var currentRound = game.getCurrentRound();

        if (currentRound.isLanOpened()) {
            String sessionInfoStr = "Session port: " +
                    game.getCurrentRound().getRoundServerPort();

            printConstructor.putTextInPosition(sessionInfoStr, 95, 1);
        }
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

    @Override
    public void onInput(String enteredText) {
        switch (enteredText) {
            case "watch" -> game.getCurrentRound().giveUserRole(game.getUser(), UserRole.Watcher);
            case "player" -> game.getCurrentRound().giveUserRole(game.getUser(), UserRole.Player);
            case "close" -> pressButton(WaitingForPlayerMenuElements.CloseRound);
            default -> {
                if (game.getCurrentRound().hasUserRole(game.getUser(), UserRole.Admin)) {
                    switch (enteredText) {
                        case "lan" -> {
                            if (!game.getCurrentRound().isLanOpened()) {
                                game.getCurrentRound().openLAN();
                            } else {
                                game.getCurrentRound().closeLAN();
                            }
                        }
                        case "play" -> game.getCurrentRound().processEvent(RoundEvents.StopWaitingForPlayers);
                    }
                }
            }
        }

    }

}
