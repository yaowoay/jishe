package com.aiinterview.controller.note;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiinterview.model.dto.api.ApiResponse;
import com.aiinterview.model.dto.note.NoteAddRequest;
import com.aiinterview.model.dto.note.NoteEditRequest;
import com.aiinterview.model.dto.note.NoteQueryRequest;
import com.aiinterview.model.dto.note.NoteVO;
import com.aiinterview.service.note.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 笔记控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/note")
public class NoteController {

    @Resource
    private NoteService noteService;

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public ApiResponse<String> test() {
        return ApiResponse.success("Note service is working!");
    }

    /**
     * 创建笔记
     */
    @PostMapping("/add")
    public ApiResponse<Long> addNote(@Valid @RequestBody NoteAddRequest noteAddRequest) {
        try {
            Long noteId = noteService.addNote(noteAddRequest);
            return ApiResponse.success(noteId);
        } catch (Exception e) {
            log.error("创建笔记失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 编辑笔记
     */
    @PostMapping("/edit")
    public ApiResponse<Boolean> editNote(@Valid @RequestBody NoteEditRequest noteEditRequest) {
        try {
            Boolean result = noteService.editNote(noteEditRequest);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("编辑笔记失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除笔记
     */
    @PostMapping("/delete")
    public ApiResponse<Boolean> deleteNote(@RequestParam Long id) {
        try {
            Boolean result = noteService.deleteNote(id);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("删除笔记失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 根据ID获取笔记详情
     */
    @GetMapping("/get")
    public ApiResponse<NoteVO> getNoteById(@RequestParam Long id) {
        try {
            NoteVO noteVO = noteService.getNoteById(id);
            return ApiResponse.success(noteVO);
        } catch (Exception e) {
            log.error("获取笔记详情失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 分页查询笔记列表
     */
    @PostMapping("/list")
    public ApiResponse<IPage<NoteVO>> listNotes(@RequestBody NoteQueryRequest noteQueryRequest) {
        try {
            IPage<NoteVO> notePage = noteService.listNotesByPage(noteQueryRequest);
            return ApiResponse.success(notePage);
        } catch (Exception e) {
            log.error("查询笔记列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取分类统计
     */
    @GetMapping("/category/stats")
    public ApiResponse<List<Map<String, Object>>> getCategoryStats() {
        try {
            List<Map<String, Object>> stats = noteService.getCategoryStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            log.error("获取分类统计失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取所有标签
     */
    @GetMapping("/tags")
    public ApiResponse<List<String>> getAllTags() {
        try {
            List<String> tags = noteService.getAllTags();
            return ApiResponse.success(tags);
        } catch (Exception e) {
            log.error("获取标签列表失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * AI优化笔记
     */
    @PostMapping("/ai/optimize")
    public ApiResponse<Map<String, Object>> optimizeNote(@RequestBody Map<String, String> request) {
        try {
            String content = request.get("content");
            String title = request.get("title");
            String category = request.get("category");
            
            Map<String, Object> result = noteService.optimizeNoteWithAI(content, title, category);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("AI优化失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * AI聊天
     */
    @PostMapping("/ai/chat")
    public ApiResponse<Map<String, Object>> chatWithAI(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            String context = request.get("context");

            Map<String, Object> result = noteService.chatWithAI(message, context);
            return ApiResponse.success(result);
        } catch (Exception e) {
            log.error("AI聊天失败", e);
            return ApiResponse.error(e.getMessage());
        }
    }
}
