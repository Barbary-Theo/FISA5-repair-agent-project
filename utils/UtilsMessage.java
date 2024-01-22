package handsOn.circularEconomy.utils;

import handsOn.circularEconomy.DataTransfert.MessageContent;
import jade.core.AID;
import jade.gui.AgentWindowed;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.io.Serializable;

public class UtilsMessage extends AgentWindowed implements Serializable {

    public void sendMessage(AID targetAgent, MessageContent messageContent, String messageID, Integer messageType) {
        ACLMessage replyMessage = new ACLMessage(messageType);
        replyMessage.addReceiver(targetAgent);
        replyMessage.setConversationId(messageID);
        if(messageContent.isObjectToDisplay()) println(messageContent.toString());
        try { replyMessage.setContentObject(messageContent); }
        catch (IOException e) { throw new RuntimeException(e); }
        send(replyMessage);
    }

}
