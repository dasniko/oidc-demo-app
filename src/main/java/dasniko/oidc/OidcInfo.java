package dasniko.oidc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.UserInfo;
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
import java.util.Base64;
import java.util.Map;

@Path("/oidc")
@Authenticated
public class OidcInfo {

	@Inject
	Template oidc;

	@Inject
	Principal principal;

	@Inject
	JsonWebToken accessToken;

	@Inject
	@IdToken
	JsonWebToken idToken;

	@Inject
	UserInfo userInfo;

	@Inject
	ObjectMapper objectMapper;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public TemplateInstance oidcInfo() throws IOException {
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		return oidc
			.data("principal", principal.getName())
			.data("accessToken", payload(accessToken.getRawToken()))
			.data("idToken", payload(idToken.getRawToken()))
			.data("userInfo", objectMapper.writeValueAsString(objectMapper.readValue(userInfo.getUserInfoString(), new TypeReference<>() {})));
	}

	private String payload(String rawToken) throws IOException {
		String payloadSegment = rawToken.split("\\.")[1];
		byte[] payloadBytes = Base64.getUrlDecoder().decode(payloadSegment);
		Map<String, Object> payload = objectMapper.readValue(payloadBytes, new TypeReference<>() {});
		return objectMapper.writeValueAsString(payload);
	}

}
