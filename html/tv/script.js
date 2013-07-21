/**
 * Any elements with class accordion are treated as a jquery-ui accordion and collapsed.
 */
$(function() {
	$(".accordion").accordion({
		collapsible : true,
		active : false,
	});
	/* Detect clicks on a video and start playing it. */
	/*
	 * $('.player_video').click(function() { if (this.paused == false) {
	 * this.pause(); } else { this.play(); } });
	 */
});

/**
 * Mechanism to prevent autobuffering of video tags. Rather than using a video
 * tag with a poster use an img tag of class swap-video and suppliy a
 * 'video-src' attribute. On click the img is swapped for some video that
 * autoplays.
 * 
 */
$(document).ready(
		function() {

			$("img.swap-video").click(
					function(event) {
						// Currently only supports a single codec.
						var codecs = {
							'mp4' : 'video/mp4'
						};
						var a, c, p, s = '';

						// Always autoplay
						a = 'autoplay';
						if ($(this).is('.controls'))
							a += ' controls';
						if ($(this).is('.autobuffer'))
							a += ' autobuffer';
						p = $(this).attr('src').substr(0,
								$(this).attr('src').lastIndexOf('.') + 1);
						s = '';
						for (c in codecs) {
							s += '<source src="' + $(this).attr("video-src")
									+ '" type="' + codecs[c] + '"';
						}
						$(this).replaceWith(
								'<video ' + a + ' >' + s
										+ '</video>');
					});
		});