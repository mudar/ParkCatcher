<?php  if ( ! defined('BASEPATH')) exit('No direct script access allowed');
/*
| -------------------------------------------------------------------------
| URI ROUTING
| -------------------------------------------------------------------------
| This file lets you re-map URI requests to specific controller functions.
|
| Typically there is a one-to-one relationship between a URL string
| and its corresponding controller class/method. The segments in a
| URL normally follow this pattern:
|
|	example.com/class/method/id/
|
| In some instances, however, you may want to remap this relationship
| so that a different class/function is called than the one
| corresponding to the URL.
|
| Please see the user guide for complete details:
|
|	http://codeigniter.com/user_guide/general/routing.html
|
| -------------------------------------------------------------------------
| RESERVED ROUTES
| -------------------------------------------------------------------------
|
| There area two reserved routes:
|
|	$route['default_controller'] = 'welcome';
|
| This route indicates which controller class should be loaded if the
| URI contains no data. In the above example, the "welcome" class
| would be loaded.
|
|	$route['404_override'] = 'errors/page_missing';
|
| This route will tell the Router what URI segments to use if those provided
| in the URL cannot be matched to a valid route.
|
*/

$route['default_controller'] = "welcome";
$route['404_override'] = '';

$route['api'] = 'api/api';
$route['api/posts'] = 'api/api/export_posts';
$route['api/panels'] = 'api/api/export_panels';
$route['api/panels-codes'] = 'api/api/export_panels_codes';
$route['api/panels-codes-rules'] = 'api/api/export_panels_codes_rules';


// English
$route['home'] = 'welcome/index';
$route['welcome'] = 'welcome/splash';
$route['quiz'] = 'quiz/index';
$route['help'] = 'help/index';
$route['about'] = 'about/index';
$route['quiz-game-over'] = 'quiz/game_over';


// French
$route['accueil'] = 'welcome/index';
$route['bienvenue'] = 'welcome/splash';
$route['quiz'] = 'quiz/index';
$route['aide'] = 'help/index';
$route['a-propos'] = 'about/index';
$route['quiz-perdu'] = 'quiz/game_over';


/* End of file routes.php */
/* Location: ./application/config/routes.php */