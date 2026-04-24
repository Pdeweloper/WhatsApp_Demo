package whatsapp_chatbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebhookService {

 private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

 public WebhookResponse handleMessage(WebhookRequest request) {
     // 1) Log incoming message
     log.info("Incoming WhatsApp message from {}: {}", request.getFrom(), request.getMessage());

     // 2) Decide reply
     String text = request.getMessage();
     String reply;

     if (text == null) {
         reply = "I did not understand that.";
     } else {
         String normalized = text.trim().toLowerCase();
         switch (normalized) {
             case "hi":
             case "hello":
                 reply = "Hello";
                 break;
             case "bye":
             case "goodbye":
                 reply = "Goodbye";
                 break;
             default:
                 reply = "I can reply to 'Hi' and 'Bye' only in this demo.";
         }
     }

     // 3) Return response DTO
     return new WebhookResponse(reply);
 }
}