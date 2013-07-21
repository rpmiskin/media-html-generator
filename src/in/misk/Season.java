package in.misk;

import java.util.List;

class Season {
	final String seasonName;
	final List<Link> episodes;

	Season(final String seasonName, final List<Link> episodes) {
		this.seasonName = seasonName;
		this.episodes = episodes;
	}

	@Override
	public String toString() {
		final StringBuilder b = new StringBuilder(seasonName);
		b.append('[');
		for (final Link e : episodes) {
			b.append(e.display);
			b.append(", ");
		}
		b.append(']');
		return b.toString();
	}
}