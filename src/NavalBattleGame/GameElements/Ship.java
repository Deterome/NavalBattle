package NavalBattleGame.GameElements;

public class Ship {

    public Ship(int shipSize) {

        this.parts = new Part[shipSize];

    }

    public void GetDamageAtPart(int partId) {
        if (partId > this.parts.length || partId < 0) {
            throw new IndexOutOfBoundsException("Trying to access non-existent part of the ship!");
        } else {
            this.parts[partId].GetDamage();
        }
    }

    Part[] parts;

}
