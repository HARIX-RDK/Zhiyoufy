package com.example.zhiyoufy.server.domain.dto.ums;

import java.util.Date;

import com.example.zhiyoufy.common.util.jsonviews.PrivateView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 登录返回数据
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VirtualLoginResponseData {
    @JsonView(PrivateView.class)
    private Long id;

    private String username;

    private String email;

    private String note;

    private Date createTime;

    private Date virtualLoginTime;

    private Boolean enabled;

    private Boolean sysAdmin;

    private Boolean admin;

}
