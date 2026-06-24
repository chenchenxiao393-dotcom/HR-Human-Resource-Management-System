package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.Contract;
import com.hr.entity.User;
import com.hr.mapper.UserMapper;
import com.hr.security.JwtTokenUtil;
import com.hr.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping
    public Result<Page<Contract>> list(PageRequest pageRequest,
                                        @RequestParam(required = false) Long employeeId,
                                        @RequestParam(required = false) String status) {
        Page<Contract> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Contract> result = contractService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Contract> getById(@PathVariable Long id) {
        Contract contract = contractService.getById(id);
        return Result.success(contract);
    }

    @GetMapping("/employee/{employeeId}")
    public Result<List<Contract>> getByEmployeeId(@PathVariable Long employeeId) {
        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId);
        queryWrapper.orderByDesc("create_time");
        return Result.success(contractService.list(queryWrapper));
    }

    @GetMapping("/my")
    public Result<List<Contract>> getMyContracts(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long employeeId = getCurrentEmployeeId(authHeader);
        if (employeeId == null) {
            return Result.error("无法获取用户信息");
        }

        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("employee_id", employeeId);
        queryWrapper.orderByDesc("create_time");
        return Result.success(contractService.list(queryWrapper));
    }

    private Long getCurrentEmployeeId(String authHeader) {
        String username = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        if (username == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                username = auth.getName();
            }
        }
        if (username == null) {
            return null;
        }
        User user = userMapper.selectOne(
            new QueryWrapper<User>().eq("username", username).eq("deleted", 0)
        );
        return user != null ? user.getEmployeeId() : null;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> create(@RequestBody Contract contract) {
        contractService.save(contract);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody Contract contract) {
        contract.setId(id);
        contractService.updateById(contract);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        contractService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/expiring")
    public Result<List<Contract>> getExpiringContracts() {
        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "生效中");
        queryWrapper.le("end_date", LocalDate.now().plusMonths(1));
        return Result.success(contractService.list(queryWrapper));
    }
}
