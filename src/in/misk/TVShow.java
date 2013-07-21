package in.misk;

import java.util.List;

class TVShow {
	final List<Link> episodes;
	final List<Season> seasons;
	final String showName;

	TVShow(final String showName, final List<Link> episodes,
			final List<Season> seasons) {
		this.showName = showName;
		this.seasons = seasons;
		this.episodes = episodes;
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder(showName);
		b.append('[');
		for (final Season s : seasons) {
			b.append(s);
			b.append(", ");
		}
		b.append(']');
		b.append('[');
		for (final Link e : episodes) {
			b.append(e.display);
			b.append(", ");
		}
		b.append(']');

		return b.toString();
	}
}