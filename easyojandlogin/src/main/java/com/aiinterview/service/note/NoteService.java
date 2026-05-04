package com.aiinterview.service.note;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiinterview.model.dto.note.NoteAddRequest;
import com.aiinterview.model.dto.note.NoteEditRequest;
import com.aiinterview.model.dto.note.NoteQueryRequest;
import com.aiinterview.model.dto.note.NoteVO;
import com.aiinterview.model.entity.note.Note;

import java.util.List;
import java.util.Map;

/**
 * 笔记服务接口
 */
public interface NoteService extends IService<Note> {
    
    /**
     * 创建笔记
     */
    Long addNote(NoteAddRequest noteAddRequest);

    /**
     * 编辑笔记
     */
    Boolean editNote(NoteEditRequest noteEditRequest);

    /**
     * 删除笔记
     */
    Boolean deleteNote(Long noteId);

    /**
     * 根据ID获取笔记详情
     */
    NoteVO getNoteById(Long noteId);

    /**
     * 分页查询笔记
     */
    IPage<NoteVO> listNotesByPage(NoteQueryRequest noteQueryRequest);

    /**
     * 获取分类统计
     */
    List<Map<String, Object>> getCategoryStats();

    /**
     * 获取所有标签
     */
    List<String> getAllTags();

    /**
     * AI优化笔记内容
     */
    Map<String, Object> optimizeNoteWithAI(String content, String title, String category);

    /**
     * AI聊天对话
     */
    Map<String, Object> chatWithAI(String message, String context);

    /**
     * 实体转VO
     */
    NoteVO getNoteVO(Note note);

    /**
     * 根据ID更新笔记内容
     */
    boolean updateNoteContent(Long noteId, String content);
}
