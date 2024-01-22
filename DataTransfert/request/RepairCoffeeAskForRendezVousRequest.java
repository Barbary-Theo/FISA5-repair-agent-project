package handsOn.circularEconomy.DataTransfert.request;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Product;

public class RepairCoffeeAskForRendezVousRequest implements MessageContent {

    private Product requestedProduct;


    public RepairCoffeeAskForRendezVousRequest() { }

    public RepairCoffeeAskForRendezVousRequest(Product requestedProduct) {
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
        return "Request if is able to repair the product " + this.requestedProduct.toString();
    }


}
