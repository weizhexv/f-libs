## 后端通用库
### 内容
- bom: maven bom，全局二三方依赖的版本定义，如果项目使用了Spring Boot的bom，优先使用其配置，版本信息参考[Dependency Versions](https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html)
- parent: 全局父项目，其中引用了bom，用于配置各种通用依赖和插件等
- spring_boot_template: 使用Spring Boot的模版项目，简单配置bom和parent的依赖，支持web,  mybatis...
- spring_cloud_template: 使用Spring Cloud的模版项目
- common: 公共库
