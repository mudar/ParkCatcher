<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Map extends CI_Controller {

	public function index()
	{
		$data = array(); 

		$now = getdate();

		$day = ( $now['wday'] == 0 ? 7 : $now['wday'] ); // We start on monday (ISO), php getdate() returns 0 (for Sunday) through 6 (for Saturday)
		$start = $now['hours'] + ( $now['minutes'] < 30 ? 0 : 0.5 );
		$duration = 2;

		$this->search( $day , $start , $duration );
	}

	public function search( $day = -1 , $start = -1 , $duration = -1 , $destination = NULL ) 
	{

		if ( $this->input->post('submitted') == 1 ) {
			$this->input->set_cookie( 'has_searched' , TRUE , 1814400 );
			$this->session->set_flashdata('has_searched', TRUE );

			$day = $this->input->post( 'day' , TRUE );
			$start = $this->input->post( 'hour' , TRUE );
			$duration = $this->input->post( 'duration' , TRUE );
			$destination = $this->input->post( 'destination' , TRUE );

			redirect('/map/search/' . $day . '/' . $start . '/' . $duration . '/' . urlencode( $destination ) );
			exit;
		}

        $destination = urldecode($destination);

		$this->load->library( 'Geocoder' );
		$geo_point = $this->geocoder->get_geo_point( $destination );

		$min_duration = $this->config->item('parking_min_duration');
		$max_duration = $this->config->item('parking_max_duration');


		$now = getdate();
		$year_day = $now['yday'];


		$day = ( $now['wday'] == 0 ? 7 : $now['wday'] ); // We start on monday (ISO), php getdate() returns 0 (for Sunday) through 6 (for Saturday)
		$start = $now['hours'] + ( $now['minutes'] < 30 ? 0 : 0.5 );

		$hour = $start;
		$start += ($day -1) * 24;

		// TWEAK: for optimized memory usage, we return the CI Query object
		$data['posts_query'] = $this->Parking_model->get_posts( $start , $duration , $year_day );
		$placemarks = array();

		$name_format = " " . $this->lang->line('parking_placemark_name' );
		$latitude = $this->config->item('parking_geo_altitude');


		$data['is_map'] = TRUE;
		$data['has_searched'] = $this->session->flashdata('has_searched');
	
		$data['day'] = $day;
		$data['hour'] = $hour;
		$data['duration'] = $duration;
		$data['destination'] = $destination;

		if ( !empty( $geo_point['error'] ) ) {
			$data['address_error'] = $this->lang->line('parking_error_geocoder_' . $geo_point['error']['code'] );
			$data['is_empty_address'] = !empty( $geo_point['is_empty_address'] );
		}
		elseif ( !empty( $geo_point ) ) {
			$data['geo_lat'] = $geo_point['lat'];
			$data['geo_lon'] = $geo_point['lon'];
			$data['is_empty_address'] = !empty( $geo_point['is_empty_address'] );
		}
		else {
			$data['geo_lat'] = $this->config->item( 'parking_default_geo_lat' );
			$data['geo_lon'] = $this->config->item( 'parking_default_geo_lon' );
			$data['is_empty_address'] = TRUE;
		}

		$this->renderer->render( 'welcome/index' , $data );
	}

}

