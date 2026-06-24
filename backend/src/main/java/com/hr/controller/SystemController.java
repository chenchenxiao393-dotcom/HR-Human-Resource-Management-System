package com.hr.controller;

import com.hr.common.Result;
import com.hr.entity.BackupRecord;
import com.hr.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Autowired
    private BackupService backupService;

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("OK");
    }

    @PostMapping("/backup")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<BackupRecord> backup() {
        BackupRecord record = backupService.backup();
        if ("SUCCESS".equals(record.getStatus())) {
            return Result.success("备份成功", record);
        } else {
            return Result.error("备份失败");
        }
    }

    @PostMapping("/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<String> restore(@RequestParam String backupFile) {
        boolean success = backupService.restore(backupFile);
        if (success) {
            return Result.success("恢复成功");
        } else {
            return Result.error("恢复失败");
        }
    }
}
