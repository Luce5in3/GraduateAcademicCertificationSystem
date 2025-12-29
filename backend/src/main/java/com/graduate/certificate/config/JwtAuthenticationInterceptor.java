package com.graduate.certificate.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graduate.certificate.common.constant.UserTypeConstant;
import com.graduate.certificate.common.context.UserContext;
import com.graduate.certificate.common.context.UserContextHolder;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.common.result.ResultCode;
import com.graduate.certificate.entity.StudentInfo;
import com.graduate.certificate.entity.TeacherInfo;
import com.graduate.certificate.mapper.StudentInfoMapper;
import com.graduate.certificate.mapper.TeacherInfoMapper;
import com.graduate.certificate.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT认证拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final StudentInfoMapper studentInfoMapper;
    private final TeacherInfoMapper teacherInfoMapper;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 从请求头获取Token
        String token = getTokenFromRequest(request);
        
        if (!StringUtils.hasText(token)) {
            log.warn("Token缺失，请求路径: {}", request.getRequestURI());
            sendErrorResponse(response, ResultCode.TOKEN_MISSING);
            return false;
        }

        // 验证Token
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token无效或已过期，请求路径: {}", request.getRequestURI());
            sendErrorResponse(response, ResultCode.TOKEN_INVALID);
            return false;
        }

        try {
            // 从Token中获取用户信息
            String userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            Integer userType = jwtUtil.getUserTypeFromToken(token);

            // 构建用户上下文
            UserContext userContext = new UserContext();
            userContext.setUserId(userId);
            userContext.setUsername(username);
            userContext.setUserType(userType);

            // 根据用户类型查询对应的学生/教师主键
            if (UserTypeConstant.STUDENT == userType) {
                LambdaQueryWrapper<StudentInfo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(StudentInfo::getPkUser, userId);
                StudentInfo studentInfo = studentInfoMapper.selectOne(wrapper);
                if (studentInfo != null) {
                    userContext.setStudentId(studentInfo.getPkStudent());
                }
            } else if (UserTypeConstant.TEACHER == userType) {
                LambdaQueryWrapper<TeacherInfo> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(TeacherInfo::getPkUser, userId);
                TeacherInfo teacherInfo = teacherInfoMapper.selectOne(wrapper);
                if (teacherInfo != null) {
                    userContext.setTeacherId(teacherInfo.getPkTeacher());
                }
            }

            // 将用户上下文设置到ThreadLocal
            UserContextHolder.setContext(userContext);
            
            log.debug("用户认证成功: userId={}, username={}, userType={}", userId, username, userType);
            return true;
            
        } catch (Exception e) {
            log.error("Token解析失败", e);
            sendErrorResponse(response, ResultCode.TOKEN_INVALID);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后清除上下文
        UserContextHolder.clear();
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, ResultCode resultCode) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.fail(resultCode);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
