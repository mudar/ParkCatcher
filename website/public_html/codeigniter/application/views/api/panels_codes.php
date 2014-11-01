<?php

header('Cache-Control: no-cache, must-revalidate');
header('Expires: Mon, 26 Jul 1997 05:00:00 GMT');
header('Content-type: application/json');

if ( empty( $panels_codes ) ) {
	$status = 'error';
	$num_rows = 0;
}
else {
	$status = 'ok';
	$num_rows = sizeof( $panels_codes );
}

?>
{"status":"<?php echo $status ?>","name":"PanelsCodes","version":<?php echo $api_version ?>,"count":<?php echo $num_rows ?>,"columns":["id","code","desc","type"],"PanelsCodes":[<?php 
if ( !empty( $panels_codes ) ): 
	$i = 1;

	foreach ($panels_codes  as $c ) {
		if ( $c['type_desc'] == $panel_code_type_desc_parking ) {
			$type = $panel_code_type_parking;
		}
		elseif ( $c['type_desc'] == $panel_code_type_desc_paid ) {
			$type = $panel_code_type_paid;
		}
		else {
			$type = null;
		}

		echo '[' . $c['id'] . ',' . json_encode( $c['code'] ) . ',' . json_encode( $c['description'] ) . ',' . $type . ']';

		$i++;
		if ( $i <= $num_rows ) {
			echo ',';
		}
	}

endif;
?>]}
