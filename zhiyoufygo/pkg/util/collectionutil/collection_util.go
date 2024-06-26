package collectionutil

import (
	"fmt"
	"strconv"
	"strings"
)

func DeepGet(container any, path string) (any, error) {
	parts := strings.Split(path, ".")
	partObjAny := container
	subPath := ""

	for _, part := range parts {
		if subPath == "" {
			subPath = part
		} else {
			subPath = subPath + "." + part
		}

		switch partObj := partObjAny.(type) {
		default:
			return nil, fmt.Errorf("DeepGet: subPath %s, unsupported type %T", subPath, partObjAny)
		case map[string]any:
			var ok bool
			partObjAny, ok = partObj[part]
			if !ok {
				return nil, fmt.Errorf("DeepGet: subPath %s not exist", subPath)
			}
		case []any:
			idx, err := strconv.Atoi(part)
			if err != nil {
				return nil, fmt.Errorf("DeepGet: subPath %s, invalid idx for list", subPath)
			}
			if idx < 0 || idx > len(partObj) {
				return nil, fmt.Errorf("DeepGet: subPath %s, out of range idx for list", subPath)
			}
			partObjAny = partObj[idx]
		}
	}

	return partObjAny, nil
}

func DeepSet(container map[string]any, path string, value any) {
	parts := strings.Split(path, ".")
	partContainer := container
	last := false

	for idx, part := range parts {
		if idx == len(parts)-1 {
			last = true
		}

		if !last {
			partObjAny := partContainer[part]

			switch partObj := partObjAny.(type) {
			default:
				newPartContainer := make(map[string]any)
				partContainer[part] = newPartContainer
				partContainer = newPartContainer
			case map[string]any:
				partContainer = partObj
			}
		} else {
			partContainer[part] = value
		}
	}
}
