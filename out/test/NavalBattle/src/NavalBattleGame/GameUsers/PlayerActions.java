package NavalBattleGame.GameUsers;

import NavalBattleGame.GameRound.CommandToClient;
import NavalBattleGame.GameRound.RoundEvents;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.AbstractMap;
import java.util.Map;

public enum PlayerActions {
    Attack ("Attack");

    PlayerActions(String actionString) {
        this.actionString = actionString;
    }

    static PlayerActions getActionByString(String actionString) {
        for (var action: PlayerActions.values()) {
            if (action.actionString.equals(actionString)) {
                return action;
            }
        }
        return null;
    }

    public String getStringOfAction() {
        return this.actionString;
    }

    private String actionString;
}
