<?php

header('Cache-Control: no-cache, must-revalidate');
header('Expires: Mon, 26 Jul 1997 05:00:00 GMT');
header('Content-type: application/json');

if ( empty( $panels_codes_rules ) ) {
	$status = 'error';
	$num_rows = 0;
}
else {
	$status = 'ok';
	$num_rows = sizeof( $panels_codes_rules );
}

?>
{"status":"<?php echo $status ?>","name":"PanelsCodesRules","count":<?php echo $num_rows ?>,"columns":["id","id_panel_code","minutes_duration","hour_start","hour_end","hour_duration","day_start","day_end"],"PanelsCodesRules":[<?php 
if ( !empty( $panels_codes_rules ) ): 
	$i = 1;

	foreach ($panels_codes_rules  as $r ) {
		echo '[' . $r['id'] 
			. ',' . $r['id_panel_code'] 
			. ',' . $r['minutes_duration'] 
			. ',' . $r['hour_start']
			. ',' . $r['hour_end'] 
			. ',' . $r['hour_duration'] 
			. ',' . $r['day_start']  
			. ',' . $r['day_end'] . ']';

		$i++;
		if ( $i <= $num_rows ) {
			echo ',';
		}
	}

endif;
?>]}
