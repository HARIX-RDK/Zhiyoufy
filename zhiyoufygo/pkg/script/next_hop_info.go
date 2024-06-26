package script

type NextHopInfo struct {
	ToContinue     bool
	ToBreak        bool
	NextCommandIdx int
}

func NewNextHopInfo() NextHopInfo {
	return NextHopInfo{
		NextCommandIdx: -1,
	}
}
