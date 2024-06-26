package hexutil

var hexDigits = []byte("0123456789abcdef")

func ToLowerHexOne(v int64) string {
	result := make([]byte, 16)
	writeHexLong(result, 0, v)
	return string(result)
}

func ToLowerHexTwo(high, low int64) string {
	result := make([]byte, 32)
	writeHexLong(result, 0, high)
	writeHexLong(result, 16, low)
	return string(result)
}

func writeHexLong(data []byte, pos int, v int64) {
	writeHexByte(data, pos+0, (byte)((v>>56)&0xff))
	writeHexByte(data, pos+2, (byte)((v>>48)&0xff))
	writeHexByte(data, pos+4, (byte)((v>>40)&0xff))
	writeHexByte(data, pos+6, (byte)((v>>32)&0xff))
	writeHexByte(data, pos+8, (byte)((v>>24)&0xff))
	writeHexByte(data, pos+10, (byte)((v>>16)&0xff))
	writeHexByte(data, pos+12, (byte)((v>>8)&0xff))
	writeHexByte(data, pos+14, (byte)(v&0xff))
}

func writeHexByte(data []byte, pos int, b byte) {
	data[pos+0] = hexDigits[(b>>4)&0xf]
	data[pos+1] = hexDigits[b&0xf]
}
