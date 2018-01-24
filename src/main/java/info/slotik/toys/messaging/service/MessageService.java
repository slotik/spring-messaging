package info.slotik.toys.messaging.service;

import info.slotik.toys.messaging.entity.Message;

import java.util.List;

public interface MessageService
{
    List<Message> findAll();
}
