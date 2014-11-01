<!doctype html>
<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="<?php echo lang( 'parking_site_lang' ) ?>"> <![endif]-->
<!--[if IE 7]>    <html class="no-js lt-ie9 lt-ie8" lang="<?php echo lang( 'parking_site_lang' ) ?>"> <![endif]-->
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="<?php echo lang( 'parking_site_lang' ) ?>"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="<?php echo lang( 'parking_site_lang' ) ?>"> <!--<![endif]-->
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <title><?php echo $html_title ?></title>
  <meta name="description" content="<?php echo lang( 'parking_site_desc' ) ?>">

  <link rel="alternate" hreflang="<?php echo $parking_alternate_lang ?>" href="<?php echo $parking_alternate_url ?>" />
  <link rel="shortcut icon" type="image/x-icon" href="<?php echo base_url() ?>favicon.ico" >
  <link rel="icon" type="image/png" href="<?php echo base_url() ?>img/favicon.png" >
  <link rel="image_src" href="<?php echo base_url() ?>img/ic_park_catcher.png" >

  <meta property="og:title" content="<?php echo $html_title ?>" />
  <meta property="og:site_name" content="<?php echo $html_title ?>" />
  <meta property="og:type" content="website" />
  <meta property="og:url" content="<?php echo base_url() ?>" />
  <meta property="og:image" content="<?php echo base_url() ?>img/ic_park_catcher.png" />
  <meta property="og:description" content="<?php echo lang( 'parking_site_desc' ) ?>" />
  <meta property="og:locale" content="<?php echo lang( 'parking_site_lang' ) ?>_CA" />

  <meta name="viewport" content="width=1050, initial-scale=1.0, maximum-scale=1.0, user-scalable=yes" >
  <link rel="stylesheet" href="<?php echo base_url() ?>js/libs/leaflet_0.4.4/leaflet.css<?php echo $assets_version ?>" >
  <!--[if lte IE 8]>
    <link rel="stylesheet" href="<?php echo base_url() ?>js/libs/leaflet_0.4.4/leaflet.ie.css" />
  <![endif]-->
  <link rel="stylesheet" href="<?php echo base_url() ?>css/style.css<?php echo $assets_version ?>">

  <script src="<?php echo base_url() ?>js/libs/modernizr-2.5.3.min.js"></script>
  
</head>
<body class="<?php echo $body_class ?>">
  <!--[if lt IE 7]><p class=chromeframe>Your browser is <em>ancient!</em> <a href="http://browsehappy.com/">Upgrade to a different browser</a> or <a href="http://www.google.com/chromeframe/?redirect=true">install Google Chrome Frame</a> to experience this site.</p><![endif]-->

  <div id="main_wrapper">
    <div role="main" id="main">
	<a href="#map" id="toggle_display" title="<?php echo lang( 'parking_display_show_map' ) ?>"><span class="hidden"><?php echo lang( 'parking_display_show_map' ) ?></span></a>
      <header>
<?php echo $this->load->view( 'header' ) ?>
      </header>
<?php echo $this->load->view( $view ) ?>
    </div>
  </div>

  <div id="search_wrapper">
<?php echo $this->load->view( 'search/index' ) ?>
  </div>

  <div id="map_wrapper">
<?php echo $this->load->view( 'map/index' ) ?>
  </div>

  <footer>
<?php echo $this->load->view( 'footer' ) ?>
  </footer>


  <script src="//ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
  <script>window.jQuery || document.write('<script src="<?php echo base_url() ?>js/libs/jquery-1.7.1.min.js"><\/script>')</script>

  <script src="<?php echo base_url() ?>js/libs/jquery.easing.1.3.js"></script>
  <script src="<?php echo base_url() ?>js/plugins.js"></script>
  <script src="<?php echo base_url() ?>js/script.js<?php echo $assets_version ?>"></script>

  <script type="text/javascript">
  // <![CDATA[
    var lang_map_show = "<?php echo lang( 'parking_display_show_map' ) ?>";
    var lang_map_hide = "<?php echo lang( 'parking_display_hide_map' ) ?>";
  // ]]>
  </script>

</body>
</html>
