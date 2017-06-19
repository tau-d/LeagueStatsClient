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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

// TODO: allow user to choose what rows to show
// TODO: allow user to filter players, champs with low # games, etc.

public class MyTable extends JPanel {
	private static final long serialVersionUID = 8306936611643892010L;

	public MyTable() {
        super(new GridLayout(1,0));

        JTable table = new JTable(new MyTableModel());
        table.setPreferredScrollableViewportSize(new Dimension(900, 800));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
    }

    class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -4676918035836585921L;
		
		private static final String TOT_KILLS = "totalKills";
    	private static final String TOT_DEATHS = "totalDeaths";
    	private static final String TOT_ASSISTS = "totalAssists";
    	private static final String KDA = "KDA";
    	private static final String NUM_WINS = "numWins";
    	private static final String NUM_GAMES= "numGames";
    	private static final String WIN_RATE = "winRate";
    	    	
        private String[] columnNames = {"Summoner",
                                        "Champion",
                                        "Kills",
                                        "Deaths",
                                        "Assists",
                                        "KDA",
                                        "# Wins",
                                        "# Games",
                                        "Win %"};
        
        private ArrayList<ArrayList<Object>> data;
        
        public MyTableModel() {
        	data = new ArrayList<>();
        	String url = "http://71.178.243.38/leaguestats/getAllData";
        	
    		try {
                String allDataJsonStr = IOUtils.toString(new URL(url), "utf-8");
                JSONArray allDataRows = (JSONArray) JSONValue.parseWithException(allDataJsonStr);
                
                for (Object o : allDataRows) {
                	JSONObject row = (JSONObject) o;
                	ArrayList<Object> newRow = new ArrayList<>(columnNames.length);
                	
                	newRow.add(row.get(MySqlHelper.COL_SUMMONER_NAME));
                	newRow.add(row.get(MySqlHelper.COL_CHAMPION_NAME));
                	newRow.add(Integer.parseInt((String) row.get(TOT_KILLS)));
                	newRow.add(Integer.parseInt((String) row.get(TOT_DEATHS)));
                	newRow.add(Integer.parseInt((String) row.get(TOT_ASSISTS)));
                	newRow.add(row.get(KDA) == null ? null : Float.parseFloat((String) row.get(KDA)));
                	newRow.add(Integer.parseInt((String) row.get(NUM_WINS)));
                	newRow.add(Integer.parseInt((String) row.get(NUM_GAMES)));
                	newRow.add(row.get(WIN_RATE) == null ? null : Float.parseFloat((String) row.get(WIN_RATE)));
                	
                	data.add(newRow);
                }
                
    		} catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data.get(row).get(col);
        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("League Stats Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        MyTable newContentPane = new MyTable();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        centerOnScreen(frame);
        frame.setVisible(true);
    }
    
    private static void centerOnScreen(JFrame frame) {
    	frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - frame.getWidth() / 2,
    			(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - frame.getHeight() / 2);
	}

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}