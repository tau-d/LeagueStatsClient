import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.MenuSelectionManager;
import javax.swing.RowFilter;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import com.sun.xml.internal.ws.api.Component;

// TODO: allow user to filter champs with low # games, etc.
// TODO: update table button
// TODO: tabs for overall winrate/kda
// TODO: preset column filter buttons (ex. show only player, champ, cs0-10, cs10-20, xp0-10, ...)
// TODO: only include matches within certain time period?

public class MyTableUI extends JPanel {
	private static final long serialVersionUID = 8306936611643892010L;

	private JTable table;
	private DefaultTableModel model;
	private TableRowSorter<DefaultTableModel> sorter;
	
	private Set<String> filteredPlayers = new HashSet<>();
	private Set<String> filteredChamps = new HashSet<>();	 
	
	public MyTableUI() {
        super(new GridLayout(1,0));

        model = new DefaultTableModel(FetchStatsHelper.getData(), FetchStatsHelper.COL_HEADERS) {
			private static final long serialVersionUID = -1344881329021797774L;

			@Override
            public Class<?> getColumnClass(int columnIndex) {
                for (int row = 0; row < getRowCount(); row++) {
                    Object o = getValueAt(row, columnIndex);
                    if (o != null) return o.getClass();
                }
                return Object.class;
            }
        };
        model.setColumnIdentifiers(FetchStatsHelper.COL_HEADERS);
        
        table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(900, 800));
        table.setFillsViewportHeight(true);
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        setCellRenderers();
        
        sorter = new TableRowSorter<>(model);
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
			@Override
			public boolean include(javax.swing.RowFilter.Entry<? extends DefaultTableModel, ? extends Integer> entry) {
				if (filteredPlayers.contains(entry.getStringValue(0))) {
					return false;
				} else if (filteredChamps.contains(entry.getStringValue(1))) {
					return false;
				}
				return true;
			}        	
        });
        table.setRowSorter(sorter);
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }
	
	private void setCellRenderers() {
		NumberFormat floatFormatter = NumberFormat.getNumberInstance();
		floatFormatter.setMinimumFractionDigits(2);
		floatFormatter.setMaximumFractionDigits(2);
		
		NumberFormat percentFormatter = NumberFormat.getPercentInstance();
		percentFormatter.setMaximumFractionDigits(0);
		
		NumberCellRenderer intRenderer = new NumberCellRenderer(NumberFormat.getIntegerInstance());
		NumberCellRenderer floatRenderer = new NumberCellRenderer(floatFormatter);
		NumberCellRenderer percentRenderer = new NumberCellRenderer(percentFormatter);
		
		table.getColumn(FetchStatsHelper.COL_KILLS).setCellRenderer(intRenderer);
		table.getColumn(FetchStatsHelper.COL_DEATHS).setCellRenderer(intRenderer);
		table.getColumn(FetchStatsHelper.COL_ASSISTS).setCellRenderer(intRenderer);
		table.getColumn(FetchStatsHelper.COL_NUM_WINS).setCellRenderer(intRenderer);
		table.getColumn(FetchStatsHelper.COL_NUM_GAMES).setCellRenderer(intRenderer);
        table.getColumn(FetchStatsHelper.COL_GOLD_0_TO_10).setCellRenderer(intRenderer);
        table.getColumn(FetchStatsHelper.COL_GOLD_10_TO_20).setCellRenderer(intRenderer);
        table.getColumn(FetchStatsHelper.COL_XP_0_TO_10).setCellRenderer(intRenderer);
        table.getColumn(FetchStatsHelper.COL_XP_10_TO_20).setCellRenderer(intRenderer);
		
        table.getColumn(FetchStatsHelper.COL_KDA).setCellRenderer(floatRenderer);
        table.getColumn(FetchStatsHelper.COL_CS_0_TO_10).setCellRenderer(floatRenderer);
        table.getColumn(FetchStatsHelper.COL_CS_10_TO_20).setCellRenderer(floatRenderer);
        
        table.getColumn(FetchStatsHelper.COL_WIN_RATE).setCellRenderer(percentRenderer);
	}
	
	
	private static class NumberCellRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 7259333366053151884L;
		private NumberFormat formatter;
	    
		public NumberCellRenderer(NumberFormat formatter) {
	    	super();
	    	this.formatter = formatter;
	    	setHorizontalAlignment(RIGHT);
    	}

	    public void setValue(Object value) {
	        setText((value == null) ? "" : formatter.format(value));
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
        @SuppressWarnings("unchecked")
		Vector<Vector<Object>> data = newContentPane.model.getDataVector();
        Set<String> allPlayers = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Map<Character, SortedSet<String>> allChampsByFirstLetter = new TreeMap<>();
        
        for (Vector<Object> row : data) {
        	allPlayers.add((String) row.elementAt(0));
        	
        	String champ = (String) row.elementAt(1);
        	Character start = Character.toUpperCase(champ.charAt(0));
        	SortedSet<String> set = allChampsByFirstLetter.getOrDefault(start, new TreeSet<>());
        	set.add(champ);        	
        	allChampsByFirstLetter.put(start, set);
        }
        
        JMenuBar menuBar = new JMenuBar();
        
        /*JMenu filterMenu = new JMenu("Filters");
        menuBar.add(filterMenu);*/
        
        JMenu playerFilterSubmenu = newContentPane.buildPlayerFilterSubmenu(allPlayers);
        JMenu champFilterSubmenu = newContentPane.buildChampFilterSubmenu(allChampsByFirstLetter);
        JMenu columnFilterSubmenu = newContentPane.buildColumnsFilterSubmenu();
        
        menuBar.add(playerFilterSubmenu);
        menuBar.add(champFilterSubmenu);
        menuBar.add(columnFilterSubmenu);
        
        frame.setJMenuBar(menuBar);
        
        // Display the window.
        frame.pack();
        centerOnScreen(frame);
        frame.setVisible(true);
    }
    
    
    private JMenu buildPlayerFilterSubmenu(Set<String> allPlayers) {
    	JMenu menu = new JMenu("Players");
    	
    	for (String summonerName : allPlayers) {
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
						filteredPlayers.add(summonerName);
						sorter.sort();
					} else if (stateChange == ItemEvent.SELECTED) {
						filteredPlayers.remove(summonerName);
						sorter.sort();
					} else {
						System.err.println("Unexpected state change: " + stateChange);
					}
				}
			});

    		menu.add(playerItem);
    	}
    	return menu;
    }
    
    
    private JMenu buildChampFilterSubmenu(Map<Character, SortedSet<String>> allChampsByFirstLetter) {
    	JMenu menu = new JMenu("Champions");
    	
    	JMenuItem selectAllItem = new JMenuItem("Select all");
    	selectAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filteredChamps.clear();
				sorter.sort();	
			}
		});
    	
    	JMenuItem deselectAllItem = new JMenuItem("Deselect all");
    	deselectAllItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (SortedSet<String> set : allChampsByFirstLetter.values()) {
					filteredChamps.addAll(set);
				}
				sorter.sort();	
			}
		});
    	
    	for (Entry<Character, SortedSet<String>> entry : allChampsByFirstLetter.entrySet()) {
    		JMenu firstLetterSubmenu = new JMenu(entry.getKey() + "...");
    		for (String champName : entry.getValue()) {
	    		JCheckBoxMenuItem playerItem = new JCheckBoxMenuItem(champName, true);
	    		
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
							filteredChamps.add(champName);
							sorter.sort();
						} else if (stateChange == ItemEvent.SELECTED) {
							filteredChamps.remove(champName);
							sorter.sort();
						} else {
							System.err.println("Unexpected state change: " + stateChange);
						}
					}
				});
	    		
	    		selectAllItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						playerItem.setSelected(true);
					}
				});
	    		
	    		deselectAllItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						playerItem.setSelected(false);
					}
				});
	    		
	    		firstLetterSubmenu.add(playerItem);
    		}
    		menu.add(firstLetterSubmenu);
    	}
    	
    	menu.add(selectAllItem);
    	menu.add(deselectAllItem);
    	
    	return menu;
    }
    
    
    private JMenu buildColumnsFilterSubmenu() {
    	JMenu menu = new JMenu("Columns");
    	
    	for (int i = 2; i < FetchStatsHelper.COL_HEADERS.length; ++i) { // cannot filter player name and champion
    		JCheckBoxMenuItem columnItem = getJCheckBoxMenuItem(FetchStatsHelper.COL_HEADERS[i]);
    		final Object identifier = FetchStatsHelper.COL_HEADERS[i];
    		
    		columnItem.setUI(new BasicCheckBoxMenuItemUI() { // make menu stay open after toggling checkbox
    			@Override
    			protected void doClick(MenuSelectionManager msm) {
    				columnItem.doClick(0);
    			}
    		});

    		columnItem.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					int stateChange = e.getStateChange();
					TableColumn col = table.getColumn(identifier);
					if (stateChange == ItemEvent.DESELECTED) { // hide column
						hideColumn(col);
					} else if (stateChange == ItemEvent.SELECTED) { // show column
						showColumn(col);
					} else {
						System.err.println("Unexpected state change: " + stateChange);
					}
				}
			});
    		
    		menu.add(columnItem);
    	}
    	return menu;
    }
    
    private void hideColumn(TableColumn col) {
    	col.setResizable(false);
		col.setMinWidth(0);
		col.setMaxWidth(0);
    }
    
    private void showColumn(TableColumn col) {
    	col.setResizable(true);
		col.setMaxWidth(Integer.MAX_VALUE);
		col.setPreferredWidth(75);
    }
    
    private JCheckBoxMenuItem getJCheckBoxMenuItem(String id) {
    	if (id == FetchStatsHelper.COL_KILLS ||
			id == FetchStatsHelper.COL_DEATHS ||
			id == FetchStatsHelper.COL_ASSISTS || 
			id == FetchStatsHelper.COL_NUM_WINS) {
    		
    		hideColumn(table.getColumn(id));    		
			return new JCheckBoxMenuItem(id, false);
		} else {
			return new JCheckBoxMenuItem(id, true);
		}
    }
    
	private static void centerOnScreen(JFrame frame) {
    	frame.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - frame.getWidth() / 2,
    			(Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - frame.getHeight() / 2);
	}
    
}