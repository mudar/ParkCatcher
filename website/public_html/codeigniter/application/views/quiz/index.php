<div id="body" >

	<div id="quiz_intro">
<?php if ( !empty( $right_answer ) ): ?>
		<div id="quiz_question_result" class="right"><?php echo lang( 'parking_quiz_answer_right' ) ?></div>
<?php elseif ( !empty( $wrong_answer ) ): ?>
		<div id="quiz_question_result" class="wrong"><?php echo lang( 'parking_quiz_answer_wrong' ) ?></div>
<?php endif ?>
		<div id="quiz_subtitle" <?php echo ( !empty( $right_answer ) || !empty( $wrong_answer ) ? 'style="display: none"' : '' )?> ><?php echo lang( 'parking_quiz_subtitle' ) ?></div>
	</div>

<?php if ( !empty( $quiz['step'] ) ): ?>
	<div id="quiz_step"><?php printf( lang( $quiz['step'] == 1 ? 'parking_quiz_step' : 'parking_quiz_step_plural' ) , $quiz['step'] ) ?></div>
	<div id="quiz_score"><?php printf( lang( 'parking_quiz_score' ) , $quiz['score'] ) ?></div>
<?php endif ?>

	<div id="parking_time" <?php echo ( ( $quiz['step'] < 5 ) || !empty( $wrong_answer ) ? '' : 'class="important"' ) ?> >
<?php if ( $quiz['parking_time']['duration'] == 1 ): ?>
	<?php printf( lang( 'parking_quiz_date_desc' ) , $quiz['parking_time']['desc'] , $quiz['parking_time']['duration'] ); ?>
<?php else: ?>
	<?php printf( lang( 'parking_quiz_date_desc_plural' ) , $quiz['parking_time']['desc'] , $quiz['parking_time']['duration'] ); ?>
<?php endif ?>
	</div>

<?php if ( !empty( $quiz['questions'] ) ): ?>
		<div id="quiz_panels">
<?php foreach ( $quiz['questions'] as $q ): ?>
			<img class="quiz_panel" src="<?php echo base_url() ?>img/panels_codes/<?php echo $q['img'] ?>" alt="<?php echo $q['desc'] ?>" title="<?php echo $q['desc'] ?>" />
<?php endforeach ?>
		</div>

		<div id="quiz_form_wrapper">
			<?php echo form_open('quiz/next' , array( 'id' => 'quiz_form' ) ); ?>
				<fielset>
					<input type="hidden" id="quiz_answer" name="answer" value="" />
<?php if ( empty( $wrong_answer ) ): ?>
					<a class="btn_quiz yes" href="#yes"><?php echo lang( 'parking_quiz_btn_yes' ) ?></a>
					<a class="btn_quiz no" href="#no"><?php echo lang( 'parking_quiz_btn_no' ) ?></a>
<?php else: ?>
	<div id="quiz_answer_correction">
		<?php printf( lang( 'parking_quiz_answer_correction' ) , '<span class="big">' . $answer_correction . '</span>' ) ?>
	</div>
	<ul>
		<li><a href="<?php echo site_url( lang( 'parking_route_welcome_quiz' ) ) ?>"><?php echo lang( 'parking_quiz_try_again' ) ?></a></li>
	</ul>
<?php endif ?>
				</fielset>
			</form>
		</div>

<?php if ( validation_errors() != "" ): ?>
		<div id="quiz_form_errors" class="validation_errors"><?php echo lang( 'parking_form_quiz_message' ) ?></div>
<?php endif ?>

<?php endif ?>

</div>
