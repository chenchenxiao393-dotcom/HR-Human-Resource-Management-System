package com.hr.service;

import com.hr.entity.BackupRecord;

public interface BackupService {
    BackupRecord backup();
    boolean restore(String backupFile);
}
