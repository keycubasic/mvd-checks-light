package org.keycube.checkpassport.resource;

import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.keycube.checkpassport.service.PassportService;
import org.keycube.checkpassport.util.Checks;

@Path("expired-passport")
public class PassportResource {

  @Inject
  Checks checks;
  @Inject
  PassportService service;

  @GET
  @Path("0-update")
  @Operation(summary = "download + decompress + copy + append", hidden = true)
  public Response update() {
    checks.checkProfile();
    long start = System.currentTimeMillis();
    service.update();
    return Response.ok().entity(System.currentTimeMillis() - start).build();
  }

  @GET
  @Path("1-download")
  @Operation(hidden = true)
  public Response download() {
    checks.checkProfile();
    long start = System.currentTimeMillis();
    service.download();
    return Response.ok().entity(System.currentTimeMillis() - start).build();
  }

  @GET
  @Path("2-decompress")
  @Operation(hidden = true)
  public Response decompress() {
    checks.checkProfile();
    long start = System.currentTimeMillis();
    service.decompress();
    return Response.ok().entity(System.currentTimeMillis() - start).build();
  }

  @GET
  @Path("3-copy")
  @Operation(hidden = true)
  public Response copy() {
    checks.checkProfile();
    long start = System.currentTimeMillis();
    service.copy();
    return Response.ok().entity(System.currentTimeMillis() - start).build();
  }

  @GET
  @Path("4-append")
  @Operation(hidden = true)
  public Response append() {
    checks.checkProfile();
    long start = System.currentTimeMillis();
    service.append();
    return Response.ok().entity(System.currentTimeMillis() - start).build();
  }

  @GET
  @Path("check")
  @Operation(summary = "Проверить паспорт", description = "Проверить паспорт по номеру и серии")
  @APIResponses(
      value = {
        @APIResponse(
            responseCode = "200",
            description = "Паспорта нет в списке недействительных российских паспортов"),
        @APIResponse(
            responseCode = "302",
            description = "Паспорт есть в списке недействительных российских паспортов")
      })
  public Response check(
      @Parameter(description = "Серия паспорта", required = true) @QueryParam("serial")
          String serial,
      @Parameter(description = "Номер паспорта", required = true) @QueryParam("number")
          String number) {
    long start = System.currentTimeMillis();
    Response.Status status = Response.Status.OK;
    if (serial != null && number != null && service.check(serial.trim(), number.trim())) {
      status = Response.Status.FOUND;
    }
    if (ConfigUtils.getProfiles().contains("dev")) {
      return Response.status(status).entity(System.currentTimeMillis() - start).build();
    } else {
      return Response.status(status).build();
    }
  }
}
