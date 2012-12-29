<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Privacy extends CI_Controller {

	public function index()
	{
		$data = array();

		$this->renderer->render( 'privacy/index' , $data );
	}

}

 