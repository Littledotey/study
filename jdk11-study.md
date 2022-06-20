## 为什么要将项目的jdk从低版本升级到高版本，目的是为了什么？

升级前需要先了解新旧版本的变化【例如从jdk1.8->jdk11】

java9新特性
https://docs.oracle.com/javase/9/whatsnew/toc.htm#JSNEW-GUID-C23AFD78-C777-460B-8ACE-58BE5EA681F6

java10新特性
http://openjdk.java.net/projects/jdk/10/

java11新特性
http://openjdk.java.net/projects/jdk/11/

### java开发者在项目中需要进行的改动点

1.本地项目运行环境修改点
*maven中指定编译和运行的java版本
<properties>
    <java.version>11</java.version>
    <maven.compiler.target>${java.version}</maven.compiler.target>
    <maven.compiler.source>${java.version}</maven.compiler.source>
</properties>

*添加jdk.tools依赖，模块依赖化项
<dependency>
    <groupId>jdk.tools</groupId>
    <artifactId>jdk.tools</artifactId>
    <version>1.7</version>
</dependency>

*添加jaxb-api依赖，模块依赖化项
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.0</version>
</dependency>

*移除依赖spring-boot-devtools，因为此依赖是用于热部署的，使用了一些高级特性，在jdk11中已经不支持了
<!--<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>-->

*升级apache的commons-langs，这个工具型依赖在3.9之后支持jdk11
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.9</version>
</dependency>

*还有一些依赖化模块这边没有用到，如果在实际操作中出现了NoClassDefFoundError，NoSuchFieldError错误可以将报错信息通过搜索引擎查出位于哪个依赖，加上就好


2.docker构建部署时的改动点
*构建打包使用jdk版本
baseline.jdk=xxjdk-11xxx

*为镜像中添加jdk11环境
RUN sudo rpm -ivh --nodeps "http://yum.tbsite.net/taobao/7/x86_64/current/ajdk11/ajdk11-11.0.8.8-20200929135541.alios7.x86_64.rpm"

*升级tomcat版本，因为这个项目比较老，需要独立配tomcat，如果使用的是springboot可以省略这一步
RUN sudo rpm -ivh --nodeps "http://yum.tbsite.net/taobao/7/noarch/current/taobao-tomcat/taobao-tomcat-7.0.108-1925027.noarch.rpm"

*把新安装的jdk11添加到环境变量中
export JAVA_HOME=/opt/taobao/java/xxjdk-11xxx

*开启zgc。如果原先有使用别的垃圾回收器调优的参数，记得去除不兼容的
## 追加到jvm参数中，CATALINA_OPTS需要换成你的jvm参数变量
CATALINA_OPTS="${CATALINA_OPTS} -Xloggc:${MIDDLEWARE_LOGS}/gc.log -XX:+PrintGCDetails"
CATALINA_OPTS="${CATALINA_OPTS} -XX:+UnlockExperimentalVMOptions"
CATALINA_OPTS="${CATALINA_OPTS} -XX:+UseZGC"

*修改cpp的内存检查，因为本项目使用了tensorflow，tensorflow使用的内存检查的方式对于zgc来说是不准确的，会误报OOM，详情请见：https://github.com/bytedeco/javacpp/issues/474
 解决方式可以是增加参数或升级tensorflow，因为确认tensorflow的模型只载入一次，并且内存绝对够用。
 为了减少影响面，选用增加jvm参数的形式，如果没有使用bytedeco可以忽略
 ## 追加到jvm参数中，CATALINA_OPTS需要换成你的jvm参数变量
 ## 关闭cpp的内存检查 有风险 因为zgc的内存是不准确的 不关闭会引起错误的内存溢出错误
 ## 关闭后需要保证tensorflow的模型大小永远不会超过内存 否则会导致应用崩溃
 CATALINA_OPTS="${CATALINA_OPTS} -Dorg.bytedeco.javacpp.maxBytes=0 -Dorg.bytedeco.javacpp.maxPhysicalBytes=0"


其他注意点
jdk11的zgc仅支持linux，
jdk14的zgc增加了对windows，mac OS的支持

