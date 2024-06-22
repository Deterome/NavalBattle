package NavalBattleGame.GameUsers;

import NavalBattleGame.GameRound.Round;

public class Bot extends Player {

    public Bot(String nickname, Round round) {
        super(nickname);

        this.round = round;
    }

    public void placeShipsOnField() {
        NavalBattleAI.automaticPlacementOfShipsToField(this.getField(), this.availableShips);
    }

    public void attack() {
        var attackCoords = NavalBattleAI.analyseFieldAndGetAttackCoords(round.getNextPlayerToAct().getField());
        this.round.makeAction(this, PlayerActions.Attack, CoordinatesParser.makeJsonStringOfAttackAction(attackCoords.getKey(), attackCoords.getValue()));
    }

    Round round;

}
