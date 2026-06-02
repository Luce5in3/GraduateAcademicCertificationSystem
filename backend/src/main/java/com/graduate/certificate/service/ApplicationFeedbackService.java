package com.graduate.certificate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.entity.ApplicationFeedback;

/**
 * 申请反馈服务接口
 */
public interface ApplicationFeedbackService {

    IPage<ApplicationFeedback> getMyFeedbacks(int current, int size);

    IPage<ApplicationFeedback> getByApplication(String pkCa, int current, int size);

    ApplicationFeedback getById(String pkFeedback);

    void create(ApplicationFeedback feedback);

    /**
     * 管理员回复反馈
     */
    void reply(String pkFeedback, String replyContent);

    void close(String pkFeedback);

    /**
     * 管理员：获取所有待处理反馈
     */
    IPage<ApplicationFeedback> getPendingFeedbacks(int current, int size);
}
