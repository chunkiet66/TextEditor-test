import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class TextEditorTest {
    TextEditor editor;

    @BeforeEach
    public void setup() {
        editor = new TextEditor();
    }

    @Test
    public void testAppend() {
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                editor.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit."));
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla aliquet scelerisque tellus, id commodo diam lacinia quis.",
                editor.append(" Nulla aliquet scelerisque tellus, id commodo diam lacinia quis."));
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla aliquet scelerisque tellus, id commodo diam lacinia quis. APPEND AGAIN",
                editor.append(" APPEND AGAIN"));
    }

    @Test
    public void testMove() {
        editor.move(2);
        String s1 = editor.append("Lorem");
        assertEquals("Lorem", s1);
        editor.move(-5);
        String s2 = editor.append("Go Get it ");
        assertEquals("Go Get it Lorem", s2);
        editor.move(100);
        String s3 = editor.append("!");
        assertEquals("Go Get it Lorem!", s3);
        editor.move(5);
        String s4 = editor.append("APPEND");
        assertEquals("Go GeAPPENDt it Lorem!", s4);
        String s5 = editor.append("APPEND AGAIN");
        assertEquals("Go GeAPPENDAPPEND AGAINt it Lorem!", s5);
    }

    @Test
    public void testDelete() {
        editor.delete();
        editor.append("Lorem");
        String s1 = editor.delete();
        assertEquals("Lorem", s1);
        editor.append("Go Get it");
        //cursor move to the end of appended string
        String s2 = editor.delete();
        assertEquals("LoremGo Get it", s2);

        editor.move(3);
        String s3 = editor.delete();
        assertEquals("LormGo Get it", s3);

        String s4 = editor.append("Go Get it");
        assertEquals("LorGo Get itmGo Get it", s4);
        editor.delete();
        editor.delete();
        String s5 = editor.delete();
        assertEquals("LorGo Get it Get it", s5);
    }

    @Test
    public void testSelect() {
        editor.select(10, 20);
        editor.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");

        editor.select(6, 11);
        String s1 = editor.append("12345");
        assertEquals("Lorem 12345 dolor sit amet, consectetur adipiscing elit.", s1);

        String s2 = editor.append(" dolor");
        assertEquals("Lorem 12345 dolor dolor sit amet, consectetur adipiscing elit.", s2);

        editor.select(6, 12);
        String s3 = editor.delete();
        assertEquals("Lorem dolor dolor sit amet, consectetur adipiscing elit.", s3);
        String s4 = editor.delete();
        assertEquals("Lorem olor dolor sit amet, consectetur adipiscing elit.", s4);

        editor.select(26, 60); //rightPosition over boundry.
        String s5 = editor.append("APPEND AT THE END!");
        assertEquals("Lorem olor dolor sit amet,APPEND AT THE END!", s5);

        editor.select(-1, 2); //rightPosition over boundry.
        String s6 = editor.delete();
        assertEquals("rem olor dolor sit amet,APPEND AT THE END!", s6);
    }

    @Test
    public void testCutAndPaste() {
        editor.append("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
        editor.select(0, 6);

        String s1 = editor.cut();
        assertEquals("ipsum dolor sit amet, consectetur adipiscing elit.", s1);

        String s2 = editor.paste();
        assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit.", s2);

        editor.move(3);
        String s3 = editor.paste();
        assertEquals("LorLorem em ipsum dolor sit amet, consectetur adipiscing elit.", s3);

        String s4 = editor.cut();
        assertEquals("LorLorem em ipsum dolor sit amet, consectetur adipiscing elit.", s4);

        editor.select(0, s4.length());
        String s5 = editor.cut();
        assertEquals("", s5);
        assertTrue(s5.length() == 0);

        editor.append("12345");
        String s6 = editor.cut();
        assertEquals("12345", s6);

        String s7 = editor.paste();
        assertEquals("12345", s7);

        editor.select(1, 4);
        String s8 = editor.cut();
        assertEquals("15", s8);

        editor.select(0, 2);
        String s9 = editor.paste();
        assertEquals("234", s9);

        String s10 = editor.paste();
        assertEquals("234234", s10);

        String s11 = editor.paste();
        assertEquals("234234234", s11);
    }

    @Test
    public void testUndoAndRedo() {
        String s1 = editor.undo();
        assertEquals("", s1);

        String s2 = editor.append("The king of the world likes to eat apple.");
        assertEquals("The king of the world likes to eat apple.", s2);
        String s3 = editor.undo();
        assertEquals("", s3);
        String s4 = editor.undo();
        assertEquals("", s4);

        String s5 = editor.redo();
        assertEquals("The king of the world likes to eat apple.", s5);

        editor.move(4);
        String s6 = editor.append("superstitious ");
        assertEquals("The superstitious king of the world likes to eat apple.", s6);

        editor.select(4, 18);
        String s7 = editor.delete();
        assertEquals("The king of the world likes to eat apple.", s7);

        String s8 = editor.undo();
        assertEquals("The superstitious king of the world likes to eat apple.", s8);

        editor.move(s8.length());
        String s9 = editor.append(" The queen hates apple!");
        assertEquals("The superstitious king of the world likes to eat apple. The queen hates apple!", s9);

        String s10 = editor.undo();
        assertEquals("The superstitious king of the world likes to eat apple.", s10);

        String s11 = editor.undo();
        assertEquals("The king of the world likes to eat apple.", s11);

        String s12 = editor.undo();
        assertEquals("", s12);
    }

    @Test
    public void testUndoAndRedo2() {
        String s1 = editor.redo();
        assertEquals("", s1);

        editor.append("It");
        editor.append(" is");
        editor.append(" a");
        editor.append(" good");
        String s2 = editor.append(" time");
        assertEquals("It is a good time", s2);

        assertEquals("It is a good", editor.undo());
        assertEquals("It is a", editor.undo());
        assertEquals("It is", editor.undo());
        assertEquals("It", editor.undo());
        assertEquals("", editor.undo());
        assertEquals("", editor.undo());
        assertEquals("", editor.undo());

        assertEquals("It", editor.redo());
        assertEquals("It is", editor.redo());
        assertEquals("It is a", editor.redo());
        assertEquals("It is a good", editor.redo());
        assertEquals("It is a good time", editor.redo());
        assertEquals("It is a good time", editor.redo());
        assertEquals("It is a good time", editor.redo());
        assertEquals("It is a good time", editor.redo());

        assertEquals("It is a good", editor.undo());
        assertEquals("It is a", editor.undo());

        assertEquals("It is an", editor.append("n"));
        assertEquals("It is an awesome", editor.append(" awesome"));
        assertEquals("It is an awesome opportunity", editor.append(" opportunity"));
        assertEquals("It is an awesome opportunity", editor.redo());
        assertEquals("It is an awesome opportunity", editor.redo());

        assertEquals("It is an awesome", editor.undo());
        assertEquals("It is an", editor.undo());
        assertEquals("It is a", editor.undo());
        assertEquals("It is", editor.undo());

        editor.select(3, 5);
        assertEquals("It ", editor.cut());

        assertEquals("It is", editor.paste());
        assertEquals("It isis", editor.paste());
        assertEquals("It isisis", editor.paste());

        assertEquals("It isis", editor.undo());
        assertEquals("It isisis", editor.redo());
        assertEquals("It isisisis", editor.paste());

        editor.select(0, 2);
        assertEquals("is isisisis", editor.paste());
        assertEquals("A isisisis", editor.append("A"));
    }
}
