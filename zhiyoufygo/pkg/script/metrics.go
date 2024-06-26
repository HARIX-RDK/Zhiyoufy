package script

import "github.com/prometheus/client_golang/prometheus"

var namespace string = "zhiyoufygo"

var scriptJobsActive = prometheus.NewGauge(
	prometheus.GaugeOpts{
		Namespace: namespace,
		Name:      "script_jobs_active",
		Help:      "Number of active script jobs.",
	},
)

func RegisterMetrics() {
	prometheus.MustRegister(scriptJobsActive)
}
