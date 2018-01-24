package info.slotik.toys.messaging.repository;

import info.slotik.toys.messaging.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>
{
}
