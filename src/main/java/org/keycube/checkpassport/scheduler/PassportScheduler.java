package org.keycube.checkpassport.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;
import org.keycube.checkpassport.service.PassportService;

@Singleton
public class PassportScheduler {

  @Inject Logger log;

  @Inject
  PassportService service;

  @Scheduled(cron = "{passports.expired-list.update.cron.expr}")
  void update() {
    log.info("update scheduled");
    service.update();
    log.info("update scheduler complete");
  }
}
