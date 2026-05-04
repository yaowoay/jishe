// 专业测试题库数据
export const questionBank = {
  // 后端开发题库
  backend: [
    {
      id: 1,
      question: '介绍一下Java的垃圾回收算法？',
      englishQuestion: 'What are Java\'s garbage collection algorithms?',
      type: 'single', // single: 单选, multiple: 多选, text: 简答
      options: [
        '包括标记-清除、标记-复制、标记-整理、分代收集等算法',
        '只有标记-清除和标记-复制两种算法',
        '主要是引用计数算法',
        '只有分代收集算法'
      ],
      correctAnswer: 0,
      chineseAnswer: '包括标记-清除（Mark-Sweep）、标记-复制（Mark-Copy）、标记-整理（Mark-Compact）、分代收集（Generational Collection）等。标记-清除效率低且有碎片；标记-复制适合年轻代；标记-整理适合老年代；分代收集结合前三者按对象年龄分代处理。',
      englishAnswer: 'Including Mark-Sweep, Mark-Copy, Mark-Compact, Generational Collection. Mark-Sweep is inefficient with fragments; Mark-Copy fits young generation; Mark-Compact fits old generation; Generational Collection combines them by object age.',
      difficulty: '中等',
      category: 'JVM'
    },
    {
      id: 2,
      question: '介绍一下Java的线程池？',
      englishQuestion: 'What is Java\'s thread pool?',
      type: 'single',
      options: [
        '线程池管理线程生命周期，包含核心线程、最大线程、任务队列等组件',
        '线程池只是简单的线程集合',
        '线程池不能控制并发量',
        '线程池会增加线程创建开销'
      ],
      correctAnswer: 0,
      chineseAnswer: '线程池管理线程生命周期，包含核心线程、最大线程、任务队列等组件，减少线程创建/销毁开销，控制并发量。主要实现类有ThreadPoolExecutor，常见类型有FixedThreadPool、CachedThreadPool等。',
      englishAnswer: 'Manages thread lifecycle with core/max threads and task queue, reducing creation/destruction overhead. Main implementation: ThreadPoolExecutor; common types: FixedThreadPool, CachedThreadPool.',
      difficulty: '中等',
      category: '并发编程'
    },
    {
      id: 3,
      question: '介绍一下SpringAOP和IOC？',
      englishQuestion: 'What are Spring AOP and IOC?',
      type: 'single',
      options: [
        'AOP是面向切面编程，IOC是控制反转',
        'AOP是面向对象编程，IOC是依赖注入',
        'AOP和IOC是同一个概念',
        'AOP是控制反转，IOC是面向切面编程'
      ],
      correctAnswer: 0,
      chineseAnswer: 'AOP（面向切面编程）通过动态代理实现横切逻辑（如日志、事务）与业务逻辑分离；IOC（控制反转）将对象创建权交给容器，通过依赖注入（DI）管理对象依赖，降低耦合。',
      englishAnswer: 'AOP separates cross-cutting logic (logging, transactions) via dynamic proxy; IOC delegates object creation to container, managing dependencies via DI to reduce coupling.',
      difficulty: '中等',
      category: 'Spring框架'
    },
    {
      id: 4,
      question: '介绍一下ConcurrentHashMap的实现原理？',
      englishQuestion: 'What is the implementation principle of ConcurrentHashMap?',
      type: 'single',
      options: [
        'JDK8采用CAS+Synchronized对桶节点加锁',
        '始终使用分段锁机制',
        '完全不使用锁机制',
        '只使用synchronized关键字'
      ],
      correctAnswer: 0,
      chineseAnswer: 'JDK7通过分段锁（Segment）实现线程安全，每个Segment独立加锁；JDK8及以上取消分段锁，采用CAS+Synchronized对桶节点加锁，支持更高并发。',
      englishAnswer: 'JDK7 uses segment locks for thread safety (each Segment locks independently); JDK8+ replaces segments with CAS+Synchronized on bucket nodes for higher concurrency.',
      difficulty: '困难',
      category: '集合框架'
    },
    {
      id: 5,
      question: 'JVM的内存模型？',
      englishQuestion: 'What is JVM memory model?',
      type: 'single',
      options: [
        'Java内存模型定义线程与主内存的交互规则',
        'JVM内存模型就是堆和栈',
        '内存模型只包含方法区',
        '内存模型不涉及线程安全'
      ],
      correctAnswer: 0,
      chineseAnswer: '即Java内存模型（JMM），定义线程与主内存的交互规则，通过抽象模型解决多线程可见性、原子性、有序性问题，规定所有变量存储在主内存，线程操作需加载到工作内存。',
      englishAnswer: 'Java Memory Model (JMM) defines thread-main memory interaction rules, solving visibility/atomicity/ordering in multi-threading. All variables are in main memory; threads load to working memory for operations.',
      difficulty: '中等',
      category: 'JVM'
    },
    {
      id: 6,
      question: '类加载机制是什么？',
      englishQuestion: 'What is class loading mechanism?',
      type: 'multiple',
      options: [
        '包括加载、验证、准备、解析、初始化5个阶段',
        '由类加载器完成',
        '遵循双亲委派模型',
        '只有3个阶段'
      ],
      correctAnswer: [0, 1, 2],
      chineseAnswer: '指将类的字节码加载到JVM并生成Class对象的过程，包括加载、验证、准备、解析、初始化5个阶段，由类加载器完成，遵循双亲委派模型。',
      englishAnswer: 'Process of loading class bytecode into JVM to generate Class objects, including loading, verification, preparation, resolution, initialization, done by class loaders following parent delegation model.',
      difficulty: '中等',
      category: 'JVM'
    },
    {
      id: 7,
      question: '介绍一下Java的多线程？',
      englishQuestion: 'What is Java multi-threading?',
      type: 'single',
      options: [
        '多线程允许程序同时执行多个任务，共享进程资源',
        '多线程不能共享资源',
        'Java不支持多线程',
        '多线程只能顺序执行'
      ],
      correctAnswer: 0,
      chineseAnswer: '多线程允许程序同时执行多个任务，共享进程资源。通过Thread类或Runnable接口创建，涉及线程状态（新建、就绪、运行、阻塞、终止）、同步机制（synchronized、Lock）等。',
      englishAnswer: 'Enables simultaneous task execution sharing process resources. Created via Thread/Runnable, involving states (New, Runnable, Running, Blocked, Terminated) and synchronization (synchronized, Lock).',
      difficulty: '中等',
      category: '并发编程'
    },
    {
      id: 8,
      question: '面向对象的原则？',
      englishQuestion: 'What are object-oriented principles?',
      type: 'multiple',
      options: [
        '封装（Encapsulation）',
        '继承（Inheritance）',
        '多态（Polymorphism）',
        '抽象（Abstraction）'
      ],
      correctAnswer: [0, 1, 2, 3],
      chineseAnswer: '包括封装（Encapsulation）、继承（Inheritance）、多态（Polymorphism）、抽象（Abstraction）四大基本原则，以及单一职责、开闭、里氏替换等设计原则。',
      englishAnswer: 'Including 4 basic principles: Encapsulation, Inheritance, Polymorphism, Abstraction; plus design principles like Single Responsibility, Open-Closed, Liskov Substitution.',
      difficulty: '简单',
      category: '面向对象'
    },
    {
      id: 9,
      question: 'Java线程池有哪些参数？',
      englishQuestion: 'What are the parameters of Java thread pool?',
      type: 'multiple',
      options: [
        '核心线程数（corePoolSize）',
        '最大线程数（maximumPoolSize）',
        '空闲线程存活时间（keepAliveTime）',
        '工作队列（workQueue）'
      ],
      correctAnswer: [0, 1, 2, 3],
      chineseAnswer: '核心参数包括：核心线程数（corePoolSize）、最大线程数（maximumPoolSize）、空闲线程存活时间（keepAliveTime）、工作队列（workQueue）、拒绝策略（handler）等。',
      englishAnswer: 'Core parameters: corePoolSize, maximumPoolSize, keepAliveTime, workQueue, rejection handler, etc.',
      difficulty: '中等',
      category: '并发编程'
    },
    {
      id: 10,
      question: '介绍一下HashMap？',
      englishQuestion: 'What is HashMap?',
      type: 'single',
      options: [
        '基于哈希表的键值对集合，线程不安全',
        '基于红黑树的集合，线程安全',
        '基于链表的集合',
        '基于数组的集合'
      ],
      correctAnswer: 0,
      chineseAnswer: '基于哈希表的键值对集合，允许null键值，线程不安全。JDK8前由数组+链表组成，JDK8及以上引入红黑树优化哈希冲突（链表长度≥8时转换）。',
      englishAnswer: 'Hash table-based key-value collection allowing nulls, thread-unsafe. JDK8-: array+linked list; JDK8+: red-black tree for hash conflicts (when list length ≥8).',
      difficulty: '中等',
      category: '集合框架'
    },
    {
      id: 11,
      question: '介绍一下JVM？',
      englishQuestion: 'What is JVM?',
      type: 'single',
      options: [
        'Java虚拟机，执行Java字节码的虚拟计算机',
        'Java编译器',
        'Java开发工具包',
        'Java运行环境'
      ],
      correctAnswer: 0,
      chineseAnswer: 'Java虚拟机（JVM）是执行Java字节码的虚拟计算机，负责将字节码翻译成机器码并运行。由类加载器、运行时数据区（方法区、堆、栈等）、执行引擎等组成。',
      englishAnswer: 'Java Virtual Machine executes Java bytecode by translating to machine code. Consists of class loaders, runtime data areas (method area, heap, stack), execution engine, etc.',
      difficulty: '中等',
      category: 'JVM'
    },
    {
      id: 12,
      question: '创建线程的方法？',
      englishQuestion: 'What are the ways to create threads in Java?',
      type: 'multiple',
      options: [
        '继承Thread类并重写run()',
        '实现Runnable接口',
        '实现Callable接口结合FutureTask',
        '使用线程池'
      ],
      correctAnswer: [0, 1, 2],
      chineseAnswer: '主要有三种：1. 继承Thread类并重写run()；2. 实现Runnable接口；3. 实现Callable接口结合FutureTask获取返回值。',
      englishAnswer: 'Three main ways: 1. Extend Thread and override run(); 2. Implement Runnable; 3. Implement Callable with FutureTask for return values.',
      difficulty: '简单',
      category: '并发编程'
    },
    {
      id: 13,
      question: '介绍一下AQS？',
      englishQuestion: 'What is AQS?',
      type: 'single',
      options: [
        'AbstractQueuedSynchronizer，Java并发工具的基础框架',
        '一种数据结构',
        '一种设计模式',
        '一种算法'
      ],
      correctAnswer: 0,
      chineseAnswer: 'AbstractQueuedSynchronizer（抽象队列同步器）是Java并发工具的基础框架，通过 volatile 状态变量和双向队列实现同步，ReentrantLock、CountDownLatch等均基于此实现。',
      englishAnswer: 'AbstractQueuedSynchronizer is the base for Java concurrency tools, implementing synchronization via volatile state and bidirectional queue. Used by ReentrantLock, CountDownLatch, etc.',
      difficulty: '困难',
      category: '并发编程'
    },
    {
      id: 14,
      question: '介绍一下Spring事务的原理？',
      englishQuestion: 'What is the principle of Spring transactions?',
      type: 'single',
      options: [
        '基于AOP实现，通过事务管理器管理事务',
        '基于数据库实现',
        '基于JVM实现',
        '基于操作系统实现'
      ],
      correctAnswer: 0,
      chineseAnswer: '基于AOP实现，通过事务管理器（PlatformTransactionManager）管理事务，利用代理机制在目标方法前后织入事务控制逻辑（开启、提交、回滚），默认基于异常回滚。',
      englishAnswer: 'Implemented via AOP; transaction manager (PlatformTransactionManager) controls transactions, with proxies weaving transaction logic (begin, commit, rollback) around target methods, default rollback on exceptions.',
      difficulty: '中等',
      category: 'Spring框架'
    },
    {
      id: 15,
      question: 'SpringEvent和RocketMQ有什么区别？',
      englishQuestion: 'Difference between SpringEvent and RocketMQ?',
      type: 'single',
      options: [
        'SpringEvent是应用内事件机制，RocketMQ是分布式消息队列',
        'SpringEvent和RocketMQ功能完全相同',
        'SpringEvent是分布式的，RocketMQ是本地的',
        '两者都是数据库'
      ],
      correctAnswer: 0,
      chineseAnswer: 'SpringEvent是Spring框架内的事件机制，基于观察者模式，用于应用内组件通信；RocketMQ是分布式消息队列，用于跨服务异步通信，支持持久化、重试等。',
      englishAnswer: 'SpringEvent is in-framework event mechanism (observer pattern) for in-app communication; RocketMQ is distributed message queue for cross-service async communication with persistence, retry support.',
      difficulty: '中等',
      category: 'Spring框架'
    }
  ],
  
  // 前端开发题库（示例）
  frontend: [
    // 这里可以添加前端题目
  ],
  
  // 数据分析题库（示例）
  dataAnalysis: [
    // 这里可以添加数据分析题目
  ]
}

// 根据类别获取题目
export function getQuestionsByCategory(category, count = 10) {
  const questions = questionBank[category] || []
  // 随机选择指定数量的题目
  const shuffled = questions.sort(() => 0.5 - Math.random())
  return shuffled.slice(0, Math.min(count, questions.length))
}

// 计算分数
export function calculateScore(answers, questions) {
  let correctCount = 0
  let totalCount = questions.length
  
  questions.forEach((question, index) => {
    const userAnswer = answers[index]
    if (question.type === 'multiple') {
      // 多选题：需要完全匹配
      if (Array.isArray(userAnswer) && Array.isArray(question.correctAnswer)) {
        const sorted1 = [...userAnswer].sort()
        const sorted2 = [...question.correctAnswer].sort()
        if (JSON.stringify(sorted1) === JSON.stringify(sorted2)) {
          correctCount++
        }
      }
    } else {
      // 单选题
      if (userAnswer === question.correctAnswer) {
        correctCount++
      }
    }
  })
  
  return Math.round((correctCount / totalCount) * 100)
}

// 生成能力分析报告
export function generateAnalysisReport(answers, questions, score) {
  const categoryStats = {}
  const difficultyStats = { '简单': 0, '中等': 0, '困难': 0 }
  const categoryCorrect = {}
  const difficultyCorrect = { '简单': 0, '中等': 0, '困难': 0 }
  
  questions.forEach((question, index) => {
    const category = question.category
    const difficulty = question.difficulty
    const userAnswer = answers[index]
    let isCorrect = false
    
    // 判断答案是否正确
    if (question.type === 'multiple') {
      if (Array.isArray(userAnswer) && Array.isArray(question.correctAnswer)) {
        const sorted1 = [...userAnswer].sort()
        const sorted2 = [...question.correctAnswer].sort()
        isCorrect = JSON.stringify(sorted1) === JSON.stringify(sorted2)
      }
    } else {
      isCorrect = userAnswer === question.correctAnswer
    }
    
    // 统计分类
    if (!categoryStats[category]) {
      categoryStats[category] = 0
      categoryCorrect[category] = 0
    }
    categoryStats[category]++
    if (isCorrect) categoryCorrect[category]++
    
    // 统计难度
    difficultyStats[difficulty]++
    if (isCorrect) difficultyCorrect[difficulty]++
  })
  
  // 生成分析结果
  const categoryAnalysis = Object.keys(categoryStats).map(category => ({
    category,
    total: categoryStats[category],
    correct: categoryCorrect[category],
    accuracy: Math.round((categoryCorrect[category] / categoryStats[category]) * 100)
  }))
  
  const difficultyAnalysis = Object.keys(difficultyStats).map(difficulty => ({
    difficulty,
    total: difficultyStats[difficulty],
    correct: difficultyCorrect[difficulty],
    accuracy: Math.round((difficultyCorrect[difficulty] / difficultyStats[difficulty]) * 100)
  }))
  
  return {
    score,
    categoryAnalysis,
    difficultyAnalysis,
    suggestions: generateSuggestions(categoryAnalysis, score)
  }
}

// 生成学习建议
function generateSuggestions(categoryAnalysis, score) {
  const suggestions = []
  
  if (score >= 90) {
    suggestions.push('恭喜！您的专业水平非常优秀，建议继续保持并深入学习前沿技术。')
  } else if (score >= 80) {
    suggestions.push('您的专业基础很好，建议针对薄弱环节进行专项提升。')
  } else if (score >= 60) {
    suggestions.push('您的基础知识掌握一般，建议系统性地复习相关知识点。')
  } else {
    suggestions.push('建议从基础知识开始，系统性地学习相关技术栈。')
  }
  
  // 针对薄弱分类给出建议
  categoryAnalysis.forEach(item => {
    if (item.accuracy < 60) {
      switch (item.category) {
      case 'JVM':
        suggestions.push('建议深入学习JVM原理，包括内存模型、垃圾回收、类加载等核心概念。')
        break
      case '并发编程':
        suggestions.push('建议加强多线程编程学习，掌握线程安全、锁机制、线程池等知识。')
        break
      case 'Spring框架':
        suggestions.push('建议系统学习Spring框架，包括IOC、AOP、事务管理等核心特性。')
        break
      case '集合框架':
        suggestions.push('建议深入理解Java集合框架的底层实现和使用场景。')
        break
      default:
        suggestions.push(`建议加强${item.category}相关知识的学习。`)
      }
    }
  })
  
  return suggestions
}
