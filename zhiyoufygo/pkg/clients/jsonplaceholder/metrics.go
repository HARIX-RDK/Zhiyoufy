package jsonplaceholder

import "github.com/prometheus/client_golang/prometheus"

var namespace string = "jsonplaceholder"

var apiRequestsActive = prometheus.NewGaugeVec(
	prometheus.GaugeOpts{
		Namespace: namespace,
		Name:      "api_requests_active",
		Help:      "Number of active api requests.",
	},
	[]string{"method", "handler"},
)

var apiRequestsTotal = prometheus.NewCounterVec(
	prometheus.CounterOpts{
		Namespace: namespace,
		Name:      "api_requests_total",
		Help:      "Number of finished api requests.",
	},
	[]string{"method", "handler", "code"},
)

var apiRequestDuration = prometheus.NewHistogramVec(
	prometheus.HistogramOpts{
		Namespace: namespace,
		Name:      "api_request_duration_seconds",
		Help:      "Histogram for the request duration",
		Buckets:   []float64{.1, .25, .5, 1, 2.5, 5, 10},
	},
	[]string{"method", "handler", "status_class"},
)

func RegisterMetrics() {
	prometheus.MustRegister(apiRequestsActive)
	prometheus.MustRegister(apiRequestsTotal)
	prometheus.MustRegister(apiRequestDuration)
}
