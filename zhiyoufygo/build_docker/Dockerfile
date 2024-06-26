FROM example.com/zhiyoufy/zhiyoufygo-base:20240218 as build

WORKDIR /app-build

COPY stomp stomp
COPY zhiyoufygo zhiyoufygo

WORKDIR /app-build/zhiyoufygo

RUN go build

WORKDIR /app

COPY zhiyoufygo/bin bin
RUN cp -a /app-build/zhiyoufygo/zhiyoufygo zhiyoufygo
RUN cp -a /app-build/zhiyoufygo/configs configs
RUN cp -a /app-build/zhiyoufygo/data data

FROM debian:bookworm-slim

RUN set -x && apt-get update && DEBIAN_FRONTEND=noninteractive apt-get install -y \
    ca-certificates rsync && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app /app

WORKDIR /app/

ENTRYPOINT ["/app/bin/tini", "--"]

CMD ["/app/bin/startup.sh"]
