<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <title>Authentication failed</title>
</head>
<body>
<h1>Authentication failed ${status}</h1>
<hr>
<p><c:out value="${description}"/></p>
<p><c:out value="${time}"/></p>
</body>
</html>
