/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

// TODO: allow user to choose what columns to show
// TODO: allow user to filter players, champs with low # games, etc.

public class MyTableUI extends JPanel {
	private static final long serialVersionUID = 8306936611643892010L;

	private MyTableModel model;
	
	public MyTableUI() {
        super(new GridLayout(1,0));

        model = new MyTableModel();
        
        JTable table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(900, 800));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
    }

	
    private static class MyTableModel extends AbstractTableModel {
		// Constants
    	private static final long serialVersionUID = -4676918035836585921L;
		
		private static final String TOT_KILLS = "totalKills";
    	private static final String TOT_DEATHS = "totalDeaths";
    	private static final String TOT_ASSISTS = "totalAssists";
    	private static final String KDA = "KDA";
    	private static final String NUM_WINS = "numWins";
    	private static final String NUM_GAMES= "numGames";
    	private static final String WIN_RATE = "winRate";
    	    	
        private static final String[] DEFAULT_COL_NAMES = {"Summoner",
                                        "Champion",
                                        "Kills",
                                        "Deaths",
                                        "Assists",
                                        "KDA",
                                        "# Wins",
                                        "# Games",
                                        "Win %"};
        
        private static final Map<String, Integer> COL_HEADER_TO_INDEX = new HashMap<>();
        
        {
        	for (int i = 0; i < DEFAULT_COL_NAMES.length; ++i) {
        		COL_HEADER_TO_INDEX.put(DEFAULT_COL_NAMES[i], i);
        	}
        }
        
        // Variables
        private final ArrayList<ArrayList<Object>> ALL_DATA = new ArrayList<>();
        
        private List<List<Object>> postFilterRows; // rows after filtering 
        private List<String> postFilterCols; // columns after filtering
        
        private Set<String> playersToFilter = new HashSet<>();
        
        // Methods
        public MyTableModel() {
        	postFilterCols = new ArrayList<>();
        	for (String head : DEFAULT_COL_NAMES) {
        		postFilterCols.add(head);
        	}
        	
        	postFilterRows = new ArrayList<>();
        	
        	String url = "http://71.178.243.38/leaguestats/getAllData";
    		try {
                String allDataJsonStr = IOUtils.toString(new URL(url), "utf-8");
                JSONArray allDataRows = (JSONArray) JSONValue.parseWithException(allDataJsonStr);
                
                for (Object o : allDataRows) {
                	JSONObject row = (JSONObject) o;
                	ArrayList<Object> newRow = new ArrayList<>(DEFAULT_COL_NAMES.length);
                	
                	newRow.add(row.get(MySqlHelper.COL_SUMMONER_NAME));
                	newRow.add(row.get(MySqlHelper.COL_CHAMPION_NAME));
                	newRow.add(myParseInt(row, TOT_KILLS));
                	newRow.add(myParseInt(row, TOT_DEATHS));
                	newRow.add(myParseInt(row, TOT_ASSISTS));
                	newRow.add(myParseFloat(row, KDA));
                	newRow.add(myParseInt(row, NUM_WINS));
                	newRow.add(myParseInt(row, NUM_GAMES));
                	newRow.add(myParseFloat(row, WIN_RATE));
                	
                	ALL_DATA.add(newRow);
                	postFilterRows.add((List<Object>) newRow.clone());
                }
    		} catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        
        private Integer myParseInt(JSONObject row, String key) {
        	Object o = row.get(key);
        	if (o == null) {
        		return null;
        	} else {
        		return Integer.parseInt((String) o);        		
        	}
        }
        
        private Float myParseFloat(JSONObject row, String key) {
        	Object o = row.get(key);
        	if (o == null) {
        		return null;
        	} else {
        		return Float.parseFloat((String) o);        		
        	}
        }
        
        private void filterRows() {
        	postFilterRows.clear();
        	for (List<Object> row : ALL_DATA) {
        		String name = (String) row.get(COL_HEADER_TO_INDEX.get("Summoner"));
        		if (!playersToFilter.contains(name)) {
        			postFilterRows.add(row);
        		}
        	}
        	fireTableDataChanged();
        }
        
        public int getColumnCount() {
            return postFilterCols.size();
        }

        public int getRowCount() {
            return postFilterRows.size();
        }

        public String getColumnName(int col) {
            return postFilterCols.get(col);
        }

        public Object getValueAt(int row, int col) {
        	if (postFilterRows.isEmpty()) return null;
            return postFilterRows.get(row).get(col);
        }
        
        public Class getColumnClass(int c) {
        	if (postFilterRows.isEmpty()) return Object.class;
            return getValueAt(0, c).getClass();
        }
    }
    
    public static void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("League Stats Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        MyTableUI newContentPane = new MyTableUI();
        newContentPane.setOpaque(true); // content panes must be opaque
        frame.setContentPane(newContentPane);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        
        JMenu filterMenu = new JMenu("Filters");
        menuBar.add(filterMenu);
        
        JMenu playerFilterSubmenu = buildPlayerFilterSubmenu(newContentPane.model);
        filterMenu.add(playerFilterSubmenu);
        
        frame.setJMenuBar(menuBar);
        
        // Display the window.
        frame.pack();
        centerOnScreen(frame);
        frame.setVisible(true);
    }
    
    private static JMenu buildPlayerFilterSubmenu(MyTableModel model) {
    	JMenu menu = new JMenu("Players");
    	
    	SortedSet<String> players = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    	int index = MyTableModel.COL_HEADER_TO_INDEX.get("Summoner");
    	for (ArrayList<Object> row : model.ALL_DATA) {
    		players.add((String) row.get(index));
    	}
    	
    	for (String summonerName : players) {
    		JCheckBoxMenuItem playerItem = new JCheckBoxMenuItem(summonerName, true);
    		playerItem.setUI(new BasicCheckBoxMenuItemUI() { // make menu stay open after toggling checkbox
    			@Override
    			protected void doClick(MenuSelectionManager msm) {
    				playerItem.doClick(0);
    			}
    		});
    		playerItem.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					int stateChange = e.getStateChange();
					if (stateChange == ItemEvent.DESELECTED) {
						model.playersToFilter.add(summonerName);
						model.filterRows();
					} else if (stateChange == ItemEvent.SELECTED) {
						model.playersToFilter.remove(summonerName);
						model.filterRows();
					} else {
						System.err.println("Unexpected state change: " + stateChange);
					}
				}
			});
    		menu.add(playerItem);
    	}
    	    	
    	return menu;
    }
	
	private static void centerOnScreen(JFrame frame) {
    	frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - frame.getWidth() / 2,
    			(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - frame.getHeight() / 2);
	}
    
}