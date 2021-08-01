# vue-laser-backend in Java
The config file is in `src/resources/application.yml`. Rename the default `application.yml.sample` to `application.yml`, or it won't work. 

Import `sql/jiguang.sql` to MySQL/MariaDB. Check if the configuration is right and run it in Maven (Maybe?)

## Usage
Run the command in the project folder. Of course you need Maven
```bash
mvn spring-boot:run
```
## Packaging
Use `maven-assembly-plugin`

 See 
 - [maven-assembly 打包bin 使用start.sh 执行 springcloud 项目](https://blog.csdn.net/yt438936731/article/details/106401279)
 - [使用assembly 插件定制化打包项目](https://www.jianshu.com/p/64c880359864)

I barely know anything in Java. So I leave it to you. 