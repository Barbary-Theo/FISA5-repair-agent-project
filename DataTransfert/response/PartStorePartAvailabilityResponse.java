package handsOn.circularEconomy.DataTransfert.response;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Part;
import handsOn.circularEconomy.data.Product;
import jade.core.AID;

public class PartStorePartAvailabilityResponse implements MessageContent {

    private boolean isAvailable;
    private Part partToRepair;
    private Product productToRepair;
    private Double price;
    private AID sender;
    private boolean isSecondHandSpecialist = false;


    public PartStorePartAvailabilityResponse() { }

    public PartStorePartAvailabilityResponse(AID sender) {
        this.isAvailable = false;
        this.sender = sender;
        this.price = null;
        this.partToRepair = null;
        this.productToRepair = null;
    }

    public PartStorePartAvailabilityResponse(AID sender, Part partToRepair, Product productToRepair) {
        this.isAvailable = false;
        this.sender = sender;
        this.price = null;
        this.partToRepair = partToRepair;
        this.productToRepair = productToRepair;
    }

    public PartStorePartAvailabilityResponse(boolean isAvailable, Part partToRepair, Double price, AID sender, boolean isSecondHandSpecialist, Product productToRepair) {
        this.isAvailable = isAvailable;
        this.partToRepair = partToRepair;
        this.price = price;
        this.sender = sender;
        this.isSecondHandSpecialist = isSecondHandSpecialist;
        this.productToRepair = productToRepair;
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Part getPartToRepair() {
        return partToRepair;
    }

    public void setPartToRepair(Part partToRepair) {
        this.partToRepair = partToRepair;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public AID getSender() {
        return sender;
    }

    public void setSender(AID sender) {
        this.sender = sender;
    }

    public boolean isSecondHandSpecialist() {
        return isSecondHandSpecialist;
    }

    public void setSecondHandSpecialist(boolean secondHandSpecialist) {
        isSecondHandSpecialist = secondHandSpecialist;
    }

    public Product getProductToRepair() {
        return productToRepair;
    }

    public void setProductToRepair(Product productToRepair) {
        this.productToRepair = productToRepair;
    }

    @Override
    public boolean isObjectToDisplay() {
        return true;
    }

    @Override
    public String toString() {
        if(this.isAvailable) return partToRepair.getName() + " is available for " + this.price + "€ taxes included";
        else return "Part requested is not available";
    }

}
