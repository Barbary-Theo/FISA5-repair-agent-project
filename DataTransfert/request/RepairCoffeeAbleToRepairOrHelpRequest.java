package handsOn.circularEconomy.DataTransfert.request;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Product;

public class RepairCoffeeAbleToRepairOrHelpRequest implements MessageContent {

    private Product requestedProduct;


    public RepairCoffeeAbleToRepairOrHelpRequest() { }

    public RepairCoffeeAbleToRepairOrHelpRequest(Product requestedProduct) {
        this.requestedProduct = requestedProduct;
    }

    public Product getRequestedProduct() {
        return requestedProduct;
    }

    public void setRequestedProduct(Product requestedProduct) {
        this.requestedProduct = requestedProduct;
    }

    @Override
    public boolean isObjectToDisplay() {
        return false;
    }

    @Override
    public String toString() {
        return "Request if coffee repair is able to repair/help for the product " + this.requestedProduct.toString();
    }

}
