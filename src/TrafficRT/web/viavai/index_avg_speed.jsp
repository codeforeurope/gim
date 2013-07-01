<jsp:useBean id="startUpAppl" class="sistematica.webcontext.StartUpAppl" scope="application"/>
<html>
    <head>
        <title>VIAVAI Traffico Provincia Roma </title>
        <link rel="shortcut icon" href="<%=request.getContextPath()%>/images/favicon.ico">
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0;">
        <meta name="apple-mobile-web-app-capable" content="yes">
        <script src="OpenLayers.js"></script>
        <script type="text/javascript">
            var urlCgi = "${startUpAppl.instance.properties['atlas.cgi']}";
            var pathMapfile = "${startUpAppl.instance.properties['atlas.mapfile.avg_speed']}";
        </script>

        <link href="<%=request.getContextPath()%>/css/jq/jquery-ui-1.8.9.custom.css" rel="stylesheet" type="text/css" />

        <script type="text/javascript" src="<%=request.getContextPath()%>/js/jq/jquery-1.6.min.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/jq/jquery-ui-1.8.9.custom.min.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/jq/jquery.blockUI.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/js/loading.js"></script>

        <style>
            /* Copyright standard */
            .olControlAttribution {position:absolute; font-size:10px; bottom:0; right:0; background:rgba(0,0,0,0.1); font-family:Arial; padding:2px 4px;}
            /* Additional buttons style */
            div.olControlPanel {height:36px; width:32px; position:absolute; top:7px; left:144px;cursor: pointer; border-radius: 0px 5px 5px 0px; border-width: 2px 2px 2px 1px; border-style: solid;}
            .olControlPanel div {width:35px; height:36px;}
            /*  refresh */
            .refreshButtonItemInactive {background:rgba(0,0,0,0.2); border-radius: 5px 5px 5px 5px; background-image:url("refresh-white.png");}
            /*  refresh */
            .followButtonItemInactive, .followButtonItemActive {background:rgba(0,0,0,0.2); position:absolute;}


            .olControlPanPanel div {
                height: 32px;
                width: 32px;
                cursor: pointer;
                position: absolute;
            }

            .olControlPanPanel .olControlPanNorthItemInactive {
                background-image: url(img/north-big.png);
                top: 5px;
                left: 16px;
                background-position: 0px 0px;
            }
            .olControlPanPanel .olControlPanSouthItemInactive {
                background-image: url(img/south-big.png);
                top: 69px;
                left: 16px;
                background-position: 32px 0px;
            }
            .olControlPanPanel .olControlPanWestItemInactive {
                background-image: url(img/west-big.png);
                position: absolute;
                top: 37px;
                left: 0px;
                background-position: 0px 32px;
            }
            .olControlPanPanel .olControlPanEastItemInactive {
                background-image: url(img/east-big.png);
                top: 37px;
                left: 32px;
                background-position: 32px 32px;
            }

        </style>

    </head>
    <body>
        <div style="width:100%; height:100%" id="map"></div>
        <div id="popup"></div>
        <div id="contenentZoom" style="background:rgba(0,0,0,0.2); z-index: 4001; position: absolute; top: 15px; left:80px; width: 70px; height:36px; border-radius: 5px 0px 0px 5px; border-width: 2px 0px 2px 2px; border-color: black; border-style: solid;">
            <div id="zoomIn" onclick="changeZoom(1)" title="ZoomIn" style="position: absolute; width:  50%; z-index: 3001; cursor: pointer; border-right: 1px solid black;">
                <img src="zoomIn.png" style="width: 32px;"/>
            </div>
            <div id="zoomOut" onclick="changeZoom(-1)" title="ZoomOut" style="position: absolute; left: 50%; width:  50%; top: 0px; z-index: 3002; cursor: pointer; ">
                <img src="zoomOut.png" style="width: 32px;"/>
            </div>
        </div>
        <div id="legend" style="position: absolute; top: 115px; left:24px; z-index: 3003;">
            <img src="gradiente.png"/>
        </div>
        <script type="text/javascript">
            var map;
            var maxZoom=13; var minZoom=8; 
            // Additional buttons container
            var panel = new OpenLayers.Control.Panel();

            var init = function () {
                // standard map mercator projectione
                map = new OpenLayers.Map({
                    div: "map",
                    theme: null,
                    controls: [
                        new OpenLayers.Control.Navigation(),
                        new OpenLayers.Control.Attribution(),
                        new OpenLayers.Control.PanPanel()
                    ],
                    layers: [
                        new OpenLayers.Layer.OSM("OpenStreetMap", null, {
                            transitionEffect: 'resize'
                        })
                    ],
                    maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
                    maxResolution: 156543.0399,                                                         
                    numZoomLevels: 19,                                                                  
                    units: 'm',                                                                         
                    projection: new OpenLayers.Projection("EPSG:900913"),                               
                    displayProjection: new OpenLayers.Projection("EPSG:4326")        
                });
                //      map.addControl(new OpenLayers.Control.MousePosition());
	
                // VIAVAI layer

                var atlas = new OpenLayers.Layer.MapServer( "VIAVAI",urlCgi,
                {map: pathMapfile, layers: 'VIAVAI', transparent: 'false'}, 
                {isBaseLayer: false, visibility:true,transitionEffect: 'resize'});
                atlas.mergeNewParams({'random':Math.random()});  // Trick for caching
                map.addLayer(atlas);
                // Reload button
                panel.addControls([ new OpenLayers.Control.Button({displayClass: "refreshButton", trigger: function() {
                            atlas.mergeNewParams({'random':Math.random()});
                            atlas.redraw(true);
                        }, title: 'Refresh'})
                ]);

                map.addControl(panel);
                atlas.events.register("loadstart", atlas, function() {
                    visLoading();
                });
                atlas.events.register("loadend", atlas, function() {
                    endLoading();
                });

                map.events.register("zoomend", map, HasZoomed); <%-- Limits zoom --%>

                <% if (request.getParameter("lat") != null && request.getParameter("lon") != null) { %>
                
                var markers = new OpenLayers.Layer.Markers( "Markers" );
                map.addLayer(markers);
                var size = new OpenLayers.Size(33, 33);
                var icon = new OpenLayers.Icon('../images/sportscar.png', size);
                var position = new OpenLayers.LonLat(<%= request.getParameter("lon") %>, <%= request.getParameter("lat") %>).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
                markers.addMarker(new OpenLayers.Marker(position, icon));
                map.setCenter(position, 9);
                
                <% } else { %>
               
                var lonLat = new OpenLayers.LonLat(12.47811,41.88818).transform(new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject());
                map.setCenter(lonLat, 9);
                
                <% } %>
            };

            function HasZoomed(){
                var zoom=map.getZoom();
                if(zoom<minZoom) {
                    map.zoomTo(minZoom);
                }
                else if(zoom>maxZoom){
                    map.zoomTo(maxZoom);
                }
            }
            function changeZoom(value) {
                var new_zoom=(map.getZoom())+value;
                map.zoomTo(new_zoom)
            }
            init();
        </script>
    </body>
</html>
