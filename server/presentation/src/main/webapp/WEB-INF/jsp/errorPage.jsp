<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Error Page</title>
    <style>
    * {
        font-family: questrialregular;
      }
      .license-expired-page {
        background-color: #e2f2fb;
        text-align: center;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        height: 100vh;
      }
      .error-message {
        font-size: 16px;
        font-weight: bold;
      }
      .renew-now-button {
        background-color: #1890ff;
        color: #ffffff;
        padding: 10px;
        margin: 10px;
      }
      .license-error {
        background-color: #f2dede;
        padding: 20px 50px;
        font-weight: bold;
      }
    </style>
  </head>
  <body>
    <div class="license-expired-page">
      <p class="error-message">Oops!</p>
      <p>
        <img src="images/hi-license-expired/pencil.png" />
      </p>
      <p class="error-message">An error has occurred. Please see your system administrator</p>
      <a href="https://www.helicalinsight.com/contact-us/" target="_blank" class="renew-now-button">
        RENEW LICENSE
      </a>
      <p class="license-error">
                    <c:if test="${!empty message}">
                
                    <c:out value="${message}" />
                
            </c:if>

            <c:if test="${!empty requestScope.errorMessage}">
               
                    <c:out value="${requestScope.errorMessage}" />
               
            </c:if>
      </p>
    </div>
  </body>
</html>
