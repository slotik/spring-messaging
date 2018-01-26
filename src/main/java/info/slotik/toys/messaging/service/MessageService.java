package info.slotik.toys.messaging.service;

import info.slotik.toys.messaging.entity.Message;

import java.util.List;

public interface MessageService
{
    List<Message> findAll();

    Message find(long id);

    Message add(Message message);

    void update(Message message);

    void delete(long id);
}
