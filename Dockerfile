FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 app
WORKDIR /app
COPY build/libs/newbeez-back-0.0.1-SNAPSHOT.jar app.jar
USER app
ENTRYPOINT ["java","-jar","app.jar"]