<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Panels extends CI_Controller {

	public function index()
	{
		$data = array();

		$panels_codes_all = $this->Parking_model->get_panels_codes();

		$img_path = $this->config->item('parking_img_panels_codes_path');

		$panels_images = scandir($img_path);

		$data['panels_codes'] = array();

		foreach ( $panels_codes_all as $v ) {
			if ( in_array( $v['code'] . '.png' , $panels_images ) ) {
				$data['panels_codes'][ $v['code'] ] = $v['description'];
			}
		}

		$this->renderer->render( 'panels/index' , $data );
	}

}

