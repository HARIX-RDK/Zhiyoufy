package com.example.zhiyoufy.server.domain.dto.wms;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class WmsGroupTokenParam {
	@NotNull
	private Long workerAppId;
	@NotEmpty
	private String workerAppName;

	@NotNull
	private Long workerGroupId;
	@NotEmpty
	private String workerGroupName;

	@NotEmpty
	@Size(min = 3)
	private String name;

	@NotEmpty
	@Size(min = 8)
	private String secret;

	private Date expiryTime;

	private String description;
}
