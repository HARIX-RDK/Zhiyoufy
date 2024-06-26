package stompchannel

import (
	log "github.com/sirupsen/logrus"
)

type stompLogger struct{}

func (s stompLogger) Debugf(format string, value ...interface{}) {
	log.Debugf(format, value...)
}

func (s stompLogger) Debug(message string) {
	log.Debug(message)
}

func (s stompLogger) Infof(format string, value ...interface{}) {
	log.Infof(format, value...)
}

func (s stompLogger) Info(message string) {
	log.Info(message)
}

func (s stompLogger) Warningf(format string, value ...interface{}) {
	log.Warningf(format, value...)
}

func (s stompLogger) Warning(message string) {
	log.Warning(message)
}

func (s stompLogger) Errorf(format string, value ...interface{}) {
	log.Errorf(format, value...)
}

func (s stompLogger) Error(message string) {
	log.Error(message)
}
