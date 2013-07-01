var onPopupClose = function(map, feature)
{
	olmap.removePopup(feature.popup);
    feature.popup.destroy();
    feature.popup = null;
};

var onMouseOver = function(object)
{
//	console.log('onMouseOver');
	var feature = object.feature;
	
	if(feature.desc != null)
	{
		try
		{
		    popup = new OpenLayers.Popup.FramedCloud("chicken", 
		                             feature.geometry.getBounds().getCenterLonLat(),
		                             null,
		                             "<div style='font-size:.8em'>" + feature.desc + "</div>",
		                             null, true, function(){
		    							onPopupClose(olmap, feature);
		    						});

		    feature.popup = popup;
		    olmap.addPopup(popup);
		}
		catch(e)
		{ }
	}
};

var onMouseOut = function(object)
{
//	console.log('onMouseOut');
	var feature = object.feature;
		
	olmap.removePopup(feature.popup);
    feature.popup.destroy();
    feature.popup = null;
};


var onClickOver = function(feature)
{
	if(feature.drag != null && feature.drag == false)
	{
		if(feature.func != null)
			feature.func();

//		console.log('OVER - drag deactive : click active');
		OL_click.clickoutFeature();
		OL_drag.deactivate();
		OL_click.activate();
	}
	else
	{
//		console.log('OVER - click deactive : drag active');
//		console.log('OVER - click active : drag active');
		OL_click.activate();
		OL_drag.activate();
		if(control_navigation)
			control_navigation.deactivate();
	}
};

var onClickOut = function(feature)
{
	if(feature.drag != null && feature.drag == true)
	{
//		OL_drag.deactivate();
		OL_click.activate();
//		if(control_navigation)
//			control_navigation.activate();
//		console.log('OUT - drag deactive : click active');
	}
	else
	{
//		console.log('OUT - else');
//		OL_click.deactivate();
//		OL_drag.activate();
	}
};

//PER LA GESTIONE DELLA MISURA DISTANZA
var handleMeasurements = function(event)
{
 var geometry = event.geometry;
 var units = event.units;
 var order = event.order;
 var measure = event.measure;
 var element = document.getElementById(MEASURE_OUTPUT_DIV);
 var out = "";
 if(order == 1) {
     out += "measure: " + measure.toFixed(3) + " " + units;
 } else {
     out += "measure: " + measure.toFixed(3) + " " + units + "<sup>2</" + "sup>";
 }
 element.innerHTML = out;
};

var toggleControl = function(element)
{
 for(key in measureControls) {
     var control = measureControls[key];
     if(element.value == key && element.checked) {
         control.activate();
     } else {
         control.deactivate();
     }
 }
};

var toggleGeodesic = function(element)
{
 for(key in measureControls) {
     var control = measureControls[key];
     control.geodesic = element.checked;
 }
};

var toggleImmediate = function(element)
{
 for(key in measureControls) {
     var control = measureControls[key];
     control.setImmediate(element.checked);
 }
};

var googleCoord = function(x, y)
{
	this.x = x;
	this.y = y;
};

var convertLL2Google = function(lat, lon)
{
	var lonlat = new OpenLayers.LonLat(lon, lat);
	lonlat.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
	
	var gGoord = new googleCoord(lonlat.lon, lonlat.lat);
	return gGoord;
};

var convertGoogle2LL = function(x, y)
{
	var lonlat = new OpenLayers.LonLat(x, y);
	lonlat.transform(new OpenLayers.Projection("EPSG:900913"), new OpenLayers.Projection("EPSG:4326"));
	
	var gGoord = new googleCoord(lonlat.lon, lonlat.lat);
	return gGoord;
};

var checkControl = function(control)
{
	var measere_prop_div = document.getElementById(MEASURE_PROP_DIV);
	
	if(control)
	{
		var displayClass = control.displayClass;
		
		if(displayClass == NAVIGATION_CLASS)
		{
			if(measere_prop_div)
				measere_prop_div.style.display = 'none';
		}
		else if(displayClass == ZOOMBOX_CLASS)
		{
			if(measere_prop_div)
				measere_prop_div.style.display = 'none';
		}
		else if(displayClass == MEASURE_CALSS)
		{
			if(measere_prop_div)
				measere_prop_div.style.display = 'block';
		}
		else if(displayClass == DRAG_CLASS)
		{
			if(measere_prop_div)
				measere_prop_div.style.display = 'none';
		}
	}
};

// TEST
var doDoubleClick = function(evt)
{
	alert(evt);
};

var doClick = function(evt)
{
	alert('----' + evt);
};