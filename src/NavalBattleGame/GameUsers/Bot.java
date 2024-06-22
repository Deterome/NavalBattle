package NavalBattleGame.GameUsers;

public class Bot extends Player {

    public Bot(String nickname) {
        super(nickname);
    }

    public void placeShipsOnField() {
        NavalBattleAI.automaticPlacementOfShipsToField(this.getField(), this.availableShips);
    }

}
