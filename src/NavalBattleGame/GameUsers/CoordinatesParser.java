package NavalBattleGame.GameUsers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.AbstractMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinatesParser {

    static public String makeJsonStringOfAttackAction(int attackRow, char attackCol) {
        ObjectMapper objectMapper = new ObjectMapper();

        String coordsJsonStr = "";
        ObjectNode coordsJson = objectMapper.createObjectNode();
        try {
            coordsJson.put("row", attackRow);
            coordsJson.put("col", attackCol);

            coordsJsonStr = objectMapper.writeValueAsString(coordsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return coordsJsonStr;
    }

    static public Map.Entry<Integer, Character> getCoordinatesByJson(String coordsJsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(coordsJsonStr);
            return new AbstractMap.SimpleEntry<>(Integer.parseInt(jsonNode.path("row").asText()), (char)jsonNode.path("col").asInt());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static public Map.Entry<Integer, Character> getCoordinatesByString(String coordsString) {
        Pattern pattern = Pattern.compile("([A-Z])([0-9]+)");
        Matcher matcher = pattern.matcher(coordsString);
        if (matcher.find()) {
            return new AbstractMap.SimpleEntry<>(Integer.parseInt(matcher.group(2)), matcher.group(1).charAt(0));
        }
        return null;
    }


}
