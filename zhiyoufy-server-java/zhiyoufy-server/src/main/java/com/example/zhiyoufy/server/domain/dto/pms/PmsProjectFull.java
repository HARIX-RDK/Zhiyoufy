package com.example.zhiyoufy.server.domain.dto.pms;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PmsProjectFull {
	private Long id;

	private String name;

	private String description;

	private Date createdTime;

	private String createdBy;

	private Date modifiedTime;

	private String modifiedBy;

	private Boolean isOwner;

	private Boolean isEditor;
}
