package org.keycube.checkpassport.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Table(name = "invalidpassportsmvd")
@Schema(hidden = true)
public class Passport {

  @Id private Long id;

  @Column(name = "series")
  public String serial;

  @Column(name = "passnum")
  public String number;

}
