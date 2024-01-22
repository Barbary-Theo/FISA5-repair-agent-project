package handsOn.circularEconomy.DataTransfert.response;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Product;
import jade.core.AID;

public class RepairCoffeeAbleToRepairOrHelpResponse implements MessageContent {

    private Product requestedProduct;
    private boolean isAbleToRepair;
    private AID sender;


    public RepairCoffeeAbleToRepairOrHelpResponse() { }

    public RepairCoffeeAbleToRepairOrHelpResponse(Product requestedProduct, boolean isAbleToRepair, AID sender) {
        this.requestedProduct = requestedProduct;
        this.isAbleToRepair = isAbleToRepair;
        this.sender = sender;
    }


    public Product getRequestedProduct() {
        return requestedProduct;
    }

    public void setRequestedProduct(Product requestedProduct) {
        this.requestedProduct = requestedProduct;
    }

    public boolean isAbleToRepair() {
        return isAbleToRepair;
    }

    public void setIsAbleToRepair(boolean ableToRepair) {
        isAbleToRepair = ableToRepair;
    }

    public AID getSender() {
        return sender;
    }

    public void setSender(AID sender) {
        this.sender = sender;
    }

    @Override
    public boolean isObjectToDisplay() {
        return true;
    }

    @Override
    public String toString() {
        return this.isAbleToRepair ? this.requestedProduct.getName() + " -> I can repair it ! " : " -> This product is not in my specialities.";
    }

}
