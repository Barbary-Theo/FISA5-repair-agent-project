package handsOn.circularEconomy.DataTransfert.request;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Part;

public class PartStoreBuyPartRequest implements MessageContent {

    private Part part;
    private double price;


    public PartStoreBuyPartRequest() { }

    public PartStoreBuyPartRequest(Part part, double price) {
        this.part = part;
        this.price = price;
    }


    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    @Override
    public boolean isObjectToDisplay() {
        return false;
    }

    @Override
    public String toString() {
        return "I would like to buy " + this.part.getName() + " for " + this.price + " €";
    }

}
