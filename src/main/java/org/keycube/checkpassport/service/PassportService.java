package org.keycube.checkpassport.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

@ApplicationScoped
public class PassportService {

  @Inject Logger log;

  @Inject SmbConnector connector;

  @Inject EntityManager manager;

  @ConfigProperty(name = "passports.expired-list.url")
  String url;

  @ConfigProperty(name = "passports.expired-list.file.name")
  String fileName;

  @ConfigProperty(name = "passports.expired-list.file.path")
  String filePath;

  @ConfigProperty(name = "passports.expired-list.archive.extension")
  String archiveExtension;

  @ConfigProperty(name = "passports.expired-list.volume.uploads")
  String uploads;

  @PostConstruct
  void init() {
    log.info(String.join(": ","current user", System.getProperty("user.name")));
  }

  public void update() {
    try {
      download();
      Thread.sleep(10000L);
      decompress();
      Thread.sleep(10000L);
      copy();
      Thread.sleep(10000L);
      append();
    } catch (InterruptedException e) {
      String error = "sleep fails with error: " + e.getMessage();
      log.error(error);
      throw new WebApplicationException(error);
    }
  }

  // @Retry
  public void download() {
    log.info("download file started");
    try {
      String archiveName = fileName + archiveExtension;
      URL url = new URL(this.url + archiveName);
      File file = new File(uploads + archiveName);
      FileUtils.copyURLToFile(url, file);
      log.info("download file complete");
    } catch (Exception e) {
      String error = "download file fails with error: " + e.getMessage();
      log.error(error);
      throw new WebApplicationException(error);
    }
  }

  public void decompress() {
    log.info("decompress file started");
    try (BZip2CompressorInputStream is =
                 new BZip2CompressorInputStream(
                         new FileInputStream(uploads + fileName + archiveExtension));
         FileOutputStream os = new FileOutputStream(uploads + fileName)) {
      IOUtils.copyLarge(is, os);
      log.info("decompress file complete");
    } catch (Exception e) {
        String error = "decompress fails with errors: " + e.getMessage();
        log.error(error);
        throw new WebApplicationException(error);
    }
  }

  public void copy() {
    log.info("copy file started");
    try (FileInputStream is = new FileInputStream(uploads + fileName);
         OutputStream os = connector.createSmbFile(filePath + fileName).getOutputStream()) {
      IOUtils.copyLarge(is, os);
      log.info("copy file complete");
    } catch (Exception e) {
      String error = "copy fails with errors: " + e.getMessage();
      log.error(error);
      throw new WebApplicationException(error);
    }
  }

  @Transactional
  public void append() {
    log.info("append data started");
    try {
      manager
          .createNativeQuery(
                  """
                          merge into invalidpassportsmvd ip
                          using (select distinct * from upload_expired_passports) uep
                          on (uep.passp_series = ip.series and uep.passp_number = ip.passnum)
                          when not matched then
                            insert (series, passnum) values (uep.passp_series, uep.passp_number)""")
          .executeUpdate();
      log.info("append data complete");
    } catch (Exception e) {
      String error = "append data fails with error: " + e.getMessage();
      log.error(error);
      throw new WebApplicationException(error);
    }
  }

  public boolean check(String serial, String number) {
    return (int) manager.createNativeQuery(
                    "select isExpiredPassport(:serial, :number) from dual")
            .setParameter("serial", serial)
            .setParameter("number", number)
            .getSingleResult() > 0;
  }
}
