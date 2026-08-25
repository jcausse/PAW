MAVEN  := mvn
PYTHON := python3

.PHONY: dev clean pack compile deploy db-start db-stop

dev: clean db-start
	$(MAVEN) install -DskipTests
	$(MAVEN) -pl webapp jetty:run

db-start:
	@./.script/db-start.sh

db-stop:
	@./.script/db-stop.sh

pack:
	$(MAVEN) package -DskipTests

compile:
	$(MAVEN) compile

clean:
	$(MAVEN) clean

deploy:
	$(PYTHON) ./.script/deploy.py
