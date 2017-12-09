package image;

public class Tag {

    // The x and y coordinate of Tag.
    private double x;
    private double y;

    // The content of the Tag
    private String content;

    /**
     * The constructor of the Tag.
     *
     * @param content The tag content.
     * @param x       The x-coordinate of tag
     * @param y       The y-coordinate of tag
     */
    public Tag(String content, double x, double y) {
        this.content = content;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof Tag && content.equals(((Tag) object).getContent())
                && x == ((Tag) object).getX() && y == ((Tag) object).getY());
    }

    /**
     * Returns the content of the Tag
     *
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the x-coordinate of the Tag
     *
     * @return x
     */
    double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the Tag
     *
     * @return y
     */
    double getY() {
        return y;
    }
}
