// package com.coldwind.easyoj.service;

// import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
// import com.coldwind.easyoj.model.dto.post.PostQueryRequest;

// import javax.annotation.Resource;
// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

// /**
//  * 帖子服务测试（已移除ES依赖）
//  *
//  * EL PSY KONGROO
//  */
// @SpringBootTest
// class PostServiceTest {

//     @Resource
//     private PostService postService;

//     // 替换为数据库搜索测试
//     @Test
//     void searchFromDb() {
//         PostQueryRequest postQueryRequest = new PostQueryRequest();
//         postQueryRequest.setUserId(1L);
//         // 假设原searchFromEs改为searchFromDb，基于数据库查询
//         Page<Post> postPage = postService.searchFromDb(postQueryRequest);
//         Assertions.assertNotNull(postPage);
//     }
// }

// @Test
// void searchFromEs() {
//     PostQueryRequest postQueryRequest = new PostQueryRequest();
//     postQueryRequest.setUserId(1L);
//     Page<Post> postPage = postService.searchFromEs(postQueryRequest);
//     Assertions.assertNotNull(postPage);
// }
