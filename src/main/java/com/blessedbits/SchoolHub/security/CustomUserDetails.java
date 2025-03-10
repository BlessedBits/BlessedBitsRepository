package com.blessedbits.SchoolHub.security;

import com.blessedbits.SchoolHub.models.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public class CustomUserDetails extends UserEntity implements UserDetails {
    public CustomUserDetails(UserEntity user) {
        super();
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setPassword(user.getPassword());
        this.setRole(user.getRole());
        this.setEmail(user.getEmail());
        this.setIsConfirmed(user.getIsConfirmed());
        this.setProfileImage(user.getProfileImage());
        this.setDuty(user.getDuty());
        this.setUserClass(user.getUserClass());
        this.setSchool(user.getSchool());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + getRole()));
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
        return true;
    }
}
