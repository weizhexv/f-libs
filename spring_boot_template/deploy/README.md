## Deployment Script
作为不同环境的服务部署和操作的工具脚本，底层直接使用Systemd Service。

## 结构说明
    deploy                  # 主目录
      ├── base-env.sh       # 默认环境变量配置，不要修改，可以被env.sh覆盖
      ├── deploy.sh         # 部署脚本，不要修改
      ├── prod              # 生产环境目录
      │    └── env.sh       # 生产环境的ENV定制，用于覆盖base-env.sh
      ├── qa                # QA环境目录
      │    └── env.sh       # QA环境的ENV定制，用于覆盖base-env.sh
      └── template.service  # Systemd Unit配置模版，用于生成应用的服务Unit定义

## 命令说明
* 通过执行deploy.sh 

      DEPLOY_ENV=<env> sh deploy/deploy.sh <command>
      # env: prod | qa
      # command: deploy | undeploy | start | stop | restart

* 通过系统systemctl提供的能力，**必须成功deploy之后**

      sudo systemctl <command> service_name
      # command: status | start | stop | restart
      # service_name: 查看deploy目录下生成服务定义文件，如 f-dc.service

* 通过系统journalctl查看服务启动日志，**必须成功deploy之后**

      sudo journalctl -f -u service_name
      # -f: follow log, likes 'tail -f'
      # service_name: 查看deploy目录下生成服务定义文件，如 f-dc.service

## 使用说明
1. Copy deploy 目录到负责应用打包的模块
2. 修改prod和qa目录下的env.sh
   1. `APP_START_TIMEOUT`: 默认健康检查30秒，如果超过需要修改为对应时间
   2. `APP_PORT`: 应用端口，需要修改为对应端口
   3. `HEALTH_CHECK_URL`: 如果需要，修改为对应URL
   4. `JAR_NAME`: 应用jar包的文件路径，默认会使用应用目录作为jar包名称前缀，
      1. 如 `/home/admin/f-dc/f-dc-1.0.0-SNAPSHOT.jar`
      2. 若jar名称有特殊规则，需要修改，如 `export JAR_NAME=${APP_HOME}/idcenter*.jar`
3. 修改所在模块的pom.xml，增加build plugin在构建时copy deploy目录到target目录
      
        <build>
          <plugins>
              <!--other plugins-->
              <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <executions>
                    <execution>
                        <id>copy-resources</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${basedir}/target</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>./</directory>
                                    <includes>
                                        <include>deploy/**</include>
                                    </includes>
                                    <filtering>true</filtering>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
          </plugins>
        </build>

4. 日志配置调整，在`base-env.sh`中已经定义了`LOG_HOME=${APP_HOME}/logs`，需要检查logback配置是否使用了正确的环境变量，推荐配置如下:
   1. 不支持条件判断，分环境配置: [f-infra](https://codeup.aliyun.com/61a59eb56f7e36c003355682/server/f-infra/blob/master/start%2Fsrc%2Fmain%2Fresources%2Flogback-qa.xml)
   2. 支持条件判断，统一配置： [f-api-gateway](https://codeup.aliyun.com/61a59eb56f7e36c003355682/server/f-api-gateway/blob/master/src%2Fmain%2Fresources%2Flogback-spring.xml)
5. 系统用户会设置为 `admin`，请确保服务器package目录和应用目录的owner是admin

       sudo chown admin:admin -R /home/admin
6. 如果启动时发现端口被占用，请确保老进程被kill后重试。

## 阿里云Flow配置
1. Java构建上传阶段，确保打包路径为 `target` 或 `start/target/` (如果存在start模块)
2. 主机部署
   1. 下载路径: `/home/admin/app/<app_name>_package.tgz`
   2. 执行用户: `admin`
   3. 部署脚本: **注意目录层次**，如果存在start模块 `-components=2`，否则为 `-components=1`
   
          mkdir -p /home/admin/<app_name>
          tar zxvf /home/admin/app/<app_name>_package.tgz -C /home/admin/<app_name>/ --strip-components=2
          DEPLOY_ENV=<env> sh /home/admin/<app_name>/deploy/deploy.sh deploy
          # env: prod | qa
          # -components: 存在start模块为2，否则为1