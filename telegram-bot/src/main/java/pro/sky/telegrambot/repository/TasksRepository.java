package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.entity.NotificationTask;

import java.time.LocalDateTime;
import java.util.Collection;

public interface TasksRepository extends JpaRepository<NotificationTask, Long> {
    Collection<NotificationTask> findAllByDateTime(LocalDateTime dateTime);
}