package org.keycube.checkpassport;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@ApplicationPath("/mvd-checks")
@OpenAPIDefinition(
        info = @Info(
                title = "Check expired passport",
                description = "Проверка по списку недействительных российских паспортов",
                version = "2.0.0",
                contact = @Contact(
                        name = "Константинов Константин Константинович",
                        email = "keycube@mail.ru"
                )
        )
)
public class PassportApplication extends Application {
}
