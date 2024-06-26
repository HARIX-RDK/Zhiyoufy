package com.example.zhiyoufy.server.domain.dto.ems;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmsConfigItemUpdateParam {
	private String name;

	private String configValue;
	private String tags;
	private Integer sort;
	private Boolean disabled;

	private Boolean inUse;
	private String usageId;
	private Date usageTimeoutAt;
}
