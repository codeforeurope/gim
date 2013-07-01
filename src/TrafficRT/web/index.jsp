<%@page import="sistematica.sinottici.Street"%>
<%@page import="sistematica.sinottici.Camera"%>
<%@page import="sistematica.sinottici.Sinottico"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="startUpAppl" class="sistematica.webcontext.StartUpAppl" scope="application"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <%
            Sinottico sinottico = new Sinottico();

            List<Street> listStr = null;
            listStr = sinottico.getAllStreet();

            Camera camDef = sinottico.getListCameras().get(0);
        %>
        <link rel="shortcut icon" href="<%=request.getContextPath()%>/images/favicon.ico">

        <title>${startUpAppl.instance.properties['application.title']}</title>

        <link href="<%=request.getContextPath()%>/css/default.css" rel="stylesheet" type="text/css" />
        <link href="<%=request.getContextPath()%>/css/map.css" rel="stylesheet" type="text/css" />
        <link href="<%=request.getContextPath()%>/css/jq/jquery-ui-1.8.9.custom.css" rel="stylesheet" type="text/css" />

        <%-- DWR --%>
        <script type="text/javascript" src="<%=request.getContextPath()%>/servlet/dwr/engine.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/servlet/dwr/util.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/servlet/dwr/interface/DwrSinottici.js"></script>

        <%-- JQuery --%>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/jq/jquery-1.6.min.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/jq/jquery-ui-1.8.9.custom.min.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/jq/jquery.blockUI.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/jq/jquery.layout-1.2.0.js"></script>

        <%-- Altre librerie --%>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/pageManagerLayout.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/date.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/loading.js"></script>

        <style>
            image[id *= "OpenLayers.Geometry.Point_"]:hover { cursor:pointer; }; 
        </style>

        <script  type="text/javascript">
            var secondRefresh = 300;      <%-- Refresh della pagina ogni 5 minuti --%>
            var millSecCamera = 10000;    <%-- Refresh della popUp delle telecamere espresso in millesecondi --%>

            var street = null;
            var timestamp = null;
            var sdfDate = 'd MMM yyyy';
            var sdfTime = 'H:mm';

            var arrayCamere = new Array();
            var arrayEvent = new Array();
            var arrayBubble = new Array();

            var idStr = -1;
            var justUsedCombo = false;
            
            var nameLayer = "Provincia Roma";
            var urlCgi = "${startUpAppl.instance.properties['atlas.cgi']}";
            var pathMapfile = "${startUpAppl.instance.properties['atlas.mapfile.traffic']}";
            
            function documentReady() {
                timedRefresh(secondRefresh*1000);
                jQuery.noConflict();

                outerLayout = jQuery('body').layout({
                    center__paneSelector: ".outer-center"
                    , applyDefaultStyles: false
                    , east__size: 350
                    , east__resizable: false
                    , north__resizable: false
                    , north__closable: false
                    , north__spacing_open: 0
                    , north__spacing_closed: 0
                    , onopen_end: function (paneName) {
                        if (paneName == 'east') {
                            rightWidth = DEFAULT_RIGHT_WIDTH;
                            resizePage();
                        }
                    }
                    , onclose_end: function (paneName) {
                        if (paneName == 'east') {
                            rightWidth = 0;
                            resizePage();
                        }
                    }
                });

                innerLayout = jQuery('div.outer-center').layout({
                    slidable: false
                    , center__paneSelector: ".inner-center"
                    , south__paneSelector: ".inner-south"
                    , south__size: bottomHeight
                    , south__resizable: false
                    , onopen_end: function (name) {
                        if (name == 'south') {
                            dims = getPageDim();
                            mainHeight = dims.height;
                            document.getElementById('centerPanel').style.width = '100%';
                            document.getElementById('map').style.width = '100%';
                        }
                    }
                    , onclose_end: function (name) {
                        if (name == 'south') {
                            dims = getPageDim();
                            mainHeight = dims.height;
                            document.getElementById('centerPanel').style.width = '100%';
                            document.getElementById('map').style.width = '100%';
                        }
                    }
                });

                jQuery("#eastPanel").tabs({disabled: [1]});
                
                resizePage();

                document.body.style.visibility = "visible";

                loadMap();
                
                if(theMap != null) {
                    var layer = theMap.addAtlasLayer(nameLayer, urlCgi, pathMapfile);
                    layer.mergeNewParams({"random": Math.random()});  <%-- Trick for caching --%>
            
                    theMap.map.events.register("moveend", theMap.map, function() { <%-- Resetta la combobox della strada ad ogni spostamento/zoom per evitare problemi durante il refresh periodico --%>
                        if (!justUsedCombo) {
                            document.getElementById('street').options[0].selected = true;
                        } else {
                            justUsedCombo = false;
                        }
                    });
                
                    theMap.map.events.register("zoomend", theMap.map, function() { <%-- Disabilita i livelli di zoom troppo lontani dal terreno --%>
                        if (theMap.map.getResolution() > 610) { 
                            theMap.setZoom(60);
                        }
                    });
                    
                    layer.events.register("loadstart", layer, function() {
                        visLoading();
                    });
                    
                    layer.events.register("loadend", layer, function() {
                        endLoading();
                    });
                }
                
                timestamp = new Date();
                document.getElementById('timestamp').innerHTML = formatDate(timestamp, sdfDate) + '<br>Dati aggiornati alle ' + formatDate(timestamp, sdfTime);

                zoomStreet(document.getElementById('street'));
            };

            function resizePage()
            {
                dims = getPageDim();
                mainWidth = dims.width;
                mainHeight = dims.height;

                document.getElementById('centerPanel').style.width = '100%';
                document.getElementById('map').style.width = '100%';

                if(theMap != null) {
                    theMap.updateSize();
                }
                
                document.body.style.margin="auto";
            }

            function timedRefresh(timeoutPeriod) {
                setTimeout("reloadPage();", timeoutPeriod);
            }

            function reloadPage() {
                street = document.getElementById('street');
                timedRefresh(secondRefresh * 1000);
                timestamp = new Date();
                document.getElementById('timestamp').innerHTML = formatDate(timestamp, sdfDate) + '<br>Dati aggiornati alle ' + formatDate(timestamp, sdfTime);
                zoomStreet(street);
            }

            function zoomStreet(street) {
                var value = street.value;
                var split = value.split("_");
                var id_street = split[0];
                idStr = id_street;
                
                if (id_street != -1) {
                    var bBox = new Array();
                    var nodePoint = {x: split[2], y: split[1]};
                    bBox.push(nodePoint);
                    nodePoint = {x: split[4], y: split[3]};
                    bBox.push(nodePoint);
                    justUsedCombo = true;
                    theMap.setMapArea(bBox);
                }

                arrayEvent = new Array();
                arrayCamere = new Array();
                arrayBubble = new Array();
                
                showHiddenEvent(true);
                showHiddenCamera(true);
            }
            
            ////////// EVENTI //////////
    
            function showHiddenEvent(isVisible) {
                if (isVisible) {
                    drawEvent();
                } else {
                    for (var e = 0; e < arrayEvent.length; e++) {
                        theMap.removeObject(arrayEvent[e]);
                    }
                    arrayEvent = new Array();
                    arrayBubble = new Array();
                }
            }
            
            function drawEvent() {
                DwrSinottici.getListEvents(CBdrawEvent);
            }
            
            function CBdrawEvent(listEvent) {    
                if (listEvent != null && listEvent.length > 0) {
                    for (var i = 0; i < listEvent.length; i++) {
                        var id_event = 'idEvent=' + i;
                        var urlIco = listEvent[i].urlIco;
                        arrayEvent.push(id_event);
                        
                        eval(
                        ' var center = {y: listEvent[i].latitude, x: listEvent[i].longitude};' +
                            'var radius = 200;' +
                            'var options = {' +
                            '     outStroke: 2' +
                            '     , outOpacity: 0.7' +
                            '     , inOpacity: 1' +
                            '     , inColor: \'#ffff00\'' +
                            '};' +
                            'var center_info' + i + ' = {x: listEvent[i].longitude, y: listEvent[i].latitude};' +
                            'var tip_id' + i + ' = \'tip_' + id_event + '\';' +
                            'var html_text'+ i +' = listEvent[i].html;'+
                            'theMap.drawObject(id_event, center, null, urlIco, function() { openInfoBox(tip_id' + i + ', center_info' + i + ', html_text' + i + '); }, false, options);');
                    }
                }
            }
            
            function openInfoBox(tip_id, center_info, html_text) {
                try {
                    theMap.removeObject(tip_id);
                } finally {
                    theMap.drawInfoBox(tip_id, center_info, html_text, true, false);
                }
            }
            
            function removeBubbleElement () {
                if(arrayBubble != null && arrayBubble.length > 0) {
                    for(var i = 0; i < arrayBubble.length; i++) { 
                        for (var j = 0; j < arrayEvent.length; j++) {
                            if(arrayBubble[i]==('tip_' + arrayEvent[j])) {
                                arrayBubble.splice(i, 1); 
                            }
                        }
                    }
                }
            }

            ////////// TELECAMERE //////////

            var linkCamera = "<%=camDef.getLink()%>";

            setTimeout("aggiornaCamera();", millSecCamera);
        
            function drawCamera() {
                DwrSinottici.getListCamera(CBdrawCamera);
            }

            function CBdrawCamera(listCam) {
                if (listCam != null && listCam.length > 0) {
                    for (var i = 0; i < listCam.length; i++) {
                        var id_camera = 'idCamera=' + i;
                        arrayCamere.push(id_camera);
                        eval(
                        ' var center = {y: listCam[i].latitude, x: listCam[i].longitude};' +
                            'var radius = 200;' +
                            'var link'  + i +  ' = listCam[i].link;' +
                            'var name' + i + ' = listCam[i].name;' +
                            'var desc' + i + ' = listCam[i].description;' +
                            'var options = {' +
                            '     outStroke: 2' +
                            '     , outOpacity: 0.7' +
                            '     , inOpacity: 1' +
                            '     , inColor: \'#ffff00\'' +
                            '};' +
                            'theMap.drawObject(id_camera, center, null, "images/camera.gif", function() { switchCamera(link' + i + ', name' + i + ', desc' + i + '); }, false, options)');
                    }
                }
            }

            function switchCamera(link, name, desc) {
                document.getElementById('Camera_IFrame').src = link;
                linkCamera = link;

                document.getElementById('nameCamera').innerHTML = name;
                document.getElementById('descCamera').innerHTML = desc;

                aggiornaCamera();
            }

            function aggiornaCamera() {
                var myIFrame = document.getElementById('Camera_IFrame');
                myIFrame.src = linkCamera + Math.floor(Math.random() * 11);
                if (millSecCamera > 0) {
                    setTimeout('aggiornaCamera();', millSecCamera);
                }
            }

            function showHiddenCamera(isVisible) {
                if (isVisible) {
                    drawCamera();
                } else {
                    for(var c = 0; c < arrayCamere.length; c++) {
                        theMap.removeObject(arrayCamere[c]);
                    }
                    arrayCamere = new Array();
                }
            }          
        </script>
        <jsp:include page="mapSinottici.jsp"/>
    </head>
    <body onload="documentReady();" onresize="resizePage()" style="visibility: hidden">
        <div class="ui-layout-north">
            <div align="left" style="float:left; height: 70px; vertical-align: middle; width: 100%; background: #2977A8;">
                <img id="mainLogo" src="images/logo_1.png"/>
            </div>
            <table width="100%" border="0">
                <tr>
                    <td class="panelHeader" >
                        <table border="0" cellpadding="0" cellspacing="0" style="vertical-align: middle; margin-left: 230px">
                            <tr style="width: 100%">
                                <td><span style="font-size: 14pt">TRAFFICO IN TEMPO REALE</span></td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </div>

        <div class="outer-center">
            <div id="centerPanel" class="inner-center">
                <div id="map" style="height: 100%; width: 100%; background-color: #ffffff;">
                    <div style="margin: 5px 5px; padding: 7px 7px; right: 5px; position: absolute; z-index: 5000; border:1px solid #888888; background-color: white">
                        <span style="margin-right: 10px">Lento</span>
                        <span style="background-color: #000000; margin-right: 2px; border:1px solid #888888">&nbsp;&nbsp;&nbsp;&nbsp;</span>
                        <span style="background-color: #990000; margin-right: 2px; border:1px solid #888888">&nbsp;&nbsp;&nbsp;&nbsp;</span>
                        <span style="background-color: #FFCC00; margin-right: 2px; border:1px solid #888888">&nbsp;&nbsp;&nbsp;&nbsp;</span>
                        <span style="background-color: #33BB00; margin-right: 10px; border:1px solid #888888;">&nbsp;&nbsp;&nbsp;&nbsp;</span>
                        <span>Veloce</span>
                    </div>
                </div>
            </div>
        </div>
        <div id="eastPanel" class="ui-layout-east" style="padding: 0">
            <div class="ui-layout-content">
                <div id="camera-tab" style="padding: 0; padding-top: 5px; overflow-y: hidden">
                    <table  style="padding: 10px">
                        <tbody>
                            <tr>
                                <td colspan="2"><div id="timestamp" style="font: bold 16px arial, sans-serif; color:#000000">dd-MM-yyyy HH24:MI</div></td>
                            </tr>
                            <tr>
                                <td>
                                    &nbsp;&nbsp;
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <select id="street" style="width: 250px; cursor: pointer;" onChange="zoomStreet(this)">
                                        <option value="-1" selected="true">Vai alla strada...</option>
                                        <optgroup label="GRA e AUTOSTRADE">
                                            <%
                                                String valueA = "";
                                                if (listStr != null)
                                                    for (Street str : listStr) {
                                                        valueA = str.getId() + "_" + str.getLatUp() + "_" + str.getLonUp() + "_" + str.getLatDown() + "_" + str.getLonDown();
                                                        if (str.getTypeStreet().equals("A")) {
                                            %>
                                            <option value=<%=valueA%>><%=str.getName()%></option>
                                            <%}
                                                }%>
                                        </optgroup>
                                        <optgroup label="CONSOLARI">
                                            <%
                                                String valueC = "";
                                                if (listStr != null)
                                                    for (Street str : listStr) {
                                                        valueC = str.getId() + "_" + str.getLatUp() + "_" + str.getLonUp() + "_" + str.getLatDown() + "_" + str.getLonDown();
                                                        if (str.getTypeStreet().equals("C")) {
                                            %>
                                            <option value=<%=valueC%>><%=str.getName()%></option>
                                            <%}
                                                }%>
                                        </optgroup>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                        </tbody>
                    </table>
                    <div id="cameraDialog" style="padding-left: 10px; z-index: 2000; bottom: 0; position:absolute;">
                        <h2 id="nameCamera"><%=camDef.getName()%></h2>
                        <div id="conteinerCamera" style="display: block;" >
                            <img id="Camera_IFrame" src="<%=camDef.getLink()%>" style="width: 320px; height: 240px; ">
                        </div>
                        <p id="descCamera"><%=camDef.getDescription()%></p>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
