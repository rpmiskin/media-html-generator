package in.misk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class TVShowHTMLGenerator {

	private static final String THE_BOX_HEADER = "		<div id=\"header\">\n"
			+ "			<h1>\n"
			+ "				<a href=\"http://192.168.0.100\">The Box</a> - <a\n"
			+ "					href=\"http://192.168.0.100/tv\">TV</a>\n"
			+ "			</h1>\n</div>";
	private static final String HEAD = "<head>\n"
			+ "<link rel=\"stylesheet\" href=\"style.css\" />\n"
			+ "<link rel=\"stylesheet\"\n"
			+ "	href=\"http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css\" />\n"
			+ "<link href='http://fonts.googleapis.com/css?family=Luckiest+Guy'\n"
			+ "	rel='stylesheet' type='text/css'>\n"
			+ "<script src=\"http://code.jquery.com/jquery-1.9.1.js\"></script>\n"
			+ "<script src=\"http://code.jquery.com/ui/1.10.3/jquery-ui.js\"></script>\n"
			+ "<script src=\"script.js\"></script>\n" + "\n" + "</head>";
	/** Directory to parse to find TV shows. */
	private static final File TV_DIR = new File(
			"/Users/richard/Documents/workspace/HTMLGen/video/tv");
	// private static final File TV_DIR = new File("D:\\iTunes\\TV Shows");
	/** Root URL for all TV shows. */
	private static final String TV_ROOT = "http://192.168.0.100/media/tv/";
	/** Directory into which the html files will be generated. */
	// private static final File HTML_ROOT = new
	// File("D:\\inetpub\\wwwroot\\tv");

	private static final File HTML_ROOT = new File(
			"/Users/richard/Documents/workspace/HTMLGen/html/tv");

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		final TVShowHTMLGenerator generator = new TVShowHTMLGenerator();
		// Loop over TV_DIR creating a data structure to process.
		// Each directory is a show
		// Each directory below that is a season.
		final List<TVShow> shows = generator.getShows(TV_DIR, HTML_ROOT);
		// Create a page that links to all of the TV shows.
		final List<Link> showLinks = new ArrayList<Link>();
		for (final TVShow show : shows) {
			showLinks.add(generator.createHTMLForTVShow(show));
			System.out.println(show.showName);
			for (final Link episode : show.episodes) {
				System.out.println("\t" + episode.toString());
			}
			for (final Season season : show.seasons) {
				System.out.println("\t" + season.seasonName);
				for (final Link episode : season.episodes) {
					System.out.println("\t\t" + episode.toString());
				}
			}
		}
		// Create an index page that links all of the individual show pages
		createShowIndex(showLinks);
	}

	private static void createShowIndex(final List<Link> showLinks)
			throws FileNotFoundException {
		final File file = new File(HTML_ROOT, "index.html");
		final PrintWriter fileWriter = new PrintWriter(file);
		file.getParentFile().mkdirs();
		fileWriter.println("<html>\n" + HEAD + "<body>" + "<div id=\"main\">"
				+ THE_BOX_HEADER + "<div id=\"content\">");
		for (final Link showLink : showLinks) {
			fileWriter.write("<span class=\"showlink\"><a href=\"");
			fileWriter.write(showLink.href);
			fileWriter.write("\">");
			fileWriter.write(showLink.display);
			fileWriter.println("</a></span>");
		}
		fileWriter.println("</div>");
		fileWriter.println("</div></body></html>");
		fileWriter.close();
	}

	/**
	 * @param show
	 * @return
	 */
	Link createHTMLForTVShow(final TVShow show) throws IOException {
		final String pageName = show.showName + ".html";
		final File file = new File(HTML_ROOT, pageName);
		file.getParentFile().mkdirs();
		final PrintWriter fileWriter = new PrintWriter(file);
		fileWriter.println("<html>\n" + HEAD + "<body>" + "<div id=\"main\">"
				+ THE_BOX_HEADER + "<div id=\"content\">");
		fileWriter.println("<h2>" + show.showName + "</h2>");
		// Episodes without a season
		if (!show.episodes.isEmpty()) {
			if (!show.seasons.isEmpty()) {
				fileWriter.println("<h3 class=\"seasonName\"> No Season </h3>");
			}
			fileWriter.println("<div class=\"Xaccordion\">");
			for (final Link episode : show.episodes) {
				fileWriter.print("<h3>" + episode.display + "</h3>");
				fileWriter.print("<p>");
				fileWriter.println(episode.toVideo());
				fileWriter.print("</p>");
			}
			fileWriter.println("</div>");
		}
		// Seasons and related episodes
		if (!show.seasons.isEmpty()) {
			for (final Season season : show.seasons) {
				fileWriter.println("<h3 class=\"seasonName\">"
						+ season.seasonName + "</h3>");
				fileWriter.println("<div class=\"Xaccordion\">");
				for (final Link episode : season.episodes) {
					fileWriter.print("<h3>" + episode.display + "</h3>");
					fileWriter.print("<p>");
					fileWriter.println(episode.toVideo());
					fileWriter.print("</p>");
				}
				fileWriter.println("</div>");
			}
		}
		fileWriter.println("</div>");
		fileWriter.println("</div></body></html>");
		fileWriter.close();
		return new Link(show.showName, "./" + pageName);
	}

	/**
	 * Loops over a folder generating TVShow objects. Each show can contain
	 * seasons.
	 * 
	 * 
	 * @throws IOException
	 */
	private List<TVShow> getShows(final File tvDir, final File htmlRoot)
			throws IOException {
		final List<TVShow> shows = new ArrayList<TVShow>();
		for (final File file : tvDir.listFiles()) {
			if (file.isDirectory()) {
				shows.add(createShow(file, htmlRoot));
			}
		}
		return shows;
	}

	private static TVShow createShow(final File showDir, final File htmlRoot)
			throws IOException {
		final String showName = showDir.getName();
		final List<Link> episodes = new ArrayList<Link>();
		final List<Season> seasons = new ArrayList<Season>();
		for (final File f : showDir.listFiles()) {
			if (f.isDirectory()) {
				// This is a season
				seasons.add(createSeason(f));
			} else {
				if (isVideo(f)) {
					// This is an episode
					episodes.add(createVideoLink(f));
				}
			}
		}
		return new TVShow(showName, episodes, seasons);
	}

	private static Season createSeason(final File seasonDir) throws IOException {
		final String seasonName = seasonDir.getName();
		final List<Link> episodes = new ArrayList<Link>();
		for (final File f : seasonDir.listFiles()) {
			if (isVideo(f)) {
				episodes.add(createVideoLink(f));
			}
		}
		return new Season(seasonName, episodes);
	}

	/**
	 * Given a file, create the relative video link.
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 *             on error
	 */
	private static Link createVideoLink(final File f) throws IOException {
		final String relativePath = f.getCanonicalPath().substring(
				TV_DIR.getCanonicalPath().length() + 1);
		System.out.println(TV_ROOT + relativePath);
		return new Link(f.getName(), TV_ROOT + relativePath);
	}

	private static boolean isVideo(final File f) {
		final String name = f.getName();
		return name.endsWith(".m4v") || name.endsWith(".mp4");
	}

}
