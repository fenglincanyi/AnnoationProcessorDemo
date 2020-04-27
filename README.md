# Demo示例：
* 自动生成 JavaBean 的 get、set、toString 方法

http://www.jianshu.com/p/745655cb431a

https://juejin.im/post/5d35b8846fb9a07efd474a5a

## auto-service
正常情况下，手动处理：
向javac中注册自定义的处理器的步骤如下：

在src/main目录下创建resources/META-INF/services/javax.annotation.processing.Processor文件。
在javax.annotation.processing.Processor中写入自定义的Processor的全名，如果有过个Processor的话，每行写一个。

com.soulmate.processor.MyProcessor
复制代码这样，在javac编译时，在处理注解处理器的时候就会执行我们自定义的注解处理器。


google 解决手动问题：
只需要在自定义的注解处理器类上面添加@AutoService(Processor.class)这个注解就可以了

源码中的 com.google.auto.service.processor.AutoServiceProcessor.generateConfigFiles 实现了相关文件生成 

## javapoet
javapoet是square开源的一个java生成库，官网解释为：

