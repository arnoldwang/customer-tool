<%@ page import="com.dianping.taskcenter.utils.Beans" %>
<%@ page import="com.dianping.taskcenter.service.OfflineAlarmDealGroupService" %>
<%@ page import="com.dianping.taskcenter.serviceagent.PermissionServiceAgent" %>
<%--
  Created by IntelliJ IDEA.
  User: shenyoujun
  Date: 14/11/3
  Time: 下午8:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
    <%
        String option = request.getParameter("syncOption");
        if("syncSalesOrgnization".equals(option)){
            Beans.getBean(PermissionServiceAgent.class).syncSalesOrgnization();
        }
        if("syncDealAmounts".equals(option)){
            Beans.getBean(OfflineAlarmDealGroupService.class).syncDealAmounts();
        }else{
            out.println("<script>alert('no－option')</script>");
        }
            out.println("<script>alert('success')</script>");
    %>
</head>
<body>

<form action="/backdoor.jsp" method="get">
    <select name="syncOption">
        <option value="syncSalesOrgnization" >同步销售组织架构</option>
        <option value="syncDealAmounts">同步销售金额</option>
    </select>
   <input type="submit" value="提交">
</form>

</body>
</html>
