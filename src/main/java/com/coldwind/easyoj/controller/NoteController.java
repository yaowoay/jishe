package com.coldwind.easyoj.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.coldwind.easyoj.common.BaseResponse;
import com.coldwind.easyoj.common.ErrorCode;
import com.coldwind.easyoj.common.ResultUtils;
import com.coldwind.easyoj.exception.BusinessException;
import com.coldwind.easyoj.model.dto.request.note.NoteAddRequest;
import com.coldwind.easyoj.model.dto.request.note.NoteEditRequest;
import com.coldwind.easyoj.model.dto.request.note.NoteQueryRequest;
import com.coldwind.easyoj.model.vo.NoteVO;
import com.coldwind.easyoj.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 笔记控制器
 * 提供笔记相关的API接口
 */
@Slf4j
@RestController
@RequestMapping("/note")
public class NoteController {

    @Resource
    private NoteService noteService;

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public BaseResponse<String> test() {
        return ResultUtils.success("Note service is working!");
    }

    /**
     * 创建笔记
     */
    @PostMapping("/add")
    public BaseResponse<Long> addNote(@Valid @RequestBody NoteAddRequest noteAddRequest) {
        Long noteId = noteService.addNote(noteAddRequest);
        return ResultUtils.success(noteId);
    }

    /**
     * 编辑笔记
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editNote(@Valid @RequestBody NoteEditRequest noteEditRequest) {
        Boolean result = noteService.editNote(noteEditRequest);
        return ResultUtils.success(result);
    }

    /**
     * 删除笔记
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteNote(@RequestParam Long noteId) {
        if (noteId == null || noteId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记ID无效");
        }

        Boolean result = noteService.deleteNote(noteId);
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取笔记详情
     */
    @GetMapping("/get")
    public BaseResponse<NoteVO> getNoteById(@RequestParam Long noteId) {
        if (noteId == null || noteId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记ID无效");
        }

        NoteVO noteVO = noteService.getNoteById(noteId);
        return ResultUtils.success(noteVO);
    }

    /**
     * 分页查询笔记列表
     */
    @PostMapping("/list/page")
    public BaseResponse<IPage<NoteVO>> listNotesByPage(@RequestBody NoteQueryRequest noteQueryRequest) {
        if (noteQueryRequest == null) {
            noteQueryRequest = new NoteQueryRequest();
        }

        IPage<NoteVO> noteVOIPage = noteService.listNotesByPage(noteQueryRequest);
        return ResultUtils.success(noteVOIPage);
    }

    /**
     * 获取分类统计
     */
    @GetMapping("/category/stats")
    public BaseResponse<List<Map<String, Object>>> getCategoryStats() {
        List<Map<String, Object>> stats = noteService.getCategoryStats();
        return ResultUtils.success(stats);
    }

    /**
     * 获取所有标签
     */
    @GetMapping("/tags")
    public BaseResponse<List<String>> getAllTags() {
        List<String> tags = noteService.getAllTags();
        return ResultUtils.success(tags);
    }

    /**
     * AI优化笔记内容
     */
    @PostMapping("/ai/optimize")
    public BaseResponse<Map<String, Object>> optimizeNoteWithAI(@RequestBody Map<String, Object> request) {
        String content = (String) request.get("content");
        String title = (String) request.get("title");
        String category = (String) request.get("category");

        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记内容不能为空");
        }

        Map<String, Object> result = noteService.optimizeNoteWithAI(content, title, category);
        return ResultUtils.success(result);
    }

    /**
     * AI聊天对话
     */
    @PostMapping("/ai/chat")
    public BaseResponse<Map<String, Object>> chatWithAI(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String context = (String) request.get("context"); // 笔记内容作为上下文

        if (message == null || message.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        }

        Map<String, Object> result = noteService.chatWithAI(message, context);
        return ResultUtils.success(result);
    }
}
