from .BlockContextBase import BlockContextBase


class ForLoopBlockContext(BlockContextBase):
    def __init__(self, block_info):
        super().__init__(block_info)

        # range_end, in_values
        self.for_type = None

        # common
        self.iter_next_idx = None
        self.iter_next_value = None

        # range_end only
        self.range_begin = None
        self.iter_step = None
        self.range_end = None

        # in_values only
        self.in_values = None
