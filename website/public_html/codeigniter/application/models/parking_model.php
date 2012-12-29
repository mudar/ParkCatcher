<?php
class Parking_model extends CI_Model
{
	function __construct()
	{
		// Call the Model constructor
		parent::__construct();
	} 

	/**
	 * Get the posts info
	 * @return Array
	 */
	function get_posts( $start = -1 , $duration = -1 , $yeary_day = -1 , $NW_lat = NULL , $NW_lon = NULL , $SE_lat = NULL , $SE_lon = NULL )
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		if ( $query->num_rows() == 0 ) { return FALSE; }
		else {
			return $query;
		}
	}

	function check_panels_allowed( $start = -1 , $duration = -1 , $yeary_day = -1 , $ids = NULL )
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		return ( $query->num_rows() == 0 );
	}

	function get_panels_codes() 
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		if ( $query->num_rows() == 0 ) { return FALSE; }
		else {
			return $query->result_array();
		}
	}

	function get_panels_codes_rules_total() 
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		if ( $query->num_rows() == 0 ) { return FALSE; }
		else {
			return $query->result_array();
		}
	}

	function get_quiz_panels( $excluded_ids = NULL , $nb_panels = 1 )
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		if ( $query->num_rows() == 0 ) { return FALSE; }
		else {
			return $query->result_array();
		}
	}

	function get_quiz_panels_by_id( $ids = NULL )
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		if ( $query->num_rows() == 0 ) { return FALSE; }
		else {
			return $query->result_array();
		}
	}

	function export_posts() 
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		if ( $query->num_rows() == 0 ) { return FALSE; }
		else {
			return $query;
		}
	}

	function export_panels() 
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		if ( $query->num_rows() == 0 ) { return FALSE; }
		else {
			return $query;
		}
	}

	function export_panels_codes() 
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		if ( $query->num_rows() == 0 ) { return FALSE; }
		else {
			return $query->result_array();
		}
	}

	function export_panels_codes_rules() 
	{
		$sql_query = 'SELECT * FROM table';

		$query = $this->db->query( $sql_query );

		if ( $query->num_rows() == 0 ) { return FALSE; }
		else {
			return $query->result_array();
		}
	}
}
