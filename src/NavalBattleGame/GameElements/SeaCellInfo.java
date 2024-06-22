package NavalBattleGame.GameElements;

public class SeaCellInfo {
    public Ship getShip() {
        return ship;
    }

    Ship ship;

    public int getShipPartId() {
        return shipPartId;
    }

    public boolean isShelled() {
        return shelled;
    }

    public void attackCell() {
        if (!shelled) {
            this.ship.GetDamageAtPart(shipPartId);
            this.shelled = true;
        }
    }

    boolean shelled = false;
    int shipPartId;
}
