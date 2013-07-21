package in.misk;

class Link {
	static volatile int linkCount = 0;
	final int linkId = linkCount++;
	final String display;
	final String href;

	Link(final String display, final String href) {
		this.display = display;
		this.href = href.replaceAll(" ", "%20").replaceAll("\\\\", "/");
	}

	@Override
	public String toString() {
		return display + " : " + href;
	};

	String toAnchor() {
		return "<a href=" + href + ">" + display + "</a>";
	}

	/**
	 * Creates a img tag with 'video-src' attribute that can be swapped out by
	 * runtime js.
	 */
	String toVideo() {
		// return "<video src=" + href + " controls></video>";
		return "<img\n"
				+ " video-src=\""
				+ href
				+ "\"	class=\"swap-video controls noborder middle-vertical pixel-tweak\" src=\"http://static.bbci.co.uk/frameworks/barlesque/2.48.3/desktop/3.5/img/blocks/dark.png\"";

	}
}
