FROM jenkins/jenkins:lts

USER root

# Instalar Java y Maven
RUN apt-get update && apt-get install -y \
    openjdk-21-jre-headless \
    maven \
    git \
    && rm -rf /var/lib/apt/lists/*

USER jenkins
