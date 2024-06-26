class ObjectUtil:
    @staticmethod
    def setattr_if_not_exist(obj, name, value):
        if not hasattr(obj, name):
            setattr(obj, name, value)
