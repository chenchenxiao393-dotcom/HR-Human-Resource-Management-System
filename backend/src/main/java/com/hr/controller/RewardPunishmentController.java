package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.RewardPunishment;
import com.hr.entity.User;
import com.hr.service.AuthService;
import com.hr.service.RewardPunishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reward-punishment")
public class RewardPunishmentController {

    @Autowired
    private RewardPunishmentService rewardPunishmentService;

    @Autowired
    private AuthService authService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<Page<RewardPunishment>> list(PageRequest pageRequest,
                                                 @RequestParam(required = false) Long employeeId,
                                                 @RequestParam(required = false) String type,
                                                 @RequestParam(required = false) String status) {
        Page<RewardPunishment> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<RewardPunishment> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("type", type);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("rp_date", "create_time");
        Page<RewardPunishment> result = rewardPunishmentService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/my")
    public Result<Page<RewardPunishment>> getMyList(PageRequest pageRequest,
                                                       @RequestParam(required = false) String type,
                                                       @RequestParam(required = false) String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = authService.getCurrentUser(username);
        if (user == null || user.getEmployeeId() == null) {
            return Result.error("未找到用户关联的员工信息");
        }

        Page<RewardPunishment> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        QueryWrapper<RewardPunishment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", user.getEmployeeId());
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("type", type);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("rp_date", "create_time");
        Page<RewardPunishment> result = rewardPunishmentService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<RewardPunishment> getById(@PathVariable Long id) {
        RewardPunishment rp = rewardPunishmentService.getById(id);
        return Result.success(rp);
    }

    @PostMapping
    public Result<String> create(@RequestBody RewardPunishment rewardPunishment) {
        rewardPunishment.setStatus("待审批");
        rewardPunishmentService.save(rewardPunishment);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody RewardPunishment rewardPunishment) {
        rewardPunishment.setId(id);
        rewardPunishmentService.updateById(rewardPunishment);
        return Result.success("更新成功");
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> approve(@PathVariable Long id, @RequestBody RewardPunishment rewardPunishment) {
        RewardPunishment existing = rewardPunishmentService.getById(id);
        existing.setStatus(rewardPunishment.getStatus());
        rewardPunishmentService.updateById(existing);
        return Result.success("审批成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        rewardPunishmentService.removeById(id);
        return Result.success("删除成功");
    }
}
