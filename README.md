### (一)FastDFS入门

分布式文件系统 DFS(Distributed File System)

FastDFS是淘宝资深架构师`余庆`老师主导开源的一个分布式文件系统，C语言，适应于中小企业。

主要有 `Tracker`（管理）和 `Storage`（存储）。

访问路径：组名/虚拟盘符{M00/00/02}/文件名

![img](https://img2018.cnblogs.com/blog/1243133/201904/1243133-20190425161356834-1310906836.png)



对于Java客户端，老师已经提供了API https://github.com/happyfish100/fastdfs-client

---

#### 1.传统文件系统与分布式文件系统区别

###### **传统文件系统**

![image-20201201145944699](https://gitee.com//mayating/BlogImage/raw/master/img/20201201145948.png)

缺点：

```
1.磁盘访问压力很大。如果用户数量多，IO操作比较多，对磁盘访问压力很大。
2.数据丢失。如果磁盘发生故障，会造成数据丢失。
3.存储容量有限。
```



###### **分布式文件系统**

![image-20201201150506873](https://gitee.com//mayating/BlogImage/raw/master/img/20201201150508.png)

优点：

```
1.解决传统方式的单点故障问题。如果某一个节点出现故障还有其他的节点来读取和写入文件。
2.提供数据备份。避免因磁盘损坏导致文件丢失。
3.提供扩容机制。无限增加文件存放的空间上限。
```



#### 2.FastDFS简介

```
阿里巴巴开源、轻量级、分布式文件系统
简单、灵活、高效
c语言开发
```

FastDFS 对文件进行管理，功能包括：

- 文件存储
- 文件同步
- 文件访问（文件上传、文件下载、文件删除）等

解决了大容量文件存储的问题，特别适合以文件作为载体的在线服务，如相册网站、文档网站、图片网站、视频网站等。

FastDFS充分考虑了 `冗余备份`、`线性扩容` 等机制，并注重 `高可用`、`高性能` 等指标，使用FastDFS 很容易搭建一套高性能的`文件服务器集群`，提供文件上传、下载等服务。

FastDFS 代码托管在GitHub上：https://github.com/happyfish100/fastdfs



#### 3.FastDFS整体架构

FastDFS文件系统由两大部分构成，一个是`客户端`、一个是`服务端`

客户端

```
客户端通常指我们的程序。
比如我们的Java程序去连接FastDFS、操作FastDFS,那我们的Java程序就是一个客户端。
FastDFS提供专有API访问，目前提供了C、Java 和 PHP几种编程语言的API，用来访问 FastDFS文件系统。

# 注意：提供的API也是个Java程序，需要本地编译后打包到本地仓库，再在maven项目中引用该依赖
```

服务端：由两个部分构成，一个是跟踪器（tracker）、一个是存储节点（storage）

```
跟踪器（tracker）主要做调度工作，在内存中记录集群中存储节点 storage 的状态信息。是前端 Client 和 后端 存储节点 storage 的枢纽。
因为相关信息全部在内存中，Tracker server的性能非常高，一个较大的集群（比如200个group）中有3台就足够了。

存储节点（storage）用于存储文件，包括文件和文件属性（meta data）都保存到存储服务器磁盘上，完成文件管理的所有功能：文件存储、文件同步和提供文件访问等。
```

#### 4.FastDFS线上使用者

```
UC、支付宝、淘淘搜等
```



---

### （二）Linux搭建FastDFS文件服务器

参见文章：

1.Linux下搭建FastFDS文件服务器 v2-mine.md

2.Linux下搭建FastDFS文件服务器 v1.md

集群搭建参见文章：

1.FastDFS分布式文件系统集群环境搭建-操作步骤手册.txt

2.FastDFS讲义.docx

### （三）FastDFS在Java项目中的应用

参见：fastdfs-java



### （四）FastDFS在web项目中的应用

参见：fastdfs-web

在虚拟机CentOS系统中启动FastDFS：

```shell
fdfs_trackerd /etc/fdfs/tracker.conf start
fdfs_storaged /etc/fdfs/storage.conf start
/usr/local/nginx_fdfs/sbin/nginx -c /usr/local/nginx_fdfs/conf/nginx.conf
```

###### 

#### 1.需求

对P2P项目合同进行管理，在WEB项目中实现对文件的上传下载和删除操作

#### 2.目标

```
实现对pdf文件上传、下载、删除
熟练 SpringBoot +  thymeleaf
```

#### 3.数据库环境搭建

###### 1.创建数据库fastdfs

###### 2.在该库下创建creditor_info表

```sql
CREATE TABLE `creditor_info` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
`realName` varchar(35) DEFAULT NULL COMMENT '债权借款人姓名',
`idCard` varchar(18) DEFAULT NULL COMMENT '债权借款人身份证',
`address` varchar(150) DEFAULT NULL COMMENT '债权借款人地址',
`sex` int(1) DEFAULT NULL COMMENT '1男2女',
`phone` varchar(11) DEFAULT NULL COMMENT '债权借款人电话',
`money` decimal(10,2) DEFAULT NULL COMMENT '债权借款人借款金额',
`groupName` varchar(10) DEFAULT NULL COMMENT '债权合同所在组',
`remoteFilePath` varchar(150) DEFAULT NULL COMMENT '债权合同所在路径',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

#### 4.开发环境搭建

1.创建SpringBoot项目10-fastdfs-web,添加Web和Thymeleaf依赖

2.在pom.xml文件中添加 Mybatis依赖及MySQL依赖

3.在pom.xml文件中添加resources，指定编译的位置

4.在SpringBoot主配置文件 application.properties中添添加数据库配置信息

5.使用MyBatis反向工程，生成实体类及mapper映射

```
A.在pom.xml文件中添加反向工程插件
B.新建GeneratorMapper.xml
C.双击生成
```

6.创建相关的包和类

#### 5.功能实现

##### 5.1功能实现-显示所有债权信息

![image-20201202194258225](https://gitee.com//mayating/BlogImage/raw/master/img/20201202194324.png)

##### 5.2功能实现-为某一债权合同上传文件

![image-20201202201612370](https://gitee.com//mayating/BlogImage/raw/master/img/20201202201617.png)

选择文件，点击提交，弹出提示：

![image-20201202201754042](https://gitee.com//mayating/BlogImage/raw/master/img/20201202201755.png)

点击确定，跳转回首页，页面数据刷新，显示“下载、删除”功能：

![image-20201202201846043](https://gitee.com//mayating/BlogImage/raw/master/img/20201202201847.png)

FastDFS文件服务器可查看上传成功的文件：

![image-20201202202129110](https://gitee.com//mayating/BlogImage/raw/master/img/20201202202130.png)

##### 5.3 功能实现-下载某一个债权合同

##### 5.4 功能实现-删除某一个债权合同，使用ajax实现异步删除

![image-20201202202351342](https://gitee.com//mayating/BlogImage/raw/master/img/20201202202352.png)

点击确定，下载、删除 功能消失，上传功能出现：

![image-20201202202506043](https://gitee.com//mayating/BlogImage/raw/master/img/20201202202509.png)

查看FastDFS文件服务器，文件已被删除：

![image-20201202202628332](https://gitee.com//mayating/BlogImage/raw/master/img/20201202202629.png)

##### 5.5 功能实现-弹层组建layer的使用

暂无



### （五）封装FastDFS进行文件操作的工具类

在FastDFSUtil类中将通用的功能进行封装，并提供上传、下载、删除文件的方法

实现步骤：

1.抽取获取StorageClient的方法

2.定义两个全局变量

3.抽取关闭资源的方法

4.改造文件上传的方法

5.改造文件下载的方法

6.改造文件删除的方法

7.测试

工具类位置：fastdfs-web\src\main\java\com\myt\fastdfs\utils\FastDFSUtil.java

