package NavalBattleGame.ToolsForGame;

import NavalBattleGame.GameElements.Part;
import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;
import NavalBattleGame.GameUsers.Player;
import NavalBattleGame.GameUsers.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {

    static public String createJsonStringFromPlayer(Player player) throws JsonProcessingException {
        return objectMapper.writeValueAsString(player);
    }

    static public String createJsonStringFromUser(User user) throws  JsonProcessingException {
        return objectMapper.writeValueAsString(user);
    }

    static public String createJsonStringFromSeaField(SeaField field) throws JsonProcessingException {
        return objectMapper.writeValueAsString(field);
    }

    static public String createJsonStringFromShip(Ship ship) throws JsonProcessingException {
        return objectMapper.writeValueAsString(ship);
    }

    static public String createJsonStringFromPart(Part part) throws JsonProcessingException {
        return objectMapper.writeValueAsString(part);
    }

    static public Part makePartFromJsonString(String partJsonStr) throws JsonProcessingException {
        return objectMapper.readValue(partJsonStr, Part.class);
    }

    static public Player makePlayerFromJsonString(String playerJsonStr) throws  JsonProcessingException {
        var player = objectMapper.readValue(playerJsonStr, Player.class);
        fixParsedSeaField(player.getField());
        return player;
    }

    static public User makeUserFromJsonString(String userJsonSrt) throws JsonProcessingException {
        return objectMapper.readValue(userJsonSrt, User.class);
    }

    static public SeaField makeSeaFieldFromJsonString(String seaFieldJsonStr) throws JsonProcessingException {
        SeaField seaField =  objectMapper.readValue(seaFieldJsonStr, SeaField.class);
        fixParsedSeaField(seaField);
        return seaField;
    }

    static public Ship makeShipFromJsonString(String shipJsonStr) throws JsonProcessingException {
        return objectMapper.readValue(shipJsonStr, Ship.class);
    }

    static private void fixParsedSeaField(SeaField field) {
        for (var row: field.getSeaTable().keySet()) {
            for (var col: field.getSeaTable().get(row).keySet()) {
                var currentCell = field.getSeaTable().get(row).get(col);
                if (currentCell.hasShip() &&
                        currentCell.getShip().countShipSize() > 1) {
                    int[][] checkDirections = {{1,0}, {-1,0}, {0,1}, {0,-1}};
                    for (var checkDirection: checkDirections) {
                        int nextRow = row + checkDirection[1];
                        char nextCol = (char)(col + checkDirection[0]);
                        if (field.getSeaTable().get(nextRow) != null &&
                                field.getSeaTable().get(nextRow).get(nextCol) != null &&
                                field.getSeaTable().get(nextRow).get(nextCol).hasShip()
                                ) {
                            do {
                                field.getSeaTable().get(nextRow).get(nextCol).setShip(
                                        field.getSeaTable().get(nextRow - checkDirection[1]).get((char)(nextCol - checkDirection[0])).getShip()
                                );
                                nextRow += checkDirection[1];
                                nextCol = (char)(nextCol + checkDirection[0]);
                            } while (field.getSeaTable().get(nextRow) != null &&
                                    field.getSeaTable().get(nextRow).get(nextCol) != null &&
                                    field.getSeaTable().get(nextRow).get(nextCol).hasShip());
                        }
                    }
                }
            }
        }
    }

    static ObjectMapper objectMapper = new ObjectMapper();
}
