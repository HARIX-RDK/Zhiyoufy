FROM openjdk:8-jre-slim

# Add Tini
ENV TINI_VERSION v0.18.0
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini
ENTRYPOINT ["/tini", "--"]

RUN apt-get update && apt-get install -y --no-install-recommends \
		iputils-ping \
		dnsutils \
        vim \
        procps \
        fontconfig

CMD ["sleep 10000"]
