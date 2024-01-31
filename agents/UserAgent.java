package handsOn.circularEconomy.agents;

import handsOn.circularEconomy.DataTransfert.request.*;
import handsOn.circularEconomy.DataTransfert.response.*;
import handsOn.circularEconomy.data.BreakdownLevel;
import handsOn.circularEconomy.data.MessageId;
import handsOn.circularEconomy.data.Product;
import handsOn.circularEconomy.data.ProductType;
import handsOn.circularEconomy.gui.UserAgentWindow;
import handsOn.circularEconomy.utils.AgentExtension;
import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

public class UserAgent extends AgentExtension implements Serializable {
    private List<Product> products;
    private int skill;
    private UserAgentWindow window;
    private Product productToRepair;
    private double currentWallet = 0;
    private List<RepairCoffeeAbleToRepairOrHelpResponse> repairCoffeeAbleToRepair = new ArrayList<>();
    private List<RepairCoffeeAbleToRepairOrHelpResponse> repairCoffeeAbleToHelp = new ArrayList<>();
    private List<RepairCoffeeAskForRendezVousResponse> rendezVousPropositions = new ArrayList<>();
    private List<RepairCoffeeAskForRendezVousResponse> rendezVousToHelpPropositions = new ArrayList<>();
    private List<PartStorePartAvailabilityResponse> partsAvailabilityResponse = new ArrayList<>();

    @Override
    public void setup()
    {
        this.window = new UserAgentWindow(getLocalName(),this);
        window.setButtonActivated(true);
        //add a random skill
        Random hasard = new Random();
        skill = hasard.nextInt(5);
        Random random = new Random();
        currentWallet = 250.0 + (500.0 - 250.0) * random.nextDouble();
        println("hello, I have a skill = "+ skill);
        println("My wallet amount is " + new DecimalFormat("#.##").format(currentWallet) + "€");
        //add some products choosen randomly in the list Product.getListProducts()
        products = new ArrayList<>();
        int nbTypeOfProducts = ProductType.values().length;
        int nbPoductsByType = Product.NB_PRODS / nbTypeOfProducts;
        var existingProducts = Product.getListProducts();
        //add products
        for(int i=0; i<nbTypeOfProducts; i++)
            if(hasard.nextBoolean())
                try {
                    products.add(existingProducts.get(hasard.nextInt(nbPoductsByType) + (i*nbPoductsByType)));
                } catch (IndexOutOfBoundsException ignored) { }
        //we need at least one product
        while (products.isEmpty()) {
            try {
                products.add(existingProducts.get(hasard.nextInt(nbPoductsByType*nbTypeOfProducts)));
            } catch (IndexOutOfBoundsException ignored) { }
        }
        window.addProductsToCombo(products);
        println("Here are my objects : ");
        products.forEach(p->println("\t"+p));


        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (message != null) {

                    switch (message.getConversationId()) {
                        case MessageId.ERROR:
                            println("Error from " + message.getSender().getLocalName());
                            terminateToRepair();
                            break;
                        case MessageId.IS_PART_AVAILABLE_RESPONSE:
                            checkPartAvailabilityResponse(message);
                            break;
                        case MessageId.BUY_PART_RESPONSE:
                            repairPart(message);
                            break;
                        case MessageId.IS_ABLE_TO_REPAIR_RESPONSE:
                            isAbleToRepairResponse(message);
                            break;
                        case MessageId.ASK_FOR_RENDEZ_VOUS_TO_REPAIR_RESPONSE:
                            checkRendezVousPropositions(message);
                            break;
                        case MessageId.IS_ABLE_TO_HELP_RESPONSE:
                            isAbleToHelpResponse(message);
                            break;
                        case MessageId.ASK_FOR_RENDEZ_VOUS_TO_HELP_RESPONSE:
                            checkRendezVousToHelpPropositions(message);
                            break;
                        case MessageId.SELECT_RENDEZ_VOUS_TO_HELP_RESPONSE:
                            goToHelpRendezVous(message);
                            break;
                        case MessageId.GIVE_INFORMATION_ABOUT_PRODUCT:
                            getRepairCoffeeInformation(message);
                            break;
                        default:
                            println("Unknown request");
                    }

                } else block();

            }
        });

    }

    /**the window sends an evt to the agent*/
    @Override
    public void onGuiEvent(GuiEvent evt)
    {
        //if it is the OK button
        if(evt.getType()==UserAgentWindow.OK_EVENT)
        {
            //search about repair coffee
            var coffees = AgentServicesTools.searchAgents(this, "repair", "coffee");
            println("-".repeat(30));
            for(AID aid:coffees)
                println("found this repair coffee : " + aid.getLocalName());
            println("-".repeat(30));

            /* -- Beginning of the implementation -- */

            Product selectedProduct = window.getSelectedProduct();
            if(!products.contains(selectedProduct)) {
                println("This product is not in my product list");
                return;
            }
            this.productToRepair = selectedProduct;

            boolean isAbleToDetectBreakdown = isAbleToDetectBreakdown(selectedProduct);
            if(isAbleToDetectBreakdown) {
                println("I'm able to detect the breakdown -> " + selectedProduct.getDefault().getName() + ", breakdown level : " + selectedProduct.getBreakdownLevel());

                if(selectedProduct.getBreakdownLevel() == BreakdownLevel.DEFINITIVE) {
                    println(selectedProduct.getName() + " is definitely broken");
                    if(this.currentWallet >= selectedProduct.getPrice()) {
                        this.currentWallet -= selectedProduct.getPrice();
                        println("I'll bought a new product at distributors for " + selectedProduct.getPrice() + "€");
                    }
                    else println(selectedProduct.getName() +  " I don't have enough money to buy a new one ...");
                    terminateToRepair();
                }
                else {
                    println("I'll ask to partStore if they have " + selectedProduct.getDefault().getName());
                    askAvailablePartPrice(selectedProduct);
                }

            }
            else {
                println("I'm not able to detect the breakdown");
                askToRepairCoffeesRendezVousToCheck();
            }

        }
    }

    private void askToRepairCoffeesRendezVousToCheck() {
        var coffees = AgentServicesTools.searchAgents(this, "repair", "coffee");
        for (AID coffee : coffees) {
            sendMessage(coffee, new RepairCoffeeAbleToRepairOrHelpRequest(this.productToRepair), MessageId.IS_ABLE_TO_HELP, ACLMessage.REQUEST);
        }
    }

    private void repairPart(ACLMessage message) {
        try {
            PartStoreBuyPartResponse response = (PartStoreBuyPartResponse) message.getContentObject();
            if(this.productToRepair.getBreakdownLevel() < this.skill) {
                println("I'm able to repair this product, I'll do it !");
                terminateToRepair();
                return;
            }

            println("I'm not able to repair this product, I'll booked a rendez vous to repair coffee");
            var coffees = AgentServicesTools.searchAgents(this, "repair", "coffee");
            for (AID coffee : coffees) {
                sendMessage(coffee, new RepairCoffeeAbleToRepairOrHelpRequest(this.productToRepair), MessageId.IS_ABLE_TO_REPAIR, ACLMessage.REQUEST);
            }

        } catch (UnreadableException ignored) { terminateToRepair(); }
    }

    public void askAvailablePartPrice(Product requestedProduct) {
        var partsStores = AgentServicesTools.searchAgents(this, "repair", "partstore");
        for(AID partStore : partsStores) {
            sendMessage(partStore, new PartStorePartAvailabilityRequest(requestedProduct.getDefault(), requestedProduct), MessageId.IS_PART_AVAILABLE, ACLMessage.REQUEST);
        }

    }

    public void checkPartAvailabilityResponse(ACLMessage message) {
        try {
            PartStorePartAvailabilityResponse response = (PartStorePartAvailabilityResponse) message.getContentObject();
            this.partsAvailabilityResponse.add(response);

            int nbOfPartsStores = AgentServicesTools.searchAgents(this, "repair", "partstore").length;
            if(nbOfPartsStores == this.partsAvailabilityResponse.size()) selectPartStoreToBuyPart();

        } catch (UnreadableException ignored) { terminateToRepair(); }
    }


    public void selectPartStoreToBuyPart() {
        List<PartStorePartAvailabilityResponse> noneSecondHandSpecialists = new ArrayList<>();
        List<PartStorePartAvailabilityResponse> secondHandSpecialists = new ArrayList<>();
        this.partsAvailabilityResponse.forEach(response -> {
            if(response.isAvailable() && response.isSecondHandSpecialist()) secondHandSpecialists.add(response);
            else if(response.isAvailable() && !response.isSecondHandSpecialist()) noneSecondHandSpecialists.add(response);
        });

        PartStorePartAvailabilityResponse lowestPrice = null;

        if(noneSecondHandSpecialists.isEmpty() && secondHandSpecialists.isEmpty()) {
            println(this.productToRepair.getDefault().getName() + " is not available in any PartStore...");
            terminateToRepair();
            return;
        }
        else if(!noneSecondHandSpecialists.isEmpty() && !secondHandSpecialists.isEmpty()) {
            Random random = new Random();
            boolean isOkToBuySecondHand = random.nextBoolean();
            lowestPrice = getBestPartToBuy(isOkToBuySecondHand ? secondHandSpecialists : noneSecondHandSpecialists);
        }
        else if(!noneSecondHandSpecialists.isEmpty()) lowestPrice = getBestPartToBuy(noneSecondHandSpecialists);
        else lowestPrice = getBestPartToBuy(secondHandSpecialists);

        if(lowestPrice == null) {
            println("Enable to find the best part to buy...");
            terminateToRepair();
            return;
        }

        if(lowestPrice.getPrice() <= this.currentWallet) {
            println("Lowest price is " + lowestPrice.getPrice() + "€ by " + lowestPrice.getSender().getLocalName());
            this.currentWallet -= lowestPrice.getPrice();
            sendMessage(lowestPrice.getSender(), new PartStoreBuyPartRequest(lowestPrice.getPartToRepair(), lowestPrice.getPrice()), MessageId.BUY_PART, ACLMessage.REQUEST);
            return;
        }
        println("I can't buy this parts for " + lowestPrice + "€...");
        terminateToRepair();

    }

    public PartStorePartAvailabilityResponse getBestPartToBuy(List<PartStorePartAvailabilityResponse> availableResponse) {
        return availableResponse.stream()
                .filter(response -> response.getPrice() != null)
                .min(Comparator.comparingDouble(PartStorePartAvailabilityResponse::getPrice)).orElse(null);
    }


    public boolean isAbleToDetectBreakdown(Product product) {
        if(this.skill == 0) return false;
        else return product.getBreakdownLevel() <= this.skill;
    }

    public void println(String s){window.println(s);}

    @Override
    public void takeDown(){println("bye !!!");}

    public void terminateToRepair() {
        Product productTerminated = this.productToRepair;
        this.productToRepair = null;
        this.repairCoffeeAbleToRepair = new ArrayList<>();
        this.repairCoffeeAbleToHelp = new ArrayList<>();
        this.partsAvailabilityResponse = new ArrayList<>();
        this.rendezVousPropositions = new ArrayList<>();
        this.rendezVousToHelpPropositions = new ArrayList<>();
        this.products.remove(productTerminated);
        window.removeProductsToCombo(productTerminated);
        println("-".repeat(10) + " product process terminated " + "-".repeat(10));
        println("Current wallet : " + this.currentWallet + " €");
    }

    public void isAbleToRepairResponse(ACLMessage message) {
        try {
            RepairCoffeeAbleToRepairOrHelpResponse response = (RepairCoffeeAbleToRepairOrHelpResponse) message.getContentObject();
            this.repairCoffeeAbleToRepair.add(response);

            if(response.isAbleToRepair()) {
                println(message.getSender().getLocalName() + " is able to repair " + response.getRequestedProduct() + ", I'll ask for a rendez vous to repair");
                sendMessage(message.getSender(), new RepairCoffeeAskForRendezVousRequest(response.getRequestedProduct()), MessageId.ASK_FOR_RENDEZ_VOUS_TO_REPAIR, ACLMessage.REQUEST);
            }
        } catch (UnreadableException ignored) { terminateToRepair(); }
    }

    public void isAbleToHelpResponse(ACLMessage message) {
        try {
            RepairCoffeeAbleToRepairOrHelpResponse response = (RepairCoffeeAbleToRepairOrHelpResponse) message.getContentObject();
            this.repairCoffeeAbleToHelp.add(response);

            if(response.isAbleToRepair()) {
                println(message.getSender().getLocalName() + " is able to help " + response.getRequestedProduct() + ", I'll ask for a rendez vous to help");
                sendMessage(message.getSender(), new RepairCoffeeAskForRendezVousRequest(response.getRequestedProduct()), MessageId.ASK_FOR_RENDEZ_VOUS_TO_HELP, ACLMessage.REQUEST);
            }
        } catch (UnreadableException ignored) { terminateToRepair(); }
    }

    public void checkRendezVousPropositions(ACLMessage message) {
        try {
            RepairCoffeeAskForRendezVousResponse response = (RepairCoffeeAskForRendezVousResponse) message.getContentObject();
            rendezVousPropositions.add(response);
            println(message.getSender().getLocalName() + " proposed a rendez vous to repair the " + response.getDateFormatted() + " for " + response.getRequestedProduct());

            boolean areAllRendezVousCollected = this.rendezVousPropositions.size() == this.repairCoffeeAbleToRepair.stream().filter(RepairCoffeeAbleToRepairOrHelpResponse::isAbleToRepair).toList().size();
            if(areAllRendezVousCollected) {
                println("I have all rendez vous propositions to repair for " + response.getRequestedProduct().getName());
                selectTheBestRendezVousToRepair();
            }

        } catch (UnreadableException ignored) { terminateToRepair(); }

    }

    public void checkRendezVousToHelpPropositions(ACLMessage message) {
        try {
            RepairCoffeeAskForRendezVousResponse response = (RepairCoffeeAskForRendezVousResponse) message.getContentObject();
            rendezVousToHelpPropositions.add(response);
            println(message.getSender().getLocalName() + " proposed a rendez vous to help the " + response.getDateFormatted() + " for " + response.getRequestedProduct());

            boolean areAllRendezVousToHelpCollected = this.rendezVousToHelpPropositions.size() == this.repairCoffeeAbleToHelp.stream().filter(RepairCoffeeAbleToRepairOrHelpResponse::isAbleToRepair).toList().size();
            if(areAllRendezVousToHelpCollected) {
                println("I have all rendez vous propositions to help for " + response.getRequestedProduct().getName());
                selectTheBestRendezVousToHelp();
            }

        } catch (UnreadableException ignored) { terminateToRepair(); }

    }

    public void selectTheBestRendezVousToRepair() {
        RepairCoffeeAskForRendezVousResponse bestRendezVous = this.rendezVousPropositions.stream().min(Comparator.comparing(RepairCoffeeAskForRendezVousResponse::getDate)).orElse(null);
        if(bestRendezVous != null && this.currentWallet >= 5) {
            this.currentWallet -= 5;
            sendMessage(bestRendezVous.getSender(), new RepairCoffeeSelectionRequest(this.productToRepair, bestRendezVous.getSender(), bestRendezVous.getDate()), MessageId.SELECT_RENDEZ_VOUS_TO_REPAIR, ACLMessage.REQUEST);
            terminateToRepair();
        }
        else if(this.currentWallet < 5) println("I don't have enough money to reserved the rendez vous...");
        terminateToRepair();
    }

    public void selectTheBestRendezVousToHelp() {
        RepairCoffeeAskForRendezVousResponse bestRendezVousToHelp = this.rendezVousToHelpPropositions.stream().min(Comparator.comparing(RepairCoffeeAskForRendezVousResponse::getDate)).orElse(null);
        if(bestRendezVousToHelp != null && this.currentWallet >= 5) {
            this.currentWallet -= 5;
            sendMessage(bestRendezVousToHelp.getSender(), new RepairCoffeeSelectionRequest(this.productToRepair, bestRendezVousToHelp.getSender(), bestRendezVousToHelp.getDate()), MessageId.SELECT_RENDEZ_VOUS_TO_HELP, ACLMessage.REQUEST);
            return;
        }
        else if(this.currentWallet < 5) println("I don't have enough money to reserved the rendez vous...");
        terminateToRepair();
    }

    public void goToHelpRendezVous(ACLMessage message) {
        try {
            RepairCoffeeSelectionResponse response = (RepairCoffeeSelectionResponse) message.getContentObject();
            sendMessage(message.getSender(), new RepairCoffeeGoToHelpRendezVousRequest(response.getRequestedProduct(), response.getRepairCoffee(), response.getDate()), MessageId.GO_TO_REPAIR_COFFEE_HELP_RENDEZ_VOUS, ACLMessage.REQUEST);
        } catch (UnreadableException ignored) { terminateToRepair(); }
    }

    public void getRepairCoffeeInformation(ACLMessage message) {
        try {
            RepairCoffeeHelpInformationResponse response = (RepairCoffeeHelpInformationResponse) message.getContentObject();
            if(response.isRepairable() && response.isAvailable()) {
                // if parts available in secondhand partStore and not secondhand partStore
                if (response.getPartStoreToGoIfNotOkForSecondhand() != null && response.getPartStoreToGoIfOkForSecondhand() != null) {
                    Random random = new Random();
                    boolean isOkToBuySecondHand = random.nextBoolean();
                    if(isOkToBuySecondHand) {
                        if(response.getPriceIfOkForSecondhand() <= this.currentWallet) {
                            //buy second hand
                            this.currentWallet -= response.getPriceIfOkForSecondhand();
                            sendMessage(response.getPartStoreToGoIfOkForSecondhand(), new PartStoreBuyPartRequest(response.getProductToHelp().getDefault(), response.getPriceIfOkForSecondhand()), MessageId.BUY_PART, ACLMessage.REQUEST);
                            return;
                        }
                        else {
                            println("I don't have enough to buy parts for " + response.getPriceIfOkForSecondhand() + "€...");
                            terminateToRepair();
                            return;
                        }
                    }
                    else {
                        if(response.getPriceIfOkForSecondhand() <= this.currentWallet) {
                            //buy none second hand
                            this.currentWallet -= response.getPriceIfNotOkForSecondhand();
                            sendMessage(response.getPartStoreToGoIfNotOkForSecondhand(), new PartStoreBuyPartRequest(response.getProductToHelp().getDefault(), response.getPriceIfNotOkForSecondhand()), MessageId.BUY_PART, ACLMessage.REQUEST);
                            return;
                        }
                        else {
                            println("I don't have enough to buy parts for " + response.getPriceIfOkForSecondhand() + "€...");
                            terminateToRepair();
                            return;
                        }
                    }
                }
                // if parts only available in not secondhand partStore
                else if(response.getPartStoreToGoIfNotOkForSecondhand() != null) {
                    if(response.getPriceIfOkForSecondhand() <= this.currentWallet) {
                        //buy none second hand
                        this.currentWallet -= response.getPriceIfNotOkForSecondhand();
                        sendMessage(response.getPartStoreToGoIfNotOkForSecondhand(), new PartStoreBuyPartRequest(response.getProductToHelp().getDefault(), response.getPriceIfNotOkForSecondhand()), MessageId.BUY_PART, ACLMessage.REQUEST);
                        return;
                    }
                    else {
                        println("I don't have enough to buy parts for " + response.getPriceIfOkForSecondhand() + "€...");
                        terminateToRepair();
                        return;
                    }
                }
                // if parts only available in secondhand partStore
                else {
                    Random random = new Random();
                    boolean isOkToBuySecondHand = random.nextBoolean();
                    if(isOkToBuySecondHand) {
                        if(response.getPriceIfOkForSecondhand() <= this.currentWallet) {
                            this.currentWallet -= response.getPriceIfOkForSecondhand();
                            sendMessage(response.getPartStoreToGoIfOkForSecondhand(), new PartStoreBuyPartRequest(response.getProductToHelp().getDefault(), response.getPriceIfOkForSecondhand()), MessageId.BUY_PART, ACLMessage.REQUEST);
                            return;
                        }
                        else {
                            println("I don't have enough to buy parts for " + response.getPriceIfOkForSecondhand() + "€...");
                            terminateToRepair();
                            return;
                        }
                    }
                    else {
                        println(response.getProductToHelp().getDefault().getName() + " is only available in second hand partStore but I don't want second hand");
                        buyToDistributor();
                        return;
                    }
                }
            }
            // part is repairable but any partStore have part
            else if(response.isRepairable()) {
                println(response.getProductToHelp().getDefault().getName() + " is not available in partStore...");
                buyToDistributor();
                return;
            }
            // part is not repairable
            else {
                println(response.getProductToHelp().getDefault().getName() + " is not repairable");
                buyToDistributor();
                return;
            }
        } catch (UnreadableException ignored) { terminateToRepair(); }
    }

    private void buyToDistributor() {
        if(this.currentWallet >= this.productToRepair.getPrice()) {
            this.currentWallet -= this.productToRepair.getPrice();
            println("I'll bought a new product at distributors for " + this.productToRepair.getPrice() + "€");
        }
        else println(this.productToRepair.getName() +  " I don't have enough money to buy a new one ...");
        terminateToRepair();
    }

}
