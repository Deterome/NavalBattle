package NavalBattleGame.GameUsers;

public enum PlayerAction {
    Attack ("Attack");

    PlayerAction(String actionString) {
        this.actionString = actionString;
    }

    public String getStringOfAction() {
        return this.actionString;
    }

    public static PlayerAction getActionByString(String actionString) {
        for (var action: PlayerAction.values()) {
            if (action.actionString.equals(actionString)) {
                return action;
            }
        }
        return null;
    }

    public static final String enumString = "PlayerAction";

    private String actionString;
}
