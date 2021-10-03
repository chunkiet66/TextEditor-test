import org.w3c.dom.Text;

import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import java.util.HashMap;
import java.util.Stack;

public class TextEditor {
    StringBuilder sb;
    int position;
    int[] select = new int[2];
    boolean selected;

    private class TextState {
        String s;
        int position;
        int[] select = new int[2];
        boolean selected;

        public TextState (String s, int position, int[] select, boolean selected) {
            this.s = s;
            this.position = position;
            this.select = select;
            this.selected = selected;
        }
    }

    String clipboard;
    Stack<TextState> undoStack;
    Stack<TextState> redoStack;

    private class FileState {
        TextState textState;
        Stack<TextState> undoStack;
        Stack<TextState> redoStack;
        public FileState() {
            textState = new TextState("", 0, new int[]{0, 0}, false);
            undoStack = new Stack<>();
            redoStack = new Stack<>();
        }

        public FileState(TextState textState, Stack<TextState> undoStack, Stack<TextState> redoStack) {
            this.textState = textState;
            this.undoStack = undoStack;
            this.redoStack = redoStack;
        }
    }

    HashMap<String, FileState> files = new HashMap<>();
    String currentFile;

    public TextEditor() {
        sb = new StringBuilder();
        position = 0;
        selected = false;
        clipboard = "";
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        currentFile = "tmp";
        try {
            createFile(currentFile);
        } catch (InvalidNameException e) {
            System.out.println(e);
        }
    }

    public String append(String value) {
        undoStack.push(new TextState(sb.toString(), position, select, selected));
        //overwrite at selected or append at position
        if (selected) {
            sb.replace(select[0], select[1], value);
            selected = false;
            position = select[0] + value.length();
        } else {
            sb.insert(position, value);
            position = position + value.length();
        }
        redoStack.clear();
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
        selected = false;
    }

    public String delete() {
        undoStack.push(new TextState(sb.toString(), position, select, selected));
        //delete char at pos or selected
        if (selected) {
            sb.replace(select[0], select[1], "");
            selected = false;
            position = select[0];
        } else if (sb.length() > 0 && position < sb.length()) {
            sb.deleteCharAt(position);
        }
        redoStack.clear();
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
        //make it simple, only string can be undo/redo, but still remember the position and select
        if (undoStack.size() > 0) {
            //Record the current state first
            redoStack.push(new TextState(sb.toString(), position, select, selected));
            TextState prevState = undoStack.pop();
            sb = new StringBuilder(prevState.s);
            position = prevState.position;
            select = prevState.select;
            selected = prevState.selected;
        }
        return sb.toString();
    }

    public String redo() {
        if (redoStack.size() > 0) {
            //Record the current state first
            undoStack.push(new TextState(sb.toString(), position, select, selected));
            TextState prevState = redoStack.pop();
            sb = new StringBuilder(prevState.s);
            position = prevState.position;
            select = prevState.select;
            selected = prevState.selected;
        }
        return sb.toString();
    }

    //throw invalid exception error if file is existed
    public void createFile(String filename) throws InvalidNameException {
        //create a new file
        if (files.containsKey(filename)) {
            throw new InvalidNameException("File existed: " + filename);
        }
        files.put(filename, new FileState());
    }

    public void switchFile(String filename) throws NameNotFoundException {
        //switch to this new file and edit it
        if (!files.containsKey(filename)) {
            throw new NameNotFoundException("File does not exist: " + filename);
        }
        FileState fileState = files.get(filename);
        files.put(currentFile, new FileState(new TextState(sb.toString(), position, select, selected), undoStack, redoStack));

        TextState textState = fileState.textState;
        this.sb = new StringBuilder(textState.s);
        this.position = textState.position;
        this.select = textState.select;
        this.selected = textState.selected;
        this.undoStack = fileState.undoStack;
        this.redoStack = fileState.redoStack;
        this.currentFile = filename;
    }
}
