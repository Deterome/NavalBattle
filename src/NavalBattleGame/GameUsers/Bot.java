package NavalBattleGame.GameUsers;

import NavalBattleGame.GameRound.Round;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends Player {

    public Bot(String nickname, Round round) {
        super(nickname);

        this.round = round;

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void placeShipsOnField() {
        NavalBattleAI.automaticPlacementOfShipsToField(this.getField(), this.availableShips);
    }

    public void stopScheduler() {
        scheduledExecutorService.shutdown();
    }

    public void attack() {
        var player = this;
        var attackCoords = NavalBattleAI.analyseFieldAndGetAttackCoords(round.getNextPlayerToAct().getField());
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                round.makeAction(player, PlayerActions.Attack,
                        CoordinatesParser.makeJsonStringOfAttackAction(attackCoords.getKey(), attackCoords.getValue()));

            }
        },1, TimeUnit.SECONDS);
    }

    ScheduledExecutorService scheduledExecutorService;
    Round round;

}
