## 用FastDFS搭建文件管理系统

 参考链接：https://www.cnblogs.com/chiangchou/p/fastdfs.html
 参考视频：https://www.bilibili.com/video/av15286388/?p=1

```
二、安装FastDFS环境
0、前言
1、安装 libfastcommon
2、安装FastDFS
3、配置FastDFS跟踪器(Tracker)
4、配置 FastDFS 存储 (Storage)
5、文件上传测试
三、安装Nginx
1、安装nginx所需环境　　
2、安装Nginx
3、访问文件
四、FastDFS 配置 Nginx 模块
1、安装配置Nginx模块
12345678910111213
```

### 安装FastDFS环境

#### **0.前言**

 操作环境：CentOS7 X64，以下操作都是单机环境。服务器：106.13.64.22
 我把fastdfs所有的安装包下载到/usr/local/software/ 下，解压到/usr/local/fast目录下

####  **1.安装环境所需要的依赖**

```
yum install make cmake gcc gcc-c++
1
```

==安装包已在百度网盘共享：==
 链接：https://pan.baidu.com/s/14XPBpU1ZaMnVjar0LDLBdQ
 提取码：6ccw

上传各个安装包到/usr/local/software/目录下。
 创建software目录

```
cd usr/local
mkdir software
```

通过xftp上传安装包到该目录下，如下图所示：
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031185404256.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZseV83Nw==,size_16,color_FFFFFF,t_70)

####  **2.安装 libfastcommon**

 切换到/usr/local/software/目录，
 **① 解压**
 解压 `libfastcommon` 到/usr/local/fast 目录下

```
tar -zxvf libfastcommonV1.0.7.tar.gz  -C /usr/local/fast 
cd /usr/local/fast/libfastcommon-1.0.7/
```

**② 编译、安装**

```
./make.sh
 ./make.sh install
```

**③libfastcommon.so 安装到了/usr/lib64/libfastcommon.so，但是FastDFS主程序设置的lib目录是/usr/local/lib，所以需要创建软链接。**

```
ln -s /usr/lib64/libfastcommon.so /usr/local/lib/libfastcommon.so
ln -s /usr/lib64/libfastcommon.so /usr/lib/libfastcommon.so
ln -s /usr/lib64/libfdfsclient.so /usr/local/lib/libfdfsclient.so
ln -s /usr/lib64/libfdfsclient.so /usr/lib/libfdfsclient.so 
```

#### **3.安装FastDFS**

 解压`FastDFS`到/usr/local/fast 目录下

```
tar -zxvf FastDFS_v5.05.tar.gz  -C /usr/local/fast 
cd /usr/local/fast/FastDFS
```

**② 编译、安装**

```
./make.sh
 ./make.sh install
```

**③ 默认安装方式安装后的相应文件与目录（此步骤无需执行命令）**
 **A、服务脚本：**

```
/etc/init.d/fdfs_storaged
/etc/init.d/fdfs_tracker
```

**B、配置文件（这三个是作者给的样例配置文件）**

```
/etc/fdfs/client.conf.sample
/etc/fdfs/storage.conf.sample
/etc/fdfs/tracker.conf.sample
```

**C、命令工具在 /usr/bin/ 目录下：**

```
fdfs_appender_test
fdfs_appender_test1
fdfs_append_file
fdfs_crc32
fdfs_delete_file
fdfs_download_file
fdfs_file_info
fdfs_monitor
fdfs_storaged
fdfs_test
fdfs_test1
fdfs_trackerd
fdfs_upload_appender
fdfs_upload_file
stop.sh
restart.sh
```

**④ FastDFS 服务脚本设置的 bin 目录是 /usr/local/bin， 但实际命令安装在 /usr/bin/ 下。**
 **两种方式：**
 》方式一是修改FastDFS 服务脚本中相应的命令路径，也就是把 /etc/init.d/fdfs_storaged 和 /etc/init.d/fdfs_tracker 两个脚本中的 /usr/local/bin 修改成 /usr/bin。

```
# vim fdfs_trackerd
使用查找替换命令进统一修改:%s+/usr/local/bin+/usr/bin
# vim fdfs_storaged
使用查找替换命令进统一修改:%s+/usr/local/bin+/usr/bin
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031185604135.png)

》 方式二是建立 /usr/bin 到 /usr/local/bin 的软链接。

```
 ln -s /usr/bin/fdfs_trackerd   /usr/local/bin
 ln -s /usr/bin/fdfs_storaged   /usr/local/bin
 ln -s /usr/bin/stop.sh         /usr/local/bin
 ln -s /usr/bin/restart.sh      /usr/local/bin
```

注意：以上两种方式，我采用的方式一。

####  **4.配置FastDFS跟踪器(Tracker)**

 **①进入 /etc/fdfs，复制 FastDFS 跟踪器样例配置文件** tracker.conf.sample，并重命名为 tracker.conf。

```
# cd /etc/fdfs
# cp tracker.conf.sample tracker.conf
# vim tracker.conf
```

**②编辑tracker.conf ，标红的需要修改下，其它的默认即可。**

```
# 配置文件是否不生效，false 为生效
disabled=false

# 提供服务的端口
port=22122

# Tracker 数据和日志目录地址(根目录必须存在,子目录会自动创建)
base_path=/ljzsg/fastdfs/tracker

# HTTP 服务端口，需要与后面nginx的listen监听端口保持一致
http.server_port=80
```

**③创建tracker基础数据目录，即base_path对应的目录**

```
mkdir -p /ljzsg/fastdfs/tracker
```

注意：mkdir命令。使用 -p 参数来创建多级文件夹，例如： test01/test02/test03
 **④防火墙中打开跟踪端口（默认的22122）**
 Centons7 X64位系统默认的防火墙是firewall

```
首先查看防火墙状态：（在防火墙开启的状态下添加端口）
firewall-cmd --state
```

```
开端口命令：
firewall-cmd --zone=public --add-port=22122/tcp --permanent

重启防火墙：
systemctl restart firewalld.service
```

此处可以参考莉婶的另外一个博文链接：
 https://blog.csdn.net/fly_77/article/details/100806274
 **⑤启动Tracker**
 初次成功启动，会在 /ljzsg/fdfsdfs/tracker/ (配置的base_path)下创建 data、logs 两个目录。

```
可以用这种方式启动
# /etc/init.d/fdfs_trackerd start

也可以用这种方式启动，前提是上面创建了软链接，后面都用这种方式
# service fdfs_trackerd start
```

上述两种方式都可以启动。
 查看 FastDFS Tracker 是否已成功启动 ，22122端口正在被监听，则算是Tracker服务安装成功。

```
netstat -unltp|grep fdfs
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031185751511.png)
 关闭Tracker命令：

```
service fdfs_trackerd stop
```

**⑥设置Tracker开机启动**

```
 chkconfig fdfs_trackerd on

或者：
# vim /etc/rc.d/rc.local
加入配置：
/etc/init.d/fdfs_trackerd start 
```

**⑦ tracker server 目录及文件结构**
 Tracker服务启动成功后，会在base_path下创建data、logs两个目录。目录结构如下：

```
${base_path}
  |__data
  |   |__storage_groups.dat：存储分组信息
  |   |__storage_servers.dat：存储服务器列表
  |__logs
  |   |__trackerd.log： tracker server 日志文件 
```

#### **5.配置 FastDFS 存储 (Storage)**

 **①进入 /etc/fdfs 目录，复制 FastDFS 存储器样例配置文件** **storage.conf.sample，并重命名为 storage.conf**
 **② storage.conf.sample，并重命名为 storage.conf。**

```
# cd /etc/fdfs
# cp storage.conf.sample storage.conf
# vim storage.conf
```

**③ 编辑storage.conf ，标红的需要修改下，其它的默认即可。**

```
# 配置文件是否不生效，false 为生效
disabled=false 

# 指定此 storage server 所在 组(卷)
group_name=group1

# storage server 服务端口
port=23000

# 心跳间隔时间，单位为秒 (这里是指主动向 tracker server 发送心跳)
heart_beat_interval=30

# Storage 数据和日志目录地址(根目录必须存在，子目录会自动生成)
base_path=/ljzsg/fastdfs/storage

# 存放文件时 storage server 支持多个路径。这里配置存放文件的基路径数目，通常只配一个目录。
store_path_count=1


# 逐一配置 store_path_count 个路径，索引号基于 0。
# 如果不配置 store_path0，那它就和 base_path 对应的路径一样。
store_path0=/ljzsg/fastdfs/file

# FastDFS 存储文件时，采用了两级目录。这里配置存放文件的目录个数。 
# 如果本参数只为 N（如： 256），那么 storage server 在初次运行时，会在 store_path 下自动创建 N * N 个存放文件的子目录。
subdir_count_per_path=256

# tracker_server 的列表 ，会主动连接 tracker_server
# 有多个 tracker server 时，每个 tracker server 写一行
此处需要修改为tracker_server所在的ip地址和端口
tracker_server=106.13.64.22:22122

# 允许系统同步的时间段 (默认是全天) 。一般用于避免高峰同步产生一些问题而设定。
sync_start_time=00:00
sync_end_time=23:59
# 访问端口 需要与后面nginx的listen监听端口保持一致
http.server_port=8080
```

**④ 创建storage基础数据目录，即base_path对应的目录**

```
mkdir -p /ljzsg/fastdfs/storage
```

注意：mkdir命令。使用 -p 参数来创建多级文件夹，例如： test01/test02/test03
 **⑤ 防火墙中打开跟踪端口（默认的23000）**
 Centons7 X64位系统默认的防火墙是firewall

```
首先查看防火墙状态：（在防火墙开启的状态下添加端口）
firewall-cmd --state

开端口命令：
firewall-cmd --zone=public --add-port=23000/tcp --permanent

重启防火墙：
systemctl restart firewalld.service
```

此处可以参考莉婶的另外一个博文链接：
 https://blog.csdn.net/fly_77/article/details/100806274
 **⑥ 启动Storage**
 初次成功启动，会在 /ljzsg/fdfsdfs/storage/ (配置的base_path)下创建 data、logs 两个目录。

```
可以用这种方式启动
# /etc/init.d/fdfs_storaged start

也可以用这种方式，后面都用这种
# service fdfs_storaged start
```

上述两种方式都可以启动。
 查看 Storage 是否成功启动，23000 端口正在被监听，就算 Storage 启动成功。

```
netstat -unltp|grep fdfs
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031190104359.png)
 关闭Storage命令：

```
# service fdfs_storaged stop
```

查看Storage和Tracker是否在通信：

```
/usr/bin/fdfs_monitor /etc/fdfs/storage.conf
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031190131252.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZseV83Nw==,size_16,color_FFFFFF,t_70)
 **⑦设置 Storage 开机启动**

```
# chkconfig fdfs_storaged on
或者：
# vim /etc/rc.d/rc.local
加入配置：
/etc/init.d/fdfs_storaged start
```

**⑧ Storage 目录**
 同 Tracker，Storage 启动成功后，在base_path 下创建了data、logs目录，记录着 Storage Server 的信息。
 在 store_path0 目录下，创建了N*N个子目录：
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031190155322.png)

**6.文件上传测试**
 **①修改 Tracker 服务器中的客户端配置文件**

```
# cd /etc/fdfs
# cp client.conf.sample client.conf
# vim client.conf
```

修改如下配置即可，其它默认。

```
# Client 的数据和日志目录
base_path=/ljzsg/fastdfs/client

# Tracker端口
tracker_server=106.13.64.22:22122
```

创建目录

```
cd /ljzsg/fastdfs/client
```

**②上传测试**
 在linux内部执行如下命令上传 /soft/aa.jpg图片

```
# /usr/bin/fdfs_upload_file /etc/fdfs/client.conf /soft/aa.jpg
```

上传成功后返回文件ID号：group1/M00/00/00/rBAABF26VpiAWdrVAAE4oMCn_Qo037.jpg

返回的文件ID由 `group、存储目录、两级子目录、fileid、文件后缀名（由客户端指定，主要用于区分文件类型）`拼接而成。
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031190308594.png)

###  **安装Nginx**

 上面将文件上传成功了，但我们无法下载。因此安装Nginx作为服务器以支持`Http方式`访问文件。同时，后面安装FastDFS的Nginx模块也需要Nginx环境。
 Nginx只需要安装到StorageServer所在的服务器即可，用于访问文件。我这里由于是单机，TrackerServer和StorageServer在一台服务器上。

**1、安装nginx所需环境**　
 **①安装nginx所需的依赖**

```
yum install pcre
yum install pcre-devel
yum install zlib
yum install zlib-devel
```

**2、安装Nginx**
 将nginx-1.17.3.tar.gz放到/soft目录
 **② 解压**

```
# tar -zxvf nginx-1.17.3.tar.gz
# cd nginx-1.17.3
```

**③使用默认配置**

```
# ./configure
```

**④编译、安装**

```
# make && make install
```

**⑤启动nginx**

```
# cd /usr/local/nginx/sbin/
# ./nginx 

其它命令
# ./nginx -s stop
# ./nginx -s quit
# ./nginx -s reload
```

**⑥设置开机启动**

```
# vim /etc/rc.local
添加一行：
/usr/local/nginx/sbin/nginx

# 设置执行权限
# chmod 755 rc.local
```

**⑦查看nginx的版本及模块**

```
/usr/local/nginx/sbin/nginx -V
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031190627417.png)
 **⑧ 防火墙中打开Nginx端口（默认的 80）**
 添加后就能在本机使用80端口访问了。

```
首先查看防火墙状态：（在防火墙开启的状态下添加端口）
firewall-cmd --state

开端口命令：
firewall-cmd --zone=public --add-port=80/tcp --permanent

重启防火墙：
systemctl restart firewalld.service
```

**3、访问文件**
 简单的测试访问文件
 **①修改nginx.conf**

```
# vim /usr/local/nginx/conf/nginx.conf

添加如下行，将 /group1/M00 映射到 /ljzsg/fastdfs/file/data
location /group1/M00 {
    alias /ljzsg/fastdfs/file/data;
}
```

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031190706789.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZseV83Nw==,size_16,color_FFFFFF,t_70)
 **② 在浏览器访问之前上传的图片、成功。**
 http://106.13.64.22/group1/M00/00/00/rBAABF26VpiAWdrVAAE4oMCn_Qo037.jpg

**四、FastDFS 配置 Nginx 模块**
 **1、安装配置Nginx模块（在 Storage 所在的服务器安装nginx模块）
 ① fastdfs-nginx-module 模块说明**
 　　FastDFS 通过 Tracker 服务器，将文件放在 Storage 服务器存储， 但是同组存储服务器之间需要进行文件复制， 有同步延迟的问题。
 　　假设 Tracker 服务器将文件上传到了 192.168.51.128，上传成功后文件 ID已经返回给客户端。
 　　此时 FastDFS 存储集群机制会将这个文件同步到同组存储 192.168.51.129，在文件还没有复制完成的情况下，客户端如果用这个文件 ID 在 192.168.51.129 上取文件,就会出现文件无法访问的错误。
 　　而 `fastdfs-nginx-module` 可以重定向文件链接到源服务器取文件，避免客户端由于复制延迟导致的文件无法访问错误。
 **② 下载 fastdfs-nginx-module、解压**
 解压fastdfs-nginx-module_v1.16.tar.gz到上级目录的fast目录下
 tar -zxvf fastdfs-nginx-module_v1.16.tar.gz -C …/fast/
 cd /usr/local/fast/fastdfs-nginx-module/src/目录下，修改配置文件
 这里必须修改配置文件
 vim config
 将/usr/local/include修改为/usr/include/
 如下图：
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031190729592.png)
 **③ 重新安装nginx并在nginx中配置fast-module**
 在nginx的解压目录下执行
 删除/usr/local/nginx
 rm -rf /usr/local/nginx
 重新解压nginx的压缩包
 tar -zxvf nginx-1.17.3.tar.gz
 解压后，cd nginx-1.17.3
 重新安装nginx所依赖的压缩包
 yum install pcre
 yum install pcre-devel
 yum install zlib
 yum install zlib-devel

安装好了nginx所必需的的依赖包之后，在nginx中增加模块fast-module

./configure --add-module=/usr/local/fast/fastdfs-nginx-module/src

make && make install
 **④ 查看Nginx的模块**
 /usr/local/nginx/sbin/nginx -V
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031190750862.png)
 **⑤ 复制 fastdfs-nginx-module 源码中的配置文件到/etc/fdfs 目录，** 并修改
 cd /usr/local/fast/fastdfs-nginx-module/src/
 cp mod_fastdfs.conf /etc/fdfs/
 修改如下配置，其它默认
 vim /etc/fdfs/mod_fastdfs.conf

```
# 连接超时时间
connect_timeout=10

# Tracker Server
tracker_server=file.ljzsg.com:22122

# StorageServer 默认端口
storage_server_port=23000

# 如果文件ID的uri中包含/group**，则要设置为true
url_have_group_name = true

# Storage 配置的store_path0路径，必须和storage.conf中的一致
store_path0=/ljzsg/fastdfs/file
```

**⑥ 复制 FastDFS 的部分配置文件到/etc/fdfs 目录**
 切换到FastDFS 的配置目录下：

```
cd /usr/local/fast/FastDFS/conf
```

拷贝配置文件到/etc/fdfs 目录

```
 cp anti-steal.jpg http.conf  mime.types /etc/fdfs/
```

**⑦ 在/ljzsg/fastdfs/file 文件存储目录下创建软连接，将其链接到实际存放数据的目录**

```
ln -s /ljzsg/fastdfs/file/data/ /ljzsg/fastdfs/file/data/M00 
```

**⑧ 配置nginx，修改nginx.conf**

```
vim /usr/local/nginx/conf/nginx.conf
```

修改配置，其它的默认
 在80端口下添加fastdfs-nginx模块

```
location ~/group([0-9])/M00 {
    ngx_fastdfs_module;
}
```

如下图所示：
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/2019103119091130.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZseV83Nw==,size_16,color_FFFFFF,t_70)
 注意：
 　　listen 8080 端口值是要与 /etc/fdfs/storage.conf 中的 http.server_port=8080 (前面改成8080了)相对应。如果改成其它端口，则需要统一，同时在防火墙中打开该端口。
 　　location 的配置，如果有多个group则配置location ~/group([0-9])/M00 ，没有则不用配group。

**⑨ 启动nginx，执行如下命令**

```
#  /usr/local/nginx/sbin/nginx
```

**⑩ 在地址栏访问。**
 能下载文件就算安装成功。注意和第三点中直接使用nginx路由访问不同的是，这里配置 fastdfs-nginx-module 模块，可以重定向文件链接到源服务器取文件。
 http://106.13.64.22:8080/group1/M00/00/00/rBAABF26VpiAWdrVAAE4oMCn_Qo037.jpg
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/20191031190953735.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZseV83Nw==,size_16,color_FFFFFF,t_70)

注意：上述访问的ip必须是storage服务器所在的ip地址。
 最终部署结构图(盗的图)：可以按照下面的结构搭建环境。

