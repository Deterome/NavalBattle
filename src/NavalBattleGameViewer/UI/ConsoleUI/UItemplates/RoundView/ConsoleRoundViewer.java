package NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView;

import NavalBattleGame.GameRound.RoundStates;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.Coord2D;
import NavalBattleGameViewer.InputListener;
import NavalBattleGameViewer.UI.Printable;

import java.util.HashMap;


public class ConsoleRoundViewer implements Printable, InputListener {

    public ConsoleRoundViewer(NavalBattleGame game, int width, int height) {
        this.game = game;

        viewerSize.x = width;
        viewerSize.y = height;

        roundViews.put(RoundStates.WaitingForPlayers, new WaitingForPlayerMenu(game, width, height));
        roundViews.put(RoundStates.PlacementOfShips, new ConsoleShipsPlacementMenu(game, width, height));
        roundViews.put(RoundStates.Match, new ConsoleMatchMenu(game, width, height));
    }

    @Override
    public String getPrintableString() {
        var currentRound = game.getCurrentRound();

        return roundViews.get(currentRound.getCurrentState()).getPrintableString();
    }

    private Coord2D viewerSize = new Coord2D();
    private HashMap<RoundStates, Printable> roundViews = new HashMap<>();

    NavalBattleGame game;

    @Override
    public void onInput(String enteredText) {
        var currentView = roundViews.get(game.getCurrentRound().getCurrentState());
        if (currentView instanceof InputListener) {
            ((InputListener) currentView).onInput(enteredText);
        }
    }
}
