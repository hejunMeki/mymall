<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2017/9/25 0025
  Time: 下午 7:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>上传图片</title>
</head>
<body>
<form name="form2" action="/manage/product/upload.do" method="post"  enctype="multipart/form-data">
    <input type="file" name="file">
    <input type="submit" value="upload"/>
</form>



<hr>
<form name="form2" action="/manage/product/richtext_img_upload.do" method="post"  enctype="multipart/form-data">
    <input type="file" name="file">
    <input type="submit" value="upload"/>
</form>
</body>
</html>
