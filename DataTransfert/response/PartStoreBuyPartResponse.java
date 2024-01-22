package handsOn.circularEconomy.DataTransfert.response;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Part;

public class PartStoreBuyPartResponse implements MessageContent {

    private Part part;


    public PartStoreBuyPartResponse() { }

    public PartStoreBuyPartResponse(Part part) {
        this.part = part;
    }


    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }


    @Override
    public boolean isObjectToDisplay() {
        return true;
    }

    @Override
    public String toString() {
        return "Part " + this.part.getName() + " removed from my stock, take it.";
    }

}
