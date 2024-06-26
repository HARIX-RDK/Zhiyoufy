package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.server.domain.dto.elk.ElkSwitchSetAllReq;
import com.example.zhiyoufy.server.domain.dto.elk.ElkSwitchSetSomeReq;
import com.example.zhiyoufy.server.domain.dto.elk.ElkSwitchState;
import com.example.zhiyoufy.server.service.ElkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_ELK_SWITCH;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_ELK_SWITCH_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_ELK_SWITCH_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_ELK_SWITCH_GET_STATE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_ELK_SWITCH_SET_ALL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_ELK_SWITCH_SET_SOME;

@RestController
@RequestMapping("/zhiyoufy-api/v1/elk-switch")
public class ElkController {
	@Autowired
	ElkService elkService;

	@ElkRecordable(type = ZHIYOUFY_ELK_SWITCH_SET_ALL,
			tags = {ZHIYOUFY_ELK_SWITCH, ZHIYOUFY_ELK_SWITCH_WRITE})
	@PostMapping(value = "/set-all")
	public CommonResult setAll(@RequestBody ElkSwitchSetAllReq setAllReq) {
		elkService.setAll(setAllReq.getAllOn());

		return CommonResult.success();
	}

	@ElkRecordable(type = ZHIYOUFY_ELK_SWITCH_SET_SOME,
			tags = {ZHIYOUFY_ELK_SWITCH, ZHIYOUFY_ELK_SWITCH_WRITE})
	@PostMapping(value = "/set-some")
	public CommonResult setSome(@RequestBody ElkSwitchSetSomeReq setSomeReq) {
		elkService.setSome(setSomeReq.getSomeSwitches());

		return CommonResult.success();
	}

	@ElkRecordable(type = ZHIYOUFY_ELK_SWITCH_GET_STATE,
			tags = {ZHIYOUFY_ELK_SWITCH, ZHIYOUFY_ELK_SWITCH_READ})
	@GetMapping(value = "/get-state")
	public CommonResult<ElkSwitchState> getState() {
		ElkSwitchState elkSwitchState = elkService.getState();

		return CommonResult.success(elkSwitchState);
	}
}
