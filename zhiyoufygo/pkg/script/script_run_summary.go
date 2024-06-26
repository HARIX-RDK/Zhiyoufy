package script

import "time"

type ScriptRunSummary struct {
	RunId             string
	CreateTime        time.Time
	UpdateTime        time.Time
	Status            ScriptJobStatus
	Detail            string
	StopRequested     bool
	CurrentNodePath   string
	CurrentCommandIdx int
	CurrentCommand    string
	CurrentCommandSeq int
	ExtendInfo        string
}

func (summary ScriptRunSummary) Clone() ScriptRunSummary {
	return ScriptRunSummary{
		RunId:             summary.RunId,
		CreateTime:        summary.CreateTime,
		UpdateTime:        summary.UpdateTime,
		Status:            summary.Status,
		Detail:            summary.Detail,
		StopRequested:     summary.StopRequested,
		CurrentNodePath:   summary.CurrentNodePath,
		CurrentCommandIdx: summary.CurrentCommandIdx,
		CurrentCommand:    summary.CurrentCommand,
		CurrentCommandSeq: summary.CurrentCommandSeq,
		ExtendInfo:        summary.ExtendInfo,
	}
}
