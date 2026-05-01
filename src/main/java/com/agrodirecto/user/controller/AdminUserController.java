package com.agrodirecto.user.controller;

import com.agrodirecto.user.dto.AdminUserDetailResponse;
import com.agrodirecto.user.dto.AdminUserSummaryResponse;
import com.agrodirecto.user.service.AdminUserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<AdminUserSummaryResponse>> users() {
        return ResponseEntity.ok(adminUserService.listUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserDetailResponse> userDetail(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(adminUserService.getUserDetail(userId));
    }
}
