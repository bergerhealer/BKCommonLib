package com.bergerkiller.bukkit.common.config;

/**
 * Can handle header formatted lines and store them in an internal buffer
 */
public class HeaderBuilder {

    private StringBuilder buffer = new StringBuilder();
    private boolean isFirstHeaderLine = true;

    /**
     * Handles the reading input of a new line
     *
     * @param line to handle
     * @return True if a header was handled, False if not
     */
    public boolean handle(CharSequence line) {
        return handle(line, 0, line.length());
    }

    /**
     * Handles the reading input of a new line
     *
     * @param line to handle
     * @param contentStart the start offset into the line
     * @param contentEnd the index (exclusive) of the last character in the line
     * @return True if a header was handled, False if not
     */
    public boolean handle(CharSequence line, int contentStart, int contentEnd) {
        if (contentStart == contentEnd) {
            this.buffer.append('\n');
        } else if (line.charAt(contentStart) == '#') {
            if (this.isFirstHeaderLine) {
                this.isFirstHeaderLine = false;
            } else {
                this.buffer.append('\n');
            }
            if (contentStart < (contentEnd-1) && line.charAt(contentStart+1) == ' ') {
                this.buffer.append(line, contentStart+2, contentEnd);
            } else {
                this.buffer.append(line, contentStart+1, contentEnd);
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Clears the header contained
     */
    public void clear() {
        this.buffer.setLength(0);
        this.isFirstHeaderLine = true;
    }

    /**
     * Checks if a header can be obtained
     *
     * @return True if it has a header, False if not
     */
    public boolean hasHeader() {
        return this.buffer.length() > 0;
    }

    /**
     * Obtains the header contained, null if there is none
     *
     * @return header
     */
    public String getHeader() {
        return hasHeader() ? this.buffer.toString() : null;
    }
}
