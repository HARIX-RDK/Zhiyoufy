package com.example.zhiyoufy.server.domain.dto.wms;

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
public class WmsGroupTokenUpdateParam {
	private String name;

	private String secret;

	private Date expiryTime;

	private String description;
}
