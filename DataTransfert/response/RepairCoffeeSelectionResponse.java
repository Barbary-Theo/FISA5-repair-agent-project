package handsOn.circularEconomy.DataTransfert.response;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Product;
import jade.core.AID;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RepairCoffeeSelectionResponse implements MessageContent {

    private Product requestedProduct;
    private AID repairCoffee;
    private LocalDate date;


    public RepairCoffeeSelectionResponse() { }

    public RepairCoffeeSelectionResponse(Product requestedProduct, AID repairCoffee, LocalDate date) {
        this.requestedProduct = requestedProduct;
        this.repairCoffee = repairCoffee;
        this.date = date;
    }


    public Product getRequestedProduct() {
        return requestedProduct;
    }

    public void setRequestedProduct(Product requestedProduct) {
        this.requestedProduct = requestedProduct;
    }

    public AID getRepairCoffee() {
        return repairCoffee;
    }

    public void setRepairCoffee(AID repairCoffee) {
        this.repairCoffee = repairCoffee;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDateFormatted() {
        return this.date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public boolean isObjectToDisplay() {
        return true;
    }

    @Override
    public String toString() {
        return "The rendez vous at " + this.repairCoffee.getLocalName() + " to help about " + this.requestedProduct.getName() + " the " + this.getDateFormatted() + " is booked";
    }

}
