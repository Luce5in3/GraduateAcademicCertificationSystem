package com.graduate.certificate.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.graduate.certificate.common.constant.ApplicationStatusConstant;
import com.graduate.certificate.common.exception.BusinessException;
import com.graduate.certificate.entity.CertificateApplication;
import com.graduate.certificate.entity.CertificateTemplate;
import com.graduate.certificate.entity.StudentInfo;
import com.graduate.certificate.mapper.CertificateApplicationMapper;
import com.graduate.certificate.mapper.CertificateTemplateMapper;
import com.graduate.certificate.mapper.StudentInfoMapper;
import com.graduate.certificate.service.CertificateGenerateService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 证书PDF生成服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CertificateGenerateServiceImpl implements CertificateGenerateService {

    private final CertificateApplicationMapper applicationMapper;
    private final CertificateTemplateMapper templateMapper;
    private final StudentInfoMapper studentInfoMapper;

    @Override
    public ByteArrayOutputStream generateCertificatePdf(String pkCa) {
        // 1. 查询申请
        CertificateApplication application = applicationMapper.selectById(pkCa);
        if (application == null) {
            throw new BusinessException(404, "申请不存在");
        }

        // 2. 验证申请状态为已通过
        if (!ApplicationStatusConstant.APPROVED.equals(application.getStatus())) {
            throw new BusinessException(400, "只有审批通过的申请才能生成证书");
        }

        // 3. 查询模板
        CertificateTemplate template = templateMapper.selectById(application.getPkCt());
        if (template == null) {
            throw new BusinessException(404, "证书模板不存在");
        }

        // 4. 查询学生信息
        StudentInfo student = studentInfoMapper.selectById(application.getPkStudent());
        if (student == null) {
            throw new BusinessException(404, "学生信息不存在");
        }

        // 5. 替换模板变量
        String content = replaceTemplateVariables(template.getTemplateContent(), student, application);

        // 6. 生成PDF
        return buildPdf(template.getTemplateName(), content, student, application);
    }

    /**
     * 替换模板中的变量
     */
    private String replaceTemplateVariables(String templateContent, StudentInfo student, CertificateApplication application) {
        if (!StringUtils.hasText(templateContent)) {
            templateContent = getDefaultTemplateContent();
        }

        String content = templateContent;
        content = content.replace("${studentName}", nullSafe(student.getName()));
        content = content.replace("${studentNo}", nullSafe(student.getStudentNo()));
        content = content.replace("${gender}", student.getGender() != null ? (student.getGender() == 1 ? "男" : "女") : "");
        content = content.replace("${idCard}", nullSafe(student.getIdCard()));
        content = content.replace("${college}", nullSafe(student.getCollege()));
        content = content.replace("${major}", nullSafe(student.getMajor()));
        content = content.replace("${className}", nullSafe(student.getClassName()));
        content = content.replace("${grade}", nullSafe(student.getGrade()));
        content = content.replace("${educationLevel}", nullSafe(student.getEducationLevel()));
        content = content.replace("${studyType}", nullSafe(student.getStudyType()));
        content = content.replace("${enrollmentDate}", student.getEnrollmentDate() != null ? student.getEnrollmentDate().toString() : "");
        content = content.replace("${graduationDate}", student.getGraduationDate() != null ? student.getGraduationDate().toString() : "");
        content = content.replace("${certificateNo}", nullSafe(application.getCertificateNo()));
        content = content.replace("${issueDate}", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日")));
        content = content.replace("${applicationNo}", nullSafe(application.getApplicationNo()));

        return content;
    }

    /**
     * 默认模板内容
     */
    private String getDefaultTemplateContent() {
        return """
                兹证明 ${studentName}，学号 ${studentNo}，性别 ${gender}，
                于 ${enrollmentDate} 至 ${graduationDate} 在我校 ${college} ${major} 专业学习，
                学历层次为 ${educationLevel}，学习形式为 ${studyType}。
                
                特此证明。
                
                证书编号：${certificateNo}
                开具日期：${issueDate}
                """;
    }

    /**
     * 构建PDF文档
     */
    private ByteArrayOutputStream buildPdf(String title, String content, StudentInfo student, CertificateApplication application) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document document = new Document(PageSize.A4, 72, 72, 72, 72);
            PdfWriter.getInstance(document, baos);
            document.open();

            // 使用系统中文字体
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(bfChinese, 22, Font.BOLD);
            Font contentFont = new Font(bfChinese, 14, Font.NORMAL);
            Font footerFont = new Font(bfChinese, 10, Font.ITALIC);

            // 标题
            Paragraph titlePara = new Paragraph(title, titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            titlePara.setSpacingAfter(30f);
            document.add(titlePara);

            // 正文内容
            String[] lines = content.split("\n");
            for (String line : lines) {
                Paragraph para = new Paragraph(line.trim(), contentFont);
                para.setAlignment(Element.ALIGN_LEFT);
                para.setSpacingAfter(10f);
                para.setFirstLineIndent(28f);
                document.add(para);
            }

            // 底部信息
            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph(
                    "生成时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    footerFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            log.error("生成PDF失败: pkCa={}", application.getPkCa(), e);
            throw new BusinessException(500, "证书PDF生成失败：" + e.getMessage());
        }

        return baos;
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
