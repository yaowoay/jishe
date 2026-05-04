import request from '@/utils/request'

/**
 * 获取所有技能关联规则
 */
export function getAllRules() {
  return request({
    url: '/skill-association-rules',
    method: 'get'
  })
}

/**
 * 根据技能A查询关联规则
 */
export function getRulesBySkillA(skillA) {
  return request({
    url: `/skill-association-rules/skill-a/${skillA}`,
    method: 'get'
  })
}

/**
 * 根据技能B查询关联规则
 */
export function getRulesBySkillB(skillB) {
  return request({
    url: `/skill-association-rules/skill-b/${skillB}`,
    method: 'get'
  })
}

/**
 * 查询技能对的关联规则
 */
export function getRuleBySkillPair(skillA, skillB) {
  return request({
    url: '/skill-association-rules/skill-pair',
    method: 'get',
    params: { skillA, skillB }
  })
}

/**
 * 按置信度排序查询规则
 */
export function getRulesOrderByConfidence() {
  return request({
    url: '/skill-association-rules/order-by-confidence',
    method: 'get'
  })
}

/**
 * 按提升度排序查询规则
 */
export function getRulesOrderByLift() {
  return request({
    url: '/skill-association-rules/order-by-lift',
    method: 'get'
  })
}

/**
 * 分页查询规则
 */
export function getRulesByPage(pageNum, pageSize) {
  return request({
    url: '/skill-association-rules/page',
    method: 'get',
    params: { pageNum, pageSize }
  })
}

/**
 * 添加规则
 */
export function addRule(rule) {
  return request({
    url: '/skill-association-rules/add',
    method: 'post',
    data: rule
  })
}

/**
 * 更新规则
 */
export function updateRule(rule) {
  return request({
    url: '/skill-association-rules/update',
    method: 'put',
    data: rule
  })
}

/**
 * 删除规则
 */
export function deleteRule(ruleId) {
  return request({
    url: `/skill-association-rules/${ruleId}`,
    method: 'delete'
  })
}

/**
 * 获取规则总数
 */
export function getRuleCount() {
  return request({
    url: '/skill-association-rules/count',
    method: 'get'
  })
}

/**
 * 获取职位的技能要求
 */
export function getSkillsByPosition(positionName) {
  return request({
    url: '/skill-association-rules/skills-by-position',
    method: 'get',
    params: { positionName }
  })
}
