package handsOn.circularEconomy.agents;

import handsOn.circularEconomy.DataTransfert.request.*;
import handsOn.circularEconomy.DataTransfert.response.*;
import handsOn.circularEconomy.data.*;
import handsOn.circularEconomy.utils.UtilsMessage;
import jade.core.AID;
import jade.core.AgentServicesTools;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.SimpleWindow4Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class RepairCoffeeAgent extends UtilsMessage implements Serializable {

    private List<ProductType> specialities;
    private List<PartStorePartAvailabilityResponse> partsAvailabilityResponse = new ArrayList<>();
    private AID checkPartCustomer;


    @Override
    public void setup(){
        this.window = new SimpleWindow4Agent(getLocalName(),this);
        this.window.setBackgroundTextColor(Color.orange);
        println("hello, do you want coffee ?");
        var hasard = new Random();
        specialities = new ArrayList<>();
        for(ProductType type : ProductType.values())
            if(hasard.nextBoolean()) specialities.add(type);
        //we need at least one speciality
        if(specialities.isEmpty()) specialities.add(ProductType.values()[hasard.nextInt(ProductType.values().length)]);
        println("I have these specialities : ");
        specialities.forEach(p->println("\t"+p));
        //registration to the yellow pages (Directory Facilitator Agent)
        AgentServicesTools.register(this, "repair", "coffee");
        println("I'm just registered as a repair-coffee");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                if (message != null) {

                    switch (message.getConversationId()) {
                        case MessageId.IS_ABLE_TO_REPAIR:
                            isAbleToRepair(message);
                            break;
                        case MessageId.ASK_FOR_RENDEZ_VOUS_TO_REPAIR:
                            proposeRendezVousToRepair(message);
                            break;
                        case MessageId.SELECT_RENDEZ_VOUS_TO_REPAIR:
                            bookedRendezVousToRepair(message);
                            break;
                        case MessageId.IS_ABLE_TO_HELP:
                            isAbleToHelp(message);
                            break;
                        case MessageId.ASK_FOR_RENDEZ_VOUS_TO_HELP:
                            proposeRendezVousToHelp(message);
                            break;
                        case MessageId.SELECT_RENDEZ_VOUS_TO_HELP:
                            bookedRendezVousToHelp(message);
                            break;
                        case MessageId.GO_TO_REPAIR_COFFEE_HELP_RENDEZ_VOUS:
                            giveInformationAboutBreakdown(message);
                            break;
                        case MessageId.IS_PART_AVAILABLE_BY_REPAIR_COFFEE_RESPONSE:
                            checkPartAvailabilityResponse(message);
                            break;
                        default:
                            sendMessage(message.getSender(), new ErrorResponse(message.getConversationId()), MessageId.ERROR, ACLMessage.INFORM);
                            println("Unknown request");
                    }

                } else block();

            }
        });
    }

    private void analyseProduct(Product p) {
        var part = p.getDefault();
        println("Here is the faulty part : " + part + " for the object : " + p);
    }

    private void isAbleToRepair(ACLMessage message) {
        try {
            RepairCoffeeAbleToRepairOrHelpRequest request = (RepairCoffeeAbleToRepairOrHelpRequest) message.getContentObject();
            ProductType requestProductType = request.getRequestedProduct().getType();
            sendMessage(message.getSender(), new RepairCoffeeAbleToRepairOrHelpResponse(request.getRequestedProduct(), specialities.stream().anyMatch(specility -> Objects.equals(requestProductType, specility)), this.getAID()), MessageId.IS_ABLE_TO_REPAIR_RESPONSE, ACLMessage.INFORM);
        } catch (UnreadableException ignored) { }
    }

    private void isAbleToHelp(ACLMessage message) {
        try {
            RepairCoffeeAbleToRepairOrHelpRequest request = (RepairCoffeeAbleToRepairOrHelpRequest) message.getContentObject();
            ProductType requestProductType = request.getRequestedProduct().getType();
            sendMessage(message.getSender(), new RepairCoffeeAbleToRepairOrHelpResponse(request.getRequestedProduct(), specialities.stream().anyMatch(specility -> Objects.equals(requestProductType, specility)), this.getAID()), MessageId.IS_ABLE_TO_HELP_RESPONSE, ACLMessage.INFORM);
        } catch (UnreadableException ignored) { }
    }

    private void proposeRendezVousToRepair(ACLMessage message) {
        try {
            RepairCoffeeAskForRendezVousRequest request = (RepairCoffeeAskForRendezVousRequest) message.getContentObject();
            Random random = new Random();
            sendMessage(message.getSender(), new RepairCoffeeAskForRendezVousResponse(request.getRequestedProduct(), LocalDate.now().plusDays(random.nextInt(3) + 1), this.getAID()), MessageId.ASK_FOR_RENDEZ_VOUS_TO_REPAIR_RESPONSE, ACLMessage.INFORM);
        } catch (UnreadableException ignored) { }
    }

    private void proposeRendezVousToHelp(ACLMessage message) {
        try {
            RepairCoffeeAskForRendezVousRequest request = (RepairCoffeeAskForRendezVousRequest) message.getContentObject();
            Random random = new Random();
            sendMessage(message.getSender(), new RepairCoffeeAskForRendezVousResponse(request.getRequestedProduct(), LocalDate.now().plusDays(random.nextInt(3) + 1), this.getAID()), MessageId.ASK_FOR_RENDEZ_VOUS_TO_HELP_RESPONSE, ACLMessage.INFORM);
        } catch (UnreadableException ignored) { }
    }

    private void bookedRendezVousToRepair(ACLMessage message) {
        try {
            RepairCoffeeSelectionRequest request = (RepairCoffeeSelectionRequest) message.getContentObject();
            println("Booked a rendez vous to repair the " + request.getDateFormatted() + " for " + message.getSender().getLocalName());
            println(request.getRequestedProduct().getName() + " of " + message.getSender().getLocalName() + " is repaired");
            this.partsAvailabilityResponse = new ArrayList<>();
        } catch (UnreadableException ignored) { }
    }

    private void bookedRendezVousToHelp(ACLMessage message) {
        try {
            RepairCoffeeSelectionRequest request = (RepairCoffeeSelectionRequest) message.getContentObject();
            println("Booked a rendez vous to help the " + request.getDateFormatted() + " for " + message.getSender().getLocalName());
            sendMessage(message.getSender(), new RepairCoffeeSelectionResponse(request.getRequestedProduct(), this.getAID(), request.getDate()), MessageId.SELECT_RENDEZ_VOUS_TO_HELP_RESPONSE, ACLMessage.INFORM);
        } catch (UnreadableException ignored) { }
    }

    private void giveInformationAboutBreakdown(ACLMessage message) {
        try {
            RepairCoffeeGoToHelpRendezVousRequest request = (RepairCoffeeGoToHelpRendezVousRequest) message.getContentObject();
            Part faultyPart = request.getRequestedProduct().getDefault();
            analyseProduct(request.getRequestedProduct());
            boolean isRepairable = false;
            if(request.getRequestedProduct().getBreakdownLevel() == BreakdownLevel.DEFINITIVE) {
                println("Faulty part is not repairable...");
                sendMessage(message.getSender(), new RepairCoffeeHelpInformationResponse(request.getRequestedProduct(), false), MessageId.GIVE_INFORMATION_ABOUT_PRODUCT, ACLMessage.INFORM);
                return;
            }

            this.checkPartCustomer = message.getSender();
            var partsStores = AgentServicesTools.searchAgents(this, "repair", "partstore");
            for(AID partStore : partsStores) {
                sendMessage(partStore, new PartStorePartAvailabilityRequest(request.getRequestedProduct().getDefault(), request.getRequestedProduct()), MessageId.IS_PART_AVAILABLE_BY_REPAIR_COFFEE, ACLMessage.REQUEST);
            }

        } catch (UnreadableException ignored) { }
    }

    public void checkPartAvailabilityResponse(ACLMessage message) {
        try {
            PartStorePartAvailabilityResponse response = (PartStorePartAvailabilityResponse) message.getContentObject();
            this.partsAvailabilityResponse.add(response);

            int nbOfPartsStores = AgentServicesTools.searchAgents(this, "repair", "partstore").length;
            if(nbOfPartsStores == this.partsAvailabilityResponse.size()) selectPartStoreToBuyPart(response);

        } catch (UnreadableException ignored) { this.partsAvailabilityResponse = new ArrayList<>(); this.checkPartCustomer = null; }
    }

    public void selectPartStoreToBuyPart(PartStorePartAvailabilityResponse partStoreResponse) {
        List<PartStorePartAvailabilityResponse> noneSecondHandSpecialists = new ArrayList<>();
        List<PartStorePartAvailabilityResponse> secondHandSpecialists = new ArrayList<>();
        this.partsAvailabilityResponse.forEach(response -> {
            if(response.isAvailable() && response.isSecondHandSpecialist()) secondHandSpecialists.add(response);
            else if(response.isAvailable() && !response.isSecondHandSpecialist()) noneSecondHandSpecialists.add(response);
        });

        PartStorePartAvailabilityResponse lowestPriceNoneSecondHand = getBestPartToBuy(noneSecondHandSpecialists);
        PartStorePartAvailabilityResponse lowestPriceSecondHand = getBestPartToBuy(secondHandSpecialists);

        sendMessage(this.checkPartCustomer, new RepairCoffeeHelpInformationResponse(partStoreResponse.getProductToRepair(), true, lowestPriceSecondHand != null ? lowestPriceSecondHand.getSender() : null, lowestPriceNoneSecondHand != null ? lowestPriceNoneSecondHand.getSender() : null, lowestPriceSecondHand != null ? lowestPriceSecondHand.getPrice() : null, lowestPriceNoneSecondHand != null ? lowestPriceNoneSecondHand.getPrice() : null), MessageId.GIVE_INFORMATION_ABOUT_PRODUCT, ACLMessage.INFORM);
        this.checkPartCustomer = null;
        this.partsAvailabilityResponse = new ArrayList<>();
    }

    public PartStorePartAvailabilityResponse getBestPartToBuy(List<PartStorePartAvailabilityResponse> availableResponse) {
        return availableResponse.stream()
                .filter(response -> response.getPrice() != null)
                .min(Comparator.comparingDouble(PartStorePartAvailabilityResponse::getPrice)).orElse(null);
    }


}
