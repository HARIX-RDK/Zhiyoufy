from zhiyoufy.common.dynamicflow import GlobalLibraryBase

from zhiyoufy.app import GlobalLibrary
from zhiyoufy.app.TestDynamicFlow import TestDynamicFlow


def debug_zhiyoufy_test(cases_list):
    GlobalLibraryBase.g_global_library_base = None
    GlobalLibrary.g_global_library = None
    GlobalLibrary.GlobalLibrary("config/zhiyoufy_test/robot.conf")

    for test_config in cases_list:
        test_dynamic_flow_inst = TestDynamicFlow()

        if isinstance(test_config, str):
            test_dynamic_flow_inst.run("dynamic_flows_from_tpl/" + test_config)
        else:
            test_dynamic_flow_inst.run("dynamic_flows_from_tpl/" + test_config[0], test_config[1])


if __name__ == '__main__':
    prepare_for_test_list = [
        "100_prepare_for_test/00100_prepare_by_admin/00100__prepare_users.json.j2",
        "100_prepare_for_test/00100_prepare_by_admin/00200__prepare_project_zhiyoufy_auto_test_project.json.j2",
        "100_prepare_for_test/00100_prepare_by_admin/00300__prepare_worker_app_zhiyoufy_auto_test_app.json.j2",

        # environment, config singles, config collections
        "100_prepare_for_test/00200_prepare_others/00100__prepare_environments.json.j2",
        "100_prepare_for_test/00200_prepare_others/00200__prepare_config_for_zhiyoufy_single.json.j2",
        "100_prepare_for_test/00200_prepare_others/00300__prepare_config_for_zhiyoufy_collection.json.j2",

        # project, job folder and job template
        "100_prepare_for_test/00200_prepare_others/00400__prepare_project_zhiyoufy_auto_test_job_related.json.j2",

        # worker app, worker group, group token
        "100_prepare_for_test/00200_prepare_others/00500__prepare_worker_app_zhiyoufy_auto_test_group_related.json.j2",
    ]

    dynamic_flow_test_list = [
        "dynamic_flow_test/00100__config_single_crud.json.j2",
    ]

    full_cases_list = []
    full_cases_list.extend(prepare_for_test_list)
    full_cases_list.extend(dynamic_flow_test_list)

    debug_target = "debug_dev"

    cases_list = []

    if debug_target == "full_cases":
        cases_list = full_cases_list
    elif debug_target == "debug_prepare_for_test":
        cases_list = prepare_for_test_list
    elif debug_target == "debug_dynamic_flow_test":
        cases_list = dynamic_flow_test_list
    elif debug_target == "debug_dev":
        cases_list = [
            "private_debug.json.j2",
            # "dynamic_flow_test/00100__config_single_crud.json.j2",
        ]

    for trial in range(1):
        debug_zhiyoufy_test(cases_list)

    print("finish")
