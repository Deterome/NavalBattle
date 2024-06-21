package NavalBattleGame.GameRound;

public enum UserRole {
    Player ("Player"),
    Watcher ("Watcher"),
    Admin ("Admin");

    UserRole(String userRoleStr) {
        this.userRoleString = userRoleStr;
    }

    static UserRole getCommandByString(String userRoleStr) {
        for (var userRole: UserRole.values()) {
            if (userRole.userRoleString.equals(userRoleStr)) {
                return userRole;
            }
        }
        return null;
    }

    public String getStringOfUserRole() {
        return this.userRoleString;
    }

    private String userRoleString;
}
