package convutil

import "fmt"

func ToFloat64(inValue any) (float64, error) {
	switch value := inValue.(type) {
	case int:
		return float64(value), nil
	case float32:
		return float64(value), nil
	case float64:
		return value, nil
	}
	return 0, fmt.Errorf("ToFloat64 unsupported type %T", inValue)
}

func ToInt(inValue any) (int, error) {
	switch value := inValue.(type) {
	case int:
		return value, nil
	case float32:
		return int(value), nil
	case float64:
		return int(value), nil
	}
	return 0, fmt.Errorf("ToInt unsupported type %T", inValue)
}
