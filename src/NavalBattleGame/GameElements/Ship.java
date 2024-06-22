package NavalBattleGame.GameElements;

import java.util.ArrayList;
import java.util.Collections;

public class Ship {

    public Ship(int shipSize) {
        this.parts = new ArrayList<>(Collections.nCopies(shipSize, new Part()));
    }

    public ArrayList<Part> getParts() {
        return parts;
    }

    public Part getPart(int partId) {
        return parts.get(partId);
    }

    public int getShipSize() {
        return parts.size();
    }

    public void GetDamageAtPart(int partId) {
        Part part = parts.get(partId);
        if (part != null) {
            this.parts.get(partId).getDamage();
        }
    }

    ArrayList<Part> parts = new ArrayList<>();

}
