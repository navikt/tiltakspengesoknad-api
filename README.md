tiltakspenger-soknad-api
================

API-tjeneste for [Tiltakspengesøknaden](https://github.com/navikt/tiltakspenger-soknad)

En del av satsningen ["Flere i arbeid – P4"](https://memu.no/artikler/stor-satsing-skal-fornye-navs-utdaterte-it-losninger-og-digitale-verktoy/)

# Komme i gang
## Forutsetninger
- [JDK](https://jdk.java.net/)
- [Kotlin](https://kotlinlang.org/)
- [Gradle](https://gradle.org/) brukes som byggeverktøy og er inkludert i oppsettet

For hvilke versjoner som brukes, [se byggefilen](build.gradle.kts)

## Kjøre opp lokalt

Appen kjøres opp lokalt ved å kjøre opp `main()` i `Application.kt`.

Det ligger et docker-compose oppsett i `/docker-compose` på rot, som mocker ut OAuth2 med mock-oauth2-server. Dette kan
kjøres opp med `docker-compose up -d --build` fra `/docker-compose`-mappa på rot av repository.

Følgende miljøvariabler må settes (typisk i din egen run config) for at appen skal kunne kjøres opp lokalt fra IDE. Påse at 
miljøvariabler ikke blir sjekket inn i versjonskontroll, ettersom det avhenger litt av IDE/eget oppsett hvor sånne ting
havner. 

```
AV_ENDPOINT_URL=<din url til av>
AZURE_APP_CLIENT_ID=mocked_client_id
AZURE_APP_CLIENT_SECRET=mocked_secret
AZURE_APP_WELL_KNOWN_URL=http://host.docker.internal:6969/azure/.well-known/openid-configuration
JOARK_AUDIENCE=mock_audience
JOARK_ENDPOINT_URL=<din url til joark>
JOARK_SCOPE=mock_scope
PDF_ENDPOINT_URL=<din url til pdf>
PDL_AUDIENCE=mock_audience
PDL_ENDPOINT_URL=<din url til pdl>
PDL_SCOPE=mock_scope
TILTAKSPENGER_ARENA_AUDIENCE=mock_audience
TILTAKSPENGER_ARENA_ENDPOINT_URL=<din url til tiltakspenger-arena>
TOKEN_X_CLIENT_ID=localhost:tpts:tiltakspenger-soknad-api
TOKEN_X_PRIVATE_JWK=<generert jwk>
TOKEN_X_WELL_KNOWN_URL=http://host.docker.internal:6969/tokendings/.well-known/openid-configuration
UNLEASH_SERVER_API_TOKEN=<token til unleash api>
UNLEASH_SERVER_API_URL=<url til unleash api>
```

Hva de ulike miljøvariablene skal settes til avhenger av hva man er ute etter å teste. Hvis man kun
er ute etter å få kjørt opp appen lokalt, kan man sette placeholder-verdier på det meste (f.eks. http://localhost:et eller annet portnummer) 
frem til man trenger å sette noe annet.

## Bygging og denslags
For å bygge artifaktene:

```sh
./gradlew build
```

---

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub.

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #tiltakspenger-utvikling.
