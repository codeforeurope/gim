var NAVIGATION_CLASS = 'olControlNavigation';
var ZOOMBOX_CLASS = 'olControlZoomBox';
var MEASURE_CALSS = 'olControlMeasure';
var DRAG_CLASS = 'olControlDragFeature';

var MEASURE_OUTPUT_DIV = 'measure_output';
var MEASURE_PROP_DIV = 'measureDiv';
var ZOOM_HISTORY_DIV = 'zoom_history';

var LABEL_PREFIX = 'label_';
var POI_PREFIX = 'poi_';
var TIP_PREFIX = 'tip_';

var selFeature = null;
var control_editing = null;

var point_options = {
    width: 19
    , 
    height: 19
    , 
    xOffSet: -10
    , 
    yOffSet: -19
    , 
    labelYOffSet: 0
    , 
    labelXOffSet: 0
};

var line_options = {
    color: '#000000'
    , 
    stroke: 2
    , 
    opacity: 1
};

var polygon_options = {
    outColor: '#000000'	
    , 
    outStroke: 2
    , 
    outOpacity: 1
    , 
    inColor: '#ffffff'
    , 
    inOpacity: 0.5
};

var circle_options = {
    unit: 'meters'
    , 
    outColor: '#000000'
    , 
    outStroke: 2
    , 
    outOpacity: 1
    , 
    inColor: '#ffffff'
    , 
    inOpacity: 0.5
};

var poi_options = {
    unit: 'meters'
    , 
    outColor: '#000000'
    , 
    outStroke: 2
    , 
    outOpacity: 1
    , 
    inColor: '#ffffff'
    , 
    inOpacity: 0.5
    , 
    label: null
    , 
    onClickFunction: null
    , 
    icon: null
};

var alertFrame = 'frame cartografico non esistente.';
var alertGoogleComp = 'browser non compatibile con Google.';
var errorArgument = 'Errore! l\'argomento deve essere ';

// memorizza gli oggetti disegnati su mappa
var overlayList = null;
var overlayObjList = null;

// openlayer
var object_layer = null;

// oggetti mappa
var olmap, measureControls = null;

// path del file
var myPath = null;

var DEFAULT_ICON = null;
var OL_LABEL_BG = null;

var resolutions_array_teleatlas = [
4891.9698095703125
, 2445.9849047851562
, 1222.9924523925781
, 611.4962261962891
, 305.74811309814453
, 152.87405654907226
, 76.43702827453613
, 38.218514137268066
, 19.109257068634033
, 9.554628534317017
, 4.777314267158508
, 2.388657133579254
, 1.194328566789627
, 0.5971642833948135
//                         , 0,298582142
//                         , 0,149291071
];

var resolutions_array_google = [
4891.9698095703125
, 2445.9849047851562
, 1222.9924523925781
, 611.4962261962891
, 305.74811309814453
, 152.87405654907226
, 76.43702827453613
, 38.218514137268066
, 19.109257068634033
, 9.554628534317017
, 4.777314267158508
, 2.388657133579254
, 1.194328566789627
, 0.5971642833948135
, 0,298582142
, 0,149291071
, 0,074645536
, 0,037322768
];

var getJsPath = function(filename)
{
    var scripts = document.getElementsByTagName('script');
    if(scripts)
    {
        for(var i=0;i<scripts.length;i++)
        {
            if(scripts[i].src.indexOf(filename) != -1)
            {
                var path = scripts[i].src.split('?')[0];      // remove any ?query
                myPath = path.split('/').slice(0, -1).join('/')+'/';  // remove last filename part of path
				
                DEFAULT_ICON = myPath + 'img/marker.png';
                OL_LABEL_BG = myPath + 'img/labelBg.png';
            }
        }
    }
};

var loadLibrary = function(options)
{
    getJsPath('magicmap.js');
	
    mOptions = options;
		
    importJs = '<link rel="stylesheet" type="text/css" href="' + myPath + 'lib/theme/default/style.css">'
    + '<script type="text/javascript" src="' + myPath + 'lib/OpenLayers.js"></script>'
    + '<script type="text/javascript" src="' + myPath + 'lib/OpenLayersFunction.js"></script>'
    + '<script type="text/javascript" src="' + myPath + 'helper.js"></script>';
	
    if(mOptions.google != null)
    {
        importJs += '<script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.3&amp;sensor=false"></script>';
    }
	
    if(mOptions.bing != null)
    {
        importJs += '<script type="text/javascript" src="http://ecn.dev.virtualearth.net/mapcontrol/mapcontrol.ashx?v=6.2&mkt=en-us"></script>';
    }
	
    importJs += '<link rel="stylesheet" type="text/css" href="' + myPath + 'css/magicmap.css">';
    return importJs;
};

function showInfo(evt)
{
    if (evt.features && evt.features.length) {
        highlightLayer.destroyFeatures();
        highlightLayer.addFeatures(evt.features);
        highlightLayer.redraw();
    } else {
        document.getElementById('nodeInfo').innerHTML = evt.text;
    }
}

var initOpenLayer = function(div)
{
    var map = null;
	
    var map_div = document.getElementById(div);
	
    overlayList = new Array();
    overlayObjList = new Array();
	
    var ol_options = null;
	
    resolutions_array = resolutions_array_teleatlas;
    
    ol_options = {
        resolutions: resolutions_array,
        //		numZoomLevels: resolutions_array.length,
        projection: new OpenLayers.Projection('EPSG:900913'),
        maxExtent: new OpenLayers.Bounds(-2.003750834E7,-2.003750834E7,2.003750834E7,2.003750834E7),
        units: 'm',
        displayProjection: new OpenLayers.Projection('EPSG:4326')
    };
	
    map = new OpenLayers.Map(div, ol_options);
	
    var wms = null;
	
    var tlLABEL = null;
    var tlURL = null;
    var tlLAYERS = null;
    var showtlLAYERS = false;
	
    for(var i in mOptions)
    {
        var frame = mOptions[i];
		
        for(var j in frame)
        {
            var frame_prop = frame[j];
			
            if(i == 'google')
            {
                if(j == 'showGoogleTerrain')
                {
                    var gphy = new OpenLayers.Layer.Google(
                        "Google Physical",
                        {
                            type: google.maps.MapTypeId.TERRAIN
                            , 
                            minZoomLevel: 0, 
                            numZoomLevels: 19
                        }
                        );
                    map.addLayers([gphy]);
			        
                    if(frame_prop == true)
                        map.setBaseLayer(gphy);
                }
                else if(j == 'showGoogleStreets')
                {
                    var gmap = new OpenLayers.Layer.Google(
                        "Google Streets", // the default
                        {
                            minZoomLevel: 0, 
                            numZoomLevels: 19
                        }
                        );
                    map.addLayers([gmap]);

                    if(frame_prop == true)
                        map.setBaseLayer(gmap);
                }
                else if(j == 'showGoogleHibrid')
                {
                    var ghyb = new OpenLayers.Layer.Google(
                        "Google Hybrid",
                        {
                            type: google.maps.MapTypeId.HYBRID
                            , 
                            minZoomLevel: 0, 
                            numZoomLevels: 19
                        }
                        );
                    map.addLayers([ghyb]);
				        
                    if(frame_prop == true)
                        map.setBaseLayer(ghyb);
                }
                else if(j == 'showGoogleSatellite')
                {
                    var gsat = new OpenLayers.Layer.Google(
                        "Google Satellite",
                        {
                            type: google.maps.MapTypeId.SATELLITE , 
                            minZoomLevel: 0, 
                            numZoomLevels: 19
                        }
                        );
                    map.addLayers([gsat]);

                    if(frame_prop == true)
                        map.setBaseLayer(gsat);
                }
            }
            else if(i == 'osm')
            {
                var osm = new OpenLayers.Layer.OSM();
				
                map.addLayers([osm]);

                if(frame_prop == true)
                    map.setBaseLayer(osm);
            }
            else if(i == 'bing')
            {
                if(j == 'showBingStreets')
                {
                    var shaded = new OpenLayers.Layer.VirtualEarth("Bing Streets", {
                        type: VEMapStyle.Shaded,
                        'sphericalMercator': true
                        , 
                        minZoomLevel: 0, 
                        numZoomLevels: 19
                    });
                    map.addLayers([shaded]);

                    if(frame_prop == true)
                        map.setBaseLayer(shaded);
                }
                else if(j == 'showBingHibrid')
                {
                    var hybrid = new OpenLayers.Layer.VirtualEarth("Bing Hibrid", {
                        type: VEMapStyle.Hybrid,
                        'sphericalMercator': true
                        , 
                        minZoomLevel: 0, 
                        numZoomLevels: 19
                    });
                    map.addLayers([hybrid]);

                    if(frame_prop == true)
                        map.setBaseLayer(hybrid);
                }
                else if(j == 'showBingSatellite')
                {
                    var aerial = new OpenLayers.Layer.VirtualEarth("Bing Satellite", {
                        type: VEMapStyle.Aerial, 
                        'sphericalMercator': true
                        , 
                        minZoomLevel: 0, 
                        numZoomLevels: 19
                    });
                    map.addLayers([aerial]);

                    if(frame_prop == true)
                        map.setBaseLayer(aerial);
                }
            }
            else if(i == 'teleatlas')
            {
                if(j == 'wmsLABEL')
                    tlLABEL = frame_prop;
                else if(j == 'wmsURL')
                    tlURL = frame_prop;
                else if(j == 'wmsLayer')
                    tlLAYERS = frame_prop;

                if(j == 'showTeleAtlas')
                {
                    if(frame_prop == true)
                        showtlLAYERS = true;
                }
				
                if(tlLABEL != null && tlURL != null && tlLAYERS != null)
                {
                    wms = new OpenLayers.Layer.WMS( tlLABEL, tlURL, {
                        layers: tlLAYERS, 
                        format: 'image/png'
                    });
                    map.addLayers([wms]);
					
                    if(showtlLAYERS == true)
                        map.setBaseLayer(wms);
                }
            }
        }
    }
	
    vectors = new OpenLayers.Layer.Vector("Vector Layer", {
        sphericalMercator : true, 
        displayInLayerSwitcher: false
    });

    olmap = map;
	
    map.addLayers([vectors]);
    map.addControl(new OpenLayers.Control.MousePosition());
    
    var panZoom = new OpenLayers.Control.PanZoom();
    map.addControl(panZoom);
    for (var i = 0; i < panZoom.buttons.length; i++) {
        if (panZoom.buttons[i] && panZoom.buttons[i].id && panZoom.buttons[i].id.indexOf("zoomworld") != -1) {
            document.getElementById(panZoom.buttons[i].id).style.display = "none";
        } else  if (panZoom.buttons[i] && panZoom.buttons[i].id && panZoom.buttons[i].id.indexOf("zoomout") != -1) {
            document.getElementById(panZoom.buttons[i].id).style.top = "81px";
        }
    }

    var sketchSymbolizers = {
        "Point": {
            pointRadius: 4,
            graphicName: "square",
            fillColor: "white",
            fillOpacity: 1,
            strokeWidth: 1,
            strokeOpacity: 1,
            strokeColor: "#333333"
        },
        "Line": {
            strokeWidth: 3,
            strokeOpacity: 1,
            strokeColor: "#666666",
            strokeDashstyle: "dash"
        },
        "Polygon": {
            strokeWidth: 2,
            strokeOpacity: 1,
            strokeColor: "#666666",
            fillColor: "white",
            fillOpacity: 0.3
        }
    };
    var style = new OpenLayers.Style();
    style.addRules([
        new OpenLayers.Rule({
            symbolizer: sketchSymbolizers
        })
        ]);
    var styleMap = new OpenLayers.StyleMap({
        "default": style
    });
	
    OL_click = new OpenLayers.Control.SelectFeature([vectors],{
        clickout: true
        , 
        toggle: false
        , 
        multiple: false
        , 
        hover: false  // attiva l'onSelect sull' onMouseOver
        , 
        toggleKey: "ctrlKey" // ctrl key removes from selection
        , 
        multipleKey: "shiftKey" // shift key adds to selection
        , 
        onSelect: onClickOver
        , 
        onUnselect: onClickOut
    }
    );
	
    map.addControl(OL_click);	
    OL_click.activate();
    
    OL_drag = new OpenLayers.Control.DragFeature(vectors);
    OL_drag.onStart = function(f)
    {
        if(f.drag != null && f.drag == true)
        {
            OL_click.deactivate();
            OL_drag.activate();
        }
        else
        {
            OL_drag.deactivate();
        }
    };
	
    OL_drag.onComplete = function(f)
    {
        if(f.drag != null && f.drag == false)
        {
            OL_drag.deactivate();
            OL_click.activate();
        }
        else
        {
            OL_drag.activate();
            OL_click.activate();
        }
    };
    map.addControl(OL_drag);

    OpenLayers.Control.CustomPanel = OpenLayers.Class(OpenLayers.Control, {
        controls:null,
        autoActivate:true,
        defaultControl:null,
        saveState:false,
        activeState:null
        , 
        initialize: function(a){
            OpenLayers.Control.prototype.initialize.apply(this,[a]);
            this.controls=[];
            this.activeState={};
        }
        , 
        destroy: function() {
            OpenLayers.Control.prototype.destroy.apply(this,arguments);
            for(var a,b=this.controls.length-1;b>=0;b--)
            {
                a=this.controls[b];
                a.events&&a.events.un({
                    activate:this.iconOn,
                    deactivate:this.iconOff
                });
                OpenLayers.Event.stopObservingElement(a.panel_div);
                a.panel_div=null;
            }
            this.activeState=null;
        }
        , 
        activate: function() {
            if(OpenLayers.Control.prototype.activate.apply(this,arguments))
            {
                for(var a,b=0,c=this.controls.length;b<c;b++)
                {
                    a=this.controls[b];
                    if(a===this.defaultControl||this.saveState&&this.activeState[a.id])
                        a.activate()
                }
                if(this.saveState===true)
                    this.defaultControl=null;
                this.redraw();
                return true;
            }
            else 
                return false;
        }
        , 
        deactivate: function() {
            if(OpenLayers.Control.prototype.deactivate.apply(this,arguments))
            {
                for(var a,b=0,c=this.controls.length;b<c;b++)
                {
                    a=this.controls[b];
                    this.activeState[a.id]=a.deactivate()
                }
                this.redraw();
                return true;
            }
            else 
                return false;
        }
        , 
        draw:function() {
            OpenLayers.Control.prototype.draw.apply(this,arguments);
            this.addControlsToMap(this.controls);
            return this.div;
        }
        , 
        redraw: function() {
            for(var a=this.div.childNodes.length-1;a>=0;a--)
                this.div.removeChild(this.div.childNodes[a]);
            this.div.innerHTML="";
            if(this.active)
            {
                a=0;
                for(var b=this.controls.length;a<b;a++)
                    this.div.appendChild(this.controls[a].panel_div)
            }
        }
        , 
        activateControl: function(a) {
            if(!this.active)
                return false;
            if(a.type==OpenLayers.Control.TYPE_BUTTON)
                a.trigger();
            else if(a.type==OpenLayers.Control.TYPE_TOGGLE)
                a.active?a.deactivate():a.activate();
            else
            {
                for(var b,c=0,d=this.controls.length;c<d;c++)
                {
                    b=this.controls[c];
                    if(b!=a&&(b.type===OpenLayers.Control.TYPE_TOOL||b.type==null))
                        b.deactivate();
                }
                a.activate();
            }
        }
        , 
        addControls: function(a) {
            a instanceof Array||(a=[a]);
            this.controls=this.controls.concat(a);
            for(var b=0,c=a.length;b<c;b++)
            {
                var d=document.createElement("div");
                d.className=a[b].displayClass+"ItemInactive";
                a[b].panel_div=d;
                if(a[b].title!="")
                    a[b].panel_div.title=a[b].title;
                OpenLayers.Event.observe(a[b].panel_div,"click",OpenLayers.Function.bind(this.onClick,this,a[b]));
                OpenLayers.Event.observe(a[b].panel_div,"dblclick",OpenLayers.Function.bind(this.onDoubleClick,this,a[b]));
                OpenLayers.Event.observe(a[b].panel_div,"mousedown",OpenLayers.Function.bindAsEventListener(OpenLayers.Event.stop));
            }
            if(this.map)
            {
                this.addControlsToMap(a);
                this.redraw();
            }
        }
        , 
        addControlsToMap: function(a) {
            for(var b,c=0,d=a.length;c<d;c++)
            {
                b=a[c];
                if(b.autoActivate===true)
                {
                    b.autoActivate=false;
                    this.map.addControl(b);
                    b.autoActivate=true;
                }
                else
                {
                    this.map.addControl(b);
                    b.deactivate();
                }
                b.events.on({
                    activate:this.iconOn,
                    deactivate:this.iconOff
                })
            }
        }
        , 
        iconOn: function() {
            var a=this.panel_div;
            a.className=a.className.replace(/ItemInactive$/,"ItemActive");
        }
        , 
        iconOff: function() {
            var a=this.panel_div;
            a.className=a.className.replace(/ItemActive$/,"ItemInactive");
        }
        , 
        onClick:function(a,b) {
            // FLORETI
            checkControl(a); // guarda OpenLayerFunction
            OpenLayers.Event.stop(b?b:window.event);
            this.activateControl(a);
        }
        , 
        onDoubleClick: function(a,b) {
            OpenLayers.Event.stop(b?b:window.event);
        }
        , 
        getControlsBy: function(a,b) {
            var c=typeof b.test=="function";
            return OpenLayers.Array.filter(this.controls, function(d){
                return d[a]==b||c&&b.test(d[a])
            })
        }
        , 
        getControlsByName:function(a) {
            return this.getControlsBy("name",a);
        }
        , 
        getControlsByClass:function(a) {
            return this.getControlsBy("CLASS_NAME",a);
        } 
        , 
        CLASS_NAME:"OpenLayers.Control.Panel"
    });
    
    // EDITING LAYER
    if(mOptions != null && mOptions.editing != null && mOptions.editing == true)
    {
        editlayer = new OpenLayers.Layer.Vector( "Editable",  {
            sphericalMercator : true, 
            displayInLayerSwitcher: false
        });
        map.addLayer(editlayer);
		
        var control_drawPoint = new OpenLayers.Control.DrawFeature(editlayer,
            OpenLayers.Handler.Point,
            {
                'displayClass': 'olControlDrawFeaturePoint'
            });
        var control_drawLine = new OpenLayers.Control.DrawFeature(editlayer,
            OpenLayers.Handler.Path,
            {
                'displayClass': 'olControlDrawFeaturePath'
            });
        var control_drawPolygon = new OpenLayers.Control.DrawFeature(editlayer,
            OpenLayers.Handler.Polygon,
            {
                'displayClass': 'olControlDrawFeaturePolygon'
            });
    }
        
    // SCALE BAR
    scalebar = new OpenLayers.Control.ScaleLine();
    map.addControl(scalebar);
        
    if(mOptions)
    {
        if(mOptions.centerPoint)
        {
            var gCoord = convertLL2Google(mOptions.centerPoint.y, mOptions.centerPoint.x);
    		
            var lonlat = new OpenLayers.LonLat(gCoord.x, gCoord.y);
            map.setCenter(lonlat);
        }
    }
    
    // support GetFeatureInfo
    OpenLayers.Marker.prototype.id = null;
    OpenLayers.Feature.Vector.prototype.id = null;
    OpenLayers.Feature.Vector.prototype.desc = null;
    OpenLayers.Feature.Vector.prototype.latitude = null;
    OpenLayers.Feature.Vector.prototype.longitude = null;
    OpenLayers.Feature.Vector.prototype.category = null;
    OpenLayers.Feature.Vector.prototype.func = null;
    OpenLayers.Feature.Vector.prototype.drag = false;
	
    document.getElementById("OpenLayers.Control.PanZoom_5").style.display = "none";
	
    return map;
};

var MagicMap = function(div)
{
    this.type = mapType;
    this.div = div;
    this.map = initOpenLayer(div);
	
    MagicMap.prototype.addLayer = function(layer, displayInLayerSwitcher)
    {
        if(displayInLayerSwitcher == undefined)
            displayInLayerSwitcher = false;
		
        if(layer != null)
        {
            if(layer.wmsLABEL != null && layer.wmsURL != null && layer.wmsLAYER != null)
            {
                OpenLayers.ProxyHost = "/cgi-bin/proxy.cgi?url=";
				
                ext_layer = new OpenLayers.Layer.WMS(
                    layer.wmsLABEL,
                    layer.wmsURL, 
                    {
                        'layers': layer.wmsLAYER, 
                        'format':'image/png', 
                        'transparent':'true'
                    },

                    {
                        'opacity': 1.0, 
                        'isBaseLayer': false, 
                        'visibility': true, 
                        sphericalMercator : true, 
                        displayInLayerSwitcher: displayInLayerSwitcher
                    }
                    );//new OpenLayers.Layer.WMS( layer.wmsLABEL, layer.wmsURL, {layers: layer.wmsLAYER, format: 'image/png'}, { sphericalMercator : true, displayInLayerSwitcher: false});
                this.map.addLayers([ext_layer]);
				
                if(false)
                {
                    highlightLayer = new OpenLayers.Layer.Vector("Highlighted Features", {
                        displayInLayerSwitcher: false, 
                        isBaseLayer: false 
                    }
                    );
		    		
                    this.map.addLayer(highlightLayer);
	    		
                    infoControls = {
                        click: new OpenLayers.Control.WMSGetFeatureInfo({
                            url: layer.wmsURL, 
                            title: 'Identify features by clicking',
                            layers: [ext_layer],
                            queryVisible: true
                        })
                    };
		    		
                    for (var i in infoControls)
                    { 
                        infoControls[i].events.register("getfeatureinfo", this, function(evt) {
                            if (evt.features && evt.features.length) {
                                highlightLayer.destroyFeatures();
                                highlightLayer.addFeatures(evt.features);
                                highlightLayer.redraw();
                            } else {
                                document.getElementById('nodeInfo').innerHTML = evt.text;
                            }
                        });
                        this.map.addControl(infoControls[i]);
                        infoControls.click.activate();
                    }
                }
	    		
                return ext_layer;
            }
        }
        else
            alert('MagicMap.addLayer ERROR: layer is null!');
    };
	
    /////////////////////////////////////////////////////////////////////////////
    MagicMap.prototype.addAtlasLayer = function(nameLayer, urlCgi, pathMapfile)
    {
        OpenLayers.ProxyHost = "/cgi-bin/proxy.cgi?url=";
	
        ext_layer = new OpenLayers.Layer.MapServer( nameLayer,  urlCgi, { 
            map: pathMapfile, 
            transparent: true
        }, {
            singleTile: true, 
            transitionEffect: 'resize'
        });
                        
        this.map.addLayers([ext_layer])
        return ext_layer;
    };
    //////////////////////////////////////////////////////////////
	
    MagicMap.prototype.addKmlLayer = function(kml, displayInLayerSwitcher)
    {
        if(displayInLayerSwitcher == undefined)
            displayInLayerSwitcher = false;
	
        if(kml != null)
        {
            if(kml.kmlLABEL != null && kml.kmlURL != null)
            {
                kml_layer = new OpenLayers.Layer.Vector(kml.kmlLABEL, {
                    projection: this.map.displayProjection,
                    strategies: [new OpenLayers.Strategy.Fixed()],
                    protocol: new OpenLayers.Protocol.HTTP({
                        url: kml.kmlURL,
                        format: new OpenLayers.Format.KML({
                            extractStyles: true, 
                            extractAttributes: true,
                            maxDepth: 2
                        })
                    }), 
                    displayInLayerSwitcher: displayInLayerSwitcher
                });
				
                this.map.addLayers([kml_layer]);
				
                return kml_layer;
            }
        }
        else
            alert('MagicMap.addKmlLayer ERROR: kml is null!');
    };
	
    MagicMap.prototype.addESRILayer = function()
    {
        if(true)
        {
            var layer = new OpenLayers.Layer.ArcGIS93Rest( "ArcGIS Server Layer",
                "http://sampleserver1.arcgisonline.com/ArcGIS/rest/services/Specialty/ESRI_StateCityHighway_USA/MapServer/export", 
                {
                    layers: "show:0,2", 
                    'format':'image/png', 
                    'transparent':'true'
                },

                {
                    'opacity': 1.0, 
                    'isBaseLayer': false, 
                    'visibility': true, 
                    sphericalMercator : false, 
                    displayInLayerSwitcher: displayInLayerSwitcher
                });
            this.map.addLayer(layer);
        }
        else
            alert('MagicMap.addESRILayer ERROR: kml is null!');
    };
	
    MagicMap.prototype.removeLayer = function(layer)
    {
        if(layer)
        {
            this.map.events.register('removelayer', null, function(evt)
            {	
                //				alert('removing');
                // aggiungere rimozione di tutti gli oggetti disegnati sul layer se necessario
                });
			
            layer.destroy();
			
        // aggiungere unregister
        }
        else
            alert('MagicMap.removeLayer ERROR: kml is null!');
    };
	
    MagicMap.prototype.drawObject = function(id, point, label, icon, onClickFunction, isDraggable, options)
    {
        if(icon == null)
            icon = DEFAULT_ICON;
		
        if(isDraggable == undefined)
            isDraggable = false;
		
        if(options == undefined)
            options = null;
		
        if(exists(overlayList, id))
        {
            this.removeObject(id);
            this.drawObject(id, point, label, icon, onClickFunction, isDraggable, options);
        }
        else
        {
            if(label != null)
            {
                var width = 80;
                try
                {
                    var t = document.createElement("table");
                    var tr = document.createElement("tr");
                    t.appendChild(tr);
                    var td = document.createElement("td");
                    tr.appendChild(td);
                    font = document.createElement("font");
                    font.setAttribute("size", "2");
                    font.innerHTML = label;
                    td.appendChild(font);
                    document.body.appendChild(t);
                    width = font.offsetWidth;
                    document.body.removeChild(t);
                }
                catch(e)
                {
                    alert(e);
                }			
            }
			
            var isToFixChrome = navigator.userAgent.toUpperCase().indexOf("WEBKIT")>-1;

            //			, labelYOffSet: 0
            var featureOptions = {
                externalGraphic: "" + icon + ""
                , 
                graphicWidth: (options!=null&&options.width!=null?options.width:point_options.width) //19
                , 
                graphicHeight: (options!=null&&options.height!=null?options.height:point_options.height) //19
                , 
                graphicXOffset: (options!=null&&options.xOffSet!=null?options.xOffSet:point_options.xOffSet) //-10
                , 
                graphicYOffset: (options!=null&&options.yOffSet!=null?options.yOffSet:(isToFixChrome? -24:point_options.yOffSet)) //(isToFixChrome? -24:-19)	//-29
                , 
                label: label!=null?label:null
                , 
                fontColor: '#000000'
                , 
                fontFamily: 'Verdana'
                , 
                fontSize: '10'
                , 
                fontWeight: 'normal'
                , 
                labelAlign: 'ct'
                , 
                backgroundGraphic: label!=null?"" + OL_LABEL_BG + "":null
                , 
                backgroundHeight: 13
                , 
                backgroundWidth: width + 15
                , 
                backgroundYOffset: (options!=null&&options.labelYOffSet!=null?options.labelYOffSet + (isToFixChrome? -3:0):point_options.labelYOffSet + (isToFixChrome? -3:0))	//-3
                , 
                labelYOffset: -(options!=null&&options.labelYOffSet!=null?options.labelYOffSet + (isToFixChrome? -3:0):point_options.labelYOffSet + (isToFixChrome? -3:0))	//-3
            //					, backgroundXOffset: (options!=null&&options.labelXOffSet!=null?options.labelXOffSet:point_options.labelXOffSet)
            //					, labelXOffset: (options!=null&&options.labelXOffSet!=null?options.labelXOffSet:point_options.labelYOffSet)
            };
			
            var gCoord = convertLL2Google(point.y, point.x);
			
            lat = gCoord.y;
            lon = gCoord.x;
			
            var feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Point(lon,lat), null, featureOptions);
			
            if(onClickFunction != null)
            {
                feature.func = onClickFunction;
            }
			
            feature.id = id;
            feature.desc = label;
            feature.latitude = lat;
            feature.longitude = lon;
            feature.category = null;
			
            if(isDraggable == true)
            {
                feature.drag = true;
            }
			
            vectors.addFeatures([feature]);
			
            overlayList.push(id);
            var overlayObj = new OverlayObj(id, feature, isDraggable);
            overlayObjList.push(overlayObj);
        }
    };
	
    MagicMap.prototype.drawLine = function(id, coords, options)
    {
        var color;
        var stroke;
        var opacity;
		
        if(options == null)
        {
            color = line_options.color;
            stroke = line_options.stroke;
            opacity = line_options.opacity;
        }
        else
        {
            color = (options.color == null?line_options.color:options.color);
            stroke = (options.stroke == null?line_options.stroke:options.stroke);
            opacity = (options.opacity == null?line_options.opacity:options.opacity);
        }
		
        if(exists(overlayList, id))
        {
            this.removeObject(id);
            this.drawLine(id, coords, options);
        }
        else
        {
            var vectorLine = new Array();
            for(var i=0;i<coords.length;i++)
            {
                var point = coords[i];
				
                var gCoord = convertLL2Google(point.y, point.x);
				
                vectorLine.push(new OpenLayers.Geometry.Point(gCoord.x, gCoord.y));
            }
            var line = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(vectorLine), null, {
                strokeColor: "" + color + "", 
                strokeOpacity: opacity, 
                strokeWidth: stroke
            });
			
            line.id = id;
            if(options != null && options.func != null)
                line.func = options.func;
            vectors.addFeatures([line]);
            overlayList.push(id);
            var overlayObj = new OverlayObj(id, line);
            overlayObjList.push(overlayObj);
        }
    };
	
    MagicMap.prototype.drawPolygon = function(id, coords, options)
    {
        var polyPoints = null;
		
        var outColor;
        var outStroke;
        var outOpacity;
        var inColor;
        var inOpacity;
		
        if(options == null)
        {
            outColor = polygon_options.outColor;
            outStroke = polygon_options.outStroke;
            outOpacity = polygon_options.outOpacity;
            inColor = polygon_options.inColor;
            inOpacity = polygon_options.inOpacity;
        }
        else
        {
            outColor = (options.outColor==null?polygon_options.outColor:options.outColor);
            outStroke = (options.outStroke==null?polygon_options.outStroke:options.outStroke);
            outOpacity = (options.outOpacity==null?polygon_options.outOpacity:options.outOpacity);
            inColor = (options.inColor==null?polygon_options.inColor:options.inColor);
            inOpacity = (options.inOpacity==null?polygon_options.inOpacity:options.inOpacity);
        }
		
        if(exists(overlayList, id))
        {
            this.removeObject(id);
            this.drawPolygon(id, coords, options);
        }
        else
        {
            polyPoints = new Array();
			
            for (var a = 0 ; a < coords.length ; a++ )
            {
                var point = {
                    x: parseFloat(coords[a].x),
                    y: parseFloat(coords[a].y)
                };
				
                var gCoord = convertLL2Google(point.y, point.x);
				
                polyPoints.push(new OpenLayers.Geometry.Point(gCoord.x, gCoord.y));
            }
            if(polyPoints)
            {
                var polygon = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LinearRing(polyPoints), null, {
                    strokeColor: "" + outColor + "", 
                    fillColor: "" + inColor + "", 
                    strokeOpacity: outOpacity, 
                    fillOpacity: inOpacity, 
                    strokeWidth: outStroke
                });
                polygon.id = id;
                if(options != null && options.func != null)
                    polygon.func = options.func;
				
                vectors.addFeatures([polygon]);
				
                overlayList.push(polygon.id);
                var overlayObj = new OverlayObj(polygon.id, polygon);
                overlayObjList.push(overlayObj);
            }
        }
    };
	
    MagicMap.prototype.drawCircle = function(id, center, radius, options)
    {
        var u;
        var outColor;
        var outStroke;
        var outOpacity;
        var inColor;
        var inOpacity;
		
        if(options == null)
        {
            u = circle_options.unit;
            outColor = circle_options.outColor;
            outStroke = circle_options.outStroke;
            outOpacity = circle_options.outOpacity;
            inColor = circle_options.inColor;
            inOpacity = circle_options.inOpacity;
        }
        else
        {
            u = (options.unit==null?circle_options.unit:options.unit);
            outColor = (options.outColor==null?circle_options.outColor:options.outColor);
            outStroke = (options.outStroke==null?circle_options.outStroke:options.outStroke);
            outOpacity = (options.outOpacity==null?circle_options.outOpacity:options.outOpacity);
            inColor = (options.inColor==null?circle_options.inColor:options.inColor);
            inOpacity = (options.inOpacity==null?circle_options.inOpacity:options.inOpacity);
        }
		
        if(u == 'meters')
            unit = 1;
        else if(u == 'miles')
            unit = 0;
        else
            unit = 0;
		
        if(exists(overlayList, id))
        {
            this.removeObject(id);
            this.drawCircle(id, center, radius, options);
        }
        else
        {
            var coords = createCircle(center, radius, unit);
				
            if(coords.length > 0)
                this.drawPolygon(id, coords, options); //this.drawPolygon(id, coords, outColor, outStroke, outOpacity, inColor, inOpacity);
        }
    };
	
    MagicMap.prototype.drawPoi = function(id, center, radius, options)
    {
        var u;
        var outColor;
        var outStroke;
        var outOpacity;
        var inColor;
        var inOpacity;
        var label;
        var icon;
        var onClickFunction;
		
        if(options == null)
        {
            u = poi_options.unit;
            outColor = poi_options.outColor;
            outStroke = poi_options.outStroke;
            outOpacity = poi_options.outOpacity;
            inColor = poi_options.inColor;
            inOpacity = poi_options.inOpacity;
            label = poi_options.label;
            onClickFunction = poi_options.onClickFunction;
        }
        else
        {
            u = (options.unit==null?poi_options.unit:options.unit);
            outColor = (options.outColor==null?poi_options.outColor:options.outColor);
            outStroke = (options.outStroke==null?poi_options.outStroke:options.outStroke);
            outOpacity = (options.outOpacity==null?poi_options.outOpacity:options.outOpacity);
            inColor = (options.inColor==null?poi_options.inColor:options.inColor);
            inOpacity = (options.inOpacity==null?poi_options.inOpacity:options.inOpacity);
            icon = (options.icon==null?null:options.icon);
            label = (options.label==null?poi_options.label:options.label);
            onClickFunction = (options.onClickFunction==null?poi_options.onClickFunction:options.onClickFunction);
        }
		
        if(u == 'meters')
            unit = 1;
        else if(u == 'miles')
            unit = 0;
        else
            unit = 0;
		
        if(exists(overlayList, id))
        {
            this.removeObject(id);
            this.drawPoi(id, center, radius, options);
        }
        else
        {
            this.drawCircle(POI_PREFIX + id, center, radius, options);//this.drawCircle(POI_PREFIX + id, center, radius, unit, outColor, outStroke, outOpacity, inColor, inOpacity);
            this.drawObject(id, center, label, icon, onClickFunction, false);
        }
    };
	
    /**
	* id --> identificativo dell'oggetto
	* point --> {x: ##.######, y: ##.######}
	* text --> testo da mostrare (anche html)
	* showCloseWidget --> mostra il pulsante di chiusura
	* autoPan --> controlla la centratura automatica dell' infoBox
	*/
    MagicMap.prototype.drawInfoBox = function(id, point, text, showCloseWidget, autoPan)
    {
        //OPENLAYER
        var tip_id = id.substr(4);

						
        if(exists(overlayList, tip_id))
        {
            for(var i=0;i<overlayObjList.length;i++)
            {
                var ele = overlayObjList[i];
                if(ele.id == tip_id) // vincola il tip ad essere associato ad un oggetto
                {
                    var feature = ele.o;
                    popup = new OpenLayers.Popup.FramedCloud("chicken", 
                        feature.geometry.getBounds().getCenterLonLat(),
                        null,
                        "<div style='font-size:.8em'>" + text + "</div>",
                        null, showCloseWidget, function(){
                            onPopupClose(olmap, feature);
                        });
										
                    if(autoPan != null)
                    {
                        popup.setAutoPan(autoPan);
                    }
					
                    feature.popup = popup;
                    olmap.addPopup(popup);
					
                    overlayList.push(id);
                    var overlayObj = new OverlayObj(id, popup);
                    overlayObjList.push(overlayObj);
                }
            }
        }
    };
	
    MagicMap.prototype.centerAt = function(lat, lon)
    {
        var gCoord = convertLL2Google(lat, lon);
        var lonlat = new OpenLayers.LonLat(gCoord.x, gCoord.y);
        this.map.setCenter(lonlat);
    };
	
    MagicMap.prototype.removeAll = function()
    {
        if(vectors != null)
        {
            if(overlayObjList != null)
            {
                for(var j=0;j<overlayObjList.length;j++)
                {
                    var ele = overlayObjList[j];
					
                    try
                    {
                        onFeatureUnselect(ele.o);
                    }
                    catch(e)
                    {}
					
                    if((typeof(ele.id) == 'string') && ele.id.indexOf(TIP_PREFIX) != -1)
                    {
                        ele.o.destroy();
                        olmap.removePopup([ele.o]);
                    }
					
                    vectors.removeFeatures([ele.o]);
                    overlayList = removeElementByValue(overlayList, ele.id);
                    var overlayObj = new OverlayObj(ele.id, ele.o);
                    overlayObjList = removeElementByValueG(overlayObjList, overlayObj);
                    this.removeAll();
                }
            }
        }
    };
	
    MagicMap.prototype.removeObject = function(id)
    {
        if(overlayObjList != null)
        {
            for(var i=0;i<overlayObjList.length;i++)
            {
                var ele = overlayObjList[i];
                if(ele.id == id)
                {
                    if(vectors != null)
                    {
                        vectors.removeFeatures([ele.o]);
                        if((typeof(ele.id) == 'string') && ele.id.indexOf(TIP_PREFIX) != -1)
                        {
                            ele.o.destroy();
                            olmap.removePopup([ele.o]);
                        }
                        overlayList = removeElementByValue(overlayList, ele.id);
                        var overlayObj = new OverlayObj(ele.id, ele.o);
                        overlayObjList = removeElementByValueG(overlayObjList, overlayObj);
                    }
                    this.removeObject(id);
                }
                if(ele.id == POI_PREFIX + id)
                {
                    if(vectors != null)
                    {
                        vectors.removeFeatures([ele.o]);
                        overlayList = removeElementByValue(overlayList, ele.id);
                        var overlayObj = new OverlayObj(ele.id, ele.o);
                        overlayObjList = removeElementByValueG(overlayObjList, overlayObj);
                    }
                    this.removeObject(id);
                }
            }
        }
    };
	
    MagicMap.prototype.removeObjectWithPrefix = function(prefix)
    {
        //OPENLAYER
        if(overlayObjList != null)
        {
            for(var i=0;i<overlayObjList.length;i++)
            {
                var ele = overlayObjList[i];
                if(ele.id.indexOf(prefix) == 0)
                {
                    if(ele.id.indexOf(TIP_PREFIX) != -1)
                    {
                        ele.o.destroy();
                    }
                    vectors.removeFeatures([ele.o]);
                    overlayList = removeElementByValue(overlayList, ele.id);
                    var overlayObj = new OverlayObj(ele.id, ele.o);
                    overlayObjList = removeElementByValueG(overlayObjList, overlayObj);
                    this.removeObjectWithPrefix(prefix);
                }
            }
        }
    };
	
    MagicMap.prototype.captureClick = function(enabled, callback)
    {
        if(enabled == true)
        {
            this.map.events.register("click", this.map, function(e){
                olCaptureClick(e, callback);
            });
        }
        else
        {
            this.map.events.unregister("click", this.map, function(e){
                olCaptureClick(e, callback);
            });
        }
    };
	
    /**
	* level valore in percentuale (da 0 a 100)
	* dove 100 e\' il massimo dettaglio
	*/
    MagicMap.prototype.setZoom = function(level)
    {
        var MAX_ZOOM = 100;
        var MIN_ZOOM = 0;
		
        var OL_MAX_ZOOM = parseInt(resolutions_array.length, 10);
		
        if(level > MAX_ZOOM && level < MIN_ZOOM)
            alert('il valore ' + level + ' non e\' ammesso come valore (deve essere compreso tra '+MIN_ZOOM+' e '+MAX_ZOOM+')');
        else
        {
            var percent_level;
            //OPENLAYER
            percent_level = (OL_MAX_ZOOM * level)/MAX_ZOOM;
            this.map.zoomTo(parseInt(percent_level, 10));
        }
    };
	
    MagicMap.prototype.setMapArea = function(coords)
    {
        var result = null;
        var minx = null;
        var miny = null;
        var maxx = null;
        var maxy = null;
        if((coords instanceof Array) || (true)) /* problema lato GWT per implementazione di tipo Array */
        {
            if (coords.length == 1)
            {
                coords.push(coords[0]);
            }
			
            //OPENLAYER
            result = new Array();
            //			xResult = new Array();
            //			yResult = new Array();
						
            for(i=0;i<coords.length;i++)
            {
                var point = coords[i];
				
                //				xResult.push(point.x);
                //				yResult.push(point.y);
				
                if(minx == null)
                    minx = point.x;
                if(miny == null)
                    miny = point.y;
                if(maxx == null)
                    maxx = point.x;
                if(maxy == null)
                    maxy = point.y;
				
                if(point.x < minx)
                    minx = point.x;
                if(point.x > maxx)
                    maxx = point.x;
				
                if(point.y < miny)
                    miny = point.y;
                if(point.y > maxy)
                    maxy = point.y;
            }
			
            //			xResult = xResult.sort(function(a,b){return a - b;});
            //			yResult = yResult.sort(function(a,b){return a - b;});
			
            //			result.push(xResult[0]);
            //			result.push(yResult[0]);
            //			result.push(xResult[xResult.length-1]);
            //			result.push(yResult[yResult.length-1]);
			
            //			var gCoordMin = convertLL2Google(yResult[0], xResult[0]);
            //			var gCoordMax = convertLL2Google(yResult[yResult.length-1], xResult[xResult.length-1]);
			
            var gCoordMin = convertLL2Google(miny, minx);
            var gCoordMax = convertLL2Google(maxy, maxx);
			
            var bounds = new OpenLayers.Bounds(gCoordMin.x, gCoordMin.y, gCoordMax.x, gCoordMax.y);
            this.map.zoomToExtent(bounds);
        }
        else
        {
            alert(errorArgument+'Array.');
        }
    };
	
    MagicMap.prototype.updateSize = function()
    {
        this.map.updateSize();
    };
	
    MagicMap.prototype.existObject = function(id)
    {
        var result = false;
		
        for(var i=0;i<overlayList.length;i++)
        {
            var item = overlayList[i];
            if(item == id)
            {
                result = true;
            }
        }
        return result;
    };
};
