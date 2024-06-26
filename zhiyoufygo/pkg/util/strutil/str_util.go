package strutil

import (
	"bytes"
	"encoding/json"
)

func Pprint(obj any) string {
	b := new(bytes.Buffer)
	jsonEncoder := json.NewEncoder(b)
	jsonEncoder.SetEscapeHTML(false)
	jsonEncoder.SetIndent("", "  ")
	jsonEncoder.Encode(obj)
	data := b.Bytes()

	return string(data)
}
