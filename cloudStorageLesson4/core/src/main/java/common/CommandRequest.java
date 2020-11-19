package common;

public class CommandRequest extends AbstractRequest {

    private CommandType commandType;
    private String arg1;
    private String arg2;

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public CommandRequest(CommandType commandType) {
        this.commandType = commandType;
    }

    public CommandRequest(CommandType commandType, String arg1, String arg2) {
        this.commandType = commandType;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public CommandRequest(CommandType commandType, String arg1) {
        this.commandType = commandType;
        this.arg1 = arg1;
    }
}
