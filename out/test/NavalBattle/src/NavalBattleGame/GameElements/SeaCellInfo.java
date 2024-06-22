package NavalBattleGame.GameElements;

public class SeaCellInfo {
    public Ship getShip() {
        return ship;
    }

    public boolean hasShip() {
        return ship != null;
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
            this.shelled = true;
            if (hasShip()) {
                ship.GetDamageAtPart(shipPartId);
                if (ship.isDestroyed()) {
                    return AttackResult.Destroyed;
                } else {
                    return AttackResult.Hit;
                }
            }
        }
        return AttackResult.Miss;
    }

    boolean shelled = false;
    int shipPartId;
}
