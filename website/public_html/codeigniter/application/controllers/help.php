<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Help extends CI_Controller {

	public function index()
	{
		$data = array();

		$this->renderer->render( 'help/index' , $data );
	}

}

