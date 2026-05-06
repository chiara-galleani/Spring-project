package com.example.spring_boot_mysql_project.user;

import lombok.Data;

@Data
public class RoleUpdateRequest {
    private Role role; // il client manda "USER" o "ADMIN"
}