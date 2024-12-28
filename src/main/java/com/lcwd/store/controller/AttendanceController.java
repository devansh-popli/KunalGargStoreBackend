package com.lcwd.store.controller;

// AttendanceController.java
import com.lcwd.store.dtos.ApiResponseMessage;
import com.lcwd.store.dtos.AttendanceDto;
import com.lcwd.store.dtos.PageableResponse;
import com.lcwd.store.entities.Attendance;
import com.lcwd.store.services.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
    public PageableResponse<AttendanceDto> getAllAttendances(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,

                                                             @RequestParam(value = "sortBy", defaultValue = "employeeName", required = false)
                                                  String sortBy,
                                                             @RequestParam(value = "sortDir", defaultValue = "asc", required = false)
                                                  String sortDir, @RequestParam int month,@RequestParam int year, @RequestParam String empCode) {
        return attendanceService.getAllAttendances(month,year,empCode,pageNumber, pageSize, sortBy, sortDir);
    }

    @PostMapping
    public ResponseEntity<AttendanceDto> saveAttendance(@RequestBody AttendanceDto attendance) {
        return ResponseEntity.ok(attendanceService.saveAttendance(attendance));
    }
    @GetMapping("/findByEmpCodeAndDate")
    public ResponseEntity<AttendanceDto> getAttendancesByEmpCodeAndDate(
            @RequestParam String empCode,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date date) {
            Date sqlDate = new Date(date.getTime());
        return ResponseEntity.ok(attendanceService.getAttendancesByEmpCodeAndDate(empCode, sqlDate));
    }
    @GetMapping("/findByToday")
    public ResponseEntity<PageableResponse<AttendanceDto>> getAttendancesByDate(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                              @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,

                                                              @RequestParam(value = "sortBy", defaultValue = "employeeName", required = false)
                                                                  String sortBy,
                                                              @RequestParam(value = "sortDir", defaultValue = "asc", required = false)
                                                                  String sortDir,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date date) {
        Date sqlDate = new Date(date.getTime());
        return ResponseEntity.ok(attendanceService.getAttendancesByDate(sqlDate,pageNumber, pageSize, sortBy, sortDir));
    }
    @GetMapping("/findByMonth")
    public ResponseEntity<PageableResponse<AttendanceDto>> getAttendancesByMonth(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
                                                                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,

                                                                                @RequestParam(value = "sortBy", defaultValue = "employeeName", required = false)
                                                                                String sortBy,
                                                                                @RequestParam(value = "sortDir", defaultValue = "asc", required = false)
                                                                                String sortDir,
                                                                                @RequestParam int month,
                                                                                 @RequestParam int year) {
//        Date sqlDate = new Date(date.getTime());
        return ResponseEntity.ok(attendanceService.getAttendancesByMonthAndYear(month,year,pageNumber, pageSize, sortBy, sortDir));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{attendanceId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable("attendanceId") Long attendanceId) {
        attendanceService.deleteAttendance(attendanceId);
        ApiResponseMessage apiResponseMessage = ApiResponseMessage.builder().message("Attendance Deleted Successfully")
                .success(true)
                .status(HttpStatus.OK).build();
        return new ResponseEntity<>(apiResponseMessage, HttpStatus.OK);
    }
}
