package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.Employee;
import com.hr.entity.User;
import com.hr.service.EmployeeService;
import com.hr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public Result<Map<String, Object>> list(PageRequest pageRequest,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String employeeCode,
                                        @RequestParam(required = false) String departmentId,
                                        @RequestParam(required = false) String employeeStatus) {
        Page<Employee> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (employeeCode != null && !employeeCode.isEmpty()) {
            queryWrapper.eq("employee_code", employeeCode);
        }
        if (departmentId != null && !departmentId.isEmpty()) {
            queryWrapper.eq("department_id", departmentId);
        }
        if (employeeStatus != null && !employeeStatus.isEmpty()) {
            queryWrapper.eq("employee_status", employeeStatus);
        }

        queryWrapper.orderByDesc("create_time");
        Page<Employee> result = employeeService.page(page, queryWrapper);

        // 组装返回数据，包含员工和用户信息
        List<Map<String, Object>> records = new java.util.ArrayList<>();
        for (Employee emp : result.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", emp.getId());
            item.put("employeeCode", emp.getEmployeeCode());
            item.put("name", emp.getName());
            item.put("gender", emp.getGender());
            item.put("birthDate", emp.getBirthDate());
            item.put("phone", emp.getPhone());
            item.put("email", emp.getEmail());
            item.put("address", emp.getAddress());
            item.put("householdType", emp.getHouseholdType());
            item.put("politicalAffiliation", emp.getPoliticalAffiliation());
            item.put("healthStatus", emp.getHealthStatus());
            item.put("hireDate", emp.getHireDate());
            item.put("departmentId", emp.getDepartmentId());
            item.put("position", emp.getPosition());
            item.put("employeeStatus", emp.getEmployeeStatus());
            item.put("salary", emp.getSalary());
            item.put("createTime", emp.getCreateTime());
            item.put("updateTime", emp.getUpdateTime());

            // 查询关联的用户信息
            if (emp.getId() != null) {
                QueryWrapper<User> userQuery = new QueryWrapper<>();
                userQuery.eq("employee_id", emp.getId());
                User user = userService.getOne(userQuery);
                if (user != null) {
                    item.put("userId", user.getId());
                    item.put("role", user.getRole());
                    item.put("userStatus", user.getStatus());
                    item.put("needChangePassword", user.getNeedChangePassword());
                }
            }
            records.add(item);
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("records", records);
        resultMap.put("total", result.getTotal());
        resultMap.put("pages", result.getPages());
        resultMap.put("size", result.getSize());
        resultMap.put("current", result.getCurrent());
        return Result.success(resultMap);
    }

    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<Map<String, Object>> create(@RequestBody Map<String, Object> data) {
        // 构建员工信息
        Employee employee = new Employee();
        employee.setEmployeeCode((String) data.get("employeeCode"));
        employee.setName((String) data.get("name"));
        employee.setGender((String) data.get("gender"));

        // 处理出生日期
        Object birthDate = data.get("birthDate");
        if (birthDate != null && !birthDate.toString().isEmpty()) {
            employee.setBirthDate(java.time.LocalDate.parse(birthDate.toString()));
        }

        employee.setPhone((String) data.get("phone"));
        employee.setEmail((String) data.get("email"));
        employee.setAddress((String) data.get("address"));
        employee.setHouseholdType((String) data.get("householdType"));
        employee.setPoliticalAffiliation((String) data.get("politicalAffiliation"));
        employee.setHealthStatus((String) data.get("healthStatus"));

        // 处理入职日期
        Object hireDate = data.get("hireDate");
        if (hireDate != null && !hireDate.toString().isEmpty()) {
            employee.setHireDate(java.time.LocalDate.parse(hireDate.toString()));
        }

        Object deptId = data.get("departmentId");
        if (deptId != null && !deptId.toString().isEmpty()) {
            employee.setDepartmentId(Long.valueOf(deptId.toString()));
        }
        employee.setPosition((String) data.get("position"));
        employee.setEmployeeStatus((String) data.getOrDefault("employeeStatus", "在职"));

        Object salary = data.get("salary");
        if (salary != null && !salary.toString().isEmpty()) {
            employee.setSalary(Double.valueOf(salary.toString()));
        }

        // 保存员工信息
        employeeService.save(employee);

        // 自动创建用户账号（使用姓名作为登录账号，重名时加编号）
        String baseUsername = employee.getName();
        String username = baseUsername;
        int suffix = 1;
        while (userService.getOne(new QueryWrapper<User>().eq("username", username).eq("deleted", 0)) != null) {
            username = baseUsername + suffix;
            suffix++;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName(employee.getName());
        user.setRole((String) data.getOrDefault("role", "EMPLOYEE"));
        user.setEmployeeId(employee.getId());
        user.setStatus((String) data.getOrDefault("userStatus", "NORMAL"));
        user.setNeedChangePassword(true);
        userService.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("employeeId", employee.getId());
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("defaultPassword", "123456");
        return Result.success(result);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        // 更新员工信息
        Employee employee = new Employee();
        employee.setId(id);

        // 处理出生日期
        Object birthDate = data.get("birthDate");
        if (birthDate != null && !birthDate.toString().isEmpty()) {
            employee.setBirthDate(java.time.LocalDate.parse(birthDate.toString()));
        }

        // 处理入职日期
        Object hireDate = data.get("hireDate");
        if (hireDate != null && !hireDate.toString().isEmpty()) {
            employee.setHireDate(java.time.LocalDate.parse(hireDate.toString()));
        }

        Object deptId = data.get("departmentId");
        if (deptId != null && !deptId.toString().isEmpty()) {
            employee.setDepartmentId(Long.valueOf(deptId.toString()));
        }

        Object salary = data.get("salary");
        if (salary != null && !salary.toString().isEmpty()) {
            employee.setSalary(Double.valueOf(salary.toString()));
        }

        employee.setEmployeeCode((String) data.get("employeeCode"));
        employee.setName((String) data.get("name"));
        employee.setGender((String) data.get("gender"));
        employee.setPhone((String) data.get("phone"));
        employee.setEmail((String) data.get("email"));
        employee.setAddress((String) data.get("address"));
        employee.setHouseholdType((String) data.get("householdType"));
        employee.setPoliticalAffiliation((String) data.get("politicalAffiliation"));
        employee.setHealthStatus((String) data.get("healthStatus"));
        employee.setPosition((String) data.get("position"));
        employee.setEmployeeStatus((String) data.get("employeeStatus"));

        employeeService.updateById(employee);

        // 同步更新用户信息
        Object userIdObj = data.get("userId");
        Object role = data.get("role");
        Object userStatus = data.get("userStatus");
        if (userIdObj != null && !userIdObj.toString().isEmpty()) {
            User user = userService.getById(Long.valueOf(userIdObj.toString()));
            if (user != null) {
                if (role != null) user.setRole((String) role);
                if (userStatus != null) user.setStatus((String) userStatus);
                if (data.get("name") != null) {
                    user.setRealName((String) data.get("name"));
                }
                userService.updateById(user);
            }
        }

        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        // 先查询员工关联的用户
        Employee employee = employeeService.getById(id);
        if (employee != null && employee.getId() != null) {
            // 删除关联的用户账号
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("employee_id", id);
            userService.remove(userQueryWrapper);
        }
        // 删除员工信息
        employeeService.removeById(id);
        return Result.success("删除成功");
    }
}
