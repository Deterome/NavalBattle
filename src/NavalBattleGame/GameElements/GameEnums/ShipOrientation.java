package NavalBattleGame.GameElements.GameEnums;

public enum ShipOrientation {
    Right,
    Down,
    Left,
    Up;

    static private final ShipOrientation[] VALUES = values();
    static private int currentOrientationId;

    public ShipOrientation nextOrientation() {
        currentOrientationId = (currentOrientationId + 1) % VALUES.length;
        return VALUES[currentOrientationId];
    }

}
