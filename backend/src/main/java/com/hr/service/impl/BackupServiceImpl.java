package com.hr.service.impl;

import com.hr.entity.BackupRecord;
import com.hr.mapper.BackupRecordMapper;
import com.hr.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BackupServiceImpl implements BackupService {

    @Autowired
    private BackupRecordMapper backupRecordMapper;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    private static final String BACKUP_DIR = "D:/hr_backups";

    @Override
    public BackupRecord backup() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = "hr_backup_" + timestamp + ".sql";
        String filePath = BACKUP_DIR + "/" + fileName;

        BackupRecord record = new BackupRecord();
        record.setBackupType("MANUAL");
        record.setFileName(fileName);
        record.setFilePath(filePath);
        record.setStatus("SUCCESS");
        record.setOperator("admin");

        try {
            ProcessBuilder pb = new ProcessBuilder(
                "mysqldump",
                "-hlocalhost",
                "-P3306",
                "-u" + username,
                "-p" + password,
                "hr_management"
            );
            pb.redirectOutput(new java.io.File(filePath));

            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                record.setStatus("SUCCESS");
                java.io.File file = new java.io.File(filePath);
                record.setFileSize(file.length());
            } else {
                record.setStatus("FAILED");
            }
        } catch (Exception e) {
            record.setStatus("FAILED");
            record.setRemark(e.getMessage());
        }

        backupRecordMapper.insert(record);
        return record;
    }

    @Override
    public boolean restore(String backupFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "mysql",
                "-hlocalhost",
                "-P3306",
                "-u" + username,
                "-p" + password,
                "hr_management"
            );
            pb.redirectInput(new java.io.File(backupFile));

            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
