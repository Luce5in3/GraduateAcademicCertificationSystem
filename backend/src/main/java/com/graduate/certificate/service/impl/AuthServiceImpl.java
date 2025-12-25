package com.graduate.certificate.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduate.certificate.common.constant.UserTypeConstant;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.common.result.ResultCode;
import com.graduate.certificate.dto.auth.LoginRequest;
import com.graduate.certificate.dto.auth.LoginResponse;
import com.graduate.certificate.dto.auth.RegisterRequest;
import com.graduate.certificate.entity.StudentInfo;
import com.graduate.certificate.entity.SysUser;
import com.graduate.certificate.entity.TeacherInfo;
import com.graduate.certificate.mapper.StudentInfoMapper;
import com.graduate.certificate.mapper.SysUserMapper;
import com.graduate.certificate.mapper.TeacherInfoMapper;
import com.graduate.certificate.service.AuthService;
import com.graduate.certificate.service.KeyGeneratorService;
import com.graduate.certificate.util.JwtUtil;
import com.graduate.certificate.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final StudentInfoMapper studentInfoMapper;
    private final TeacherInfoMapper teacherInfoMapper;
    private final JwtUtil jwtUtil;
    private final KeyGeneratorService keyGeneratorService;

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 查询用户
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = sysUserMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 验证密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 3. 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 4. 更新最后登录信息
        user.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(user);

        // 5. 生成Token
        String token = jwtUtil.generateToken(user.getPkUser(), user.getUsername(), user.getUserType());

        // 6. 构建响应
        return LoginResponse.builder()
                .token(token)
                .userId(user.getPkUser())
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .userTypeDesc(UserTypeConstant.getUserTypeDesc(user.getUserType()))
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        // 1. 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        // 2. 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, request.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 3. 验证用户类型
        if (request.getUserType() != UserTypeConstant.STUDENT 
                && request.getUserType() != UserTypeConstant.TEACHER) {
            throw new BusinessException("用户类型错误");
        }

        // 4. 创建用户账号
        SysUser user = new SysUser();
        user.setPkUser(keyGeneratorService.generateUserKey()); // 生成业务主键
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setUserType(request.getUserType());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        sysUserMapper.insert(user);

        // 5. 根据用户类型创建对应的信息表记录
        if (request.getUserType() == UserTypeConstant.STUDENT) {
            createStudentInfo(user);
        } else if (request.getUserType() == UserTypeConstant.TEACHER) {
            createTeacherInfo(user);
        }

        log.info("用户注册成功：username={}, userType={}", request.getUsername(), request.getUserType());
    }

    /**
     * 创建学生信息
     */
    private void createStudentInfo(SysUser user) {
        // 检查学号是否已存在
        LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentInfo::getStudentNo, user.getUsername());
        if (studentInfoMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.STUDENT_NO_DUPLICATE);
        }

        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setPkStudent(keyGeneratorService.generateStudentKey()); // 生成业务主键
        studentInfo.setPkUser(user.getPkUser());
        studentInfo.setStudentNo(user.getUsername());
        studentInfo.setName(user.getRealName());
        studentInfo.setGraduationStatus(0); // 默认在读
        studentInfo.setCreateTime(LocalDateTime.now());
        studentInfo.setUpdateTime(LocalDateTime.now());

        studentInfoMapper.insert(studentInfo);
    }

    /**
     * 创建教师信息
     */
    private void createTeacherInfo(SysUser user) {
        // 检查工号是否已存在
        LambdaQueryWrapper<TeacherInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeacherInfo::getTeacherNo, user.getUsername());
        if (teacherInfoMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.TEACHER_NO_DUPLICATE);
        }

        TeacherInfo teacherInfo = new TeacherInfo();
        teacherInfo.setPkTeacher(keyGeneratorService.generateTeacherKey()); // 生成业务主键
        teacherInfo.setPkUser(user.getPkUser());
        teacherInfo.setTeacherNo(user.getUsername());
        teacherInfo.setName(user.getRealName());
        teacherInfo.setApprovalLevel(1); // 默认初审级别
        teacherInfo.setCanApprove(1); // 默认有审批权限
        teacherInfo.setCreateTime(LocalDateTime.now());
        teacherInfo.setUpdateTime(LocalDateTime.now());

        teacherInfoMapper.insert(teacherInfo);
    }
}
