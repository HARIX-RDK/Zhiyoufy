package script

import (
	"testing"
	"time"

	"github.com/stretchr/testify/require"
	"github.com/zhiyoufy/zhiyoufygo/pkg/script"
	"github.com/zhiyoufy/zhiyoufygo/test/testutils"
)

func TestBase_CatchPointHop(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/catchPointHop.json",
	}
	testutils.BaseRunScriptOk(t, cfg)
}

func TestBase_MainBodyDoneThenFinallyDone(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/mainBodyDoneThenFinallyDone.json",
	}
	testutils.BaseRunScriptOk(t, cfg)
}

func TestBase_SetAndIncrGlobalVar(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/setAndIncrGlobalVar.json",
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	numVar1, _ := runCtx.GetVarTypeFloat64("userCnt")

	require.True(t, numVar1 == 45)
}

func TestBase_SetAndIncrNamespaceVar(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/setAndIncrNamespaceVar.json",
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	numVar1, _ := runCtx.GetVarTypeFloat64("TestNamespace.userCnt")

	require.True(t, numVar1 == 45)
}

func TestBase_Sleep(t *testing.T) {
	startTime := time.Now()
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/sleep.json",
	}
	testutils.BaseRunScriptOk(t, cfg)
	duration := time.Since(startTime)

	require.True(t, duration >= 2*time.Second)
}

func TestCondition_IfCustomConditionIntCompareCase1(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("totalUserCnt", 5)
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/ifCustomConditionIntCompare.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	value, _ := runCtx.GetVarTypeFloat64("userCnt")

	require.True(t, value == 40)
}

func TestCondition_IfCustomConditionIntCompareCase2(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("totalUserCnt", 45)
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/ifCustomConditionIntCompare.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	value, _ := runCtx.GetVarTypeFloat64("userCnt")

	require.True(t, value == 41)
}

func TestCondition_NestedIfCondition(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/nestedIfCondition.json",
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	numVar1, _ := runCtx.GetVarTypeFloat64("TestNamespace.numVar1")

	require.True(t, numVar1 == 20)

	numVar2, _ := runCtx.GetVarTypeFloat64("TestNamespace.numVar2")

	require.True(t, numVar2 == 20)
}

func TestLoop_CustomLoop(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("loopCount", 5)
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/forCustomLoop.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	value, _ := runCtx.GetVarTypeFloat64("userCnt")

	require.True(t, value == 45)
}

func TestLoop_NestedForLoop(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/nestedForLoop.json",
	}
	testutils.BaseRunScriptOk(t, cfg)
}

func TestLoop_NestedWhileLoop(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/nestedWhileLoop.json",
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	numVar1, _ := runCtx.GetVarTypeFloat64("TestNamespace.numVar1")

	require.True(t, numVar1 == 12)

	numVar2, _ := runCtx.GetVarTypeFloat64("TestNamespace.numVar2")

	require.True(t, numVar2 == 11)
}

func TestLoop_SimpleForLoop(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/simpleForLoop.json",
	}
	testutils.BaseRunScriptOk(t, cfg)
}

func TestLoop_WhileCustomConditionBooleanCompareLoopCase1(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("enterLoopBool", true)
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/whileCustomConditionBooleanCompareLoop.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	value, _ := runCtx.GetVarTypeBool("BoolVar")

	require.True(t, value == false)
}

func TestLoop_WhileCustomConditionBooleanCompareLoopCase2(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("enterLoopBool", false)
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/whileCustomConditionBooleanCompareLoop.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	value, _ := runCtx.GetVarTypeBool("BoolVar")

	require.True(t, value == true)
}

func TestLoop_WhileCustomConditionFloat64CompareLoop(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("totalUserCnt", 50)
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/whileCustomConditionFloat64CompareLoop.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	userCnt, _ := runCtx.GetVarTypeFloat64("userCnt")

	require.True(t, userCnt == 50)
}

func TestLoop_WhileCustomConditionFloat64CompareLoopAndCatchPointHop(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("totalUserCnt", 50)
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/whileCustomConditionFloat64CompareLoopAndCatchPointHop.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	userCnt, _ := runCtx.GetVarTypeFloat64("userCnt")

	require.True(t, userCnt == 50)
}

func TestLoop_WhileCustomConditionStringCompareLoopCase1(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("enterLoopString", "enter loop")
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/whileCustomConditionStringCompareLoop.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	value, _ := runCtx.GetVarTypeString("StringVar")

	require.True(t, value == "loop entered")
}

func TestLoop_WhileCustomConditionStringCompareLoopCase2(t *testing.T) {
	ctxCustomizer := func(ctx *script.ScriptRunContext) {
		ctx.SetConfig("enterLoopString", "not enter loop")
	}

	cfg := testutils.ScriptTestConfig{
		FS:             TestdataFS,
		ScriptFile:     "testdata/whileCustomConditionStringCompareLoop.json",
		CtxCustomizers: []testutils.RunContextCustomizer{ctxCustomizer},
	}
	_, runCtx := testutils.BaseRunScriptOk(t, cfg)

	value, _ := runCtx.GetVarTypeString("StringVar")

	require.True(t, value == "enter loop")
}

func TestLoop_RangeEndForLoop(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/rangeEndForLoop.json",
	}
	testutils.BaseRunScriptOk(t, cfg)
}

func TestLoop_InValuesForLoop(t *testing.T) {
	cfg := testutils.ScriptTestConfig{
		FS:         TestdataFS,
		ScriptFile: "testdata/inValuesForLoop.json",
	}
	testutils.BaseRunScriptOk(t, cfg)
}
