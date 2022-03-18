BIN_DIR := .tools/bin

.PHONY: compile
compile: install-dev-env
	@./gradlew clean compileJava

.PHONY: test
test: install-dev-env
	@./gradlew clean test

.PHONY: jar
jar: install-dev-env build/libs/service.jar

.PHONY: docker
docker:
	@docker build --no-cache -t $(shell ./gradlew -q print-container-name) \
		--build-arg=GITHUB_USERNAME=$(GITHUB_USERNAME) \
		--build-arg=GITHUB_TOKEN=$(GITHUB_TOKEN) .

.PHONY: install-dev-env
install-dev-env:
	@if [ ! -d .tools ]; then \
		git clone https://github.com/VEuPathDB/lib-jaxrs-container-build-utils .tools; \
	else \
		cd .tools && git pull && cd ..; \
	fi
	@$(BIN_DIR)/check-env.sh
	@./gradlew download-fgputil
	@$(BIN_DIR)/install-oracle.sh

clean:
	@rm -rf .gradle .tools vendor build

#
# File based targets
#

build/libs/service.jar: \
      vendor/fgputil-accountdb-1.0.0.jar \
      vendor/fgputil-cache-1.0.0.jar \
      vendor/fgputil-cli-1.0.0.jar \
      vendor/fgputil-client-1.0.0.jar \
      vendor/fgputil-core-1.0.0.jar \
      vendor/fgputil-db-1.0.0.jar \
      vendor/fgputil-events-1.0.0.jar \
      vendor/fgputil-json-1.0.0.jar \
      vendor/fgputil-server-1.0.0.jar \
      vendor/fgputil-servlet-1.0.0.jar \
      vendor/fgputil-solr-1.0.0.jar \
      vendor/fgputil-test-1.0.0.jar \
      vendor/fgputil-web-1.0.0.jar \
      vendor/fgputil-xml-1.0.0.jar \
      build.gradle.kts \
      service.properties
	@echo "Building application jar"
	@./gradlew clean test shadowJar
