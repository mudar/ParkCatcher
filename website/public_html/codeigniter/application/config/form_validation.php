<?php

$config = array(
	'quiz' => array(
		array(
			'field'   => 'answer',
			'label'   => 'lang:parking_form_quiz_answer',
			'rules'   => 'required'
		)
	) ,
	'search' => array(
		array(
			'field'   => 'day',
			'label'   => 'lang:parking_form_search_day',
			'rules'   => 'required'
		),
		array(
			'field'   => 'hour',
			'label'   => 'lang:parking_form_search_hour',
			'rules'   => 'required'
		),
		array(
			'field'   => 'duration',
			'label'   => 'lang:parking_form_search_duration',
			'rules'   => 'required'
		),   
		array(
			'field'   => 'destination',
			'label'   => 'lang:parking_form_search_destination',
			'rules'   => 'trim'
		)
	)
); 
