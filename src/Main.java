import NavalBattleGame.NavalBattleGame;
import NavalBattleGameViewer.NavalBattleGameConsoleViewer;

public class Main {

    public static void main(String[] args) {

        NavalBattleGame navalBattle = new NavalBattleGame();
        NavalBattleGameConsoleViewer navalBattleViewer = new NavalBattleGameConsoleViewer(navalBattle, 120, 28);

        navalBattleViewer.processGame();

        navalBattleViewer.closeViewer();
        navalBattle.stopStateMachine();
    }

}
