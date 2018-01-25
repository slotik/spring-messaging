package info.slotik.toys.messaging.service;

import info.slotik.toys.messaging.entity.Message;
import info.slotik.toys.messaging.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpaMessageService implements MessageService
{
    private MessageRepository repository;

    @Autowired
    JpaMessageService(MessageRepository repository)
    {
        this.repository = repository;
    }

    @Override
    public List<Message> findAll()
    {
        return repository.findAll();
    }

    @Override
    public Message find(long id)
    {
        return this.repository.findOne(id);
    }

    @Override
    public Message add(Message message)
    {
        return this.repository.save(message.withId(null));
    }
}
