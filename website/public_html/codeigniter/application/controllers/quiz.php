<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Quiz extends CI_Controller {

	public function index()
	{
		$data = array();

		$this->renderer->render( 'quiz/index' , $data );
	}

}

