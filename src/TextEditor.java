public class TextEditor {
    StringBuilder sb;
    int position;
    int[] select = new int[2];
    boolean selected;

    String clipboard;


    public TextEditor() {
        sb = new StringBuilder();
        position = 0;
        selected = false;
        clipboard = "";
    }

    public String append(String value) {
        //overwrite at selected or append at position
        if (selected) {
            sb.replace(select[0], select[1], value);
            selected = false;
            position = select[0] + value.length();
        } else {
            sb.insert(position, value);
            position = position + value.length();
        }

        return sb.toString();
    }

    public void move(int position) {
        //move cursor position, if position is not within string length, move it to the closest one
        if (position < 0) {
            this.position = 0;
        } else if (position > sb.length()) {
            this.position = sb.length();
        } else {
            this.position = position;
        }
    }

    public String delete() {
        //delete char at pos or selected
        if (selected) {
            sb.replace(select[0], select[1], "");
            selected = false;
            position = select[0];
        } else if (sb.length() > 0 && position < sb.length()) {
            sb.deleteCharAt(position);
        }
        return sb.toString();
    }

    public void select(int leftPosition, int rightPosition) {
        //select text starting at leftPosition and end at rightPosition, if position is not within string length, move it to the closest one
        if (leftPosition >= rightPosition || leftPosition >= sb.length()) {
            return;
        }

        select[0] = leftPosition < 0 ? 0 : leftPosition;
        select[1] = rightPosition > sb.length() ? sb.length() : rightPosition;
        selected = true;
    }

    public String cut() {
        //cut and copy the selected text into clipboard
        clipboard = "";
        if (selected) {
            //cut it
            clipboard = sb.substring(select[0], select[1]);
            return delete();
        }
        return sb.toString();
    }

    public String paste() {
        //paste the string from clipboard into position or selected text
        return append(clipboard);
    }

    public String undo() {
        //position, sb, selected can be undo/redo
        //clipboard cant be undo/redo
        return "";
    }

    public String redo() {
        return "";
    }

    //throw invalid exception error if file is existed
    public void createFile(String filename) {
        //create a new file
    }

    public void switchFile(String filename) {
        //switch to this new file and edit it
    }
}
