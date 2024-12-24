package ru.anvera.scheduled;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class ScheduleSourceToSinkDataPolling {

  @Scheduled(cron = "1 * * * * *")
  public void registerKafkaConnector() {

  }
}
