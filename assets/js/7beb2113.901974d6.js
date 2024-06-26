"use strict";(self.webpackChunkzhiyoufy_docs=self.webpackChunkzhiyoufy_docs||[]).push([[902],{2033:(e,n,r)=>{r.r(n),r.d(n,{assets:()=>c,contentTitle:()=>l,default:()=>h,frontMatter:()=>s,metadata:()=>t,toc:()=>a});var o=r(4848),i=r(8453);const s={title:"\u6982\u8ff0"},l=void 0,t={id:"developer-guide/framework_intro/intro",title:"\u6982\u8ff0",description:"zhiyoufy\u662f\u4e00\u5957\u57fa\u4e8eRobotFramework, pyhocon, jinja\u7b49\u5f00\u53d1\u7684\u81ea\u52a8\u5316\u6d4b\u8bd5\u6846\u67b6",source:"@site/docs/0300-developer-guide/0100-framework_intro/00100-intro.md",sourceDirName:"0300-developer-guide/0100-framework_intro",slug:"/developer-guide/framework_intro/intro",permalink:"/Zhiyoufy/developer-guide/framework_intro/intro",draft:!1,unlisted:!1,tags:[],version:"current",sidebarPosition:100,frontMatter:{title:"\u6982\u8ff0"},sidebar:"docSidebar",previous:{title:"performance-test-flow",permalink:"/Zhiyoufy/user-guide/howto/performance-test-flow"},next:{title:"\u521b\u5efa\u65b0case",permalink:"/Zhiyoufy/developer-guide/framework_intro/create-new-case"}},c={},a=[{value:"Robot Framework",id:"robot-framework",level:2},{value:"\u5982\u4f55\u63a7\u5236\u8ba9\u4e00\u4e2acase\u5931\u8d25",id:"\u5982\u4f55\u63a7\u5236\u8ba9\u4e00\u4e2acase\u5931\u8d25",level:3},{value:"RobotFramework\u539f\u751f\u7528\u6cd5",id:"robotframework\u539f\u751f\u7528\u6cd5",level:3},{value:"json\u63cf\u8ff0\u7684case",id:"json\u63cf\u8ff0\u7684case",level:2},{value:"case\u793a\u4f8b",id:"case\u793a\u4f8b",level:3},{value:"case\u6b65\u9aa4\u914d\u7f6e\u6587\u4ef6\u793a\u4f8b",id:"case\u6b65\u9aa4\u914d\u7f6e\u6587\u4ef6\u793a\u4f8b",level:3},{value:"\u81ea\u52a8\u5316\u914d\u7f6e",id:"\u81ea\u52a8\u5316\u914d\u7f6e",level:2},{value:"JSON superset features",id:"json-superset-features",level:3},{value:"\u8bfb\u53d6\u914d\u7f6e",id:"\u8bfb\u53d6\u914d\u7f6e",level:3},{value:"\u81ea\u52a8\u5316\u6267\u884c",id:"\u81ea\u52a8\u5316\u6267\u884c",level:2},{value:"zhiyoufy\u6267\u884c",id:"zhiyoufy\u6267\u884c",level:3},{value:"\u672c\u5730\u6267\u884c",id:"\u672c\u5730\u6267\u884c",level:3},{value:"case\u7ec4\u7ec7",id:"case\u7ec4\u7ec7",level:2},{value:"\u524d\u7aef\u76ee\u5f55\u89c4\u5219",id:"\u524d\u7aef\u76ee\u5f55\u89c4\u5219",level:3},{value:"\u51c6\u5907\u7c7b",id:"\u51c6\u5907\u7c7b",level:3},{value:"\u81ea\u52a8\u5316\u90e8\u7f72",id:"\u81ea\u52a8\u5316\u90e8\u7f72",level:2},{value:"zhiyoufy\u67b6\u6784",id:"zhiyoufy\u67b6\u6784",level:2},{value:"zhiyoufy-java",id:"zhiyoufy-java",level:2},{value:"zhiyoufy-web",id:"zhiyoufy-web",level:2},{value:"zhiyoufy-python",id:"zhiyoufy-python",level:2},{value:"zhiyoufy-go",id:"zhiyoufy-go",level:2},{value:"clients\u53cahandler\u5c01\u88c5",id:"clients\u53cahandler\u5c01\u88c5",level:2},{value:"worker\u5de5\u7a0b",id:"worker\u5de5\u7a0b",level:2},{value:"\u4ee3\u7801\u90e8\u5206",id:"\u4ee3\u7801\u90e8\u5206",level:3}];function d(e){const n={a:"a",code:"code",h2:"h2",h3:"h3",img:"img",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,i.R)(),...e.components};return(0,o.jsxs)(o.Fragment,{children:[(0,o.jsxs)(n.p,{children:["zhiyoufy\u662f\u4e00\u5957\u57fa\u4e8e**",(0,o.jsx)(n.a,{href:"https://robotframework.org",children:"RobotFramework"}),", ",(0,o.jsx)(n.a,{href:"https://github.com/chimpler/pyhocon",children:"pyhocon"}),", ",(0,o.jsx)(n.a,{href:"https://jinja.palletsprojects.com/en/2.10.x/",children:"jinja"}),"**\u7b49\u5f00\u53d1\u7684\u81ea\u52a8\u5316\u6d4b\u8bd5\u6846\u67b6"]}),"\n",(0,o.jsx)(n.p,{children:"\u5982\u679c\u53ea\u662f\u5f00\u53d1worker\uff0c\u5219\u53ea\u9700\u8981\u4e86\u89e3zhiyoufy-python\u90e8\u5206\uff0c\u5176\u5b83\u90e8\u5206\u901a\u8fc7\u7528\u6237\u6307\u5357\u4f1a\u4f7f\u7528\u5c31\u884c\u3002"}),"\n",(0,o.jsx)(n.h2,{id:"robot-framework",children:"Robot Framework"}),"\n",(0,o.jsxs)(n.p,{children:[(0,o.jsx)(n.a,{href:"https://robotframework.org",children:"RobotFramework"}),"\u662f\u57fa\u4e8ekeyword\u7684test\u7cfb\u7edf\uff0c\u6bd4\u5982keyword \u201cLogin\u201d\uff0ckeyword \u201cInput ***\u201d\uff0c\u57fa\u4e8e\u6211\u4eec\u7684\u60c5\u51b5\uff0c\u6211\u4eec\u8fd9\u91cc\u91c7\u7528\u4e00\u4e2acase\u90fd\u7531\u5355\u4e00keyword\u52a0\u914d\u7f6e\u6587\u4ef6\u7ec4\u6210\uff0c\u5e76\u5728\u914d\u7f6e\u6587\u4ef6\u4e2d\u8be6\u7ec6\u63cf\u8ff0\u6d4b\u8bd5\u6b65\u9aa4"]}),"\n",(0,o.jsx)(n.p,{children:"\u76f8\u6bd4\u539f\u751f\u7684\u4f18\u70b9"}),"\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsx)(n.li,{children:"\u5f53\u4f20\u9012\u590d\u6742\u7684\u53c2\u6570\u65f6\u66f4\u6e05\u6670"}),"\n",(0,o.jsx)(n.li,{children:"\u53ef\u65ad\u70b9\u8c03\u8bd5"}),"\n"]}),"\n",(0,o.jsx)(n.h3,{id:"\u5982\u4f55\u63a7\u5236\u8ba9\u4e00\u4e2acase\u5931\u8d25",children:"\u5982\u4f55\u63a7\u5236\u8ba9\u4e00\u4e2acase\u5931\u8d25"}),"\n",(0,o.jsxs)(n.p,{children:[(0,o.jsx)(n.a,{href:"https://robotframework.org",children:"RobotFramework"}),"\u672c\u8eab\u5c31\u662f\u5728\u666e\u901a\u7684python\u7a0b\u5e8f\u91cc\u8dd1\uff0c\u5b83\u662f\u901a\u8fc7catch Exception\u6765\u5224\u65adcase\u5931\u8d25\u7684\uff0c\u5f53\u6211\u4eec\u5199\u7684\u6d4b\u8bd5\u7a0b\u5e8f\u5224\u65adcase\u5931\u8d25\uff0c\u6bd4\u5982api\u8fd4\u56de\u7801\u4e0d\u7b26\u5408\u9884\u671f\u65f6\uff0c\u53ea\u8981\u629b\u51fa\u5f02\u5e38\u5c31\u53ef\u4ee5\u5bfc\u81f4\u5bf9\u5e94case\u5931\u8d25"]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-python",children:'    if delay > timedelta(seconds=data["max_delay"]):\n        raise Exception("delay %s larger than max_delay %s" % (delay, data["max_delay"]))\n'})}),"\n",(0,o.jsx)(n.h3,{id:"robotframework\u539f\u751f\u7528\u6cd5",children:"RobotFramework\u539f\u751f\u7528\u6cd5"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{children:"*** Settings ***\nDocumentation     A test suite with a single test for valid login.\n...\n...               This test has a workflow that is created using keywords in\n...               the imported resource file.\nResource          resource.txt\n\n*** Test Cases ***\nValid Login\n    Open Browser To Login Page\n    Input Username    demo\n    Input Password    mode\n    Submit Credentials\n    Welcome Page Should Be Open\n    [Teardown]    Close Browser\n"})}),"\n",(0,o.jsx)(n.h2,{id:"json\u63cf\u8ff0\u7684case",children:"json\u63cf\u8ff0\u7684case"}),"\n",(0,o.jsxs)(n.p,{children:[(0,o.jsx)(n.a,{href:"https://robotframework.org",children:"RobotFramework"}),"\u662f\u5728\u5b83\u7684case\u6587\u4ef6\u4e2d\u7ec4\u7ec7\u5177\u4f53\u6d4b\u8bd5\u6b65\u9aa4\uff0c\u4f46\u662f\u5728case\u6587\u4ef6\u4e2d\u914d\u7f6e\u5927\u91cf\u53c2\u6570\u6bd4\u8f83\u53d7\u9650\uff0c\u53ef\u8bfb\u6027\u4e5f\u4e0d\u597d\u3002"]}),"\n",(0,o.jsxs)(n.p,{children:["\u7c7b\u4f3c\u4e8e",(0,o.jsx)(n.a,{href:"https://robotframework.org",children:"RobotFramework"}),"\u81ea\u5df1\u7684keyword\u7ec4\u5408\u7cfb\u7edf\uff0c\u4f46\u6211\u4eec\u91c7\u7528json\u914d\u7f6e\u6587\u4ef6\u6765\u65b9\u4fbf\u5404\u6d4b\u8bd5\u6b65\u9aa4\u53c2\u6570\u7684\u914d\u7f6e\uff0c\u5e76\u4e14\u5229\u7528",(0,o.jsx)(n.a,{href:"https://jinja.palletsprojects.com/en/2.10.x/",children:"jinja"}),"\u7684\n\u6a21\u677f\u80fd\u529b\u6765\u8fdb\u4e00\u6b65\u589e\u52a0\u7075\u6d3b\u6027\uff0c\u5728\u6d4b\u8bd5\u4ee3\u7801\u4e2d\u5b9e\u73b0\u5404\u79cd\u529f\u80fd\u6307\u4ee4\uff0c\u7136\u540e\u5728json\u6587\u4ef6\u4e2d\u6307\u5b9a\u6307\u4ee4\u7684\u987a\u5e8f\u548c\u5bf9\u5e94\u914d\u7f6e"]}),"\n",(0,o.jsx)(n.h3,{id:"case\u793a\u4f8b",children:"case\u793a\u4f8b"}),"\n",(0,o.jsxs)(n.p,{children:["\u53ef\u4ee5\u5728\u4e0b\u9762\u793a\u4f8b\u4e2d\u770b\u5230\u5728",(0,o.jsx)(n.a,{href:"https://robotframework.org",children:"RobotFramework"}),"\u7684case\u4e2d\u53ea\u6709\u4e00\u4e2a",(0,o.jsx)(n.code,{children:"TestDynamicFlow.run"}),"\u8c03\u7528\uff0c\u5b83\u7684\u53c2\u6570\u5c31\u662f\u5bf9\u5e94case\u7684json\u6b65\u9aa4\u63cf\u8ff0"]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{children:"*** Settings ***\nDocumentation     A test suite for Dynamic Flow\n...\n...               Config Single CRUD\nResource          ../../global_resource.robot\nLibrary           zhiyoufy.app.TestDynamicFlow   WITH NAME    TestDynamicFlow\nForce Tags        nostat-00100__config_single_crud\n\n*** Test Cases ***\nTest Config Single CRUD\n    TestDynamicFlow.run  dynamic_flows_from_tpl/dynamic_flow_test/00100__config_single_crud.json.j2\n"})}),"\n",(0,o.jsx)(n.h3,{id:"case\u6b65\u9aa4\u914d\u7f6e\u6587\u4ef6\u793a\u4f8b",children:"case\u6b65\u9aa4\u914d\u7f6e\u6587\u4ef6\u793a\u4f8b"}),"\n",(0,o.jsxs)(n.p,{children:["\u6bcf\u4e00\u6b65\u90fd\u662f\u4e00\u4e2ajson object\uff0c\u5f53\u53c2\u6570\u590d\u6742\u65f6\u4e5f\u5f88\u6e05\u6670\uff0c\u53e6\u5916\u8fd8\u901a\u8fc7",(0,o.jsx)(n.a,{href:"https://github.com/chimpler/pyhocon",children:"pyhocon"}),"\u548c",(0,o.jsx)(n.a,{href:"https://jinja.palletsprojects.com/en/2.10.x/",children:"jinja"}),"\u63d0\u4f9b\u4e86\u52a8\u6001\u914d\u7f6e\u7684\u80fd\u529b"]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-json",children:'  "datas": [\n    {\n      "type": "zhiyoufy_base_login",\n      "username": "{{ zhiyoufy.user_params_group_3.username }}",\n      "password": "{{ zhiyoufy.user_params_group_3.password }}"\n    },\n\n    {\n        "type": "zhiyoufy_environment_get_single_by_name",\n        "environment_var_path": "environment_var",\n        "name": "{{ env_name }}",\n        "must_exist": true,\n        "step_description": "\u67e5\u627e\u540d\u5b57\u4e3aname\u7684environment"\n    },\n'})}),"\n",(0,o.jsx)(n.h2,{id:"\u81ea\u52a8\u5316\u914d\u7f6e",children:"\u81ea\u52a8\u5316\u914d\u7f6e"}),"\n",(0,o.jsxs)(n.p,{children:["\u914d\u7f6e\u6587\u4ef6\u6211\u4eec\u91c7\u7528",(0,o.jsx)(n.a,{href:"https://github.com/lightbend/config/blob/master/HOCON.md",children:"hocon"}),"\uff0c\u5b83\u7684\u53d8\u91cf\u8986\u76d6\uff0cmerge\u7b49\u529f\u80fd\n\u8ba9\u52a8\u6001\u7ec4\u5408config\u53d8\u7684\u5bb9\u6613"]}),"\n",(0,o.jsx)(n.h3,{id:"json-superset-features",children:(0,o.jsx)(n.a,{href:"https://github.com/lightbend/config",children:"JSON superset features"})}),"\n",(0,o.jsx)(n.p,{children:"\u76f8\u6bd4json\u591a\u7684\u529f\u80fd"}),"\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsx)(n.li,{children:"comments"}),"\n",(0,o.jsx)(n.li,{children:"includes, \u5305\u542b\u5b50\u6587\u4ef6\uff0c\u6216\u8005\u8bf4\u6587\u4ef6\u5d4c\u5957"}),"\n",(0,o.jsx)(n.li,{children:"\u53d8\u91cf\u8986\u76d6\uff0c\u540e\u9762\u7684\u8d4b\u503c\u8986\u76d6\u524d\u9762\u7684"}),"\n",(0,o.jsxs)(n.li,{children:["substitutions ",(0,o.jsx)(n.code,{children:'("foo" : ${bar}, "foo" : Hello ${who})'})]}),"\n",(0,o.jsxs)(n.li,{children:["properties-like notation ",(0,o.jsx)(n.code,{children:"(a.b=c)"})]}),"\n",(0,o.jsx)(n.li,{children:"less noisy, more lenient syntax"}),"\n",(0,o.jsxs)(n.li,{children:["substitute environment variables ",(0,o.jsx)(n.code,{children:"(logdir=${HOME}/logs)"})]}),"\n"]}),"\n",(0,o.jsx)(n.h3,{id:"\u8bfb\u53d6\u914d\u7f6e",children:"\u8bfb\u53d6\u914d\u7f6e"}),"\n",(0,o.jsx)(n.p,{children:"\u9996\u5148\u5728\u6267\u884c\u6d4b\u8bd5\u811a\u672c\u65f6\u8bbe\u7f6e\u914d\u7f6e\u6587\u4ef6\uff0c\u76f8\u5173\u4ee3\u7801\u5982\u4e0b, global_library_config\u5c31\u662f\u914d\u7f6e\u6587\u4ef6\u8def\u5f84"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-python",children:'    cmd = "robot --outputdir %s --tagstatexclude nostat-* --variable global_library_config:%s %s %s %s %s" % \\\n          (run_output_dir, config_inst.global_library_config, additional_python_path,\n           test_include, test_exclude, config_inst.test_target)\n'})}),"\n",(0,o.jsx)(n.p,{children:"\u7136\u540e\u5728\u8dd1\u5177\u4f53\u6d4b\u8bd5case\u65f6\u5c06\u8fd9\u4e2a\u914d\u7f6e\u6587\u4ef6\u4f20\u9012\u8fdb\u53bb\uff0c\u76f8\u5173\u4ee3\u7801\u5982\u4e0b\uff0c\nGlobalLibrary\u8fd9\u4e2apython\u5e93\u4f1a\u4ece\u6307\u5b9a\u8def\u5f84\u8bfb\u53d6\u914d\u7f6e\u6587\u4ef6"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{children:"*** Settings ***\nLibrary           zhiyoufy.app.GlobalLibrary  ${global_library_config}  WITH NAME    GlobalLibrary\n"})}),"\n",(0,o.jsx)(n.p,{children:"\u5177\u4f53\u914d\u7f6e\u6587\u4ef6\u793a\u4f8b"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-text",children:'zhiyoufy: {\n  addr: "http://localhost:8088"\n\n  default_update_if_exist: false,\n'})}),"\n",(0,o.jsx)(n.h2,{id:"\u81ea\u52a8\u5316\u6267\u884c",children:"\u81ea\u52a8\u5316\u6267\u884c"}),"\n",(0,o.jsxs)(n.p,{children:["\u6267\u884c\u662f\u901a\u8fc7python\u7684",(0,o.jsx)(n.a,{href:"https://docs.python.org/3.8/library/subprocess.html",children:"subprocess"}),"\u6a21\u5757\u6765\u8c03\u7528robot\u7a0b\u5e8f\uff0crobot\u662f",(0,o.jsx)(n.a,{href:"https://robotframework.org",children:"RobotFramework"}),"\u63d0\u4f9b\u7684\u4e00\u4e2a\u547d\u4ee4\u884c\u7a0b\u5e8f\uff0c\u76f8\u5173\u4ee3\u7801\u5982\u4e0b"]}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-python",children:'            cmd = f"robot --outputdir {robotframework_output_dir} --tagstatinclude stat-*" \\\n                  f" --variable global_library_config:{dst_base_conf_path} {self.additional_python_path}" \\\n                  f" {self.extra_args} {abs_test_suite_path}"\n\n            proc = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE,\n                stderr=subprocess.STDOUT, text=True, encoding="utf-8")\n'})}),"\n",(0,o.jsx)(n.h3,{id:"zhiyoufy\u6267\u884c",children:"zhiyoufy\u6267\u884c"}),"\n",(0,o.jsxs)(n.p,{children:["\u5728zhiyoufy\u4e2d\u662f",(0,o.jsx)(n.code,{children:"worker"}),"\u5728\u63a5\u5230\u4ece",(0,o.jsx)(n.code,{children:"zhiyoufy-java"}),"\u53d1\u6765\u7684\u8bf7\u6c42\u540e\u89e6\u53d1\u6267\u884c\u7684"]}),"\n",(0,o.jsx)(n.h3,{id:"\u672c\u5730\u6267\u884c",children:"\u672c\u5730\u6267\u884c"}),"\n",(0,o.jsxs)(n.p,{children:["\u53ef\u53c2\u7167",(0,o.jsx)(n.code,{children:"zhiyoufy-python/run_robot_zhiyoufy_test.py"}),"\u7f16\u5199\u4e00\u4e2a\u672c\u5730\u89e6\u53d1\u7684\u811a\u672c\uff0c\u5728\u63d0\u4ea4\u4ee3\u7801\u524d\u53ef\u4ee5\u901a\u8fc7\u5b83\u6765\u9a8c\u8bc1"]}),"\n",(0,o.jsx)(n.h2,{id:"case\u7ec4\u7ec7",children:"case\u7ec4\u7ec7"}),"\n",(0,o.jsxs)(n.p,{children:["\u56e0\u4e3a\u662f\u57fa\u4e8e",(0,o.jsx)(n.a,{href:"https://robotframework.org",children:"RobotFramework"}),"\uff0c\u6240\u4ee5\u7ec4\u7ec7\u4e0a\u662f\u57fa\u4e8e\u76ee\u5f55\u548ctag\u7684"]}),"\n",(0,o.jsx)(n.h3,{id:"\u524d\u7aef\u76ee\u5f55\u89c4\u5219",children:"\u524d\u7aef\u76ee\u5f55\u89c4\u5219"}),"\n",(0,o.jsx)(n.p,{children:"template\u76ee\u5f55\u5e94\u4e0e\u771f\u5b9eRobotFramework\u76ee\u5f55\u4e00\u4e00\u5bf9\u5e94\uff0c\u8fd9\u6837\u5229\u4e8e\u7ef4\u62a4\u4e5f\u597d\u7406\u89e3"}),"\n",(0,o.jsxs)(n.p,{children:["\u7279\u6b8a\u60c5\u51b5\u53ef\u521b\u5efa\u865a\u62df\u76ee\u5f55\uff0c\u5e94\u4ee5\u524d\u7f00",(0,o.jsx)(n.strong,{children:"virtual_"}),"\u533a\u5206"]}),"\n",(0,o.jsx)(n.p,{children:"\u76ee\u5f55\u5bf9\u5e94\u7684template\u5728zhiyoufy\u4e0a\u5f52\u5c5e\u4e8e\u4e0a\u7ea7\u76ee\u5f55"}),"\n",(0,o.jsx)(n.p,{children:"\u5047\u8bbeRobotFramework\u7684\u76ee\u5f55\u7ed3\u6784\u4e3a"}),"\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsxs)(n.li,{children:["A1","\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsx)(n.li,{children:"A1_1"}),"\n",(0,o.jsx)(n.li,{children:"A1_2"}),"\n"]}),"\n"]}),"\n",(0,o.jsxs)(n.li,{children:["B1","\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsx)(n.li,{children:"B1_1"}),"\n",(0,o.jsx)(n.li,{children:"B1_2"}),"\n"]}),"\n"]}),"\n",(0,o.jsxs)(n.li,{children:["C1","\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsx)(n.li,{children:"C1_1"}),"\n",(0,o.jsx)(n.li,{children:"C1_2"}),"\n"]}),"\n"]}),"\n"]}),"\n",(0,o.jsx)(n.p,{children:"\u5219zhiyoufy portal\u4e0a\u4e5f\u6709\u5bf9\u5e94\u7684\u8fd9\u51e0\u4e2a\u76ee\u5f55"}),"\n",(0,o.jsx)(n.p,{children:"\u7136\u540e\u6bd4\u5982A1_1\u8fd9\u4e2a\u76ee\u5f55\uff0c\u5b83\u7684\u4e0a\u7ea7\u76ee\u5f55\u662fA1\uff0c\u90a3A1_1\u5bf9\u5e94\u7684template\u5728zhiyoufy\u4e0a\u521b\u5efa\u5728A1\u4e0a"}),"\n",(0,o.jsxs)(n.p,{children:["\u5982\u679c\u76ee\u5f55\u5bf9\u5e94\u7684template\u6bd4\u8f83\u591a\uff0c\u653e\u5728\u4e0a\u7ea7\u76ee\u5f55\u4f1a\u6bd4\u8f83\u4e71\uff0c\u8fd9\u65f6\u5019\u53ef\u4ee5\u5728\u539f\u76ee\u5f55\u4e0b\u521b\u5efa\u865a\u62df\u76ee\u5f55\u6765\u7ba1\u7406\uff0c\n\u6bd4\u5982\u52a0\u5165A1_1\u670910\u79cd\u4e0d\u540ctemplate\uff0c\u90a3\u53ef\u4ee5\u5728A1_1\u4e0b\u521b\u5efa\u865a\u62df\u76ee\u5f55\u6bd4\u5982",(0,o.jsx)(n.strong,{children:"virtual_A1_1"})]}),"\n",(0,o.jsx)(n.h3,{id:"\u51c6\u5907\u7c7b",children:"\u51c6\u5907\u7c7b"}),"\n",(0,o.jsx)(n.p,{children:"case\u8fd0\u884c\u6709\u7684\u9700\u8981\u6709\u4e00\u4e9b\u524d\u7f6e\u6761\u4ef6\uff0c\u6bd4\u5982\u7528\u6237\u5b58\u5728\uff0c\u5982\u679c\u6709\u64b0\u5199\u81ea\u52a8\u5316\u811a\u672c\u6765\u505a\u51c6\u5907\u5de5\u4f5c\uff0c\u90a3\u5e94\u8be5\u628a\u51c6\u5907\u5de5\u4f5c\u653e\u5230\u72ec\u7acb\u7684\u76ee\u5f55\u4e2d\uff0c\n\u8fd9\u6837\u53ef\u4ee5\u8ba9case\u66f4\u7b80\u7ec3\u6613\u61c2\uff0c\u5f53\u7136\u53ef\u4ee5\u5728case\u63cf\u8ff0\u4e2d\u5199\u6e05\u695a\u4f9d\u8d56\u9879"}),"\n",(0,o.jsx)(n.p,{children:"\u51c6\u5907\u7c7b\u53ef\u4ee5\u6309\u4e0d\u540c\u539f\u5219\u5206\u7c7b\uff0c\u6bd4\u5982\u4ea7\u54c1\uff0c\u6bd4\u5982\u8d26\u53f7\u7ec4"}),"\n",(0,o.jsx)(n.p,{children:"\u4e0d\u540c\u811a\u672c\u7684\u6267\u884c\u6b21\u6570\u53ef\u80fd\u4e5f\u4e0d\u4e00\u6837\uff0c\u6bd4\u5982\u6709\u7684\u8d26\u53f7\u7ec4\u6709100\u4e2a\uff0c\u6709\u7684\u53ea\u67095\u4e2a\uff0c\u90a3\u4e48\u4e0d\u80fd\u7b80\u5355\u7684\u8ba9\u6240\u6709\u811a\u672c\u6267\u884c\n\u591a\u5c11\u6b21"}),"\n",(0,o.jsx)(n.p,{children:"\u8fd9\u79cd\u60c5\u51b5\u53ef\u80fd\u6ca1\u6709\u7b80\u5355\u7684\u65b9\u6cd5\u9002\u5e94\u5404\u79cd\u60c5\u51b5\uff0c\u9700\u8981\u6309\u7167\u5b9e\u9645\u9700\u8981\u8bbe\u8ba1\u811a\u672c\u6267\u884c\u987a\u5e8f\uff0c\u6267\u884c\u6b21\u6570\uff0c\u7136\u540e\u7528\u6587\u6863\u8bb0\u5f55\u4e0b\u6765\uff0c\n\u8fd9\u90e8\u5206\u5e94\u8be5\u8fd8\u662f\u51c6\u5907\u597d\u540e\u4eba\u5de5\u6309\u7167\u6587\u6863\u6267\u884c\u6bd4\u8f83\u7a33\u59a5\uff0c\u56e0\u4e3a\u51c6\u5907\u7c7b\u5e94\u8be5\u662f\u5f88\u4f4e\u9891\u7684\uff0c\u6240\u4ee5\u6548\u7387\u4e0d\u662f\u95ee\u9898\u3002"}),"\n",(0,o.jsx)(n.h2,{id:"\u81ea\u52a8\u5316\u90e8\u7f72",children:"\u81ea\u52a8\u5316\u90e8\u7f72"}),"\n",(0,o.jsxs)(n.ul,{children:["\n",(0,o.jsxs)(n.li,{children:[(0,o.jsx)(n.code,{children:"worker"}),"\u53ef\u4ee5\u6253\u5305\u6210docker image"]}),"\n",(0,o.jsxs)(n.li,{children:["\u5982\u679c",(0,o.jsx)(n.code,{children:"worker"}),"\u53ef\u4ee5\u90e8\u7f72\u5728\u4e91\u7aef\uff0c\u5219\u5efa\u8bae\u901a\u8fc7",(0,o.jsx)(n.code,{children:"helm"}),"\u90e8\u7f72\u5230k8s\u73af\u5883\u4e2d"]}),"\n"]}),"\n",(0,o.jsx)(n.h2,{id:"zhiyoufy\u67b6\u6784",children:"zhiyoufy\u67b6\u6784"}),"\n",(0,o.jsx)(n.p,{children:(0,o.jsx)(n.img,{alt:"zhiyoufy-architecture",src:r(1064).A+"",width:"541",height:"561"})}),"\n",(0,o.jsx)(n.h2,{id:"zhiyoufy-java",children:"zhiyoufy-java"}),"\n",(0,o.jsx)(n.p,{children:"\u4f7f\u7528java\u5f00\u53d1\u7684\u670d\u52a1\u5668\u540e\u7aef\uff0c\u7528\u4e8e\u7ba1\u7406\u73af\u5883\u3001project\u3001app\u7b49"}),"\n",(0,o.jsx)(n.h2,{id:"zhiyoufy-web",children:"zhiyoufy-web"}),"\n",(0,o.jsx)(n.p,{children:"\u4f7f\u7528vue\u5f00\u53d1\u7684\u524d\u7aef\u5e94\u7528\uff0c\u7528\u6237\u53ef\u4ee5\u901a\u8fc7\u8fd9\u4e2a\u521b\u5efa\u73af\u5883\u3001project\u7b49"}),"\n",(0,o.jsx)(n.h2,{id:"zhiyoufy-python",children:"zhiyoufy-python"}),"\n",(0,o.jsx)(n.p,{children:"\u5f00\u53d1\u529f\u80fd\u6d4b\u8bd5worker\u65f6\u57fa\u7840\u7684\u529f\u80fd\u5c01\u88c5\uff0c\u6bd4\u5982config\u7684\u89e3\u6790\uff0c\u4e0d\u540cutil\u7684\u5c01\u88c5\u7b49"}),"\n",(0,o.jsx)(n.h2,{id:"zhiyoufy-go",children:"zhiyoufy-go"}),"\n",(0,o.jsx)(n.p,{children:"\u5f00\u53d1\u6027\u80fd\u6d4b\u8bd5worker\u65f6\u57fa\u7840\u7684\u529f\u80fd\u5c01\u88c5\uff0c\u6bd4\u5982script\u7684\u89e3\u6790\uff0c\u57fa\u672ccommand handler\uff0cjob\u7684\u8fd0\u884c\u7b49"}),"\n",(0,o.jsx)(n.h2,{id:"clients\u53cahandler\u5c01\u88c5",children:"clients\u53cahandler\u5c01\u88c5"}),"\n",(0,o.jsx)(n.p,{children:"\u8fd9\u4e2a\u4e0d\u540c\u7528\u6237\u53ef\u4ee5\u6309\u9700\u505a\u5c01\u88c5\uff0c\u6bd4\u5982\u7528\u6237\u6709A\u3001B\u3001C\u4e09\u4e2a\u4ea7\u54c1\uff0c\u5b83\u53ef\u4ee5\u521b\u5efa\u4e00\u4e2arepo\uff0c\u7136\u540e\n\u5728\u91cc\u9762\u5305\u542b3\u4e2aclients\u548c\u5bf9\u5e94handler\uff0c\u4e5f\u53ef\u4ee5\u5efa\u7acb\u591a\u4e2arepo\uff0c\u5728\u91cc\u9762\u76f8\u5e94\u5c01\u88c5"}),"\n",(0,o.jsx)(n.p,{children:"\u8fd9\u4e2a\u4e0d\u548cworker\u653e\u5728\u4e00\u8d77\u7684\u76ee\u7684\u662f\u4e3a\u4e86\u590d\u7528\uff0cclients\u5c01\u88c5\u4e3b\u8981\u5173\u6ce8\u76ee\u6807\u4ea7\u54c1\u529f\u80fdclient\u4fa7\n\u7684\u5c01\u88c5\uff0c\u5b83\u5e76\u4e0d\u5173\u6ce8\u5177\u4f53\u7684\u6d4b\u8bd5case"}),"\n",(0,o.jsx)(n.h2,{id:"worker\u5de5\u7a0b",children:"worker\u5de5\u7a0b"}),"\n",(0,o.jsxs)(n.p,{children:["worker\u5de5\u7a0b\u4e3b\u8981\u5173\u6ce8case\u7684\u6784\u5efa\uff0c\u9996\u5148\u5b83\u4f1a\u4f9d\u8d56",(0,o.jsx)(n.code,{children:"zhiyoufy-python"}),"\uff0c\u5176\u6b21\u5b83\u4f1a\u4f9d\u8d56case\u4e2d\n\u6d89\u53ca\u7684\u4e0d\u540cclients\uff0cworker\u5de5\u7a0b\u4ee3\u7801\u6bd4\u8f83\u5c11\uff0c\u4e3b\u8981\u662fcase\u63cf\u8ff0\u548c\u4e00\u4e9b\u8d44\u6e90\u6587\u4ef6"]}),"\n",(0,o.jsx)(n.p,{children:"\u4e00\u4e2aworker\u5982\u679c\u88ab\u6d4b\u4ea7\u54c1\u6d89\u53caProductAaa, ProductBbb\uff0c\u90a3\u4e48\u5b83\u5c31\u53ef\u4ee5\u5f15\u5165\u4f9d\u8d56ProductAaaClient,\nProductBbbClient"}),"\n",(0,o.jsx)(n.p,{children:"\u53e6\u4e00\u4e2aworker\u5982\u679c\u88ab\u6d4b\u4ea7\u54c1\u6d89\u53caProductBbb, ProductCcc\uff0c\u90a3\u4e48\u5b83\u5c31\u53ef\u4ee5\u5f15\u5165\u4f9d\u8d56ProductBbbClient,\nProductCccClient"}),"\n",(0,o.jsx)(n.p,{children:"\u8fd9\u91ccclient\u90e8\u5206\u5c31\u88ab\u590d\u7528\uff0c\u4ece\u800c\u51cf\u5c11\u7ef4\u62a4\u6210\u672c"}),"\n",(0,o.jsx)(n.h3,{id:"\u4ee3\u7801\u90e8\u5206",children:"\u4ee3\u7801\u90e8\u5206"}),"\n",(0,o.jsx)(n.p,{children:"\u8fd9\u91cc\u4ee3\u7801\u4e00\u4e2a\u662fhandler\u7684\u6ce8\u518c\uff0c\u6bd4\u5982"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-python",children:"        self.add_handler(DynamicFlowProductAaaClient())\n        self.add_handler(DynamicFlowProductBbbClient())\n"})}),"\n",(0,o.jsx)(n.p,{children:"\u4e00\u4e2a\u662fGlobalLibraryBase\u4e2d\u5bf9config\u7684\u9ed8\u8ba4\u914d\u7f6e"}),"\n",(0,o.jsx)(n.pre,{children:(0,o.jsx)(n.code,{className:"language-python",children:'        if "long_timeout" not in test_step_dynamic:\n            test_step_dynamic.long_timeout = 180\n'})})]})}function h(e={}){const{wrapper:n}={...(0,i.R)(),...e.components};return n?(0,o.jsx)(n,{...e,children:(0,o.jsx)(d,{...e})}):d(e)}},1064:(e,n,r)=>{r.d(n,{A:()=>o});const o=r.p+"assets/images/zhiyoufy-architecture-f69d5bccd5c1164ead832bc211e66b51.png"},8453:(e,n,r)=>{r.d(n,{R:()=>l,x:()=>t});var o=r(6540);const i={},s=o.createContext(i);function l(e){const n=o.useContext(s);return o.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function t(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:l(e.components),o.createElement(s.Provider,{value:n},e.children)}}}]);