package pro.sky.telegrambot.listener;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.TasksRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    TasksRepository repository;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }
    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (matchMessage("/start", update).matches()) {
                telegramBot.execute(new SendMessage(update.message().chat().id(), "Привет!"));
            } else {
                var matcher = matchMessage("([0-9\\.\\:\\s]{16})(\\s)([0-9\\W+]+)", update);
                if (matcher.matches()) {
                    saveTask(matcher.group(1), matcher.group(3), update.message().chat().id());
                } else {
                    telegramBot.execute(new SendMessage(update.message().chat().id(), "Неверный формат"));
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    private void saveTask(String dateTime, String description, long chatId) {
        var task = new NotificationTask();
        task.setDateTime(LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        task.setDescription(description);
        task.setChatId(chatId);
        repository.save(task);
    }

    private Matcher matchMessage(String regex, Update update) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(update.message().text());
    }
}