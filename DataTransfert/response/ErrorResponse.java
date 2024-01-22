package handsOn.circularEconomy.DataTransfert.response;

import handsOn.circularEconomy.DataTransfert.MessageContent;

public class ErrorResponse implements MessageContent {

    private String message;


    public ErrorResponse() { }

    public ErrorResponse(String message) {
        this.message = message;
    }


    @Override
    public boolean isObjectToDisplay() {
        return true;
    }

    @Override
    public String toString() {
        return "Error unknown request " + this.message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
