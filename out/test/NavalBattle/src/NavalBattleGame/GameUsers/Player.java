package NavalBattleGame.GameUsers;

import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class Player {

    public Player(String nickname) {
        this.nickname = nickname;
    }


    public Ship getFirstAvailableShip() {
        return availableShips.keySet().stream()
                .findFirst()
                .flatMap(shipSize -> availableShips.get(shipSize).stream().findFirst())
                .orElse(null);
    }

    public Ship pickFirstAvailableShip() {
        Optional<Ship> pickedShip = Optional.ofNullable(getFirstAvailableShip());
        pickedShip.ifPresent(this::removeAvailableShip);
        return pickedShip.orElse(null);
    }

    public void removeAvailableShip(Ship shipToDelete) {
        Optional.ofNullable(availableShips.get(shipToDelete.getShipSize())).ifPresent(ships -> {
            ships.remove(shipToDelete);
            if (ships.isEmpty()) {
                availableShips.remove(shipToDelete.getShipSize());
            }
        });
    }

    public void attackPlayer(Player player, int row, char col) {
        player.getField().attackCell(row, col);
    }

    public void addEnemy(Player player) {
        enemiesFields.put(player, player.field);
    }

    public void addShips(Ship ship, int countOfShips) {
        int shipSize = ship.getShipSize();
        if (!availableShips.containsKey(shipSize)) {
            availableShips.put(shipSize, new ArrayList<>());
        }
        for (int shipId = 0; shipId < countOfShips; shipId++) {
            availableShips.get(shipSize).add(new Ship(ship.getShipSize()));
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

    private SeaField field;

    public String getNickname() {
        return nickname;
    }

    public void ready() {
        ready = true;
    }

    public boolean isReady() {
        return ready;
    }

    HashMap<Integer, ArrayList<Ship>> availableShips  = new HashMap<>();
    HashMap<Player, SeaField> enemiesFields = new HashMap<>();

    boolean ready = false;
    String nickname = "noname";
}
