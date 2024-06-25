package NavalBattleGame.GameUsers;

import NavalBattleGame.GameElements.GameEnums.AttackResult;
import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class Player {

    public Player() {
        this.nickname = "noname";
    }

    public Player(String nickname) {
        this.nickname = nickname;
    }

    public Ship findFirstAvailableShip() {
        return availableShips.keySet().stream()
                .findFirst()
                .flatMap(shipSize -> availableShips.get(shipSize).stream().findFirst())
                .orElse(null);
    }

    public Ship pickFirstAvailableShip() {
        Optional<Ship> pickedShip = Optional.ofNullable(findFirstAvailableShip());
        pickedShip.ifPresent(this::removeAvailableShip);
        return pickedShip.orElse(null);
    }

    public void removeAvailableShip(Ship shipToDelete) {
        Optional.ofNullable(availableShips.get(shipToDelete.countShipSize())).ifPresent(ships -> {
            ships.remove(shipToDelete);
            if (ships.isEmpty()) {
                availableShips.remove(shipToDelete.countShipSize());
            }
        });
    }

    public AttackResult attackPlayer(Player player, int row, char col) {
        return player.getField().attackCell(row, col);
    }

    public void addShips(Ship ship, int countOfShips) {
        int shipSize = ship.countShipSize();
        if (!availableShips.containsKey(shipSize)) {
            availableShips.put(shipSize, new ArrayList<>());
        }
        for (int shipId = 0; shipId < countOfShips; shipId++) {
            availableShips.get(shipSize).add(new Ship(ship.countShipSize()));
        }
    }

    public void setField(SeaField field) {
        this.field = field;
    }

    public void setAvailableShips(HashMap<Integer, ArrayList<Ship>> availableShips) {
        this.availableShips = availableShips;
    }

    public SeaField getField() {
        return field;
    }

    public HashMap<Integer, ArrayList<Ship>> getAvailableShips() {
        return availableShips;
    }

    public String getNickname() {
        return nickname;
    }

    public int countRemainingShips() {
        int remainingShipsCount = 0;
        for (var ship: field.makeShipsListFromField()) {
            if (!ship.checkIsShipDestroyed()) remainingShipsCount++;
        }
        return remainingShipsCount;
    }


    private SeaField field;
    HashMap<Integer, ArrayList<Ship>> availableShips  = new HashMap<>();
    String nickname;

}
