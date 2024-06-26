class CommandHandlerBase:
    def __init__(self, dynamic_flow_base_inst):
        self.log_prefix = "%s: " % type(self).__name__

        self.dynamic_flow_base_inst = dynamic_flow_base_inst

    # region Properties
    @property
    def block_context_map(self):
        if not self.dynamic_flow_base_inst.finally_node_reached:
            return self.dynamic_flow_base_inst.datas_node_block_context_map
        else:
            return self.dynamic_flow_base_inst.finally_node_block_context_map

    @property
    def block_context_stack(self):
        if not self.dynamic_flow_base_inst.finally_node_reached:
            return self.dynamic_flow_base_inst.datas_node_block_context_stack
        else:
            return self.dynamic_flow_base_inst.finally_node_block_context_stack

    @property
    def case_context(self):
        return self.dynamic_flow_base_inst.case_context

    @case_context.setter
    def case_context(self, value):
        self.dynamic_flow_base_inst.case_context = value

    @property
    def config_inst(self):
        return self.dynamic_flow_base_inst.config_inst

    @property
    def context(self):
        return self.dynamic_flow_base_inst.context

    @property
    def global_library_base(self):
        return self.dynamic_flow_base_inst.global_library_base

    @property
    def robot_logger(self):
        return self.dynamic_flow_base_inst.robot_logger

    @property
    def runner_context(self):
        return self.dynamic_flow_base_inst.runner_context

    @property
    def script_json(self):
        return self.dynamic_flow_base_inst.script_json
    # endregion

    def setup(self, dynamic_flow_base_inst):
        pass

    def cleanup(self, case_context):
        pass

    def is_block_begin(self, command_type):
        return False

    def is_block_end(self, command_type):
        return False

    def get_last_block_context(self):
        if len(self.block_context_stack) == 0:
            return None

        return self.block_context_stack[-1]

    def pop_last_block_context(self):
        return self.block_context_stack.pop()

    def get_step_var(self, step_var_path, raise_exception_if_miss=None):
        return self.global_library_base.get_step_var(step_var_path, raise_exception_if_miss)

    def set_step_var(self, step_var_path, step_var_value):
        self.global_library_base.set_step_var(step_var_path, step_var_value)

    def process_command(self, command):
        self.dynamic_flow_base_inst.process_command(command)
