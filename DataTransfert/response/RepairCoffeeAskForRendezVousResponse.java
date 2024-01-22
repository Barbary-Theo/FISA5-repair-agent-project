package handsOn.circularEconomy.DataTransfert.response;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Product;
import jade.core.AID;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RepairCoffeeAskForRendezVousResponse implements MessageContent {

    private Product requestedProduct;
    private LocalDate date;
    private AID sender;


    public RepairCoffeeAskForRendezVousResponse() { }

    public RepairCoffeeAskForRendezVousResponse(Product requestedProduct, LocalDate date, AID sender) {
        this.requestedProduct = requestedProduct;
        this.date = date;
        this.sender = sender;
    }


    public Product getRequestedProduct() {
        return requestedProduct;
    }

    public void setRequestedProduct(Product requestedProduct) {
        this.requestedProduct = requestedProduct;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AID getSender() {
        return sender;
    }

    public void setSender(AID sender) {
        this.sender = sender;
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
        return this.requestedProduct.getName() + " -> I propose a rendez vous to repair or help the " + getDateFormatted();
    }

}
