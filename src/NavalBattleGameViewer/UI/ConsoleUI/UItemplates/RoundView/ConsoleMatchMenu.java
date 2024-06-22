package NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView;

import NavalBattleGame.GameUsers.NavalBattleAI;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.ConsoleUI.ConsoleCanvas;
import NavalBattleGameViewer.UI.ConsoleUI.FieldPrinter;
import NavalBattleGameViewer.UI.ConsoleUI.PrintConstructor;
import NavalBattleGameViewer.UI.Printable;

enum MatchMenuElements {

}

public class ConsoleMatchMenu extends ConsoleCanvas<MatchMenuElements> implements Printable, InputListener {

    public ConsoleMatchMenu(NavalBattleGame game, int width, int height) {
        super(width, height);

        this.game = game;
    }

    @Override
    public void onInput(String enteredText) {
        PrintConstructor printConstructor = new PrintConstructor();
        printConstructor.setSize(canvasSize.x, canvasSize.y);
        printConstructor.putTextInPosition(super.getPrintableString(), 0, 0);

        addFieldToPrint(printConstructor);
    }

    @Override
    public String getPrintableString() {
        PrintConstructor printConstructor = new PrintConstructor();
        printConstructor.setSize(canvasSize.x, canvasSize.y);
        printConstructor.putTextInPosition(super.getPrintableString(), 0, 0);

        addFieldToPrint(printConstructor);

        return printConstructor.getPrint();
    }

    private void addFieldToPrint(PrintConstructor printConstructor) {
        var players = game.getCurrentRound().getPlayersList();
        int printXPos = 5;
        int printXOffset = 45;
        for (var player: players) {
            printConstructor.putTextInPosition(FieldPrinter.getFieldPrint(player.getField(), game.getCurrentRound().getPlayerByUser(game.getUser()) != player), printXPos, 2);
            printXPos += printXOffset;
        }
    }
    NavalBattleGame game;
}
