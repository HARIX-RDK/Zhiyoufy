package com.example.zhiyoufy.server.domain.dto.wms;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WmsWorkerAppFull {
	private Long id;

	private String name;

	private String workerLabels;

	private String description;

	private Boolean needConfigBeJson;

	private Date createdTime;

	private String createdBy;

	private Date modifiedTime;

	private String modifiedBy;

	private Boolean isOwner;

	private Boolean isEditor;
}
