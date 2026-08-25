# This is just a local development database. These credentials are not real production credentials.

FROM postgres:18-alpine

ENV POSTGRES_DB=paw
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

EXPOSE 5432
