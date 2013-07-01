<%@ page import="sistematica.gim.infomobility.utils.*" %>
<%@ page import="sistematica.gim.infomobility.conf.*" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%
Long positions = -1L;
Long files = -1L;

Loader loader = Loader.getInstance();

positions = loader.getPositions();
files = loader.getFiles();

Long timestampLastRun = loader.getTimestlastrun();
Long diff = -1L;
if(timestampLastRun>0)
	diff = ((timestampLastRun + (WebSettings.SENDMGR_TIME_MIN*60000L))-System.currentTimeMillis())/1000L;

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<META HTTP-EQUIV="Refresh" CONTENT="5; url=/Infomobility/index.jsp">
<title>Infomobility.it - FCD Importer</title>
</head>
<body>
<h2>Infomobility.it - FCD Importer</h2>
<%if(WebSettings.SENDMGR_ENABLE){ %>
<div>
<table border="1" style="border-spacing: 5px; border-color: black; text-align: center;">
	<tr>
		<%if(diff != -1L){ %>
		<th colspan="2">Statistiche Infomobility.it (reset tra <%=diff%> secondi)</th>
		<%}else{%>
		<th colspan="2">Statistiche Infomobility.it</th>
		<%}%>
	</tr>
	<tr>
		<td>Numero file ricevuti</td>
		<td><%=files%></td>
	</tr>
	<tr>
		<td>Numero posizioni ricevute</td>
		<td><%=positions%></td>
	</tr>
</table>
</div>
<%} %>
</body>
</html>
