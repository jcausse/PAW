MAVEN := mvn
PYTHON := python3

.PHONY: run clean pack deploy

default: run

run:
	cd webapp && $(MAVEN) clean jetty:run

clean:
	$(MAVEN) clean

pack:
	$(MAVEN) clean package

deploy:
	$(PYTHON) deploy.py
