MAVEN  := mvn
PYTHON := python3

.PHONY: dev clean pack compile deploy

dev: clean
	$(MAVEN) install -DskipTests
	$(MAVEN) -pl webapp jetty:run

pack:
	$(MAVEN) package -DskipTests

compile:
	$(MAVEN) compile

clean:
	$(MAVEN) clean

deploy:
	$(PYTHON) deploy.py
