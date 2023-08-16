#!/bin/bash
# overriding settings in ../base-env.sh

export APP_START_TIMEOUT=30    # 等待应用启动的时间
export APP_PORT=11000          # 应用端口
export HEALTH_CHECK_URL=http://127.0.0.1:${APP_PORT}/healthcheck  # 应用健康检查URL