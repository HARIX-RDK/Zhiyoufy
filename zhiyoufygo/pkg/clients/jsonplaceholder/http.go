package jsonplaceholder

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"strconv"

	"github.com/prometheus/client_golang/prometheus"
	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/util/strutil"
)

func (client Client) httpGetAny(ctx context.Context, reqUrl string, handler string) (any, error) {
	rootCtx := ctx
	httpClient := client.GetHttpClient()

	req, err := http.NewRequestWithContext(rootCtx, http.MethodGet, reqUrl, nil)
	if err != nil {
		return nil, err
	}

	method := http.MethodGet
	apiRequestsActiveGauge := apiRequestsActive.WithLabelValues(method, handler)

	apiRequestsActiveGauge.Inc()
	defer apiRequestsActiveGauge.Dec()

	status := -1
	timer := prometheus.NewTimer(prometheus.ObserverFunc(func(v float64) {
		switch {
		case status >= 500: // Server error.
			apiRequestDuration.WithLabelValues(method, handler, "5xx").Observe(v)
		case status >= 400: // Client error.
			apiRequestDuration.WithLabelValues(method, handler, "4xx").Observe(v)
		case status >= 300: // Redirection.
			apiRequestDuration.WithLabelValues(method, handler, "3xx").Observe(v)
		case status >= 200: // Success.
			apiRequestDuration.WithLabelValues(method, handler, "2xx").Observe(v)
		default:
			apiRequestDuration.WithLabelValues(method, handler, "-1").Observe(v)
		}
	}))
	defer timer.ObserveDuration()

	rsp, err := httpClient.Do(req)
	if err != nil {
		apiRequestsTotal.WithLabelValues(method, handler, "-1").Inc()
		return nil, err
	}
	defer rsp.Body.Close()

	status = rsp.StatusCode
	defer apiRequestsTotal.WithLabelValues(method, handler, strconv.Itoa(rsp.StatusCode)).Inc()

	body, err := io.ReadAll(rsp.Body)
	if err != nil {
		return nil, err
	}

	if !(status >= 200 && status <= 299) {
		return nil, fmt.Errorf("fail status %d: body %s", status, body)
	}

	var varValue any
	err = json.Unmarshal(body, &varValue)
	if err != nil {
		return nil, err
	}

	if client.IsInfoEnabled() {
		log.Infof("%s got rsp %s", client.GetTag(), strutil.Pprint(varValue))
	}

	return varValue, nil
}

func (client Client) httpPostAny(ctx context.Context, reqUrl string, handler string, reqBody any) (any, error) {
	rootCtx := ctx
	httpClient := client.GetHttpClient()

	data, _ := json.Marshal(reqBody)

	req, err := http.NewRequestWithContext(rootCtx, http.MethodPost, reqUrl, bytes.NewBuffer(data))
	if err != nil {
		return nil, err
	}

	req.Header.Set("Content-Type", "application/json")

	method := http.MethodPost
	apiRequestsActiveGauge := apiRequestsActive.WithLabelValues(method, handler)

	apiRequestsActiveGauge.Inc()
	defer apiRequestsActiveGauge.Dec()

	status := -1
	timer := prometheus.NewTimer(prometheus.ObserverFunc(func(v float64) {
		switch {
		case status >= 500: // Server error.
			apiRequestDuration.WithLabelValues(method, handler, "5xx").Observe(v)
		case status >= 400: // Client error.
			apiRequestDuration.WithLabelValues(method, handler, "4xx").Observe(v)
		case status >= 300: // Redirection.
			apiRequestDuration.WithLabelValues(method, handler, "3xx").Observe(v)
		case status >= 200: // Success.
			apiRequestDuration.WithLabelValues(method, handler, "2xx").Observe(v)
		default:
			apiRequestDuration.WithLabelValues(method, handler, "-1").Observe(v)
		}
	}))
	defer timer.ObserveDuration()

	rsp, err := httpClient.Do(req)
	if err != nil {
		apiRequestsTotal.WithLabelValues(method, handler, "-1").Inc()
		return nil, err
	}
	defer rsp.Body.Close()

	status = rsp.StatusCode
	defer apiRequestsTotal.WithLabelValues(method, handler, strconv.Itoa(rsp.StatusCode)).Inc()

	body, err := io.ReadAll(rsp.Body)
	if err != nil {
		return nil, err
	}

	if !(status >= 200 && status <= 299) {
		return nil, fmt.Errorf("fail status %d: body %s", status, body)
	}

	var varValue any
	err = json.Unmarshal(body, &varValue)
	if err != nil {
		return nil, err
	}

	if client.IsInfoEnabled() {
		log.Infof("%s got rsp %s", client.GetTag(), strutil.Pprint(varValue))
	}

	return varValue, nil
}

func (client Client) httpPutAny(ctx context.Context, reqUrl string, handler string, reqBody any) (any, error) {
	rootCtx := ctx
	httpClient := client.GetHttpClient()

	data, _ := json.Marshal(reqBody)

	req, err := http.NewRequestWithContext(rootCtx, http.MethodPut, reqUrl, bytes.NewBuffer(data))
	if err != nil {
		return nil, err
	}

	req.Header.Set("Content-Type", "application/json")

	method := http.MethodPut
	apiRequestsActiveGauge := apiRequestsActive.WithLabelValues(method, handler)

	apiRequestsActiveGauge.Inc()
	defer apiRequestsActiveGauge.Dec()

	status := -1
	timer := prometheus.NewTimer(prometheus.ObserverFunc(func(v float64) {
		switch {
		case status >= 500: // Server error.
			apiRequestDuration.WithLabelValues(method, handler, "5xx").Observe(v)
		case status >= 400: // Client error.
			apiRequestDuration.WithLabelValues(method, handler, "4xx").Observe(v)
		case status >= 300: // Redirection.
			apiRequestDuration.WithLabelValues(method, handler, "3xx").Observe(v)
		case status >= 200: // Success.
			apiRequestDuration.WithLabelValues(method, handler, "2xx").Observe(v)
		default:
			apiRequestDuration.WithLabelValues(method, handler, "-1").Observe(v)
		}
	}))
	defer timer.ObserveDuration()

	rsp, err := httpClient.Do(req)
	if err != nil {
		apiRequestsTotal.WithLabelValues(method, handler, "-1").Inc()
		return nil, err
	}
	defer rsp.Body.Close()

	status = rsp.StatusCode
	defer apiRequestsTotal.WithLabelValues(method, handler, strconv.Itoa(rsp.StatusCode)).Inc()

	body, err := io.ReadAll(rsp.Body)
	if err != nil {
		return nil, err
	}

	if !(status >= 200 && status <= 299) {
		return nil, fmt.Errorf("fail status %d: body %s", status, body)
	}

	var varValue any
	err = json.Unmarshal(body, &varValue)
	if err != nil {
		return nil, err
	}

	if client.IsInfoEnabled() {
		log.Infof("%s got rsp %s", client.GetTag(), strutil.Pprint(varValue))
	}

	return varValue, nil
}
