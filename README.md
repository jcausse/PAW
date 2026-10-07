# Swappr - PAW 2026b Team 08

## Contents

About us:
- [Team Members](#team-members)

About `Swappr`:
- [About This Project](#about-this-project)
- [Technologies Used](#technologies-used)

About the deployed application:
- [Useful Links](#useful-links)
- [Administrator User and Example User Credentials](#administrator-user-and-example-user-credentials)
- [Application Info Endpoint](#application-info-endpoint)

Running, Testing and Deploying:
- [Running Locally](#running-locally)
- [Testing](#testing)
- [Deploying To Production](#deploying-to-production)

---

## Team Members

| **ID**     | **Last Name** | **First Name** | **ITBA email**                                          |
|------------|---------------|----------------|---------------------------------------------------------|
| **64637**  | Arcodaci      | Tiziano        | [tarcodaci@itba.edu.ar](mailto:tarcodaci@itba.edu.ar)   |
| **64092**  | Bridoux       | Juan Ignacio   | [jbridoux@itba.edu.ar](mailto:jbridoux@itba.edu.ar)     |
| **61105**  | Causse        | Juan Ignacio   | [jcausse@itba.edu.ar](mailto:jcausse@itba.edu.ar)       |
| **66048**  | Fumagalli     | Teo            | [tfumagalli@itba.edu.ar](mailto:tfumagalli@itba.edu.ar) |

---

## About This Project

**Swappr** is a marketplace webapp aimed to buy, promote, sell and trade used electronic devices such 
as (but not limited to) laptops, PC parts, gaming consoles, photography equipment or mobile phones.

In Argentina, where this was developed, you have two main options where you can do this:
- Mercado Libre: high selling commissions, aimed at brand-new products, does not allow trades
- Facebook Marketplace: unsafe, highly coupled to the user's social profile, lacks advanced filters

**Swappr** aims to solve this problem by:
- allowing sellers and buyers to find each other and establish contact paying low commissions
- providing support for trades (where both parties exchange their products) free of charge
- making the buyer's search more efficient

## Technologies Used

This webapp project uses:

- Java (version 21)
- Spring (version 5.3.33)
- Maven
- Make
- PostgreSQL
- Docker (for local development database management)
- Python 3 (for automated deployment to production)
- Tailwind (version 4.3.3)

---

## Useful Links

- [Production URL](https://pawserver.it.itba.edu.ar/paw-2026b-08/): `https://pawserver.it.itba.edu.ar/paw-2026b-08/`
- [INFO Logs](https://pawserver.it.itba.edu.ar/logs/paw-2026b-08.info.2026-10-05.log): `https://pawserver.it.itba.edu.ar/logs/paw-2026b-08.info.2026-10-05.log`
- [WARN+ Logs](https://pawserver.it.itba.edu.ar/logs/paw-2026b-08.warnings.2026-10-05.log): `https://pawserver.it.itba.edu.ar/logs/paw-2026b-08.warnings.2026-10-05.log`

## Administrator User and Example User Credentials

The following credentials correspond to a user that can be used as an example, which is also an administrator, so
admin-only application flows can be also tested using it:

- Email: `jcausse@itba.edu.ar`
- Password: `123123123`

## Application Info Endpoint

The endpoint `/appinfo` prints out information about the project build. This is useful to check production versions.

Information available:
- Build time
- Build version
- Git branch
- Git commit hash
- Git tags
- Total repository commits at build time

### Example

```text
Build Time: 2026-10-05T19:19:50-0300
Build Version: 1.0-SNAPSHOT
Branch: main
Commit: 56103e8a22339b653df75b3aae8c35f5588769a7
Tags: Sprint-3
Total Commits: 843
```

---

## Running Locally

To build and run the project locally, you must first install OpenJDK 21 or any other JAVA SDK that supports Java 21, 
Maven and Docker (so that the local development database can be automatically created by the build scripts). 

After installing those requirements, just run:

```shell
make dev
```

After running, the local development database running inside a Docker Container can be stopped by running:

```shell
make db-stop
```

## Testing

To run unit tests, run:

```shell
make test
```

Also, the project includes some AI-made Python troubleshooters to find possible problems in JSP files, such as
missing `<c:out>` and `<c:url>` tags.

Developers are strongly advised to run said troubleshooters, but acknowledging that those scripts do not replace human
reviewers, and are not some kind of magic but just some glorified regular expressions and pattern-matching tools.

To run these troubleshooters, invoke the `troubleshoot` target:

```shell
make troubleshoot
```

## Deploying To Production

The Python script `.script/deploy.py` automates deploy to production. To be able to use it, you must create a file named
`.script/deploy_secrets.properties` with your credentials (which will be ignored by Git) and then run this to deploy:

```shell
make prod-deploy
```

See `.script/deploy_secrets.properties.sample` for a configuration example.
