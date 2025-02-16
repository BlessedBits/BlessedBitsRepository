package com.blessedbits.SchoolHub.dto;

import com.blessedbits.SchoolHub.misc.RoleType;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    private RoleType role;
}
