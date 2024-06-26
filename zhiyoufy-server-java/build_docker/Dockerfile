ARG REGISTRY_HUB

FROM ${REGISTRY_HUB}/zhiyoufy-base:20240625 as build

WORKDIR /app-build

COPY . /app-build

RUN mvn clean package -DskipTests --offline

WORKDIR /app

COPY bin bin
RUN jar -xf /app-build/zhiyoufy-server/target/zhiyoufy*.jar
RUN rm ./BOOT-INF/classes/application.yml
RUN ls -l /app

FROM ${REGISTRY_HUB}/openjdk-11-jre:20240626

COPY --from=build /app /app

WORKDIR /app/

CMD ["/app/bin/startup.sh"]
