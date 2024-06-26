package com.example.zhiyoufy.server.support.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.server.domain.dto.ems.EmsEnvironmentFull;
import com.example.zhiyoufy.server.domain.dto.ums.FormLoginParam;
import com.example.zhiyoufy.server.domain.dto.ums.LoginResponseData;
import com.example.zhiyoufy.server.support.model.ZhiyoufyTestProperties;
import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

@Getter
@Setter
public class ControllerTestHelper {
	@Autowired
	WebClient.Builder webClientBuilder;

	@Autowired
	ZhiyoufyTestProperties zhiyoufyTestProperties;

	int port = 0;
	WebClient webClient;
	LoginResponseData loginResponseData;

	public WebClient getWebClient(String path) throws Exception {
		webClient = webClientBuilder.baseUrl(getHttpUrl(path).toString())
				.build();
		return webClient;
	}

	public URI getHttpUrl(String path) throws URISyntaxException {
		return getUrl(path, "http");
	}

	public URI getUrl(String path, String protocol) throws URISyntaxException {
		// local_test, local_run
		String target = "local_test";

		if (target.equals("local_test")) {
			return new URI(protocol + "://localhost:" + this.port + path);
		} else if (target.equals("local_run")) {
			return new URI(protocol + "://localhost:18080" + path);
		} else {
			throw new RuntimeException("Invalid target");
		}
	}

	public LoginResponseData formLoginAdmin() {
		return formLogin("admin", zhiyoufyTestProperties.getAdminPassword());
	}

	public LoginResponseData formLogin(String username, String password) {
		// given
		FormLoginParam loginParam = FormLoginParam.builder()
				.username(username)
				.password(password)
				.build();

		// when
		CommonResult<LoginResponseData> result = webClient.post()
				.uri("/zhiyoufy-api/v1/user/form-login")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(loginParam)
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<LoginResponseData>>() {})
				.block();

		// then
		assertThat(result).isNotNull();

		loginResponseData = result.getData();

		return loginResponseData;
	}

	public EmsEnvironmentFull getEnvironmentByName(String envName) {
		List<EmsEnvironmentFull> environmentList = getEnvironmentList(envName);

		EmsEnvironmentFull environmentFull = environmentList.stream()
				.filter(emsEnvironmentFull -> {
					return emsEnvironmentFull.getName().equals(envName);
				})
				.findAny()
				.orElse(null);

		return environmentFull;
	}

	public List<EmsEnvironmentFull> getEnvironmentList(String keyword) {
		// when
		CommonResult<CommonPage<EmsEnvironmentFull>> result = webClient.get()
				.uri("/zhiyoufy-api/v1/environment/environment-list?keyword="+keyword)
				.header("Authorization",
						"Bearer " + loginResponseData.getToken())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<CommonResult<CommonPage<EmsEnvironmentFull>>>() {})
				.block();

		// then
		assertThat(result).isNotNull();

		// given
		List<EmsEnvironmentFull> environmentList = result.getData().getList();

		return environmentList;
	}
}
