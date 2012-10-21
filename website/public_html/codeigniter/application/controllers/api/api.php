<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Api extends CI_Controller {

	public function index() 
	{
// TODO: publish to GitHub!
		$this->load->view( 'api/index' , $data );
	}


}
