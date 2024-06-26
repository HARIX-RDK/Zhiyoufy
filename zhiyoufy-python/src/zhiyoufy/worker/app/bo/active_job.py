class ActiveJob(dict):
    def __init__(self, templateId=None, templateName=None, environmentId=None, environmentName=None,
                 runNum=None, parallelNum=None, runGuid=None, index=None, **kwargs):
        dict.__init__(self, templateId=templateId, templateName=templateName,
                      environmentId=environmentId, environmentName=environmentName,
                      runNum=runNum, parallelNum=parallelNum,
                      runGuid=runGuid, index=index)

    @property
    def job_key(self):
        return "runGuid-%s-index-%s" % (self["runGuid"], self["index"])

    @property
    def job_dir_key(self):
        return "%s/%s-index-%s" % (
            self["runGuid"][:8],
            self["runGuid"][-8:],
            self["index"])
