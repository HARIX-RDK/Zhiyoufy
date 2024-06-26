
## mod初始化

```bash
go mod init github.com/zhiyoufy/zhiyoufygo
```

## 引入外部module

```go
import "xxx/yyy"
```
```bash
go mod tidy
```

## 引入本地依赖

```text title="go.mod"
module example.com/hello

go 1.16

replace example.com/greetings => ../greetings
```

## debugging

<https://github.com/golang/vscode-go/blob/master/docs/debugging.md>
