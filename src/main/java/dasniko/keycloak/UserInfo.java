package dasniko.keycloak;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.quarkus.oidc.IdToken;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Path("/user")
@Authenticated
public class UserInfo {

	@Inject
	Template user;

	@Inject
	Principal principal;
	@Inject
	JsonWebToken accessToken;
	@Inject
	@IdToken
	JsonWebToken idToken;
	@Inject
	io.quarkus.oidc.UserInfo userInfo;

	@Inject
	ObjectMapper objectMapper;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance userInfo() throws IOException {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
		};
		return user
			.data("principal", principal.getName())
			.data("accessToken", objectMapper.writeValueAsString(accessToken))
			.data("rawAccessToken", accessToken.getRawToken())
			.data("idToken", objectMapper.writeValueAsString(idToken))
			.data("rawIdToken", idToken.getRawToken())
			.data("userInfo", objectMapper.writeValueAsString(objectMapper.readValue(userInfo.getUserInfoString(), typeRef)));
	}

}
