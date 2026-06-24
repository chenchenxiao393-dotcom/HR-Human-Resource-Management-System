package com.hr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.PageRequest;
import com.hr.common.Result;
import com.hr.entity.Appraisal;
import com.hr.service.AppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appraisal")
public class AppraisalController {

    @Autowired
    private AppraisalService appraisalService;

    @GetMapping
    public Result<Page<Appraisal>> list(PageRequest pageRequest,
                                          @RequestParam(required = false) Long employeeId,
                                          @RequestParam(required = false) String appraisalPeriod) {
        Page<Appraisal> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());

        QueryWrapper<Appraisal> queryWrapper = new QueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq("employee_id", employeeId);
        }
        if (appraisalPeriod != null && !appraisalPeriod.isEmpty()) {
            queryWrapper.eq("appraisal_period", appraisalPeriod);
        }

        queryWrapper.orderByDesc("appraisal_date", "create_time");
        Page<Appraisal> result = appraisalService.page(page, queryWrapper);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<Appraisal> getById(@PathVariable Long id) {
        Appraisal appraisal = appraisalService.getById(id);
        return Result.success(appraisal);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> create(@RequestBody Appraisal appraisal) {
        appraisalService.save(appraisal);
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
    public Result<String> update(@PathVariable Long id, @RequestBody Appraisal appraisal) {
        appraisal.setId(id);
        appraisalService.updateById(appraisal);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> delete(@PathVariable Long id) {
        appraisalService.removeById(id);
        return Result.success("删除成功");
    }
}
