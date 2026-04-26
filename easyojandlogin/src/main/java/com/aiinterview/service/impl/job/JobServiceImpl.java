package com.aiinterview.service.impl.job;

import com.aiinterview.model.dto.job.JobDetailDTO;
import com.aiinterview.model.entity.job.Job;
import com.aiinterview.mapper.JobMapper;
import com.aiinterview.service.job.JobService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 职位服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobMapper jobMapper;
    
    @Override
    @Transactional
    public Job createJob(Job job) {
        try {
            // 设置默认值
            if (job.getIsActive() == null) {
                job.setIsActive(true);
            }
            if (job.getPostDate() == null) {
                job.setPostDate(LocalDate.now());
            }
            
            jobMapper.insert(job);
            log.info("职位创建成功，ID: {}", job.getJobId());
            return job;
        } catch (Exception e) {
            log.error("创建职位失败: {}", e.getMessage());
            throw new RuntimeException("创建职位失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public Job updateJob(Job job) {
        try {
            Job existingJob = jobMapper.selectById(job.getJobId());
            if (existingJob == null) {
                throw new RuntimeException("职位不存在");
            }
            
            jobMapper.updateById(job);
            log.info("职位更新成功，ID: {}", job.getJobId());
            return jobMapper.selectById(job.getJobId());
        } catch (Exception e) {
            log.error("更新职位失败: {}", e.getMessage());
            throw new RuntimeException("更新职位失败: " + e.getMessage());
        }
    }
    
    @Override
    public Job getJobById(Long jobId) {
        try {
            return jobMapper.selectById(jobId);
        } catch (Exception e) {
            log.error("获取职位详情失败: {}", e.getMessage());
            throw new RuntimeException("获取职位详情失败");
        }
    }

    @Override
    public JobDetailDTO getJobDetailById(Long jobId) {
        try {
            return jobMapper.selectJobDetailById(jobId);
        } catch (Exception e) {
            log.error("获取职位详情失败: {}", e.getMessage());
            throw new RuntimeException("获取职位详情失败");
        }
    }
    
    @Override
    public List<Job> getJobsByCompanyId(Long companyId) {
        try {
            QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("company_id", companyId)
                       .orderByDesc("created_at");
            return jobMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取公司职位列表失败: {}", e.getMessage());
            throw new RuntimeException("获取职位列表失败");
        }
    }

    @Override
    public List<JobDetailDTO> getJobsByCompanyIdWithCompany(Long companyId) {
        try {
            return jobMapper.selectJobsByCompanyIdWithCompany(companyId);
        } catch (Exception e) {
            log.error("获取公司职位列表失败: {}", e.getMessage());
            throw new RuntimeException("获取职位列表失败");
        }
    }
    
    @Override
    public Page<Job> getJobsByCompanyIdWithPage(Long companyId, int current, int size) {
        try {
            Page<Job> page = new Page<>(current, size);
            QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("company_id", companyId)
                       .orderByDesc("created_at");
            return jobMapper.selectPage(page, queryWrapper);
        } catch (Exception e) {
            log.error("分页获取公司职位列表失败: {}", e.getMessage());
            throw new RuntimeException("获取职位列表失败");
        }
    }
    
    @Override
    public List<JobDetailDTO> searchJobsWithCompany(String keyword, String jobType, String location) {
        try {
            return jobMapper.searchJobsWithCompany(keyword, jobType, location);
        } catch (Exception e) {
            log.error("搜索职位失败: {}", e.getMessage());
            throw new RuntimeException("搜索职位失败");
        }
    }

    @Override
    public List<JobDetailDTO> advancedSearchJobs(
            String keyword, String jobType, String location, String city,
            Integer minSalary, Integer maxSalary, String industry,
            String experience, String education, String companyScale) {
        try {
            return jobMapper.advancedSearchJobs(
                keyword, jobType, location, city, minSalary, maxSalary,
                industry, experience, education, companyScale
            );
        } catch (Exception e) {
            log.error("高级搜索职位失败: {}", e.getMessage());
            throw new RuntimeException("搜索职位失败");
        }
    }

    @Override
    public List<Job> searchJobs(String keyword, String jobType, String location) {
        try {
            QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_active", true);

            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.and(wrapper -> wrapper
                    .like("title", keyword)
                    .or()
                    .like("description", keyword)
                    .or()
                    .like("requirements", keyword)
                );
            }

            if (jobType != null && !jobType.trim().isEmpty()) {
                queryWrapper.eq("job_type", jobType);
            }

            if (location != null && !location.trim().isEmpty()) {
                queryWrapper.like("location", location);
            }

            queryWrapper.orderByDesc("post_date");
            return jobMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("搜索职位失败: {}", e.getMessage());
            throw new RuntimeException("搜索职位失败");
        }
    }
    
    @Override
    @Transactional
    public boolean toggleJobStatus(Long jobId, Long companyId) {
        try {
            Job job = jobMapper.selectById(jobId);
            if (job == null) {
                throw new RuntimeException("职位不存在");
            }
            
            if (!job.getCompanyId().equals(companyId)) {
                throw new RuntimeException("无权限操作此职位");
            }
            
            job.setIsActive(!job.getIsActive());
            jobMapper.updateById(job);
            
            log.info("职位状态切换成功，ID: {}, 新状态: {}", jobId, job.getIsActive());
            return true;
        } catch (Exception e) {
            log.error("切换职位状态失败: {}", e.getMessage());
            throw new RuntimeException("操作失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteJob(Long jobId, Long companyId) {
        try {
            Job job = jobMapper.selectById(jobId);
            if (job == null) {
                throw new RuntimeException("职位不存在");
            }
            
            if (!job.getCompanyId().equals(companyId)) {
                throw new RuntimeException("无权限删除此职位");
            }
            
            jobMapper.deleteById(jobId);
            log.info("职位删除成功，ID: {}", jobId);
            return true;
        } catch (Exception e) {
            log.error("删除职位失败: {}", e.getMessage());
            throw new RuntimeException("删除失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<JobDetailDTO> getActiveJobsWithCompany() {
        try {
            List<JobDetailDTO> jobs = jobMapper.selectActiveJobsWithCompany();
            log.info("查询活跃职位成功，共 {} 条", jobs.size());
            return jobs;
        } catch (Exception e) {
            log.error("获取活跃职位失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取职位列表失败");
        }
    }

    @Override
    public Page<JobDetailDTO> getActiveJobsWithCompanyPage(
            int current, int size,
            String keyword, String jobType, String location,
            String industry, String experience, String education,
            String companyScale, String applicationStatus,
            List<Long> submittedJobIds, String sortBy) {
        try {
            Page<JobDetailDTO> page = new Page<>(current, size);
            return jobMapper.selectActiveJobsWithCompanyPage(
                page,
                keyword,
                jobType,
                location,
                industry,
                experience,
                education,
                companyScale,
                applicationStatus,
                submittedJobIds,
                sortBy
            );
        } catch (Exception e) {
            log.error("分页获取活跃职位失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取职位列表失败");
        }
    }

    @Override
    public List<Job> getActiveJobs() {
        try {
            QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_active", true)
                       .and(wrapper -> wrapper
                           .isNull("expiration_date")
                           .or()
                           .ge("expiration_date", LocalDate.now())
                       )
                       .orderByDesc("post_date");
            return jobMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取活跃职位失败: {}", e.getMessage());
            throw new RuntimeException("获取职位列表失败");
        }
    }
    
    @Override
    public List<Job> getJobsByType(String jobType) {
        try {
            QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("job_type", jobType)
                       .eq("is_active", true)
                       .orderByDesc("post_date");
            return jobMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("根据类型获取职位失败: {}", e.getMessage());
            throw new RuntimeException("获取职位列表失败");
        }
    }
    
    @Override
    public List<Job> getExpiringJobs(Long companyId, int days) {
        try {
            QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("company_id", companyId)
                       .eq("is_active", true)
                       .isNotNull("expiration_date")
                       .between("expiration_date", LocalDate.now(), LocalDate.now().plusDays(days))
                       .orderBy(true, true, "expiration_date");
            return jobMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取即将过期职位失败: {}", e.getMessage());
            throw new RuntimeException("获取职位列表失败");
        }
    }
}
