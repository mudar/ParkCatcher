<div id="body">
<?php if ( !empty( $panels_codes ) && is_array( $panels_codes ) ): ?>
<ul>
<?php foreach( $panels_codes as $code => $description ): ?>

<li><?php echo $description ?><br /><img src="<?php echo base_url() ?>img/panels_codes/<?php echo $code ?>.png" alt="<?php echo $description ?>" title="<?php echo $description ?>"/></li>

<?php endforeach ?>
</ul>
<?php endif ?>
</div>
