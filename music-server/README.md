# 三端统一启动指南（Server + Admin + Client）

本项目为三端音乐网站： - music-server：后端服务（Spring Boot 3 +
MyBatis-Plus + MySQL + Redis + MinIO） - music-admin：管理端前端（Web
管理后台） - music-client：用户端前端（Web 客户端）

推荐环境：Windows 11 + PowerShell + Docker Desktop

------------------------------------------------------------------------

## 1. 环境准备

### 必备软件

-   JDK 17
-   Maven 3.9+
-   Node.js 18+
-   Docker Desktop

检查版本：

    java -version
    mvn -v
    node -v
    docker -v

------------------------------------------------------------------------

## 2. 启动基础依赖（MySQL / Redis / MinIO）

### MySQL 8

    docker run -d --name music-mysql ^
      -p 3306:3306 ^
      -e MYSQL_ROOT_PASSWORD=root ^
      -e MYSQL_DATABASE=musicdb ^
      mysql:8.0

### Redis

    docker run -d --name music-redis ^
      -p 6379:6379 ^
      redis:7

### MinIO

    docker run -d --name music-minio ^
      -p 9000:9000 ^
      -p 9001:9001 ^
      -e MINIO_ROOT_USER=minioadmin ^
      -e MINIO_ROOT_PASSWORD=minioadmin ^
      minio/minio server /data --console-address ":9001"

MinIO 控制台：http://localhost:9001

------------------------------------------------------------------------

## 3. 初始化 Bucket

进入 MinIO Console 创建 bucket：

    music

------------------------------------------------------------------------

## 4. 启动后端 music-server

进入目录：

    cd music-server
    mvn spring-boot:run

健康检查：

    curl http://localhost:8080/test/ping

------------------------------------------------------------------------

## 5. 启动管理端 music-admin

    cd music-admin
    npm install
    npm run dev

------------------------------------------------------------------------

## 6. 启动用户端 music-client

    cd music-client
    npm install
    npm run dev

------------------------------------------------------------------------

## 7. PowerShell UTF8 请求工具

    chcp 65001

    function Invoke-JsonUtf8($method,$url,$obj,$token){
      $json = $obj | ConvertTo-Json -Depth 10
      $bytes = [System.Text.Encoding]::UTF8.GetBytes($json)
      $headers = @{}
      if ($token) { $headers["Authorization"] = "Bearer $token" }
      Invoke-RestMethod -Method $method `
        -Uri $url `
        -Headers $headers `
        -ContentType "application/json; charset=utf-8" `
        -Body $bytes
    }

------------------------------------------------------------------------

## 8. 常见问题

### MinIO 403

-   私有桶必须用 presigned URL

### SignatureDoesNotMatch

-   不要用 curl -I 测试
-   使用：

```{=html}
<!-- -->
```
    curl -L -H "Range: bytes=0-1023" "<playUrl>"

------------------------------------------------------------------------

## 9. 停止容器

    docker rm -f music-mysql music-redis music-minio
