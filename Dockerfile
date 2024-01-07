FROM eclipse-temurin
COPY /out/artifacts/EjercicioSitrack_jar /tmp/EjercicioSitrack_jar
WORKDIR /tmp/EjercicioSitrack_jar
# RUN pwd && ls
ENTRYPOINT ["java", "-jar", "EjercicioSitrack.jar"]