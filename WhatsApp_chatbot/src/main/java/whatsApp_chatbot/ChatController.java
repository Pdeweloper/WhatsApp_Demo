package whatsApp_chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class ChatController {

 @Autowired
 private ChatService webhookService;

 @PostMapping
 public UserResponse receiveMessage(@RequestBody UserRequest request) {
     return webhookService.handleMessage(request);
 }
}