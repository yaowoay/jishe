package com.coldwind.easyoj.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coldwind.easyoj.common.ErrorCode;
import com.coldwind.easyoj.exception.BusinessException;
import com.coldwind.easyoj.mapper.NoteMapper;
import com.coldwind.easyoj.model.dto.request.note.NoteAddRequest;
import com.coldwind.easyoj.model.dto.request.note.NoteEditRequest;
import com.coldwind.easyoj.model.dto.request.note.NoteQueryRequest;
import com.coldwind.easyoj.model.entity.note.Note;
import com.coldwind.easyoj.model.vo.NoteVO;
import com.coldwind.easyoj.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 笔记服务实现类
 * 实现笔记相关的业务逻辑
 */
@Slf4j
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {
    
    @Resource
    private NoteMapper noteMapper;
    
    @Override
    public Long addNote(NoteAddRequest noteAddRequest) {
        // 参数校验
        if (noteAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        // 创建笔记实体
        Note note = new Note();
        BeanUtils.copyProperties(noteAddRequest, note);
        note.setUserId(1L); // 暂时使用固定用户ID
        note.setCreateTime(new Date());
        note.setUpdateTime(new Date());

        // 保存到数据库
        boolean result = this.save(note);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建笔记失败");
        }

        return note.getId();
    }
    
    @Override
    public Boolean editNote(NoteEditRequest noteEditRequest) {
        // 参数校验
        if (noteEditRequest == null || noteEditRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        // 查询原笔记
        Note oldNote = this.getById(noteEditRequest.getId());
        if (oldNote == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "笔记不存在");
        }

        // 更新笔记
        Note note = new Note();
        BeanUtils.copyProperties(noteEditRequest, note);
        note.setUpdateTime(new Date());

        boolean result = this.updateById(note);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "编辑笔记失败");
        }

        return true;
    }
    
    @Override
    public Boolean deleteNote(Long noteId) {
        // 参数校验
        if (noteId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记ID不能为空");
        }

        // 查询笔记
        Note note = this.getById(noteId);
        if (note == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "笔记不存在");
        }

        // 逻辑删除
        boolean result = this.removeById(noteId);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除笔记失败");
        }

        return true;
    }
    
    @Override
    public NoteVO getNoteById(Long noteId) {
        // 参数校验
        if (noteId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "笔记ID不能为空");
        }

        // 查询笔记
        Note note = this.getById(noteId);
        if (note == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "笔记不存在");
        }

        return this.getNoteVO(note);
    }
    
    @Override
    public IPage<NoteVO> listNotesByPage(NoteQueryRequest noteQueryRequest) {
        // 参数校验
        if (noteQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }

        // 创建分页对象
        Page<Note> page = new Page<>(noteQueryRequest.getCurrent(), noteQueryRequest.getPageSize());

        // 查询数据 - 查询所有笔记，不限制用户，支持排序
        IPage<Note> noteIPage = noteMapper.selectNotesByPage(
                page,
                null, // 不限制用户ID
                noteQueryRequest.getKeyword(),
                noteQueryRequest.getCategory(),
                noteQueryRequest.getTags(),
                noteQueryRequest.getSortField(),
                noteQueryRequest.getSortOrder()
        );

        // 转换为VO
        IPage<NoteVO> noteVOIPage = new Page<>(noteIPage.getCurrent(), noteIPage.getSize(), noteIPage.getTotal());
        List<NoteVO> noteVOList = noteIPage.getRecords().stream()
                .map(this::getNoteVO)
                .collect(Collectors.toList());
        noteVOIPage.setRecords(noteVOList);

        return noteVOIPage;
    }
    
    @Override
    public List<Map<String, Object>> getCategoryStats() {
        return noteMapper.countNotesByCategory(null); // 查询所有用户的分类统计
    }

    @Override
    public List<String> getAllTags() {
        return noteMapper.selectAllTagsByUserId(null); // 查询所有用户的标签
    }
    
    @Override
    public NoteVO getNoteVO(Note note) {
        if (note == null) {
            return null;
        }
        
        NoteVO noteVO = new NoteVO();
        BeanUtils.copyProperties(note, noteVO);
        
        // 处理标签列表
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
    public Map<String, Object> optimizeNoteWithAI(String content, String title, String category) {
        // 模拟AI优化功能
        Map<String, Object> result = new HashMap<>();

        try {
            // 模拟AI处理延迟
            Thread.sleep(1000);

            // 模拟优化后的内容
            String optimizedContent = optimizeContentWithMockAI(content, title, category);
            String optimizedTitle = optimizeTitleWithMockAI(title, content);
            List<String> suggestedTags = generateSuggestedTags(content, category);
            String suggestedCategory = suggestCategory(content, title);

            result.put("success", true);
            result.put("optimizedContent", optimizedContent);
            result.put("optimizedTitle", optimizedTitle);
            result.put("suggestedTags", suggestedTags);
            result.put("suggestedCategory", suggestedCategory);
            result.put("message", "AI优化完成");

        } catch (Exception e) {
            log.error("AI优化失败", e);
            result.put("success", false);
            result.put("message", "AI优化失败，请稍后重试");
        }

        return result;
    }

    @Override
    public Map<String, Object> chatWithAI(String message, String context) {
        // 模拟AI聊天功能
        Map<String, Object> result = new HashMap<>();

        try {
            // 模拟AI处理延迟
            Thread.sleep(800);

            String aiResponse = generateAIResponse(message, context);

            result.put("success", true);
            result.put("response", aiResponse);
            result.put("timestamp", new Date());

        } catch (Exception e) {
            log.error("AI聊天失败", e);
            result.put("success", false);
            result.put("response", "抱歉，AI助手暂时无法回应，请稍后重试。");
        }

        return result;
    }

    /**
     * 模拟AI内容优化
     */
    private String optimizeContentWithMockAI(String content, String title, String category) {
        if (content == null || content.trim().isEmpty()) {
            return content;
        }

        StringBuilder optimized = new StringBuilder();

        // 添加结构化标题
        if (title != null && !content.contains("# " + title)) {
            optimized.append("# ").append(title).append("\n\n");
        }

        // 优化内容结构
        String[] lines = content.split("\n");
        boolean inCodeBlock = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            // 检测代码块
            if (trimmedLine.startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                optimized.append(line).append("\n");
                continue;
            }

            if (!inCodeBlock) {
                // 优化标题格式
                if (trimmedLine.matches("^[0-9]+\\..+") || trimmedLine.matches("^[一二三四五六七八九十]+[、.].+")) {
                    optimized.append("## ").append(trimmedLine).append("\n");
                }
                // 优化列表格式
                else if (trimmedLine.startsWith("-") || trimmedLine.startsWith("*")) {
                    optimized.append(line).append("\n");
                }
                // 添加重点标记
                else if (trimmedLine.contains("重要") || trimmedLine.contains("注意") || trimmedLine.contains("关键")) {
                    optimized.append("**").append(trimmedLine).append("**\n");
                }
                else {
                    optimized.append(line).append("\n");
                }
            } else {
                optimized.append(line).append("\n");
            }
        }

        // 添加总结部分
        if (category != null && category.equals("技术笔记")) {
            optimized.append("\n## 总结\n");
            optimized.append("- 本文介绍了").append(title != null ? title : "相关技术").append("的核心概念\n");
            optimized.append("- 提供了实用的代码示例和最佳实践\n");
            optimized.append("- 建议在实际项目中灵活运用这些知识点\n");
        }

        return optimized.toString();
    }

    /**
     * 模拟AI标题优化
     */
    private String optimizeTitleWithMockAI(String title, String content) {
        if (title == null || title.trim().isEmpty()) {
            // 从内容中提取关键词生成标题
            if (content.contains("Vue")) return "Vue 开发实践指南";
            if (content.contains("Spring")) return "Spring Boot 核心技术解析";
            if (content.contains("MySQL")) return "MySQL 性能优化实战";
            if (content.contains("算法")) return "算法学习心得总结";
            return "技术学习笔记";
        }

        // 优化现有标题
        String optimized = title.trim();
        if (!optimized.contains("实践") && !optimized.contains("指南") && !optimized.contains("总结")) {
            if (content.contains("代码") || content.contains("示例")) {
                optimized += " - 实践指南";
            } else if (content.contains("面试") || content.contains("问题")) {
                optimized += " - 面试总结";
            } else {
                optimized += " - 学习笔记";
            }
        }

        return optimized;
    }

    /**
     * 生成建议标签
     */
    private List<String> generateSuggestedTags(String content, String category) {
        List<String> tags = new ArrayList<>();

        // 基于内容关键词生成标签
        if (content.contains("Vue") || content.contains("vue")) tags.add("Vue");
        if (content.contains("React") || content.contains("react")) tags.add("React");
        if (content.contains("Spring") || content.contains("spring")) tags.add("SpringBoot");
        if (content.contains("MySQL") || content.contains("mysql")) tags.add("MySQL");
        if (content.contains("Redis") || content.contains("redis")) tags.add("Redis");
        if (content.contains("算法") || content.contains("algorithm")) tags.add("算法");
        if (content.contains("面试") || content.contains("interview")) tags.add("面试");
        if (content.contains("JavaScript") || content.contains("javascript")) tags.add("JavaScript");
        if (content.contains("Java") || content.contains("java")) tags.add("Java");
        if (content.contains("Python") || content.contains("python")) tags.add("Python");

        // 基于分类添加标签
        if ("技术笔记".equals(category)) {
            tags.add("技术");
            tags.add("编程");
        } else if ("面试准备".equals(category)) {
            tags.add("面试");
            tags.add("求职");
        } else if ("学习心得".equals(category)) {
            tags.add("学习");
            tags.add("总结");
        }

        return tags.stream().distinct().limit(5).collect(Collectors.toList());
    }

    /**
     * 建议分类
     */
    private String suggestCategory(String content, String title) {
        String text = (title != null ? title : "") + " " + (content != null ? content : "");
        text = text.toLowerCase();

        if (text.contains("面试") || text.contains("interview")) {
            return "面试准备";
        } else if (text.contains("项目") || text.contains("管理") || text.contains("团队")) {
            return "工作总结";
        } else if (text.contains("学习") || text.contains("心得") || text.contains("总结")) {
            return "学习心得";
        } else {
            return "技术笔记";
        }
    }

    /**
     * 生成AI回复
     */
    private String generateAIResponse(String message, String context) {
        String lowerMessage = message.toLowerCase();

        // 基于消息内容生成回复
        if (lowerMessage.contains("优化") || lowerMessage.contains("改进")) {
            return "我可以帮您优化笔记内容的结构和表达。建议您：\n" +
                   "1. 使用清晰的标题层次结构\n" +
                   "2. 添加代码示例和实际案例\n" +
                   "3. 在关键点添加总结和要点\n" +
                   "4. 使用合适的标签便于后续查找\n\n" +
                   "您希望我重点优化哪个方面呢？";
        } else if (lowerMessage.contains("标签") || lowerMessage.contains("分类")) {
            return "根据您的笔记内容，我建议使用以下标签和分类：\n" +
                   "- 如果是技术相关：可以按技术栈分类（如前端、后端、数据库）\n" +
                   "- 如果是学习笔记：可以按知识领域分类\n" +
                   "- 建议使用3-5个精准的标签，便于快速检索\n\n" +
                   "需要我为您的笔记推荐具体的标签吗？";
        } else if (lowerMessage.contains("结构") || lowerMessage.contains("格式")) {
            return "好的笔记结构应该包括：\n" +
                   "1. **清晰的标题** - 概括主要内容\n" +
                   "2. **核心概念** - 重点知识点\n" +
                   "3. **实例说明** - 具体代码或案例\n" +
                   "4. **注意事项** - 易错点和最佳实践\n" +
                   "5. **总结回顾** - 关键要点梳理\n\n" +
                   "您希望我帮您重新组织笔记结构吗？";
        } else if (lowerMessage.contains("搜索") || lowerMessage.contains("查找")) {
            return "为了提高笔记的可搜索性，建议您：\n" +
                   "1. 在标题中包含关键技术词汇\n" +
                   "2. 使用统一的标签命名规范\n" +
                   "3. 在内容中适当重复重要概念\n" +
                   "4. 建立笔记间的关联链接\n\n" +
                   "这样可以让您更容易找到相关内容。";
        } else {
            return "我是您的AI笔记助手，可以帮您：\n" +
                   "✨ 优化笔记内容和结构\n" +
                   "🏷️ 推荐合适的标签和分类\n" +
                   "📝 改进文档格式和排版\n" +
                   "🔍 提升内容的可搜索性\n\n" +
                   "请告诉我您需要什么帮助，我会根据您的笔记内容提供个性化建议。";
        }
    }
}
