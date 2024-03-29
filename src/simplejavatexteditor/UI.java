/**
 * @name        Simple Java NotePad
 * @package ph.notepad
 * @file UI.java
 *
 * @author Pierre-Henry Soria
 * @email pierrehenrysoria@gmail.com
 * @link        http://github.com/pH-7
 *
 * @copyright   Copyright Pierre-Henry SORIA, All Rights Reserved.
 * @license     Apache (http://www.apache.org/licenses/LICENSE-2.0)
 * @create      2012-04-05
 * @update      2017-02-18
 *
 * @modifiedby  Achintha Gunasekara
 * @modemail    contact@achinthagunasekara.com
 *
 * @modifiedby  Marcus Redgrave-Close
 * @modemail    marcusrc1@hotmail.co.uk
 *
 * @Modifiedby SidaDan
 * @modemail Fschultz@sinf.de
 * Added Tooltip Combobox Font type and Font size
 * Overwrite method processWindowEvent to detect window closing event.
 * Added safety query to save the file before exit
 * or the user select "newfile"
 * Only available if the user has pressed a key
 * Added safety query if user pressed the clearButton
 *
 * @Modifiedby SidaDan
 * @modemail Fschultz@sinf.de
 * Removed unuse objects like container,  Border emptyBorder
 * Removed unsused imports
 *
 * @Modifiedby Giorgos Pasentsis
 * @modemail gpasents@gmail.com
 */

/**
 * @name        Encripted Simple Java NotePad Modification
 * @package ph.notepad
 * @file UI.java
 *
 * @author Daniel Delgado
 * @author Anthony Leandro
 * @author Bryan Vargas
 * @author Ignacio Cantillo
 *
 * Modification made taking as base the original Simple Java NotePad
 * It incorporates encryption functionalities using AES algorithm
 * It can encrypt partially, decrypt partially, save encrypted files,
 * and decrypt encrypted files.
 */
package simplejavatexteditor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.text.DefaultEditorKit;

public class UI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final JTextArea textArea;
    private final JMenuBar menuBar;
    private final JComboBox fontSize, fontType;
    private final JMenu menuFile, menuEdit, menuFind, menuAbout;
    private final JMenuItem newFile, openFile, encryptText, encryptSliceText, decryptSliceText, decryptText, showKey, saveFile, close, cut, copy, paste, clearFile, selectAll, quickFind,
            aboutMe, aboutSoftware, wordWrap;
    private final JToolBar mainToolbar;
    JButton newButton, openButton, encryptButton, encryptSliceButton , decryptSliceButton, decryptButton, showKeyButton, saveButton, clearButton, quickButton, aboutMeButton, aboutButton, closeButton, boldButton, italicButton;
    private final Action selectAllAction;

    //setup icons - Bold and Italic
     private final ImageIcon boldIcon = new ImageIcon("icons/bold.png");
     private final ImageIcon italicIcon = new ImageIcon("icons/italic.png");

    // setup icons - File Menu
    private final ImageIcon newIcon = new ImageIcon("icons/new.png");
    private final ImageIcon openIcon = new ImageIcon("icons/open.png");
    private final ImageIcon saveIcon = new ImageIcon("icons/save.png");
    private final ImageIcon closeIcon = new ImageIcon("icons/close.png");

    // setup icons - Edit Menu
    private final ImageIcon clearIcon = new ImageIcon("icons/clear.png");
    private final ImageIcon cutIcon = new ImageIcon("icons/cut.png");
    private final ImageIcon copyIcon = new ImageIcon("icons/copy.png");
    private final ImageIcon pasteIcon = new ImageIcon("icons/paste.png");
    private final ImageIcon selectAllIcon = new ImageIcon("icons/selectall.png");
    private final ImageIcon wordwrapIcon = new ImageIcon("icons/wordwrap.png");
    private final ImageIcon encryptIcon = new ImageIcon("icons/encrypt.png");
    private final ImageIcon encryptSliceIcon = new ImageIcon("icons/encryptSlice.png");
    private final ImageIcon decryptIcon = new ImageIcon("icons/decrypt.png");
    private final ImageIcon decryptSliceIcon = new ImageIcon("icons/decryptSlice.png");
    private final ImageIcon showKeyIcon = new ImageIcon("icons/showKey.png");

    // setup icons - Search Menu
    private final ImageIcon searchIcon = new ImageIcon("icons/search.png");

    // setup icons - Help Menu
    private final ImageIcon aboutMeIcon = new ImageIcon("icons/about_me.png");
    private final ImageIcon aboutIcon = new ImageIcon("icons/about.png");

    private SupportedKeywords kw = new SupportedKeywords();
    private HighlightText languageHighlighter = new HighlightText(Color.GRAY);
    AutoComplete autocomplete;
    private boolean hasListener = false;
    private boolean edit = false;
    private static int lastEncryptedCharIndex = 0;
    private static String key = "";

    public UI() {        
        try {
            ImageIcon image = new ImageIcon("icons/ste.png");
            super.setIconImage(image.getImage());
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // Set the initial size of the window
        setSize(1100, 650);

        // Set the title of the window
        setTitle("Untitled | " + SimpleJavaTextEditor.NAME);

        // Set the default close operation (exit when it gets closed)
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // center the frame on the monitor
        setLocationRelativeTo(null);

        // Set a default font for the TextArea
        textArea = new JTextArea("", 0, 0);
        textArea.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        textArea.setTabSize(2);
        textArea.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        textArea.setTabSize(2);

        /* SETTING BY DEFAULT WORD WRAP ENABLED OR TRUE */
        textArea.setLineWrap(true);
        DropTarget dropTarget = new DropTarget(textArea, dropTargetListener);

        // Set an higlighter to the JTextArea
        textArea.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                edit = true;
                languageHighlighter.highLight(textArea, kw.getCppKeywords());
                languageHighlighter.highLight(textArea, kw.getJavaKeywords());
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setWrapStyleWord(true);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().setLayout(new BorderLayout()); // the BorderLayout bit makes it fill it automatically
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane);
        getContentPane().add(panel);

        // Set the Menus
        menuFile = new JMenu("File");
        menuEdit = new JMenu("Edit");
        menuFind = new JMenu("Search");
        menuAbout = new JMenu("About");
        //Font Settings menu

        // Set the Items Menu
        newFile = new JMenuItem("New", newIcon);
        openFile = new JMenuItem("Open", openIcon);
        encryptText = new JMenuItem("Encrypt", encryptIcon);
        encryptSliceText = new JMenuItem("Encrypt slice", encryptSliceIcon);
        decryptText = new JMenuItem("Decrypt", decryptIcon);
        showKey = new JMenuItem("Show key", showKeyIcon);
        decryptSliceText = new JMenuItem("Decrypt slice", decryptSliceIcon);
        saveFile = new JMenuItem("Save", saveIcon);
        close = new JMenuItem("Quit", closeIcon);
        clearFile = new JMenuItem("Clear", clearIcon);
        quickFind = new JMenuItem("Quick", searchIcon);
        aboutMe = new JMenuItem("About Me", aboutMeIcon);
        aboutSoftware = new JMenuItem("About Software", aboutIcon);

        menuBar = new JMenuBar();
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuFind);

        menuBar.add(menuAbout);

        this.setJMenuBar(menuBar);

        // Set Actions:
        selectAllAction = new SelectAllAction("Select All", clearIcon, "Select all text", new Integer(KeyEvent.VK_A),
                textArea);

        this.setJMenuBar(menuBar);

        // New File
        newFile.addActionListener(this);  // Adding an action listener (so we know when it's been clicked).
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK)); // Set a keyboard shortcut
        menuFile.add(newFile); // Adding the file menu

        // Open File
        openFile.addActionListener(this);
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        menuFile.add(openFile);
        
        // Encrypt, decrypt
        menuFile.add(encryptText);
        menuFile.add(encryptSliceText);
        menuFile.add(decryptText);
        menuFile.add(decryptSliceText);
        menuFile.add(showKey);
        
        // Save File
        saveFile.addActionListener(this);
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        menuFile.add(saveFile);

        // Close File
        /*
         * Along with our "CTRL+F4" shortcut to close the window, we also have
         * the default closer, as stated at the beginning of this tutorial. this
         * means that we actually have TWO shortcuts to close:
         * 1) the default close operation (example, Alt+F4 on Windows)
         * 2) CTRL+F4, which we are
         * about to define now: (this one will appear in the label).
         */
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        close.addActionListener(this);
        menuFile.add(close);

        // Select All Text
        selectAll = new JMenuItem(selectAllAction);
        selectAll.setText("Select All");
        selectAll.setIcon(selectAllIcon);
        selectAll.setToolTipText("Select All");
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        menuEdit.add(selectAll);

        // Clear File (Code)
        clearFile.addActionListener(this);
        clearFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));
        menuEdit.add(clearFile);

        // Cut Text
        cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setIcon(cutIcon);
        cut.setToolTipText("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuEdit.add(cut);

        // WordWrap
        wordWrap = new JMenuItem();
        wordWrap.setText("Word Wrap");
        wordWrap.setIcon(wordwrapIcon);
        wordWrap.setToolTipText("Word Wrap");

        //Short cut key or key stroke
        wordWrap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        menuEdit.add(wordWrap);

        /* CODE FOR WORD WRAP OPERATION
         * BY DEFAULT WORD WRAPPING IS ENABLED.
         */
        wordWrap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                // If wrapping is false then after clicking on menuitem the word wrapping will be enabled
                if (textArea.getLineWrap() == false) {
                    /* Setting word wrapping to true */
                    textArea.setLineWrap(true);
                } else {
                    // else  if wrapping is true then after clicking on menuitem the word wrapping will be disabled
                    /* Setting word wrapping to false */
                    textArea.setLineWrap(false);
                }
            }
        });

        // Copy Text
        copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setIcon(copyIcon);
        copy.setToolTipText("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        menuEdit.add(copy);

        // Paste Text
        paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setIcon(pasteIcon);
        paste.setToolTipText("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        menuEdit.add(paste);

        // Find Word
        quickFind.addActionListener(this);
        quickFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        menuFind.add(quickFind);

        // About Me
        aboutMe.addActionListener(this);
        aboutMe.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuAbout.add(aboutMe);

        // About Software
        aboutSoftware.addActionListener(this);
        aboutSoftware.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        menuAbout.add(aboutSoftware);

        mainToolbar = new JToolBar();
        this.add(mainToolbar, BorderLayout.NORTH);
        // used to create space between button groups
        newButton = new JButton(newIcon);
        newButton.setToolTipText("New");
        newButton.addActionListener(this);
        mainToolbar.add(newButton);
        mainToolbar.addSeparator();

        openButton = new JButton(openIcon);
        openButton.setToolTipText("Open");
        openButton.addActionListener(this);
        mainToolbar.add(openButton);
        mainToolbar.addSeparator();

        saveButton = new JButton(saveIcon);
        saveButton.setToolTipText("Save");
        saveButton.addActionListener(this);
        mainToolbar.add(saveButton);
        mainToolbar.addSeparator();
        
        encryptButton = new JButton(encryptIcon);
        encryptButton.setToolTipText("Encrypt");
        encryptButton.addActionListener(this);
        mainToolbar.add(encryptButton);
        mainToolbar.addSeparator();
        
        encryptSliceButton = new JButton(encryptSliceIcon);
        encryptSliceButton.setToolTipText("Encrypt slice");
        encryptSliceButton.addActionListener(this);
        mainToolbar.add(encryptSliceButton);
        mainToolbar.addSeparator();
        
        decryptButton = new JButton(decryptIcon);
        decryptButton.setToolTipText("Decrypt");
        decryptButton.addActionListener(this);
        mainToolbar.add(decryptButton);
        mainToolbar.addSeparator();
        
        decryptSliceButton = new JButton(decryptSliceIcon);
        decryptSliceButton.setToolTipText("Decrypt slice");
        decryptSliceButton.addActionListener(this);
        mainToolbar.add(decryptSliceButton);
        mainToolbar.addSeparator();
        
        showKeyButton = new JButton(showKeyIcon);
        showKeyButton.setToolTipText("Show key");
        showKeyButton.addActionListener(this);
        //mainToolbar.add(showKeyButton);
        mainToolbar.addSeparator();

        clearButton = new JButton(clearIcon);
        clearButton.setToolTipText("Clear All");
        clearButton.addActionListener(this);
        mainToolbar.add(clearButton);
        mainToolbar.addSeparator();

        quickButton = new JButton(searchIcon);
        quickButton.setToolTipText("Quick Search");
        quickButton.addActionListener(this);
        mainToolbar.add(quickButton);
        mainToolbar.addSeparator();

        aboutMeButton = new JButton(aboutMeIcon);
        aboutMeButton.setToolTipText("About Me");
        aboutMeButton.addActionListener(this);
        mainToolbar.add(aboutMeButton);
        mainToolbar.addSeparator();

        aboutButton = new JButton(aboutIcon);
        aboutButton.setToolTipText("About NotePad PH");
        aboutButton.addActionListener(this);
        mainToolbar.add(aboutButton);
        mainToolbar.addSeparator();

        closeButton = new JButton(closeIcon);
        closeButton.setToolTipText("Quit");
        closeButton.addActionListener(this);
        mainToolbar.add(closeButton);
        mainToolbar.addSeparator();

        boldButton = new JButton(boldIcon);
        boldButton.setToolTipText("Bold");
        boldButton.addActionListener(this);
        mainToolbar.add(boldButton);
        mainToolbar.addSeparator();

        italicButton = new JButton(italicIcon);
        italicButton.setToolTipText("Italic");
        italicButton.addActionListener(this);
        mainToolbar.add(italicButton);
        mainToolbar.addSeparator();
        /**
         * **************** FONT SETTINGS SECTION **********************
         */
        //FONT FAMILY SETTINGS SECTION START
        fontType = new JComboBox();

        //GETTING ALL AVAILABLE FONT FOMILY NAMES
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for (int i = 0; i < fonts.length; i++) {
            //Adding font family names to font[] array
            fontType.addItem(fonts[i]);
        }
        //Setting maximize size of the fontType ComboBox
        fontType.setMaximumSize(new Dimension(170, 30));
        fontType.setToolTipText("Font Type");
        mainToolbar.add(fontType);
        mainToolbar.addSeparator();

        //Adding Action Listener on fontType JComboBox
        fontType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                //Getting the selected fontType value from ComboBox
                String p = fontType.getSelectedItem().toString();
                //Getting size of the current font or text
                int s = textArea.getFont().getSize();
                textArea.setFont(new Font(p, Font.PLAIN, s));
            }
        });

        //FONT FAMILY SETTINGS SECTION END
        //FONT SIZE SETTINGS START
        fontSize = new JComboBox();

        for (int i = 5; i <= 100; i++) {
            fontSize.addItem(i);
        }
        fontSize.setMaximumSize(new Dimension(70, 30));
        fontSize.setToolTipText("Font Size");
        mainToolbar.add(fontSize);

        fontSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String sizeValue = fontSize.getSelectedItem().toString();
                int sizeOfFont = Integer.parseInt(sizeValue);
                String fontFamily = textArea.getFont().getFamily();

                Font font1 = new Font(fontFamily, Font.PLAIN, sizeOfFont);
                textArea.setFont(font1);
            }
        });
        //FONT SIZE SETTINGS SECTION END
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (edit) {
                Object[] options = {"Save and exit", "No Save and exit", "Return"};
                int n = JOptionPane.showOptionDialog(this, "Do you want to save the file ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if (n == 0) {// save and exit
                    saveFile();
                    this.dispose();// dispose all resources and close the application
                } else if (n == 1) {// no save and exit
                    this.dispose();// dispose all resources and close the application
                }
            } else {
                System.exit(99);
            }
        }
    }

    // Make the TextArea available to the autocomplete handler
    protected JTextArea getEditor() {
        return textArea;
    }

    // Enable autocomplete option
    public void enableAutoComplete(File file) {
        if (hasListener) {
            textArea.getDocument().removeDocumentListener(autocomplete);
            hasListener = false;
        }

        ArrayList<String> arrayList;
        String[] list = kw.getSupportedLanguages();

        for (int i = 0; i < list.length; i++) {
            if (file.getName().endsWith(list[i])) {
                switch (i) {
                    case 0:
                        String[] jk = kw.getJavaKeywords();
                        arrayList = kw.setKeywords(jk);
                        autocomplete = new AutoComplete(this, arrayList);
                        textArea.getDocument().addDocumentListener(autocomplete);
                        hasListener = true;
                        break;
                    case 1:
                        String[] ck = kw.getCppKeywords();
                        arrayList = kw.setKeywords(ck);
                        autocomplete = new AutoComplete(this, arrayList);
                        textArea.getDocument().addDocumentListener(autocomplete);
                        hasListener = true;
                        break;
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        // If the source of the event was our "close" option
        if (e.getSource() == close || e.getSource() == closeButton) {
            if (edit) {
                Object[] options = {"Save and exit", "No Save and exit", "Return"};
                int n = JOptionPane.showOptionDialog(this, "Do you want to save the file ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                if (n == 0) {// save and exit
                    saveFile();
                    this.dispose();// dispose all resources and close the application
                } else if (n == 1) {// no save and exit
                    this.dispose();// dispose all resources and close the application
                }
            } else {
                this.dispose();// dispose all resources and close the application
            }
        } // If the source was the "new" file option
        else if (e.getSource() == newFile || e.getSource() == newButton) {
            if (edit) {
                Object[] options = {"Save", "No Save", "Return"};
                int n = JOptionPane.showOptionDialog(this, "Do you want to save the file at first ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                if (n == 0) {// save
                    saveFile();
                    edit = false;
                } else if (n == 1) {
                    edit = false;
                    FEdit.clear(textArea);
                }
            } else {
                FEdit.clear(textArea);
            }

        } // If the source was the "open" option
        else if (e.getSource() == openFile || e.getSource() == openButton) {
            JFileChooser open = new JFileChooser(); // open up a file chooser (a dialog for the user to  browse files to open)
            int option = open.showOpenDialog(this); // get the option that the user selected (approve or cancel)

            /*
             * NOTE: because we are OPENing a file, we call showOpenDialog~ if
             * the user clicked OK, we have "APPROVE_OPTION" so we want to open
             * the file
             */
            if (option == JFileChooser.APPROVE_OPTION) {
                FEdit.clear(textArea); // clear the TextArea before applying the file contents
                try {
                    File openFile = open.getSelectedFile();
                    setTitle(openFile.getName() + " | " + SimpleJavaTextEditor.NAME);
                    Scanner scan = new Scanner(new FileReader(openFile.getPath()));
                    while (scan.hasNext()) {
                        textArea.append(scan.nextLine() + "\n");
                    }

                    enableAutoComplete(openFile);
                } catch (Exception ex) { // catch any exceptions, and...
                    // ...write to the debug console
                    System.err.println(ex.getMessage());
                }
                
                this.key = JOptionPane.showInputDialog("Ingrese la llave para la desencriptación");
                this.lastEncryptedCharIndex = textArea.getText().length();
            }
        } // If the source of the event was the "save" option
        else if (e.getSource() == saveFile || e.getSource() == saveButton) {
            
            saveFile();
        }// If the source of the event was the "Bold" button
        else if (e.getSource() == boldButton) {
            if (textArea.getFont().getStyle() == Font.BOLD){
                textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
            } else {
                textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
            }
        }// If the source of the event was the "Italic" button
        else if (e.getSource() == italicButton) {
            if (textArea.getFont().getStyle() == Font.ITALIC){
                textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
            } else {
                textArea.setFont(textArea.getFont().deriveFont(Font.ITALIC));
            }
        } // If the source of the event was the "encrypt" option
        else if (e.getSource() == encryptText || e.getSource() == encryptButton) {
            this.lastEncryptedCharIndex = getLastEncryptedCharIndex(textArea.getText());
            
            if (lastEncryptedCharIndex < textArea.getText().length() - 1) {
                if ((textArea.getText().length() > 0)) {
                    while ((this.lastEncryptedCharIndex < textArea.getText().length()) || ((this.key.isEmpty() || this.key == null) && textArea.getText().length() > 0)){
                        System.out.println(this.lastEncryptedCharIndex);
                        if (this.key == null || this.key.isEmpty()){
                            this.key = JOptionPane.showInputDialog("Ingrese la llave para la encriptación");
                            if(this.key != null){
                                textArea.setText(encryptText(textArea.getText(), this.key));
                            } else {
                                break;
                            }
                        } else {
                            textArea.setText(encryptText(textArea.getText(), this.key));
                        }
                    }
                }
            }
        } // If the source of the event was the "encrypt slice" option
        else if (e.getSource() == encryptSliceText || e.getSource() == encryptSliceButton) {
            this.lastEncryptedCharIndex = getLastEncryptedCharIndex(textArea.getText());
            
            if (lastEncryptedCharIndex < textArea.getText().length() - 1) {
                if ((textArea.getText().length() > 0)) {
                    if (this.key == null || this.key.isEmpty()){
                        this.key = JOptionPane.showInputDialog("Ingrese la llave para la encriptación");
                        if(this.key != null){
                            textArea.setText(encryptText(textArea.getText(), this.key));
                        }
                    } else {
                        textArea.setText(encryptText(textArea.getText(), this.key));
                    }
                }
            }
        } // If the source of the event was the "decrypt" option
        else if (e.getSource() == decryptText || e.getSource() == decryptButton) {
            if ((textArea.getText().length() > 0)) {
                while (this.lastEncryptedCharIndex > 0){
                    System.out.println(this.lastEncryptedCharIndex);
                    if(this.key == null || this.key.isEmpty()){
                        this.key = JOptionPane.showInputDialog("Ingrese la llave para la desencriptación");
                        if(this.key != null){
                            textArea.setText(decryptText(textArea.getText(), this.key));
                        }
                    } else {
                        textArea.setText(decryptText(textArea.getText(), this.key));
                    }
                }
            }
        } // If the source of the event was the "decrypt slice" option
        else if (e.getSource() == decryptSliceText || e.getSource() == decryptSliceButton) {
            if ((textArea.getText().length() > 0)) {
                if (this.lastEncryptedCharIndex > 0){
                    System.out.println(this.lastEncryptedCharIndex);
                    if(this.key == null || this.key.isEmpty()){
                        this.key = JOptionPane.showInputDialog("Ingrese la llave para la desencriptación");
                        if(this.key != null){
                            textArea.setText(decryptText(textArea.getText(), this.key));
                        }
                    } else {
                        textArea.setText(decryptText(textArea.getText(), this.key));
                    }
                }
            }
        } 
        // Clear File (Code)
        if (e.getSource() == clearFile || e.getSource() == clearButton) {

            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(this, "Are you sure to clear the text Area ?", "Question",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == 0) {// clear
                FEdit.clear(textArea);
            }
        }
        // Find
        if (e.getSource() == quickFind || e.getSource() == quickButton) {
            new Find(textArea);
        } // About Me
        else if (e.getSource() == aboutMe || e.getSource() == aboutMeButton) {
            new About(this).me();
        } // About Software
        else if (e.getSource() == aboutSoftware || e.getSource() == aboutButton) {
            new About(this).software();
        }
    }

    class SelectAllAction extends AbstractAction {

        /**
         * Used for Select All function
         */
        private static final long serialVersionUID = 1L;

        public SelectAllAction(String text, ImageIcon icon, String desc, Integer mnemonic, final JTextArea textArea) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e) {
            textArea.selectAll();
        }
    }

    private void saveFile() {
        // Open a file chooser
        JFileChooser fileChoose = new JFileChooser();
        // Open the file, only this time we call
        int option = fileChoose.showSaveDialog(this);

        /*
             * ShowSaveDialog instead of showOpenDialog if the user clicked OK
             * (and not cancel)
         */
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File openFile = fileChoose.getSelectedFile();
                setTitle(openFile.getName() + " | " + SimpleJavaTextEditor.NAME);

                BufferedWriter out = new BufferedWriter(new FileWriter(openFile.getPath()));
                
                while ((this.lastEncryptedCharIndex < textArea.getText().length()) || (this.key.isEmpty() && textArea.getText().length() > 0)){
                    System.out.println(this.lastEncryptedCharIndex);
                    if (this.key.isEmpty()) {
                        this.key = JOptionPane.showInputDialog("Ingrese la llave para la encriptación");
                    }
                    textArea.setText(encryptText(textArea.getText(), this.key));
                }
                
                out.write(textArea.getText());
                out.close();

                enableAutoComplete(openFile);
                edit = false;
            } catch (Exception ex) { // again, catch any exceptions and...
                // ...write to the debug console
                System.err.println(ex.getMessage());
            }
        }
    }
    
    private int getLastEncryptedCharIndex(String text) {
        for (int i = text.length() -1; i >= 0; i--){
            if (text.charAt(i) == '=') {
                return i;
            }
        }
        return 0;
    }
     
    private String encryptText(String text, String key) {
        int textLength = text.length();
        
        
        
        if(this.lastEncryptedCharIndex < textLength){
            if(this.lastEncryptedCharIndex + 3 <= textLength){
                String textToEncrypt = text.substring(lastEncryptedCharIndex, lastEncryptedCharIndex + 3);
                String encriptedText = AES.encrypt(textToEncrypt, key);
                text = text.substring(0, lastEncryptedCharIndex) + encriptedText + text.substring(lastEncryptedCharIndex + 3, textLength);
                System.out.println("text: " + text);
                this.lastEncryptedCharIndex += encriptedText.length();
            } 
            else {
                System.out.println("else: " + text);
                int missingCharacters = this.lastEncryptedCharIndex + 3 - textLength;
                while(missingCharacters > 0){
                    text = text + " ";
                    missingCharacters--;
                }
                
                System.out.println("text: " + text);
                textLength = text.length();
                
                String textToEncrypt = text.substring(lastEncryptedCharIndex, lastEncryptedCharIndex + 3);
                String encriptedText = AES.encrypt(textToEncrypt, key);
                text = text.substring(0, lastEncryptedCharIndex) + encriptedText + text.substring(lastEncryptedCharIndex + 3, textLength);
                System.out.println("text: " + text);
                this.lastEncryptedCharIndex += encriptedText.length();
            }
        }
        return text;
    }
    private String decryptText(String text, String key) {
        int textLength = text.length();
        int decryptStartInd = -1;
        int decryptFinalInd = -1;
        if(this.lastEncryptedCharIndex > 0){
            for (int i = textLength -1; i >= 0; i--){
                if (text.charAt(i) == '=' && decryptFinalInd == -1) {
                    decryptFinalInd = i+1;
                    i--;
                }
                else if (text.charAt(i) == '=' && decryptFinalInd != -1 && decryptStartInd == -1) {
                    decryptStartInd = i + 1;
                    break;
                }
                else if (i == 0 && decryptFinalInd != -1) {
                    decryptStartInd = i;
                    break;
                }
            }
            String textToDecrypt = text.substring(decryptStartInd, decryptFinalInd);
            
            System.out.println("textToDecrypt: " + textToDecrypt);
            
            this.lastEncryptedCharIndex = decryptStartInd;

            String decriptedText = AES.decrypt(textToDecrypt, key);
            
            System.out.println("decriptedText: " + decriptedText);

            text = text.substring(0, lastEncryptedCharIndex) + decriptedText + text.substring(decryptFinalInd, textLength);
        }
        
        
        
        return text;
    }
     DropTargetListener dropTargetListener = new DropTargetListener() {

        @Override
        public void dragEnter(DropTargetDragEvent e) {
        }

        @Override
        public void dragExit(DropTargetEvent e) {
        }

        @Override
        public void dragOver(DropTargetDragEvent e) {
        }

        @Override
        public void drop(DropTargetDropEvent e) {
            try {
                Transferable tr = e.getTransferable();
                DataFlavor[] flavors = tr.getTransferDataFlavors();
                for (int i = 0; i < flavors.length; i++) {
                    if (flavors[i].isFlavorJavaFileListType()) {
                        e.acceptDrop(e.getDropAction());

                        try {
                            String fileName = tr.getTransferData(flavors[i]).toString().replace("[", "").replace("]", "");
                            FileInputStream fis = new FileInputStream(new File(fileName));
                            byte[] ba = new byte[fis.available()];
                            fis.read(ba);
                            textArea.setText(new String(ba));
                            fis.close();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        e.dropComplete(true);
                        return;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            e.rejectDrop();
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent e) {
        }
    };

}
