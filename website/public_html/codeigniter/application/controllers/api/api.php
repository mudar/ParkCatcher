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

    public function export_posts() {
		$data['posts_query'] = $this->Parking_model->export_posts();

		$this->load->view( 'api/posts' , $data );
	}

    public function export_panels() {
		$data['panels_query'] = $this->Parking_model->export_panels();

		$this->load->view( 'api/panels' , $data );
	}

    public function export_panels_codes() {
		$data['panels_codes'] = $this->Parking_model->export_panels_codes();

		$data['panel_code_type_parking'] = $this->config->item('parking_panel_code_type_parking');
		$data['panel_code_type_paid'] = $this->config->item('parking_panel_code_type_paid');
		$data['panel_code_type_desc_parking'] = $this->config->item('parking_panel_code_type_desc_parking');
		$data['panel_code_type_desc_paid'] = $this->config->item('parking_panel_code_type_desc_paid');

		$this->load->view( 'api/panels_codes' , $data );
	}

    public function export_panels_codes_rules() {
		$data['panels_codes_rules'] = $this->Parking_model->export_panels_codes_rules();

		$this->load->view( 'api/panels_codes_rules' , $data );
	}

}
