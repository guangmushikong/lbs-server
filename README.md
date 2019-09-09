# lbs-server
lbs地图引擎

  - linux环境启动
```
nohup java -jar lbs-server-1.0-SNAPSHOT.jar &
```
  - docker环境启动
```
cd lbs-server	#进入lbs-server目录
docker build -t lbs-server  .	#生成镜像
docker run -d -p 8080:8080 -v /home/dev/data:/home/dev/data  --net=host lbs-server	#启动镜像
docker logs -f -t --tail=10 1aed58273a56 #查看日志
```
