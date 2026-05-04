const assert = require('assert')

const {
  normalizeResumeData,
  getModuleKeysWithData,
  applyModuleVisibility,
  buildResumeRequestPayload
} = require('./resumePreviewMapper')

const backendResume = {
  id: 9,
  name: 'Backend resume',
  fullName: 'Zhang San',
  phone: '13800138000',
  email: 'zhangsan@example.com',
  avatarUrl: 'avatar1.jpg',
  position: 'Java Engineer',
  workYears: 3,
  location: 'Beijing',
  profile: 'Backend developer',
  templateId: 3,
  workExperiences: [
    {
      company: 'Acme',
      position: 'Developer',
      startDate: '2024-01',
      endDate: '',
      responsibility: 'Built services',
      achievement: 'Improved latency'
    }
  ],
  educations: [
    {
      school: 'Example University',
      major: 'Computer Science',
      degree: 'Bachelor',
      startDate: '2020-09',
      endDate: '2024-06',
      description: 'GPA 3.8'
    }
  ],
  projectExperiences: [
    {
      projectName: 'Resume Builder',
      role: 'Owner',
      description: 'Built preview renderer'
    }
  ],
  skills: [
    {
      skillName: 'Java',
      proficiency: 4,
      description: 'Spring Boot'
    }
  ],
  additionalInfos: [
    {
      type: 'award',
      name: 'Scholarship',
      description: 'School level',
      time: '2023'
    }
  ]
}

const frontEndResume = {
  title: 'Frontend resume',
  templateId: 2,
  basicInfo: {
    name: 'Li Si',
    phone: '13900139000',
    email: 'lisi@example.com',
    avatar: 'data:image/png;base64,avatar',
    position: 'Frontend Engineer',
    workYears: 2,
    address: 'Shanghai',
    summary: 'Frontend developer'
  },
  workExperience: [
    {
      company: 'Widgets Inc',
      position: 'Frontend',
      startDate: '2023-03',
      responsibilities: 'Built UI',
      achievements: 'Raised conversion'
    }
  ],
  education: [
    {
      school: 'Frontend College',
      major: 'Software',
      degree: 'Bachelor'
    }
  ],
  projects: [
    {
      name: 'Design System',
      role: 'Developer',
      description: 'Components'
    }
  ],
  skills: [
    {
      name: 'Vue',
      level: 5,
      description: 'Vue 3'
    }
  ],
  others: {
    certificates: [{ name: 'CET-6', issuer: 'MOE', date: '2022' }],
    awards: [],
    hobbies: ['Reading']
  }
}

const normalizedBackend = normalizeResumeData(backendResume)
assert.strictEqual(normalizedBackend.avatarUrl, 'avatar1.jpg')
assert.strictEqual(normalizedBackend.workExperiences[0].responsibility, 'Built services')
assert.strictEqual(normalizedBackend.educations[0].school, 'Example University')
assert.strictEqual(normalizedBackend.projectExperiences[0].projectName, 'Resume Builder')
assert.strictEqual(normalizedBackend.skills[0].level, 4)
assert.strictEqual(normalizedBackend.additionalInfos[0].type, 'award')

const normalizedFrontend = normalizeResumeData(frontEndResume)
assert.strictEqual(normalizedFrontend.avatarUrl, 'data:image/png;base64,avatar')
assert.strictEqual(normalizedFrontend.workExperiences[0].responsibility, 'Built UI')
assert.strictEqual(normalizedFrontend.workExperiences[0].achievement, 'Raised conversion')
assert.strictEqual(normalizedFrontend.educations[0].school, 'Frontend College')
assert.strictEqual(normalizedFrontend.projectExperiences[0].projectName, 'Design System')
assert.strictEqual(normalizedFrontend.skills[0].skillName, 'Vue')
assert.strictEqual(normalizedFrontend.skills[0].level, 5)

assert.deepStrictEqual(
  getModuleKeysWithData(frontEndResume).sort(),
  ['education', 'others', 'projects', 'skills', 'workExperience'].sort()
)

const visibleData = applyModuleVisibility(frontEndResume, {
  basicInfo: true,
  workExperience: true,
  education: true,
  projects: true,
  skills: false,
  others: true
})
assert.strictEqual(visibleData.skills.length, 0)
assert.strictEqual(visibleData.workExperience.length, 1)
assert.strictEqual(visibleData.education.length, 1)

const payload = buildResumeRequestPayload(frontEndResume)
assert.strictEqual(payload.avatarUrl, 'data:image/png;base64,avatar')
assert.strictEqual(payload.expectedCity, 'Shanghai')
assert.strictEqual(payload.location, 'Shanghai')
assert.strictEqual(payload.workExperiences[0].responsibility, 'Built UI')
assert.strictEqual(payload.workExperiences[0].achievement, 'Raised conversion')
assert.strictEqual(payload.projectExperiences[0].projectName, 'Design System')
assert.strictEqual(payload.skills[0].proficiency, 5)
assert.strictEqual(payload.additionalInfos[0].description, 'MOE')
assert.strictEqual(payload.additionalInfos[0].time, '2022')

console.log('resumePreviewMapper tests passed')
