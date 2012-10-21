<?php

/**
 * Prepare the three arrays for form_dropdown()
 */
$days = array();
$hours = array();
$durations = array();


// TODO: replace this by form validation
if ( !isset( $destination ) ) { $destination = NULL; }

for ( $i = 1 ; $i <= 7 ; $i++ ) {
	$days[ $i ] = lang( 'parking_day_' . $i );
}

for ( $i = 0 ; $i < 24 ; $i++ ) {
	$i_formatted = sprintf( '%02d' , $i );
	$hours[ strval( $i ) ] = lang( 'parking_start_' . $i_formatted . '_00' );
	$hours[ strval( $i + 0.5 ) ] = lang( 'parking_start_' . $i_formatted . '_30' );
}

$available_durations = array(1,2,3,4,5,6,12,24,48);
foreach( $available_durations as $i ) {
	$durations[ $i ] = lang( 'parking_duration_' . sprintf( '%02d' , $i ) );
}

?>

<div id="search" <?php if ( empty( $is_map ) ): ?>style="display: none;"<?php endif ?>>

	<div id="search_logo">
		<h2 id="search_title"><a href="<?php echo base_url() ?>"><span class="hidden"><?php echo lang( 'parking_search_title' ) ?></span></a></h2>
		<div id="search_subtitle"><?php echo lang( 'parking_search_subtitle' ) ?></div>
	</div>

	<?php echo form_open('map/search' ,  array( 'id' => 'search_form') ); ?>
	<fieldset>
		<label for="day"><?php echo lang( 'parking_form_day' ) ?></label>
		<div class="dropdown"><?php echo form_dropdown( 'day' , $days , $day , 'id="day"') ?></div>
	</fieldset>
	<fieldset>
		<label for="hour"><?php echo lang( 'parking_form_hour' ) ?></label>
		<div class="dropdown"><?php echo form_dropdown( 'hour' , $hours , $hour , 'id="hour"' ) ?></div>
	</fieldset>
	<fieldset>
		<label for="duration"><?php echo lang( 'parking_form_duration' ) ?></label>
		<div class="dropdown"><?php echo form_dropdown( 'duration' , $durations , $duration , 'id="duration"') ?></div>
	</fieldset>
	<fieldset>
		<label for="destination"><?php echo lang( 'parking_form_destination' ) ?></label>
		<div class="dropdown"><input id="destination" name="destination" type="text" value="<?php echo $destination ?>" /></div>
	</fieldset>
	<fieldset>
		<input type="submit" value="<?php echo lang( 'parking_form_submit' ) ?>" />
		<input type="hidden" name="submitted" value="1" />
	</fieldset>
	</form>

	<div id="help">
		<a href="#display_help" id="btn_help"><span><?php echo lang( 'parking_form_help_btn' ) ?></span></a>
		<div id="display_help"><?php echo lang( 'parking_form_help_desc' ) ?></div>
	</div>
</div>
<div id="bg_loader"></div>
