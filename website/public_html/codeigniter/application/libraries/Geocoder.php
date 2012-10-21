<?php  if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Geocoder
{

	private $auth_key;
	private $url_query;

	function __construct()
	{
		$CI =& get_instance();
		$this->auth_key = $CI->config->item( 'parking_key_geocoder' );

		$this->url_query = 'http://geocoder.ca/?auth=%s&city=%s&prov=%s&locate=%s&geoit=xml&showpostal=1';
	}


	function get_geo_point( $address , $city = 'montreal' , $province = 'QC' )
	{
		$result = array( 'lat' => NULL , 'lon' => NULL );

		$address = trim( $address );

		if ( empty( $address ) ) {
			$CI =& get_instance();

			$result['lat'] = $CI->config->item( 'parking_default_geo_lat' );
			$result['lon'] = $CI->config->item( 'parking_default_geo_lon' );
			$result['is_empty_address'] = TRUE;
		}
		else {
			if ( ( strlen( $address ) <= 7 ) ) {
				$postal_code = str_replace( array( ' ' , '-' ) , '' , $address );
				if ( $this->_is_postal_code( $postal_code ) ) {
					$address = strtoupper( $postal_code );
				}
			}

			$url = sprintf( $this->url_query , $this->auth_key , $city , $province , urlencode( $address ) );

			$xml_string = file_get_contents($url );
			$simple_xml = simplexml_load_string( $xml_string );

			if ( !empty( $simple_xml->error ) ) {
				$result['error'] = array( 'code' => (string) $simple_xml->error->code , 'desc' => (string) $simple_xml->error->description );
				$result['is_empty_address'] = TRUE;
			}
			else {
				$postal_code = $simple_xml->postal;
				$result['lat'] = (float) $simple_xml->latt;
				$result['lon'] = (float) $simple_xml->longt;
			}
		}

		return $result;
	}

	function _get_cached()
	{

	}

	function _is_postal_code( $postal_code )
	{
		//function by Roshan Bhattara(http://roshanbh.com.np)
		if( preg_match( "/^([a-ceghj-npr-tv-z]){1}[0-9]{1}[a-ceghj-npr-tv-z]{1}[0-9]{1}[a-ceghj-npr-tv-z]{1}[0-9]{1}$/i" , $postal_code ) )
			return TRUE;
		else
			return FALSE;
	}
}