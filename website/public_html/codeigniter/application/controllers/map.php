<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Map extends CI_Controller {

	public function index()
	{
// TODO: publish to GitHub!
		$this->search();
	}

	public function search() 
	{
// TODO: publish to GitHub!
		$this->renderer->render( 'welcome/index' , $data );
	}

}

