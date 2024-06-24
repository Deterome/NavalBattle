package NavalBattleGame.ToolsForGame;

import NavalBattleGame.GameElements.Part;
import NavalBattleGame.GameElements.SeaField;
import NavalBattleGame.GameElements.Ship;
import NavalBattleGame.GameUsers.Player;
import NavalBattleGame.GameUsers.PlayerAction;
import NavalBattleGame.GameUsers.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    static public String createJsonStringFromMovementInfo(Player actingPlayer, PlayerAction action, int moveRow, char moveCol) throws JsonProcessingException {
        ObjectNode jsonMoveInformation = objectMapper.createObjectNode();
        jsonMoveInformation.put(PlayerAction.enumString, action.getStringOfAction());
        jsonMoveInformation.put("player_nickname", actingPlayer.getNickname());
        jsonMoveInformation.put("coordinates", JsonParser.createJsonStringFromCoordinates(moveRow, moveCol));
        return objectMapper.writeValueAsString(jsonMoveInformation);
    }

    static public String createJsonStringFromCoordinates(int row, char col) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        ObjectNode coordsJson = objectMapper.createObjectNode();
        coordsJson.put("row", row);
        coordsJson.put("col", col);

        return objectMapper.writeValueAsString(coordsJson);
    }

    static public String createJsonStringFromCoordinatesString(String coordsString) throws JsonProcessingException {
        Pattern pattern = Pattern.compile("([A-Z])([0-9]+)");
        Matcher matcher = pattern.matcher(coordsString);
        Map.Entry<Integer, Character> coords;
        if (matcher.find()) {
            coords = new AbstractMap.SimpleEntry<>(Integer.parseInt(matcher.group(2)), matcher.group(1).charAt(0));
            return createJsonStringFromCoordinates(coords.getKey(), coords.getValue());
        }
        return null;
    }

    static public Map.Entry<Integer, Character> makeCoordinatesFromJsonString(String coordsJsonStr) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(coordsJsonStr);
        return new AbstractMap.SimpleEntry<>(Integer.parseInt(jsonNode.path("row").asText()), (char)jsonNode.path("col").asInt());
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
