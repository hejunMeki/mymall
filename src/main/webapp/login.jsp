<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/9/14 0014
  Time: 下午 3:41
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录</title>
</head>
<body>
        <form method="post" action="/user/login.do">
            用户名  <input type="text" name="username"/>
            密码    <<input type="password" name="password">
            <input type="submit" value="login" />
        </form>
</body>
</html>
