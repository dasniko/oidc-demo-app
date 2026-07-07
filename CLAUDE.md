# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A minimal Quarkus web app that demonstrates OIDC login against Keycloak. It has one
protected page that dumps the raw access token, ID token, and user info for the
logged-in user — useful as a reference/demo for how `quarkus-oidc` wires up
authentication.

## Commands

- Dev mode (live reload): `./mvnw quarkus:dev`
- Build: `./mvnw clean package`
- Run the packaged jar: `java -jar target/quarkus-app/quarkus-run.jar`
- Build the JVM container image (after `./mvnw package`):
  `docker build -f src/main/docker/Dockerfile.jvm -t oidc-demo-app .`

There is no test suite in this project currently.

Running the app requires a reachable Keycloak instance (not included in this repo) with
a realm/client matching `application.properties` / `docker-compose.yml` (realm `demo`,
client `quarkus-app`). `docker-compose.yml` runs the pre-built image from
`ghcr.io/dasniko/oidc-demo-app` against a Keycloak expected at `http://localhost:8080`.

## Architecture

- Single JAX-RS resource, `src/main/java/dasniko/oidc/OidcInfo.java`, mapped to `/oidc`
  and annotated `@Authenticated` — this is the only endpoint that requires login and the
  natural place to look when extending what's shown/inspected.
- It injects Quarkus OIDC types directly (`UserInfo`, `Principal`, `JsonWebToken` for
  both access token and `@IdToken`) rather than going through a service layer, and
  serializes them with Jackson for display. This is intentional for a demo app — keep
  additions inline unless the scope grows meaningfully.
- Qute templates render the token/user-info dump: `templates/template.html` is the shared
  layout (`{#insert body}`), `templates/oidc.html` fills the body for `/oidc`.
- `META-INF/resources/index.html` is a static landing page served at `/` linking to `/oidc`.
- All OIDC behavior (PKCE, redirect path `/oidc/callback`, logout paths) is configured
  declaratively in `application.properties`; there is no custom auth code beyond the
  `@Authenticated` annotation.
- Requires Java 25 at compile time (`maven.compiler.release=25`) via Quarkus 3.37.1(BOM).
- CI (`.github/workflows/build.yml`) builds with the Maven wrapper on JDK 25 and, on
  pushes to the default branch, builds/pushes a multi-arch (amd64/arm64) Docker image to
  `ghcr.io` using `src/main/docker/Dockerfile.jvm`.

## Formatting

`.editorconfig` is authoritative: tabs, indent size 2, LF line endings, max line length 140.

## Maintaining this file

After making changes to this project, check whether they affect anything documented above
(architecture, commands, config, known issues) and update this file if so — keep it in sync
rather than letting it drift.
