package NavalBattleGame.GameRound;

public enum CommandToClient {
    SetUsersList ("SetUsersList"),
    StopWaitingForPlayers ("StopWaitingForPlayers"),
    StartMatch ("StartMatch"),
    SendPlayerInformationToServer ("SendPlayerInformationToServer"),
    UpdatePlayerInformation("UpdatePlayerInformation"),
    ProcessPlayerMove("ProcessPlayerMove"),
    AddRoleToUser ("AddRoleToUser"),
    DeleteUserRole ("DeleteUserRole");

    CommandToClient(String commandString) {
        this.commandString = commandString;
    }

    static CommandToClient getCommandByString(String commandString) {
        for (var command: CommandToClient.values()) {
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
