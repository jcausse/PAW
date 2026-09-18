MAVEN  := mvn
PYTHON := python3

.PHONY: dev clean pack compile deploy db-start db-stop

# LOCAL DEVELOPMENT TARGETS

dev: clean db-start
	$(MAVEN) install -DskipTests -Pdev
	$(MAVEN) -pl webapp jetty:run -Pdev

db-start:
	@./.script/db-start.sh

db-stop:
	@./.script/db-stop.sh

compile:
	$(MAVEN) compile

clean:
	$(MAVEN) clean

# PRODUCTION TARGETS

prod-deploy:
	$(PYTHON) ./.script/deploy.py

prod-db-backup:
	$(PYTHON) ./.script/deploy.py --db-backup-only
