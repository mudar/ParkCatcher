<?php  if ( ! defined('BASEPATH')) exit('No direct script access allowed');

$config['parking_assets_version'] = '?v=14';

$config['parking_base_url_en'] = 'http://www.parkcatcher.com/';
$config['parking_base_url_fr'] = 'http://www.capteurdestationnement.com/';
$config['parking_url_playstore'] = 'http://play.google.com/store/apps/details?id=ca.mudar.parkcatcher';

$config['parking_geo_altitude'] = "0.0";

$config['parking_min_duration'] = 0.5;
$config['parking_max_duration'] = 48;

$config['parking_year_day_first'] = 0;	// As in PHP's date('z')
$config['parking_year_day_last'] = 364;	// Leap year's last day is 365

$config['parking_key_geocoder'] = '';

$config['parking_default_geo_lat'] = 45.508918;
$config['parking_default_geo_lon'] = -73.553617;

$config['parking_panel_code_type_parking'] = 1;
$config['parking_panel_code_type_paid'] = 2;
$config['parking_panel_code_type_desc_parking'] = 'STATIONNEMENT';
$config['parking_panel_code_type_desc_paid'] = 'STAT-$';

