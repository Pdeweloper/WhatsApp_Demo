package whatsapp_chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

 @Autowired
 private WebhookService webhookService;

 @PostMapping
 public WebhookResponse receiveMessage(@RequestBody WebhookRequest request) {
     return webhookService.handleMessage(request);
 }
}