function clone(value) {
  return JSON.parse(JSON.stringify(value || {}))
}

function firstFilled() {
  for (let i = 0; i < arguments.length; i += 1) {
    const value = arguments[i]
    if (value !== undefined && value !== null && value !== '') {
      return value
    }
  }
  return ''
}

function toList(value) {
  return Array.isArray(value) ? value : []
}

function fixDate(value) {
  if (!value) return ''
  if (/^\d{4}-\d{2}-\d{2}$/.test(value)) return value
  if (/^\d{4}-\d{2}$/.test(value)) return value + '-01'
  return value
}

function hasMeaningfulValue(value) {
  if (value === null || value === undefined) return false
  if (typeof value === 'string') return value.trim() !== ''
  if (typeof value === 'number') return value > 0
  if (typeof value === 'boolean') return false
  if (Array.isArray(value)) return value.some(hasMeaningfulValue)
  if (typeof value === 'object') {
    return Object.keys(value).some((key) => {
      if (key === 'id' || key === 'isCurrent') return false
      return hasMeaningfulValue(value[key])
    })
  }
  return Boolean(value)
}

function normalizeWorkExperience(work) {
  return {
    id: work.id,
    company: firstFilled(work.company),
    position: firstFilled(work.position),
    startDate: firstFilled(work.startDate),
    endDate: firstFilled(work.endDate),
    responsibility: firstFilled(work.responsibility, work.responsibilities),
    achievement: firstFilled(work.achievement, work.achievements)
  }
}

function normalizeEducation(edu) {
  return {
    id: edu.id,
    school: firstFilled(edu.school),
    major: firstFilled(edu.major),
    degree: firstFilled(edu.degree),
    startDate: firstFilled(edu.startDate),
    endDate: firstFilled(edu.endDate),
    description: firstFilled(edu.description)
  }
}

function normalizeProject(project) {
  return {
    id: project.id,
    projectName: firstFilled(project.projectName, project.name),
    name: firstFilled(project.projectName, project.name),
    role: firstFilled(project.role),
    startDate: firstFilled(project.startDate),
    endDate: firstFilled(project.endDate),
    technologies: firstFilled(project.technologies),
    description: firstFilled(project.description)
  }
}

function normalizeSkill(skill) {
  const rawLevel = firstFilled(skill.level, skill.proficiency, 0)
  const level = Number(rawLevel) || 0
  return {
    id: skill.id,
    skillName: firstFilled(skill.skillName, skill.name),
    name: firstFilled(skill.skillName, skill.name),
    level,
    proficiency: level,
    description: firstFilled(skill.description)
  }
}

function normalizeAdditionalInfo(info) {
  return {
    id: info.id,
    type: firstFilled(info.type),
    name: firstFilled(info.name),
    time: firstFilled(info.time, info.date),
    description: firstFilled(info.description, info.issuer, info.level)
  }
}

function normalizeOthers(others) {
  if (!others) return []
  return [
    ...toList(others.certificates).map((cert) => normalizeAdditionalInfo({ type: 'certificate', ...cert })),
    ...toList(others.awards).map((award) => normalizeAdditionalInfo({ type: 'award', ...award })),
    ...toList(others.hobbies).map((hobby) => normalizeAdditionalInfo(
      typeof hobby === 'string' ? { type: 'hobby', name: hobby } : { type: 'hobby', ...hobby }
    ))
  ]
}

function normalizeResumeData(rawData) {
  const data = rawData || {}
  const basicInfo = data.basicInfo || {}
  const isFrontendShape = Boolean(data.basicInfo || data.workExperience || data.education || data.projects || data.others)

  if (!isFrontendShape) {
    return {
      id: data.id,
      name: firstFilled(data.name, data.title),
      fullName: firstFilled(data.fullName, data.name),
      phone: firstFilled(data.phone),
      email: firstFilled(data.email),
      avatarUrl: firstFilled(data.avatarUrl, data.avatar, data.photoUrl, data.imgUrl),
      position: firstFilled(data.position),
      workYears: Number(firstFilled(data.workYears, 0)) || 0,
      location: firstFilled(data.location, data.expectedCity),
      profile: firstFilled(data.profile, data.summary),
      template: firstFilled(data.template, 'template1'),
      templateId: data.templateId || null,
      workExperiences: toList(data.workExperiences).map(normalizeWorkExperience),
      educations: toList(data.educations).map(normalizeEducation),
      projectExperiences: toList(data.projectExperiences).map(normalizeProject),
      skills: toList(data.skills).map(normalizeSkill),
      additionalInfos: toList(data.additionalInfos).map(normalizeAdditionalInfo)
    }
  }

  return {
    id: data.id,
    name: firstFilled(data.title, data.name, basicInfo.name),
    fullName: firstFilled(basicInfo.name, data.fullName),
    phone: firstFilled(basicInfo.phone, data.phone),
    email: firstFilled(basicInfo.email, data.email),
    avatarUrl: firstFilled(basicInfo.avatar, data.avatarUrl, data.avatar),
    position: firstFilled(basicInfo.position, data.position),
    workYears: Number(firstFilled(basicInfo.workYears, data.workYears, 0)) || 0,
    location: firstFilled(basicInfo.address, data.location, data.expectedCity),
    profile: firstFilled(basicInfo.summary, data.profile, data.summary),
    template: firstFilled(data.template, 'template1'),
    templateId: data.templateId || null,
    workExperiences: toList(data.workExperience).map(normalizeWorkExperience),
    educations: toList(data.education).map(normalizeEducation),
    projectExperiences: toList(data.projects).map(normalizeProject),
    skills: toList(data.skills).map(normalizeSkill),
    additionalInfos: normalizeOthers(data.others)
  }
}

function getModuleKeysWithData(rawData) {
  const data = rawData || {}
  const checks = {
    workExperience: hasMeaningfulValue(data.workExperience) || hasMeaningfulValue(data.workExperiences),
    education: hasMeaningfulValue(data.education) || hasMeaningfulValue(data.educations),
    projects: hasMeaningfulValue(data.projects) || hasMeaningfulValue(data.projectExperiences),
    skills: hasMeaningfulValue(data.skills),
    others: hasMeaningfulValue(data.others) || hasMeaningfulValue(data.additionalInfos)
  }

  return Object.keys(checks).filter((key) => checks[key])
}

function applyModuleVisibility(rawData, visibilityMap) {
  const data = clone(rawData)
  const visibility = visibilityMap || {}

  if (visibility.workExperience === false) data.workExperience = []
  if (visibility.education === false) data.education = []
  if (visibility.projects === false) data.projects = []
  if (visibility.skills === false) data.skills = []
  if (visibility.others === false) {
    data.others = {
      certificates: [],
      awards: [],
      hobbies: []
    }
  }

  return data
}

function buildResumeRequestPayload(rawData) {
  const data = rawData || {}
  const normalized = normalizeResumeData(data)

  return {
    name: firstFilled(data.title, data.name, normalized.fullName),
    fullName: normalized.fullName,
    phone: normalized.phone,
    email: normalized.email,
    avatarUrl: normalized.avatarUrl,
    position: normalized.position,
    workYears: normalized.workYears,
    location: normalized.location,
    expectedCity: normalized.location,
    profile: normalized.profile,
    template: normalized.template,
    templateId: normalized.templateId,
    workExperiences: normalized.workExperiences.map((work) => ({
      company: work.company,
      position: work.position,
      startDate: fixDate(work.startDate),
      endDate: fixDate(work.endDate),
      responsibility: work.responsibility,
      achievement: work.achievement
    })),
    educations: normalized.educations.map((edu) => ({
      school: edu.school,
      major: edu.major,
      degree: edu.degree,
      startDate: fixDate(edu.startDate),
      endDate: fixDate(edu.endDate),
      description: edu.description
    })),
    projectExperiences: normalized.projectExperiences.map((project) => ({
      projectName: project.projectName,
      role: project.role,
      startDate: fixDate(project.startDate),
      endDate: fixDate(project.endDate),
      description: project.description
    })),
    skills: normalized.skills.map((skill) => ({
      skillName: skill.skillName,
      proficiency: skill.proficiency || skill.level,
      description: skill.description
    })),
    additionalInfos: normalized.additionalInfos.map((info) => ({
      type: info.type,
      name: info.name,
      time: info.time,
      description: info.description
    }))
  }
}

module.exports = {
  normalizeResumeData,
  getModuleKeysWithData,
  applyModuleVisibility,
  buildResumeRequestPayload
}
