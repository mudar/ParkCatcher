<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Welcome extends CI_Controller {

	public function index()
	{
		$data = array();

		if ( $this->input->cookie( 'skip_splash' , FALSE ) ) {

			$data['has_hint'] = !$this->input->cookie( 'has_searched' , FALSE ); 

			$this->renderer->render( 'welcome/index' , $data );
		}
		else {
			$this->splash();
		}
	}


	public function splash()
	{
		$data = array( 'is_splash' => TRUE ); 

		$this->input->set_cookie( 'skip_splash' , TRUE , 604800 );

		$this->renderer->render( 'welcome/splash' , $data );
	}

}

