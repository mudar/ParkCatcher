<?php  if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Renderer
{
	private $layout;
	private $ajax_layout;
	private $json_layout;
	private $site_title;
	private $html_title;
	private $display_navig_menu;
	private $display_navig_search;
	private $pagination;

	function __construct()
	{
		$this->layout = 'page';
		$this->ajax_layout = 'page_ajax';
		$this->json_layout = 'json_response';
		$this->display_navig_menu = TRUE;
		$this->pagination = NULL;

		$CI =& get_instance();
		$this->site_title = lang( 'parking_site_title' );
		$this->html_title = lang( 'parking_html_title' );
	}


	public function set_layout( $layout )
	{
		$this->layout = $layout;
	}

	public function set_html_title( $html_title , $prepend = TRUE )
	{
		$this->html_title = ( $prepend ? $html_title . ' - ' . $this->html_title : $html_title );
	}

	public function set_site_title( $site_title )
	{
		$this->site_title = $site_title;
	}

	public function json_response( $json_data )
	{
		if ( IS_AJAX ) {
			$CI =& get_instance();
			$CI->load->view( $this->json_layout , array( 'data' => $json_data ) );
			return TRUE;
		}
		else {
			return FALSE;
		}
	}

	public function render( $view , $data = null )
	{
		if ( empty( $data) ) { $data = array(); }

		$CI =& get_instance();

		$data['body_class'] = $CI->router->class . '_' . $CI->router->method . ' ' . lang('parking_site_lang');
		$data['body_class'] .= ( $CI->router->class == 'map' ? ' left' : ' right' );
		$data['site_title'] = $this->site_title;
		$data['html_title'] = $this->html_title;
		$data['parking_alternate_lang'] = ( lang('parking_site_lang') == 'en' ? 'fr' : 'en' );
		$data['parking_alternate_url'] = $CI->config->item( 'parking_base_url_' . $data['parking_alternate_lang'] );
		$data['view'] = $view;

		$data_search = $this->_init_search_engine( $data );
		$data =  array_merge( $data , $data_search );

// 		$CI->output->enable_profiler( ENVIRONMENT == 'development' );

		if ( !empty( $data['is_splash'] ) ) { $data['body_class'] .= ' splash'; }
		if ( !empty( $data['has_hint'] ) ) { $data['body_class'] .= ' hint'; }

		$data['has_searched'] = ( $CI->input->cookie( 'has_searched' , FALSE ) || !empty( $data['has_searched'] ) );

		$CI->load->view( ( IS_AJAX ? $this->ajax_layout : $this->layout ) , $data );
	}

	private function _init_search_engine( $data )
	{
		$CI =& get_instance();

		$min_duration = $CI->config->item('parking_min_duration');
		$max_duration = $CI->config->item('parking_max_duration');

		$assets_version = $CI->config->item('parking_assets_version');

		$data_search = array();

		$day = ( empty( $data['day'] ) ? -1 : $data['day'] );
		$hour = ( empty( $data['hour'] ) ? -1 : $data['hour'] );
		$duration = ( empty( $data['duration'] ) ? -1 : $data['duration'] );

		$now = getdate();
		$year_day = $now['yday'];

		if ( !is_numeric( $day ) || ( $day < 1 ) || ( $day > 7 ) ) {
			$day = ( $now['wday'] == 0 ? 7 : $now['wday'] ); // We start on monday (ISO), php getdate() returns 0 (for Sunday) through 6 (for Saturday)
		}
		if ( !is_numeric( $hour ) || ( $hour < 1 ) || ( $hour > 24 ) ) {
			$hour = $now['hours'] + ( $now['minutes'] < 30 ? 0 : 0.5 );
		}
		if ( !is_numeric( $duration ) || ( $duration < $min_duration ) || ( $duration > $max_duration ) ) {
			$duration = 2;
		}

		$data_search['day'] = $day;
		$data_search['hour'] = $hour;
		$data_search['duration'] = $duration;
		$data_search['assets_version'] = $assets_version;

		return $data_search;
	}

}
