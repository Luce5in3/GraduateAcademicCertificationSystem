package com.graduate.certificate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.dto.application.ApplicationResponse;
import com.graduate.certificate.dto.application.StatisticsResponse;
import com.graduate.certificate.entity.CertificateTemplate;
import com.graduate.certificate.entity.CollegeInfo;
import com.graduate.certificate.entity.DepartmentInfo;
import com.graduate.certificate.service.CertificateApplicationService;
import com.graduate.certificate.service.CertificateTemplateService;
import com.graduate.certificate.service.CollegeInfoService;
import com.graduate.certificate.service.DepartmentInfoService;
import com.graduate.certificate.service.StudentInfoService;
import com.graduate.certificate.service.TeacherInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CertificateTemplateService templateService;
    private final StudentInfoService studentInfoService;
    private final TeacherInfoService teacherInfoService;
    private final CertificateApplicationService applicationService;
    private final CollegeInfoService collegeInfoService;
    private final DepartmentInfoService departmentInfoService;

    // --- 证明模板管理 ---
    @GetMapping("/templates")
    public Result<IPage<CertificateTemplate>> listTemplates(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(templateService.pageTemplates(current, size));
    }

    @PostMapping("/templates")
    public Result<Void> createTemplate(@RequestBody CertificateTemplate template) {
        templateService.saveTemplate(template);
        return Result.success("创建模板成功");
    }

    @PutMapping("/templates")
    public Result<Void> updateTemplate(@RequestBody CertificateTemplate template) {
        templateService.updateTemplate(template);
        return Result.success("修改模板成功");
    }

    @DeleteMapping("/templates/{pkCt}")
    public Result<Void> deleteTemplate(@PathVariable String pkCt) {
        templateService.deleteTemplate(pkCt);
        return Result.success("删除模板成功");
    }

    // --- 学生管理 ---
    @GetMapping("/students")
    public Result<IPage<?>> listStudents(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String major) {
        return Result.success(studentInfoService.pageStudents(current, size, college, major, null));
    }

    // --- 教师管理 ---
    @GetMapping("/teachers")
    public Result<IPage<?>> listTeachers(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Integer approvalLevel) {
        return Result.success(teacherInfoService.pageTeachers(current, size, college, department, approvalLevel));
    }

    // --- 统计数据 ---
    @GetMapping("/statistics")
    public Result<StatisticsResponse> getStatistics() {
        return Result.success(applicationService.getAdminStatistics());
    }

    // --- 全站申请记录 ---
    @GetMapping("/applications")
    public Result<IPage<ApplicationResponse>> listAllApplications(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        return Result.success(applicationService.listAllApplications(current, size, status));
    }

    // --- 学院管理 ---
    @GetMapping("/colleges")
    public Result<IPage<CollegeInfo>> listColleges(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(collegeInfoService.pageColleges(current, size));
    }

    @GetMapping("/colleges/active")
    public Result<List<CollegeInfo>> listActiveColleges() {
        return Result.success(collegeInfoService.listAllActive());
    }

    @PostMapping("/colleges")
    public Result<Void> createCollege(@RequestBody CollegeInfo college) {
        collegeInfoService.create(college);
        return Result.success("创建学院成功");
    }

    @PutMapping("/colleges")
    public Result<Void> updateCollege(@RequestBody CollegeInfo college) {
        collegeInfoService.update(college);
        return Result.success("更新学院成功");
    }

    @DeleteMapping("/colleges/{pkCollege}")
    public Result<Void> deleteCollege(@PathVariable String pkCollege) {
        collegeInfoService.delete(pkCollege);
        return Result.success("删除学院成功");
    }

    // --- 部门管理 ---
    @GetMapping("/departments")
    public Result<IPage<DepartmentInfo>> listDepartments(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String pkCollege) {
        return Result.success(departmentInfoService.pageDepartments(current, size, pkCollege));
    }

    @GetMapping("/departments/college/{pkCollege}")
    public Result<List<DepartmentInfo>> listDepartmentsByCollege(@PathVariable String pkCollege) {
        return Result.success(departmentInfoService.listByCollege(pkCollege));
    }

    @PostMapping("/departments")
    public Result<Void> createDepartment(@RequestBody DepartmentInfo department) {
        departmentInfoService.create(department);
        return Result.success("创建部门成功");
    }

    @PutMapping("/departments")
    public Result<Void> updateDepartment(@RequestBody DepartmentInfo department) {
        departmentInfoService.update(department);
        return Result.success("更新部门成功");
    }

    @DeleteMapping("/departments/{pkDepartment}")
    public Result<Void> deleteDepartment(@PathVariable String pkDepartment) {
        departmentInfoService.delete(pkDepartment);
        return Result.success("删除部门成功");
    }
}
