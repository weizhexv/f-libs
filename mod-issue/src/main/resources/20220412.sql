
CREATE TABLE `issue_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `body` varchar(1024) NOT NULL comment '消息体',
  `status` tinyint NOT NULL DEFAULT 0 comment '消息处理状态',
  `re_send_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `created_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `modified_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '消息事件';


CREATE TABLE `issue_sub` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic` varchar(128) NOT NULL comment '消息主题',
  `target` varchar(128) NOT NULL comment '消息目标处理器',
  `body` varchar(1024) NOT NULL comment '消息体',
  `status` tinyint NOT NULL DEFAULT 0 comment '消息处理状态',
  `created_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `modified_at` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci comment '消息持久化';