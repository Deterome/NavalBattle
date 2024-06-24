package NavalBattleGame.GameRound;

public enum CommandToServer {

    CreateUser ("CreateUser"),
    PlayerIsReady ("PlayerIsReady"),
    UpdatePlayerInformation("LoadPlayerInformation");

    CommandToServer(String commandString) {
        this.commandString = commandString;
    }

    static CommandToServer getCommandByString(String commandString) {
        for (var command: CommandToServer.values()) {
            if (command.commandString.equals(commandString)) {
                return command;
            }
        }
        return null;
    }

    public String getStringOfCommand() {
        return this.commandString;
    }

    private String commandString;

}
