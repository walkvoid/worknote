### Docker的安装
#### 安装
#### 启动
#### 镜像管理
- 查看本地已下载的镜像:
``` txt
docker images
```
- 搜索dockerhub上的镜像
``` txt
docker search mysql
```
- 下载dockerhub上的镜像
``` txt
docker pull mysql:8.0.32
```
- 删除本地已下载的镜像
``` txt
docker rmi {IMAGE ID}
#可以不用完整的image id， 前缀即可
```
#### 容器管理
- 创建容器
``` txt
docker run -i -t -d --name={name} {IMAGE NAME}:{IMAGE TAG} \
-p {localPort}:{containerPort}
/bin/bash
# -i: 以交互模式创建
# -t: 分配一个终端
# -d: 后台守护模式运行
# /bin/bash: 创建容器成功后执行的指令
```
- 查看容器
``` txt
docker ps -a
```
- 删除容器
``` txt
docker rm {容器id或者容器名称}
```

- 容器与宿主机文件移动
``` txt
docker rm {容器id或者容器名称}
```

- 常见软件的安装脚本
``` 
# 安装mysql
docker run -id --name=mysql-8.0.32 \
-p 3306:3306 \
-v ~/dockervolumes/mysql-8.0.32/conf/my.cnf:/etc/my.cnf \
-v ~/dockervolumes/mysql-8.0.32/log:/var/log \
-v ~/dockervolumes/mysql-8.0.32/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=Aa@123456 \
mysql:8.0.32 
#如果要远程链接，还要授权
alter user 'root'@'%' identified with mysql_native_password by 'Aa@123456';
#刷新刚授权的配置
flush privileges;
# /Users/jiangjunqing/
```

```
#安装redis命令:
docker run -id --name=redis4 -p 6379:6379 \
-v ~/software/docker/volumes/redis-4.0.6:/data/redis \
redis:4.0.6 \
redis-server /data/redis/redis.conf \
# redis-server /data/redis/redis.conf 这一行用来制定redis使用配置文件启动,注意需要将daemonize no而不是yes,不然会启动不起来
```

 
