import collections

from .core import StompException


class BufferingStompDecoder:
    """
    Reference spring org.springframework.messaging.simp.stomp.BufferingStompDecoder
    """

    def __init__(self, stomp_decoder, buffer_size_limit=1_000_000):
        self._stomp_decoder = stomp_decoder
        self._buffer_size_limit = buffer_size_limit
        self._chunks = collections.deque()
        self._expected_content_length = None

    @property
    def stomp_decoder(self):
        return self._stomp_decoder

    @property
    def buffer_size_limit(self):
        return self._buffer_size_limit

    @property
    def expected_content_length(self):
        return self._expected_content_length

    def decode(self, new_data):
        self._chunks.append(new_data)
        self._check_buffer_limits()

        content_length = self._expected_content_length

        if content_length is not None and self.get_buffer_size() < content_length:
            return []

        buffer_to_decode = self._assemble_chunks_and_reset()
        messages, remaining, content_length = self._stomp_decoder.decode(buffer_to_decode)

        if remaining:
            self._chunks.append(remaining)
            self._expected_content_length = content_length

        return messages

    def get_buffer_size(self):
        size = 0
        for chunk in self._chunks:
            size += len(chunk)
        return size

    def _assemble_chunks_and_reset(self):
        result = b"".join(self._chunks)

        self._chunks.clear()
        self._expected_content_length = None

        return result

    def _check_buffer_limits(self):
        content_length = self._expected_content_length

        if content_length and content_length > self._buffer_size_limit:
            raise StompException("STOMP 'content-length' header value " + self._expected_content_length +
                                 "  exceeds configured buffer size limit " + self._buffer_size_limit)

        if self.get_buffer_size() > self._buffer_size_limit:
            raise StompException("The configured STOMP buffer size limit of " +
                                 self._buffer_size_limit + " bytes has been exceeded")
