package NavalBattleGameViewer.UI.ConsoleUI.UItemplates.RoundView;

import NavalBattleGame.GameRound.RoundStates;
import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.Coord2D;
import NavalBattleGameViewer.UI.Printable;

import java.util.HashMap;


public class ConsoleRoundViewer implements Printable {

    public ConsoleRoundViewer(NavalBattleGame game, int width, int height) {
        this.game = game;

        viewerSize.x = width;
        viewerSize.y = height;

        roundViews.put(RoundStates.WaitingForPlayers, new WaitingForPlayerMenu(game, viewerSize.x, viewerSize.y));
    }

    @Override
    public String getPrintableString() {
        var currentRound = game.getCurrentRound();

        return roundViews.get(currentRound.getCurrentState()).getPrintableString();
    }

    private Coord2D viewerSize = new Coord2D();
    private HashMap<RoundStates, Printable> roundViews = new HashMap<>();

    NavalBattleGame game;

}
