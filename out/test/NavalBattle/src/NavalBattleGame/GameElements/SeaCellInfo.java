package NavalBattleGame.GameElements;

import NavalBattleGame.GameElements.GameEnums.AttackResult;

public class SeaCellInfo {

    public Ship getShip() {
        return ship;
    }

    public boolean hasShip() {
        return ship != null;
    }

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
                if (ship.checkIsShipDestroyed()) {
                    return AttackResult.Destroyed;
                } else {
                    return AttackResult.Hit;
                }
            }
        }
        return AttackResult.Miss;
    }


    public void setShip(Ship ship) {
        this.ship = ship;
    }

    Ship ship;
    boolean shelled = false;
    int shipPartId;
}
