/**
 * Class: Sapo.Control.FeatureEventHandlers
 * 
 * This control allows to register listners on feature(s) events
 */
Sapo.Control.FeatureEventHandlers = OpenLayers.Class(OpenLayers.Control,{
	/**
	 * Property: features
	 * {{Array: <OpenLayers.Feature.Vector>} The features which will have the handlers associated
	 */
	features: null,
	
	 /**
     * APIProperty: callbacks
     * {Object} The functions that are sent to the handlers.feature for callback
     */
    callbacks: null,
	
	/**
     * Property: handlers
     * {Object} Object with references to multiple <OpenLayers.Handler>
     *     instances.
     */
    handlers: null,
	
	/**
	 * Property: listeners
	 * {Object} Object with the listeners for each event
	 * 
	 * Object structure:
	 * {
	 * 	'evtName': {callback: function, context: Object}
	 * }
	 * 
	 * Supported evtNames:
	 * - click
	 * - mouseover
	 * - mouseout
	 * - dblclick
	 */
	listeners: null,
	
	 /**
     * Constructor: Sapo.Control.FeatureEventHandlers
     * Create a new control to register listners on feature events
     *
     * Parameters:
     * features - {Array: <OpenLayers.Feature.Vector>} The features which will
     *     listen to events
     * options - {Object} Optional object whose properties will be set on the
     *     control.
     */
    initialize: function(features, listeners, options) {
		OpenLayers.Control.prototype.initialize.apply(this, [options]);
		
		this.features = features;
		this.listeners = listeners;
		
		var callbacks = {
			click: this.clickFeature,
			over: this.overFeature,
			out: this.outFeature,
			dblclick: this.dblclickFeature
		}
		
		this.callbacks = OpenLayers.Util.extend(callbacks, this.callbacks);
		this.handlers = {
			feature: new Sapo.Handler.Feature(
				this,
				this.callbacks
			)
		};
	},
	
	 /**
     * Method: activate
     * Activates the control.
     * 
     * Returns:
     * {Boolean} The control was effectively activated.
     */
    activate: function () {
       this.handlers.feature.activate();
	   return OpenLayers.Control.prototype.activate.apply(this, arguments);
    },
	
	 /**
     * Method: deactivate
     * Activates the control.
     * 
     * Returns:
     * {Boolean} The control was effectively activated.
     */
    deactivate: function () {
       this.handlers.feature.deactivate();
	   return OpenLayers.Control.prototype.deactivate.apply(this, arguments);
    },
	
	clickFeature: function(feature){
			if(this.listeners['click'] != null){
				var context = this.listeners['click'].context != null ? this.listeners['click'].context : this;
				this.listeners['click'].callback.apply(context, [feature]);
			}
	},
	
	overFeature: function(feature){
			if(this.listeners['mouseover'] != null){
				var context = this.listeners['mouseover'].context != null ? this.listeners['click'].context : this;
				this.listeners['mouseover'].callback.apply(context, [feature]);
			}
	},
	
	outFeature: function(feature){
			if(this.listeners['mouseout'] != null){
				var context = this.listeners['mouseout'].context != null ? this.listeners['click'].context : this;
				this.listeners['mouseout'].callback.apply(context, [feature]);
			}
	},
	
	dblclickFeature: function(feature){
			if(this.listeners['dblclick'] != null){
				var context = this.listeners['dblclick'].context != null ? this.listeners['click'].context : this;
				this.listeners['dblclick'].callback.apply(context, [feature]);
			}
	},
	
	/**
	 * Method: addFeature
	 * Adds a feature as listener
	 */
	addFeature: function(feature){
		this.features.push(feature);
	},
	
	/**
	 * Method: removeFeature
	 * Removes a feature from being listener
	 */
	removeFeature: function(feature){
		var index = -1;
		for(i = 0; i < this.features.length; ++i){
			if(feature == this.features[i]){
				index = i;
				break;
			}
		}
		if(index != -1){
			this.features.splice(index, 1);
		}
	},
	
	/**
	 * Method: addListeners
	 * Adds listeners to the feature events
	 */
	addListeners: function(newListeners){
		this.listeners = OpenLayers.Util.extend(newListeners, this.listeners);
	},
	
	 /** 
     * Method: setMap
     * Set the map property for the control. 
     * 
     * Parameters:
     * map - {<OpenLayers.Map>} 
     */
    setMap: function(map) {
        this.handlers.feature.setMap(map);
        
        OpenLayers.Control.prototype.setMap.apply(this, arguments);
    },
	
	CLASS_NAME: "Sapo.Control.FeatureEventHandlers"
});
