<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MagicMap 2 Test</title>
</head>
<style type="text/css">
		
    </style>
<script type="text/javascript" src="/MagicMap2/magicmap.js"></script>
<script type='text/javascript' src='<%=request.getContextPath() %>/servlet/dwr/engine.js'></script>
<script type='text/javascript' src='<%=request.getContextPath() %>/servlet/dwr/util.js'></script>
<script type="text/Javascript" src="<%=request.getContextPath() %>/servlet/dwr/interface/OracleSpatial.js"></script>
<script type="text/javascript">
var mapType = 1;
var map = null;
var div = "mapDiv";

var options = {
		google: {
			showGoogleStreets: true
			, showGoogleSatellite: true
			, showGoogleTerrain: true
		}
		, osm: {
				showOSM: true
			}
		, bing: {showBingStreets: true}
		, teleatlas: {
				showTeleAtlas: true
				, wmsLABEL : 'europa'
				, wmsURL : 'http://192.168.1.141:8080/geowebcache/service/wms'
				, wmsLayer: 'eursistri'
			}
		, editing: true
};

document.write(loadLibrary(options));
</script>
<script type="text/javascript"><!--
var initMap = function()
{
	map = new MagicMap(div);

	map.centerAt(45, 12);
	map.setZoom(70);
};

var disegnaOggetto = function()
{
	var lat = parseFloat(document.getElementById('latitudine').value, 10);
	var lon = parseFloat(document.getElementById('longitudine').value, 10);
	var id = document.getElementById('id').value;
	var label = document.getElementById('label').value;
	var drag = document.getElementById('drag').checked;

	map.drawObject(id, {x: lon, y: lat}, label, null, function(){console.log('selezionato: ' + id);}, drag);
	
	map.centerAt(lat, lon);
};

var disegnaLinea = function()
{
	var color = '00FF00';
	var stroke = 3;
	var opacity = 1;

	var coords = new Array();
	coords.push({x: 12, y: 41});
	coords.push({x: 12, y: 42});
	coords.push({x: 13, y: 42});
	
	var id = document.getElementById('id').value;

	var options = {
				stroke: 1,
				opacity: 1
			};

	map.drawLine(id, coords, options);
	
	map.setMapArea(coords);
};

var disegnaPoligono = function()
{
	var color = '#00FF00';
	var stroke = 3;
	var opacity = 0.7;

	var incolor = "#FFFFFF";
	var inopacity = 0.5;

	var coords = new Array();

	coords.push({x: 7.6174876, y: 45.3743576});
	coords.push({x: 7.6197465, y: 45.3764469});
	coords.push({x: 7.612183, y: 45.3758076});
	coords.push({x: 7.6174876, y: 45.3743576});
	
	var id = document.getElementById('id').value;

	var options = {
			outStroke: 6,
			outOpacity: 0.7,
			inOpacity: 1,
			inColor: '#ff00ff'
		};

	map.drawPolygon(id, coords, options);
	
	map.setMapArea(coords);
};

var disegnaCircle = function()
{
	var center = {y: 43, x: 12};
	var unit = 1;
	var radius = 10000;
	
	var color = '#00FF00';
	var stroke = 3;
	var opacity = 0.7;

	var incolor = "#FFFFFF";
	var inopacity = 0.5;

	var id = document.getElementById('id').value;

	var options = {
			outStroke: 6,
			outOpacity: 0.7,
			inOpacity: 1,
			inColor: '#ff00ff'
		};
	
	map.drawCircle(id, center, radius, options);
	
	map.centerAt(center.y, center.x);
};

var drawPoi = function()
{
	var lat = parseFloat(document.getElementById('latitudine').value, 10);
	var lon = parseFloat(document.getElementById('longitudine').value, 10);
	var id = document.getElementById('id').value;
	var label = document.getElementById('label').value;

	var center = {y: lat, x: lon};
	var unit = 1;
	var radius = 10000;
	
	var color = '#00FF00';
	var stroke = 3;
	var opacity = 0.7;

	var incolor = "#ff00ff";
	var inopacity = 0.5;

	var options = {
			outStroke: stroke,
			outOpacity: opacity,
			inOpacity: inopacity,
			inColor: incolor,
			label: label,
			onClickFunction: function() {onclickFunc();}
		};
	
	map.drawPoi(id, center, radius, options);
	
	map.centerAt(center.y, center.x);
};

var onclickFunc = function()
{
	alert('');
};

var clickEvent = function(value)
{
	var enable = false;
	if(value == 'y')
		enable = true;
	else if(value == 'n')
		enable = false;

	alert(enable);
	map.captureClick(enable, call);
};

var call = function(x, y)
{
	OracleSpatial.reverseGeocode(y, x, cbRevGeocode);
};

var cbRevGeocode = function(obj)
{
	var id = obj.id;
	var lat = obj.y;
	var lon = obj.x;

	map.drawObject(id, {x: lon, y: lat}, 'id: ' + id, null);
	routingArray.push(id);
};

var routingArray = new Array();

var routing = function()
{
	if(routingArray != null && routingArray.length == 2)
	{
		OracleSpatial.routing(routingArray[0], routingArray[1], cbRouting);
	}
	else
		alert('routing error');
};

var cbRouting = function(list)
{
	routingArray = new Array();

	if(list != null && list.length > 0)
	{
		for(var i=0;i<list.length;i++)
		{
			var point = list[i];
		}
		map.drawLine('routing', list);
	}
	else
		alert('no route');
};
--></script>
<body onload="initMap()">
	<div id="mapDiv" style="position: relative; height: 550px; width: 800px; background-color: #cccccc; border: 1px solid black; float: left">
	</div>
	<div id="console" style="border: 1px solid black; height: 600px; width: 400px; float: left">
		<input type="text" id="id" value="1" />&nbsp;ID<br/>
		<input type="text" id="label" value="test" />&nbsp;DESCRIZIONE<br/>
		<input type="text" id="latitudine" value="41.68" />&nbsp;LATITUDINE<br/>
		<input type="text" id="longitudine" value="11.35" />&nbsp;LONGITUDINE<br/>
		<input type="checkbox" id="drag" value="y" />&nbsp;DRAG<br/>
		<p/>
		<input type="button" value="drawObject" onclick="disegnaOggetto()" />
		<input type="button" value="drawLine" onclick="disegnaLinea()" />
		<br/>
		<input type="button" value="drawPoligon" onclick="disegnaPoligono()" />
		<input type="button" value="drawCircle" onclick="disegnaCircle()" />
		<input type="button" value="drawPoi" onclick="drawPoi()" />
		<p/>
		<br/>
		<input type="button" value="removeObject" onclick="map.removeObject(document.getElementById('id').value)" />
		<input type="button" value="removeAll" onclick="map.removeAll()" />
		<br/>
		Click su mappa
		<select id="clickEv" name="clickEv" onchange="clickEvent(this.value)">
			<option value="y">
				Abilita
			</option>
			<option value="n" selected="selected">
				Disabilita
			</option>
		</select>
		<br/>
		<input onclick="routing()" value="routing" type="button">
		<p/>
		<!-- 
		<ul id="controlToggle">
            <li>
                <input type="radio" name="type" value="none" id="noneToggle"
                       onclick="toggleControl(this);" checked="checked" />
                <label for="noneToggle">navigate</label>
            </li>
            <li>
                <input type="radio" name="type" value="line" id="lineToggle" onclick="toggleControl(this);" />
                <label for="lineToggle">measure distance</label>
            </li>
            <li>
                <input type="checkbox" name="geodesic" id="geodesicToggle" onclick="toggleGeodesic(this);" />
                <label for="geodesicToggle">use geodesic measures</label>
            </li>
            <li>

                <input type="checkbox" name="immediate" id="immediateToggle" onclick="toggleImmediate(this);" />
                <label for="immediateToggle">use immediate measures</label>
            </li>
        </ul>
        <div id="output">
        </div>
        -->
	</div>
</body>
</html>