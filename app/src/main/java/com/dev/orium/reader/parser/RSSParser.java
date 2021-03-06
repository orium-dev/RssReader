package com.dev.orium.reader.parser;

import com.dev.orium.reader.utils.DateUtils;
import com.dev.orium.reader.model.Feed;
import com.dev.orium.reader.model.RssItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class RSSParser {
	private static final String NAMESPACE_MEDIA = "http://search.yahoo.com/mrss/";

	private RSSParser() {
	}

	static void process(XmlPullParser parser, Feed feed) throws XmlPullParserException, IOException {
		boolean in_image = false;

		// look for subscription details, stop at item tag
		for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
			// check for an ending image tag
			if (in_image && eventType == XmlPullParser.END_TAG && parser.getName().equals("image")) {
				in_image = false;
				continue;
			}
			if (eventType != XmlPullParser.START_TAG)
				continue;

			String name = parser.getName();
			// these are elements about the thumbnail
			if (in_image) {
				if (name.equals("url"))
					feed.thumbnail = parser.nextText();
				continue;
			}

			// if we're starting an item, move past the subscription details section
			if (name.equals("item")) {
				break;
			} else if (name.equals("image")) {
				in_image = true;
				continue;
			} else if (parser.getDepth() != 3) {
				continue;
			} else if (name.equalsIgnoreCase("pubDate")) {
				Date date = DateUtils.parseDate(parser.nextText());
				if (date != null)
					feed.pubDate = date;
			} else if (name.equalsIgnoreCase("lastBuildDate")) {
				Date date = DateUtils.parseDate(parser.nextText());
				if (date != null)
					feed.lastBuildDate = date;
			} else if (name.equalsIgnoreCase("title") && parser.getNamespace().equals("")) {
				feed.title = parser.nextText();
			} else if (name.equalsIgnoreCase("thumbnail") && parser.getNamespace().equals(NAMESPACE_MEDIA)) {
				feed.thumbnail = parser.getAttributeValue("", "url");
			}
		}

        parseRSSItems(parser, feed);
	}

	private static void parseRSSItems(XmlPullParser parser, Feed feed) throws XmlPullParserException, IOException {
		feed.entries = new ArrayList<RssItem>();

        RssItem item = null;

		// grab podcasts from item tags
		for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser.next()) {
			if (eventType == XmlPullParser.START_TAG) {
				String name = parser.getName();
				String namespace = parser.getNamespace();
				if (name.equalsIgnoreCase("item")) {
					item = new RssItem();
				} else if (name.equalsIgnoreCase("guid")) {
					item._uniqueId = parser.nextText();
				} else if (name.equalsIgnoreCase("title") && parser.getNamespace().equals("")) {
					item.title  = parser.nextText();
				} else if (name.equalsIgnoreCase("link")) {
					String rel = parser.getAttributeValue(null, "rel");
					if (rel != null && rel.equalsIgnoreCase("payment")) {
						item.paymentURL = parser.getAttributeValue(null, "href");
					} else {
						item.link  = parser.nextText();
					}
				} else if (namespace.equals("") && name.equalsIgnoreCase("description")) {
					item.description  = parser.nextText();
				} else if (name.equalsIgnoreCase("pubDate")) {
					item.publicationDate = DateUtils.parseDate(parser.nextText());
				} else if (name.equalsIgnoreCase("enclosure")) {
					item.mediaURL = parser.getAttributeValue(null, "url");
//					try {
//						item.setMediaSize(Long.valueOf(parser.getAttributeValue(null, "length")));
//					} catch (Exception e) {
//						item.setMediaSize(0L);
//					}
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				String name = parser.getName();
				if (name.equalsIgnoreCase("item")) {
                    item._feedId = feed._id;
                    feed.entries.add(item);
				}
			}
		}
	}
}
