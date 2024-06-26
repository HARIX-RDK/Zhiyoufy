*** Settings ***
Documentation     A test suite for DynamicFlow.
...
...               Dynamic flow tests
Resource          ../../../global_resource.robot
Library           zhiyoufy.app.TestDynamicFlow   WITH NAME    TestDynamicFlow
Force Tags        nostat-00400__prepare_project_zhiyoufy_auto_test_job_related  parallel-no

*** Test Cases ***
Prepare Users
    TestDynamicFlow.run  dynamic_flows_from_tpl/100_prepare_for_test/00200_prepare_others/00400__prepare_project_zhiyoufy_auto_test_job_related.json.j2