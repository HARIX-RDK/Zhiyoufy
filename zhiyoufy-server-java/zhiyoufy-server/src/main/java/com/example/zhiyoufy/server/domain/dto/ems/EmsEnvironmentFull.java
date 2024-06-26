package com.example.zhiyoufy.server.domain.dto.ems;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmsEnvironmentFull {
	private Long id;

	private Long parentId;

	private String name;

	private String description;

	private String workerLabels;

	private String extraArgs;

	private Date createdTime;

	private String createdBy;

	private Date modifiedTime;

	private String modifiedBy;

	private Boolean isOwner;

	private Boolean isEditor;
}
