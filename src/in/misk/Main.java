package in.misk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tool to read the iTunes TV and Film folders and generate static html linking
 * to the films/shows.
 * 
 * <p>
 * General structure of Films is:
 * </p>
 * 
 * <pre>
 * </pre>
 * <p>
 * General structure of TV is
 * </p>
 * 
 * <pre>
 * TV Shows
 * |
 * |--Show
 * |   |--episode1.m4v
 * |   |--episode2.m4v
 * |
 * |---Show2
 * |     |--season1
 * |     |     |--episode1.m4v
 * |     |     |--episode2.m4v
 * |     |--season2
 * |     |     |--episode1.m4v
 * |     |     |--episode2.m4v|
 * </pre>
 * <p>
 * The generated html for tv shows has one page per show and then &lt;video&gt;
 * tags linking to the episodes, grouped by season.
 * </p>
 * 
 * 
 * <p>
 * There are three base paths to consider:
 * <ol>
 * <li>Directory to process for files</li>
 * <li>Directory to generate HTML</li>
 * <li>The URLs to use for video links</li>
 * </ol>
 * </p>
 * 
 * @author richard
 * 
 */
public class Main {

	private static final File TV_DIR = new File(
			"/Users/richard/Documents/workspace/HTMLGen/video/tv");
	// private static final File TV_DIR = new File("D:\\iTunes\\TV Shows");
	private static final String TV_ROOT = "http://thebox/tv/";
	private static final File FILM_DIR = new File(
			"/Users/richard/Documents/workspace/HTMLGen/video/film");
	// private static final File FILM_DIR = new File("D:\\iTunes\\Movies");
	private static final String FILM_ROOT = "http://thebox/film/";
	// private static final String HTML_ROOT = "D:\\inetpub\\wwwroot\\autogen";
	private static final String HTML_ROOT = "/Users/richard/Documents/workspace/HTMLGen/html";

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws IOException {
		// TV Shows
		final List<Link> tvPages = new ArrayList<Link>();
		for (final File f : TV_DIR.listFiles()) {
			if (f.isDirectory()) {
				tvPages.add(processDir(f, HTML_ROOT + "/tv/" + f.getName(),
						TV_ROOT + f.getName()));
			}
		}
		createTVShowIndex(tvPages);

	}

	private static void createTVShowIndex(final List<Link> tvPages) {
		// TODO Auto-generated method stub

	}

	private static Link processDir(final File dir, final String htmlDir,
			final String url) throws IOException {
		final List<Link> links = new ArrayList<Link>();
		final List<File> videoFiles = new ArrayList<File>();
		for (final File f : dir.listFiles()) {
			if (f.isDirectory()) {
				// Treat as a season
				final Link processDir = processDir(f,
						htmlDir + "/" + f.getName(), url + "/" + f.getName());
				links.add(processDir);
			} else {
				// Just create links to episodes
				final String name = f.getName();
				if (name.endsWith(".m4v") || name.endsWith(".mp4")) {
					videoFiles.add(f);
					// links.add(createPageForFile(f, htmlDir, url));
				}
			}
		}
		System.out.println("Creating page for dir: " + dir.getAbsolutePath());
		return createPageForLinks(dir.getName(), htmlDir, links, videoFiles,
				url);
	}

	private static Link createPageForFile(final File f, final String htmlDir,
			final String url) throws IOException {
		//
		System.out.println("Creating page for file: " + f.getAbsolutePath());
		final StringBuilder fileText = new StringBuilder("<html><body>");
		fileText.append("<video src=\"" + f.getPath() + "\" controls>");
		fileText.append("</body></html>");
		final String htmlFile = htmlDir + "/" + f.getName() + ".html";
		FileWriter fileWriter = null;
		try {
			new File(htmlFile).getParentFile().mkdirs();
			fileWriter = new FileWriter(htmlFile);
			fileWriter.write(fileText.toString());
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
		return new Link(f.getName(), url + "/" + f.getName() + ".html");
	}

	private static Link createPageForLinks(final String displayName,
			final String htmlDir, final List<Link> links,
			final List<File> videoFiles, final String url) throws IOException {
		final File linksFile = new File(htmlDir + "/index.html");
		System.out.println("Creating links file: "
				+ linksFile.getAbsolutePath());
		final StringBuilder fileText = new StringBuilder("<html><body>");
		if (!links.isEmpty()) {
			fileText.append("<div>");
			for (final Link l : links) {
				fileText.append("<p>");
				fileText.append("<a href=\"");
				fileText.append(l.href);
				fileText.append("\">");
				fileText.append(l.display);
				fileText.append("</a>");
				fileText.append("\r\n");
				fileText.append("</p>");
			}
			fileText.append("</div>");
		}
		if (!videoFiles.isEmpty()) {
			fileText.append("<ul>");
			for (final File videoFile : videoFiles) {
				fileText.append("\r\n");
				fileText.append("\r\n");
				fileText.append("<li>");
				// FIXME Does this need to be made a relative path?
				fileText.append("<video src=\"" + videoFile.getPath()
						+ "\" controls>");
				fileText.append("</li>");
			}
			fileText.append("</ul>");
		}
		fileText.append("</body></html>");
		final String htmlFile = htmlDir + "/" + "index.html";
		FileWriter fileWriter = null;
		try {
			linksFile.getParentFile().mkdirs();
			fileWriter = new FileWriter(linksFile);
			fileWriter.write(fileText.toString());
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
		return new Link(displayName, url + "/index.html");
	}

}
