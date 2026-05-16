/**
 * A node in a singly-linked list used to store a chain of replies on a review.
 * append() and count() are recursive to satisfy the recursive data structure requirement.
 */
public class CommentNode {
    private String reply;
    private CommentNode next;

    public CommentNode(String reply) {
        this.reply = reply;
        this.next = null;
    }

    public String getReply() { return reply; }
    public CommentNode getNext() { return next; }
    public void setNext(CommentNode next) { this.next = next; }

    /**
     * Recursively walks to the tail and appends a new node.
     * @param replyText the reply text to add at the end of the chain
     */
    public void append(String replyText) {
        if (this.next == null) {
            this.next = new CommentNode(replyText);
        } else {
            this.next.append(replyText);
        }
    }

    /**
     * Recursively counts nodes from this node to the end of the chain.
     * @return total number of nodes in the chain starting from this node
     */
    public int count() {
        if (next == null) return 1;
        return 1 + next.count();
    }

    @Override
    public String toString() {
        return "   > " + reply + (next != null ? "\n" + next.toString() : "");
    }
}
