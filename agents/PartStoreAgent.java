package handsOn.circularEconomy.agents;

import handsOn.circularEconomy.DataTransfert.request.PartStoreBuyPartRequest;
import handsOn.circularEconomy.DataTransfert.request.PartStorePartAvailabilityRequest;
import handsOn.circularEconomy.DataTransfert.request.RepairCoffeeAskForRendezVousRequest;
import handsOn.circularEconomy.DataTransfert.response.ErrorResponse;
import handsOn.circularEconomy.DataTransfert.response.PartStoreBuyPartResponse;
import handsOn.circularEconomy.DataTransfert.response.PartStorePartAvailabilityResponse;
import handsOn.circularEconomy.DataTransfert.response.RepairCoffeeAskForRendezVousResponse;
import handsOn.circularEconomy.data.MessageId;
import handsOn.circularEconomy.data.Part;
import handsOn.circularEconomy.utils.UtilsMessage;
import jade.core.AgentServicesTools;
import jade.core.behaviours.CyclicBehaviour;
import jade.gui.AgentWindowed;
import jade.gui.SimpleWindow4Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.awt.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;
import java.util.List;


/** class that represents a part-Store agent.
 * It is declared in the service repair-partstore.
 * It an infitine number of specific part wih a pecific cost ( up to 30% more than the standard price)
 * @author emmanueladam
 * */
public class PartStoreAgent extends UtilsMessage implements Serializable {

    List<Part> parts;
    private boolean isSecondHandSpecialist = false;

    @Override
    public void setup(){
        this.window = new SimpleWindow4Agent(getLocalName(),this);
        this.window.setBackgroundTextColor(Color.LIGHT_GRAY);
        AgentServicesTools.register(this, "repair", "partstore");
        Random hasard = new Random();
        this.isSecondHandSpecialist = hasard.nextBoolean();
        println("hello, I'm just registered as a parts-store" + (this.isSecondHandSpecialist ? " specialized in second-hand" : ""));
        println("do you want some special parts ?");
        parts = new ArrayList<>();
        var existingParts = Part.getListParts();
        Collections.shuffle(existingParts);
        for(Part p : existingParts)
            // PartStore can't have part 4 of an object and a maximum of 10 elements
            if(!p.getName().contains("part4") && parts.size() < 10)
                parts.add(new Part(p.getName(), p.getType(), p.getStandardPrice()*(1+Math.random()*.3)));
        //we need at least one part
        if(parts.isEmpty()) parts.add(existingParts.get(hasard.nextInt(existingParts.size())));
        println("here are the parts I sell : ");
        parts.forEach(p->println("\t"+p));

        //registration to the yellow pages (Directory Facilitator Agent)

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                if (message != null) {

                    switch (message.getConversationId()) {
                        case MessageId.IS_PART_AVAILABLE:
                            checkPartAvailability(message);
                            break;
                        case MessageId.IS_PART_AVAILABLE_BY_REPAIR_COFFEE:
                            checkPartAvailabilityForRepairCoffee(message);
                            break;
                        case MessageId.BUY_PART:
                            removePartFromStock(message);
                            break;
                        default:
                            sendMessage(message.getSender(), new ErrorResponse(message.getConversationId()), MessageId.ERROR, ACLMessage.INFORM);
                            println("Unknown request");
                    }

                } else block();

            }
        });

    }

    private void checkPartAvailability(ACLMessage message) {
        try {
            PartStorePartAvailabilityRequest request = (PartStorePartAvailabilityRequest) message.getContentObject();
            Part partAvailable = this.parts.stream().filter(part -> part.getName().equals(request.getPartToRepair().getName())).findFirst().orElse(null);
            PartStorePartAvailabilityResponse responseObject = partAvailable == null ? new PartStorePartAvailabilityResponse(this.getAID()) : new PartStorePartAvailabilityResponse(true, partAvailable, partAvailable.getStandardPrice() + (isSecondHandSpecialist ? 15 : 30), this.getAID(), this.isSecondHandSpecialist, request.getProductToRepair());
            sendMessage(message.getSender(), responseObject, MessageId.IS_PART_AVAILABLE_RESPONSE, ACLMessage.INFORM);
        } catch (UnreadableException ignored) { }
    }

    private void checkPartAvailabilityForRepairCoffee(ACLMessage message) {
        try {
            PartStorePartAvailabilityRequest request = (PartStorePartAvailabilityRequest) message.getContentObject();
            Part partAvailable = this.parts.stream().filter(part -> part.getName().equals(request.getPartToRepair().getName())).findFirst().orElse(null);
            PartStorePartAvailabilityResponse responseObject = partAvailable == null ? new PartStorePartAvailabilityResponse(this.getAID(), request.getPartToRepair(), request.getProductToRepair()) : new PartStorePartAvailabilityResponse(true, partAvailable, partAvailable.getStandardPrice() + (isSecondHandSpecialist ? 15 : 30), this.getAID(), this.isSecondHandSpecialist, request.getProductToRepair());
            sendMessage(message.getSender(), responseObject, MessageId.IS_PART_AVAILABLE_BY_REPAIR_COFFEE_RESPONSE, ACLMessage.REQUEST);
        } catch (UnreadableException ignored) { }
    }

    private void removePartFromStock(ACLMessage message) {
        try {
            PartStoreBuyPartRequest request = (PartStoreBuyPartRequest) message.getContentObject();
            this.parts.remove(request.getPart());
            sendMessage(message.getSender(), new PartStoreBuyPartResponse(request.getPart()), MessageId.BUY_PART_RESPONSE, ACLMessage.INFORM);
        } catch (UnreadableException ignored) { }
    }

}
