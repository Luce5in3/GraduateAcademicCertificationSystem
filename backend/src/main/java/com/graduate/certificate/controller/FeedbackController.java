package com.graduate.certificate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.graduate.certificate.common.result.Result;
import com.graduate.certificate.entity.ApplicationFeedback;
import com.graduate.certificate.service.ApplicationFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 申请反馈控制器
 */
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final ApplicationFeedbackService feedbackService;

    /**
     * 我的反馈列表
     */
    @GetMapping("/my")
    public Result<IPage<ApplicationFeedback>> myList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(feedbackService.getMyFeedbacks(current, size));
    }

    /**
     * 按申请查询反馈
     */
    @GetMapping("/application/{pkCa}")
    public Result<IPage<ApplicationFeedback>> listByApplication(
            @PathVariable String pkCa,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(feedbackService.getByApplication(pkCa, current, size));
    }

    /**
     * 提交反馈
     */
    @PostMapping
    public Result<Void> create(@RequestBody ApplicationFeedback feedback) {
        feedbackService.create(feedback);
        return Result.success("反馈提交成功");
    }

    /**
     * 管理员：获取待处理反馈
     */
    @GetMapping("/pending")
    public Result<IPage<ApplicationFeedback>> pendingList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(feedbackService.getPendingFeedbacks(current, size));
    }

    /**
     * 管理员：回复反馈
     */
    @PutMapping("/{pkFeedback}/reply")
    public Result<Void> reply(@PathVariable String pkFeedback, @RequestBody String replyContent) {
        feedbackService.reply(pkFeedback, replyContent);
        return Result.success("回复成功");
    }

    /**
     * 管理员/用户：关闭反馈
     */
    @PutMapping("/{pkFeedback}/close")
    public Result<Void> close(@PathVariable String pkFeedback) {
        feedbackService.close(pkFeedback);
        return Result.success("已关闭");
    }
}
