package NavalBattleGame.GameUsers;

import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;

import java.util.ArrayList;

public class Player extends User {

    public Player(User user) {
        super(user);
    }

    SeaField playerField;
    ArrayList<Ship> availableShips  = new ArrayList<>();

}
