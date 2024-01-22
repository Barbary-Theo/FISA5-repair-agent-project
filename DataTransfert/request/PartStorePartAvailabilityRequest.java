package handsOn.circularEconomy.DataTransfert.request;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Part;
import handsOn.circularEconomy.data.Product;

public class PartStorePartAvailabilityRequest implements MessageContent {

    private Part partToRepair;
    private Product productToRepair;


    public PartStorePartAvailabilityRequest() { }

    public PartStorePartAvailabilityRequest(Part partToRepair, Product productToRepair) {
        this.partToRepair = partToRepair;
        this.productToRepair = productToRepair;
    }

    public Product getProductToRepair() {
        return productToRepair;
    }

    public void setProductToRepair(Product productToRepair) {
        this.productToRepair = productToRepair;
    }

    public Part getPartToRepair() {
        return partToRepair;
    }

    public void setPartToRepair(Part partToRepair) {
        this.partToRepair = partToRepair;
    }


    @Override
    public boolean isObjectToDisplay() {
        return false;
    }

    @Override
    public String toString() {
        return "I need to know if you have this part available " + partToRepair.getName();
    }

}
