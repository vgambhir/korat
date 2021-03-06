package korat.gui.viz;

import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import korat.testing.ITestCaseListener;
import edu.mit.csail.sdg.alloy4viz.VizGUI;

/**
 * 
 * @author Aleksandar Milicevic <aca.milicevic@gmail.com>
 *
 */
public class VizListener implements ITestCaseListener {

    private int idx = 0;

    private boolean firstTestCase = true;
    private boolean newGUI = false;

    public VizListener() {
        //this(true);
    	// new GUI
    	this.newGUI = true;
        
    }
    
    private boolean generateTheme = true;
    
    public VizListener(boolean generateTheme) {
        
        this.generateTheme = generateTheme;
        
    }

    private List<String> instanceFileNames = new LinkedList<String>();

    private String themeFileName = null;

    public void notifyNewTestCase(Object testCase) {

    	if (this.newGUI) { // added : Bhaskar, 3/15
    		
			ToXMLInstanceConverter conv = new ToXMLInstanceConverter(); // no args, new GUI
			conv.convertJson(testCase, idx++);

    	} else {

    		try {

    			ToXMLInstanceConverter conv = new ToXMLInstanceConverter(generateTheme);
    			conv.convert(testCase, idx++);

    			String instFileName = conv.getGeneratedModelFilename();
    			instanceFileNames.add(instFileName);

    			if (firstTestCase) {
    				themeFileName = conv.getGeneratedThemeFilename();
    				firstTestCase = false;
    			}

    		} catch (UnsupportedTypeException e) {
    			System.err.println("Cannot convert given test case");
    			e.printStackTrace();
    		}
    	}

    }

    public void notifyTestFinished(long numOfExplored, long numOfGenerated) {
    	if (!this.newGUI) { // added : Bhaskar, 3/15
    		
    		visualizeAll();
    	}
    }

    public void visualizeAll() {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                VizGUI vg = new VizGUI(true, "", null);

                for (String inst : instanceFileNames)
                    vg.run(VizGUI.evs_loadInstance, inst);

                vg.run(203, themeFileName);

            }

        });

    }


}