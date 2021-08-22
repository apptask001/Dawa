package tzdawa.app.mwakalonga.dawa.models;

public class fragment_items_posts {
    private String postTitle;
    private String postImage;

    public fragment_items_posts(String postTitle, String postImage) {
        this.postTitle = postTitle;
        this.postImage = postImage;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostImage() {
        return postImage;
    }
}
