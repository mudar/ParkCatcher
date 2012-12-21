<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Quiz extends CI_Controller {

	public function index()
	{
		$data = array();

		$data['quiz'] = $this->_get_user_quiz_data();

		$data['right_answer'] = $this->session->flashdata( 'right_answer' );

		$this->renderer->render( 'quiz/index' , $data );
	}

	public function next()
	{
		$this->load->helper( 'form' );
		$this->load->library( 'form_validation' );

		if ( $this->form_validation->run( 'quiz' ) ) {
			$quiz_data = $this->session->userdata( 'quiz_data' );

			$is_allowed = $this->Parking_model->check_panels_allowed( $quiz_data['parking_time']['start'] , $quiz_data['parking_time']['duration'] , $quiz_data['parking_time']['year_day'] , $quiz_data['id_questions'] );
			$user_answer = ( set_value( 'answer' ) == 1 );

			if ( $is_allowed == $user_answer ) {
				$quiz_data['step']++;
				$quiz_data['score'] += $quiz_data['questions_points'];
				$quiz_data['answered'] = array_merge( $quiz_data['answered'] , $quiz_data['id_questions'] );

				$questions = $this->_get_new_question( $quiz_data['answered'] );

				$quiz_data['id_questions'] = array();
				$quiz_data['questions_points'] = 0;
				foreach ( $questions as $v ) {
					$quiz_data['id_questions'][] = $v['id_panel_code'];
					$quiz_data['questions_points'] += $v['points'];
				}

				$quiz_data['parking_time'] = $this->_get_parking_time( $quiz_data['step'] );

				$this->session->set_userdata( array( 'quiz_data' => $quiz_data ) );

				$this->session->set_flashdata('right_answer', TRUE );

				redirect( lang( 'parking_route_welcome_quiz' ) );
			}
			else {
				$this->session->set_flashdata('wrong_answer', TRUE );
				$this->session->set_flashdata('answer_correction', $is_allowed );

				redirect( lang( 'parking_route_welcome_quiz_game_over' ) );
			}
		}
		else {
			$this->index();
		}
	}

	public function game_over() 
	{

		$data = array();

		$data['wrong_answer'] = $this->session->flashdata( 'wrong_answer' );
		$data['answer_correction'] = ( $this->session->flashdata( 'answer_correction' ) == TRUE ? lang( 'parking_quiz_answer_correction_yes' ) : lang( 'parking_quiz_answer_correction_no' ) );

		$data['quiz'] = $this->session->userdata( 'quiz_data' );

		if ( empty( $data['quiz'] ) || empty( $data['wrong_answer'] ) ) {
			redirect( lang( 'parking_route_welcome_quiz' ) );
		}
		
		$data['quiz']['questions'] = $this->Parking_model->get_quiz_panels_by_id( $data['quiz']['id_questions'] );

		$this->session->unset_userdata( 'quiz_data' );

		$this->renderer->render( 'quiz/index' , $data );
	}


	public function reset () 
	{
		$this->session->unset_userdata( 'quiz_data' );
		redirect('quiz/');
	}


	private function _get_parking_time( $step = 0 , $time = NULL ) 
	{
		if ( empty( $time ) ) {
			$time = time();
		}

		$date = getdate( $time );

		if ( $step < 5 ) {
			// The 5 first questions are based on the current time

			$hour = $date['hours'] + round( $date['minutes'] / 60 , 2 );

			$duration = 2;
		}
		else {
			$time_h = rand( 6 , 22 );
			$time_m = rand( 0 , 1 );
			$time_m = ( empty( $time_m ) ? 0 : 30 );
			$time_month = rand( 1 , 12 );
			$time_day = rand( 0 , 28 );

			$time = mktime( $time_h , $time_m , 0 , $time_month , $time_day , $date['year'] );
			$date = getdate( $time );

			$hour = $time_h + ( empty( $time_m ) ? 0 : 0.5 );

			$available_durations = array(1,2,3,4,5,6,12,24,48);
			$duration = $available_durations[ array_rand($available_durations) ];
		}

		$wday = ( $date['wday'] == 0 ? 7 : $date['wday'] );
		$year_day = $date['yday'];


		setlocale( LC_TIME , lang( 'parking_site_locale' ) );
		$result['desc'] = strftime( lang( 'parking_quiz_date_format' ) , $time );

		$result['start'] = $hour + ( ( $wday -1 ) * 24 );
		$result['duration'] = $duration;
		$result['year_day'] = $year_day;
 
		$result['time'] = $time;

		return $result;
	}

	private function _get_user_quiz_data() 
	{
		$result_data = array();

		$quiz_data = $this->session->userdata( 'quiz_data' );

		$needs_session_save = FALSE;

		if ( empty( $quiz_data['answered'] ) ) {
			$quiz_data['answered'] = array();
			$needs_session_save = TRUE;
		}
		if ( empty( $quiz_data['score'] ) ) {
			$quiz_data['score'] = 0;
			$needs_session_save = TRUE;
		}
		if ( empty( $quiz_data['step'] ) ) {
			$quiz_data['step'] = 0;
			$needs_session_save = TRUE;
		}
		/**
		 * Get user's last questions details or get new question
		 */
		if ( ( $quiz_data['step'] == 0 ) || empty( $quiz_data['id_questions'] ) || empty( $quiz_data['questions_points'] ) ) {
			$questions = $this->_get_new_question( $quiz_data['answered'] );

			$quiz_data['id_questions'] = array();
			$quiz_data['questions_points'] = 0;
			foreach ( $questions as $v ) {
				$quiz_data['id_questions'][] = $v['id_panel_code'];
				$quiz_data['questions_points'] += $v['points'];
			}
			$needs_session_save = TRUE;
		}
		else {
			$questions = $this->Parking_model->get_quiz_panels_by_id( $quiz_data['id_questions'] );
		}

		/**
		 * Get parking time
		 */
		if ( empty( $quiz_data['parking_time'] ) ) {
			$quiz_data['parking_time'] = $this->_get_parking_time( $quiz_data['step'] , time() );
			$needs_session_save = TRUE;
		}

		if ( $needs_session_save ) {
			$this->session->set_userdata( array( 'quiz_data' => $quiz_data ) );
		}

		$result_data['step'] = $quiz_data['step'];		
		$result_data['score'] = $quiz_data['score'];
		$result_data['questions'] = $questions;
		$result_data['parking_time'] = $quiz_data['parking_time'];

		return $result_data;
	}

	/**
	 * Generate a new question
	 */
	private function _get_new_question( $answered = NULL )
	{
		$rand = rand(1,10);
		if ( $rand <= 5 ) {
			$nb_panels = 1;
		}
		elseif ( $rand <= 8 ) {
			$nb_panels = 2;
		}
		else  {
			$nb_panels = 3;
		}

		$panels = $this->Parking_model->get_quiz_panels( $answered , $nb_panels );

		return $panels;
	}

}

