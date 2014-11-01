<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/*
    Park Catcher Montréal
    Find a free parking in the nearest residential street when driving in
    Montréal. A Montréal Open Data project.

    Copyright (C) 2012 Mudar Noufal <mn@mudar.ca>

    This file is part of Park Catcher Montréal.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

class Api extends CI_Controller {

	public function index( $day = -1 , $start = -1 , $duration = -1 , $destination = NULL ) 
	{

		$day = $this->input->get( 'day' , TRUE );
		$start = $this->input->get( 'hour' , TRUE );
		$duration = $this->input->get( 'duration' , TRUE );
		$destination = $this->input->get( 'destination' , TRUE );

		if ( $this->input->get( 'api_key' , -1 ) == $this->config->item( 'parking_key_api_export' ) ) {
			$data['has_api_key'] = true;
		}

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

		if ( !is_numeric( $day ) || ( $day < 1 ) || ( $day > 7 ) ) {
			$day = ( $now['wday'] == 0 ? 7 : $now['wday'] ); // We start on monday (ISO), php getdate() returns 0 (for Sunday) through 6 (for Saturday)
		}
		if ( !is_numeric( $start ) || ( $start < 1 ) || ( $start > 24 ) ) {
			$start = $now['hours'] + ( $now['minutes'] < 30 ? 0 : 0.5 );
		}
		$hour = $start;
		$start += ($day -1) * 24;
		if ( !is_numeric( $duration ) || ( $duration < $min_duration ) || ( $duration > $max_duration ) ) {
			$duration = 2;
		}

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

    public function export_posts( $api_key = NULL , $offset = 0 , $limit = 0 ) 
	{
		if ( $api_key == $this->config->item('parking_key_api_export') ) {
			$data['posts_query'] = $this->Parking_model->export_posts( $offset, $limit );
		}
		else {
			$data['posts_query'] = null;
		}

		$data['api_version'] = $this->config->item( 'parking_api_posts_version' );

		$this->load->view( 'api/posts' , $data );
	}

    public function export_panels( $api_key = NULL , $offset = 0 , $limit = 0 ) 
	{
		if ( $api_key == $this->config->item('parking_key_api_export') ) {
			$data['panels_query'] = $this->Parking_model->export_panels( $offset, $limit );
		}
		else {
			$data['panels_query'] = null;
		}

		$data['api_version'] = $this->config->item( 'parking_api_panels_version' );

		$this->load->view( 'api/panels' , $data );
	}

    public function export_panels_codes( $api_key = NULL ) 
	{
		if ( $api_key == $this->config->item('parking_key_api_export') ) {
			$data['panels_codes'] = $this->Parking_model->export_panels_codes();
		}
		else {
			$data['panels_codes'] = null;
		}

		$data['panel_code_type_parking'] = $this->config->item('parking_panel_code_type_parking');
		$data['panel_code_type_paid'] = $this->config->item('parking_panel_code_type_paid');
		$data['panel_code_type_desc_parking'] = $this->config->item('parking_panel_code_type_desc_parking');
		$data['panel_code_type_desc_paid'] = $this->config->item('parking_panel_code_type_desc_paid');

		$data['api_version'] = $this->config->item( 'parking_api_panels_codes_version' );

		$this->load->view( 'api/panels_codes' , $data );
	}

    public function export_panels_codes_rules( $api_key = NULL ) 
	{
		if ( $api_key == $this->config->item('parking_key_api_export') ) {
			$data['panels_codes_rules'] = $this->Parking_model->export_panels_codes_rules();
		}
		else {
			$data['panels_codes_rules'] = null;
		}

		$data['api_version'] = $this->config->item( 'parking_api_panels_codes_rules_version' );

		$this->load->view( 'api/panels_codes_rules' , $data );
	}

}
