BIN_DIR := .tools/bin

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

#
# File based targets
#

build/libs/service.jar: build.gradle.kts service.properties
	@echo "$(C_BLUE)Building application jar$(C_NONE)"
	@./gradlew clean test shadowJar
