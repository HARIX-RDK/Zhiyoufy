package stompchannel

import (
	"io"

	log "github.com/sirupsen/logrus"

	"github.com/gorilla/websocket"
)

type webSocketConn struct {
	conn    *websocket.Conn
	reader  io.Reader
	traceOn bool
}

func (c *webSocketConn) Read(p []byte) (n int, err error) {
	if c.traceOn {
		defer func() {
			log.Infof("webSocketConn.Read n %d, %s, err %s", n, string(p[0:n]), err)
		}()
	}

	for {
		if c.reader == nil {
			_, c.reader, err = c.conn.NextReader()
			if err != nil {
				n = 0
				return 0, err
			}
		}

		n, err = c.reader.Read(p)
		if err == io.EOF {
			c.reader = nil // Reader is drained. Reset it.
			if n > 0 {     // If there's data read, return it. Next call will continue with new reader.
				return n, nil
			} else { // No data read. Continue with new reader now.
				continue
			}
		}
		return n, err // For all other errors or successful reads with len(p) > 0.
	}
}

func (c *webSocketConn) Write(p []byte) (n int, err error) {
	if c.traceOn {
		defer func() {
			log.Infof("webSocketConn.Write n %d, %s, err %s", n, string(p[0:n]), err)
		}()
	}

	w, err := c.conn.NextWriter(websocket.BinaryMessage)
	if err != nil {
		n = 0
		return 0, err
	}

	defer w.Close()

	n, err = w.Write(p)

	return n, err
}

func (c *webSocketConn) Close() error {
	return c.conn.Close()
}
