package jsonutil

import (
	"encoding/json"
	"os"
)

func LoadByPath(path string, obj any) error {
	content, err := os.ReadFile(path)

	if err != nil {
		return err
	}

	err = json.Unmarshal(content, obj)

	if err != nil {
		return err
	}

	return nil
}
