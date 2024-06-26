package com.example.zhiyoufy.server.domain.bo.ums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.zhiyoufy.mbg.model.UmsPermission;
import com.example.zhiyoufy.mbg.model.UmsRole;
import com.example.zhiyoufy.mbg.model.UmsUser;
import lombok.Getter;
import lombok.Setter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * SpringSecurity需要的用户详情
 */
@Getter
@Setter
public class UmsUserDetails implements UserDetails {
	private UmsUser umsUser;
	private List<String> roleNameList;
	private List<GrantedAuthority> authorityList;

	private boolean admin;
	private boolean sysAdmin;

	public UmsUserDetails(UmsUser umsUser, List<UmsRole> roleList,
			List<UmsPermission> permissionList) {
		this.umsUser = umsUser;

		roleNameList = new ArrayList<>();
		authorityList = new ArrayList<>();

		if (umsUser.getSysAdmin()) {
			sysAdmin = true;
			roleNameList.add("sysAdmin");
			authorityList.add(new SimpleGrantedAuthority("roles/sysAdmin"));
		}

		if (umsUser.getAdmin()) {
			admin = true;
			roleNameList.add("admin");
			authorityList.add(new SimpleGrantedAuthority("roles/admin"));
		}

		roleList.forEach(role -> {
			roleNameList.add(role.getName());
			authorityList.add(new SimpleGrantedAuthority("roles/" + role.getName()));
		});

		permissionList.forEach(permission -> {
			authorityList.add(new SimpleGrantedAuthority(permission.getName()));
		});
	}

	public Long getUserId() {
		return umsUser.getId();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorityList;
	}

	@Override
	public String getPassword() {
		return umsUser.getPassword();
	}

	@Override
	public String getUsername() {
		return umsUser.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return umsUser.getEnabled();
	}
}
