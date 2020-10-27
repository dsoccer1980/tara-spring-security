<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Authentication success</title>
</head>
<body>
<h1>Authentication success</h1>
<hr>
<p>Personal id: <c:out value="${personalId}"/></p>
<p>First name: <c:out value="${firstName}"/></p>
<p>Last name: <c:out value="${lastName}"/></p>
<p>Date of birth: <c:out value="${dateOfBirth}"/></p>
<p>Authentication Method: <c:out value="${authMethod}"/></p>
<hr>
<a href='${pageContext.request.contextPath}/'>Back to login page</a>
</body>
</html>