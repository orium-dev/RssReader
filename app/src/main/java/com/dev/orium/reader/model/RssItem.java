package com.dev.orium.reader.model;

import java.util.Date;

/**
 * Created by y.drobysh on 18.11.2014.
 */
public class RssItem {

    public static final String PUBLICATION_DATE = "publicationDate";

    public long _feedId;
    public Long _id;

    public String _uniqueId;
    public String title;
    public String paymentURL;
    public String link;
    public String description;
    public Date publicationDate;

    public String mediaURL;

    public boolean readed = false;
//    public Long _mediaSize;
}
