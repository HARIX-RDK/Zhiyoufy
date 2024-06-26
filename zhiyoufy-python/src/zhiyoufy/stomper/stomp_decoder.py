import re

from .core import StompException

# regexp to check that the buffer starts with a command.
command_re = re.compile("^(.*?)\n")

# Separator between the header and the body.
len_sep = len("\n\n")
# Footer after the message body.
len_footer = len("\x00")

char_lf_int = b"\n"[0]
char_cr_int = b"\r"[0]


class StompDecoder:
    """
    reference org.springframework.messaging.simp.stomp.StompDecoder
    """
    def __init__(self):
        self._msg_buffer_info = MessageBufferInfo()

    def decode(self, buffer):
        messages = []
        left_buffer = buffer

        while left_buffer:
            message, left_buffer = self.decode_message(left_buffer)

            if message:
                messages.append(message)
            else:
                break

        return messages, left_buffer, self._msg_buffer_info.content_length

    def decode_message(self, buffer):
        left_buffer = self._skip_eol(buffer)

        if not left_buffer or len(left_buffer) < 6:
            return None, left_buffer

        self._update_msg_buffer_info(left_buffer)

        if not self._msg_buffer_info.msg_bytes:
            return None, left_buffer

        msg_bytes = self._msg_buffer_info.msg_bytes
        head_bytes = self._msg_buffer_info.head_bytes
        cmd = self._msg_buffer_info.cmd
        headers = self._msg_buffer_info.headers

        self._msg_buffer_info = MessageBufferInfo()

        msg_data = left_buffer[:msg_bytes]
        left_buffer = left_buffer[msg_bytes:]

        # head_bytes points to the start of the '\n\n' at the end of the header,
        # so 2 bytes beyond this is the start of the body. The body EXCLUDES
        # the final one bytes, which are '\x00'.
        body = msg_data[head_bytes + 2:-1].decode("utf-8")
        msg = {
            "cmd": cmd,
            "headers": headers,
            "body": body,
        }

        return msg, left_buffer

    def _skip_eol(self, buffer):
        if not buffer:
            return None

        idx = 0
        buf_len = len(buffer)

        while idx < buf_len:
            if buffer[idx] == char_lf_int:
                idx += 1
                continue
            elif buffer[idx] == char_cr_int:
                if (idx + 1) < buf_len:
                    if buffer[idx + 1] == char_lf_int:
                        idx += 2
                        continue
                    else:
                        raise StompException("'\\r' must be followed by '\\n'")
                else:
                    break
            else:
                break

        if idx < buf_len:
            if idx == 0:
                return buffer
            else:
                return buffer[idx:]
        else:
            return None

    @staticmethod
    def unescape(header_field):
        if b"\\" not in header_field:
            return header_field

        unescaped_header_field = b""
        idx = 0

        while idx < len(header_field):
            if header_field[idx] == b"\\"[0]:
                if idx + 1 == len(header_field):
                    raise Exception("invalid stomp header field")
                if header_field[idx + 1] == char_cr_int:
                    unescaped_header_field += b"\r"
                elif header_field[idx + 1] == char_lf_int:
                    unescaped_header_field += b"\n"
                elif header_field[idx + 1] == b"c"[0]:
                    unescaped_header_field += b":"
                elif header_field[idx + 1] == b"\\"[0]:
                    unescaped_header_field += b"\\"
                else:
                    raise Exception("Invalid escape sequence")
                idx += 2
            else:
                unescaped_header_field += header_field[idx]
                idx += 1

        return unescaped_header_field

    def _update_msg_buffer_info(self, buffer):
        msg_buffer_info = self._msg_buffer_info

        if not msg_buffer_info.head_bytes:
            try:
                head_end_idx = buffer.index(b"\n\n")
            except ValueError:
                return

            msg_buffer_info.head_bytes = head_end_idx

            head_data = buffer[:head_end_idx]
            elems = head_data.split(b"\n")
            cmd = elems.pop(0).decode("utf-8")
            headers = {}
            # We can't use a simple split because the value can legally contain
            # colon characters (for example, the session returned by ActiveMQ).
            for e in elems:
                i = e.find(b":")
                if i < 0:
                    raise Exception("invalid header line")
                k = StompDecoder.unescape(e[:i]).decode("utf-8")
                v = StompDecoder.unescape(e[i + 1:]).decode("utf-8")
                headers[k] = v

            msg_buffer_info.cmd = cmd
            msg_buffer_info.headers = headers

            if "content-length" in headers:
                msg_buffer_info.content_length = int(headers["content-length"])

        if msg_buffer_info.content_length is not None:
            req_len = msg_buffer_info.head_bytes + len_sep + msg_buffer_info.content_length + len_footer

            if len(buffer) < req_len:
                # We don't have enough bytes in the buffer.
                return
            else:
                # We have enough bytes in the buffer
                msg_buffer_info.msg_bytes = req_len

                return
        else:
            # There was no content-length header, so just look for the
            # message terminator ('\x00' ).
            try:
                j = buffer.index(b"\x00")
            except ValueError:
                return

            msg_buffer_info.msg_bytes = j + 1

            return


class MessageBufferInfo:
    def __init__(self, msg_bytes=None, head_bytes=None, content_length=None):
        self.msg_bytes = msg_bytes
        self.head_bytes = head_bytes
        self.content_length = content_length
        self.cmd = None
        self.headers = {}
