GEN_PACKAGE  := $(shell ./gradlew -q print-gen-package)
APP_PACKAGE  := $(shell ./gradlew -q print-package)
PWD          := $(shell pwd)
MAIN_DIR     := src/main/java/$(shell echo $(APP_PACKAGE) | sed 's/\./\//g')
TEST_DIR     := $(shell echo $(MAIN_DIR) | sed 's/main/test/')
ALL_PACKABLE := $(shell find src/main -type f)
BIN_DIR      := .tools/bin

C_BLUE := "\\033[94m"
C_NONE := "\\033[0m"
C_CYAN := "\\033[36m"

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
	@echo "$(C_BLUE)Building application jar$(C_NONE)"
	@./gradlew clean test shadowJar
