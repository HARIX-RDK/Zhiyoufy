class BlockContextBase:
    def __init__(self, block_info):
        self.block_info = block_info
        self.block_end = False

    @property
    def block_name(self):
        return self.block_info["block_name"]

    @property
    def begin_idx(self):
        return self.block_info["begin_idx"]

    @property
    def end_idx(self):
        return self.block_info["end_idx"]
