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

    public AttackResult attackCell() {
        if (!shelled) {
            if (ship != null) {
                ship.GetDamageAtPart(shipPartId);
                if (ship.isDestroyed()) {
                    return AttackResult.Destroyed;
                } else {
                    return AttackResult.Hit;
                }
            }
            this.shelled = true;
        }
        return AttackResult.Miss;
    }

    boolean shelled = false;
    int shipPartId;
}
