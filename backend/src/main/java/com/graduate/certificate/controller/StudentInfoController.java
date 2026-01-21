package com.graduate.certificate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.dto.student.StudentInfoRequest;
import com.graduate.certificate.dto.student.StudentInfoResponse;
import com.graduate.certificate.service.StudentInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 学生信息控制器
 */
@Slf4j
@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentInfoController {

    private final StudentInfoService studentInfoService;

    /**
     * 更新学生信息（仅当前登录的学生可更新自己的信息）
     */
    @PutMapping
    public Result<StudentInfoResponse> updateStudent(@RequestBody StudentInfoRequest request) {
        log.info("更新学生信息请求: {}", request);
        StudentInfoResponse response = studentInfoService.updateStudent(request);
        return Result.success("更新学生信息成功", response);
    }

    /**
     * 获取当前登录学生的信息
     */
    @GetMapping("/current")
    public Result<StudentInfoResponse> getCurrentStudent() {
        log.info("获取当前学生信息");
        // 从上下文获取当前用户ID
        String userId = com.graduate.certificate.common.context.UserContextHolder.getUserId();
        StudentInfoResponse response = studentInfoService.getStudentByUserId(userId);
        return Result.success(response);
    }

    /**
     * 删除学生信息
     */
    @DeleteMapping("/{pkStudent}")
    public Result<Void> deleteStudent(@PathVariable String pkStudent) {
        log.info("删除学生信息请求: {}", pkStudent);
        studentInfoService.deleteStudent(pkStudent);
        return Result.success("删除学生信息成功");
    }

    /**
     * 根据主键查询学生信息
     */
    @GetMapping("/{pkStudent}")
    public Result<StudentInfoResponse> getStudentById(@PathVariable String pkStudent) {
        log.info("查询学生信息请求: {}", pkStudent);
        StudentInfoResponse response = studentInfoService.getStudentById(pkStudent);
        return Result.success(response);
    }

    /**
     * 根据用户ID查询学生信息
     */
    @GetMapping("/user/{pkUser}")
    public Result<StudentInfoResponse> getStudentByUserId(@PathVariable String pkUser) {
        log.info("根据用户ID查询学生信息: {}", pkUser);
        StudentInfoResponse response = studentInfoService.getStudentByUserId(pkUser);
        return Result.success(response);
    }

    /**
     * 根据学号查询学生信息
     */
    @GetMapping("/studentNo/{studentNo}")
    public Result<StudentInfoResponse> getStudentByStudentNo(@PathVariable String studentNo) {
        log.info("根据学号查询学生信息: {}", studentNo);
        StudentInfoResponse response = studentInfoService.getStudentByStudentNo(studentNo);
        return Result.success(response);
    }

    /**
     * 分页查询学生信息
     */
    @GetMapping("/page")
    public Result<IPage<StudentInfoResponse>> pageStudents(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Integer graduationStatus) {
        log.info("分页查询学生信息: current={}, size={}, college={}, major={}, graduationStatus={}", 
                current, size, college, major, graduationStatus);
        IPage<StudentInfoResponse> page = studentInfoService.pageStudents(current, size, college, major, graduationStatus);
        return Result.success(page);
    }
}
