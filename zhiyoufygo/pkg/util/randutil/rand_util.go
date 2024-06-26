package randutil

import (
	"math/rand"
	"time"

	"github.com/zhiyoufy/zhiyoufygo/pkg/util/hexutil"
)

func GenerateHexId() string {
	spanId := NextId()
	traceIdHigh := NextTraceIdHigh()

	return hexutil.ToLowerHexTwo(traceIdHigh, spanId)
}

func GenerateShortHexId() string {
	traceIdHigh := NextTraceIdHigh()
	return hexutil.ToLowerHexOne(traceIdHigh)
}

func NextId() int64 {
	nextId := RandomLong()

	for nextId == 0 {
		nextId = RandomLong()
	}

	return nextId
}

func NextTraceIdHigh() int64 {
	return nextTraceIdHigh(rand.Int31())
}

func RandomLong() int64 {
	return rand.Int63()
}

func RandomLongInRange(min, max int64) int64 {
	return min + rand.Int63n(max-min)
}

func nextTraceIdHigh(random int32) int64 {
	epochSeconds := time.Now().Unix()
	return (epochSeconds&0x7fffffff)<<32 | (int64)(random&0x7fffffff)
}
