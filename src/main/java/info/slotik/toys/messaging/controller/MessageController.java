package info.slotik.toys.messaging.controller;

import info.slotik.toys.messaging.entity.Message;
import info.slotik.toys.messaging.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.net.URI;
import java.util.List;

@Controller
public class MessageController
{
    @Value("${messages.base.path}")
    private String basePath;

    private MessageService service;

    @Autowired
    MessageController(MessageService service)
    {
        this.service = service;
    }

    @RequestMapping(
        method = RequestMethod.GET,
        path = "${messages.base.path}"
    )
    public ResponseEntity<List<Message>> getAllMessages()
    {
        return ResponseEntity.ok().body(service.findAll());
    }

    @RequestMapping(
        method = RequestMethod.POST,
        path = "${messages.base.path}"
    )
    public ResponseEntity<Message> createMessage(RequestEntity<Message> request)
    {
        Message message = request.getBody();
        if (isInputMessageValid(message))
        {
            Message createdMessage = this.service.add(message);
            URI relativeLocation = URI.create(String.format("%s/%d", basePath, createdMessage.getId()));
            URI location = request.getUrl().resolve(relativeLocation);
            return ResponseEntity
                .created(location)
                .body(createdMessage);
        }
        else
        {
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isInputMessageValid(Message message)
    {
        return message != null &&
            message.getContent() != null &&
            getAuthenticatedUser().equals(message.getUserId());
    }

    private String getAuthenticatedUser()
    {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return authentication.getPrincipal().toString();
    }
}
