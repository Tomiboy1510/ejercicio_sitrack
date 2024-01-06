FROM eclipse-temurin
COPY /target /tmp/target
WORKDIR /tmp
ENTRYPOINT ["java", "-cp", "/target/classes", "main.Main"]