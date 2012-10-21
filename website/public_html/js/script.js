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
		if (($("#main_wrapper").has(e.target).length === 0) && $('body').hasClass('right'))
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
