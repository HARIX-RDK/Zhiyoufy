package com.example.zhiyoufy.server.domain.dto.ems;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmsEnvironmentBase {
	private Long id;

	private Long parentId;

	private String name;

	private String workerLabels;
}
