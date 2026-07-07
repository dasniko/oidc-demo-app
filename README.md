# OIDC Demo App

Shows the access- and id-token and the user info response from the OIDC provider for the currently logged-in user.

Example Docker compose entry:
```yaml
services:
  oidc-demo:
    image: ghcr.io/dasniko/oidc-demo-app:latest
    environment:
      QUARKUS_OIDC_AUTH_SERVER_URL: https://keycloak:8443/realms/demo
      QUARKUS_OIDC_CLIENT_ID: quarkus-app
      QUARKUS_OIDC_CREDENTIALS_SECRET: changeme
    ports:
      - "8080:8080"
```
