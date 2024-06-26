package script

import (
	"fmt"
	"strings"
	"testing"

	"github.com/stretchr/testify/require"

	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
	"github.com/zhiyoufy/zhiyoufygo/pkg/util/scriptutil"
	"github.com/zhiyoufy/zhiyoufygo/test"
	"github.com/zhiyoufy/zhiyoufygo/test/testutils"
)

func TestBase_BlockNotComplete(t *testing.T) {
	test.LoadConfig(t)

	scriptFile := "testdata/blockNotComplete.json"
	content, err := TestdataFS.ReadFile(scriptFile)

	require.True(t, err == nil, fmt.Sprintf("read script %s failed, %s", scriptFile, err))

	runner := scriptutil.NewScriptJobRunner()

	_, err = runner.ParseScript(string(content))

	require.True(t, err != nil)

	require.True(t, strings.Contains(err.Error(), "incomplete"))

	require.True(t, true)
}

func TestBase_BlockNotMatch(t *testing.T) {
	test.LoadConfig(t)

	scriptFile := "testdata/blockNotMatch.json"
	content, err := TestdataFS.ReadFile(scriptFile)

	require.True(t, err == nil, fmt.Sprintf("read script %s failed, %s", scriptFile, err))

	runner := scriptutil.NewScriptJobRunner()

	_, err = runner.ParseScript(string(content))

	require.True(t, err != nil)

	require.True(t, strings.Contains(err.Error(), "mismatch"))

	require.True(t, true)
}

func TestBase_EmptyCommands(t *testing.T) {
	test.LoadConfig(t)

	scriptFile := "testdata/emptyCommands.json"
	content, err := TestdataFS.ReadFile(scriptFile)

	require.True(t, err == nil, fmt.Sprintf("read script %s failed, %s", scriptFile, err))

	runner := scriptutil.NewScriptJobRunner()

	_, err = runner.ParseScript(string(content))

	require.True(t, err != nil)

	require.True(t, strings.Contains(err.Error(), "commands empty"))

	require.True(t, true)
}

func TestBase_MainBodyDoneThenFinallyFailed(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/mainBodyDoneThenFinallyFailed.json",
	}
	_, runCtx := testutils.BaseRunScript(t, cfg)

	require.True(t, runCtx.JobStatus == script.Status_Failed)
}

func TestBase_MainBodyFailedThenFinallyDone(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/mainBodyFailedThenFinallyDone.json",
	}
	_, runCtx := testutils.BaseRunScript(t, cfg)

	require.True(t, runCtx.JobStatus == script.Status_Failed)
}

func TestBase_SetAndIncrNonNumberVar(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/setAndIncrNonNumberVar.json",
	}
	_, runCtx := testutils.BaseRunScript(t, cfg)

	require.True(t, runCtx.JobStatus == script.Status_Failed)

	require.True(t, strings.Contains(runCtx.FailCause.Error(), "can't be convert"))
}

func TestBase_UnknownCommandType(t *testing.T) {
	test.LoadConfig(t)

	scriptFile := "testdata/unknownCommandType.json"
	content, err := TestdataFS.ReadFile(scriptFile)

	require.True(t, err == nil, fmt.Sprintf("read script %s failed, %s", scriptFile, err))

	runner := scriptutil.NewScriptJobRunner()

	_, err = runner.ParseScript(string(content))

	require.True(t, err != nil)

	require.True(t, strings.Contains(err.Error(), "invalid commandId"))
}

func TestLoop_WhileCustomConditionWrongOperator(t *testing.T) {
	test.LoadConfig(t)

	scriptFile := "testdata/whileCustomConditionWrongOperator.json"
	content, err := TestdataFS.ReadFile(scriptFile)

	require.True(t, err == nil, fmt.Sprintf("read script %s failed, %s", scriptFile, err))

	runner := scriptutil.NewScriptJobRunner()

	_, err = runner.ParseScript(string(content))

	require.True(t, err != nil)

	require.True(t, strings.Contains(err.Error(), "invalid operator"))
}

func TestLoop_WhileCustomConditionWrongVarType(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("enterLoopBool", true)
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/whileCustomConditionWrongVarType.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScript(t, cfg)

	require.True(t, runCtx.JobStatus == script.Status_Failed)

	require.True(t, strings.Contains(runCtx.FailCause.Error(), "is not string"))
}
