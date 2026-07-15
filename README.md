# pensjon-opptjening-filadapter

Brotjeneste mellom NAVs SFTP-filsluse og POPP. Laster ned pensjonsfiler fra filslusa og overfører dem segmentert til POPP for videre prosessering.

## Hovedfunksjoner

| Funksjon | Beskrivelse |
|---|---|
| **Fil-listing** | `GET /list` — scanner SFTP-filsluse, beriker med lagerstatus fra POPP |
| **Overføring** | `POST /overfor` — laster ned fil fra filsluse, segmenterer og sender til POPP |
| **Automatisk scanning** | Scheduled task hvert 15. minutt — logger nye filer |
| **Automatisk overføring** | Scheduled task hvert 30. sekund — overfører neste ventende fil |

## Arkitektur

```
┌─────────────┐       ┌──────────────────────┐       ┌──────────┐
│ SFTP        │──SSH──▶│ pensjon-opptjening-  │──HTTP─▶│  POPP    │
│ filsluse    │◀──────│ filadapter           │◀──────│  (API)   │
│ /outbound   │       └──────────────────────┘       └──────────┘
└─────────────┘                ▲
                               │ OBO (Azure AD)
                    ┌──────────────────────────┐
                    │ pensjon-opptjening-      │
                    │ administrasjon (frontend) │
                    └──────────────────────────┘
```

**Dataflyt:**

1. Filer legges på SFTP-filsluse (`/outbound`) av eksterne systemer
2. Filadapter scanner og lister filer med metadata (størrelse, SFTP mtime)
3. Ved overføring: filadapter laster ned via SFTP, oppretter fil i POPP (`/fil/opprett`), og pusher innholdet i segmenter (`/fil/leggtil`) til `T_FIL_SEGMENT`
4. POPP validerer og setter status `KLAR` — filen får en UUID (`fil_metadata_id`)
5. Behandlinger i POPP startes med denne `filId` — POPP leser segmentene direkte fra `T_FIL_SEGMENT` og prosesserer

## Konsumenter

| App | Hva den bruker |
|---|---|
| **pensjon-opptjening-administrasjon** | `GET /list`, `POST /overfor` — manuell fil-håndtering i admin-UI |

POPP gir filadapter inbound-tilgang (filadapter kaller POPP), men POPP kaller aldri filadapter. Når filen er overført, bruker POPP sin egen `fil_metadata_id` (UUID) til å starte behandlinger — innholdet ligger allerede i `T_FIL_SEGMENT`.

## Miljøer

| Miljø | Cluster | POPP | Filsluse |
|---|---|---|---|
| Q0 | dev-fss | pensjon-popp-q0 | filmottak.preprod.local |
| Q1 | dev-fss | pensjon-popp-q1 | filmottak.preprod.local |
| Q2 | dev-fss | pensjon-popp-q2 | filmottak.preprod.local |
| Q5 | dev-fss | pensjon-popp-q5 | filmottak.preprod.local |
| Prod | prod-fss | pensjon-popp | filmottak.adeo.no |

## API

### `GET /list`

Returnerer alle filer på filslusa med lagerstatus fra POPP.

```json
{
  "filer": [
    {
      "filnavn": "P491.FFUPGI.TRANS.TILPOPP.D2250315",
      "size": 3850,
      "lagretMedId": "a1b2c3d4-...",
      "lagresMedId": [],
      "modifiedAt": "2025-03-15T10:30:00Z"
    }
  ]
}
```

- `lagretMedId` — POPP fil-ID hvis overført og `KLAR`, ellers `null`
- `lagresMedId` — liste over uferdige overføringer (status `LAGRES`)
- `modifiedAt` — SFTP mtime (når filen ble lagt på filslusa)

### `POST /overfor`

Laster ned og overfører fil til POPP.

```json
// Request
{ "filnavn": "P491.FFUPGI.TRANS.TILPOPP.D2250315" }

// Response (OK)
{ "filId": "a1b2c3d4-...", "melding": null }

// Response (feil)
{ "filId": null, "melding": "Finnes ikke i filsluse: ..." }
```

## Teknisk stack

- **Kotlin** + **Spring Boot 4.1** (webmvc)
- **SFTP** via JSch/Apache MINA for filsluse-tilkobling
- **Azure AD** (token-validation-spring) for autentisering
- **NAIS** på FSS (on-prem) — krever webproxy for utgående trafikk
- **Gradle** (Kotlin DSL)

## Kjøre lokalt

```bash
# Bygg
./gradlew build

# Kjør tester
./gradlew test

# Start applikasjon (krever SSH-nøkkel og miljøvariabler)
./gradlew bootRun
```

Lokalkjøring mot filsluse krever VPN/naisdevice og SSH-nøkkel. Admin-appen kan peke mot Q2-instansen i stedet.

### Miljøvariabler (lokalt)

| Variabel | Beskrivelse |
|---|---|
| `POPP_URL` | POPP API base-URL |
| `POPP_API_ID` | Azure AD audience for POPP |
| `FILSLUSE_HOST` | SFTP-host |
| `FILSLUSE_PORT` | SFTP-port (22) |
| `FILSLUSE_USERNAME` | SFTP-brukernavn |
| `AZURE_APP_WELL_KNOWN_URL` | Azure AD discovery |
| `AZURE_APP_CLIENT_ID` | App client ID |

## Tester

```bash
./gradlew test
```

Tester bruker WireMock for POPP-kall og en `MockFilsluseKlient` for SFTP. Ingen reell nettverkstilgang kreves.

## Team

**pensjonopptjening** — [#pensjonopptjening](https://nav-it.slack.com/archives/pensjonopptjening) på Slack.
