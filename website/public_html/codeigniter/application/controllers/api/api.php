<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Api extends CI_Controller {

	public function index( $day = -1 , $start = -1 , $duration = -1 , $destination = NULL ) 
	{

		$day = $this->input->get( 'day' , TRUE );
		$start = $this->input->get( 'hour' , TRUE );
		$duration = $this->input->get( 'duration' , TRUE );
		$destination = $this->input->get( 'destination' , TRUE );

        $NW_lat = $this->input->get( 'latNW' , TRUE );
        $NW_lon = $this->input->get( 'lonNW' , TRUE );
        $SE_lat = $this->input->get( 'latSE' , TRUE );
        $SE_lon = $this->input->get( 'lonSE' , TRUE );

        $is_html_desc = $this->input->get( 'descHtml' , FALSE );
        $is_html_desc = ( ( $is_html_desc == 1 ) || ( strtolower( $is_html_desc ) === 'true' ) );

		$min_duration = $this->config->item('parking_min_duration');
		$max_duration = $this->config->item('parking_max_duration');

		$now = getdate();
		$year_day = $now['yday'];


		$day = ( $now['wday'] == 0 ? 7 : $now['wday'] ); // We start on monday (ISO), php getdate() returns 0 (for Sunday) through 6 (for Saturday)
		$start = $now['hours'] + ( $now['minutes'] < 30 ? 0 : 0.5 );

		$hour = $start;
		$start += ($day -1) * 24;


		// TWEAK: for optimized memory usage, we return the CI Query object
		$posts_query = $this->Parking_model->get_posts( $start , $duration , $year_day , $NW_lat , $NW_lon , $SE_lat , $SE_lon );
		$placemarks = array();

		$name_format = " " . $this->lang->line('parking_placemark_name' );
		$latitude = $this->config->item('parking_geo_altitude');

		$data['is_map'] = TRUE;
		$data['posts_query'] = $posts_query;

		$data['day'] = $day;
		$data['hour'] = $hour;
		$data['duration'] = $duration;

		$data['is_html_desc'] = $is_html_desc;

		$this->load->view( 'api/index' , $data );
	}


}
