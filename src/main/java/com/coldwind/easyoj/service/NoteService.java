package com.coldwind.easyoj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.coldwind.easyoj.model.dto.request.note.NoteAddRequest;
import com.coldwind.easyoj.model.dto.request.note.NoteEditRequest;
import com.coldwind.easyoj.model.dto.request.note.NoteQueryRequest;
import com.coldwind.easyoj.model.entity.note.Note;
import com.coldwind.easyoj.model.vo.NoteVO;

import java.util.List;
import java.util.Map;

/**
 * 笔记服务接口
 * 提供笔记相关的业务逻辑方法
 */
public interface NoteService extends IService<Note> {
    
    /**
     * 创建笔记
     *
     * @param noteAddRequest 创建请求
     * @return 笔记ID
     */
    Long addNote(NoteAddRequest noteAddRequest);

    /**
     * 编辑笔记
     *
     * @param noteEditRequest 编辑请求
     * @return 是否成功
     */
    Boolean editNote(NoteEditRequest noteEditRequest);

    /**
     * 删除笔记
     *
     * @param noteId 笔记ID
     * @return 是否成功
     */
    Boolean deleteNote(Long noteId);

    /**
     * 根据ID获取笔记详情
     *
     * @param noteId 笔记ID
     * @return 笔记详情
     */
    NoteVO getNoteById(Long noteId);

    /**
     * 分页查询笔记
     *
     * @param noteQueryRequest 查询请求
     * @return 分页结果
     */
    IPage<NoteVO> listNotesByPage(NoteQueryRequest noteQueryRequest);

    /**
     * 获取分类统计
     *
     * @return 分类统计结果
     */
    List<Map<String, Object>> getCategoryStats();

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    List<String> getAllTags();

    /**
     * AI优化笔记内容
     *
     * @param content 原始内容
     * @param title 标题
     * @param category 分类
     * @return 优化结果
     */
    Map<String, Object> optimizeNoteWithAI(String content, String title, String category);

    /**
     * AI聊天对话
     *
     * @param message 用户消息
     * @param context 上下文（笔记内容）
     * @return 对话结果
     */
    Map<String, Object> chatWithAI(String message, String context);

    /**
     * 实体转VO
     *
     * @param note 笔记实体
     * @return 笔记VO
     */
    NoteVO getNoteVO(Note note);
}
