<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class About extends CI_Controller {

	public function index()
	{
		$data = array();

		$this->renderer->render( 'about/index' , $data );
	}

}

