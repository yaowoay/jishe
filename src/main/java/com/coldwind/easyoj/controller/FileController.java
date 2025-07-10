package com.coldwind.easyoj.controller;

import cn.hutool.core.io.FileUtil;
import com.coldwind.easyoj.common.BaseResponse;
import com.coldwind.easyoj.common.ErrorCode;
import com.coldwind.easyoj.common.ResultUtils;
import com.coldwind.easyoj.constant.FileConstant;
import com.coldwind.easyoj.exception.BusinessException;
import com.coldwind.easyoj.manager.LocalFileManager;
import com.coldwind.easyoj.model.dto.file.UploadFileRequest;
import com.coldwind.easyoj.model.entity.User;
import com.coldwind.easyoj.model.enums.FileUploadBizEnum;
import com.coldwind.easyoj.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * 文件接口（本地存储版）
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private LocalFileManager localFileManager;

    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        // 1. 校验业务类型
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "业务类型不合法");
        }

        // 2. 校验文件合法性（大小、后缀）
        validFile(multipartFile, fileUploadBizEnum);

        // 3. 获取登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 4. 构造文件存储相对路径（业务类型/用户ID/随机文件名）
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = FileUtil.getSuffix(originalFilename);
        String newFilename = uuid + "." + ext;
        String relativePath = String.format("%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), newFilename);

        try {
            // 5. 调用本地存储上传
            String accessUrl = localFileManager.upload(relativePath, multipartFile);
            return ResultUtils.success(accessUrl);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败", e);
        }
    }

    /**
     * 上传简历文件
     */
    @PostMapping("/upload-resume")
    public BaseResponse<String> uploadResume(@RequestPart("file") MultipartFile multipartFile,
                                           HttpServletRequest request) {
        // 1. 校验简历文件合法性
        validResumeFile(multipartFile);

        // 2. 获取登录用户信息
        User loginUser = userService.getLoginUser(request);

        // 3. 构造简历文件存储路径
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = FileUtil.getSuffix(originalFilename);
        String newFilename = uuid + "." + ext;
        String relativePath = String.format("resume/%s/%s", loginUser.getId(), newFilename);

        try {
            // 4. 调用本地存储上传
            String accessUrl = localFileManager.upload(relativePath, multipartFile);
            return ResultUtils.success(accessUrl);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "简历文件上传失败", e);
        }
    }

    /**
     * 校验文件大小和后缀（示例逻辑，根据实际业务调整）
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum bizEnum) {
        // 文件大小限制
        if (bizEnum == FileUploadBizEnum.USER_AVATAR && multipartFile.getSize() > 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像文件大小不能超过1MB");
        }
        
        if (bizEnum == FileUploadBizEnum.RESUME_FILE && multipartFile.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简历文件大小不能超过10MB");
        }

        // 文件后缀限制
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        
        if (bizEnum == FileUploadBizEnum.USER_AVATAR) {
            // 头像文件仅允许图片格式
            if (!Arrays.asList("jpg", "jpeg", "png", "webp").contains(suffix.toLowerCase())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像文件格式不支持，请上传图片文件");
            }
        } else if (bizEnum == FileUploadBizEnum.RESUME_FILE) {
            // 简历文件支持文档格式
            if (!Arrays.asList("pdf", "doc", "docx", "txt", "rtf").contains(suffix.toLowerCase())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "简历文件格式不支持，请上传PDF、Word或文本文件");
            }
        }

        // 文件名不能为空
        if (multipartFile.getOriginalFilename() == null || multipartFile.getOriginalFilename().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名不能为空");
        }
    }

    /**
     * 校验简历文件
     */
    private void validResumeFile(MultipartFile multipartFile) {
        // 文件大小限制（简历文件限制10MB）
        if (multipartFile.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简历文件大小不能超过10MB");
        }

        // 文件后缀限制（支持常见的文档格式）
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (!Arrays.asList("pdf", "doc", "docx", "txt", "rtf").contains(suffix.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "简历文件格式不支持，请上传PDF、Word或文本文件");
        }

        // 文件名不能为空
        if (multipartFile.getOriginalFilename() == null || multipartFile.getOriginalFilename().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名不能为空");
        }
    }
}
