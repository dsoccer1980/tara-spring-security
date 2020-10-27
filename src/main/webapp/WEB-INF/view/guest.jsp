<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Guest</title>
</head>
<body>
<h1>Guest Authentication success</h1>
<hr>
<p>Personal id: <c:out value="${personalId}"/></p>
<hr>
<a href='${pageContext.request.contextPath}/'>Back to login page</a>
</body>
</html>