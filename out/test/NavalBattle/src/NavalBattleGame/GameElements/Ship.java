package NavalBattleGame.GameElements;

import java.util.ArrayList;

public class Ship  {

    public Ship() {
        this(0);
    }

    public Ship (Ship ship) {
        this(ship.countShipSize());
    }

    public Ship(int shipSize) {
        this.parts = new ArrayList<>();
        for (int partId = 0; partId < shipSize; partId++) {
            this.parts.add(new Part());
        }
    }

    public boolean checkIsShipHited() {
        for (var part: parts) {
            if (part.isDestroyed()) return true;
        }
        return false;
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public Part partAtId(int partId) {
        return parts.get(partId);
    }

    public int countShipSize() {
        return parts.size();
    }

    public void GetDamageAtPart(int partId) {
        Part attackedPart = parts.get(partId);
        if (attackedPart != null) {
            attackedPart.getDamage();
        }
    }

    public boolean checkIsShipDestroyed() {
        for (var part: parts) {
            if (!part.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    public void setParts(ArrayList<Part> parts) {
        this.parts = parts;
    }

    ArrayList<Part> parts;

}
