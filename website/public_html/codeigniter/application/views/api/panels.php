<?php

$db_callback = ( $this->db->dbdriver == 'postgre' ? 'pg_fetch_object' : 'mysql_fetch_object' );

header('Cache-Control: no-cache, must-revalidate');
header('Expires: Mon, 26 Jul 1997 05:00:00 GMT');
header('Content-type: application/json');

if ( empty( $panels_query ) ) {
	$status = 'error';
	$num_rows = 0;
}
else {
	$status = 'ok';
	$num_rows = $panels_query->num_rows();
}

?>
{"status":"<?php echo $status ?>","name":"Panels","count":<?php echo $num_rows ?>,"columns":["id_panel","id_post","id_panel_code"],"Panels":[<?php 
if ( !empty( $panels_query ) ): 
	$i = 1;

	$resource = $panels_query->result_id;
	while ( $row = call_user_func_array( $db_callback , array( $resource ) ) ) {
		echo '[' . $row->id_panel 
			. ',' . $row->id_post  
			. ',' . $row->id_panel_code . ']';

		$i++;
		if ( $i <= $num_rows ) {
			echo ',';
		}
	}

endif;
?>]}
