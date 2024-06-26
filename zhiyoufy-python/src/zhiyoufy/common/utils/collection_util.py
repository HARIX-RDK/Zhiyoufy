class CollectionUtil:
    @staticmethod
    def dict_extract_by_keys(in_dict, key_list):
        target_dict = {}

        for key in key_list:
            if key in in_dict:
                target_dict[key] = in_dict[key]

        return target_dict

    @staticmethod
    def dict_get_by_path(key_path, cur_ns):
        key_path_items = key_path.split(".")
        for i in range(len(key_path_items) - 1):
            if key_path_items[i] not in cur_ns or not isinstance(cur_ns[key_path_items[i]], dict):
                return None, False
            cur_ns = cur_ns[key_path_items[i]]
        return cur_ns.get(key_path_items[-1]), True

    @staticmethod
    def dict_ensure_key_path(key_path, cur_ns):
        key_path_items = key_path.split(".")
        for i in range(len(key_path_items) - 1):
            if key_path_items[i] not in cur_ns or cur_ns[key_path_items[i]] is None:
                cur_ns[key_path_items[i]] = {}
            cur_ns = cur_ns[key_path_items[i]]
        return key_path_items[-1], cur_ns

    @staticmethod
    def dict_update_if_not_exist(target, source, keys):
        for key in keys:
            if key not in target and key in source:
                target[key] = source[key]

    @staticmethod
    def dict_update_on_mode(target, other, mode="override"):
        if mode == "override":
            target.update(other)
        elif mode == "merge":
            for key, value in other.items():
                if key not in target:
                    target[key] = value
                elif not (isinstance(target[key], dict) and isinstance(value, dict)):
                    target[key] = value
                else:
                    CollectionUtil.dict_update_on_mode(target[key], value, mode)
        else:
            raise Exception("invalid mode %s" % mode)
