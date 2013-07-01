<%@ page import="sistematica.gim.infomobility.utils.*" %>
<%@ page import="sistematica.gim.infomobility.conf.*" %>
<html>
<body>
<h2>Infomobility.it index page</h2>
<%if(WebSettings.SENDMGR_ENABLE){ %>
<FORM>
<INPUT TYPE="BUTTON" VALUE="Statistics" ONCLICK="window.location.href='/Infomobility/stats.jsp'">
</FORM>
<%} %>
</body>
</html>
