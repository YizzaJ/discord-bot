package es.upm.bot.discordbot.elements;

public class Article {
	
	private String title;
	private String image;
	private String content;
	private String authors;
	private String link;
	
	public Article(String title, String image, String content, String authors, String link) {
		this.title = title;
		this.image = image;
		this.content = content;
		this.authors = authors;
		this.link = link;
	}
	
	public String getTitle() {
		return title;
	}
	public String getImage() {
		return image;
	}
	public String getContent() {
		return content;
	}
	public String getAuthors() {
		return authors;
	}
	public String getLink() {
		return link;
	}

	@Override
	public String toString() {
		return "Article [title=" + title + ", image=" + image + ", content=" + content + ", authors=" + authors
				+ ", link=" + link + "]";
	}
	
	
	

}
