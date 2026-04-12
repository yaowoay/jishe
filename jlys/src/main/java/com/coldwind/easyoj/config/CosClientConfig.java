// package com.coldwind.easyoj.config;

// import com.qcloud.cos.COSClient;
// import com.qcloud.cos.ClientConfig;
// import com.qcloud.cos.auth.BasicCOSCredentials;
// import com.qcloud.cos.auth.COSCredentials;
// import com.qcloud.cos.region.Region;
// import lombok.Data;
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云对象存储（COS）客户端配置
 * 配置项需在application.properties中以cos.client为前缀定义，例如：
 * cos.client.accessKey=你的腾讯云accessKey
 * cos.client.secretKey=你的腾讯云secretKey
 * cos.client.region=存储桶地域（如ap-guangzhou）
 * cos.client.bucket=存储桶名称
 */
// 原CosClientConfig类已不再需要，建议删除或注释
// @Configuration
// @ConfigurationProperties(prefix = "cos.client")
// @Data
// public class CosClientConfig {

    /**
     * 腾讯云API密钥的accessKey（必填）
     */
//     private String accessKey;

//     /**
//      * 腾讯云API密钥的secretKey（必填）
//      */
//     private String secretKey;

//     /**
//      * 存储桶所属地域（必填，格式如ap-guangzhou）
//      * 参考：https://cloud.tencent.com/document/product/436/6224
//      */
//     private String region;

//     /**
//      * 存储桶名称（必填，格式如bucket-accountId）
//      */
//     private String bucket;

//     @Bean
//     public COSClient cosClient() {
//         // 初始化用户身份信息
//         COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
//         // 配置存储桶地域
//         ClientConfig clientConfig = new ClientConfig(new Region(region));
//         // 生成COS客户端实例
//         return new COSClient(cred, clientConfig);
//     }
// }