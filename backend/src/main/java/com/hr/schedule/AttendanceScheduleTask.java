package com.hr.schedule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hr.entity.Attendance;
import com.hr.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AttendanceScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceScheduleTask.class);

    @Autowired
    private AttendanceService attendanceService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void processPreviousDayAttendance() {
        LocalDate previousDay = LocalDate.now().minusDays(1);
        logger.info("开始处理前一天({})未签退的考勤记录", previousDay);

        try {
            QueryWrapper<Attendance> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attendance_date", previousDay);
            queryWrapper.isNotNull("check_in_time");
            queryWrapper.and(wrapper -> wrapper.isNull("check_out_time").or().eq("check_out_time", ""));
            queryWrapper.eq("deleted", 0);

            List<Attendance> list = attendanceService.list(queryWrapper);

            if (list != null && !list.isEmpty()) {
                logger.info("找到{}条未签退的考勤记录，开始更新状态为未签退", list.size());
                
                UpdateWrapper<Attendance> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("attendance_date", previousDay);
                updateWrapper.isNotNull("check_in_time");
                updateWrapper.and(wrapper -> wrapper.isNull("check_out_time").or().eq("check_out_time", ""));
                updateWrapper.eq("deleted", 0);
                updateWrapper.set("status", "未签退");
                
                boolean result = attendanceService.update(updateWrapper);
                if (result) {
                    logger.info("成功更新{}条未签退考勤记录的状态", list.size());
                } else {
                    logger.warn("更新未签退考勤记录状态失败");
                }
            } else {
                logger.info("前一天无未签退记录");
            }
        } catch (Exception e) {
            logger.error("处理前一天考勤记录失败", e);
        }
    }
}
