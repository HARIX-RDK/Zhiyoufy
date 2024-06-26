package backoff

type ExponentialBackoffConfig struct {
	BaseInterval int
	BackoffSeq   []int
}

type ExponentialBackoff struct {
	baseInterval int
	backoffSeq   []int
	backoffIdx   int
}

func DefaultExponentialBackoffConfig() *ExponentialBackoffConfig {
	config := &ExponentialBackoffConfig{
		BaseInterval: 2,
		BackoffSeq:   []int{1, 1, 2, 4, 8, 16, 32, 60},
	}
	return config
}

func NewExponentialBackoff(config *ExponentialBackoffConfig) *ExponentialBackoff {
	if config == nil {
		config = DefaultExponentialBackoffConfig()
	}
	backoff := &ExponentialBackoff{
		baseInterval: config.BaseInterval,
		backoffSeq:   config.BackoffSeq,
	}
	return backoff
}

func (backoff *ExponentialBackoff) Next() int {
	next := backoff.backoffSeq[backoff.backoffIdx] * backoff.baseInterval
	if backoff.backoffIdx < len(backoff.backoffSeq)-1 {
		backoff.backoffIdx += 1
	}
	return next
}

func (backoff *ExponentialBackoff) Reset() {
	backoff.backoffIdx = 0
}
