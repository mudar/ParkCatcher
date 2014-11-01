<?php
if ( empty( $geo_lat ) || empty( $geo_lon ) ) {
	$geo_lat = $this->config->item('parking_default_geo_lat');
	$geo_lon = $this->config->item('parking_default_geo_lon');
	$is_empty_address = TRUE;
	$zoom = 14;
}
else {
	$zoom = 17;
}

?>
<div id="map" style="height: 100%;"></div>

<script src="<?php echo base_url() ?>js/libs/leaflet_0.4.4/leaflet.js"></script>
<script type="text/javascript">
// <![CDATA[
var map = L.map('map').setView([<?php echo $geo_lat ?> , <?php echo $geo_lon ?>], <?php echo $zoom ?>);

L.tileLayer('http://{s}.tile.cloudmade.com/{key}/22677/256/{z}/{x}/{y}.png', {
	attribution: '',
	minZoom: 13, 
	maxZoom: 18,
	key: 'BC9A493B41014CAABB98F0471D759707'
}).addTo(map);



function onEachFeature(feature, layer) {
	var popupContent = "";

	if (feature.properties && feature.properties.desc) {
		popupContent = "<strong><?php echo lang( 'parking_placemark_is_available' ) ?>.</strong><br /><?php echo lang( 'parking_placemark_next_forbidding' ) ?><ul>" 
			+ feature.properties.desc + "</ul>";
		var coordinates = feature.geometry.coordinates[1]+","+feature.geometry.coordinates[0];
		popupContent += "<div class=\"google_maps\">";
		popupContent += "<a target=\"_blank\" href=\"http://maps.google.ca/maps?daddr="+coordinates+"&amp;hl=<?php echo lang( 'parking_site_lang' ) ?>\"><?php echo lang( 'parking_placemark_itinerary' ) ?></a> ";
		popupContent += "<a target=\"_blank\" href=\"https://maps.google.ca/?cbll="+coordinates+"&amp;cbp=0,0,0,0,0&amp;layer=c&amp;z=16&amp;hl=<?php echo lang( 'parking_site_lang' ) ?>\"><?php echo lang( 'parking_placemark_street_view' ) ?></a>";
		popupContent += "</div>";
	}

	layer.bindPopup(popupContent);
}

var geoJsonLayer = L.geoJson(null, {
	onEachFeature: onEachFeature,
	pointToLayer: function (feature, latlng) {
		return L.circleMarker(latlng, {
			radius: 5,
			fillColor: "#6ca144",
			color: "#000",
			weight: 1,
			opacity: 0.8,
			fillOpacity: 0.8,
zIndexOffset: 100
		});
	}
}).addTo(map);

<?php if ( !empty( $destination ) && empty( $is_empty_address ) ): ?>
	L.circle([<?php echo $geo_lat ?> , <?php echo $geo_lon ?>], 100, {
			color: "#1877cf",
			fillColor: "#fff",
			opacity: 0.3,
			fillOpacity: 0.3
		}).addTo(map)

	var marker = L.marker([<?php echo $geo_lat ?> , <?php echo $geo_lon ?>] , {zIndexOffset: -10} ).addTo(map)
			.bindPopup( "<span class=\"black\"><?php printf( lang( 'parking_placemark_destination_radius' ) , '<strong>' . $destination . '</strong>' )?></span>").openPopup();

	var hasOpenedPopup = false;
<?php else: ?>
	var hasOpenedPopup = <?php echo ( empty( $id_post ) ? 'true' : 'false' ) ?>;
	hasOpenedPopup = true;

<?php endif ?>


var hasZoomedIn = false;
var hasZoomedOut = false;
function getGeoJSON() {
	if ( map.getZoom() >= 16 ) {
		globalAjaxCursorChange();

		var bounds = map.getBounds();
		var NW = bounds.getNorthWest();
		var SE = bounds.getSouthEast();

		jQuery.getJSON( "<?php echo base_url() ?>api/" , 
			{ day: $('#day').val() , hour: $('#hour').val() , duration: $('#duration').val() , destination: $('#destination').val() , 
				latNW: NW.lat , lonNW: NW.lng , latSE: SE.lat , lonSE: SE.lng , descHtml: true } , 
			function(data) {
				geoJsonLayer.clearLayers();
				geoJsonLayer.addData([data]);

				if ( hasOpenedPopup ) {
					var mapCenter = map.getCenter();
					for (var item in geoJsonLayer._layers) {
						if ( geoJsonLayer._layers[ item ]._latlng.distanceTo( mapCenter ) < <?php echo ( empty( $id_post ) ? 100 : 5 ) ?> ) {
							geoJsonLayer._layers[ item ].openPopup();
							break;
						}
					}
					hasOpenedPopup = false;
				}
		});
	}
}

map.on("moveend", function(e) {
	getGeoJSON();
});

map.on("zoomend", function(e) {
	if ( map.getZoom() >= 16 ) {
		hasZoomedIn = true;
		if ( hasZoomedOut ) {
			$( "#footer_zoom_in" ).hide();
			$("#footer_wrapper").hide();
		}
	}
	else if ( hasZoomedIn ) {
		hasZoomedOut = true;
		$( "#footer_content .hint" ).hide();
		$( "#footer_zoom_in" ).show();
		$("#footer_wrapper").show();
	}
});

// ]]>
</script>
