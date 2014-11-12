package ch.dermitza.myocheetah.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;


/**
 * 
 * Created: ??/??/2004 rev 0.1 M. Green
 * Edited : 17/07/2006 rev 0.2 dermitza
 * 
 * A collection of generally useful Swing utility methods.  Original Copyright
 * 2004 by Superliminal Software and Melinda Green. The class has been since
 * modified to incorporate additional functionality.
 *
 * @filename UIUtils.java
 * @author Melinda Green
 * @author K. Dermitzakis
 * @version 0.2
 */
public class UIUtils {
    // to disallow instantiation
    private UIUtils(){};

    /**
     * Adds a control hot key to the containing window of a component.
     * In the case of buttons and menu items it also attaches the given action
     * to the component itself.
     *
     * @param key one of the KeyEvent keyboard constants
     * @param to component to map to
     * @param actionName unique action name for the component's action map
     * @param action callback to notify when control key is pressed
     */
    public static void addHotKey(int key, JComponent to, String actionName,
            Action action) {
        KeyStroke keystroke = KeyStroke.getKeyStroke(
                key, java.awt.event.InputEvent.CTRL_MASK);
        InputMap map = to.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        map.put(keystroke, actionName);
        to.getActionMap().put(actionName, action);
        if(to instanceof JMenuItem)
            ((JMenuItem)to).setAccelerator(keystroke);
        if(to instanceof AbstractButton) // includes JMenuItem
            ((AbstractButton)to).addActionListener(action);
    }
    
    /**
     * Finds the top-level JFrame in the component tree containing a given
     * component.
     *
     * @param comp leaf component to search up from
     * @return the containing JFrame or null if none
     */
    public static JFrame getTopFrame(Component comp) {
        if(comp == null)
            return null;
        while (comp.getParent() != null)
            comp = comp.getParent();
        if (comp instanceof JFrame)
            return (JFrame) comp;
        return null;
    }
    
        /**
     * Finds the top-level JWindow in the component tree containing a given
     * component.
     *
     * @param comp leaf component to search up from
     * @return the containing JFrame or null if none
     */
    public static JWindow getTopWindow(Component comp) {
        if(comp == null)
            return null;
        while (comp.getParent() != null)
            comp = comp.getParent();
        if (comp instanceof JWindow)
            return (JWindow) comp;
        return null;
    }

    /**
     * Different platforms use different mouse gestures as pop-up triggers.
     * This class unifies them. Just implement the abstract popUp method
     * to add your handler.
     */
    public static abstract class PopperUpper extends MouseAdapter {
        // To work properly on all platforms, must check on mouse press
        // as well as release
        @Override
        public void mousePressed(MouseEvent e)  {
            if(e.isPopupTrigger()) popUp(e);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if(e.isPopupTrigger()) popUp(e);
        }
        protected abstract void popUp(MouseEvent e);
    }

    // simple Clipboard string routines

    public static void placeInClipboard(String str) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
            new StringSelection(str), null);
    }

    public static String getFromClipboard() {
        String str = null;
        try {
            str = (String)Toolkit.getDefaultToolkit().getSystemClipboard()
                    .getContents(null).getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * Draws the given string into the given graphics with the area behind the
     * string
     * filled with a given background color.
     */
    public static void fillString(String str, int x, int y, Color bg,
            Graphics g) {
        Rectangle2D strrect = g.getFontMetrics().getStringBounds(str, null);
        Color ocolor = g.getColor();
        g.setColor(bg);
        g.fillRect((int)(x+strrect.getX()), (int)(y+strrect.getY()),
                (int)(strrect.getWidth()), (int)(strrect.getHeight()));
        g.setColor(ocolor);
        g.drawString(str, x, y);
    }

    /**
     * Utility class that initializes a meduim sized, screen-centered,
     * exit-on-close JFrame.
     * Mostly useful for simple example main programs.
     */
    public static class QuickFrame extends JFrame {
        public QuickFrame(String title) {
            super(title);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(640, 480);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation(
                Math.max(0,screenSize.width/2  - getWidth()/2),
                Math.max(0,screenSize.height/2 - getHeight()/2));
        }
    }

    /**
     * Selection utility in the style of the JOptionPane.showXxxDialog methods.
     * Given a JTree, presents an option dialog presenting the tree allowing
     * users to select a node.
     * @param tree is the tree to display
     * @param parent is the component to anchor the diaglog to
     * @return the path of the selected tree node or null if cancelled.
     */
    public static TreePath showTreeNodeChooser(JTree tree, String title,
            Component parent) {
        final String OK = "OK", CANCEL = "Cancel";
        final JButton ok_butt = new JButton(OK);
        final JButton cancel_butt = new JButton(CANCEL);
        final TreePath selected[] = new TreePath[] {
            tree.getLeadSelectionPath() };
        // only an array so it can be final, yet mutable
        ok_butt.setEnabled(selected[0] != null);
        final JOptionPane option_pane = new JOptionPane(
                new JScrollPane(tree), JOptionPane.QUESTION_MESSAGE,
            JOptionPane.DEFAULT_OPTION, null,
            new Object[]{ok_butt, cancel_butt});
        ok_butt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                option_pane.setValue(OK);
            }
        });
        cancel_butt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                option_pane.setValue(CANCEL);
                selected[0] = null;
            }
        });
        TreeSelectionListener tsl = new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                selected[0] = e.getNewLeadSelectionPath();
                ok_butt.setEnabled(selected[0] != null);
            }
        };
        JDialog dialog = option_pane.createDialog(parent, title);
        // to start monitoring user tree selections
        tree.addTreeSelectionListener(tsl);
        // present modal tree dialog to user
        dialog.setVisible(true);
        // don't want to clutter caller's tree with listeners
        tree.removeTreeSelectionListener(tsl); 
        return OK.equals(option_pane.getValue()) ? selected[0] : null;
    }

    /**
     * Returns the maximum dimension of all the components contained in a
     * container
     * @param container The container containing the components to compare
     * @return The dimension of the biggest component in the container
     */
    public static Dimension getMaxDimension(JComponent container){
        Component[] comps = container.getComponents();
        Dimension maxDim = new Dimension();
        if(comps != null){
            maxDim = comps[0].getPreferredSize();
            int maxSurf = maxDim.height*maxDim.width;
            int surf;
            for(int i=1; i < comps.length; i++){
                surf = comps[i].getPreferredSize().width*
                        comps[i].getPreferredSize().height;
                if(surf > maxSurf){
                    maxSurf = surf;
                    maxDim = comps[i].getPreferredSize();
                }
            }
        }

        return maxDim;
    }

    /**
     * Fixes a JComponent's size. Does this by setting all sizes to the same
     * dimension
     *
     * @param comp The JComponent to set the size of
     * @param dim The Dimension to be set
     * 
     */
    public static void setAllSizes(JComponent comp, Dimension dim){
        if(comp != null){
            comp.setMinimumSize(dim);
            comp.setPreferredSize(dim);
            comp.setMaximumSize(dim);
            comp.setSize(dim);
        }
    }

    /**
     * Gets all components of a top level container recursively.
     * @param c The top level container
     * @param l The list to add the components to
     */
    public static void getAllComponents(Container c, List<Container> l) {
        if (c.getComponentCount() == 0) {
            return;
        }
        Component[] components = c.getComponents();
        for (int i = 0; i < components.length; i++) {
            Container element = (Container) components[i];
            getAllComponents(element, l);
            l.add(element);
        }
    }

    public static void main(String[] args) {
        TreePath got = showTreeNodeChooser(new JTree(), "Select A Node", null);
        System.out.println(got);
        System.exit(0);
    }
}
