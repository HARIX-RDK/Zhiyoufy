*** Settings ***
Documentation     A test suite for Dynamic Flow
...
...               Config Single CRUD
Resource          ../../global_resource.robot
Library           zhiyoufy.app.TestDynamicFlow   WITH NAME    TestDynamicFlow
Force Tags        nostat-00100__config_single_crud

*** Test Cases ***
Test Config Single CRUD
    TestDynamicFlow.run  dynamic_flows_from_tpl/dynamic_flow_test/00100__config_single_crud.json.j2
