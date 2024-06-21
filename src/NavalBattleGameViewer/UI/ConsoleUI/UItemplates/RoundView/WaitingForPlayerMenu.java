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
    PlayButton,
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

        var playButton = new ConsoleButton("Play [play]", 10,1);
        playButton.addListener(() -> {
            game.getCurrentRound().processEvent(RoundEvents.StopWaitingForPlayers);
        });
        playButton.setPosition(50, 22);
        UIElementsMap.put(WaitingForPlayerMenuElements.PlayButton, playButton);
        focusableElementsMap.put(WaitingForPlayerMenuElements.PlayButton, playButton);

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
        addSessionInfoToPrint(printConstructor);

        return printConstructor.getPrint();
    }

    private void addSessionInfoToPrint(PrintConstructor printConstructor) {
        StringBuilder sessionInfoStr = new StringBuilder();

        sessionInfoStr.append("Session port: ");
        sessionInfoStr.append(game.getCurrentRound().getRoundPort());

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

    @Override
    public void onInput(String enteredText) {
        if ("watch".equals(enteredText)) {
            game.getCurrentRound().giveUserRole(game.getUser(), UserRole.Watcher);
        } else if ("player".equals(enteredText)) {
            game.getCurrentRound().giveUserRole(game.getUser(), UserRole.Player);
        } else if ("play".equals(enteredText)) {
            pressButton(WaitingForPlayerMenuElements.PlayButton);
        } else if ("close".equals(enteredText)) {
            pressButton(WaitingForPlayerMenuElements.CloseRound);
        }
    }


}
