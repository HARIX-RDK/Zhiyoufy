from .BlockContextBase import BlockContextBase


class WhileLoopBlockContext(BlockContextBase):
    def __init__(self, block_info):
        super().__init__(block_info)

        self.var_path = None
        self.target_var_path = None
        self.operator = "=="
        self.var_type = "bool"
        self.return_if_true = True
