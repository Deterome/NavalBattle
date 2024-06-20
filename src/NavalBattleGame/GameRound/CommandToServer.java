package NavalBattleGame.GameRound;

public enum CommandToServer {

    CreateUser ("CreateUser");

    CommandToServer(String commandString) {
        this.commandString = commandString;
    }

    static CommandToServer CommandByString(String commandString) {
        for (var command: CommandToServer.values()) {
            if (command.commandString.equals(commandString)) {
                return command;
            }
        }
        return null;
    }

    public String getCommandString() {
        return this.commandString;
    }

    private String commandString;

}
