*** Settings ***
Documentation     A test suite for DynamicFlow.
...
...               Dynamic flow tests
Resource          ../../../global_resource.robot
Library           zhiyoufy.app.TestDynamicFlow   WITH NAME    TestDynamicFlow
Force Tags        nostat-00100__prepare_environments  parallel-no

*** Test Cases ***
Prepare Users
    TestDynamicFlow.run  dynamic_flows_from_tpl/100_prepare_for_test/00200_prepare_others/00100__prepare_environments.json.j2