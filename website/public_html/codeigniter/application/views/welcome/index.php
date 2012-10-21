<div id="body">
	<div id="home_subtitle"><p><?php echo lang( 'parking_home_subtitle' ) ?></p></div>

	<div id="home_desc">
		<ul>
			<li><?php echo lang( 'parking_home_desc_1' ) ?></li>
			<li><?php echo sprintf( lang( 'parking_home_desc_2' ) , 'href="#map" id="txt_toggle_display"' ) ?></li>
			<li><?php echo lang( 'parking_home_desc_3' ) ?></li>
			<li><?php echo lang( 'parking_home_desc_4' ) ?></li>
			<li><?php echo lang( 'parking_home_desc_5' ) ?></li>
		</ul>
	</div>

	<div id="home_quiz" class="box_home">
		<h2><a href="<?php echo site_url( lang( 'parking_route_welcome_quiz' ) ) ?>"><?php echo lang( 'parking_home_box_quiz' ) ?></a></h2>
	</div>

	<div id="home_help" class="box_home">
		<h2><a href="<?php echo site_url( lang( 'parking_route_welcome_help' ) ) ?>"><?php echo lang( 'parking_home_box_help' ) ?></a></h2>
	</div>

	<div id="home_about">
		<a href="<?php echo site_url( lang( 'parking_route_welcome_about' ) ) ?>"><img src="<?php echo base_url() ?>img/ic_arrow_list.png" alt="&gt;" /><?php echo lang( 'parking_home_box_about' ) ?><img src="<?php echo base_url() ?>img/ic_arrow_left.png" alt="&lt;" /></a>
	</div>
</div>

