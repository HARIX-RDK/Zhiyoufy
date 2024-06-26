package com.example.zhiyoufy.server.mapstruct;

import com.example.zhiyoufy.mbg.model.UmsUser;
import com.example.zhiyoufy.server.domain.dto.ums.UmsUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig()
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource("classpath:application-test.properties")
@Slf4j
public class UmsUserStructMapperTests {
	@BeforeEach
	void setUp(TestInfo testInfo) {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Enter setup", displayName);
	}

	@AfterEach
	void cleanup(TestInfo testInfo) {
		String displayName = testInfo.getDisplayName();

		log.debug("{}: Leave cleanup", displayName);
	}

	@Test
	@Order(10)
	public void shouldMapUmsUserToDTO() {
		//given
		UmsUser umsUser = new UmsUser();
		umsUser.setUsername("shouldMapUmsUserToDTO");
		umsUser.setPassword("shouldMapUmsUserToDTO-password");

		//when
		UmsUserDTO umsUserDTO = UmsUserStructMapper.INSTANCE.umsUserToUmsUserDTO(umsUser);

		//then
		assertThat(umsUserDTO).isNotNull();
		assertThat(umsUserDTO.getUsername()).isEqualTo(umsUser.getUsername());
	}
}
