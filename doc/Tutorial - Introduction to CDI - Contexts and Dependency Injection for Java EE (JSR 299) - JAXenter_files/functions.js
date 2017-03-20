$(document).ready(function() {
	var locked = false;

	$('#searchbutton').on('click', function(event) {
		event.preventDefault();

		if (!locked) {
			$('.search-wrapper').toggleClass('active');
			$('.search-input').focus();
		}
	});
	
	$('.search-label').on('click', function(event) {
		if (!locked) {
			$('.search-wrapper').toggleClass('active');
			$('.search-input').focus();
		}
	});	

	$('form').each(function() {
		$(this).find('input').keypress(function(e) {
		    if (e.which == 10 || e.which == 13) {
				locked = true;
				this.form.submit();
			}
		});
	});
	
	$.each($('a[rel="popup"]'), function(key, value) {
		var $e  = $(value);
		var url = $e.attr('href');

		$e.on('click', function(e) {
			var win = window.open(url, 'Share the Post!', 'width=500,height=450,resizable=yes');
			win.focus();

			return false;
		});
	});

	$.each($('.num-shares'), function(key, value) {
		var element = $(value);

		var platformKey  = element.data('platform-key');
		var platformName = element.data('platform-name');
		var url          = element.data('url');
		var urlOld       = element.data('url-old');

		getShares(platformKey, url, urlOld, function(numShares) {
			if (numShares == 0) {
				element.html(platformName);
			} else {
				element.html(numShares);
			}
		});
	});

	var insertedSidebar = false;

	$('.navbar-toggle').on('click', function(e) {
		if (insertedSidebar === false) {
			var navigation = $('#menu-main-menu').clone();
			var topics     = $('.topics-list').clone();
			var sidebar    = $('<div id="mobilenav">');

			sidebar.append(navigation);
			sidebar.append(topics);
			$('body').append(sidebar);
			$('#mobilenav').animate({
				left: '0px'
			}, 250);
			
			insertedSidebar = true;
		} else {
			$('#mobilenav').animate({
				left: '-200px'
			}, 250, function() {
				$('#mobilenav').remove();
			});

			insertedSidebar = false;
		}
	});

	if ($(window).width() > 992) {
		initSticky();
	}

	$(window).resize(function() {
		if ($(window).width() > 992) {
			initSticky();
		}
	});

	/*$(window).scroll(function() {
		$('.sticky').each(function(index) {
			var $e = $(this);

			if ($(window).scrollTop() + $(window).height() < $e.data('sticky-max')) {
				if ($(window).scrollTop() + $(window).height() >= $e.data('sticky-min')) {
					$e.css({
						position: 'fixed',
						left: $e.offset().left + 'px',
						bottom: '0px'
					});
				} else {
					$e.css({
						position: 'static'
					});
				}
			}
		});
	});*/
	
	$(window).scroll(function() {

		if ($(this).scrollTop()>0)
		 {
			$('.cc_banner-wrapper').fadeOut();
		 }
		else
		 {
		  $('.cc_banner-wrapper').fadeOut();
		 }
	 });

});

function initSticky() {
	$('.sticky').each(function(index) {
		var $e = $(this);

		$e.data('sticky-min', $e.offset().top + $(this).height());
		$e.data('sticky-max', $e.parent().offset().top + $(this).parent().height());
	});
}

function getShares(platform, url, urlOld, callback) {
	var getUrl = templateDirUrl + '/count.php?platform=' + platform + '&url=' + url + '&old=' + urlOld;

	$.get(getUrl, function(result) {
		if (typeof result.shares == 'undefined') {
			callback(false);
		} else {
			callback(result.shares);
		}
	});
}