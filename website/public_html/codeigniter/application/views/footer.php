<?php
$is_hidden_footer_search_desc = !empty( $has_searched );
$is_hidden_footer_error_address = ( !$is_hidden_footer_search_desc || empty( $address_error ) );
$is_hidden_footer_enter_address = ( !$is_hidden_footer_search_desc || !$is_hidden_footer_error_address || ( !empty( $destination ) && empty( $is_empty_address ) ) );

$is_hidden_footer_wrapper = ( $is_hidden_footer_search_desc && $is_hidden_footer_error_address && $is_hidden_footer_enter_address );

?>
<div id="footer_wrapper" <?php echo ( $is_hidden_footer_wrapper ? 'style="display: none;"' : '' ) ?>>
	<div id="footer_content">
		<div id="footer_hr"></div>
		<span class="hint" id="footer_search_desc" <?php echo ( $is_hidden_footer_search_desc == TRUE ? 'style="display: none;"' : '' ) ?>><?php echo lang( 'parking_footer_search_desc' ) ?></span>
		<span class="hint" id="footer_zoom_in" style="display: none;" ><?php echo lang( 'parking_footer_zoom_in' ) ?></span>
		<span class="hint" id="footer_error_address" <?php echo ( $is_hidden_footer_error_address == TRUE ? 'style="display: none;"' : '' ) ?>><?php echo ( empty( $address_error ) ? '' : $address_error ) ?></span>
		<span class="hint" id="footer_enter_address" <?php echo ( $is_hidden_footer_enter_address == TRUE ? 'style="display: none;"' : '' ) ?>><?php echo lang( 'parking_footer_enter_address' ) ?></span>
	</div>
</div>

