package filemgr

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"path/filepath"
	"sync"

	log "github.com/sirupsen/logrus"

	"github.com/zhiyoufy/zhiyoufygo/pkg/core"
)

var (
	DefaultFileMgr *FileMgr = NewFileMgr(&FileMgrConfig{})
)

type FileMgrConfig struct {
}

type FileMgr struct {
	localPathToDigest map[string]string
	mutex             sync.Mutex
}

func ensureDir(fileName string) error {
	dirName := filepath.Dir(fileName)
	if _, serr := os.Stat(dirName); serr != nil {
		merr := os.MkdirAll(dirName, os.ModePerm)
		return merr
	}
	return nil
}

func NewFileMgr(cfg *FileMgrConfig) *FileMgr {
	fileMgr := &FileMgr{
		localPathToDigest: make(map[string]string),
	}
	return fileMgr
}

func (mgr *FileMgr) DownloadFileIfNeeded(runCtx core.IRunContext, fileUrl string, fileDigest string, localPath string) error {
	mgr.mutex.Lock()
	defer mgr.mutex.Unlock()

	localDigestExist := false
	localDigestEqual := false
	var err error

	defer func() {
		if runCtx.IsInfoEnabled() {
			log.Infof("%s localDigestExist %v, localDigestEqual %v", runCtx.GetTag(), localDigestExist, localDigestEqual)
			log.Infof("%s err %s", runCtx.GetTag(), err)
		}
	}()

	localDigestPath := fmt.Sprintf("%s.digest", localPath)

	localDigest, exist := mgr.localPathToDigest[localPath]

	if exist {
		localDigestExist = true
		if localDigest == fileDigest {
			localDigestEqual = true
			return nil
		}
	} else {
		data, err := os.ReadFile(localDigestPath)

		if err == nil {
			localDigestExist = true
			localDigest = string(data)
			if localDigest == fileDigest {
				localDigestEqual = true
				mgr.localPathToDigest[localPath] = fileDigest
				return nil
			}
		}
	}

	req, err := http.NewRequestWithContext(runCtx.GetRootContext(), http.MethodGet, fileUrl, nil)
	if err != nil {
		return err
	}

	rsp, err := runCtx.GetHttpClient().Do(req)
	if err != nil {
		return err
	}
	defer rsp.Body.Close()

	if rsp.StatusCode >= 300 {
		return fmt.Errorf("unexpected StatusCode %d", rsp.StatusCode)
	}

	if err = ensureDir(localPath); err != nil {
		return err
	}

	localOut, err := os.Create(localPath)
	if err != nil {
		return err
	}
	defer localOut.Close()

	_, err = io.Copy(localOut, rsp.Body)

	digestOut, err := os.Create(localDigestPath)
	if err != nil {
		return err
	}
	defer digestOut.Close()

	_, err = digestOut.WriteString(fileDigest)
	if err != nil {
		return err
	}

	mgr.localPathToDigest[localPath] = fileDigest

	return nil
}
