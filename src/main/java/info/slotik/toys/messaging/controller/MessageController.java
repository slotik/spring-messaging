package info.slotik.toys.messaging.controller;

import info.slotik.toys.messaging.entity.Message;
import info.slotik.toys.messaging.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class MessageController
{
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
}
