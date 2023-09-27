package org.keycube.checkpassport.service;

import jakarta.enterprise.context.ApplicationScoped;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.MalformedURLException;

@ApplicationScoped
public class SmbConnector {

  @ConfigProperty(name = "passports.expired-list.file.path.username")
  String username;

  @ConfigProperty(name = "passports.expired-list.file.path.password")
  String password;

  private NtlmPasswordAuthentication auth;

  public SmbFile createSmbFile(String fileName) throws MalformedURLException {
    authenticate();
    return new SmbFile(fileName, auth);
  }

  private void authenticate() {
    if (auth == null)
      auth = new NtlmPasswordAuthentication("DOMAIN", username, password);
  }
}
