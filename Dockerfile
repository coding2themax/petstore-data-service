FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

#COPY .mvn/ .mvn
#COPY mvnw pom.xml ./
#RUN ./mvnw dependency:go-offline


#COPY src/ src/

#RUN ./mvnw package -Pnative -DskipTests

#FROM scratch

#COPY --from=0 /tmp /tmp
COPY target/petstore-data-service-0.0.1-SNAPSHOT.jar petstore-data-service.jar

ENTRYPOINT ["java", "-jar", "petstore-data-service.jar"]