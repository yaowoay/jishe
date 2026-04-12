package com.aiinterview.service.note.impl;

import com.aiinterview.mapper.NoteMapper;
import com.aiinterview.model.dto.note.NoteAddRequest;
import com.aiinterview.model.dto.note.NoteEditRequest;
import com.aiinterview.model.dto.note.NoteQueryRequest;
import com.aiinterview.model.dto.note.NoteVO;
import com.aiinterview.model.entity.note.Note;
import com.aiinterview.service.note.NoteService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 * 笔记服务实现类
 */
@Slf4j
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {
    
    @Resource
    private NoteMapper noteMapper;
    
    @Override
    public Long addNote(NoteAddRequest noteAddRequest) {
        if (noteAddRequest == null) {
            throw new RuntimeException("参数不能为空");
        }

        Note note = new Note();
        BeanUtils.copyProperties(noteAddRequest, note);
        note.setUserId(1L); // 暂时使用固定用户ID
        note.setCreateTime(new Date());
        note.setUpdateTime(new Date());
        note.setIsDelete(0);

        boolean result = this.save(note);
        if (!result) {
            throw new RuntimeException("创建笔记失败");
        }

        return note.getId();
    }

    @Override
    public Boolean editNote(NoteEditRequest noteEditRequest) {
        if (noteEditRequest == null || noteEditRequest.getId() == null) {
            throw new RuntimeException("参数不能为空");
        }

        Note note = this.getById(noteEditRequest.getId());
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }

        BeanUtils.copyProperties(noteEditRequest, note);
        note.setUpdateTime(new Date());

        return this.updateById(note);
    }

    @Override
    public Boolean deleteNote(Long noteId) {
        if (noteId == null || noteId <= 0) {
            throw new RuntimeException("参数错误");
        }

        Note note = this.getById(noteId);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }

        note.setIsDelete(1);
        note.setUpdateTime(new Date());
        return this.updateById(note);
    }

    @Override
    public NoteVO getNoteById(Long noteId) {
        if (noteId == null || noteId <= 0) {
            throw new RuntimeException("参数错误");
        }

        Note note = this.getById(noteId);
        if (note == null || note.getIsDelete() == 1) {
            throw new RuntimeException("笔记不存在");
        }

        return getNoteVO(note);
    }

    @Override
    public IPage<NoteVO> listNotesByPage(NoteQueryRequest noteQueryRequest) {
        int current = noteQueryRequest.getCurrent();
        int pageSize = noteQueryRequest.getPageSize();
        
        Page<Note> page = new Page<>(current, pageSize);
        
        IPage<Note> notePage = noteMapper.selectNotesByPage(
            page,
            1L, // 暂时使用固定用户ID
            noteQueryRequest.getKeyword(),
            noteQueryRequest.getCategory(),
            noteQueryRequest.getTags(),
            noteQueryRequest.getSortField(),
            noteQueryRequest.getSortOrder()
        );
        
        Page<NoteVO> noteVOPage = new Page<>(current, pageSize, notePage.getTotal());
        List<NoteVO> noteVOList = notePage.getRecords().stream()
                .map(this::getNoteVO)
                .collect(Collectors.toList());
        noteVOPage.setRecords(noteVOList);
        
        return noteVOPage;
    }

    @Override
    public List<Map<String, Object>> getCategoryStats() {
        return noteMapper.countNotesByCategory(1L); // 暂时使用固定用户ID
    }

    @Override
    public List<String> getAllTags() {
        return noteMapper.selectAllTagsByUserId(1L); // 暂时使用固定用户ID
    }

    @Override
    public Map<String, Object> optimizeNoteWithAI(String content, String title, String category) {
        Map<String, Object> result = new HashMap<>();
        try {
            Thread.sleep(1000);

            String optimizedContent = "优化后的内容：\n\n" + content + "\n\n[AI优化建议：内容结构清晰，建议增加更多实例]";
            String optimizedTitle = title + " [已优化]";
            List<String> suggestedTags = Arrays.asList("Java", "Spring", "技术");
            String suggestedCategory = category != null ? category : "技术笔记";

            result.put("success", true);
            result.put("optimizedContent", optimizedContent);
            result.put("optimizedTitle", optimizedTitle);
            result.put("suggestedTags", suggestedTags);
            result.put("suggestedCategory", suggestedCategory);
            result.put("message", "AI优化完成");
        } catch (Exception e) {
            log.error("AI优化失败", e);
            result.put("success", false);
            result.put("message", "AI优化失败");
        }
        return result;
    }

    @Override
    public Map<String, Object> chatWithAI(String message, String context) {
        Map<String, Object> result = new HashMap<>();
        try {
            String response = "这是AI的回复：" + message;
            result.put("success", true);
            result.put("response", response);
            result.put("message", "对话成功");
        } catch (Exception e) {
            log.error("AI对话失败", e);
            result.put("success", false);
            result.put("message", "AI对话失败");
        }
        return result;
    }

    @Override
    public NoteVO getNoteVO(Note note) {
        if (note == null) {
            return null;
        }

        NoteVO noteVO = new NoteVO();
        BeanUtils.copyProperties(note, noteVO);

        if (note.getTags() != null && !note.getTags().trim().isEmpty()) {
            List<String> tagList = Arrays.stream(note.getTags().split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toList());
            noteVO.setTagList(tagList);
        }

        return noteVO;
    }

    @Override
    public boolean updateNoteContent(Long noteId, String content) {
        if (noteId == null || noteId <= 0) {
            throw new RuntimeException("参数错误");
        }

        Note note = this.getById(noteId);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }

        note.setContent(content);
        note.setUpdateTime(new Date());
        return this.updateById(note);
    }
}
