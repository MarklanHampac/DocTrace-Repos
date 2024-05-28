package com.example.dts_1.User_Ui.incoming;

import com.example.dts_1.User_Ui.view_document.user_viewdocument_PlaceholderContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class user_incoming_PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    public static final List<PlaceholderItem> ITEMS = new ArrayList<PlaceholderItem>();

    /**
     * A map of sample (placeholder) items, by ID.
     */
    public static final Map<String, PlaceholderItem> ITEM_MAP = new HashMap<String, PlaceholderItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    private static void addItem(PlaceholderItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static PlaceholderItem createPlaceholderItem(int position) {
        return new PlaceholderItem(String.valueOf(position), "Item " + position, makeDetails(position), null, null, null, null, null, null, null, null, null, null, null);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class PlaceholderItem {
        public final String id;
        public final String documentId;
        public final String documentTitle;
        public final String documentDescription;
        public final String author;
        public final String creationDate;
        public final String lastModifiedDate;
        public final String status;
        public final String documentTypeName;
        public final String filePath;
        public final String isDone;
        public final String state;
        public final String state_desc;
        public final String doc_status;

        public PlaceholderItem(String id, String documentId, String documentTitle, String documentDescription, String author, String creationDate, String lastModifiedDate, String status, String documentTypeName, String filePath, String isDone, String state, String stateDesc, String doc_status) {
            this.id = id;
            this.documentId = documentId;
            this.documentTitle = documentTitle;
            this.documentDescription = documentDescription;
            this.author = author;
            this.creationDate = creationDate;
            this.lastModifiedDate = lastModifiedDate;
            this.status = status;
            this.documentTypeName = documentTypeName;
            this.filePath = filePath;
            this.isDone = isDone;
            this.state = state;
            this.state_desc = stateDesc;
            this.doc_status = doc_status;
        }

        @Override
        public String toString() {
            return author;
        }
    }
}