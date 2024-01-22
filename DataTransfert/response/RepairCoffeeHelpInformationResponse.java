package handsOn.circularEconomy.DataTransfert.response;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import handsOn.circularEconomy.data.Product;
import jade.core.AID;

public class RepairCoffeeHelpInformationResponse implements MessageContent {

    private Product productToHelp;
    private boolean isRepairable = false;
    private boolean isAvailable = false;
    private AID partStoreToGoIfOkForSecondhand;
    private AID partStoreToGoIfNotOkForSecondhand;
    private Double priceIfOkForSecondhand;
    private Double priceIfNotOkForSecondhand;


    public RepairCoffeeHelpInformationResponse() { }

    public RepairCoffeeHelpInformationResponse(Product productToHelp, boolean isRepairable) {
        this.productToHelp = productToHelp;
        this.isRepairable = isRepairable;
    }

    public RepairCoffeeHelpInformationResponse(Product productToHelp, boolean isRepairable, AID partStoreToGoIfOkForSecondhand, AID partStoreToGoIfNotOkForSecondhand, Double priceIfOkForSecondhand, Double priceIfNotOkForSecondhand) {
        this.productToHelp = productToHelp;
        this.isRepairable = isRepairable;
        this.partStoreToGoIfOkForSecondhand = partStoreToGoIfOkForSecondhand;
        this.partStoreToGoIfNotOkForSecondhand = partStoreToGoIfNotOkForSecondhand;
        this.priceIfOkForSecondhand = priceIfOkForSecondhand;
        this.priceIfNotOkForSecondhand = priceIfNotOkForSecondhand;
        if(partStoreToGoIfOkForSecondhand != null && partStoreToGoIfNotOkForSecondhand != null) this.isAvailable = true;
    }


    public Product getProductToHelp() {
        return productToHelp;
    }

    public void setProductToHelp(Product productToHelp) {
        this.productToHelp = productToHelp;
    }

    public boolean isRepairable() {
        return isRepairable;
    }

    public void setRepairable(boolean repairable) {
        isRepairable = repairable;
    }

    public AID getPartStoreToGoIfOkForSecondhand() {
        return partStoreToGoIfOkForSecondhand;
    }

    public void setPartStoreToGoIfOkForSecondhand(AID partStoreToGoIfOkForSecondhand) {
        this.partStoreToGoIfOkForSecondhand = partStoreToGoIfOkForSecondhand;
    }

    public AID getPartStoreToGoIfNotOkForSecondhand() {
        return partStoreToGoIfNotOkForSecondhand;
    }

    public void setPartStoreToGoIfNotOkForSecondhand(AID partStoreToGoIfNotOkForSecondhand) {
        this.partStoreToGoIfNotOkForSecondhand = partStoreToGoIfNotOkForSecondhand;
    }

    public Double getPriceIfOkForSecondhand() {
        return priceIfOkForSecondhand;
    }

    public void setPriceIfOkForSecondhand(Double priceIfOkForSecondhand) {
        this.priceIfOkForSecondhand = priceIfOkForSecondhand;
    }

    public Double getPriceIfNotOkForSecondhand() {
        return priceIfNotOkForSecondhand;
    }

    public void setPriceIfNotOkForSecondhand(Double priceIfNotOkForSecondhand) {
        this.priceIfNotOkForSecondhand = priceIfNotOkForSecondhand;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }


    @Override
    public boolean isObjectToDisplay() {
        return true;
    }

    @Override
    public String toString() {
        if(this.isRepairable) {
            if(this.partStoreToGoIfOkForSecondhand == null && this.partStoreToGoIfNotOkForSecondhand == null) {
                return this.productToHelp.getName() + " is repairable, but not available in partStores";
            }
            else if(this.partStoreToGoIfOkForSecondhand != null && this.partStoreToGoIfNotOkForSecondhand != null) {
                return this.productToHelp.getName() + " is repairable, contact " + this.partStoreToGoIfOkForSecondhand.getLocalName() + " for a secondhand specialist for price " + this.priceIfOkForSecondhand + "€ or contact " + this.partStoreToGoIfNotOkForSecondhand.getLocalName() + " for a new part for " + this.priceIfNotOkForSecondhand + "€";
            }
            else if(this.partStoreToGoIfOkForSecondhand != null) {
                return this.productToHelp.getName() + " is repairable and only available in secondhand specialist, contact " + this.partStoreToGoIfOkForSecondhand.getLocalName() + " for " + this.priceIfOkForSecondhand + "€";
            }
            else {
                return this.productToHelp.getName() + " is repairable, contact " + this.partStoreToGoIfNotOkForSecondhand.getLocalName() + " for " + this.priceIfNotOkForSecondhand + "€";
            }
        }
        return this.productToHelp.getName() + " is not repairable...";
    }

}
