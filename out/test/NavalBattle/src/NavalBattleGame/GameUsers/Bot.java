package NavalBattleGame.GameUsers;

import NavalBattleGame.GameRound.Round;

public class Bot extends Player {

    public Bot(String nickname, Round round) {
        super(nickname);
    }

    public void placeShipsOnField() {
        NavalBattleAI.automaticPlacementOfShipsToField(this.getField(), this.availableShips);
    }

    public void attack() {

    }

    Round round;

}
