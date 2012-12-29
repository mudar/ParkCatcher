/* Author: 
 * Mudar Noufal <mn@mudar.ca>
 */

var bodyHeight = 600;
var bodyWidth = 600;
$(document).ready(function() {
	
	getGeoJSON();
	
	$("#toggle_display, #txt_toggle_display").click( function(e) {
		e.preventDefault();
		var isMapHidden = $('body').hasClass('right');
		toggleContent(isMapHidden);
	});
		
	
	$(document).mouseup(function (e) {
		if (($("#main_wrapper").has(e.target).length === 0) && $('body').hasClass('right') && !$('body').hasClass('splash') )
		{
			toggleContent(true);
		}
	});
	
	
/*
	$( "a#btn_help" ).click( function(e) {
		e.preventDefault();
		$( $(this).attr( "href" ) ).slideToggle();
	} );
*/

} );


$(window).load(function(){  
	bodyHeight = $("#body").height() + 150 + 100 + 30;
	bodyWidth = $("#main").width();
	if ( $('body').hasClass('right') ) { bodyResize(); }
	
	$(".hint #main").delay(3000).animate({ width: "-=50" } , 200 , function() {
		$("#main").animate({ width: "+=50" } , { duration: "slow", easing: 'easeOutBounce', complete: function () {
			$("#main").css("width","100%");
		}});
	});
	
	if ( $( "#quiz_question_result.right" ).length > 0 ) {
		$( "#quiz_question_result" ).delay(3000).fadeOut( function() {
			$( "#quiz_subtitle" ).fadeIn();
		} );
	}
	
	if ( $( "#parking_time.important" ).length > 0 ) {
		$( "#parking_time.important" ).fadeTo( 'slow' , 0.3 , function() {
			$( "#parking_time.important" ).fadeTo( 'slow' , 1 );
		} );	
	}
});  

function toggleContent(isMapHidden ) {
	$("#main").clearQueue();
	$('body').toggleClass('left right');
	if(isMapHidden){ // left
		$("#toggle_display").attr('title',lang_map_hide);
		$("header, #body").fadeOut( 'fast' );
		$("#main").animate({ width: '10px' });
		$("#search").slideDown();
	}
	else { // right
		$("#toggle_display").attr('title',lang_map_show);
		$("#search").slideUp('fast');
		$("#main").animate({ width: bodyWidth+"px" } , function() { $("#main").css("width","100%"); bodyWidth = $("#main").width(); } );
		$("header, #body").fadeIn( 'slow' );
	}
}

function bodyResize() {
	if ( $("body").hasClass("panels_index") || $("body").hasClass("privacy_index")) { return false; }
	bodyWidth = $("#main").width();
	
	var maxHeight = ($(window).height() > bodyHeight ? "100%" : bodyHeight + "px" );
	$("body").css("height", maxHeight );
	$("#main_wrapper").css("height", maxHeight);
}

$(window).resize(function(){bodyResize();});


function globalAjaxCursorChange()   
{  
	$("html").bind("ajaxStart", function(){  
		$(this).addClass('busy');  
	}).bind("ajaxStop", function(){  
		$(this).removeClass('busy');  
	});  
}


// Quiz
$( ".btn_quiz" ).click( function(e) {
	e.preventDefault();
	var answer = ( $(this).attr("href") == "#yes" ? 1 : 0 );
	$( "#quiz_answer").val(answer);
	$( "#quiz_form" ).submit();
});