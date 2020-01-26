# 创建项目

关联github

1. file里添加github账号
2. 创建远程仓库
3. 创建本地仓库并提交
4. alt f12   git pull origin master --allow-unrelated-histories   把readme拉下来  或者 git pull --rebase origin master 
5. push





# 数据库  首页展示

mysql  +  druid  + mybatis



![image-20191223164221822](README.assets/image-20191223164221822.png)

写建表sql

druid数据源    

IDEA自动生成实体类

集合mybatis（配置 编写DAO层）



写service  control    修改thymeleaf模板



# 登录注册

webjars  引入jquery等

首先是基本的注册登录功能

注册前端验证合法性 ajax验证用户名是否重复等

然后登录注册同理  成功之后 生成一个token（JWT）  用cookie的方式进行存储

注意：chrome对setMaxAge（-1）的cookie还是会保存 因此设置1s

然后用一个threadlocal保存所有用户登录信息，方便多线程使用



然后就是拦截器功能

第一个拦截器读取cookie并验证  如果有用户信息就存储

在视图渲染前 存储用户信息方便视图读取 注意这里还要判断modelAndView是否为null  因为有的responsebody的控制器是不返回视图的

最后渲染结束进行清空

第二个拦截器对需要登录的页面验证是否有用户信息  如果没登录 保存要访问的地址 返回登录页面  登录后 如果要返回的地址不为空 再继续访问



然后就是前端页面 要存储这个继续访问地址   并且根据是否有user进行展示



最后登出功能  要删除cookie 并删除存储的用户信息





# 发布问题





# 多线程





