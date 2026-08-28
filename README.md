# PAW 2026b Team 08

## Contents

- [Team Members](#team-members)
- [About This Project](#about-this-project)
- [Technologies Used](#technologies-used)
- [Running Locally](#running-locally)
- [Deploying To Production](#deploying-to-production)

## Team Members

| **ID**     | **Last Name** | **First Name** | **ITBA email**                                          |
|------------|---------------|----------------|---------------------------------------------------------|
| **XXXXX**  | Arcodaci      | Tiziano        | [tarcodaci@itba.edu.ar](mailto:tarcodaci@itba.edu.ar)   |
| **64092**  | Bridoux       | Juan Ignacio   | [jbridoux@itba.edu.ar](mailto:jbridoux@itba.edu.ar)     |
| **61105**  | Causse        | Juan Ignacio   | [jcausse@itba.edu.ar](mailto:jcausse@itba.edu.ar)       |
| **XXXXX**  | Fumagalli     | Teo            | [tfumagalli@itba.edu.ar](mailto:tfumagalli@itba.edu.ar) |

## About This Project

[TODO: PROJECT NAME GOES HERE] is a marketplace webapp aimed to buy, promote, sell and trade used electronic devices such 
as (but not limited to) laptops, PC parts, gaming consoles, photography equipment or mobile phones.

In Argentina, where this was developed, you have two main options where you can do this:
- Mercado Libre: high selling commissions, aimed at brand-new products, does not allow trades
- Facebook Marketplace: unsafe, highly coupled to the user's social profile, lacks advanced filters

[TODO: PROJECT NAME GOES HERE] aims to solve this problem by:
- allowing sellers and buyers to find each other and establish contact paying low commissions
- providing support for trades (where both parties exchange their products) free of charge
- making the buyer's search more efficient

## Technologies Used

This webapp project uses:

- Java (version 21)
- Spring (version 5.3.33)
- Maven
- Make
- PostgreSQL (version XXXXX)
- Docker (for local development database management)
- Python 3 (for automated deployment to production)
- Tailwind (version 4.3.3)

## Running Locally

To build and run the project locally, you must first install OpenJDK 21 or any other JAVA SDK that supports Java 21, 
Maven and Docker (so that the local development database can be automatically created by the build scripts). 

After installing those requirements, just run:

```shell
make dev
```

## Deploying To Production

The Python script `.script/deploy.py` automates deploy to production. To be able to use it, you must create a file named
`.script/deploy_secrets.properties` with your credentials (which will be ignored by Git) and then run this to deploy:

```shell
make deploy
```

See `.script/deploy_secrets.properties.sample` for a configuration example.
