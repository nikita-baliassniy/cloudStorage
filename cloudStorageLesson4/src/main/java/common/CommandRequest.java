package common;

public class CommandRequest extends AbstractRequest {

    private CommandType commandType;

    public CommandType getCommandType() {
        return commandType;
    }

    public CommandRequest(CommandType commandType) {
        this.commandType = commandType;
    }

}
