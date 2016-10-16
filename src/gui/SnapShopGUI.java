/*
 * Assignment 4
 * 
 * TCSS 305 Autumn 2015
 */

package gui;

import filters.EdgeDetectFilter;
import filters.EdgeHighlightFilter;
import filters.Filter;
import filters.FlipHorizontalFilter;
import filters.FlipVerticalFilter;
import filters.GrayscaleFilter;
import filters.SharpenFilter;
import filters.SoftenFilter;
import image.Pixel;
import image.PixelImage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Creates a SnapShop GUI that opens a selected Image from the Users Directory and 
 * gives the user the ability to select different types of filters to alter the image. 
 * All filtering done on the image does not change the source image. 
 * 
 * @author Jeremy Wolf email: jwolf059@uw.edu
 * @version 30 October 2015
 */
public class SnapShopGUI {
    
    /**
     * A generated serial version UID for object Serialization.
     */
    private static final long serialVersionUID = -2144739668936849562L;

    /**
     * Field for the JFileChooser pointing to a relative directory.
     */
    private static final JFileChooser PICKAFILE =  new JFileChooser(new File("."));
    
    /**
     * Field to Store the Open Button.
     */
    private JButton myOpen;
    
    /**
     * Field to Store the Save As Button.
     */
    private JButton mySaveAs;
    
    /**
     * Field to Store the Close Button.
     */
    private JButton myCloseImage;
    
    /**
     * Field to Store the Open Button.
     */
    private JButton myUndo;
    
    /**
     * Field to store a List of Buttons.
     */
    private  List<JButton> myButtonList;
    
    /**
     * Field to store the Image to be edited.
     */
    private PixelImage myImage;
    
    /**
     * 2D Array to store the Pixel data for Image being edited.
     */
    private Pixel[][] myPixelData;
    
    /**
     * JLabel for the Image.
     */
    private JLabel myImageLabel;
    
    /**
     * Main JFrame for the GUI.
     */
    private final JFrame myMainJFrame;

    
    /**
     * Constructs a new SnapShopGUI.
     */
    public SnapShopGUI() {
        myMainJFrame = new JFrame("TCSS 305 SnapShop");
        final ArrayList<Filter> filterList = new ArrayList<>();
        filterList.add(new EdgeDetectFilter());
        filterList.add(new EdgeHighlightFilter());
        filterList.add(new FlipHorizontalFilter());
        filterList.add(new FlipVerticalFilter());
        filterList.add(new GrayscaleFilter());
        filterList.add(new SharpenFilter());
        filterList.add(new SoftenFilter());

        makeButtons(filterList);

    }
    /**
     * Places two JPanels on to the extended JFrame and sets up the buttons. 
     */
    public void start() {

        myMainJFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel topPanel = new JPanel();
        final JPanel utilPanel = new JPanel();
        
        // Generates all Filter Buttons.
        for (final JButton button : myButtonList) {
            topPanel.add(button);  
        }
        
        // Creates the Open button and adds an ActionListener.
        myOpen = new JButton("Open...");
        utilPanel.add(myOpen);
        myOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                chooseImage(); 

            }
        });
        
        // Creates the SaveAs button and adds an ActionListener.
        
        mySaveAs = new JButton("Save As...");
        utilPanel.add(mySaveAs);
        mySaveAs.setEnabled(false);
        mySaveAs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                saveImage(); 
            } 
        });
        
        // Creates the Close button and adds an ActionListener.
        
        myCloseImage = new JButton("Close Image");
        utilPanel.add(myCloseImage);
        myCloseImage.setEnabled(false);
        myCloseImage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                grayOutButtons(true);
                closeImage(); 
            }
        });
        
        myUndo = new JButton("Undo");
        utilPanel.add(myUndo);
        myUndo.setEnabled(false);
        myUndo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent theEvent) {
                undo();
            }
            
        });

        // Add JPanel Contains Filter buttons to North and JPanel containing 
        // Utility buttons to the south.
        
        myMainJFrame.add(topPanel, BorderLayout.NORTH);
        myMainJFrame.add(utilPanel, BorderLayout.SOUTH);
        myMainJFrame.pack();
        myMainJFrame.setVisible(true);
    }
    
    
    /**
     * Enables or disables buttons when a file needs to be open or when a file is open. 
     * All buttons will be disabled until a file is opened and once a file is opened 
     * the open button will be disabled.
     * 
     * @param theImageOpen boolean true if buttons are to be Disabled and false otherwise.
     */
    private void grayOutButtons(final boolean theImageOpen) {
        if (theImageOpen) {
            for (final JButton button : myButtonList) {
                button.setEnabled(false); 
            }
            myOpen.setEnabled(true);
            mySaveAs.setEnabled(false);
            myCloseImage.setEnabled(false);
            myUndo.setEnabled(false);
            
        } else {
            for (final JButton button : myButtonList) {
                button.setEnabled(true);
            }
            myCloseImage.setEnabled(true);
            mySaveAs.setEnabled(true);
            myUndo.setEnabled(true);  
        }
    }
    
    /**
     * Creates buttons using String description of their given filter and adds a 
     * ActionListener to each of them based their internal filter method.
     * 
     * @param theFilters a List of Filter Objects.
     */
    private void makeButtons(final List<Filter> theFilters) {
        myButtonList = new ArrayList<>();
        for (final Filter filterType: theFilters) {
            final JButton button = new JButton(filterType.getDescription());
            button.setEnabled(false);
            button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent theEvent) {

                    filterType.filter(myImage);
                    myImageLabel.setIcon(new ImageIcon(myImage));
                }
            });
            myButtonList.add(button);
        }
    }

    
    /**
     * When open button is pressed this method displays the file chooser to select
     * the desired Image file.
     */
    private void chooseImage() {

        File choosenFile;
        final int result = PICKAFILE.showOpenDialog(myMainJFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            choosenFile = PICKAFILE.getSelectedFile();
            
            try {
                closeImage();
                myImage = PixelImage.load(choosenFile);
                if (myImage != null) {
                    myPixelData = myImage.getPixelData();
                    myImageLabel = new JLabel(new ImageIcon(myImage), JLabel.CENTER);
                    myMainJFrame.add(myImageLabel, BorderLayout.CENTER);
                    myMainJFrame.pack();
                    myMainJFrame.setMinimumSize(myMainJFrame.getSize());
                    grayOutButtons(false);
                }

            } catch (final IOException e) {
                JOptionPane.showMessageDialog(null, "The File must be a Image", 
                                              "ERROR", JOptionPane.ERROR_MESSAGE);
                grayOutButtons(true);
                chooseImage();  
            }
        }
    }
    /**
     * When saveAs button is pressed this method saves the Image with the last edit.
     */
    private void saveImage() {
        final int result = PICKAFILE.showSaveDialog(myMainJFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                myImage.save(PICKAFILE.getSelectedFile());
            } catch (final IOException e) {
                saveImage();
                e.printStackTrace();
            }
        }
    }
    
    /**
     * undo method replaces the edited image with the unedited image.
     */
    private void undo() {
        myImage.setPixelData(myPixelData);
        myMainJFrame.repaint();
    }
    
    /**
     * When close button is pressed this method closes the image, resets the minimum size
     * of the JFrame, and packs it. 
     */
    private void closeImage() {
        if (myImageLabel != null) {
            myImageLabel.setIcon(null);
            myMainJFrame.setMinimumSize(myMainJFrame.getPreferredSize());
            myMainJFrame.pack();
        }
    }
}